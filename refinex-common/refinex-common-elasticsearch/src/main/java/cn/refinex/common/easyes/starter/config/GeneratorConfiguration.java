package cn.refinex.common.easyes.starter.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.dromara.easyes.core.config.GeneratorConfig;
import org.dromara.easyes.core.toolkit.Generator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 索引生成器
 *
 * @author MoJie
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class GeneratorConfiguration extends Generator {

    private final ElasticsearchClient client;

    public GeneratorConfiguration(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * 生成索引
     *
     * @param generatorConfig 生成配置
     * @return 是否生成成功
     */
    @Override
    public Boolean generate(GeneratorConfig generatorConfig) {
        super.generateEntity(generatorConfig, this.client);
        return Boolean.TRUE;
    }
}
