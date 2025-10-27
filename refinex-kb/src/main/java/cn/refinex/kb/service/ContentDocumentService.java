package cn.refinex.kb.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentCreateRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentQueryRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentSaveContentRequestDTO;
import cn.refinex.kb.controller.document.dto.request.ContentDocumentUpdateRequestDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentDetailResponseDTO;
import cn.refinex.kb.controller.document.dto.response.ContentDocumentResponseDTO;

import java.util.List;

/**
 * 文档管理服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ContentDocumentService {

    /**
     * 创建文档
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 文档ID
     */
    Long create(ContentDocumentCreateRequestDTO request, Long operatorId);

    /**
     * 更新文档基本信息
     *
     * @param documentId 文档ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean update(Long documentId, ContentDocumentUpdateRequestDTO request, Long operatorId);

    /**
     * 保存文档内容（MDXEditor 保存）
     * <p>
     * 核心功能：保存文档内容并自动创建版本
     * </p>
     *
     * @param documentId 文档ID
     * @param request    保存请求
     * @param operatorId 操作人ID
     * @return 新版本号
     */
    Integer saveContent(Long documentId, ContentDocumentSaveContentRequestDTO request, Long operatorId);

    /**
     * 发布文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean publish(Long documentId, Long operatorId);

    /**
     * 下架文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean offline(Long documentId, Long operatorId);

    /**
     * 移动文档到指定目录
     *
     * @param documentId  文档ID
     * @param directoryId 目标目录ID
     * @param operatorId  操作人ID
     * @return 是否成功
     */
    boolean moveToDirectory(Long documentId, Long directoryId, Long operatorId);

    /**
     * 删除文档
     *
     * @param documentId 文档ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean delete(Long documentId, Long operatorId);

    /**
     * 根据ID查询文档详情
     *
     * @param documentId 文档ID
     * @param userId     当前用户ID（用于权限验证）
     * @return 文档详情
     */
    ContentDocumentDetailResponseDTO getById(Long documentId, Long userId);

    /**
     * 根据文档GUID查询文档详情
     *
     * @param docGuid 文档GUID
     * @param userId  当前用户ID（用于权限验证）
     * @return 文档详情
     */
    ContentDocumentDetailResponseDTO getByDocGuid(String docGuid, Long userId);

    /**
     * 查询空间下的文档列表
     *
     * @param spaceId 空间ID
     * @param userId  当前用户ID（用于权限验证）
     * @return 文档列表
     */
    List<ContentDocumentResponseDTO> listBySpaceId(Long spaceId, Long userId);

    /**
     * 查询目录下的文档列表
     *
     * @param directoryId 目录ID
     * @param userId      当前用户ID（用于权限验证）
     * @return 文档列表
     */
    List<ContentDocumentResponseDTO> listByDirectoryId(Long directoryId, Long userId);

    /**
     * 分页查询文档列表
     *
     * @param queryDTO    查询条件
     * @param pageRequest 分页请求
     * @param userId      当前用户ID（用于权限验证）
     * @return 分页结果
     */
    PageResult<ContentDocumentResponseDTO> page(ContentDocumentQueryRequestDTO queryDTO,
                                                PageRequest pageRequest,
                                                Long userId);

    /**
     * 绑定文档标签
     *
     * @param documentId 文档ID
     * @param tagIds     标签ID列表
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean bindTags(Long documentId, List<Long> tagIds, Long operatorId);

    /**
     * 解绑文档标签
     *
     * @param documentId 文档ID
     * @param tagId      标签ID
     * @param operatorId 操作人ID
     * @return 是否成功
     */
    boolean unbindTag(Long documentId, Long tagId, Long operatorId);

    /**
     * 增加浏览次数
     *
     * @param documentId 文档ID
     */
    void incrementViewCount(Long documentId);

    /**
     * 点赞文档
     *
     * @param documentId 文档ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean like(Long documentId, Long userId);

    /**
     * 取消点赞
     *
     * @param documentId 文档ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean unlike(Long documentId, Long userId);

    /**
     * 收藏文档
     *
     * @param documentId 文档ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean collect(Long documentId, Long userId);

    /**
     * 取消收藏
     *
     * @param documentId 文档ID
     * @param userId     用户ID
     * @return 是否成功
     */
    boolean uncollect(Long documentId, Long userId);

    /**
     * 复制文档
     *
     * @param documentId        源文档ID
     * @param targetSpaceId     目标空间ID
     * @param targetDirectoryId 目标目录ID
     * @param operatorId        操作人ID
     * @return 新文档ID
     */
    Long copy(Long documentId, Long targetSpaceId, Long targetDirectoryId, Long operatorId);

    /**
     * 记录文档浏览
     *
     * @param documentId 文档ID
     * @param userId     用户ID（可为null表示未登录）
     * @param duration   浏览时长（秒）
     * @return 是否成功
     */
    boolean recordView(Long documentId, Long userId, Integer duration);
}

