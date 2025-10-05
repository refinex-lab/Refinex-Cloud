package cn.refinex.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.auth.repository.SysUserRepository;
import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.dto.response.LoginResponse;
import cn.refinex.auth.domain.entity.SysUser;
import cn.refinex.auth.properties.CaptchaProperties;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaProperties captchaProperties;
    private final CaptchaService captchaService;

    /**
     * 用户登录
     *
     * @param request  登录请求
     * @param clientIp 客户端 IP
     * @return 登录响应（包含 Token）
     */
    public LoginResponse login(LoginRequest request, String clientIp) {
        log.info("用户登录，username: {}, clientIp: {}", request.getUsername(), clientIp);

        // 1. 验证码校验（如果启用）
        if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            captchaService.verify(request.getCaptchaUuid(), request.getCaptchaCode());
        }

        // 2. 查询用户
        SysUser user = sysUserRepository.selectByUsername(request.getUsername());
        if (user == null) {
            log.warn("用户不存在，username: {}", request.getUsername());
            throw new BusinessException("AUTH", "AUTH_1001", "用户名或密码错误");
        }

        // 3. 验证用户状态
        if (user.getUserStatus() == null || user.getUserStatus() != 1) {
            log.warn("用户状态异常，username: {}, status: {}", request.getUsername(), user.getUserStatus());
            throw new BusinessException("AUTH", "AUTH_1002", "用户状态异常，无法登录");
        }

        // 4. 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("密码错误，username: {}", request.getUsername());
            throw new BusinessException("AUTH", "AUTH_1001", "用户名或密码错误");
        }

        // 5. 执行登录（Sa-Token）
        StpUtil.login(user.getId());
        log.info("用户登录成功，userId: {}, username: {}", user.getId(), user.getUsername());

        // 6. 存储用户信息到 Session
        StpUtil.getSession().set("username", user.getUsername());
        StpUtil.getSession().set("nickname", user.getNickname());
        StpUtil.getSession().set("avatar", user.getAvatar());

        // 7. 更新最后登录信息（异步执行，不影响登录响应）
        try {
            sysUserRepository.updateLastLoginInfo(user.getId(), LocalDateTime.now(), clientIp);
        } catch (Exception e) {
            log.error("更新最后登录信息失败，userId: {}", user.getId(), e);
            // 不影响登录流程
        }

        // 8. 获取 Token
        String token = StpUtil.getTokenValue();

        // 8. 构建响应
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(StpUtil.getTokenTimeout())
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }

    /**
     * 用户登出
     */
    public void logout() {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("用户登出，userId: {}", userId);

        StpUtil.logout();

        log.info("用户登出成功，userId: {}", userId);
    }

    /**
     * 刷新 Token
     * <p>
     * 说明：
     * 1. Sa-Token 的 JWT Simple 模式下，Token 刷新会生成新的 JWT
     * 2. 旧 Token 会失效
     * </p>
     *
     * @return 新的 Token
     */
    public String refreshToken() {
        Long userId = SecurityUtils.getRequiredUserId();
        log.info("刷新 Token，userId: {}", userId);

        // Sa-Token 会自动刷新 Token
        StpUtil.renewTimeout(StpUtil.getTokenTimeout());

        String newToken = StpUtil.getTokenValue();
        log.info("Token 刷新成功，userId: {}", userId);

        return newToken;
    }

    /**
     * 验证用户权限
     *
     * @param permission 权限码
     * @return 是否拥有权限
     */
    public boolean checkPermission(String permission) {
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                log.warn("用户未登录，权限验证失败");
                return false;
            }

            boolean hasPermission = SecurityUtils.hasPermission(permission);
            log.debug("权限验证结果：userId={}, permission={}, hasPermission={}", userId, permission, hasPermission);
            return hasPermission;
        } catch (Exception e) {
            log.error("权限验证异常", e);
            return false;
        }
    }
}

