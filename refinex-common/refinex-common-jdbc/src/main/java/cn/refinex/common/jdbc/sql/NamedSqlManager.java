package cn.refinex.common.jdbc.sql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 命名 SQL 管理器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class NamedSqlManager {

    /**
     * SQL 定义缓存
     */
    private final Map<String, SqlDefinition> sqlCache = new ConcurrentHashMap<>();

    /**
     * 注册 SQL 定义
     *
     * @param definition SQL 定义
     */
    public void register(SqlDefinition definition) {
        if (Objects.isNull(definition) || Objects.isNull(definition.getName()) || Objects.isNull(definition.getSql())) {
            throw new IllegalArgumentException("SQL 定义不能为空");
        }

        if (sqlCache.containsKey(definition.getName())) {
            log.warn("SQL 定义已存在，将被覆盖: {}", definition.getName());
        }

        sqlCache.put(definition.getName(), definition);
        log.info("注册 SQL 定义: {}", definition.getName());
    }

    /**
     * 批量注册 SQL 定义
     *
     * @param definitions SQL 定义列表
     */
    public void registerAll(Map<String, SqlDefinition> definitions) {
        if (MapUtils.isNotEmpty(definitions)) {
            definitions.values().forEach(this::register);
        }
    }

    /**
     * 获取 SQL 语句
     *
     * @param name SQL 名称
     * @return SQL 语句
     */
    public String getSql(String name) {
        SqlDefinition definition = sqlCache.get(name);
        if (Objects.isNull(definition)) {
            throw new IllegalArgumentException("未找到 SQL 定义: " + name);
        }
        return definition.getSql();
    }

    /**
     * 检查 SQL 是否存在
     *
     * @param name SQL 名称
     * @return 是否存在
     */
    public boolean contains(String name) {
        return sqlCache.containsKey(name);
    }

    /**
     * 移除 SQL 定义
     *
     * @param name SQL 名称
     */
    public void remove(String name) {
        sqlCache.remove(name);
        log.info("移除 SQL 定义: {}", name);
    }

    /**
     * 清空所有 SQL 定义
     */
    public void clear() {
        sqlCache.clear();
        log.info("清空所有 SQL 定义");
    }
}
