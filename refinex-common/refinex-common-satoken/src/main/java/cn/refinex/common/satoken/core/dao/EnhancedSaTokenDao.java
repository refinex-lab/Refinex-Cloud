package cn.refinex.common.satoken.core.dao;

import cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.hutool.core.convert.Convert;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.object.BeanConverter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 增强的 SaTokenDaoBySessionFollowObject 实现类(Sa-Token 持久层接口)
 * <p>
 * 1. 使用框架封装的 RedisService 实现 Sa-Token 持久层接口统一协议
 * 2. 采用 Caffeine + Redis 实现多级缓存(一级缓存：Caffeine，二级缓存：Redis)，优化并发查询性能
 * 3. SaTokenDaoBySessionFollowObject 是 SaTokenDao 的子类，继承了 SaTokenDao 的所有方法
 *
 * @author Lion Li
 * @author Refinex
 * @since 1.0.0
 */
@AllArgsConstructor
public class EnhancedSaTokenDao implements SaTokenDaoBySessionFollowObject {

    private final RedisService redisService;

    /**
     * Caffeine 缓存，用于存储 Sa-Token 会话信息
     */
    private static final Cache<String, Object> CAFFEINE_CACHE = Caffeine.newBuilder()
            // 缓存过期时间：5秒 (设置最后一次写入或访问后过期时间)
            .expireAfterWrite(Duration.ofSeconds(5))
            // 初始容量：100
            .initialCapacity(100)
            // 最大缓存数量：1000
            .maximumSize(1000)
            .build();

    /**
     * 获取 Sa-Token 会话信息
     *
     * @param key 键名称
     * @return Sa-Token 会话信息
     */
    @Override
    public String get(String key) {
        if (key == null) {
            return null;
        }
        Object valueObj = CAFFEINE_CACHE.get(key, redisService.string()::get);
        return Convert.toStr(valueObj);
    }

    /**
     * 设置 Sa-Token 会话信息
     *
     * @param key     键名称
     * @param value   值
     * @param timeout 数据有效期（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void set(String key, String value, long timeout) {
        if (key == null) {
            return;
        }
        // 如果有效期为 0 或者小于 -2 (表示系统中不存在这个缓存)，则不存储
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }

        // 判断是否为永不过期(-1)
        if (timeout == NEVER_EXPIRE) {
            redisService.string().set(key, value);
        } else {
            redisService.string().set(key, value, Duration.ofSeconds(timeout));
        }

        // 刷新(丢弃) Caffeine 缓存, 确保下次查询时从 Redis 中获取最新数据
        CAFFEINE_CACHE.invalidate(key);
    }

    /**
     * 更新 Sa-Token 会话信息
     *
     * @param key   键名称
     * @param value 值
     */
    @Override
    public void update(String key, String value) {
        if (key == null) {
            return;
        }
        Boolean hasKey = redisService.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            // 更新 Redis 中的值
            redisService.string().set(key, value);

            // 刷新(丢弃) Caffeine 缓存, 确保下次查询时从 Redis 中获取最新数据
            CAFFEINE_CACHE.invalidate(key);
        }
    }

    /**
     * 删除 Sa-Token 会话信息
     *
     * @param key 键名称
     */
    @Override
    public void delete(String key) {
        if (key == null) {
            return;
        }
        Boolean hasKey = redisService.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            // 删除 Redis 中的值
            redisService.delete(key);

            // 刷新(丢弃) Caffeine 缓存, 确保下次查询时从 Redis 中获取最新数据
            CAFFEINE_CACHE.invalidate(key);
        }
    }

    /**
     * 获取 Sa-Token 会话信息有效期
     *
     * @param key 指定 key
     * @return Sa-Token 会话信息有效期（单位: 秒）
     */
    @Override
    public long getTimeout(String key) {
        if (key == null) {
            return NOT_VALUE_EXPIRE; // 表示 key 不存在
        }
        // 获取 TTL（单位: 秒）
        Long expire = redisService.getExpire(key);
        // 如果 TTL 为 null，则表示永不过期(-1)，否则返回实际 TTL
        return expire == null ? NEVER_EXPIRE : expire;
    }

    /**
     * 更新 Sa-Token 会话信息有效期
     *
     * @param key     指定 key
     * @param timeout 过期时间（单位: 秒）
     */
    @Override
    public void updateTimeout(String key, long timeout) {
        if (key == null) {
            return;
        }
        // 如果有效期为 0 或者小于 -2 (表示系统中不存在这个缓存)，则不更新
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }

        // 判断是否为永不过期(-1)，已经是永不过期的不需要更新
        if (timeout != NEVER_EXPIRE) {
            // 更新 Redis 中的 TTL
            redisService.expire(key, Duration.ofSeconds(timeout));
        }
    }

    /**
     * 获取 Sa-Token 会话信息对象
     *
     * @param key 键名称
     * @return Sa-Token 会话信息对象
     */
    @Override
    public Object getObject(String key) {
        if (key == null) {
            return null;
        }
        return CAFFEINE_CACHE.get(key, redisService.string()::get);
    }

    /**
     * 获取 Sa-Token 会话信息对象
     *
     * @param key       键名称
     * @param classType 目标类型
     * @return Sa-Token 会话信息对象
     */
    @Override
    public <T> T getObject(String key, Class<T> classType) {
        if (key == null) {
            return null;
        }
        Object valueObj = CAFFEINE_CACHE.get(key, redisService.string()::get);
        return BeanConverter.toBean(valueObj, classType);
    }

    /**
     * 设置 Sa-Token 会话信息对象
     *
     * @param key     键名称
     * @param object  值
     * @param timeout 存活时间（值大于0时限时存储，值=-1时永久存储，值=0或小于-2时不存储）
     */
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (key == null) {
            return;
        }
        // 如果有效期为 0 或者小于 -2 (表示系统中不存在这个缓存)，则不存储
        if (timeout == 0 || timeout <= NOT_VALUE_EXPIRE) {
            return;
        }

        // 判断是否为永不过期(-1)
        if (timeout == NEVER_EXPIRE) {
            redisService.string().set(key, JsonUtils.toJson(object));
        } else {
            redisService.string().set(key, JsonUtils.toJson(object), Duration.ofSeconds(timeout));
        }

        // 刷新(丢弃) Caffeine 缓存, 确保下次查询时从 Redis 中获取最新数据
        CAFFEINE_CACHE.invalidate(key);
    }

    /**
     * 更新 Sa-Token 会话信息对象
     *
     * @param key    键名称
     * @param object 值
     */
    @Override
    public void updateObject(String key, Object object) {
        if (key == null) {
            return;
        }
        Boolean hasKey = redisService.hasKey(key);
        if (Boolean.TRUE.equals(hasKey)) {
            // 更新 Redis 中的值
            redisService.string().set(key, JsonUtils.toJson(object));
            // 刷新(丢弃) Caffeine 缓存, 确保下次查询时从 Redis 中获取最新数据
            CAFFEINE_CACHE.invalidate(key);
        }
    }

    /**
     * 删除 Sa-Token 会话信息对象
     *
     * @param key 键名称
     */
    @Override
    public void deleteObject(String key) {
        if (key == null) {
            return;
        }
        delete(key);
    }

    /**
     * 获取 Sa-Token 会话信息对象有效期
     *
     * @param key 指定 key
     * @return Sa-Token 会话信息对象有效期
     */
    @Override
    public long getObjectTimeout(String key) {
        if (key == null) {
            return NOT_VALUE_EXPIRE;
        }
        return getTimeout(key);
    }

    /**
     * 更新 Sa-Token 会话信息对象有效期
     *
     * @param key     指定 key
     * @param timeout 剩余存活时间（单位: 秒）
     */
    @Override
    public void updateObjectTimeout(String key, long timeout) {
        if (key == null) {
            return;
        }
        updateTimeout(key, timeout);
    }

    /**
     * 搜索 Sa-Token 会话信息对象键名
     *
     * @param prefix   前缀
     * @param keyword  关键字
     * @param start    开始处索引
     * @param size     获取数量  (-1代表从 start 处一直取到末尾)
     * @param sortType 排序类型（true=正序，false=反序）
     * @return Sa-Token 会话信息对象键名列表
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        if (prefix == null) {
            return new ArrayList<>();
        }
        String pattern = prefix + "*" + keyword + "*";
        Object resultObj = CAFFEINE_CACHE.get(pattern, k -> {
            Set<String> keys = redisService.keys(pattern);
            List<String> list = new ArrayList<>(keys);
            return SaFoxUtil.searchList(list, start, size, sortType);
        });
        return (List<String>) resultObj;
    }
}
