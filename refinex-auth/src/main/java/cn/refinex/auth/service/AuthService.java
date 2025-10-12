package cn.refinex.auth.service;

import cn.refinex.auth.domain.dto.request.LoginRequest;
import cn.refinex.auth.domain.vo.LoginVo;
import cn.refinex.common.enums.LoginType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.function.Supplier;

/**
 * 认证服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request        登录请求
     * @param clientIp       客户端 IP
     * @param httpRequest    HttpServletRequest 对象（用于获取 User-Agent）
     * @return 登录响应（包含 Token）
     */
    LoginVo login(LoginRequest request, String clientIp, HttpServletRequest httpRequest);

    /**
     * 检查登录状态
     *
     * @param loginType 登录类型
     * @param username  用户名
     * @param supplier  登录状态检查逻辑
     */
    void checkLogin(LoginType loginType, String username, Supplier<Boolean> supplier);

    /**
     * 用户登出
     */
    void logout();

}
