package cn.refinex.common.file.config;

import cn.refinex.common.file.config.properties.FileProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 文件存储自动配置类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@EnableAsync
@AutoConfiguration
@EnableConfigurationProperties(FileProperties.class)
@ConditionalOnProperty(prefix = "refinex.file", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "cn.refinex.common.file")
public class FileAutoConfiguration {

    public FileAutoConfiguration() {
        log.info("初始化文件存储自动配置");
    }
}
