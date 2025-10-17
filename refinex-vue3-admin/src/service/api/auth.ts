import { request } from '../request';

/** Login request params */
export interface LoginRequest {
  loginType: 1 | 2; // 1=密码登录, 2=邮箱登录
  username?: string;
  email?: string;
  password: string;
  captchaUuid?: string;
  captchaCode?: string;
  clientId?: string;
  deviceType?: string;
  rememberMe?: boolean;
}

/** Login response */
export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  expireIn: number;
  refreshExpireIn: number;
  clientId: string;
}

/** Captcha response */
export interface CaptchaResponse {
  uuid: string;
  image: string; // base64 image (data:image/png;base64,xxx)
  expireSeconds: number;
}

/**
 * Generate captcha
 */
export function fetchGenerateCaptcha() {
  return request<CaptchaResponse>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/captcha/generate',
    method: 'get'
  });
}

/**
 * Login
 *
 * @param data Login request data
 */
export function fetchLogin(data: LoginRequest) {
  return request<LoginResponse>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/login',
    method: 'post',
    data
  });
}

/** Get user info */
export function fetchGetUserInfo() {
  return request<Api.Auth.UserInfo>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/getUserInfo'
  });
}

/**
 * Register user
 *
 * @param data Register request data
 */
export function fetchRegister(data: any) {
  return request<boolean>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/register',
    method: 'post',
    data
  });
}

/**
 * Logout
 */
export function fetchLogout() {
  return request<void>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/logout',
    method: 'post'
  });
}

/**
 * Refresh token
 *
 * @param refreshToken Refresh token
 */
export function fetchRefreshToken(refreshToken: string) {
  return request<LoginResponse>({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/refreshToken',
    method: 'post',
    data: {
      refreshToken
    }
  });
}

/**
 * return custom backend error
 *
 * @param code error code
 * @param msg error message
 */
export function fetchCustomBackendError(code: string, msg: string) {
  return request({
    url: import.meta.env.VITE_BASE_MODELURL__AUTH + '/error',
    params: { code, msg }
  });
}
