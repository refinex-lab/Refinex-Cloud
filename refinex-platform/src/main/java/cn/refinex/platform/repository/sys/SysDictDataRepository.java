package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.entity.sys.SysDictData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典数据数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysDictDataRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据 ID 查询字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据
     */
    public SysDictData selectById(Long id) {
        String sql = """
                SELECT *
                FROM sys_dict_data
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = Map.of("id", id);

        try {
            return jdbcManager.queryObject(sql, params, true, SysDictData.class);
        } catch (Exception e) {
            log.error("根据ID查询字典数据失败，id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据类型ID和字典值查询
     *
     * @param dictTypeId 字典类型ID
     * @param dictValue  字典值
     * @return 字典数据
     */
    public SysDictData selectByTypeIdAndValue(Long dictTypeId, String dictValue) {
        String sql = """
                SELECT *
                FROM sys_dict_data
                WHERE dict_type_id = :dictTypeId AND dict_value = :dictValue AND deleted = 0
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("dictTypeId", dictTypeId);
        params.put("dictValue", dictValue);

        try {
            return jdbcManager.queryObject(sql, params, true, SysDictData.class);
        } catch (Exception e) {
            log.error("根据类型和值查询字典数据失败，dictTypeId: {}, dictValue: {}", dictTypeId, dictValue, e);
            return null;
        }
    }

    /**
     * 根据类型ID查询数据列表（按排序）
     *
     * @param dictTypeId 字典类型ID
     * @return 字典数据列表
     */
    public List<SysDictData> selectListByTypeId(Long dictTypeId) {
        String sql = """
                SELECT *
                FROM sys_dict_data
                WHERE dict_type_id = :dictTypeId AND deleted = 0
                ORDER BY dict_sort ASC, id ASC
                """;

        Map<String, Object> params = Map.of("dictTypeId", dictTypeId);

        try {
            return jdbcManager.queryList(sql, params, true, SysDictData.class);
        } catch (Exception e) {
            log.error("根据类型ID查询字典数据列表失败，dictTypeId: {}", dictTypeId, e);
            return List.of();
        }
    }

    /**
     * 通过字典类型编码查询数据列表
     *
     * @param dictCode 字典类型编码
     * @return 字典数据列表
     */
    public List<SysDictData> selectListByTypeCode(String dictCode) {
        String sql = """
                SELECT *
                FROM sys_dict_data d
                INNER JOIN sys_dict_type t ON d.dict_type_id = t.id
                WHERE t.dict_code = :dictCode AND t.deleted = 0 AND d.deleted = 0
                ORDER BY d.dict_sort ASC, d.id ASC
                """;

        Map<String, Object> params = Map.of("dictCode", dictCode);

        try {
            return jdbcManager.queryList(sql, params, true, SysDictData.class);
        } catch (Exception e) {
            log.error("通过字典类型编码查询字典数据列表失败，dictCode: {}", dictCode, e);
            return List.of();
        }
    }

    /**
     * 检查在指定类型下字典标签是否存在（可排除某个ID）
     *
     * @param dictTypeId 字典类型ID
     * @param dictLabel  字典标签
     * @param excludeId  排除的ID
     * @return 是否存在
     */
    public boolean existsLabelInType(Long dictTypeId, String dictLabel, Long excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM sys_dict_data WHERE dict_type_id = :dictTypeId AND dict_label = :dictLabel AND deleted = 0");

        Map<String, Object> params = new HashMap<>();
        params.put("dictTypeId", dictTypeId);
        params.put("dictLabel", dictLabel);
        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer count = jdbcManager.queryInt(sql.toString(), params);
        return count != null && count > 0;
    }

    /**
     * 检查在指定类型下字典值是否存在（可排除某个ID）
     *
     * @param dictTypeId 字典类型ID
     * @param dictValue  字典值
     * @param excludeId  排除的ID
     * @return 是否存在
     */
    public boolean existsValueInType(Long dictTypeId, String dictValue, Long excludeId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM sys_dict_data WHERE dict_type_id = :dictTypeId AND dict_value = :dictValue AND deleted = 0");

        Map<String, Object> params = new HashMap<>();
        params.put("dictTypeId", dictTypeId);
        params.put("dictValue", dictValue);
        if (excludeId != null) {
            sql.append(" AND id <> :excludeId");
            params.put("excludeId", excludeId);
        }

        Integer count = jdbcManager.queryInt(sql.toString(), params);
        return count != null && count > 0;
    }

    /**
     * 新增字典数据
     *
     * @param dictData 字典数据
     * @return 影响行数
     */
    public int insert(SysDictData dictData) {
        String sql = """
                INSERT INTO sys_dict_data (
                    id, dict_type_id, dict_label, dict_value, dict_sort, css_class, list_class, is_default,
                    create_by, create_time, update_by, update_time, deleted, version, remark, status
                ) VALUES (
                    :id, :dictTypeId, :dictLabel, :dictValue, :dictSort, :cssClass, :listClass, :isDefault,
                    :createBy, :createTime, :updateBy, :updateTime, :deleted, :version, :remark, :status
                )
                """;

        Map<String, Object> params = BeanConverter.beanToMap(dictData, false, false);
        return jdbcManager.insert(sql, params);
    }

    /**
     * 更新字典数据
     *
     * @param dictData 字典数据
     * @return 影响行数
     */
    public int update(SysDictData dictData) {
        String sql = """
                UPDATE sys_dict_data SET
                    dict_type_id = :dictTypeId,
                    dict_label = :dictLabel,
                    dict_value = :dictValue,
                    dict_sort = :dictSort,
                    css_class = :cssClass,
                    list_class = :listClass,
                    is_default = :isDefault,
                    update_by = :updateBy,
                    update_time = :updateTime,
                    version = :version,
                    remark = :remark,
                    status = :status
                WHERE id = :id AND deleted = 0
                """;

        Map<String, Object> params = BeanConverter.beanToMap(dictData, false, false);
        return jdbcManager.update(sql, params);
    }

    /**
     * 逻辑删除字典数据
     *
     * @param id         字典数据ID
     * @param operatorId 操作人ID
     * @return 影响行数
     */
    public int softDeleteById(Long id, Long operatorId) {
        String sql = """
                UPDATE sys_dict_data SET
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
     * 分页查询字典数据
     *
     * @param dictTypeId  字典类型ID
     * @param dictLabel   字典标签
     * @param dictValue   字典值
     * @param status      状态
     * @param pageRequest 分页请求
     * @return 字典数据分页结果
     */
    public PageResult<SysDictData> pageQuery(Long dictTypeId, String dictLabel, String dictValue, Integer status, PageRequest pageRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT *
                FROM sys_dict_data
                WHERE deleted = 0
                """);

        Map<String, Object> params = new HashMap<>();
        if (dictTypeId != null) {
            sql.append(" AND dict_type_id = :dictTypeId");
            params.put("dictTypeId", dictTypeId);
        }
        if (dictLabel != null && !dictLabel.isEmpty()) {
            sql.append(" AND dict_label LIKE :dictLabel");
            params.put("dictLabel", "%" + dictLabel + "%");
        }
        if (dictValue != null && !dictValue.isEmpty()) {
            sql.append(" AND dict_value LIKE :dictValue");
            params.put("dictValue", "%" + dictValue + "%");
        }
        if (status != null) {
            sql.append(" AND status = :status");
            params.put("status", status);
        }
        sql.append(" ORDER BY dict_sort ASC, id ASC");

        return jdbcManager.queryPage(sql.toString(), params, pageRequest, SysDictData.class);
    }
}