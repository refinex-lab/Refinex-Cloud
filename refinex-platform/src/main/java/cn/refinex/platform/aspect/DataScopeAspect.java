package cn.refinex.platform.aspect;

import cn.refinex.platform.annotation.DataScope;
import cn.refinex.platform.context.DataScopeContext;
import cn.refinex.platform.util.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 数据权限切面
 * <p>
 * 拦截 @DataScope 注解，根据用户权限动态设置数据权限上下文
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@Aspect
@Component
public class DataScopeAspect {

    /**
     * 环绕通知：处理数据权限
     *
     * @param point     切入点
     * @param dataScope 数据权限注解
     * @return 方法执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(dataScope)")
    public Object around(ProceedingJoinPoint point, DataScope dataScope) throws Throwable {
        // 获取当前用户 ID
        Long userId = UserContextHolder.getCurrentUserId();
        if (userId == null) {
            log.debug("当前用户未登录，跳过数据权限控制");
            return point.proceed();
        }

        // 根据数据权限范围设置查询条件
        DataScope.DataScopeType scopeType = dataScope.value();
        log.debug("设置数据权限上下文：scopeType={}, userId={}", scopeType, userId);

        try {
            // 设置数据权限上下文
            DataScopeContext.set(scopeType, userId);

            // 执行目标方法
            return point.proceed();
        } finally {
            // 清除数据权限上下文
            DataScopeContext.clear();
            log.debug("清除数据权限上下文");
        }
    }
}

