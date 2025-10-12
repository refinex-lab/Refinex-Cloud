package cn.refinex.auth.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.auth.enums.ClientTypeEnum;
import cn.refinex.auth.enums.UserStatusEnum;
import cn.refinex.auth.properties.CaptchaProperties;
import cn.refinex.auth.properties.UserPasswordProperties;
import cn.refinex.auth.service.AuthService;
import cn.refinex.auth.service.CaptchaService;
import cn.refinex.auth.service.LoginAsyncService;
import cn.refinex.common.constants.SystemRedisKeyConstants;
import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.LoginType;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.device.DeviceUtils;
import cn.refinex.common.utils.servlet.ServletUtils;
import cn.refinex.platform.api.UserFeignClient;
import cn.refinex.platform.domain.model.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.function.Supplier;

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
    private final LoginAsyncService loginAsyncService;
    private final UserFeignClient userClient;
    private final RedisService redisService;
    private final UserPasswordProperties userPasswordProperties;

    /**
     * 用户登录
     *
     * @param request     登录请求
     * @param clientIp    客户端 IP
     * @param httpRequest HttpServletRequest 对象（用于获取 User-Agent）
     * @return 登录响应（包含 Token）
     */
    @Override
    public LoginVo login(LoginRequest request, String clientIp, HttpServletRequest httpRequest) {
        // 验证码校验（如果启用）
        if (Boolean.TRUE.equals(captchaProperties.getEnabled())) {
            captchaService.verify(request.getCaptchaUuid(), request.getCaptchaCode());
        }

        // 根据用户名获取登录用户信息
        ApiResult<LoginUser> userNameResult = userClient.getLoginUserByUserName(request.getUsername());
        LoginUser loginUser = userNameResult.getData();
        if (Objects.isNull(loginUser)) {
            log.warn("根据用户名查询登录用户失败，username: {}", request.getUsername());
            throw new BusinessException("用户不存在");
        }

        // 验证用户状态
        if (loginUser.getStatus() == null) {
            log.warn("用户状态异常，username: {}, status: null", request.getUsername());
            throw new BusinessException("用户状态异常，无法登录");
        }
        if (loginUser.getStatus().equals(UserStatusEnum.FROZEN.getValue())) {
            log.warn("用户已被冻结，username: {}", request.getUsername());
            throw new BusinessException("用户已被冻结，无法登录");
        }
        if (loginUser.getStatus().equals(UserStatusEnum.LOGGED_OUT.getValue())) {
            log.warn("用户已注销，username: {}", request.getUsername());
            throw new BusinessException("用户已注销，无法登录");
        }

        // 验证密码
        checkLogin(LoginType.PASSWORD, request.getUsername(), () -> !passwordEncoder.matches(request.getPassword(), loginUser.getPassword()));

        // 获取设备类型（优先使用前端传递，降级使用 User-Agent 解析）
        String deviceType = DeviceUtils.getDeviceType(httpRequest, request.getDeviceType());
        log.info("识别设备类型，userId: {}, deviceType: {}", loginUser.getUserId(), deviceType);

        // 构建登录参数
        SaLoginParameter loginParameter = new SaLoginParameter()
                // 设置设备类型
                .setDeviceType(deviceType)
                // 设置登录 token 有效期，后台登录 30 分钟，移动端登录 1 天
                .setTimeout(request.getClientId().equalsIgnoreCase(ClientTypeEnum.MOBILE_ADMIN.getCode())
                        ? Duration.ofMinutes(30).getSeconds()
                        : Duration.ofDays(1).getSeconds())
                // 设置登录 token 最低活跃频率，单位：秒（默认 5 分钟）
                .setActiveTimeout(Duration.ofMinutes(5).getSeconds())
                // 允许多设备并发登录
                .setIsConcurrent(true)
                // 设置客户端 ID 到 Extra 中，用于后续权限校验
                .setExtra(LoginHelper.CLIENT_KEY, request.getClientId());

        // [记住我] 模式：延长 Token 有效期
        if (Boolean.TRUE.equals(request.getRememberMe())) {
            // 7 天有效期
            loginParameter.setTimeout(Duration.ofDays(7).getSeconds());
            // 持久化 Cookie (临时 Cookie 在浏览器关闭时会自动删除，持久 Cookie 在重新打开后依然存在)
            loginParameter.setIsLastingCookie(true);
            log.info("启用 [记住我] 模式，userId: {}, timeout: 7 days", loginUser.getUserId());
        }

        // 执行登录（Sa-Token）生成 Token
        LoginHelper.login(loginUser, loginParameter);

        // 构建登录响应 VO
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(request.getClientId());

        // 异步记录登录日志
        String userAgent = ServletUtils.getUserAgent(httpRequest);
        loginAsyncService.recordLoginLog(
                loginUser.getUserId(),
                loginUser.getUsername(),
                clientIp,
                userAgent,
                deviceType,
                Convert.toInt(SystemStatusConstants.NORMAL),
                null
        );

        // 异步更新最后登录信息
        loginAsyncService.updateLastLoginInfo(loginUser.getUserId(), LocalDateTime.now(), clientIp);

        // 返回登录响应 VO
        return loginVo;
    }

    /**
     * 检查登录状态
     *
     * @param loginType 登录类型
     * @param username  用户名
     * @param supplier  登录状态检查逻辑
     */
    @Override
    public void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier) {
        if (loginType.equals(LoginType.PASSWORD)) {
            Integer maxRetryCount = userPasswordProperties.getMaxRetryCount();
            Integer lockTime = userPasswordProperties.getLockTime();

            // 检查密码登录错误次参数
            String loginErrorCountCacheKey = SystemRedisKeyConstants.Login.buildLoginErrorCountCacheKey(username);
            int loginErrorCount = redisService.string().get(loginErrorCountCacheKey, Integer.class);

            // 如果用户登录错误次数超过最大次数，锁定用户
            if (loginErrorCount >= maxRetryCount) {
                throw new BusinessException(StrUtil.format("密码错误次数超过最大次数，请 %s 分钟后重试", lockTime));
            }

            // 密码比对: supplier.get() 返回 true 表示登录失败
            if (supplier.get()) {
                // 增加登录错误次数
                loginErrorCount++;

                // 缓存登录错误次数
                redisService.string().set(loginErrorCountCacheKey, loginErrorCount, Duration.ofMinutes(lockTime));

                // 达到规定错误次数, 锁定用户
                if (loginErrorCount >= maxRetryCount) {
                    throw new BusinessException(StrUtil.format("密码错误次数超过最大次数，请 %s 分钟后重试", lockTime));
                } else {
                    // 没有达到规定次数，提示密码错误次数
                    throw new BusinessException(StrUtil.format("密码错误，您还有 %s 次尝试机会", maxRetryCount - loginErrorCount));
                }
            }

            // 登录成功，删除登录错误次数缓存
            redisService.delete(loginErrorCountCacheKey);
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

}

