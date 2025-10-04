package cn.refinex.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 登录响应 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@Schema(description = "登录响应")
public class LoginResponse {

    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌（JWT 格式）")
    private String token;

    /**
     * Token 类型（固定为 Bearer）
     */
    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    /**
     * Token 有效期（秒）
     */
    @Schema(description = "Token 有效期（秒）", example = "7200")
    private Long expiresIn;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private UserInfo userInfo;

    /**
     * 用户信息内部类
     */
    @Data
    @Builder
    @Schema(description = "用户信息")
    public static class UserInfo {

        /**
         * 用户 ID
         */
        @Schema(description = "用户 ID", example = "1")
        private Long userId;

        /**
         * 用户名
         */
        @Schema(description = "用户名", example = "admin")
        private String username;

        /**
         * 昵称
         */
        @Schema(description = "昵称", example = "管理员")
        private String nickname;

        /**
         * 头像 URL
         */
        @Schema(description = "头像 URL")
        private String avatar;
    }
}

