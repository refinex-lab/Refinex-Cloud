package cn.refinex.platform.entity.sys;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 敏感数据实体类
 * <p>
 * 对应数据库表：sys_sensitive
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "敏感数据实体")
public class SysSensitive {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "被加密记录的唯一标识，对应业务表主键", example = "1234567890")
    private String rowGuid;

    @Schema(description = "来源表名", example = "sys_user")
    private String tableName;

    @Schema(description = "字段代码", example = "mobile")
    private String fieldCode;

    @Schema(description = "AES-256-GCM加密后的密文")
    private String encryptedValue;

    @Schema(description = "加密算法标识", example = "AES-256-GCM")
    private String encryptionAlgorithm;
}
