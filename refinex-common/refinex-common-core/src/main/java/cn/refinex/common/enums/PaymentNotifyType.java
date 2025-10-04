package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付通知类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum PaymentNotifyType {

    PAY_SUCCESS("PAY_SUCCESS", "支付成功"),
    REFUND_SUCCESS("REFUND_SUCCESS", "退款成功"),
    ;

    private final String value;
    private final String description;
}
