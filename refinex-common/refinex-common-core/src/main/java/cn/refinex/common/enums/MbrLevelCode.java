package cn.refinex.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员等级编码
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum MbrLevelCode {

    FREE("FREE", "免费"),
    MONTHLY("MONTHLY", "月度"),
    YEARLY("YEARLY", "年度"),
    LIFETIME("LIFETIME", "终身"),
    ;

    private final String value;
    private final String description;
}
