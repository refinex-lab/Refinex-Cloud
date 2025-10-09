package cn.refinex.platform.constants;

import cn.refinex.common.security.constants.SecurityCacheConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 字典模块缓存常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DictionaryCacheConstants {

    /**
     * 字典类型缓存 Key 前缀
     * KEY 格式：platform:dict:type:{dictCode}
     */
    public static final String DICT_TYPE_CACHE_PREFIX = "platform:dict:type:";

    /**
     * 字典数据列表缓存 Key 前缀（按类型）
     * KEY 格式：platform:dict:data:{dictCode}
     */
    public static final String DICT_DATA_CACHE_PREFIX = "platform:dict:data:";

    /**
     * 默认缓存过期时间（秒）- 30 分钟
     */
    public static final long DEFAULT_CACHE_TTL = 1800L;

    /**
     * 构建字典类型缓存 Key
     *
     * @param dictCode 字典编码
     * @return 缓存 Key
     */
    public static String buildDictTypeCacheKey(String dictCode) {
        return DICT_TYPE_CACHE_PREFIX + dictCode;
    }

    /**
     * 构建字典数据列表缓存 Key（按类型）
     *
     * @param dictCode 字典编码
     * @return 缓存 Key
     */
    public static String buildDictDataListCacheKey(String dictCode) {
        return DICT_DATA_CACHE_PREFIX + dictCode;
    }
}