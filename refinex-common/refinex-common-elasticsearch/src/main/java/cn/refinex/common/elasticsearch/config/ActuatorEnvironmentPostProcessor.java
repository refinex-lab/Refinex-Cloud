package cn.refinex.common.elasticsearch.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 为 actuator 环境添加 elasticsearch 相关配置
 *
 * @author Lion Li
 * @since 1.0.0
 */
public class ActuatorEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * 为 actuator 环境添加 elasticsearch 相关配置
     *
     * @param environment 环境
     * @param application 应用
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        System.setProperty("management.health.elasticsearch.enabled", "false");
    }

    /**
     * 设置顺序为最高优先级
     *
     * @return 顺序
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
