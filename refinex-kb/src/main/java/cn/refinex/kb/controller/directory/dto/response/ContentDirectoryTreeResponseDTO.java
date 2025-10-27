package cn.refinex.kb.controller.directory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 目录树响应DTO（用于前端树形展示）
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "目录树响应DTO")
public class ContentDirectoryTreeResponseDTO {

    @Schema(description = "节点唯一标识key", example = "dir_1")
    private String key;

    @Schema(description = "节点显示标题", example = "Java基础")
    private String title;

    @Schema(description = "目录ID", example = "1")
    private Long id;

    @Schema(description = "父目录ID", example = "0")
    private Long parentId;

    @Schema(description = "目录名称", example = "Java基础")
    private String directoryName;

    @Schema(description = "目录路径", example = "/Java技术/Java基础")
    private String directoryPath;

    @Schema(description = "目录深度", example = "2")
    private Integer depthLevel;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "是否为叶子节点", example = "false")
    private Boolean isLeaf;

    @Schema(description = "子节点列表")
    private List<ContentDirectoryTreeResponseDTO> children;
}

