package cn.refinex.common.protection.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.ArrayUtil;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.exception.code.ResultCode;
import cn.refinex.common.protection.ratelimiter.core.annotation.RateLimiter;
import cn.refinex.common.protection.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * Spring EL 表达式限流键解析器
 * <p>
 * 键名：表达式
 * 说明：根据表达式解析限流键
 *
 * @author 芋道源码
 * @since 1.0.0
 */
public class ExpressionRateLimiterKeyResolver implements RateLimiterKeyResolver {

    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 解析限流键
     *
     * @param joinPoint   连接点
     * @param rateLimiter 限流注解
     * @return 限流键
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        Method method = getMethod(joinPoint);
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(method);

        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (ArrayUtil.isNotEmpty(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }
        }

        Expression expression = expressionParser.parseExpression(rateLimiter.keyArg());
        return expression.getValue(evaluationContext, String.class);
    }

    /**
     * 获取连接点的方法
     *
     * @param point 连接点
     * @return 方法
     */
    private static Method getMethod(JoinPoint point) {
        // 声明在类上
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (!method.getDeclaringClass().isInterface()) {
            return method;
        }

        // 声明在接口上
        try {
            return point.getTarget()
                    .getClass()
                    .getDeclaredMethod(point.getSignature().getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new SystemException(ResultCode.INTERNAL_ERROR);
        }
    }
}
