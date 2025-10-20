package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.SysConfigQueryRequest;
import cn.refinex.platform.domain.entity.sys.SysConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统配置数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysConfigRepository {

    private final JdbcTemplateManager jdbcManager;

    // ================= Read =================

    /**
     * 根据 ID 查询系统配置
     *
     * @param id 系统配置ID
     * @return 系统配置
     */
    public SysConfig selectById(Long id) {
        String sql = """
                SELECT *
                FROM sys_config
                WHERE id = :id AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("id", id), true, SysConfig.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("查询系统配置失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据配置键查询系统配置
     *
     * @param configKey 系统配置键
     * @return 系统配置
     */
    public SysConfig selectByKey(String configKey) {
        String sql = """
                SELECT *
                FROM sys_config
                WHERE config_key = :configKey AND deleted = 0
                """;

        try {
            return jdbcManager.queryObject(sql, Map.of("configKey", configKey), true, SysConfig.class);
        } catch (EmptyResultDataAccessException e) {
            log.error("根据配置键查询失败，configKey: {}", configKey, e);
            return null;
        }
    }

    /**
     * 根据配置分组查询系统配置
     *
     * @param configGroup 系统配置分组
     * @return 系统配置列表
     */
    public List<SysConfig> listByGroup(String configGroup) {
        String sql = """
                SELECT *
                FROM sys_config
                WHERE config_group = :configGroup AND deleted = 0
                ORDER BY sort ASC, id DESC
                """;

        try {
            return jdbcManager.queryList(sql, Map.of("configGroup", configGroup), true, SysConfig.class);
        } catch (Exception e) {
            log.error("根据分组查询配置失败，group: {}", configGroup, e);
            return List.of();
        }
    }

    /**
     * 分页查询系统配置
     *
     * @param query       查询参数
     * @param pageRequest 分页参数
     * @return 系统配置分页结果
     */
    public PageResult<SysConfig> pageQuery(SysConfigQueryRequest query, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM sys_config
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();

        if (query != null) {
            if (query.getConfigKey() != null && !query.getConfigKey().isEmpty()) {
                sql.append(" AND config_key LIKE :configKey");
                params.put("configKey", "%" + query.getConfigKey() + "%");
            }
            if (query.getConfigGroup() != null && !query.getConfigGroup().isEmpty()) {
                sql.append(" AND config_group LIKE :configGroup");
                params.put("configGroup", "%" + query.getConfigGroup() + "%");
            }
            if (query.getConfigType() != null && !query.getConfigType().isEmpty()) {
                sql.append(" AND config_type = :configType");
                params.put("configType", query.getConfigType());
            }
            if (query.getIsSensitive() != null) {
                sql.append(" AND is_sensitive = :isSensitive");
                params.put("isSensitive", query.getIsSensitive());
            }
            if (query.getIsFrontend() != null) {
                sql.append(" AND is_frontend = :isFrontend");
                params.put("isFrontend", query.getIsFrontend());
            }
        }

        sql.append(" ORDER BY sort ASC, id DESC");
        return jdbcManager.queryPage(sql.toString(), params, pageRequest, SysConfig.class);
    }

    /**
     * 检查配置键是否存在（排除指定ID）
     *
     * @param configKey 系统配置键
     * @param excludeId 排除的系统配置ID
     * @return 是否存在
     */
    public boolean existsKey(String configKey, Long excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM sys_config WHERE config_key = :configKey AND deleted = 0");

        Map<String, Object> params = new HashMap<>();
        params.put("configKey", configKey);
        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer c = jdbcManager.queryInt(sql.toString(), params);
        return c != null && c > 0;
    }

    // ================= Write (tx-managed) =================

    /**
     * 插入系统配置
     *
     * @param tx     事务管理器
     * @param config 系统配置
     * @return 系统配置ID
     */
    public long insert(JdbcTemplateManager tx, SysConfig config) {
        String sql = """
                INSERT INTO sys_config (
                    config_key, config_value, config_type, config_group, config_label, config_desc,
                    is_sensitive, is_frontend, create_by, create_time, update_by, update_time, remark, sort
                ) VALUES (
                    :configKey, :configValue, :configType, :configGroup, :configLabel, :configDesc,
                    :isSensitive, :isFrontend, :createBy, :createTime, :updateBy, :updateTime, :remark, :sort
                )
                """;

        BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(config);
        return tx.insertAndGetKey(sql, params);
    }

    /**
     * 根据 ID 更新系统配置
     *
     * @param tx     事务管理器
     * @param config 系统配置
     * @return 影响行数
     */
    public int updateById(JdbcTemplateManager tx, SysConfig config) {
        String sql = """
                UPDATE sys_config SET
                    config_key    = :configKey,
                    config_value  = :configValue,
                    config_type   = :configType,
                    config_group  = :configGroup,
                    config_label  = :configLabel,
                    config_desc   = :configDesc,
                    is_sensitive  = :isSensitive,
                    is_frontend   = :isFrontend,
                    update_by     = :updateBy,
                    update_time   = :updateTime,
                    version       = :version,
                    remark        = :remark,
                    sort          = :sort
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(config, false, false);
        return tx.update(sql, params);
    }

    /**
     * 根据 ID 软删除系统配置
     *
     * @param tx       事务管理器
     * @param id       系统配置 ID
     * @param operatorId 操作人 ID
     * @return 影响行数
     */
    public int softDeleteById(JdbcTemplateManager tx, Long id, Long operatorId) {
        String sql = """
                UPDATE sys_config SET
                    deleted = 1,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }

    /**
     * 根据 ID 更新系统配置状态（前端展示）
     *
     * @param tx       事务管理器
     * @param id       系统配置 ID
     * @param status   状态值（0：不展示，1：展示）
     * @param operatorId 操作人 ID
     * @return 影响行数
     */
    public int updateStatus(JdbcTemplateManager tx, Long id, Integer status, Long operatorId) {
        String sql = """
                UPDATE sys_config SET
                    is_frontend = :status,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return tx.update(sql, params);
    }
}


