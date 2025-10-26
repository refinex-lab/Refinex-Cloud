package cn.refinex.kb.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceCreateRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpacePublishRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceQueryRequestDTO;
import cn.refinex.kb.controller.space.dto.request.ContentSpaceUpdateRequestDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceDetailResponseDTO;
import cn.refinex.kb.controller.space.dto.response.ContentSpaceResponseDTO;

import java.util.List;

/**
 * 内容空间服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ContentSpaceService {

    /**
     * 创建内容空间
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 空间ID
     */
    Long create(ContentSpaceCreateRequestDTO request, Long operatorId);

    /**
     * 更新内容空间
     *
     * @param spaceId    空间ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean update(Long spaceId, ContentSpaceUpdateRequestDTO request, Long operatorId);

    /**
     * 发布/取消发布空间
     *
     * @param spaceId    空间ID
     * @param request    发布请求
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean publish(Long spaceId, ContentSpacePublishRequestDTO request, Long operatorId);

    /**
     * 删除内容空间
     *
     * @param spaceId    空间ID
     * @param operatorId 操作人ID
     */
    void delete(Long spaceId, Long operatorId);

    /**
     * 根据ID获取空间详情
     *
     * @param spaceId 空间ID
     * @return 空间详情
     */
    ContentSpaceDetailResponseDTO getDetail(Long spaceId);

    /**
     * 根据空间编码获取空间详情
     *
     * @param spaceCode 空间编码
     * @return 空间详情
     */
    ContentSpaceDetailResponseDTO getDetailByCode(String spaceCode);

    /**
     * 根据拥有者ID获取空间列表
     *
     * @param ownerId 拥有者ID
     * @return 空间列表
     */
    List<ContentSpaceResponseDTO> listByOwnerId(Long ownerId);

    /**
     * 分页查询空间列表
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<ContentSpaceResponseDTO> page(ContentSpaceQueryRequestDTO queryDTO, PageRequest pageRequest);

    /**
     * 增加浏览次数
     *
     * @param spaceId 空间ID
     */
    void incrementViewCount(Long spaceId);

    /**
     * 校验空间访问权限
     *
     * @param spaceId  空间ID
     * @param userId   用户ID
     * @param password 访问密码（如果需要）
     * @return 是否有权限访问
     */
    boolean validateAccess(Long spaceId, Long userId, String password);
}

