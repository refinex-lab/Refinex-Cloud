package cn.refinex.kb.service.impl;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentVersionResponseDTO;
import cn.refinex.kb.entity.ContentDocument;
import cn.refinex.kb.entity.ContentDocumentVersion;
import cn.refinex.kb.repository.ContentDocumentRepository;
import cn.refinex.kb.repository.ContentDocumentVersionRepository;
import cn.refinex.kb.service.ContentDocumentVersionService;
import cn.refinex.kb.util.DocumentDiffUtils;
import cn.refinex.kb.util.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 文档版本服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentDocumentVersionServiceImpl implements ContentDocumentVersionService {

    private final ContentDocumentRepository documentRepository;
    private final ContentDocumentVersionRepository versionRepository;
    private final cn.refinex.kb.client.PlatformUserServiceClient platformUserServiceClient;
    private final cn.refinex.kb.repository.ContentSpaceRepository spaceRepository;

    /**
     * 分页查询文档版本历史
     *
     * @param documentId  文档ID
     * @param pageRequest 分页参数
     * @param operatorId  操作人ID（用于权限验证）
     * @return 分页结果
     */
    @Override
    public PageResult<ContentDocumentVersionResponseDTO> page(
            Long documentId,
            PageRequest pageRequest,
            Long operatorId) {

        // 1. 验证文档存在和权限
        ContentDocument document = validateDocumentAccess(documentId, operatorId);

        // 2. 执行分页查询
        PageResult<ContentDocumentVersion> pageResult = versionRepository.selectPageByDocumentId(
                documentId,
                pageRequest
        );

        // 3. 转换为响应DTO
        if (CollectionUtils.isEmpty(pageResult.getRecords())) {
            return new PageResult<>(
                    Collections.emptyList(),
                    pageResult.getTotal(),
                    pageResult.getPageNum(),
                    pageResult.getPageSize()
            );
        }

        List<ContentDocumentVersionResponseDTO> records = pageResult.getRecords().stream()
                .map(version -> {
                    ContentDocumentVersionResponseDTO dto = BeanConverter.toBean(
                            version,
                            ContentDocumentVersionResponseDTO.class
                    );

                    // 设置内容摘要（仅显示前200字）
                    if (version.getContentBody() != null) {
                        dto.setContentSummary(MarkdownUtils.extractSummary(version.getContentBody(), 200));
                    }

                    // 设置是否为当前版本
                    dto.setIsCurrent(version.getVersionNumber().equals(document.getVersionNumber()));

                    // 设置创建人昵称
                    if (version.getCreatedBy() != null) {
                        String createdByName = getUserNickname(version.getCreatedBy());
                        dto.setCreatedByName(createdByName);
                    }

                    return dto;
                })
                .toList();

        return new PageResult<>(
                records,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
    }

    /**
     * 查询指定版本详情
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @param operatorId    操作人ID（用于权限验证）
     * @return 版本详情
     */
    @Override
    public ContentDocumentVersionDetailResponseDTO getVersion(
            Long documentId,
            Integer versionNumber,
            Long operatorId) {

        // 1. 验证文档存在和权限
        ContentDocument document = validateDocumentAccess(documentId, operatorId);

        // 2. 查询版本记录
        ContentDocumentVersion version = versionRepository.selectByDocumentIdAndVersionNumber(documentId, versionNumber);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        // 3. 转换为响应DTO
        ContentDocumentVersionDetailResponseDTO dto = BeanConverter.toBean(
                version,
                ContentDocumentVersionDetailResponseDTO.class
        );

        dto.setDocumentTitle(document.getDocTitle());
        dto.setIsCurrent(version.getVersionNumber().equals(document.getVersionNumber()));

        // 设置创建人昵称
        if (version.getCreatedBy() != null) {
            String createdByName = getUserNickname(version.getCreatedBy());
            dto.setCreatedByName(createdByName);
        }

        return dto;
    }

    /**
     * 恢复到指定版本
     *
     * @param documentId    文档ID
     * @param versionNumber 要恢复的版本号
     * @param operatorId    操作人ID
     * @return 新版本号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer restoreVersion(Long documentId, Integer versionNumber, Long operatorId) {
        // 1. 验证文档存在和权限
        ContentDocument document = validateDocumentAccess(documentId, operatorId);
        validateDocumentEditPermission(document, operatorId);

        // 2. 查询要恢复的版本
        ContentDocumentVersion oldVersion = versionRepository.selectByDocumentIdAndVersionNumber(documentId, versionNumber);
        if (oldVersion == null) {
            throw new BusinessException("版本不存在");
        }

        // 3. 如果就是当前版本，无需恢复
        if (versionNumber.equals(document.getVersionNumber())) {
            throw new BusinessException("已经是当前版本，无需恢复");
        }

        // 4. 获取当前最大版本号
        Integer currentMaxVersion = versionRepository.selectMaxVersionNumber(documentId);
        Integer newVersionNumber = currentMaxVersion + 1;

        // 5. 统计字数和阅读时长
        int wordCount = MarkdownUtils.countWords(oldVersion.getContentBody());
        int readDuration = MarkdownUtils.calculateReadDuration(wordCount);

        // 6. 创建新版本记录（内容来自旧版本）
        ContentDocumentVersion newVersion = new ContentDocumentVersion();
        newVersion.setDocumentId(documentId);
        newVersion.setVersionNumber(newVersionNumber);
        newVersion.setContentBody(oldVersion.getContentBody());
        newVersion.setFileId(oldVersion.getFileId());
        newVersion.setChangeSummary("从版本 #" + versionNumber + " 恢复");
        newVersion.setWordCount(wordCount);
        newVersion.setCreatedBy(operatorId);
        newVersion.setCreateTime(LocalDateTime.now());
        versionRepository.insert(newVersion);

        // 7. 更新文档的内容和版本号
        documentRepository.updateContentAndVersion(
                documentId,
                oldVersion.getContentBody(),
                wordCount,
                readDuration,
                newVersionNumber,
                operatorId,
                document.getVersion()
        );

        return newVersionNumber;
    }

    /**
     * 清理文档的旧版本
     *
     * @param documentId 文档ID
     * @param keepCount  保留的版本数量
     * @param operatorId 操作人ID
     * @return 删除的版本数量
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanOldVersions(Long documentId, int keepCount, Long operatorId) {
        // 1. 验证文档存在和权限
        ContentDocument document = validateDocumentAccess(documentId, operatorId);
        validateDocumentEditPermission(document, operatorId);

        // 2. 验证保留数量
        if (keepCount < 1) {
            throw new BusinessException("至少要保留1个版本");
        }

        // 3. 执行清理
        return versionRepository.cleanOldVersions(documentId, keepCount);
    }

    /**
     * 删除指定版本
     *
     * @param documentId    文档ID
     * @param versionNumber 版本号
     * @param operatorId    操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteVersion(Long documentId, Integer versionNumber, Long operatorId) {
        // 1. 验证文档存在和权限
        ContentDocument document = validateDocumentAccess(documentId, operatorId);
        validateDocumentEditPermission(document, operatorId);

        // 2. 不能删除当前版本
        if (versionNumber.equals(document.getVersionNumber())) {
            throw new BusinessException("不能删除当前版本");
        }

        // 3. 查询版本记录
        ContentDocumentVersion version = versionRepository.selectByDocumentIdAndVersionNumber(documentId, versionNumber);
        if (version == null) {
            throw new BusinessException("版本不存在");
        }

        // 4. 删除版本记录
        return versionRepository.deleteById(version.getId()) > 0;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证文档存在且用户有访问权限
     *
     * @param documentId 文档ID
     * @param userId     用户ID
     * @return 文档实体
     */
    private ContentDocument validateDocumentAccess(Long documentId, Long userId) {
        ContentDocument document = documentRepository.selectById(documentId);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 验证文档查看权限（参考 ContentDocumentServiceImpl 的权限验证逻辑）
        validateDocumentViewPermission(document, userId);

        return document;
    }

    /**
     * 验证文档查看权限
     *
     * @param document 文档实体
     * @param userId   用户ID
     */
    private void validateDocumentViewPermission(ContentDocument document, Long userId) {
        // 1. 草稿状态只有创建者可见
        if (cn.refinex.kb.enums.DocumentStatus.DRAFT.getCode().equals(document.getDocStatus())) {
            if (!document.getCreateBy().equals(userId)) {
                throw new BusinessException("文档未发布");
            }
            return;
        }

        // 2. 已下架状态只有创建者可见
        if (cn.refinex.kb.enums.DocumentStatus.OFFLINE.getCode().equals(document.getDocStatus())) {
            if (!document.getCreateBy().equals(userId)) {
                throw new BusinessException("文档已下架");
            }
            return;
        }

        // 3. 检查访问类型
        if (document.getAccessType() == null || document.getAccessType() == 0) {
            // 继承空间权限 - 实现空间级别权限检查
            cn.refinex.kb.entity.ContentSpace space = spaceRepository.selectById(document.getSpaceId());
            if (space != null) {
                // 私有空间：只有拥有者可见
                if (space.getAccessType() != null && space.getAccessType() == 0 && !space.getOwnerId().equals(userId)) {
                    throw new BusinessException("该空间为私有空间，您没有访问权限");
                }

                // 密码空间：需要密码（暂时只允许拥有者访问）
                if (space.getAccessType() != null && space.getAccessType() == 2 && !space.getOwnerId().equals(userId)) {
                    throw new BusinessException("该空间需要密码访问");
                }

            }
            return;
        }

        if (document.getAccessType() == 1) {
            // 自定义私有 - 只有创建者和空间拥有者可见
            if (userId == null) {
                throw new BusinessException("此文档为私有文档");
            }

            if (!document.getCreateBy().equals(userId)) {
                // 检查是否是空间拥有者
                cn.refinex.kb.entity.ContentSpace space = spaceRepository.selectById(document.getSpaceId());
                if (space == null || !space.getOwnerId().equals(userId)) {
                    throw new BusinessException("此文档为私有文档");
                }
            }
        }

        // document.getAccessType() == 2 时为公开文档，无需验证
    }

    /**
     * 验证文档编辑权限
     *
     * @param document 文档实体
     * @param userId   用户ID
     */
    private void validateDocumentEditPermission(ContentDocument document, Long userId) {
        if (!document.getCreateBy().equals(userId)) {
            throw new BusinessException("无权编辑此文档");
        }
    }

    /**
     * 获取用户昵称
     *
     * @param userId 用户ID
     * @return 用户昵称
     */
    private String getUserNickname(Long userId) {
        if (userId == null) {
            return null;
        }

        try {
            ApiResult<String> result = platformUserServiceClient.getUsernameByUserId(userId);
            if (result != null && result.is2xxSuccess() && result.data() != null) {
                return result.data();
            }
        } catch (Exception e) {
            log.error("获取用户昵称失败: userId={}", userId, e);
        }

        return "未知用户";
    }

    /**
     * 对比两个版本的差异
     *
     * @param documentId  文档ID
     * @param fromVersion 起始版本号
     * @param toVersion   目标版本号
     * @param operatorId  操作人ID（用于权限验证）
     * @return 差异结果
     */
    @Override
    public Map<String, Object> compareVersions(Long documentId, Integer fromVersion, Integer toVersion, Long operatorId) {
        // 1. 验证文档存在和权限
        validateDocumentAccess(documentId, operatorId);

        // 2. 查询两个版本
        ContentDocumentVersion fromVersionEntity = versionRepository.selectByDocumentIdAndVersionNumber(documentId, fromVersion);
        ContentDocumentVersion toVersionEntity = versionRepository.selectByDocumentIdAndVersionNumber(documentId, toVersion);

        if (fromVersionEntity == null) {
            throw new BusinessException("起始版本不存在");
        }
        if (toVersionEntity == null) {
            throw new BusinessException("目标版本不存在");
        }

        // 3. 获取版本内容
        String fromContent = fromVersionEntity.getContentBody() != null ? fromVersionEntity.getContentBody() : "";
        String toContent = toVersionEntity.getContentBody() != null ? toVersionEntity.getContentBody() : "";

        // 4. 执行差异对比
        DocumentDiffUtils.DiffResult diffResult = DocumentDiffUtils.diff(fromContent, toContent);

        // 5. 生成统一差异格式
        String unifiedDiff = DocumentDiffUtils.generateUnifiedDiff(
                fromContent,
                toContent,
                "版本 #" + fromVersion,
                "版本 #" + toVersion
        );

        // 6. 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("documentId", documentId);
        result.put("fromVersion", fromVersion);
        result.put("toVersion", toVersion);
        result.put("fromVersionTime", fromVersionEntity.getCreateTime());
        result.put("toVersionTime", toVersionEntity.getCreateTime());
        result.put("fromLineCount", diffResult.getFromLineCount());
        result.put("toLineCount", diffResult.getToLineCount());
        result.put("changeCount", diffResult.getChangeCount());

        // 差异摘要
        DocumentDiffUtils.DiffSummary summary = diffResult.getSummary();
        Map<String, Object> summaryMap = new HashMap<>();
        summaryMap.put("insertions", summary.getInsertions());
        summaryMap.put("deletions", summary.getDeletions());
        summaryMap.put("changes", summary.getChanges());
        summaryMap.put("totalChanges", summary.getTotalChanges());
        result.put("summary", summaryMap);

        // 详细差异块
        List<Map<String, Object>> diffBlocks = new ArrayList<>();
        for (DocumentDiffUtils.DiffBlock block : diffResult.getDiffBlocks()) {
            Map<String, Object> blockMap = new HashMap<>();
            blockMap.put("type", block.getType());
            blockMap.put("sourcePosition", block.getSourcePosition());
            blockMap.put("sourceLines", block.getSourceLines());
            blockMap.put("targetPosition", block.getTargetPosition());
            blockMap.put("targetLines", block.getTargetLines());
            diffBlocks.add(blockMap);
        }
        result.put("diffBlocks", diffBlocks);

        // 统一差异格式（类似 Git Diff）
        result.put("unifiedDiff", unifiedDiff);

        return result;
    }
}

