// 导入通用类型
import type { ApiResponse } from '@/services/typings';

/** 后端统一响应格式（使用通用 ApiResponse） */
export type ApiResult<T = any> = ApiResponse<T>;

/** 验证码生成响应 */
export interface CaptchaResponse {
  /** 验证码UUID */
  uuid: string;
  /** 验证码图片Base64 */
  image: string;
  /** 验证码过期时间 */
  expireSeconds: number;
}

/** 登录请求参数 */
export interface LoginRequest {
  /** 登录类型：1-用户名登录，2-邮箱登录 */
  loginType: 1 | 2;
  /** 用户名 (loginType=1时使用) */
  username?: string;
  /** 邮箱 (loginType=2时使用) */
  email?: string;
  /** 密码 */
  password: string;
  /** 验证码UUID */
  captchaUuid: string;
  /** 验证码 */
  captchaCode: string;
}

/** 登录响应数据 */
export interface LoginResponse {
  /** 访问令牌 */
  accessToken: string;
  /** 刷新令牌 */
  refreshToken: string;
  /** 令牌过期时间 */
  expireIn: number;
  /** 刷新令牌过期时间 */
  refreshExpireIn: number;
  /** 客户端ID */
  clientId: string;
}

/** 注册请求参数 */
export interface RegisterRequest {
  /** 用户名 */
  username: string;
  /** 密码 */
  password: string;
  /** 邮箱 */
  email: string;
  /** 邮箱验证码 */
  verifyCode: string;
  /** 昵称 */
  nickname?: string;
}

/** 发送邮箱验证码请求 */
export interface SendEmailCodeRequest {
  /** 邮箱地址 */
  email: string;
  /** 验证码类型：RESET_PASSWORD-重置密码，REGISTER-注册 */
  codeType: 'RESET_PASSWORD' | 'REGISTER';
}

/** 重置密码请求 */
export interface ResetPasswordRequest {
  /** 邮箱地址 */
  email: string;
  /** 邮箱验证码 */
  emailCode: string;
  /** 新密码 */
  newPassword: string;
  /** 确认新密码 */
  confirmPassword: string;
}

/** 当前用户信息 */
export interface CurrentUser {
  userId: number;
  username: string;
  nickname: string;
  avatar?: string;
  sex?: 'male' | 'female' | 'other';
  email?: string; // 已脱敏
  mobile?: string; // 已脱敏
  userStatus: number;
  userType?: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  roles?: string[];
  permissions?: string[];
}

// 为了向后兼容，保留 AUTH 命名空间（全局声明）
declare global {
  namespace AUTH {
    export type ApiResult<T = any> = ApiResponse<T>;
    export type CaptchaResponse = import('@/services/auth/typings').CaptchaResponse;
    export type LoginRequest = import('@/services/auth/typings').LoginRequest;
    export type LoginResponse = import('@/services/auth/typings').LoginResponse;
    export type RegisterRequest = import('@/services/auth/typings').RegisterRequest;
    export type SendEmailCodeRequest = import('@/services/auth/typings').SendEmailCodeRequest;
    export type ResetPasswordRequest = import('@/services/auth/typings').ResetPasswordRequest;
    export type CurrentUser = import('@/services/auth/typings').CurrentUser;
  }
}

export {};
