package cn.refinex.platform.controller.dict.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 字典类型响应 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "字典类型响应")
public class DictTypeResponseDTO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "字典编码", example = "user_status")
    private String dictCode;

    @Schema(description = "字典名称", example = "用户状态")
    private String dictName;

    @Schema(description = "字典描述", example = "用户账号状态字典")
    private String dictDesc;

    @Schema(description = "字典排序", example = "100")
    private Integer dictSort;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "备注说明", example = "用户状态字典")
    private String remark;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;
}

