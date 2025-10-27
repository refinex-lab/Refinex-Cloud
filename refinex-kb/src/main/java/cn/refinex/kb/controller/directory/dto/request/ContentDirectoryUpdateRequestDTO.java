package cn.refinex.kb.controller.directory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新目录请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新目录请求DTO")
public class ContentDirectoryUpdateRequestDTO {

    @NotNull(message = "目录ID不能为空")
    @Schema(description = "目录ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotBlank(message = "目录名称不能为空")
    @Schema(description = "目录名称", example = "Java高级", requiredMode = Schema.RequiredMode.REQUIRED)
    private String directoryName;

    @Schema(description = "排序字段", example = "10")
    private Integer sort;

    @Schema(description = "备注说明", example = "Java高级知识目录")
    private String remark;
}

