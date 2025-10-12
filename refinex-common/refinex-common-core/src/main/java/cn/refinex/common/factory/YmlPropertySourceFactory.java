package cn.refinex.common.factory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.Properties;

/**
 * YAML 属性源工厂类
 *
 * @author Lion Li
 * @since 1.0.0
 */
public class YmlPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * 创建 YAML 属性源
     *
     * @param name     属性源名称
     * @param resource 编码资源
     * @return 属性源
     * @throws IOException IO 异常
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
        // 获取资源文件名
        String sourceName = resource.getResource().getFilename();
        // 检查资源文件名是否以 .yml 或 .yaml 结尾
        if (StringUtils.isNotBlank(sourceName) && StringUtils.endsWithAny(sourceName, ".yml", ".yaml")) {
            // 创建 YAML 属性工厂 bean
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();

            Properties props = factory.getObject();
            if (props == null) {
                props = new Properties();
            }

            // 创建属性源并返回
            return new PropertiesPropertySource(sourceName, props);
        }

        // 不是 YAML 文件，调用父类方法创建属性源
        return super.createPropertySource(name, resource);
    }
}
