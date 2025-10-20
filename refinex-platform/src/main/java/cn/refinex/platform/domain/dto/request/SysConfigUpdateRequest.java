package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统配置更新请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统配置更新请求")
public class SysConfigUpdateRequest {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID", example = "1")
    private Long id;

    @NotBlank(message = "配置值不能为空")
    @Size(max = 2048, message = "配置值长度不能超过2048个字符")
    @Schema(description = "配置值", example = "Refinex Cloud")
    private String configValue;

    @NotBlank(message = "配置类型不能为空")
    @Size(max = 32, message = "配置类型长度不能超过32个字符")
    @Schema(description = "配置类型：STRING,NUMBER,BOOLEAN,JSON", example = "STRING")
    private String configType;

    @Size(max = 64, message = "配置分组长度不能超过64个字符")
    @Schema(description = "配置分组", example = "system")
    private String configGroup;

    @Size(max = 128, message = "配置标签长度不能超过128个字符")
    @Schema(description = "配置标签", example = "系统标题")
    private String configLabel;

    @Size(max = 512, message = "配置说明长度不能超过512个字符")
    @Schema(description = "配置说明", example = "系统显示标题")
    private String configDesc;

    @NotNull(message = "是否敏感配置不能为空")
    @Schema(description = "是否敏感配置：0否,1是", example = "0")
    private Integer isSensitive;

    @NotNull(message = "是否前端可见不能为空")
    @Schema(description = "是否前端可见：0否,1是", example = "1")
    private Integer isFrontend;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;
}


