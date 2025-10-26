import { request } from '@umijs/max';
import type { ApiResponse } from '@/services/typings';
import type {
  CaptchaResponse,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  SendEmailCodeRequest,
  ResetPasswordRequest,
  CurrentUser,
} from './typings';

/**
 * 生成验证码
 * GET /refinex-auth/captcha/generate
 */
export async function generateCaptcha(): Promise<ApiResponse<CaptchaResponse>> {
  return request('/refinex-auth/captcha', {
    method: 'GET',
  });
}

/**
 * 用户登录
 * POST /refinex-auth/auth/login
 */
export async function login(params: LoginRequest): Promise<ApiResponse<LoginResponse>> {
  return request('/refinex-auth/auth/login', {
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
export async function register(params: RegisterRequest): Promise<ApiResponse<void>> {
  return request('/refinex-auth/auth/register', {
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
export async function sendEmailVerifyCode(params: SendEmailCodeRequest): Promise<ApiResponse<void>> {
  return request('/refinex-auth/auth/sendEmailVerifyCode', {
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
export async function resetPassword(params: ResetPasswordRequest): Promise<ApiResponse<void>> {
  return request('/refinex-auth/auth/resetPassword', {
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
export async function logout(): Promise<ApiResponse<void>> {
  return request('/refinex-auth/auth/logout', {
    method: 'POST',
    // 跳过全局错误处理，登出操作无论成功失败都应该清理本地状态
    skipErrorHandler: true,
  });
}

/**
 * 获取当前用户信息
 * GET /refinex-platform/users/current
 */
export async function getCurrentUser(): Promise<ApiResponse<CurrentUser>> {
  return request('/refinex-platform/users/current', {
    method: 'GET',
  });
}

/**
 * 刷新令牌
 * POST /refinex-auth/auth/refresh
 */
export async function refreshToken(refreshToken: string): Promise<ApiResponse<LoginResponse>> {
  return request('/refinex-auth/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: { refreshToken },
  });
}
