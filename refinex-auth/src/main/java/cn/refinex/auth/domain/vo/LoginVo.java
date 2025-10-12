package cn.refinex.auth.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("access_token")
    private String accessToken;

    @Schema(description = "刷新令牌")
    @JsonProperty("refresh_token")
    private String refreshToken;

    @Schema(description = "访问令牌过期时间（秒）")
    @JsonProperty("expire_in")
    private Long expireIn;

    @Schema(description = "刷新令牌过期时间（秒）")
    @JsonProperty("refresh_expire_in")
    private Long refreshExpireIn;

    @Schema(description = "客户端 ID")
    @JsonProperty("client_id")
    private String clientId;
}
