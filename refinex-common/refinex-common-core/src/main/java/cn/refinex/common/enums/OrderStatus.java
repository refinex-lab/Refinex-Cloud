package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum OrderStatus {

    WAIT_PAY(0, "待支付"),
    PAID(1, "已支付"),
    CANCELED(2, "已取消"),
    CLOSED(3, "已关闭"),
    REFUNDED(4, "已退款"),
    ;

    private final Integer value;
    private final String description;
}
