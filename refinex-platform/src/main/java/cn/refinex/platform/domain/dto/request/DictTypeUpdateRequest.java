package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 字典类型更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "字典类型更新请求")
public class DictTypeUpdateRequest {

    @NotNull
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "字典名称", example = "用户状态")
    private String dictName;

    @Size(max = 256)
    @Schema(description = "字典描述")
    private String dictDesc;

    @Size(max = 256)
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}