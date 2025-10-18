package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 系统角色仓库接口
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysRoleRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据角色ID检查角色是否存在
     *
     * @param roleId 角色ID
     * @return 如果角色存在则返回true，否则返回false
     */
    public boolean checkRoleExistsById(Long roleId) {
        String sql = "SELECT COUNT(*) FROM sys_role WHERE id = :roleId";
        Map<String, Object> params = Map.of("roleId", roleId);
        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查角色是否存在失败", e);
            return false;
        }
    }
}
