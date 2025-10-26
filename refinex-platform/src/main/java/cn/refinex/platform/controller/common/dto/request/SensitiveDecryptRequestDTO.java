package cn.refinex.platform.controller.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 敏感数据解密请求 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "敏感数据解密请求")
public class SensitiveDecryptRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "表名不能为空")
    @Schema(description = "来源表名", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_user")
    private String tableName;

    @NotBlank(message = "记录GUID不能为空")
    @Schema(description = "被加密记录的唯一标识（业务表主键）", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String rowGuid;

    @NotBlank(message = "字段代码不能为空")
    @Schema(description = "字段代码", requiredMode = Schema.RequiredMode.REQUIRED, example = "mobile")
    private String fieldCode;
}

