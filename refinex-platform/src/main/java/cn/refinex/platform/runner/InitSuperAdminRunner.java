package cn.refinex.platform.runner;

import cn.refinex.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化超级管理员 Runner
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InitSuperAdminRunner implements CommandLineRunner {

    private final UserService userService;

    /**
     * 初始化超级管理员
     */
    @Override
    public void run(String... args) {
        // 初始化超级管理员角色
        userService.initSuperAdmin();
    }
}
