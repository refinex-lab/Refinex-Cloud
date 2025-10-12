package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "字典数据创建请求")
public class DictDataCreateRequest {

    @NotNull
    @Schema(description = "字典类型ID", example = "1")
    private Long dictTypeId;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "字典标签", example = "正常")
    private String dictLabel;

    @NotBlank
    @Size(max = 128)
    @Schema(description = "字典值", example = "1")
    private String dictValue;

    @Schema(description = "排序", example = "0")
    private Integer dictSort;

    @Size(max = 64)
    @Schema(description = "样式类名", example = "success")
    private String cssClass;

    @Size(max = 64)
    @Schema(description = "列表样式", example = "default")
    private String listClass;

    @Schema(description = "是否默认：0否,1是", example = "0")
    private Integer isDefault;

    @Size(max = 256)
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}