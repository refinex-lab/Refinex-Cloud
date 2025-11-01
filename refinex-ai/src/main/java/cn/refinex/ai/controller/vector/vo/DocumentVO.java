package cn.refinex.ai.controller.vector.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 文档 VO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "文档 VO")
public class DocumentVO {

    @Schema(description = "文档ID")
    private String id;

    @Schema(description = "文档内容")
    private String content;

    @Schema(description = "文档元数据")
    private Map<String, Object> metadata;

    @Schema(description = "文档得分")
    private Double score;
}

