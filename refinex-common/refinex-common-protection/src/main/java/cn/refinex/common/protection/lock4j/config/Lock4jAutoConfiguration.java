package cn.refinex.common.protection.lock4j.config;

import cn.refinex.common.protection.lock4j.core.callback.DefaultLockFailureStrategy;
import com.baomidou.lock.spring.boot.autoconfigure.LockAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * Lock4j 自动配置类
 *
 * @author 芋道源码
 * @author Refinex
 * @since 1.0.0
 */
@AutoConfiguration(before = LockAutoConfiguration.class)
@ConditionalOnClass(name = "com.baomidou.lock.annotation.Lock4j")
public class Lock4jAutoConfiguration {

    @Bean
    public DefaultLockFailureStrategy lockFailureStrategy() {
        return new DefaultLockFailureStrategy();
    }
}
