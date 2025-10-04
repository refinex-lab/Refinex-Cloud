package cn.refinex.api.auth.domain.dto;

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
public class CaptchaGenerateResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码唯一标识（UUID）
     */
    private String uuid;

    /**
     * 验证码图片（Base64 编码）
     */
    private String image;

    /**
     * 验证码过期时间（秒）
     */
    private Integer expireSeconds;
}

