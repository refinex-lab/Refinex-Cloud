package cn.refinex.platform.controller.config.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统配置 响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统配置响应")
public class SysConfigResponseDTO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置类型：STRING,NUMBER,BOOLEAN,JSON")
    private String configType;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "配置标签")
    private String configLabel;

    @Schema(description = "配置说明")
    private String configDesc;

    @Schema(description = "是否敏感配置：0否,1是")
    private Integer isSensitive;

    @Schema(description = "是否前端可见：0否,1是")
    private Integer isFrontend;

    @Schema(description = "排序字段")
    private Integer sort;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}


