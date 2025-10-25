package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.entity.sys.SysDictType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典类型数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysDictTypeRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据 ID 查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型
     */
    public SysDictType selectById(Long id) {
        String sql = """
                SELECT *
                FROM sys_dict_type
                WHERE id = :id AND deleted = 0
                """;
        Map<String, Object> params = Map.of("id", id);
        try {
            return jdbcManager.queryObject(sql, params, true, SysDictType.class);
        } catch (Exception e) {
            log.error("根据ID查询字典类型失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据编码查询字典类型
     *
     * @param dictCode 字典类型编码
     * @return 字典类型
     */
    public SysDictType selectByCode(String dictCode) {
        String sql = """
                SELECT *
                FROM sys_dict_type
                WHERE dict_code = :dictCode AND deleted = 0
                """;

        Map<String, Object> params = Map.of("dictCode", dictCode);

        try {
            return jdbcManager.queryObject(sql, params, true, SysDictType.class);
        } catch (Exception e) {
            log.error("根据编码查询字典类型失败，dictCode: {}", dictCode, e);
            return null;
        }
    }

    /**
     * 检查字典类型编码是否存在（可排除某个ID）
     *
     * @param dictCode  字典类型编码
     * @param excludeId 排除的字典类型ID
     * @return 是否存在
     */
    public boolean existsByCode(String dictCode, Long excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM sys_dict_type WHERE dict_code = :dictCode AND deleted = 0");

        Map<String, Object> params = new HashMap<>();
        params.put("dictCode", dictCode);

        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer count = jdbcManager.queryInt(sql.toString(), params);
        return count != null && count > 0;
    }

    /**
     * 新增字典类型
     *
     * @param dictType 字典类型
     * @return 影响行数
     */
    public int insert(SysDictType dictType) {
        String sql = """
                INSERT INTO sys_dict_type (
                    id, dict_code, dict_name, dict_desc, create_by, create_time, update_by, update_time,
                    deleted, version, remark, status
                ) VALUES (
                    :id, :dictCode, :dictName, :dictDesc, :createBy, :createTime, :updateBy, :updateTime,
                    :deleted, :version, :remark, :status
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(dictType, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 更新字典类型
     *
     * @param dictType 字典类型
     * @return 影响行数
     */
    public int update(SysDictType dictType) {
        String sql = """
                UPDATE sys_dict_type SET
                    dict_code = :dictCode,
                    dict_name = :dictName,
                    dict_desc = :dictDesc,
                    update_by = :updateBy,
                    update_time = :updateTime,
                    version = :version,
                    remark = :remark,
                    status = :status
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(dictType, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除字典类型
     *
     * @param id         字典类型ID
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int softDeleteById(Long id, Long operatorId) {
        String sql = """
                UPDATE sys_dict_type SET
                    deleted = 1,
                    update_by = :updateBy,
                    update_time = :updateTime
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("updateBy", operatorId);
        params.put("updateTime", LocalDateTime.now());

        return jdbcManager.update(sql, params);
    }

    /**
     * 分页查询字典类型
     *
     * @param dictCode    字典类型编码
     * @param dictName    字典类型名称
     * @param status      状态
     * @param pageRequest 分页请求
     * @return 字典类型分页结果
     */
    public PageResult<SysDictType> pageQuery(String dictCode, String dictName, Integer status, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM sys_dict_type
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        if (dictCode != null && !dictCode.isEmpty()) {
            sql.append(" AND dict_code LIKE :dictCode");
            params.put("dictCode", "%" + dictCode + "%");
        }
        if (dictName != null && !dictName.isEmpty()) {
            sql.append(" AND dict_name LIKE :dictName");
            params.put("dictName", "%" + dictName + "%");
        }
        if (status != null) {
            sql.append(" AND status = :status");
            params.put("status", status);
        }
        sql.append(" ORDER BY id DESC");

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, SysDictType.class);
    }

    /**
     * 查询所有启用的字典类型
     *
     * @return 启用的字典类型列表
     */
    public List<SysDictType> selectEnabledList() {
        String sql = """
                SELECT *
                FROM sys_dict_type
                WHERE deleted = 0 AND status = 0
                ORDER BY id DESC
                """;

        try {
            return jdbcManager.queryList(sql, Map.of(), true, SysDictType.class);
        } catch (Exception e) {
            log.error("查询启用的字典类型列表失败", e);
            return List.of();
        }
    }
}