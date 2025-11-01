package cn.refinex.ai.controller.vector.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 添加文档请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "添加文档请求")
public class AddDocumentsRequest {

    @NotBlank(message = "模型编码不能为空")
    @Schema(description = "模型编码")
    private String modelCode;

    @Valid
    @NotEmpty(message = "文档列表不能为空")
    @Size(min = 1, max = 100, message = "文档数量必须在1-100之间")
    @Schema(description = "文档列表")
    private List<DocumentDTO> documents;
}

