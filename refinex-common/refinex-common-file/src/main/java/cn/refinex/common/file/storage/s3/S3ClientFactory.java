package cn.refinex.common.file.storage.s3;

import cn.refinex.common.file.domain.entity.FileStorageConfig;
import cn.refinex.common.file.exception.FileErrorCode;
import cn.refinex.common.file.exception.FileException;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * S3 客户端工厂
 * <p>
 * 根据存储配置创建和缓存 S3Client 实例。
 * 支持 AWS S3、MinIO 等 S3 协议兼容的对象存储。
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3ClientFactory {

    private final SensitiveDataService sensitiveDataService;

    /**
     * S3Client 缓存（key: configId）
     */
    private final Map<Long, S3Client> clientCache = new ConcurrentHashMap<>();

    /**
     * 获取 S3Client
     *
     * @param config 存储配置
     * @return S3Client
     */
    public S3Client getClient(FileStorageConfig config) {
        return clientCache.computeIfAbsent(config.getId(), id -> createClient(config));
    }

    /**
     * 创建 S3Client
     *
     * @param config 存储配置
     * @return S3Client
     */
    private S3Client createClient(FileStorageConfig config) {
        try {
            // 1. 解密 access_key 和 secret_key
            String accessKey = sensitiveDataService.queryAndDecrypt(config.getAccessKey());
            String secretKey = sensitiveDataService.queryAndDecrypt(config.getSecretKey());

            if (accessKey == null || secretKey == null) {
                throw new FileException(FileErrorCode.STORAGE_CONFIG_NOT_FOUND, "存储配置密钥不存在，configId=" + config.getId());
            }

            // 2. 创建凭证
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

            // 3. 构建 S3Client
            var builder = S3Client.builder()
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(config.getRegion()))
                    .serviceConfiguration(S3Configuration.builder()
                            // 启用路径风格访问（兼容 MinIO）
                            .pathStyleAccessEnabled(true)
                            .build());

            // 4. 如果配置了自定义 endpoint，则使用自定义 endpoint
            if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
                builder.endpointOverride(URI.create(config.getEndpoint()));
            }

            S3Client client = builder.build();
            log.info("S3Client 创建成功，configId={}, endpoint={}", config.getId(), config.getEndpoint());
            return client;

        } catch (Exception e) {
            log.error("S3Client 创建失败，configId={}", config.getId(), e);
            throw new FileException(FileErrorCode.STORAGE_INIT_FAILED, "S3Client 创建失败", e);
        }
    }

    /**
     * 清除缓存
     *
     * @param configId 配置 ID
     */
    public void evictCache(Long configId) {
        S3Client client = clientCache.remove(configId);
        if (client != null) {
            client.close();
            log.info("S3Client 缓存已清除，configId={}", configId);
        }
    }

    /**
     * 清除所有缓存
     */
    public void evictAllCache() {
        clientCache.values().forEach(S3Client::close);
        clientCache.clear();
        log.info("所有 S3Client 缓存已清除");
    }
}

