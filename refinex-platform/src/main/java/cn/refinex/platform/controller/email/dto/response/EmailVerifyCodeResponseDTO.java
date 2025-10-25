package cn.refinex.platform.controller.email.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 验证码发送结果 DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerifyCodeResponseDTO {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 验证码类型
     */
    private String codeType;

    /**
     * 验证码（仅测试环境返回）
     */
    private String verifyCode;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 创建成功结果
     *
     * @param email      邮箱地址
     * @param codeType   验证码类型
     * @param verifyCode 验证码
     * @param expireTime 过期时间
     * @return VerifyCodeResult
     */
    public static EmailVerifyCodeResponseDTO success(String email, String codeType, String verifyCode, LocalDateTime expireTime) {
        return EmailVerifyCodeResponseDTO.builder()
                .success(true)
                .email(email)
                .codeType(codeType)
                .verifyCode(verifyCode)
                .expireTime(expireTime)
                .build();
    }

    /**
     * 创建失败结果
     *
     * @param email        邮箱地址
     * @param codeType     验证码类型
     * @param errorMessage 错误信息
     * @return VerifyCodeResult
     */
    public static EmailVerifyCodeResponseDTO failure(String email, String codeType, String errorMessage) {
        return EmailVerifyCodeResponseDTO.builder()
                .success(false)
                .email(email)
                .codeType(codeType)
                .errorMessage(errorMessage)
                .build();
    }
}

