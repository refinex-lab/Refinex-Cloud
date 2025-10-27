package cn.refinex.kb.controller.directory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建目录请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建目录请求DTO")
public class ContentDirectoryCreateRequestDTO {

    @NotNull(message = "所属空间ID不能为空")
    @Schema(description = "所属空间ID", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long spaceId;

    @NotNull(message = "父目录ID不能为空，根目录请传0")
    @Schema(description = "父目录ID，根目录为0", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long parentId;

    @NotBlank(message = "目录名称不能为空")
    @Schema(description = "目录名称", example = "Java基础", requiredMode = Schema.RequiredMode.REQUIRED)
    private String directoryName;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "备注说明", example = "Java基础知识目录")
    private String remark;
}

