package cn.refinex.common.protection.lock4j.core.callback;

import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.SystemException;
import com.baomidou.lock.LockFailureStrategy;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * 默认的 Lock4j 锁失败策略
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public class DefaultLockFailureStrategy implements LockFailureStrategy {

    /**
     * 锁失败时的回调方法
     *
     * @param key     锁的键值
     * @param method  加锁的方法
     * @param arguments 方法参数
     */
    @Override
    public void onLockFailure(String key, Method method, Object[] arguments) {
        log.debug("[onLockFailure][线程:{} 获取锁失败，key:{} 获取失败:{} ]", Thread.currentThread().getName(), key, arguments);
        throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR.getCode(), "请求失败，请稍后重试");
    }
}
