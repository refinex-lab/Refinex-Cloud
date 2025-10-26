package cn.refinex.common.jdbc.service;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.properties.RefinexBizProperties;
import cn.refinex.common.utils.security.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * 敏感数据加密/解密服务
 * <p>
 * 提供统一的敏感数据加密存储能力，使用 AES-256-GCM 加密算法
 * 所有敏感数据存储到 sys_sensitive 表中
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SensitiveDataService {

    private final JdbcTemplateManager jdbcManager;
    private final RefinexBizProperties bizProperties;

    /**
     * 加密算法标识
     */
    private static final String ALGORITHM = "AES256-GCM";

    /**
     * 加密并存储敏感数据
     *
     * @param tableName  来源表名（如 file_storage_config）
     * @param rowGuid    被加密记录的唯一标识（业务表主键或 GUID）
     * @param fieldCode  字段代码（如 access_key、secret_key）
     * @param plainValue 明文值
     */
    public void encryptAndStore(String tableName, String rowGuid, String fieldCode, String plainValue) {
        Assert.hasText(tableName, "tableName 不能为空");
        Assert.hasText(rowGuid, "rowGuid 不能为空");
        Assert.hasText(fieldCode, "fieldCode 不能为空");

        if (!StringUtils.hasText(plainValue)) {
            log.warn("明文值为空，跳过加密存储，table={}, rowGuid={}, field={}", tableName, rowGuid, fieldCode);
            return;
        }

        try {
            // 1. 使用 AES-256-GCM 加密
            String encryptedValue = encryptValue(plainValue);

            // 2. 插入到 sys_sensitive 表
            String sql = """
                    INSERT INTO sys_sensitive (
                        row_guid, table_name, field_code, encrypted_value,
                        encryption_algorithm, create_time, update_time
                    ) VALUES (
                        :rowGuid, :tableName, :fieldCode, :encryptedValue,
                        :algorithm, NOW(), NOW()
                    )
                    """;

            Map<String, Object> params = Map.of(
                    "rowGuid", rowGuid,
                    "tableName", tableName,
                    "fieldCode", fieldCode,
                    "encryptedValue", encryptedValue,
                    "algorithm", ALGORITHM
            );

            jdbcManager.insert(sql, params);

            log.debug("敏感数据已加密存储，table={}, field={}, rowGuid={}", tableName, fieldCode, rowGuid);

        } catch (Exception e) {
            log.error("敏感数据加密存储失败，table={}, field={}", tableName, fieldCode, e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "敏感数据加密存储失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询并解密敏感数据
     *
     * @param rowGuid sys_sensitive 表的 row_guid
     * @return 明文值（如果不存在返回 null）
     */
    public String queryAndDecrypt(String tableName, String rowGuid, String fieldCode) {
        Assert.hasText(tableName, "tableName 不能为空");
        Assert.hasText(rowGuid, "rowGuid 不能为空");
        Assert.hasText(fieldCode, "fieldCode 不能为空");

        try {
            // 1. 从 sys_sensitive 表查询加密值
            String sql = """
                    SELECT encrypted_value FROM sys_sensitive
                    WHERE table_name = :tableName
                      AND row_guid = :rowGuid
                      AND field_code = :fieldCode
                    """;

            Map<String, Object> params = Map.of(
                    "tableName", tableName,
                    "rowGuid", rowGuid,
                    "fieldCode", fieldCode
            );
            String encryptedValue = jdbcManager.queryString(sql, params, false);

            if (!StringUtils.hasText(encryptedValue)) {
                log.warn("敏感数据不存在，rowGuid={}", rowGuid);
                return null;
            }

            // 2. 解密
            return decryptValue(encryptedValue);

        } catch (Exception e) {
            log.error("敏感数据查询解密失败，rowGuid={}", rowGuid, e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "敏感数据查询解密失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量查询并解密敏感数据
     *
     * @param rowGuids sys_sensitive 表的 row_guid 列表
     * @return Map<rowGuid, 明文值>
     */
    public Map<String, String> batchQueryAndDecrypt(List<String> rowGuids) {
        if (CollectionUtils.isEmpty(rowGuids)) {
            return Collections.emptyMap();
        }

        try {
            // 1. 批量查询
            String sql = """
                    SELECT row_guid, encrypted_value FROM sys_sensitive
                    WHERE row_guid IN (:rowGuids)
                    """;

            Map<String, Object> params = Map.of("rowGuids", rowGuids);
            List<Map<String, Object>> results = jdbcManager.queryList(sql, params);

            // 2. 批量解密
            Map<String, String> decryptedMap = new HashMap<>();
            for (Map<String, Object> row : results) {
                String rowGuid = (String) row.get("row_guid");
                String encryptedValue = (String) row.get("encrypted_value");

                if (StringUtils.hasText(encryptedValue)) {
                    String plainValue = decryptValue(encryptedValue);
                    decryptedMap.put(rowGuid, plainValue);
                }
            }

            log.debug("批量解密敏感数据完成，查询数量={}, 解密数量={}", rowGuids.size(), decryptedMap.size());
            return decryptedMap;

        } catch (Exception e) {
            log.error("批量查询解密敏感数据失败", e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "批量查询解密敏感数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新敏感数据
     *
     * @param rowGuid    sys_sensitive 表的 row_guid
     * @param plainValue 新的明文值
     */
    public void updateSensitiveData(String rowGuid, String plainValue) {
        Assert.hasText(rowGuid, "rowGuid 不能为空");
        Assert.hasText(plainValue, "plainValue 不能为空");

        try {
            // 1. 加密新值
            String encryptedValue = encryptValue(plainValue);

            // 2. 更新数据库
            String sql = """
                    UPDATE sys_sensitive
                    SET encrypted_value = :encryptedValue,
                        update_time = NOW()
                    WHERE row_guid = :rowGuid
                    """;

            Map<String, Object> params = Map.of(
                    "encryptedValue", encryptedValue,
                    "rowGuid", rowGuid
            );

            int updated = jdbcManager.update(sql, params);

            if (updated == 0) {
                log.warn("敏感数据不存在，无法更新，rowGuid={}", rowGuid);
                throw new SystemException(HttpStatusCode.NOT_FOUND, "敏感数据不存在，rowGuid=" + rowGuid);
            }

            log.debug("敏感数据已更新，rowGuid={}", rowGuid);

        } catch (Exception e) {
            log.error("更新敏感数据失败，rowGuid={}", rowGuid, e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "更新敏感数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除敏感数据
     *
     * @param rowGuid sys_sensitive 表的 row_guid
     */
    public void deleteSensitiveData(String rowGuid) {
        if (!StringUtils.hasText(rowGuid)) {
            log.warn("rowGuid 为空，跳过删除");
            return;
        }

        try {
            String sql = """
                    DELETE FROM sys_sensitive
                    WHERE row_guid = :rowGuid
                    """;

            Map<String, Object> params = Map.of("rowGuid", rowGuid);
            int deleted = jdbcManager.delete(sql, params);

            log.debug("敏感数据已删除，rowGuid={}, 删除数量={}", rowGuid, deleted);

        } catch (Exception e) {
            log.error("删除敏感数据失败，rowGuid={}", rowGuid, e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "删除敏感数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量删除敏感数据
     *
     * @param rowGuids sys_sensitive 表的 row_guid 列表
     */
    public void batchDeleteSensitiveData(List<String> rowGuids) {
        if (CollectionUtils.isEmpty(rowGuids)) {
            return;
        }

        try {
            String sql = """
                    DELETE FROM sys_sensitive
                    WHERE row_guid IN (:rowGuids)
                    """;

            Map<String, Object> params = Map.of("rowGuids", rowGuids);
            int deleted = jdbcManager.delete(sql, params);

            log.debug("批量删除敏感数据完成，删除数量={}", deleted);

        } catch (Exception e) {
            log.error("批量删除敏感数据失败", e);
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, "批量删除敏感数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 AES-256-GCM 加密
     *
     * @param plainValue 明文
     * @return Base64 编码的密文（包含 IV）
     */
    public String encryptValue(String plainValue) throws GeneralSecurityException {
        byte[] keyBytes = getAesKeyBytes();
        byte[] plainBytes = plainValue.getBytes(StandardCharsets.UTF_8);

        // 使用 CryptoUtils 的 AES-GCM 加密（无 AAD）
        return CryptoUtils.aesGcmEncryptToBase64(plainBytes, keyBytes, null);
    }

    /**
     * 使用 AES-256-GCM 解密
     *
     * @param encryptedValue Base64 编码的密文（包含 IV）
     * @return 明文
     */
    public String decryptValue(String encryptedValue) throws GeneralSecurityException {
        byte[] keyBytes = getAesKeyBytes();

        // 使用 CryptoUtils 的 AES-GCM 解密（无 AAD）
        byte[] plainBytes = CryptoUtils.aesGcmDecryptFromBase64(encryptedValue, keyBytes, null);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 获取 AES-256 密钥字节数组
     *
     * @return 32 字节密钥
     */
    private byte[] getAesKeyBytes() {
        String keyBase64 = bizProperties.getDbSensitiveDataKey();

        if (!StringUtils.hasText(keyBase64)) {
            throw new IllegalStateException("数据库敏感数据加密密钥未配置，请在配置文件中设置 refinex.biz.db-sensitive-data-key");
        }

        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);

            if (keyBytes.length != 32) {
                throw new IllegalStateException("AES-256 密钥必须是 32 字节，当前长度: " + keyBytes.length);
            }

            return keyBytes;

        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("数据库敏感数据加密密钥格式错误，必须是 Base64 编码的 32 字节密钥", e);
        }
    }
}

