package cn.refinex.platform.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * <p>
 * 对应数据库表：sys_config
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统配置实体")
public class SysConfig {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "配置键", example = "system.title")
    private String configKey;

    @Schema(description = "配置值", example = "Refinex Cloud")
    private String configValue;

    @Schema(description = "配置类型：STRING,NUMBER,BOOLEAN,JSON", example = "STRING")
    private String configType;

    @Schema(description = "配置分组", example = "system")
    private String configGroup;

    @Schema(description = "配置标签", example = "系统标题")
    private String configLabel;

    @Schema(description = "配置说明", example = "系统显示标题")
    private String configDesc;

    @Schema(description = "是否敏感配置：0否,1是", example = "0")
    private Integer isSensitive;

    @Schema(description = "是否前端可见：0否,1是", example = "1")
    private Integer isFrontend;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "系统配置项")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;
}
