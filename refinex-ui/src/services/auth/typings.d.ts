declare namespace AUTH {
  /** 后端统一响应格式 */
  interface ApiResult<T = any> {
    code: number;
    message: string;
    data: T;
    success: boolean;
  }

  /** 验证码生成响应 */
  interface CaptchaResponse {
    /** 验证码UUID */
    uuid: string;
    /** 验证码图片Base64 */
    image: string;
    /** 验证码过期时间 */
    expireSeconds: number;
  }

  /** 登录请求参数 */
  interface LoginRequest {
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
  interface LoginResponse {
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
  interface RegisterRequest {
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
  interface SendEmailCodeRequest {
    /** 邮箱地址 */
    email: string;
    /** 验证码类型：RESET_PASSWORD-重置密码，REGISTER-注册 */
    codeType: 'RESET_PASSWORD' | 'REGISTER';
  }

  /** 重置密码请求 */
  interface ResetPasswordRequest {
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
  interface CurrentUser {
    userId: number;
    username: string;
    nickname: string;
    email: string;
    avatar?: string;
    status: number;
    roles?: string[];
    permissions?: string[];
  }
}
