package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包变更类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum WalletChangeType {

    RECHARGE("RECHARGE", "充值"),
    CONSUME("CONSUME", "消费"),
    REFUND("REFUND", "退款"),
    GIFT("GIFT", "奖励"),
    FREEZE("FREEZE", "冻结"),
    UNFREEZE("UNFREEZE", "解冻"),
    ;

    private final String value;
    private final String description;
}
