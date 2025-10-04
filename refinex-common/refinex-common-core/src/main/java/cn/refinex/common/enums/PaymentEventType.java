package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付事件类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum PaymentEventType {

    CREATE_ORDER("CREATE_ORDER", "创建订单"),
    CALL_GATEWAY("CALL_GATEWAY", "调用网关"),
    RECEIVE_NOTIFY("RECEIVE_NOTIFY", "接收通知"),
    PAY_SUCCESS("PAY_SUCCESS", "支付成功"),
    PAY_FAIL("PAY_FAIL", "支付失败"),
    ;

    private final String value;
    private final String description;
}
