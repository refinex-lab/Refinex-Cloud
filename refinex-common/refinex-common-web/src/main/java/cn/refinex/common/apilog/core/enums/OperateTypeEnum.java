package cn.refinex.common.apilog.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum OperateTypeEnum {

    GET("GET"),
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    EXPORT("EXPORT"),
    IMPORT("IMPORT"),
    OTHER("OTHER")
    ;

    private final String value;
}
