package cn.refinex.kb.controller.directory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 移动目录请求DTO（用于拖拽排序和层级迁移）
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "移动目录请求DTO")
public class ContentDirectoryMoveRequestDTO {

    @NotNull(message = "目录ID不能为空")
    @Schema(description = "要移动的目录ID", example = "5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotNull(message = "目标父目录ID不能为空")
    @Schema(description = "目标父目录ID，移动到根目录则传0", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long targetParentId;

    @Schema(description = "目标排序位置", example = "2")
    private Integer targetSort;
}

