package cn.refinex.platform.controller.dict.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "字典类型创建请求")
public class DictTypeCreateRequest {

    @NotBlank
    @Size(max = 128)
    @Schema(description = "字典编码", example = "user_status")
    private String dictCode;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "字典名称", example = "用户状态")
    private String dictName;

    @Size(max = 256)
    @Schema(description = "字典描述", example = "用户账号状态字典")
    private String dictDesc;

    @Size(max = 256)
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}