package cn.refinex.kb.service.impl;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.controller.tag.dto.request.ContentTagCreateRequestDTO;
import cn.refinex.kb.controller.tag.dto.request.ContentTagUpdateRequestDTO;
import cn.refinex.kb.controller.tag.dto.response.ContentTagResponseDTO;
import cn.refinex.kb.entity.ContentTag;
import cn.refinex.kb.repository.ContentTagRepository;
import cn.refinex.kb.service.ContentTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 内容标签服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentTagServiceImpl implements ContentTagService {

    private final JdbcTemplateManager jdbcManager;
    private final ContentTagRepository contentTagRepository;
    private final cn.refinex.kb.repository.ContentDocumentTagRepository documentTagRepository;

    /**
     * 创建标签
     *
     * @param request    创建请求
     * @param operatorId 操作人ID（即标签创建者）
     * @return 标签ID
     */
    @Override
    public Long create(ContentTagCreateRequestDTO request, Long operatorId) {
        // 校验：同一用户下标签名不能重复
        if (contentTagRepository.existsName(operatorId, request.getTagName(), null)) {
            throw new BusinessException("标签名称已存在");
        }

        ContentTag entity = BeanConverter.toBean(request, ContentTag.class);
        entity.setCreatorId(operatorId);
        entity.setUsageCount(0L);
        entity.setCreateBy(operatorId);
        entity.setUpdateBy(operatorId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        // 如果没有指定颜色，使用默认颜色
        if (entity.getTagColor() == null || entity.getTagColor().isEmpty()) {
            entity.setTagColor(generateRandomColor());
        }

        // 如果没有指定类型，默认为用户自定义标签
        if (entity.getTagType() == null) {
            entity.setTagType(1);
        }

        if (entity.getStatus() == null) {
            entity.setStatus(0);
        }

        return jdbcManager.executeInTransaction(tx -> contentTagRepository.insert(tx, entity));
    }

    /**
     * 更新标签
     *
     * @param id         标签ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean update(Long id, ContentTagUpdateRequestDTO request, Long operatorId) {
        // 校验标签是否存在
        ContentTag exist = contentTagRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("标签不存在");
        }

        // 权限校验：只有创建者本人或系统管理员可以修改标签
        // 这里简化处理，只允许创建者本人修改
        if (!exist.getCreatorId().equals(operatorId)) {
            throw new BusinessException("无权修改他人的标签");
        }

        // 校验：同一用户下标签名不能重复
        if (contentTagRepository.existsName(operatorId, request.getTagName(), id)) {
            throw new BusinessException("标签名称已存在");
        }

        exist.setTagName(request.getTagName());
        exist.setTagColor(request.getTagColor());
        exist.setRemark(request.getRemark());
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = jdbcManager.executeInTransaction(tx -> contentTagRepository.updateById(tx, exist));
        return rows > 0;
    }

    /**
     * 删除标签
     *
     * @param id         标签ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id, Long operatorId) {
        // 校验标签是否存在
        ContentTag exist = contentTagRepository.selectById(id);
        if (exist == null) {
            return false;
        }

        // 权限校验：只有创建者本人可以删除标签
        if (!exist.getCreatorId().equals(operatorId)) {
            throw new BusinessException("无权删除他人的标签");
        }

        // 检查标签是否被文档使用，如果被使用则提示用户
        long documentCount = documentTagRepository.countByTagId(id);
        if (documentCount > 0) {
            throw new BusinessException("该标签已被 " + documentCount + " 个文档使用，无法删除。请先移除文档中的标签关联");
        }

        int rows = jdbcManager.executeInTransaction(tx -> contentTagRepository.softDeleteById(tx, id, operatorId));
        return rows > 0;
    }

    /**
     * 批量删除标签
     *
     * @param ids        标签ID列表
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean batchDelete(List<Long> ids, Long operatorId) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 权限校验：检查所有标签是否都属于当前用户
        for (Long id : ids) {
            ContentTag tag = contentTagRepository.selectById(id);
            if (tag != null && !tag.getCreatorId().equals(operatorId)) {
                throw new BusinessException("无权删除他人的标签");
            }
        }

        // 检查标签是否被文档使用
        List<String> usedTags = new java.util.ArrayList<>();
        for (Long id : ids) {
            long documentCount = documentTagRepository.countByTagId(id);
            if (documentCount > 0) {
                ContentTag tag = contentTagRepository.selectById(id);
                String tagName = tag != null ? tag.getTagName() : "ID:" + id;
                usedTags.add(tagName + "(" + documentCount + "个文档)");
            }
        }

        if (!usedTags.isEmpty()) {
            throw new BusinessException("以下标签已被文档使用，无法删除：" + String.join("、", usedTags) + "。请先移除文档中的标签关联");
        }

        int rows = jdbcManager.executeInTransaction(tx ->
                contentTagRepository.batchSoftDelete(tx, ids, operatorId)
        );

        return rows > 0;
    }

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签详情
     */
    @Override
    public ContentTagResponseDTO getById(Long id) {
        ContentTag tag = contentTagRepository.selectById(id);
        if (tag == null) {
            return null;
        }
        return BeanConverter.toBean(tag, ContentTagResponseDTO.class);
    }

    /**
     * 查询当前用户的所有标签（用户端）
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    @Override
    public List<ContentTagResponseDTO> listByUser(Long userId) {
        List<ContentTag> tags = contentTagRepository.listByCreator(userId);
        return tags.stream()
                .map(tag -> BeanConverter.toBean(tag, ContentTagResponseDTO.class))
                .toList();
    }

    /**
     * 查询系统标签（所有人共享）
     *
     * @return 系统标签列表
     */
    @Override
    public List<ContentTagResponseDTO> listSystemTags() {
        List<ContentTag> tags = contentTagRepository.listSystemTags();
        return tags.stream()
                .map(tag -> BeanConverter.toBean(tag, ContentTagResponseDTO.class))
                .toList();
    }

    /**
     * 分页查询标签（用户端 - 仅当前用户）
     *
     * @param userId      用户ID
     * @param tagName     标签名称（可选）
     * @param pageRequest 分页请求
     * @return 标签分页结果
     */
    @Override
    public PageResult<ContentTagResponseDTO> pageByUser(Long userId, String tagName, PageRequest pageRequest) {
        PageResult<ContentTag> page = contentTagRepository.pageQueryByCreator(userId, tagName, pageRequest);
        List<ContentTagResponseDTO> records = page.getRecords().stream()
                .map(tag -> BeanConverter.toBean(tag, ContentTagResponseDTO.class))
                .toList();

        return new PageResult<>(records, page.getTotal(), page.getPageNum(), page.getPageSize());
    }

    /**
     * 分页查询标签（管理端 - 所有用户）
     *
     * @param tagName     标签名称（可选）
     * @param tagType     标签类型（可选）
     * @param creatorId   创建者ID（可选）
     * @param pageRequest 分页请求
     * @return 标签分页结果
     */
    @Override
    public PageResult<ContentTagResponseDTO> pageAll(String tagName, Integer tagType, Long creatorId, PageRequest pageRequest) {
        PageResult<ContentTag> page = contentTagRepository.pageQueryAll(tagName, tagType, creatorId, pageRequest);
        List<ContentTagResponseDTO> records = page.getRecords().stream()
                .map(tag -> BeanConverter.toBean(tag, ContentTagResponseDTO.class))
                .toList();

        return new PageResult<>(records, page.getTotal(), page.getPageNum(), page.getPageSize());
    }

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     */
    @Override
    public void incrementUsageCount(Long tagId) {
        contentTagRepository.incrementUsageCount(tagId);
    }

    /**
     * 减少标签使用次数
     *
     * @param tagId 标签ID
     */
    @Override
    public void decrementUsageCount(Long tagId) {
        contentTagRepository.decrementUsageCount(tagId);
    }

    /**
     * 生成随机颜色（Ant Design 配色方案）
     *
     * @return 十六进制颜色值
     */
    private String generateRandomColor() {
        String[] colors = {
                "#1890ff", // 拂晓蓝（默认）
                "#52c41a", // 极光绿
                "#fa8c16", // 日暮黄
                "#f5222d", // 薄暮红
                "#722ed1", // 酱紫
                "#13c2c2", // 明青
                "#eb2f96", // 法式洋红
                "#faad14", // 金盏花
                "#a0d911", // 青柠
                "#2f54eb"  // 极客蓝
        };
        int index = ThreadLocalRandom.current().nextInt(colors.length);
        return colors[index];
    }
}

