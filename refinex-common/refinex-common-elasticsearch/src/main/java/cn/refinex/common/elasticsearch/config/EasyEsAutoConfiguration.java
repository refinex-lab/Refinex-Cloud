package cn.refinex.common.elasticsearch.config;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * EasyEs 自动配置类
 *
 * @author Lion Li
 * @since 1.0.0
 */
@AutoConfiguration
@EsMapperScan(value = "cn.refinex.**.elasticsearch.mapper") // 扫描制定路径的 es mapper 接口
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class EasyEsAutoConfiguration {
}
