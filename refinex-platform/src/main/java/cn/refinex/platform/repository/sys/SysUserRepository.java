package cn.refinex.platform.repository.sys;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.platform.domain.entity.sys.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 用户数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据用户 ID 查询用户信息
     *
     * @param userId 用户 ID
     * @return 用户实体，不存在返回 null
     */
    public SysUser selectById(Long userId) {
        String sql = """
                SELECT id, username, email, nickname
                FROM sys_user
                WHERE id = :userId AND deleted = 0
                """;

        Map<String, Object> params = Map.of("userId", userId);

        try {
            return jdbcManager.queryObject(sql, params, true, SysUser.class);
        } catch (Exception e) {
            log.error("根据用户 ID 查询用户失败，userId: {}", userId, e);
            return null;
        }
    }
}

