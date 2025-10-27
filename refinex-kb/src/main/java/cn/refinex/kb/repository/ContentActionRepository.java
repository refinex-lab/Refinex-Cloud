package cn.refinex.kb.repository;

import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.kb.entity.ContentAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为数据访问层
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContentActionRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 插入用户行为记录
     *
     * @param action 行为实体
     * @return 行为ID
     */
    public Long insert(ContentAction action) {
        String sql = """
                INSERT INTO content_action (
                    user_id, document_id, action_type, action_value, action_time, create_time
                ) VALUES (
                    :userId, :documentId, :actionType, :actionValue, :actionTime, :createTime
                )
                """;

        BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(action);
        return jdbcManager.insertAndGetKey(sql, paramSource);
    }

    /**
     * 检查用户是否对文档有指定行为
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param actionType 行为类型
     * @return 是否存在
     */
    public boolean exists(Long userId, Long documentId, String actionType) {
        String sql = """
                SELECT COUNT(*)
                FROM content_action
                WHERE user_id = :userId
                  AND document_id = :documentId
                  AND action_type = :actionType
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("documentId", documentId);
        params.put("actionType", actionType);

        try {
            return jdbcManager.queryInt(sql, params) > 0;
        } catch (Exception e) {
            log.error("检查用户行为是否存在失败，userId: {}, documentId: {}, actionType: {}",
                    userId, documentId, actionType, e);
            return false;
        }
    }

    /**
     * 删除用户行为记录
     *
     * @param userId     用户ID
     * @param documentId 文档ID
     * @param actionType 行为类型
     * @return 影响行数
     */
    public int delete(Long userId, Long documentId, String actionType) {
        String sql = """
                DELETE FROM content_action
                WHERE user_id = :userId
                  AND document_id = :documentId
                  AND action_type = :actionType
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("documentId", documentId);
        params.put("actionType", actionType);

        try {
            return jdbcManager.update(sql, params);
        } catch (Exception e) {
            log.error("删除用户行为记录失败，userId: {}, documentId: {}, actionType: {}",
                    userId, documentId, actionType, e);
            return 0;
        }
    }

    /**
     * 更新或插入行为记录（用于浏览记录等）
     *
     * @param userId      用户ID
     * @param documentId  文档ID
     * @param actionType  行为类型
     * @param actionValue 行为值
     * @return 影响行数
     */
    public int insertOrUpdate(Long userId, Long documentId, String actionType, Integer actionValue) {
        // 先尝试更新
        String updateSql = """
                UPDATE content_action
                SET action_value = :actionValue,
                    action_time  = :actionTime
                WHERE user_id = :userId
                  AND document_id = :documentId
                  AND action_type = :actionType
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("documentId", documentId);
        params.put("actionType", actionType);
        params.put("actionValue", actionValue);
        params.put("actionTime", LocalDateTime.now());

        try {
            int updated = jdbcManager.update(updateSql, params);
            if (updated > 0) {
                return updated;
            }

            // 如果更新失败，说明记录不存在，执行插入
            ContentAction action = new ContentAction();
            action.setUserId(userId);
            action.setDocumentId(documentId);
            action.setActionType(actionType);
            action.setActionValue(actionValue);
            action.setActionTime(LocalDateTime.now());
            action.setCreateTime(LocalDateTime.now());

            insert(action);
            return 1;
        } catch (Exception e) {
            log.error("更新或插入用户行为记录失败，userId: {}, documentId: {}, actionType: {}",
                    userId, documentId, actionType, e);
            return 0;
        }
    }

    /**
     * 批量检查用户是否对文档列表有指定行为
     *
     * @param userId      用户ID
     * @param documentIds 文档ID列表
     * @param actionType  行为类型
     * @return Map<文档ID, 是否有行为>
     */
    public Map<Long, Boolean> batchCheckExists(Long userId, List<Long> documentIds, String actionType) {
        if (documentIds == null || documentIds.isEmpty()) {
            return Map.of();
        }

        String sql = """
                SELECT document_id
                FROM content_action
                WHERE user_id = :userId
                  AND document_id IN (:documentIds)
                  AND action_type = :actionType
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("documentIds", documentIds);
        params.put("actionType", actionType);

        try {
            List<Long> existsDocumentIds = jdbcManager.queryList(sql, params, Long.class);

            Map<Long, Boolean> resultMap = new HashMap<>();
            for (Long documentId : documentIds) {
                resultMap.put(documentId, existsDocumentIds.contains(documentId));
            }

            return resultMap;
        } catch (Exception e) {
            log.error("批量检查用户行为失败，userId: {}, actionType: {}", userId, actionType, e);
            return Map.of();
        }
    }
}

