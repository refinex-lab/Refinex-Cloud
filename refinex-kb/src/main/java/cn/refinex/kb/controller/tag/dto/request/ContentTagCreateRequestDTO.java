package cn.refinex.kb.controller.tag.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 内容标签创建请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "内容标签创建请求")
public class ContentTagCreateRequestDTO {

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    @Schema(description = "标签名称", example = "Java")
    private String tagName;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "标签颜色必须是有效的十六进制颜色值")
    @Schema(description = "标签颜色，十六进制值", example = "#1890ff")
    private String tagColor;

    @Schema(description = "标签类型：0系统标签,1用户自定义标签", example = "1", defaultValue = "1")
    private Integer tagType;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注说明", example = "编程语言相关")
    private String remark;
}

