package cn.refinex.auth.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.StrUtil;
import cn.refinex.api.platform.client.user.UserRemoteService;
import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.dto.request.RecordLoginLogRequest;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.auth.properties.CaptchaProperties;
import cn.refinex.auth.properties.UserPasswordProperties;
import cn.refinex.auth.service.AuthService;
import cn.refinex.auth.service.CaptchaService;
import cn.refinex.auth.service.LoginAsyncService;
import cn.refinex.common.constants.SystemRedisKeyConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.enums.LoginType;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.device.DeviceUtils;
import cn.refinex.common.utils.servlet.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import static cn.refinex.common.constants.SystemStatusConstants.DISABLE_VALUE;

/**
 * 认证服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final CaptchaProperties captchaProperties;
    private final CaptchaService captchaService;
    private final UserRemoteService userRemoteService;
    private final RedisService redisService;
    private final UserPasswordProperties userPasswordProperties;
    private final LoginAsyncService loginAsyncService;

    /**
     * 用户登录
     *
     * @param request     登录请求
     * @return 登录响应（包含 Token）
     */
    @Override
    public LoginVo login(LoginRequest request) {
        // 校验登录类型
        LoginType loginType = LoginType.fromCode(request.getLoginType());

        // 获取设备类型、IP 和 User-Agent（提前获取，用于失败日志记录）
        String deviceType = DeviceUtils.getDeviceType(request.getDeviceType());
        String clientIp = ServletUtils.getClientIp();
        String userAgent = ServletUtils.getUserAgent();

        try {
            // 验证码校验（如果启用）
            if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
                captchaService.verify(request.getCaptchaUuid(), request.getCaptchaCode());
            }
        } catch (BusinessException e) {
            // 记录验证码校验失败日志
            recordLoginFailLog(null, request.getUsername(), request.getLoginType(), clientIp, userAgent, deviceType, e.getMessage());
            throw e;
        }

        // 根据用户名/邮箱获取登录用户信息
        ApiResult<LoginUser> userResult = Objects.equals(request.getLoginType(), loginType.getCode())
                ? userRemoteService.getLoginUserByUserName(request.getUsername())
                : userRemoteService.getLoginUserByEmail(request.getUsername());
        LoginUser loginUser = userResult.data();
        if (Objects.isNull(loginUser)) {
            log.warn("根据用户名查询登录用户失败，username: {}", request.getUsername());
            // 记录用户不存在失败日志
            recordLoginFailLog(null, request.getUsername(), request.getLoginType(), clientIp, userAgent, deviceType, Optional.ofNullable(userResult.message()).orElse("用户不存在"));
            throw new BusinessException("用户不存在");
        }

        try {
            // 验证用户状态
            loginUser.validateUserStatus();
        } catch (BusinessException e) {
            // 记录用户状态异常失败日志
            recordLoginFailLog(loginUser.getUserId(), loginUser.getUsername(), request.getLoginType(), clientIp, userAgent, deviceType, e.getMessage());
            throw e;
        }

        // 验证用户密码（checkLogin 方法内部会记录密码错误日志）
        checkLogin(LoginType.PASSWORD, request.getUsername(), loginUser.getUserId(), request.getLoginType(), clientIp, userAgent, deviceType,
                () -> !passwordEncoder.matches(request.getPassword(), loginUser.getPassword()));

        // 构建登录参数
        SaLoginParameter loginParameter = new SaLoginParameter()
                // 设置设备类型
                .setDeviceType(deviceType)
                // 设置客户端 ID 到 Extra 中，用于后续权限校验
                .setExtra(LoginHelper.CLIENT_KEY, request.getClientId());

        // [记住我] 模式
        if (Boolean.TRUE.equals(request.getRememberMe())) {
            // 持久化 Cookie (临时 Cookie 在浏览器关闭时会自动删除，持久 Cookie 在重新打开后依然存在)
            loginParameter.setIsLastingCookie(true);
        }

        // 执行登录（Sa-Token）生成 Token
        LoginHelper.login(loginUser, loginType.getCode(), loginParameter);

        // 构建登录响应 VO
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(request.getClientId());

        // 返回登录响应 VO
        return loginVo;
    }

    /**
     * 检查登录状态
     *
     * @param loginType              登录类型
     * @param username               用户名
     * @param userId                 用户 ID
     * @param loginTypeCode          登录类型代码
     * @param clientIp               客户端 IP
     * @param userAgent              User-Agent
     * @param deviceType             设备类型
     * @param passwordValidationFail 密码校验失败判断逻辑，返回 true 表示密码错误
     */
    @Override
    public void checkLogin(LoginType loginType, String username, Long userId, Integer loginTypeCode,
                           String clientIp, String userAgent, String deviceType,
                           BooleanSupplier passwordValidationFail) {
        if (loginType.equals(LoginType.PASSWORD)) {
            // 获取配置参数
            Integer maxRetryCount = userPasswordProperties.getMaxRetryCount();
            Integer lockTime = userPasswordProperties.getLockTime();

            // 构建 Redis 缓存键
            String loginErrorCountCacheKey = SystemRedisKeyConstants.Login.buildLoginErrorCountCacheKey(username);

            // 获取当前登录错误次数
            int currentErrorCount = Optional.ofNullable(
                    redisService.string().get(loginErrorCountCacheKey, Integer.class)
            ).orElse(0);

            // 如果用户登录错误次数超过最大次数，锁定用户
            if (currentErrorCount >= maxRetryCount) {
                String failReason = StrUtil.format("密码错误次数超过最大次数，请 %s 分钟后重试", lockTime);
                // 记录失败日志
                recordLoginFailLog(userId, username, loginTypeCode, clientIp, userAgent, deviceType, failReason);
                throw new BusinessException(failReason);
            }

            // 执行密码校验
            boolean isPasswordIncorrect = passwordValidationFail.getAsBoolean();

            // 密码校验通过，清除错误计数
            if (!isPasswordIncorrect) {
                redisService.delete(loginErrorCountCacheKey);
                return;
            }

            // 密码错误，增加错误计数
            int newErrorCount = currentErrorCount + 1;
            redisService.string().set(
                    loginErrorCountCacheKey,
                    newErrorCount,
                    Duration.ofMinutes(lockTime)
            );

            // 判断是否达到锁定阈值
            String failReason;
            if (newErrorCount >= maxRetryCount) {
                failReason = String.format("密码错误次数已达上限，账户已被锁定 %d 分钟", lockTime);
            } else {
                // 提示剩余尝试次数
                int remainingAttempts = maxRetryCount - newErrorCount;
                failReason = String.format("密码错误，您还有 %d 次尝试机会", remainingAttempts);
            }

            // 记录失败日志
            recordLoginFailLog(userId, username, loginTypeCode, clientIp, userAgent, deviceType, failReason);
            throw new BusinessException(failReason);
        }
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        try {
            // 获取登录用户
            LoginUser loginUser = LoginHelper.getLoginUser();

            // 如果登录用户为空，直接返回
            if (Objects.isNull(loginUser)) {
                return;
            }

            // 超级管理员根据实际需要处理
            // if (LoginHelper.isSuperAdmin()) {}
        } catch (NotLoginException ignored) {
            log.error("用户登出时未登录，忽略异常");
        } finally {
            try {
                // 登出用户
                StpUtil.logout();
            } catch (NotLoginException ignored) {
                log.error("用户登出时未登录，忽略异常");
            }
        }
    }

    /**
     * 记录登录失败日志（辅助方法）
     *
     * @param userId       用户 ID（可能为 null）
     * @param username     用户名
     * @param loginType    登录类型
     * @param clientIp     客户端 IP
     * @param userAgent    User-Agent
     * @param deviceType   设备类型
     * @param failReason   失败原因
     */
    private void recordLoginFailLog(Long userId, String username, Integer loginType, String clientIp, String userAgent, String deviceType, String failReason) {
        RecordLoginLogRequest request = RecordLoginLogRequest.builder()
                .userId(userId)
                .username(username)
                .loginType(loginType)
                .loginIp(clientIp)
                .userAgent(userAgent)
                .deviceType(deviceType)
                .loginStatus(DISABLE_VALUE)
                .failReason(failReason)
                .build();

        loginAsyncService.recordLoginLog(request);
    }

}

