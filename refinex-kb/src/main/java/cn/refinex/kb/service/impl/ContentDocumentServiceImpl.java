package cn.refinex.kb.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.client.PlatformUserServiceClient;
import cn.refinex.kb.constants.DocumentConstants;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentCreateRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentQueryRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentSaveContentRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentUpdateRequestDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentResponseDTO;
import cn.refinex.kb.entity.ContentDocument;
import cn.refinex.kb.entity.ContentDocumentVersion;
import cn.refinex.kb.entity.ContentSpace;
import cn.refinex.kb.entity.ContentTag;
import cn.refinex.kb.enums.DocumentAccessType;
import cn.refinex.kb.enums.DocumentStatus;
import cn.refinex.kb.repository.ContentDocumentRepository;
import cn.refinex.kb.repository.ContentDocumentTagRepository;
import cn.refinex.kb.repository.ContentDocumentVersionRepository;
import cn.refinex.kb.repository.ContentSpaceRepository;
import cn.refinex.kb.service.ContentDocumentService;
import cn.refinex.kb.util.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 文档服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentDocumentServiceImpl implements ContentDocumentService {

    private final ContentDocumentRepository documentRepository;
    private final ContentDocumentVersionRepository versionRepository;
    private final ContentDocumentTagRepository documentTagRepository;
    private final ContentSpaceRepository spaceRepository;
    private final PlatformUserServiceClient platformUserServiceClient;
    private final cn.refinex.kb.repository.ContentActionRepository contentActionRepository;
    private final cn.refinex.kb.repository.ContentDirectoryRepository contentDirectoryRepository;

    /**
     * 创建文档
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 文档ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ContentDocumentCreateRequestDTO request, Long operatorId) {
        // 1. 验证空间存在且有权限
        validateSpaceAccess(request.getSpaceId(), operatorId);

        // 2. 如果指定了目录，验证目录存在且属于该空间
        if (request.getDirectoryId() != null) {
            validateDirectoryBelongsToSpace(request.getDirectoryId(), request.getSpaceId());
        }

        // 3. 生成文档GUID
        String docGuid = DocumentConstants.DOC_GUID_PREFIX + IdUtil.fastSimpleUUID();

        // 4. 构建实体
        ContentDocument document = BeanConverter.toBean(request, ContentDocument.class);
        document.setDocGuid(docGuid);
        // 默认文档状态为草稿
        document.setDocStatus(DocumentStatus.DRAFT.getCode());
        document.setVersionNumber(1);
        document.setViewCount(0L);
        document.setLikeCount(0L);
        document.setCollectCount(0L);
        document.setCommentCount(0L);
        document.setShareCount(0L);
        document.setCreateBy(operatorId);
        document.setCreateTime(LocalDateTime.now());
        document.setUpdateBy(operatorId);
        document.setUpdateTime(LocalDateTime.now());
        document.setDeleted(0);
        document.setVersion(0);

        if (document.getStatus() == null) {
            document.setStatus(0);
        }
        if (document.getSort() == null) {
            document.setSort(0);
        }

        // 5. 如果提供了内容，自动统计字数和阅读时长
        if (request.getContentBody() != null && !request.getContentBody().isEmpty()) {
            int wordCount = MarkdownUtils.countWords(request.getContentBody());
            int readDuration = MarkdownUtils.calculateReadDuration(wordCount);
            document.setWordCount(wordCount);
            document.setReadDuration(readDuration);

            // 如果启用自动摘要且没有提供摘要
            if ((request.getDocSummary() == null || request.getDocSummary().isEmpty())) {
                // 提取移除 Markdown 语法的前 200 个字符作为摘要（末尾补充 ...）
                String summary = MarkdownUtils.extractSummary(request.getContentBody(), 200);
                document.setDocSummary(summary);
            }
        } else {
            // 没有内容时，设置默认值
            document.setWordCount(0);
            document.setReadDuration(0);
        }

        // 6. 插入数据库
        Long documentId = documentRepository.insert(document);

        // 7. 如果提供了内容，创建初始版本记录
        if (request.getContentBody() != null && !request.getContentBody().isEmpty()) {
            ContentDocumentVersion version = new ContentDocumentVersion();
            version.setDocumentId(documentId);
            version.setVersionNumber(1);
            version.setContentBody(request.getContentBody());
            version.setFileId(document.getFileId());
            version.setChangeSummary("初始版本");
            version.setWordCount(document.getWordCount());
            version.setCreatedBy(operatorId);
            version.setCreateTime(LocalDateTime.now());
            versionRepository.insert(version);
        }

        return documentId;
    }

    /**
     * 更新文档
     *
     * @param id         文档ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, ContentDocumentUpdateRequestDTO request, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证：只有创建者可以更新
        validateDocumentEditPermission(exist, operatorId);

        // 3. 构建更新实体
        ContentDocument document = BeanConverter.toBean(request, ContentDocument.class);
        document.setId(id);
        document.setUpdateBy(operatorId);
        document.setUpdateTime(LocalDateTime.now());
        document.setVersion(exist.getVersion());

        // 4. 更新数据库
        return documentRepository.update(document) > 0;
    }

    /**
     * 保存文档内容（MDXEditor 集成核心方法）
     *
     * @param documentId 文档ID
     * @param request    保存请求
     * @param operatorId 操作人ID
     * @return 新版本号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveContent(Long documentId, ContentDocumentSaveContentRequestDTO request, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证
        validateDocumentEditPermission(exist, operatorId);

        // 3. 统计字数和阅读时长
        int wordCount = MarkdownUtils.countWords(request.getContentBody());
        int readDuration = MarkdownUtils.calculateReadDuration(wordCount);

        // 4. 获取当前最大版本号
        Integer currentVersion = versionRepository.selectMaxVersionNumber(documentId);
        if (currentVersion == null) {
            currentVersion = 1;
        }
        Integer newVersionNumber = currentVersion + 1;

        // 5. 创建新版本记录
        ContentDocumentVersion version = new ContentDocumentVersion();
        version.setDocumentId(documentId);
        version.setVersionNumber(newVersionNumber);
        version.setContentBody(request.getContentBody());
        version.setFileId(exist.getFileId());
        version.setChangeSummary(request.getChangeSummary() != null ? request.getChangeSummary() : "保存文档内容");
        version.setWordCount(wordCount);
        version.setCreatedBy(operatorId);
        version.setCreateTime(LocalDateTime.now());
        versionRepository.insert(version);

        // 6. 更新文档内容、版本号、字数统计
        documentRepository.updateContentAndVersion(
                documentId,
                request.getContentBody(),
                wordCount,
                readDuration,
                newVersionNumber,
                operatorId,
                exist.getVersion()
        );

        // 7. 如果需要，更新标题和摘要（需要重新查询以获取最新数据）
        if (request.getDocTitle() != null || (request.getAutoGenerateSummary() != null && request.getAutoGenerateSummary())) {
            // 重新查询文档以获取最新数据（包含刚才更新的版本号）
            ContentDocument latestDoc = documentRepository.selectById(documentId);
            if (latestDoc == null) {
                throw new BusinessException("文档不存在");
            }

            if (request.getDocTitle() != null) {
                latestDoc.setDocTitle(request.getDocTitle());
            }
            if (request.getAutoGenerateSummary() != null && request.getAutoGenerateSummary()
                    && (latestDoc.getDocSummary() == null || latestDoc.getDocSummary().isEmpty())) {
                String summary = MarkdownUtils.extractSummary(request.getContentBody(), 200);
                latestDoc.setDocSummary(summary);
            }

            latestDoc.setUpdateBy(operatorId);
            latestDoc.setUpdateTime(LocalDateTime.now());
            documentRepository.update(latestDoc);
        }

        return newVersionNumber;
    }

    /**
     * 发布文档
     *
     * @param id         文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(Long id, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        validateDocumentEditPermission(exist, operatorId);

        // 2. 验证文档状态
        if (DocumentStatus.PUBLISHED.getCode().equals(exist.getDocStatus())) {
            throw new BusinessException("文档已发布，无需重复操作");
        }

        // 3. 更新发布状态
        return documentRepository.updatePublishStatus(
                id,
                DocumentStatus.PUBLISHED.getCode(),
                LocalDateTime.now(),
                operatorId,
                exist.getVersion()
        ) > 0;
    }

    /**
     * 下架文档
     *
     * @param id         文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean offline(Long id, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        validateDocumentEditPermission(exist, operatorId);

        // 2. 更新下架状态
        return documentRepository.updatePublishStatus(
                id,
                DocumentStatus.OFFLINE.getCode(),
                null,
                operatorId,
                exist.getVersion()
        ) > 0;
    }

    /**
     * 移动文档到其他目录
     *
     * @param id          文档ID
     * @param directoryId 目标目录ID（null表示移动到根目录）
     * @param operatorId  操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean moveToDirectory(Long id, Long directoryId, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        validateDocumentEditPermission(exist, operatorId);

        // 2. 如果指定了目录，验证目录存在且属于同一空间
        if (directoryId != null) {
            validateDirectoryBelongsToSpace(directoryId, exist.getSpaceId());
        }

        // 3. 移动文档
        return documentRepository.moveToDirectory(id, directoryId, operatorId) > 0;
    }

    /**
     * 删除文档（逻辑删除）
     *
     * @param id         文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id, Long operatorId) {
        // 1. 查询文档并验证权限
        ContentDocument exist = documentRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        validateDocumentEditPermission(exist, operatorId);

        // 2. 逻辑删除
        return documentRepository.deleteById(id, operatorId) > 0;
    }

    /**
     * 根据ID查询文档详情
     *
     * @param id         文档ID
     * @param operatorId 操作人ID（用于权限判断，可为null）
     * @return 文档详情
     */
    @Override
    public ContentDocumentDetailResponseDTO getById(Long id, Long operatorId) {
        // 1. 查询文档
        ContentDocument document = documentRepository.selectById(id);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证：检查是否有查看权限
        validateDocumentViewPermission(document, operatorId);

        // 3. 转换为响应DTO
        return buildDocumentDetail(document, operatorId);
    }

    /**
     * 根据GUID查询文档详情
     *
     * @param docGuid    文档GUID
     * @param operatorId 操作人ID（用于权限判断，可为null）
     * @return 文档详情
     */
    @Override
    public ContentDocumentDetailResponseDTO getByDocGuid(String docGuid, Long operatorId) {
        // 1. 查询文档
        ContentDocument document = documentRepository.selectByDocGuid(docGuid);
        if (document == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证
        validateDocumentViewPermission(document, operatorId);

        // 3. 转换为响应DTO
        return buildDocumentDetail(document, operatorId);
    }

    /**
     * 根据空间ID查询文档列表
     *
     * @param spaceId    空间ID
     * @param operatorId 操作人ID（可为null）
     * @return 文档列表
     */
    @Override
    public List<ContentDocumentResponseDTO> listBySpaceId(Long spaceId, Long operatorId) {
        List<ContentDocument> documents = documentRepository.selectBySpaceId(spaceId);
        return buildDocumentResponseList(documents, operatorId);
    }

    /**
     * 根据目录ID查询文档列表
     *
     * @param directoryId 目录ID
     * @param operatorId  操作人ID（可为null）
     * @return 文档列表
     */
    @Override
    public List<ContentDocumentResponseDTO> listByDirectoryId(Long directoryId, Long operatorId) {
        List<ContentDocument> documents = documentRepository.selectByDirectoryId(directoryId);
        return buildDocumentResponseList(documents, operatorId);
    }

    /**
     * 分页查询文档
     *
     * @param request     查询条件
     * @param pageRequest 分页参数
     * @param operatorId  操作人ID（可为null）
     * @return 分页结果
     */
    @Override
    public PageResult<ContentDocumentResponseDTO> page(
            ContentDocumentQueryRequestDTO request,
            PageRequest pageRequest,
            Long operatorId) {

        // 1. 构建查询参数
        Map<String, Object> queryParams = new HashMap<>();
        if (request.getSpaceId() != null) {
            queryParams.put("spaceId", request.getSpaceId());
        }
        if (request.getDirectoryId() != null) {
            queryParams.put("directoryId", request.getDirectoryId());
        }
        if (request.getDocTitle() != null) {
            queryParams.put("docTitle", request.getDocTitle());
        }
        if (request.getContentType() != null) {
            queryParams.put("contentType", request.getContentType());
        }
        if (request.getDocStatus() != null) {
            queryParams.put("docStatus", request.getDocStatus());
        }
        if (request.getIsPaid() != null) {
            queryParams.put("isPaid", request.getIsPaid());
        }
        if (request.getCreateBy() != null) {
            queryParams.put("createBy", request.getCreateBy());
        }
        if (request.getStatus() != null) {
            queryParams.put("status", request.getStatus());
        }

        // 2. 执行分页查询
        PageResult<ContentDocument> pageResult = documentRepository.selectPage(queryParams, pageRequest);

        // 3. 转换为响应DTO
        List<ContentDocumentResponseDTO> records = buildDocumentResponseList(
                pageResult.getRecords(),
                operatorId
        );

        return new PageResult<>(
                records,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
    }

    /**
     * 绑定标签
     *
     * @param documentId 文档ID
     * @param tagIds     标签ID列表
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindTags(Long documentId, List<Long> tagIds, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证
        validateDocumentEditPermission(exist, operatorId);

        // 3. 验证标签数量限制
        if (CollectionUtils.isNotEmpty(tagIds) && tagIds.size() > DocumentConstants.MAX_TAG_COUNT) {
            throw new BusinessException("标签数量不能超过" + DocumentConstants.MAX_TAG_COUNT + "个");
        }

        // 4. 替换标签（先删除旧的，再插入新的）
        return documentTagRepository.replaceDocumentTags(documentId, tagIds);
    }

    /**
     * 解绑标签
     *
     * @param documentId 文档ID
     * @param tagId      标签ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindTag(Long documentId, Long tagId, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 权限验证
        validateDocumentEditPermission(exist, operatorId);

        // 3. 删除关联
        documentTagRepository.deleteByDocumentId(documentId);
        return true;
    }

    /**
     * 增加浏览次数
     *
     * @param documentId 文档ID
     */
    @Override
    public void incrementViewCount(Long documentId) {
        documentRepository.incrementViewCount(documentId);
    }

    /**
     * 点赞文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean like(Long documentId, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 检查是否已点赞
        boolean hasLiked = contentActionRepository.exists(operatorId, documentId, "LIKE");
        if (hasLiked) {
            throw new BusinessException("您已点赞过该文档");
        }

        // 3. 记录点赞行为
        cn.refinex.kb.entity.ContentAction action = cn.refinex.kb.entity.ContentAction.builder()
                .userId(operatorId)
                .documentId(documentId)
                .actionType("LIKE")
                .actionValue(null)
                .actionTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .build();
        contentActionRepository.insert(action);

        // 4. 增加点赞数
        documentRepository.incrementLikeCount(documentId);
        return true;
    }

    /**
     * 取消点赞文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlike(Long documentId, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 检查是否已点赞
        boolean hasLiked = contentActionRepository.exists(operatorId, documentId, "LIKE");
        if (!hasLiked) {
            throw new BusinessException("您未点赞该文档");
        }

        // 3. 删除点赞记录
        contentActionRepository.delete(operatorId, documentId, "LIKE");

        // 4. 减少点赞数
        documentRepository.decrementLikeCount(documentId);
        return true;
    }

    /**
     * 收藏文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collect(Long documentId, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 检查是否已收藏
        boolean hasCollected = contentActionRepository.exists(operatorId, documentId, "COLLECT");
        if (hasCollected) {
            throw new BusinessException("您已收藏过该文档");
        }

        // 3. 记录收藏行为
        cn.refinex.kb.entity.ContentAction action = cn.refinex.kb.entity.ContentAction.builder()
                .userId(operatorId)
                .documentId(documentId)
                .actionType("COLLECT")
                .actionValue(null)
                .actionTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .build();
        contentActionRepository.insert(action);

        // 4. 增加收藏数
        documentRepository.incrementCollectCount(documentId);
        return true;
    }

    /**
     * 取消收藏文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uncollect(Long documentId, Long operatorId) {
        // 1. 验证文档存在
        ContentDocument exist = documentRepository.selectById(documentId);
        if (exist == null) {
            throw new BusinessException("文档不存在");
        }

        // 2. 检查是否已收藏
        boolean hasCollected = contentActionRepository.exists(operatorId, documentId, "COLLECT");
        if (!hasCollected) {
            throw new BusinessException("您未收藏该文档");
        }

        // 3. 删除收藏记录
        contentActionRepository.delete(operatorId, documentId, "COLLECT");

        // 4. 减少收藏数
        documentRepository.decrementCollectCount(documentId);
        return true;
    }

    /**
     * 复制文档
     *
     * @param id             源文档ID
     * @param newSpaceId     目标空间ID（可为null，表示复制到同一空间）
     * @param newDirectoryId 目标目录ID（可为null）
     * @param operatorId     操作人ID
     * @return 新文档ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copy(Long id, Long newSpaceId, Long newDirectoryId, Long operatorId) {
        // 1. 查询源文档
        ContentDocument source = documentRepository.selectById(id);
        if (source == null) {
            throw new BusinessException("源文档不存在");
        }

        // 2. 验证目标空间
        Long targetSpaceId = newSpaceId != null ? newSpaceId : source.getSpaceId();
        validateSpaceAccess(targetSpaceId, operatorId);

        // 3. 如果指定了目录，验证目录
        if (newDirectoryId != null) {
            validateDirectoryBelongsToSpace(newDirectoryId, targetSpaceId);
        }

        // 4. 创建新文档
        String newDocGuid = DocumentConstants.DOC_GUID_PREFIX + IdUtil.fastSimpleUUID();

        ContentDocument newDocument = new ContentDocument();
        BeanConverter.copyProperties(source, newDocument);
        newDocument.setId(null);
        newDocument.setDocGuid(newDocGuid);
        newDocument.setDocTitle(source.getDocTitle() + " - 副本");
        newDocument.setSpaceId(targetSpaceId);
        newDocument.setDirectoryId(newDirectoryId);
        newDocument.setDocStatus(DocumentStatus.DRAFT.getCode());
        newDocument.setVersionNumber(1);
        newDocument.setPublishTime(null);
        newDocument.setViewCount(0L);
        newDocument.setLikeCount(0L);
        newDocument.setCollectCount(0L);
        newDocument.setCommentCount(0L);
        newDocument.setShareCount(0L);
        newDocument.setCreateBy(operatorId);
        newDocument.setCreateTime(LocalDateTime.now());
        newDocument.setUpdateBy(operatorId);
        newDocument.setUpdateTime(LocalDateTime.now());
        newDocument.setVersion(0);

        // 5. 插入新文档
        Long newDocumentId = documentRepository.insert(newDocument);

        // 6. 复制最新版本的内容
        ContentDocumentVersion latestVersion = versionRepository.selectLatestByDocumentId(id);
        if (latestVersion != null) {
            ContentDocumentVersion newVersion = new ContentDocumentVersion();
            newVersion.setDocumentId(newDocumentId);
            newVersion.setVersionNumber(1);
            newVersion.setContentBody(latestVersion.getContentBody());
            newVersion.setFileId(latestVersion.getFileId());
            newVersion.setChangeSummary("从文档 #" + id + " 复制");
            newVersion.setWordCount(latestVersion.getWordCount());
            newVersion.setCreatedBy(operatorId);
            newVersion.setCreateTime(LocalDateTime.now());
            versionRepository.insert(newVersion);
        }

        // 7. 复制标签关联
        List<ContentTag> tags = documentTagRepository.selectTagsByDocumentId(id);
        if (CollectionUtils.isNotEmpty(tags)) {
            List<Long> tagIds = tags.stream()
                    .map(ContentTag::getId)
                    .toList();

            documentTagRepository.replaceDocumentTags(newDocumentId, tagIds);
        }

        return newDocumentId;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 验证空间存在且用户有访问权限
     *
     * @param spaceId 空间ID
     * @param userId  用户ID
     */
    private void validateSpaceAccess(Long spaceId, Long userId) {
        ContentSpace space = spaceRepository.selectById(spaceId);
        if (space == null) {
            throw new BusinessException("空间不存在");
        }

        // 空间访问权限验证
        // 1. 私有空间：只有拥有者可以访问
        if (space.getAccessType() != null
                && space.getAccessType().equals(DocumentAccessType.CUSTOM_PRIVATE.getCode())
                && !space.getOwnerId().equals(userId)) {
            throw new BusinessException("该空间为私有空间，您没有访问权限");
        }

        // 3. 公开空间：所有人都可以访问
        // accessType == 1 或 null，不做限制
    }

    /**
     * 验证目录属于指定空间
     *
     * @param directoryId 目录ID
     * @param spaceId     空间ID
     */
    private void validateDirectoryBelongsToSpace(Long directoryId, Long spaceId) {
        cn.refinex.kb.entity.ContentDirectory directory = contentDirectoryRepository.selectById(directoryId);
        if (directory == null) {
            throw new BusinessException("目录不存在");
        }

        if (!directory.getSpaceId().equals(spaceId)) {
            throw new BusinessException("目录不属于指定空间");
        }
    }

    /**
     * 验证文档编辑权限
     *
     * @param document 文档实体
     * @param userId   用户ID
     */
    private void validateDocumentEditPermission(ContentDocument document, Long userId) {
        // 1. 文档创建者有编辑权限
        if (document.getCreateBy().equals(userId)) {
            return;
        }

        // 2. 空间拥有者有编辑权限
        ContentSpace space = spaceRepository.selectById(document.getSpaceId());
        if (space != null && space.getOwnerId().equals(userId)) {
            return;
        }

        // 3. 其他情况无编辑权限
        throw new BusinessException("无权编辑此文档");
    }

    /**
     * 验证文档查看权限
     *
     * @param document 文档实体
     * @param userId   用户ID
     */
    private void validateDocumentViewPermission(ContentDocument document, Long userId) {
        // 1. 草稿状态只有创建者可见
        if (DocumentStatus.DRAFT.getCode().equals(document.getDocStatus())) {
            if (!document.getCreateBy().equals(userId)) {
                throw new BusinessException("文档未发布");
            }
            return;
        }

        // 2. 已下架状态只有创建者可见
        if (DocumentStatus.OFFLINE.getCode().equals(document.getDocStatus())) {
            if (!document.getCreateBy().equals(userId)) {
                throw new BusinessException("文档已下架");
            }
            return;
        }

        // 3. 检查访问类型
        if (document.getAccessType() == null || document.getAccessType() == 0) {
            // 继承空间权限 - 实现空间级别权限检查
            ContentSpace space = spaceRepository.selectById(document.getSpaceId());
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
                ContentSpace space = spaceRepository.selectById(document.getSpaceId());
                if (space == null || !space.getOwnerId().equals(userId)) {
                    throw new BusinessException("此文档为私有文档");
                }
            }
        }

        // accessType == 2 为公开，所有人可见
    }

    /**
     * 构建文档详情响应DTO
     *
     * @param document 文档实体
     * @param operatorId 操作人ID（用于权限验证）
     * @return 文档详情响应DTO
     */
    private ContentDocumentDetailResponseDTO buildDocumentDetail(ContentDocument document, Long operatorId) {
        ContentDocumentDetailResponseDTO dto = BeanConverter.toBean(document, ContentDocumentDetailResponseDTO.class);

        // 设置枚举描述
        if (document.getDocStatus() != null) {
            dto.setDocStatusDesc(DocumentStatus.getDescription(document.getDocStatus()));
        }

        // 查询空间信息
        if (document.getSpaceId() != null) {
            ContentSpace space = spaceRepository.selectById(document.getSpaceId());
            if (space != null) {
                dto.setSpaceCode(space.getSpaceCode());
                dto.setSpaceName(space.getSpaceName());
            }
        }

        // 查询目录信息和路径
        if (document.getDirectoryId() != null) {
            cn.refinex.kb.entity.ContentDirectory directory = contentDirectoryRepository.selectById(document.getDirectoryId());
            if (directory != null) {
                dto.setDirectoryName(directory.getDirectoryName());
                dto.setDirectoryPath(directory.getDirectoryPath());
            }
        }

        // 查询标签
        List<ContentTag> tags = documentTagRepository.selectTagsByDocumentId(document.getId());
        if (CollectionUtils.isNotEmpty(tags)) {
            List<ContentDocumentDetailResponseDTO.DocumentTagDTO> tagDTOs = tags.stream()
                    .map(tag -> ContentDocumentDetailResponseDTO.DocumentTagDTO.builder()
                            .id(tag.getId())
                            .tagName(tag.getTagName())
                            .tagColor(tag.getTagColor())
                            .tagType(tag.getTagType())
                            .usageCount(tag.getUsageCount())
                            .build())
                    .toList();
            dto.setTags(tagDTOs);
        }

        // 查询创建人和更新人信息
        if (document.getCreateBy() != null) {
            String createByName = getUserNickname(document.getCreateBy());
            dto.setCreateByName(createByName);
        }

        if (document.getUpdateBy() != null) {
            String updateByName = getUserNickname(document.getUpdateBy());
            dto.setUpdateByName(updateByName);
        }

        // 设置用户相关字段（是否点赞、收藏、权限等）
        if (operatorId != null) {
            // 权限字段 - 创建者或空间拥有者可以编辑和删除
            boolean isCreator = false;
            if (document.getCreateBy() != null) {
                isCreator = document.getCreateBy().equals(operatorId);
            }
            boolean isSpaceOwner = false;

            ContentSpace space = spaceRepository.selectById(document.getSpaceId());
            if (space != null && space.getOwnerId().equals(operatorId)) {
                isSpaceOwner = true;
            }

            dto.setCanEdit(isCreator || isSpaceOwner);
            dto.setCanDelete(isCreator || isSpaceOwner);

            // 用户行为字段
            try {
                boolean hasLiked = contentActionRepository.exists(operatorId, document.getId(), "LIKE");
                dto.setIsLiked(hasLiked);

                boolean hasCollected = contentActionRepository.exists(operatorId, document.getId(), "COLLECT");
                dto.setIsCollected(hasCollected);
            } catch (Exception e) {
                log.error("查询用户行为失败，operatorId: {}, documentId: {}", operatorId, document.getId(), e);
                dto.setIsLiked(false);
                dto.setIsCollected(false);
            }
        } else {
            dto.setCanEdit(false);
            dto.setCanDelete(false);
            dto.setIsLiked(false);
            dto.setIsCollected(false);
        }

        return dto;
    }

    /**
     * 批量构建文档响应列表DTO
     *
     * @param documents 文档实体列表
     * @param operatorId 操作人ID（用于权限验证）
     * @return 文档响应DTO列表
     */
    private List<ContentDocumentResponseDTO> buildDocumentResponseList(List<ContentDocument> documents, Long operatorId) {
        if (CollectionUtils.isEmpty(documents)) {
            return Collections.emptyList();
        }

        // 批量查询标签
        List<Long> documentIds = documents.stream()
                .map(ContentDocument::getId)
                .toList();

        Map<Long, List<ContentTag>> tagsMap = documentTagRepository.selectTagsMapByDocumentIds(documentIds);

        // 批量查询创建人信息
        Set<Long> createByIds = documents.stream()
                .map(ContentDocument::getCreateBy)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> userNicknameMap = batchGetUserNicknames(new ArrayList<>(createByIds));

        // 转换为响应DTO
        return documents.stream()
                .map(document -> {
                    ContentDocumentResponseDTO dto = BeanConverter.toBean(document, ContentDocumentResponseDTO.class);

                    // 设置枚举描述
                    if (document.getDocStatus() != null) {
                        dto.setDocStatusDesc(DocumentStatus.getDescription(document.getDocStatus()));
                    }

                    // 设置标签
                    List<ContentTag> tags = tagsMap.get(document.getId());
                    if (CollectionUtils.isNotEmpty(tags)) {
                        List<ContentDocumentResponseDTO.DocumentTagDTO> tagDTOs = tags.stream()
                                .map(tag -> ContentDocumentResponseDTO.DocumentTagDTO.builder()
                                        .id(tag.getId())
                                        .tagName(tag.getTagName())
                                        .tagColor(tag.getTagColor())
                                        .tagType(tag.getTagType())
                                        .build())
                                .toList();
                        dto.setTags(tagDTOs);
                    }

                    // 设置创建人昵称
                    if (document.getCreateBy() != null) {
                        dto.setCreateByName(userNicknameMap.get(document.getCreateBy()));
                    }

                    return dto;
                })
                .toList();
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
     * 批量获取用户昵称
     *
     * @param userIds 用户ID列表
     * @return 用户ID到昵称的映射
     */
    private Map<Long, String> batchGetUserNicknames(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }

        try {
            ApiResult<Map<String, Object>> result = platformUserServiceClient.getUsernameMap(userIds);
            if (result != null && result.is2xxSuccess() && result.data() != null) {
                Map<Long, String> nicknameMap = new HashMap<>();
                for (Map.Entry<String, Object> entry : result.data().entrySet()) {
                    try {
                        Long userId = Long.parseLong(entry.getKey());
                        String nickname = entry.getValue() != null ? entry.getValue().toString() : "未知用户";
                        nicknameMap.put(userId, nickname);
                    } catch (NumberFormatException e) {
                        log.warn("解析用户ID失败: {}", entry.getKey());
                    }
                }
                return nicknameMap;
            }
        } catch (Exception e) {
            log.error("批量获取用户昵称失败: userIds={}", userIds, e);
        }

        // 如果批量查询失败，降级为逐个查询
        Map<Long, String> result = new HashMap<>();
        for (Long userId : userIds) {
            result.put(userId, getUserNickname(userId));
        }

        return result;
    }

    /**
     * 记录文档浏览
     *
     * @param documentId 文档ID
     * @param userId     用户ID（可为null表示未登录）
     * @param duration   浏览时长（秒）
     * @return 是否成功
     */
    @Override
    public boolean recordView(Long documentId, Long userId, Integer duration) {
        try {
            // 1. 增加文档浏览次数
            incrementViewCount(documentId);

            // 2. 如果用户已登录，记录用户浏览行为
            if (userId != null) {
                int viewDuration = (duration != null && duration > 0) ? duration : 0;
                contentActionRepository.insertOrUpdate(
                        userId,
                        documentId,
                        "VIEW",
                        viewDuration
                );
            }

            return true;
        } catch (Exception e) {
            log.error("记录文档浏览失败，documentId: {}, userId: {}", documentId, userId, e);
            return false;
        }
    }
}

