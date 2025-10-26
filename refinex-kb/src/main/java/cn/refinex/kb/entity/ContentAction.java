package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户行为实体类
 * <p>
 * 对应数据库表：content_action
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户行为实体")
public class ContentAction {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1001")
    private Long userId;

    @Schema(description = "文档ID", example = "2000")
    private Long documentId;

    @Schema(description = "行为类型：VIEW,LIKE,COLLECT,SHARE", example = "VIEW")
    private String actionType;

    @Schema(description = "行为值，如浏览时长秒数", example = "300")
    private Integer actionValue;

    @Schema(description = "行为时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actionTime;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

