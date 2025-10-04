package cn.refinex.platform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.refinex.api.auth.domain.dto.UserInfoDTO;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.service.RemoteAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器
 * <p>
 * 用于测试 Feign 调用和 Token 传递
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@RestController
@RequestMapping("/platform/test")
@Tag(name = "测试接口", description = "用于测试 Feign 调用")
public class TestController {

    @Autowired
    private RemoteAuthService remoteAuthService;

    /**
     * 测试 Feign 调用
     *
     * @return 用户信息
     */
    @GetMapping("/feign-test")
    @Operation(summary = "测试 Feign 调用", description = "通过 Feign 调用认证服务获取用户信息")
    @SaCheckLogin
    public ApiResult<UserInfoDTO> testFeign() {
        log.info("开始测试 Feign 调用");
        UserInfoDTO userInfo = remoteAuthService.getCurrentUserInfo();
        return ApiResult.success(userInfo);
    }

    /**
     * 测试权限验证
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    @GetMapping("/check-permission-test")
    @Operation(summary = "测试权限验证", description = "通过 Feign 调用认证服务验证权限")
    @SaCheckLogin
    public ApiResult<Boolean> testCheckPermission(@RequestParam String permission) {
        log.info("开始测试权限验证：permission={}", permission);
        boolean hasPermission = remoteAuthService.checkPermission(permission);
        return ApiResult.success(hasPermission);
    }
}

