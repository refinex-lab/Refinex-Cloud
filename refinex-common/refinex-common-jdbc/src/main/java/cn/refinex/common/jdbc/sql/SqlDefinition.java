package cn.refinex.common.jdbc.sql;

import lombok.*;

/**
 * SQL 定义类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class SqlDefinition {

    /**
     * SQL 名称
     */
    private String name;

    /**
     * SQL 语句
     */
    private String sql;

    /**
     * 描述
     */
    private String description;
}
