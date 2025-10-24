package cn.refinex.common.easyes.spring.config;

import lombok.Setter;
import org.dromara.easyes.common.constants.BaseEsConstants;
import org.dromara.easyes.common.property.EasyEsDynamicProperties;
import org.dromara.easyes.common.property.EasyEsProperties;
import org.dromara.easyes.common.strategy.AutoProcessIndexStrategy;
import org.dromara.easyes.common.utils.EsClientUtils;
import org.dromara.easyes.core.index.AutoProcessIndexNotSmoothlyStrategy;
import org.dromara.easyes.core.index.AutoProcessIndexSmoothlyStrategy;
import org.dromara.easyes.spring.factory.IndexStrategyFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * EasyEs Spring 配置类
 *
 * @author MoJie
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class EasyEsConfiguration implements InitializingBean, EnvironmentAware {

    /**
     * 环境变量
     */
    private Environment environment;

    /**
     * EasyEs 静态配置属性
     */
    @Setter
    @Autowired(required = false)
    private EasyEsProperties easyEsProperties;

    /**
     * EasyEs 动态配置属性
     */
    @Setter
    @Autowired(required = false)
    private EasyEsDynamicProperties easyEsDynamicProperties;

    /**
     * 初始化 EasyEs 配置
     *
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Boolean enable = environment.getProperty(BaseEsConstants.ENABLE_PREFIX, Boolean.class, Boolean.TRUE);
        if (Boolean.TRUE.equals(enable)) {
            Assert.notNull(this.easyEsProperties, "easyEsProperties must is A bean. please config easy-es properties. for-example: easy-es.enable=true");
        }
    }

    /**
     * 设置环境变量
     *
     * @param environment 环境变量
     */
    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    /**
     * 索引策略工厂
     */
    @Bean
    public IndexStrategyFactory indexStrategyFactory() {
        return new IndexStrategyFactory();
    }

    /**
     * Elasticsearch 客户端工具类
     */
    @Bean
    public EsClientUtils esClientUtils() {
        EsClientUtils esClientUtils = new EsClientUtils();
        // 初始化动态配置属性
        if (this.easyEsDynamicProperties == null) {
            this.easyEsDynamicProperties = new EasyEsDynamicProperties();
        }

        Map<String, EasyEsProperties> datasourceMap = this.easyEsDynamicProperties.getDatasource();
        if (datasourceMap.isEmpty()) {
            // 设置默认数据源, 兼容不使用多数据源配置场景的老用户使用习惯
            datasourceMap.put(EsClientUtils.DEFAULT_DS, this.easyEsProperties);
        }

        // 注册 Elasticsearch 客户端
        datasourceMap.forEach((key, easyEsConfigProperties) ->
                EsClientUtils.registerClient(key, () -> EsClientUtils.buildClient(easyEsConfigProperties)));

        return esClientUtils;
    }

    /**
     * 自动处理索引策略 - 平滑处理
     */
    @Bean
    public AutoProcessIndexStrategy autoProcessIndexSmoothlyStrategy() {
        return new AutoProcessIndexSmoothlyStrategy();
    }

    /**
     * 自动处理索引策略 - 非平滑处理
     */
    @Bean
    public AutoProcessIndexStrategy autoProcessIndexNotSmoothlyStrategy() {
        return new AutoProcessIndexNotSmoothlyStrategy();
    }
}
