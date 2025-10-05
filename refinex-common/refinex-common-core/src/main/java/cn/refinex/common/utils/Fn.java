package cn.refinex.common.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * 函数工具类
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Fn {

    // ======================== 基础类型转换 ======================== //

    /**
     * 安全转换为 Integer
     *
     * @param obj          源对象
     * @param defaultValue 转换失败时的默认值
     * @return Integer 值
     */
    public static Integer getInt(final Object obj, final Integer defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        final String str = obj.toString().trim();
        return NumberUtils.isCreatable(str) ? NumberUtils.toInt(str, defaultValue) : defaultValue;
    }

    /**
     * 安全转换为 Long
     */
    public static Long getLong(final Object obj, final Long defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        final String str = obj.toString().trim();
        return NumberUtils.isCreatable(str) ? NumberUtils.toLong(str, defaultValue) : defaultValue;
    }

    /**
     * 安全转换为 Double
     */
    public static Double getDouble(final Object obj, final Double defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        final String str = obj.toString().trim();
        return NumberUtils.isCreatable(str) ? NumberUtils.toDouble(str, defaultValue) : defaultValue;
    }

    /**
     * 安全转换为 BigDecimal
     */
    public static BigDecimal getDecimal(final Object obj, final BigDecimal defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        try {
            return new BigDecimal(obj.toString().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取安全字符串
     *
     * @param obj          任意对象
     * @param defaultValue 默认字符串
     */
    public static String getString(final Object obj, final String defaultValue) {
        return Optional.ofNullable(obj)
                .map(Object::toString)
                .map(String::trim)
                .filter(StrUtil::isNotEmpty)
                .orElse(defaultValue);
    }

    // ======================== UUID / 唯一标识生成 ======================== //

    /**
     * 生成 32 位 UUID（无中划线）
     *
     * @return UUID 字符串（长度 32）
     */
    public static String getUuid32() {
        // Hutool 快速 UUID，无 "-"，高性能线程安全
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 生成 64 位 UUID（Base64 编码，无中划线）
     *
     * @return Base64 UUID 字符串（长度约 22）
     */
    public static String getUuid64() {
        final UUID uuid = UUID.randomUUID();
        final byte[] bytes = new byte[16];
        System.arraycopy(longToBytes(uuid.getMostSignificantBits()), 0, bytes, 0, 8);
        System.arraycopy(longToBytes(uuid.getLeastSignificantBits()), 0, bytes, 8, 8);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 将 long 转换为 8 字节数组（小端序）
     *
     * @param x long 值
     * @return 8 字节数组
     */
    private static byte[] longToBytes(long x) {
        final byte[] buffer = new byte[8];
        for (int i = 7; i >= 0; i--) {
            buffer[i] = (byte) (x & 0xFF);
            x >>= 8;
        }
        return buffer;
    }

    // ======================== 布尔/安全检查 ======================== //

    /**
     * 安全转换为布尔值
     *
     * @param obj          源对象
     * @param defaultValue 转换失败时的默认值
     * @return Boolean 值
     */
    public static boolean getBoolean(final Object obj, final boolean defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        final String str = obj.toString().trim().toLowerCase();
        if ("true".equals(str) || "1".equals(str) || "yes".equals(str) || "y".equals(str)) {
            return true;
        }
        if ("false".equals(str) || "0".equals(str) || "no".equals(str) || "n".equals(str)) {
            return false;
        }
        return defaultValue;
    }

    /**
     * 判断对象是否为数字类型
     *
     * @param obj 源对象
     * @return 是否为数字类型
     */
    public static boolean isNumber(final Object obj) {
        return obj != null && NumberUtil.isNumber(obj.toString());
    }
}
