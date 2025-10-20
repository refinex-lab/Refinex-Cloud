package cn.refinex.platform.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置分页查询条件
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "系统配置分页查询条件")
public class SysConfigQueryRequest {

    @Schema(description = "配置键，支持模糊")
    private String configKey;

    @Schema(description = "配置分组，支持模糊")
    private String configGroup;

    @Schema(description = "配置类型：STRING,NUMBER,BOOLEAN,JSON")
    private String configType;

    @Schema(description = "是否敏感配置：0否,1是")
    private Integer isSensitive;

    @Schema(description = "是否前端可见：0否,1是")
    private Integer isFrontend;
}


