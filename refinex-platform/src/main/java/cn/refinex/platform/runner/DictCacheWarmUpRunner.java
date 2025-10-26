package cn.refinex.platform.runner;

import cn.refinex.platform.service.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 字典缓存预热 Runner
 * <p>
 * 在应用启动完成后自动预热字典缓存，提升首次访问性能
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(100) // 设置较低优先级，确保在其他初始化完成后执行
@RequiredArgsConstructor
public class DictCacheWarmUpRunner implements CommandLineRunner {

    private final SysDictService dictService;

    /**
     * 应用启动后执行缓存预热
     *
     * @param args 启动参数
     */
    @Override
    public void run(String... args) {
        log.info("开始执行字典缓存预热...");
        try {
            dictService.warmUpCache();
        } catch (Exception e) {
            log.error("字典缓存预热失败", e);
        }
    }
}

