package cn.refinex.platform.controller.dict.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型创建请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "字典类型创建请求")
public class DictTypeCreateRequestDTO {

    @NotBlank(message = "字典编码不能为空")
    @Size(max = 128, message = "字典编码长度不能超过128个字符")
    @Schema(description = "字典编码", example = "user_status")
    private String dictCode;

    @NotBlank(message = "字典名称不能为空")
    @Size(max = 128, message = "字典名称长度不能超过128个字符")
    @Schema(description = "字典名称", example = "用户状态")
    private String dictName;

    @Size(max = 256, message = "字典描述长度不能超过256个字符")
    @Schema(description = "字典描述", example = "用户账号状态字典")
    private String dictDesc;

    @Size(max = 256, message = "备注长度不能超过256个字符")
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}