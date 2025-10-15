package cn.refinex.common.job.config;

import com.alibaba.ttl.TtlRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务自动配置类
 *
 * <p>该配置类用于增强 Spring 异步任务执行器，使其支持阿里 TransmittableThreadLocal (TTL)，
 * 解决异步线程中上下文信息（如用户信息、租户信息、追踪ID等）传递丢失的问题。</p>
 *
 * <h3>核心功能：</h3>
 * <ul>
 *     <li>自动为 ThreadPoolTaskExecutor 添加 TTL 装饰器</li>
 *     <li>自动为 SimpleAsyncTaskExecutor 添加 TTL 装饰器</li>
 *     <li>确保上下文信息在异步线程中正确传递</li>
 * </ul>
 *
 * <h3>解决的问题：</h3>
 * <p>在使用 @Async 注解进行异步处理时，ThreadLocal 中的上下文信息无法传递到异步线程，导致：</p>
 * <ul>
 *     <li><strong>用户信息丢失：</strong>异步线程中无法获取当前用户信息</li>
 *     <li><strong>租户信息丢失：</strong>多租户场景下无法识别当前租户</li>
 *     <li><strong>追踪信息丢失：</strong>链路追踪 ID 断裂，无法关联日志</li>
 *     <li><strong>请求信息丢失：</strong>无法获取原始请求的上下文</li>
 * </ul>
 *
 * <h3>工作原理：</h3>
 * <p>通过 BeanPostProcessor 在 Bean 初始化阶段，为所有异步执行器设置 TaskDecorator，
 * 使用 TtlRunnable 包装原始任务，自动在任务执行前恢复上下文，执行后清理上下文。</p>
 *
 * <h3>TTL 传递示例：</h3>
 * <pre>{@code
 * // 主线程设置上下文
 * TenantContextHolder.setTenantId(100L);
 * SecurityUtils.setCurrentUser(userInfo);
 * MDC.put("traceId", "abc-123");
 *
 * // 调用异步方法
 * orderService.processOrderAsync(orderId);
 *
 * // 异步线程中自动获取到相同的上下文
 * // tenantId = 100
 * // userInfo = 主线程设置的用户信息
 * // traceId = "abc-123"
 * }</pre>
 *
 * <h3>支持的执行器类型：</h3>
 * <ul>
 *     <li><strong>ThreadPoolTaskExecutor：</strong>Spring 默认的线程池执行器</li>
 *     <li><strong>SimpleAsyncTaskExecutor：</strong>Spring 简单异步执行器</li>
 * </ul>
 *
 * <h3>注意事项：</h3>
 * <ul>
 *     <li>需要引入 TransmittableThreadLocal 依赖：transmittable-thread-local</li>
 *     <li>只有通过 @Async 注解的方法才会自动传递上下文</li>
 *     <li>手动创建的线程需要显式使用 TtlRunnable 包装</li>
 *     <li>该配置会影响所有 Spring 管理的异步执行器</li>
 * </ul>
 *
 * <h3>依赖配置：</h3>
 * <pre>{@code
 * <dependency>
 *     <groupId>com.alibaba</groupId>
 *     <artifactId>transmittable-thread-local</artifactId>
 *     <version>2.14.4</version>
 * </dependency>
 * }</pre>
 *
 * @author Refinex
 * @see TtlRunnable
 * @see ThreadPoolTaskExecutor
 * @see SimpleAsyncTaskExecutor
 * @see EnableAsync
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableAsync
public class RefinexAsyncAutoConfiguration {

    /**
     * 创建异步执行器装饰器处理器
     *
     * <p>该 BeanPostProcessor 在 Spring 容器初始化阶段，自动为所有异步执行器添加 TTL 支持，无需手动配置。</p>
     *
     * <h4>处理流程：</h4>
     * <ol>
     *     <li>检测 Bean 是否为支持的异步执行器类型</li>
     *     <li>为执行器设置 TaskDecorator，使用 TtlRunnable 包装任务</li>
     *     <li>返回增强后的执行器 Bean</li>
     * </ol>
     *
     * <h4>装饰器作用：</h4>
     * <ul>
     *     <li>任务提交时：捕获当前线程的上下文信息</li>
     *     <li>任务执行前：恢复捕获的上下文信息到执行线程</li>
     *     <li>任务执行后：清理执行线程的上下文信息</li>
     * </ul>
     *
     * @return BeanPostProcessor 实例
     */
    @Bean
    public BeanPostProcessor asyncExecutorTtlBeanPostProcessor() {
        log.info("[异步任务配置] 初始化 TTL 异步执行器装饰器");
        return new AsyncExecutorTtlBeanPostProcessor();
    }

    /**
     * 异步执行器 TTL 装饰器处理器
     *
     * <p>内部静态类，实现 BeanPostProcessor 接口，负责在 Bean 初始化前为异步执行器添加 TTL 支持。</p>
     */
    @Slf4j
    private static class AsyncExecutorTtlBeanPostProcessor implements BeanPostProcessor {

        /**
         * Bean 初始化前的处理方法
         *
         * <p>在 Bean 的初始化方法（如 @PostConstruct）调用之前执行，用于为异步执行器添加 TTL 装饰器。</p>
         *
         * @param bean     Spring 容器中的 Bean 实例
         * @param beanName Bean 的名称
         * @return 处理后的 Bean 实例（可能是原始 Bean 或增强后的 Bean）
         * @throws BeansException 如果处理过程中发生异常
         */
        @Override
        @Nullable
        public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
            // 处理 ThreadPoolTaskExecutor
            if (bean instanceof ThreadPoolTaskExecutor threadPoolTaskExecutor) {
                return enhanceThreadPoolTaskExecutor(threadPoolTaskExecutor, beanName);
            }

            // 处理 SimpleAsyncTaskExecutor
            if (bean instanceof SimpleAsyncTaskExecutor simpleAsyncTaskExecutor) {
                return enhanceSimpleAsyncTaskExecutor(simpleAsyncTaskExecutor, beanName);
            }

            return bean;
        }

        /**
         * 增强 ThreadPoolTaskExecutor，添加 TTL 支持
         *
         * <p>ThreadPoolTaskExecutor 是 Spring 最常用的异步执行器，基于 JDK 的 ThreadPoolExecutor 实现。</p>
         *
         * @param executor 原始执行器
         * @param beanName Bean 名称
         * @return 增强后的执行器
         */
        private Object enhanceThreadPoolTaskExecutor(ThreadPoolTaskExecutor executor, String beanName) {
            try {
                executor.setTaskDecorator(TtlRunnable::get);
                log.info("[异步任务配置] 为 ThreadPoolTaskExecutor 添加 TTL 支持 - Bean名称: {}, 核心线程数: {}, 最大线程数: {}",
                        beanName,
                        executor.getCorePoolSize(),
                        executor.getMaxPoolSize());
                return executor;
            } catch (Exception e) {
                log.error("[异步任务配置] 增强 ThreadPoolTaskExecutor 失败 - Bean名称: {}", beanName, e);
                // 增强失败不影响 Bean 创建，返回原始执行器
                return executor;
            }
        }

        /**
         * 增强 SimpleAsyncTaskExecutor，添加 TTL 支持
         *
         * <p>SimpleAsyncTaskExecutor 为每个任务创建新线程，不使用线程池，适用于执行周期较长的后台任务。</p>
         *
         * <p><strong>使用场景：</strong></p>
         * <ul>
         *     <li>短生命周期的应用</li>
         *     <li>任务执行频率很低的场景</li>
         *     <li>不需要线程复用的场景</li>
         * </ul>
         *
         * <p><strong>注意：</strong>生产环境推荐使用 ThreadPoolTaskExecutor。</p>
         *
         * @param executor 原始执行器
         * @param beanName Bean 名称
         * @return 增强后的执行器
         */
        private Object enhanceSimpleAsyncTaskExecutor(SimpleAsyncTaskExecutor executor, String beanName) {
            try {
                executor.setTaskDecorator(TtlRunnable::get);
                log.info("[异步任务配置] 为 SimpleAsyncTaskExecutor 添加 TTL 支持 - Bean名称: {}, 并发限制: {}",
                        beanName,
                        executor.getConcurrencyLimit());
                return executor;
            } catch (Exception e) {
                log.error("[异步任务配置] 增强 SimpleAsyncTaskExecutor 失败 - Bean名称: {}", beanName, e);
                // 增强失败不影响 Bean 创建，返回原始执行器
                return executor;
            }
        }
    }

}
