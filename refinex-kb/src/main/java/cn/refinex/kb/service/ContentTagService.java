package cn.refinex.kb.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.controller.tag.dto.request.ContentTagCreateRequestDTO;
import cn.refinex.kb.controller.tag.dto.request.ContentTagUpdateRequestDTO;
import cn.refinex.kb.controller.tag.dto.response.ContentTagResponseDTO;

import java.util.List;

/**
 * 内容标签服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ContentTagService {

    /**
     * 创建标签
     *
     * @param request    创建请求
     * @param operatorId 操作人ID（即标签创建者）
     * @return 标签ID
     */
    Long create(ContentTagCreateRequestDTO request, Long operatorId);

    /**
     * 更新标签
     *
     * @param id         标签ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean update(Long id, ContentTagUpdateRequestDTO request, Long operatorId);

    /**
     * 删除标签
     *
     * @param id         标签ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean delete(Long id, Long operatorId);

    /**
     * 批量删除标签
     *
     * @param ids        标签ID列表
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean batchDelete(List<Long> ids, Long operatorId);

    /**
     * 根据ID查询标签
     *
     * @param id 标签ID
     * @return 标签详情
     */
    ContentTagResponseDTO getById(Long id);

    /**
     * 查询当前用户的所有标签（用户端）
     *
     * @param userId 用户ID
     * @return 标签列表
     */
    List<ContentTagResponseDTO> listByUser(Long userId);

    /**
     * 查询系统标签（所有人共享）
     *
     * @return 系统标签列表
     */
    List<ContentTagResponseDTO> listSystemTags();

    /**
     * 分页查询标签（用户端 - 仅当前用户）
     *
     * @param userId      用户ID
     * @param tagName     标签名称（可选）
     * @param pageRequest 分页请求
     * @return 标签分页结果
     */
    PageResult<ContentTagResponseDTO> pageByUser(Long userId, String tagName, PageRequest pageRequest);

    /**
     * 分页查询标签（管理端 - 所有用户）
     *
     * @param tagName     标签名称（可选）
     * @param tagType     标签类型（可选）
     * @param creatorId   创建者ID（可选）
     * @param pageRequest 分页请求
     * @return 标签分页结果
     */
    PageResult<ContentTagResponseDTO> pageAll(String tagName, Integer tagType, Long creatorId, PageRequest pageRequest);

    /**
     * 增加标签使用次数
     *
     * @param tagId 标签ID
     */
    void incrementUsageCount(Long tagId);

    /**
     * 减少标签使用次数
     *
     * @param tagId 标签ID
     */
    void decrementUsageCount(Long tagId);
}

