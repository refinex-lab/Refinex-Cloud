package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 向量同步状态枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum SyncStatus {

    /**
     * 待同步
     */
    PENDING("PENDING", "待同步"),

    /**
     * 同步中
     */
    SYNCING("SYNCING", "同步中"),

    /**
     * 已同步
     */
    SYNCED("SYNCED", "已同步"),

    /**
     * 同步失败
     */
    FAILED("FAILED", "同步失败");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 状态代码
     * @return 枚举
     */
    public static SyncStatus fromCode(String code) {
        if (code == null) {
            return PENDING;
        }
        for (SyncStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return PENDING;
    }

    /**
     * 判断是否已同步
     *
     * @return 是否已同步
     */
    public boolean isSynced() {
        return this == SYNCED;
    }

    /**
     * 判断是否正在同步
     *
     * @return 是否正在同步
     */
    public boolean isSyncing() {
        return this == SYNCING;
    }

    /**
     * 判断是否需要重试
     *
     * @return 是否需要重试
     */
    public boolean needsRetry() {
        return this == FAILED || this == PENDING;
    }
}

