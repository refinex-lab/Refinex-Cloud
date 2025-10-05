package cn.refinex.common.file.repository;

import cn.refinex.common.file.domain.entity.FileStorageConfig;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 存储配置 Repository
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class FileStorageConfigRepository {

    private final JdbcTemplateManager jdbcManager;

    /**
     * 根据存储类型查询启用的配置（按优先级排序）
     *
     * @param storageType 存储类型
     * @return 配置列表
     */
    public List<FileStorageConfig> findByStorageType(String storageType) {
        String sql = """
            SELECT * FROM file_storage_config
            WHERE storage_type = :storageType AND is_enabled = 1 AND deleted = 0
            ORDER BY priority DESC, id ASC
            """;

        Map<String, Object> params = Map.of("storageType", storageType);
        return jdbcManager.queryList(sql, params, FileStorageConfig.class);
    }

    /**
     * 查询默认配置
     *
     * @param storageType 存储类型
     * @return 默认配置
     */
    public FileStorageConfig findDefaultConfig(String storageType) {
        String sql = """
            SELECT * FROM file_storage_config
            WHERE storage_type = :storageType AND is_default = 1 AND is_enabled = 1 AND deleted = 0
            LIMIT 1
            """;

        Map<String, Object> params = Map.of("storageType", storageType);
        return jdbcManager.queryObject(sql, params, FileStorageConfig.class);
    }

    /**
     * 根据配置 ID 查询
     *
     * @param configId 配置 ID
     * @return 配置
     */
    public FileStorageConfig findById(Long configId) {
        String sql = """
            SELECT * FROM file_storage_config
            WHERE id = :configId AND deleted = 0
            """;

        Map<String, Object> params = Map.of("configId", configId);
        return jdbcManager.queryObject(sql, params, FileStorageConfig.class);
    }
}

