package cn.refinex.common.jdbc.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据库类型
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum DatabaseType {

    /**
     * MySQL 数据库
     */
    MYSQL("mysql"),

    /**
     * PostgreSQL 数据库
     */
    POSTGRESQL("postgresql"),

    /**
     * Oracle 数据库
     */
    ORACLE("oracle"),

    ;

    private final String value;
}
