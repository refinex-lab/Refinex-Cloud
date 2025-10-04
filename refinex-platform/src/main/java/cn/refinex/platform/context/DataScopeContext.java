package cn.refinex.platform.context;

import cn.refinex.platform.annotation.DataScope;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据权限上下文
 * <p>
 * 使用 ThreadLocal 存储当前线程的数据权限信息
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataScopeContext {

    private static final ThreadLocal<DataScopeInfo> CONTEXT = new ThreadLocal<>();

    /**
     * 设置数据权限信息
     *
     * @param scopeType 数据权限范围类型
     * @param userId    用户ID
     */
    public static void set(DataScope.DataScopeType scopeType, Long userId) {
        CONTEXT.set(new DataScopeInfo(scopeType, userId));
    }

    /**
     * 获取数据权限信息
     *
     * @return 数据权限信息
     */
    public static DataScopeInfo get() {
        return CONTEXT.get();
    }

    /**
     * 清除数据权限信息
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 数据权限信息
     */
    @Data
    @AllArgsConstructor
    public static class DataScopeInfo {
        /**
         * 数据权限范围类型
         */
        private DataScope.DataScopeType scopeType;

        /**
         * 用户ID
         */
        private Long userId;
    }
}

