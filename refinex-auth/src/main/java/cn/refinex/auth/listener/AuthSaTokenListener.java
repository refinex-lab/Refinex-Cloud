package cn.refinex.auth.listener;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import cn.refinex.auth.service.LoginAsyncService;
import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.servlet.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 自定义 Sa-Token 事件监听器
 * <p>
 * 功能：监听用户登录事件，异步处理登录日志记录和最后登录信息更新
 * 参考: <a href="https://sa-token.cc/doc.html#/up/global-listener?id=%e5%85%a8%e5%b1%80%e4%be%a6%e5%90%ac%e5%99%a8">...</a>
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthSaTokenListener extends SaTokenListenerForSimple {

    private final LoginAsyncService loginAsyncService;

    /**
     * 每次登录时触发
     * <p>
     * 处理逻辑：
     * 1. 异步记录登录日志
     * 2. 异步更新最后登录信息
     * </p>
     *
     * @param loginType      登录类型
     * @param loginId        登录者ID
     * @param tokenValue     Token值
     * @param loginParameter 登录参数
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
        try {
            log.debug("Sa-Token 登录事件触发，loginId: {}, loginType: {}", loginId, loginType);

            // 从 Login Extra 中获取客户端 ID
            String clientId = loginParameter.getExtra(LoginHelper.CLIENT_KEY).toString();

            // 获取设备类型
            String deviceType = loginParameter.getDeviceType();

            // 获取客户端 IP
            String clientIp = ServletUtils.getClientIp();

            // 获取 User-Agent
            String userAgent = ServletUtils.getUserAgent();

            // 记录登录日志（成功）
            loginAsyncService.recordLoginLog(
                    Convert.toLong(loginId),
                    loginId.toString(),
                    clientIp,
                    userAgent,
                    deviceType,
                    Convert.toInt(SystemStatusConstants.NORMAL),
                    null
            );

            // 更新最后登录信息
            loginAsyncService.updateLastLoginInfo(
                    Convert.toLong(loginId),
                    java.time.LocalDateTime.now(),
                    clientIp
            );

            log.info("Sa-Token 登录事件处理完成，loginId: {}, clientId: {}, deviceType: {}",
                    loginId, clientId, deviceType);

        } catch (Exception e) {
            // 使用 try-catch 包裹不安全的代码，防止异常影响 Sa-Token 登录流程
            log.error("Sa-Token 登录事件处理失败，loginId: {}", loginId, e);
        }
    }

    /**
     * 每次注销时触发
     *
     * @param loginType  登录类型
     * @param loginId    登录者ID
     * @param tokenValue Token值
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        try {
            log.debug("Sa-Token 注销事件触发，loginId: {}, loginType: {}", loginId, loginType);

            // 这里可以添加注销日志记录等逻辑
            // 例如：记录注销日志、清理用户缓存等

            log.info("Sa-Token 注销事件处理完成，loginId: {}", loginId);

        } catch (Exception e) {
            log.error("Sa-Token 注销事件处理失败，loginId: {}", loginId, e);
        }
    }

    /**
     * 每次被踢下线时触发
     *
     * @param loginType  登录类型
     * @param loginId    登录者ID
     * @param tokenValue Token值
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        try {
            log.debug("Sa-Token 踢下线事件触发，loginId: {}, loginType: {}", loginId, loginType);

            // 这里可以添加踢下线日志记录等逻辑
            // 例如：记录被踢下线原因、通知用户等

            log.info("Sa-Token 踢下线事件处理完成，loginId: {}", loginId);

        } catch (Exception e) {
            log.error("Sa-Token 踢下线事件处理失败，loginId: {}", loginId, e);
        }
    }

    /**
     * 每次被顶下线时触发
     *
     * @param loginType  登录类型
     * @param loginId    登录者ID
     * @param tokenValue Token值
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        try {
            log.debug("Sa-Token 顶下线事件触发，loginId: {}, loginType: {}", loginId, loginType);

            // 这里可以添加顶下线日志记录等逻辑
            // 例如：记录被顶下线信息、发送通知等

            log.info("Sa-Token 顶下线事件处理完成，loginId: {}", loginId);

        } catch (Exception e) {
            log.error("Sa-Token 顶下线事件处理失败，loginId: {}", loginId, e);
        }
    }
}