import { request } from '@umijs/max';

/**
 * 生成验证码
 * GET /refinex-auth/captcha/generate
 */
export async function generateCaptcha() {
  return request<AUTH.ApiResult<AUTH.CaptchaResponse>>('/refinex-auth/captcha', {
    method: 'GET',
  });
}

/**
 * 用户登录
 * POST /refinex-auth/auth/login
 */
export async function login(params: AUTH.LoginRequest) {
  return request<AUTH.ApiResult<AUTH.LoginResponse>>('/refinex-auth/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: params,
    // 跳过全局错误处理，由页面自己处理错误
    skipErrorHandler: true,
  });
}

/**
 * 用户注册
 * POST /refinex-auth/auth/register
 */
export async function register(params: AUTH.RegisterRequest) {
  return request<AUTH.ApiResult<void>>('/refinex-auth/auth/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: params,
    // 跳过全局错误处理，由页面自己处理错误
    skipErrorHandler: true,
  });
}

/**
 * 发送邮箱验证码
 * POST /refinex-auth/auth/sendEmailVerifyCode
 */
export async function sendEmailVerifyCode(params: AUTH.SendEmailCodeRequest) {
  return request<AUTH.ApiResult<void>>('/refinex-auth/auth/sendEmailVerifyCode', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: params,
    // 跳过全局错误处理，由页面自己处理错误
    skipErrorHandler: true,
  });
}

/**
 * 重置密码
 * POST /refinex-auth/auth/resetPassword
 */
export async function resetPassword(params: AUTH.ResetPasswordRequest) {
  return request<AUTH.ApiResult<void>>('/refinex-auth/auth/resetPassword', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: params,
    // 跳过全局错误处理，由页面自己处理错误
    skipErrorHandler: true,
  });
}

/**
 * 用户登出
 * POST /refinex-auth/auth/logout
 */
export async function logout() {
  return request<AUTH.ApiResult<void>>('/refinex-auth/auth/logout', {
    method: 'POST',
  });
}

/**
 * 获取当前用户信息
 * GET /refinex-platform/users/current
 */
export async function getCurrentUser() {
  return request<AUTH.ApiResult<AUTH.CurrentUser>>('/refinex-platform/users/current', {
    method: 'GET',
  });
}

/**
 * 刷新令牌
 * POST /refinex-auth/auth/refresh
 */
export async function refreshToken(refreshToken: string) {
  return request<AUTH.ApiResult<AUTH.LoginResponse>>('/refinex-auth/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: { refreshToken },
  });
}
