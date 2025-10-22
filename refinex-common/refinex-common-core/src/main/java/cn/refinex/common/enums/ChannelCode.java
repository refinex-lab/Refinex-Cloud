package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 渠道编码
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ChannelCode {

    WXPAY("WXPAY", "微信支付"),
    ALIPAY("ALIPAY", "支付宝支付"),
    BALANCE("BALANCE", "余额支付"),
    ;

    private final String value;
    private final String description;
}
