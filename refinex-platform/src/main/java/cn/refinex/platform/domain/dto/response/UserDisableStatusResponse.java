package cn.refinex.platform.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 封禁状态响应
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "封禁状态响应")
public class UserDisableStatusResponse {

    /**
     * 用户 ID
     */
    @Schema(description = "用户 ID")
    private Long userId;

    /**
     * 是否被封禁
     */
    @Schema(description = "是否被封禁")
    private Boolean disabled;

    /**
     * 剩余封禁时间（秒）
     * <p>
     * -1=永久封禁，-2=未被封禁
     * </p>
     */
    @Schema(description = "剩余封禁时间（秒），-1=永久封禁，-2=未被封禁")
    private Long remainingTime;

    /**
     * 封禁类型
     * <p>
     * "全局" 或 具体服务名称
     * </p>
     */
    @Schema(description = "封禁类型", example = "全局")
    private String disableType;
}

