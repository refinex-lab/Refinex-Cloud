package cn.refinex.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应 VO
 *
 * @author Michelle.Chung
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "登录响应 VO")
public class LoginVo {

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "访问令牌过期时间（秒）")
    private Long expireIn;

    @Schema(description = "刷新令牌过期时间（秒）")
    private Long refreshExpireIn;

    @Schema(description = "客户端 ID")
    private String clientId;
}
