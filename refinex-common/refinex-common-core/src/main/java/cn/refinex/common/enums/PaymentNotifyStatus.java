package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付通知处理状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentNotifyStatus {

    PENDING("pending", "待处理"),
    SUCCESS("success", "处理成功"),
    FAILED("failed", "处理失败"),
    ;

    private final String value;
    private final String description;
}
