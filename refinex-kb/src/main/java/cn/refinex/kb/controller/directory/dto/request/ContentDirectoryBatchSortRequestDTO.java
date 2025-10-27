package cn.refinex.kb.controller.directory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 批量更新目录排序请求DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "批量更新目录排序请求DTO")
public class ContentDirectoryBatchSortRequestDTO {

    @NotEmpty(message = "排序列表不能为空")
    @Valid
    @Schema(description = "排序列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DirectorySortItem> sortItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "目录排序项")
    public static class DirectorySortItem {
        @Schema(description = "目录ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        private Long id;

        @Schema(description = "排序值", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer sort;
    }
}

