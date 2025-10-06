package cn.refinex.platform.domain.entity.sys;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典类型实体类
 * <p>
 * 对应数据库表：sys_dict_type
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典类型实体")
public class SysDictType {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "字典编码", example = "user_status")
    private String dictCode;

    @Schema(description = "字典名称", example = "用户状态")
    private String dictName;

    @Schema(description = "字典描述", example = "用户账号状态字典")
    private String dictDesc;

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

    @Schema(description = "备注说明", example = "用户状态字典")
    private String remark;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}
