package cn.refinex.kb.service;

import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryBatchSortRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryCreateRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryMoveRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryUpdateRequestDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryTreeResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentTreeNodeResponseDTO;

import java.util.List;

/**
 * 内容目录服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ContentDirectoryService {

    /**
     * 创建目录
     *
     * @param requestDTO 创建请求
     * @param createBy   创建人ID
     * @return 目录ID
     */
    Long createDirectory(ContentDirectoryCreateRequestDTO requestDTO, Long createBy);

    /**
     * 更新目录
     *
     * @param requestDTO 更新请求
     * @param updateBy   更新人ID
     */
    void updateDirectory(ContentDirectoryUpdateRequestDTO requestDTO, Long updateBy);

    /**
     * 移动目录（拖拽排序/层级迁移）
     *
     * @param requestDTO 移动请求
     * @param updateBy   更新人ID
     */
    void moveDirectory(ContentDirectoryMoveRequestDTO requestDTO, Long updateBy);

    /**
     * 批量更新目录排序
     *
     * @param requestDTO 批量排序请求
     * @param updateBy   更新人ID
     */
    void batchUpdateSort(ContentDirectoryBatchSortRequestDTO requestDTO, Long updateBy);

    /**
     * 删除目录（会级联删除所有子目录）
     *
     * @param directoryId 目录ID
     * @param deleteBy    删除人ID
     */
    void deleteDirectory(Long directoryId, Long deleteBy);

    /**
     * 根据ID查询目录详情
     *
     * @param directoryId 目录ID
     * @return 目录详情
     */
    ContentDirectoryResponseDTO getDirectoryById(Long directoryId);

    /**
     * 查询空间下的所有目录（树形结构）
     *
     * @param spaceId 空间ID
     * @return 目录树列表
     */
    List<ContentDirectoryTreeResponseDTO> getDirectoryTree(Long spaceId);

    /**
     * 查询空间下的所有目录（平铺列表）
     *
     * @param spaceId 空间ID
     * @return 目录列表
     */
    List<ContentDirectoryResponseDTO> getDirectoryList(Long spaceId);

    /**
     * 查询指定父目录下的子目录列表
     *
     * @param spaceId  空间ID
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    List<ContentDirectoryResponseDTO> getChildDirectories(Long spaceId, Long parentId);

    /**
     * 查询空间下的统一树结构（包含目录和文档节点）
     * 用于前端树形展示，合并目录和文档为统一的树节点
     *
     * @param spaceId 空间ID
     * @param userId  用户ID（用于权限过滤，可为null）
     * @return 统一树节点列表
     */
    List<ContentTreeNodeResponseDTO> getTreeWithDocuments(Long spaceId, Long userId);
}

