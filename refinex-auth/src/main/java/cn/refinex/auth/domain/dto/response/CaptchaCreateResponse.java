package cn.refinex.auth.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码生成响应
 *
 * @author Refinex
 * @since 2025-10-05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "验证码生成响应")
public class CaptchaCreateResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "验证码唯一标识（UUID）", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String uuid;

    @Schema(description = "验证码图片（Base64 编码）")
    private String image;

    @Schema(description = "验证码过期时间（秒）", defaultValue = "300", example = "60")
    private Integer expireSeconds;
}

