package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 渠道类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum ChannelType {

    THIRD_PARTY("third_party", "第三方渠道"),
    WALLET("wallet", "钱包渠道"),
    ;

    private final String value;
    private final String description;
}
