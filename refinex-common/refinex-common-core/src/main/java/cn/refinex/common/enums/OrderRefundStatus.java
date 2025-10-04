package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单退款状态
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum OrderRefundStatus {

    PENDING_AUDIT(0, "待审核"),
    AUDIT_PASSED(1, "审核通过"),
    REFUNDING(2, "退款中"),
    REFUND_SUCCESS(3, "退款成功"),
    REFUND_FAILED(4, "退款失败"),
    REFUND_REJECTED(5, "已拒绝"),
    ;

    private final Integer value;
    private final String description;
}
