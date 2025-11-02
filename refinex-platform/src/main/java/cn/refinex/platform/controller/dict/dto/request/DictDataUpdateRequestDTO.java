package cn.refinex.platform.controller.dict.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典数据更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "字典数据更新请求")
public class DictDataUpdateRequestDTO {

    @NotNull(message = "字典类型ID不能为空")
    @Schema(description = "字典类型ID", example = "1")
    private Long dictTypeId;

    @NotBlank(message = "字典标签不能为空")
    @Size(max = 128, message = "字典标签长度不能超过128个字符")
    @Schema(description = "字典标签", example = "正常")
    private String dictLabel;

    @NotBlank(message = "字典值不能为空")
    @Size(max = 128, message = "字典值长度不能超过128个字符")
    @Schema(description = "字典值", example = "1")
    private String dictValue;

    @Schema(description = "排序", example = "0")
    private Integer dictSort;

    @Size(max = 64, message = "样式类名长度不能超过64个字符")
    @Schema(description = "样式类名", example = "success")
    private String cssClass;

    @Size(max = 64, message = "列表样式长度不能超过64个字符")
    @Schema(description = "列表样式", example = "default")
    private String listClass;

    @Schema(description = "是否默认：0否,1是", example = "0")
    private Integer isDefault;

    @Size(max = 256, message = "备注长度不能超过256个字符")
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;
}