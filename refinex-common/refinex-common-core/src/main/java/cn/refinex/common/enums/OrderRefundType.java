package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单退款类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OrderRefundType {

    FULL("FULL", "全额退款"),
    PARTIAL("PARTIAL", "部分退款"),
    CANCEL("CANCEL", "取消订单"),
    ;

    private final String value;
    private final String description;
}
