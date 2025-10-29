package cn.refinex.kb.service.impl;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.kb.client.PlatformUserServiceClient;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryBatchSortRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryCreateRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryMoveRequestDTO;
import cn.refinex.kb.controller.directory.dto.request.ContentDirectoryUpdateRequestDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentDirectoryTreeResponseDTO;
import cn.refinex.kb.controller.directory.dto.response.ContentTreeNodeResponseDTO;
import cn.refinex.kb.entity.ContentDirectory;
import cn.refinex.kb.entity.ContentDocument;
import cn.refinex.kb.entity.ContentSpace;
import cn.refinex.kb.enums.DocumentStatus;
import cn.refinex.kb.repository.ContentDirectoryRepository;
import cn.refinex.kb.repository.ContentDocumentRepository;
import cn.refinex.kb.repository.ContentSpaceRepository;
import cn.refinex.kb.service.ContentDirectoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 内容目录服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentDirectoryServiceImpl implements ContentDirectoryService {

    private final ContentDirectoryRepository directoryRepository;
    private final ContentSpaceRepository spaceRepository;
    private final ContentDocumentRepository documentRepository;
    private final PlatformUserServiceClient platformUserServiceClient;

    /**
     * 创建目录
     *
     * @param requestDTO 创建请求
     * @param createBy    创建人ID
     * @return 目录ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDirectory(ContentDirectoryCreateRequestDTO requestDTO, Long createBy) {
        // 1. 校验空间是否存在
        ContentSpace space = spaceRepository.selectById(requestDTO.getSpaceId());
        if (space == null) {
            throw new BusinessException("空间不存在");
        }

        // 2. 校验父目录是否存在（非根目录）
        ContentDirectory parentDirectory = null;
        if (requestDTO.getParentId() != 0) {
            parentDirectory = directoryRepository.selectById(requestDTO.getParentId());
            if (parentDirectory == null) {
                throw new BusinessException("父目录不存在");
            }
            if (!parentDirectory.getSpaceId().equals(requestDTO.getSpaceId())) {
                throw new BusinessException("父目录与空间不匹配");
            }
        }

        // 3. 校验同一父目录下目录名称是否重复
        boolean exists = directoryRepository.existsByName(
                requestDTO.getSpaceId(),
                requestDTO.getParentId(),
                requestDTO.getDirectoryName(),
                null
        );
        if (exists) {
            throw new BusinessException("同一目录下已存在同名目录");
        }

        // 4. 计算层级深度和路径
        int depthLevel = 1;
        String directoryPath = "/" + requestDTO.getDirectoryName();
        if (parentDirectory != null) {
            depthLevel = parentDirectory.getDepthLevel() + 1;
            directoryPath = parentDirectory.getDirectoryPath() + "/" + requestDTO.getDirectoryName();
        }

        // 5. 如果未指定排序，则自动计算
        Integer sort = requestDTO.getSort();
        if (sort == null) {
            Integer maxSort = directoryRepository.getMaxSort(requestDTO.getSpaceId(), requestDTO.getParentId());
            sort = maxSort + 10;
        }

        // 6. 构建实体并插入
        ContentDirectory directory = ContentDirectory.builder()
                .spaceId(requestDTO.getSpaceId())
                .parentId(requestDTO.getParentId())
                .directoryName(requestDTO.getDirectoryName())
                .directoryPath(directoryPath)
                .depthLevel(depthLevel)
                .sort(sort)
                .remark(requestDTO.getRemark())
                .status(0)
                .deleted(0)
                .version(0)
                .createBy(createBy)
                .createTime(LocalDateTime.now())
                .updateBy(createBy)
                .updateTime(LocalDateTime.now())
                .build();

        return directoryRepository.insert(directory);
    }

    /**
     * 更新目录
     *
     * @param requestDTO 更新请求
     * @param updateBy   更新人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDirectory(ContentDirectoryUpdateRequestDTO requestDTO, Long updateBy) {
        // 1. 查询目录是否存在
        ContentDirectory directory = directoryRepository.selectById(requestDTO.getId());
        if (directory == null) {
            throw new BusinessException("目录不存在");
        }

        // 2. 如果修改了名称，检查是否重复
        if (!directory.getDirectoryName().equals(requestDTO.getDirectoryName())) {
            boolean exists = directoryRepository.existsByName(
                    directory.getSpaceId(),
                    directory.getParentId(),
                    requestDTO.getDirectoryName(),
                    requestDTO.getId()
            );
            if (exists) {
                throw new BusinessException("同一目录下已存在同名目录");
            }

            // 3. 更新目录路径
            String oldName = directory.getDirectoryName();
            String newPath = directory.getDirectoryPath().replace("/" + oldName, "/" + requestDTO.getDirectoryName());
            directory.setDirectoryPath(newPath);
            directory.setDirectoryName(requestDTO.getDirectoryName());

            // 4. 更新所有子目录的路径
            updateChildrenPath(directory.getId(), directory.getDirectoryPath(), "/" + oldName, "/" + requestDTO.getDirectoryName());
        }

        // 5. 更新目录信息
        directory.setSort(requestDTO.getSort() != null ? requestDTO.getSort() : directory.getSort());
        directory.setRemark(requestDTO.getRemark());
        directory.setUpdateBy(updateBy);
        directory.setUpdateTime(LocalDateTime.now());

        int rows = directoryRepository.update(directory);
        if (rows == 0) {
            throw new BusinessException("更新目录失败，请重试");
        }
    }

    /**
     * 移动目录
     *
     * @param requestDTO 移动请求
     * @param updateBy   更新人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveDirectory(ContentDirectoryMoveRequestDTO requestDTO, Long updateBy) {
        // 1. 查询要移动的目录
        ContentDirectory directory = directoryRepository.selectById(requestDTO.getId());
        if (directory == null) {
            throw new BusinessException("目录不存在");
        }

        // 2. 校验目标父目录
        if (!requestDTO.getTargetParentId().equals(0L)) {
            ContentDirectory targetParent = directoryRepository.selectById(requestDTO.getTargetParentId());
            if (targetParent == null) {
                throw new BusinessException("目标父目录不存在");
            }
            if (!targetParent.getSpaceId().equals(directory.getSpaceId())) {
                throw new BusinessException("不能跨空间移动目录");
            }

            // 不能移动到自己或自己的子目录下
            if (requestDTO.getTargetParentId().equals(requestDTO.getId())) {
                throw new BusinessException("不能移动到自己下面");
            }
            List<Long> descendantIds = directoryRepository.selectDescendantIds(requestDTO.getId());
            if (descendantIds.contains(requestDTO.getTargetParentId())) {
                throw new BusinessException("不能移动到自己的子目录下");
            }
        }

        // 3. 检查目标目录下是否有同名目录
        boolean exists = directoryRepository.existsByName(
                directory.getSpaceId(),
                requestDTO.getTargetParentId(),
                directory.getDirectoryName(),
                requestDTO.getId()
        );
        if (exists) {
            throw new BusinessException("目标目录下已存在同名目录");
        }

        // 4. 计算新的层级深度和路径
        int newDepthLevel;
        String newPath;
        if (requestDTO.getTargetParentId().equals(0L)) {
            newDepthLevel = 1;
            newPath = "/" + directory.getDirectoryName();
        } else {
            ContentDirectory targetParent = directoryRepository.selectById(requestDTO.getTargetParentId());
            newDepthLevel = targetParent.getDepthLevel() + 1;
            newPath = targetParent.getDirectoryPath() + "/" + directory.getDirectoryName();
        }

        String oldPath = directory.getDirectoryPath();
        Integer depthChange = newDepthLevel - directory.getDepthLevel();

        // 5. 更新目录的父目录和层级
        directoryRepository.updateParentAndDepth(requestDTO.getId(), requestDTO.getTargetParentId(), newDepthLevel, updateBy);

        // 6. 更新目录路径
        directoryRepository.updatePath(requestDTO.getId(), newPath, updateBy);

        // 7. 递归更新所有子目录的路径和层级
        updateChildrenPathAndDepth(requestDTO.getId(), oldPath, newPath, depthChange, updateBy);

        // 8. 更新排序
        if (requestDTO.getTargetSort() != null) {
            directoryRepository.updateSort(requestDTO.getId(), requestDTO.getTargetSort(), updateBy);
        }
    }

    /**
     * 批量更新目录排序
     *
     * @param requestDTO 批量排序请求
     * @param updateBy   更新人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateSort(ContentDirectoryBatchSortRequestDTO requestDTO, Long updateBy) {
        for (ContentDirectoryBatchSortRequestDTO.DirectorySortItem item : requestDTO.getSortItems()) {
            directoryRepository.updateSort(item.getId(), item.getSort(), updateBy);
        }
    }

    /**
     * 删除目录（级联删除所有子目录）
     *
     * @param directoryId 目录ID
     * @param deleteBy    删除人ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDirectory(Long directoryId, Long deleteBy) {
        // 1. 查询目录是否存在
        ContentDirectory directory = directoryRepository.selectById(directoryId);
        if (directory == null) {
            throw new BusinessException("目录不存在");
        }

        // 2. 查询所有子孙目录
        List<Long> descendantIds = directoryRepository.selectDescendantIds(directoryId);

        // 3. 删除所有子孙目录
        for (Long descendantId : descendantIds) {
            directoryRepository.deleteById(descendantId, deleteBy);
        }

        // 4. 删除当前目录
        int rows = directoryRepository.deleteById(directoryId, deleteBy);
        if (rows == 0) {
            throw new BusinessException("删除目录失败");
        }
    }

    /**
     * 根据ID查询目录详情
     *
     * @param directoryId 目录ID
     * @return 目录详情
     */
    @Override
    public ContentDirectoryResponseDTO getDirectoryById(Long directoryId) {
        ContentDirectory directory = directoryRepository.selectById(directoryId);
        if (directory == null) {
            throw new BusinessException("目录不存在");
        }

        ContentDirectoryResponseDTO responseDTO = BeanConverter.toBean(directory, ContentDirectoryResponseDTO.class);

        // 查询是否有子目录
        long childCount = directoryRepository.countByParentId(directory.getSpaceId(), directoryId);
        responseDTO.setHasChildren(childCount > 0);

        return responseDTO;
    }

    /**
     * 查询空间下目录树结构
     *
     * @param spaceId 空间ID
     * @return 目录树结构
     */
    @Override
    public List<ContentDirectoryTreeResponseDTO> getDirectoryTree(Long spaceId) {
        // 1. 查询空间下所有目录
        List<ContentDirectory> allDirectories = directoryRepository.selectBySpaceId(spaceId);
        if (allDirectories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 转换为响应DTO
        List<ContentDirectoryTreeResponseDTO> allNodes = allDirectories.stream()
                .map(this::convertToTreeNode)
                .toList();

        // 3. 构建树形结构
        return buildTree(allNodes, 0L);
    }

    /**
     * 查询空间下所有目录（不包含子目录）
     *
     * @param spaceId 空间ID
     * @return 目录列表
     */
    @Override
    public List<ContentDirectoryResponseDTO> getDirectoryList(Long spaceId) {
        List<ContentDirectory> directories = directoryRepository.selectBySpaceId(spaceId);

        return directories.stream()
                .map(dir -> {
                    ContentDirectoryResponseDTO dto = BeanConverter.toBean(dir, ContentDirectoryResponseDTO.class);
                    long childCount = directoryRepository.countByParentId(spaceId, dir.getId());
                    dto.setHasChildren(childCount > 0);
                    return dto;
                })
                .toList();
    }

    /**
     * 查询指定目录下的所有子目录（不包含子目录的子目录）
     *
     * @param spaceId 空间ID
     * @param parentId 父目录ID
     * @return 子目录列表
     */
    @Override
    public List<ContentDirectoryResponseDTO> getChildDirectories(Long spaceId, Long parentId) {
        List<ContentDirectory> children = directoryRepository.selectByParentId(spaceId, parentId);

        return children.stream()
                .map(dir -> {
                    ContentDirectoryResponseDTO dto = BeanConverter.toBean(dir, ContentDirectoryResponseDTO.class);
                    long childCount = directoryRepository.countByParentId(spaceId, dir.getId());
                    dto.setHasChildren(childCount > 0);
                    return dto;
                })
                .toList();
    }

    /**
     * 递归更新子目录路径（不包含子目录的子目录）
     *
     * @param directoryId 目录ID
     * @param newParentPath 新父路径
     * @param oldNamePart 旧目录名部分
     * @param newNamePart 新目录名部分
     */
    private void updateChildrenPath(Long directoryId, String newParentPath, String oldNamePart, String newNamePart) {
        List<ContentDirectory> children = directoryRepository.selectByParentId(null, directoryId);
        for (ContentDirectory child : children) {
            String childNewPath = newParentPath + "/" + child.getDirectoryName();
            directoryRepository.updatePath(child.getId(), childNewPath, null);

            // 递归更新
            updateChildrenPath(child.getId(), childNewPath, oldNamePart, newNamePart);
        }
    }

    /**
     * 递归更新子目录路径和层级（不包含子目录的子目录）
     *
     * @param directoryId 目录ID
     * @param oldParentPath 旧父路径
     * @param newParentPath 新父路径
     * @param depthChange 层级变化量
     * @param updateBy 更新人ID
     */
    private void updateChildrenPathAndDepth(Long directoryId, String oldParentPath, String newParentPath, Integer depthChange, Long updateBy) {
        // 查询所有子孙目录
        List<Long> descendantIds = directoryRepository.selectDescendantIds(directoryId);

        for (Long descendantId : descendantIds) {
            ContentDirectory descendant = directoryRepository.selectById(descendantId);
            if (descendant != null) {
                // 更新路径：替换路径前缀
                String newPath = descendant.getDirectoryPath().replace(oldParentPath, newParentPath);
                directoryRepository.updatePath(descendantId, newPath, updateBy);

                // 更新层级
                Integer newDepth = descendant.getDepthLevel() + depthChange;
                directoryRepository.updateParentAndDepth(descendantId, descendant.getParentId(), newDepth, updateBy);
            }
        }
    }

    /**
     * 转换为树节点（不包含子目录的子目录）
     *
     * @param directory 目录实体
     * @return 目录树节点DTO
     */
    private ContentDirectoryTreeResponseDTO convertToTreeNode(ContentDirectory directory) {
        return ContentDirectoryTreeResponseDTO.builder()
                .key("dir_" + directory.getId())
                .title(directory.getDirectoryName())
                .id(directory.getId())
                .parentId(directory.getParentId())
                .directoryName(directory.getDirectoryName())
                .directoryPath(directory.getDirectoryPath())
                .depthLevel(directory.getDepthLevel())
                .sort(directory.getSort())
                .isLeaf(false) // 稍后计算
                .children(new ArrayList<>())
                .build();
    }

    /**
     * 构建树形结构（递归）（不包含子目录的子目录）
     *
     * @param allNodes 所有目录节点
     * @param parentId 父目录ID
     * @return 子目录树节点列表
     */
    private List<ContentDirectoryTreeResponseDTO> buildTree(List<ContentDirectoryTreeResponseDTO> allNodes, Long parentId) {
        List<ContentDirectoryTreeResponseDTO> result = new ArrayList<>();

        for (ContentDirectoryTreeResponseDTO node : allNodes) {
            if (node.getParentId().equals(parentId)) {
                // 递归查找子节点
                List<ContentDirectoryTreeResponseDTO> children = buildTree(allNodes, node.getId());
                node.setChildren(children);
                node.setIsLeaf(children.isEmpty());
                result.add(node);
            }
        }

        // 按排序字段排序
        result.sort(Comparator.comparing(ContentDirectoryTreeResponseDTO::getSort));

        return result;
    }

    /**
     * 查询空间下的统一树结构（包含目录和文档节点）
     *
     * @param spaceId 空间ID
     * @param userId  用户ID（用于权限过滤，可为null）
     * @return 统一树节点列表
     */
    @Override
    public List<ContentTreeNodeResponseDTO> getTreeWithDocuments(Long spaceId, Long userId) {
        // 1. 查询所有目录
        List<ContentDirectory> directories = directoryRepository.selectBySpaceId(spaceId);
        if (directories.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 转换目录为统一树节点
        List<ContentTreeNodeResponseDTO> allNodes = new ArrayList<>();
        for (ContentDirectory directory : directories) {
            ContentTreeNodeResponseDTO node = convertDirectoryToTreeNode(directory);
            allNodes.add(node);
        }

        // 3. 查询所有文档
        List<ContentDocument> documents = documentRepository.selectBySpaceId(spaceId);

        // 4. 转换文档为统一树节点并加入列表
        for (ContentDocument document : documents) {
            // 权限过滤：草稿状态只有创建者可见
            if (DocumentStatus.DRAFT.getCode().equals(document.getDocStatus()) && !document.getCreateBy().equals(userId)) {
                continue; // 跳过草稿文档
            }

            ContentTreeNodeResponseDTO node = convertDocumentToTreeNode(document);
            allNodes.add(node);
        }

        // 5. 构建树形结构
        return buildUnifiedTree(allNodes, 0L);
    }

    /**
     * 转换目录为统一树节点
     *
     * @param directory 目录实体
     * @return 统一树节点
     */
    private ContentTreeNodeResponseDTO convertDirectoryToTreeNode(ContentDirectory directory) {
        return ContentTreeNodeResponseDTO.builder()
                .nodeType("directory")
                .key("dir_" + directory.getId())
                .title(directory.getDirectoryName())
                .directoryId(directory.getId())
                .directoryName(directory.getDirectoryName())
                .directoryPath(directory.getDirectoryPath())
                .parentId(directory.getParentId())
                .depthLevel(directory.getDepthLevel())
                .sort(directory.getSort())
                .isLeaf(false) // 稍后根据是否有子节点计算
                .children(new ArrayList<>())
                .build();
    }

    /**
     * 转换文档为统一树节点
     *
     * @param document 文档实体
     * @return 统一树节点
     */
    private ContentTreeNodeResponseDTO convertDocumentToTreeNode(ContentDocument document) {
        // 获取创建人昵称
        String createByName = null;
        if (document.getCreateBy() != null) {
            try {
                ApiResult<String> apiResult = platformUserServiceClient.getUsernameByUserId(document.getCreateBy());
                createByName = apiResult.data() != null ? apiResult.data() : "未知用户";
            } catch (Exception e) {
                createByName = "用户" + document.getCreateBy();
            }
        }

        return ContentTreeNodeResponseDTO.builder()
                .nodeType("document")
                .key("doc_" + document.getDocGuid())
                .title(document.getDocTitle())
                .documentId(document.getId())
                .docGuid(document.getDocGuid())
                .docTitle(document.getDocTitle())
                .docStatus(document.getDocStatus())
                .docStatusDesc(DocumentStatus.getDescription(document.getDocStatus()))
                .accessType(document.getAccessType())
                // 新增文档元信息字段
                .docSummary(document.getDocSummary())
                .coverImage(document.getCoverImage())
                .wordCount(document.getWordCount())
                .readDuration(document.getReadDuration())
                .viewCount(document.getViewCount())
                .likeCount(document.getLikeCount())
                .collectCount(document.getCollectCount())
                .commentCount(document.getCommentCount())
                .createBy(document.getCreateBy())
                .createByName(createByName)
                .createTime(document.getCreateTime() != null ? document.getCreateTime().toString() : null)
                .updateTime(document.getUpdateTime() != null ? document.getUpdateTime().toString() : null)
                // 树结构字段
                .parentId(document.getDirectoryId())
                .depthLevel(null) // 文档不需要深度
                .sort(document.getSort() != null ? document.getSort() : 999) // 文档默认排在目录后面
                .isLeaf(true) // 文档一定是叶子节点
                .children(null) // 文档没有子节点
                .build();
    }

    /**
     * 构建统一树形结构（递归）
     * 同时包含目录和文档节点
     *
     * @param allNodes 所有节点列表（包含目录和文档）
     * @param parentId 父节点ID（0表示根目录）
     * @return 统一树节点列表
     */
    private List<ContentTreeNodeResponseDTO> buildUnifiedTree(List<ContentTreeNodeResponseDTO> allNodes, Long parentId) {
        List<ContentTreeNodeResponseDTO> result = new ArrayList<>();

        for (ContentTreeNodeResponseDTO node : allNodes) {
            // 安全的 parentId 比较 - 处理 null 情况
            Long nodeParentId = node.getParentId();
            if (nodeParentId == null) {
                // 如果节点的 parentId 为 null，跳过（理论上不应该出现）
                log.warn("节点 {} 的 parentId 为 null，已跳过", node.getKey());
                continue;
            }
            
            if (nodeParentId.equals(parentId)) {
                // 如果是目录节点，递归查找子节点（包括子目录和文档）
                if ("directory".equals(node.getNodeType())) {
                    List<ContentTreeNodeResponseDTO> children = buildUnifiedTree(allNodes, node.getDirectoryId());
                    node.setChildren(children);
                    node.setIsLeaf(children.isEmpty());
                }

                result.add(node);
            }
        }

        // 按排序字段排序（目录和文档混合排序）
        result.sort(Comparator.comparing(ContentTreeNodeResponseDTO::getSort));

        return result;
    }
}

