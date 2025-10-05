package cn.refinex.common.file.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StorageType {

    /**
     * AWS S3
     */
    S3("S3", "AWS S3"),

    /**
     * 阿里云 OSS
     */
    OSS("OSS", "阿里云 OSS"),

    /**
     * 腾讯云 COS
     */
    COS("COS", "腾讯云 COS"),

    /**
     * 七牛云 Kodo
     */
    KODO("KODO", "七牛云 Kodo"),

    /**
     * MinIO
     */
    MINIO("MINIO", "MinIO"),

    /**
     * 数据库存储
     */
    DATABASE("DB", "数据库存储");

    /**
     * 存储类型代码
     */
    private final String code;

    /**
     * 存储类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     * @throws IllegalArgumentException 如果代码无效
     */
    public static StorageType of(String code) {
        if (code == null) {
            throw new IllegalArgumentException("存储类型代码不能为null");
        }
        for (StorageType type : values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的存储类型: " + code);
    }
}

