package cn.refinex.ai.controller.vector.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 文档 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "文档 DTO")
public class DocumentDTO {

    @NotBlank(message = "文档ID不能为空")
    @Schema(description = "文档ID")
    private String id;

    @NotBlank(message = "文档内容不能为空")
    @Schema(description = "文档内容")
    private String content;

    @Schema(description = "文档元数据")
    private Map<String, Object> metadata;
}

