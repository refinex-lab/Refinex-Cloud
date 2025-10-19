import {
  LockOutlined,
  MailOutlined,
  UserOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCheckbox,
  ProFormText,
} from '@ant-design/pro-components';
import {
  FormattedMessage,
  Helmet,
  SelectLang,
  useIntl,
  useModel,
} from '@umijs/max';
import { Alert, App, Tabs, Form } from 'antd';
import { createStyles } from 'antd-style';
import React, { useState } from 'react';
import { flushSync } from 'react-dom';
import { Footer, Captcha } from '@/components';
import { ThemeSwitch } from '@/components/RightContent';
import { login, getCurrentUser } from '@/services/auth';
import Settings from '../../../../config/defaultSettings';

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      marginLeft: '8px',
      color: 'rgba(0, 0, 0, 0.45)',
      fontSize: '24px',
      verticalAlign: 'middle',
      cursor: 'pointer',
      transition: 'color 0.3s',
      '&:hover': {
        color: token.colorPrimaryActive,
      },
    },
    lang: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      top: 16,
      borderRadius: token.borderRadius,
      zIndex: 100,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    langDark: {
      width: 42,
      height: 42,
      lineHeight: '42px',
      position: 'fixed',
      right: 16,
      top: 16,
      borderRadius: token.borderRadius,
      zIndex: 100,
      backgroundColor: 'rgba(148, 163, 184, 0.1)',
      ':hover': {
        backgroundColor: 'rgba(148, 163, 184, 0.2)',
      },
      '& .ant-dropdown-trigger': {
        color: '#e2e8f0 !important',
      },
      '& .anticon': {
        color: '#e2e8f0 !important',
      },
    },
    themeSwitch: {
      width: 42,
      height: 42,
      position: 'fixed',
      right: 70,
      top: 16,
      borderRadius: token.borderRadius,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      color: 'rgba(0, 0, 0, 0.65)',
      fontSize: '18px',
      cursor: 'pointer',
      transition: 'all 0.3s',
      zIndex: 100,
      ':hover': {
        backgroundColor: token.colorBgTextHover,
        color: token.colorPrimary,
      },
    },
    themeSwitchDark: {
      width: 42,
      height: 42,
      position: 'fixed',
      right: 70,
      top: 16,
      borderRadius: token.borderRadius,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      color: '#e2e8f0',
      fontSize: '18px',
      cursor: 'pointer',
      transition: 'all 0.3s',
      zIndex: 100,
      backgroundColor: 'rgba(148, 163, 184, 0.1)',
      ':hover': {
        backgroundColor: 'rgba(148, 163, 184, 0.2)',
        color: token.colorPrimary,
      },
    },
    // 亮色主题 - 浅蓝白色优雅渐变
    containerLight: {
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
      background: 'linear-gradient(180deg, #e0f2fe 0%, #f0f9ff 25%, #ffffff 50%, #f0f9ff 75%, #e0f2fe 100%)',
      position: 'relative',
    },
    // 暗色主题 - 深蓝黑色专业渐变
    containerDark: {
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
      background: 'linear-gradient(180deg, #0a0e1a 0%, #131825 25%, #1a1f35 50%, #131825 75%, #0a0e1a 100%)',
      position: 'relative',
    },
    // 品牌标识区域
    brandContainer: {
      position: 'absolute',
      top: '32px',
      left: '48px',
      display: 'flex',
      alignItems: 'center',
      gap: '12px',
      zIndex: 10,
      '@media (max-width: 768px)': {
        top: '20px',
        left: '20px',
        gap: '8px',
      },
    },
    brandLogo: {
      width: '40px',
      height: '40px',
      '@media (max-width: 768px)': {
        width: '32px',
        height: '32px',
      },
    },
    brandTextContainer: {
      display: 'flex',
      flexDirection: 'column',
      '@media (max-width: 768px)': {
        display: 'none',
      },
    },
    brandTitle: {
      fontSize: '22px',
      fontWeight: 600,
      lineHeight: '28px',
      margin: 0,
      color: '#1e3a8a',
      letterSpacing: '0.3px',
    },
    brandTitleDark: {
      color: '#e0f2fe',
    },
    brandSubtitle: {
      fontSize: '12px',
      lineHeight: '18px',
      margin: 0,
      color: '#64748b',
      letterSpacing: '0.2px',
    },
    brandSubtitleDark: {
      color: '#94a3b8',
    },
    // 主内容区域
    contentWrapper: {
      flex: '1',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '32px 20px',
      '@media (max-width: 768px)': {
        padding: '100px 20px 40px',
      },
    },
    // 登录表单容器 - 亮色模式
    formContainerLight: {
      width: '100%',
      maxWidth: '500px',
      backgroundColor: '#ffffff',
      borderRadius: '16px',
      padding: '40px 36px',
      boxShadow: '0 4px 20px rgba(30, 58, 138, 0.08), 0 2px 8px rgba(30, 58, 138, 0.04)',
      border: '1px solid rgba(226, 232, 240, 0.8)',
      '@media (max-width: 768px)': {
        maxWidth: '100%',
        padding: '32px 24px',
        borderRadius: '12px',
      },
    },
    // 登录表单容器 - 暗色模式
    formContainerDark: {
      width: '100%',
      maxWidth: '500px',
      backgroundColor: '#1e293b',
      borderRadius: '16px',
      padding: '40px 36px',
      boxShadow: '0 4px 20px rgba(0, 0, 0, 0.5), 0 2px 8px rgba(0, 0, 0, 0.3)',
      border: '1px solid rgba(148, 163, 184, 0.15)',
      '@media (max-width: 768px)': {
        maxWidth: '100%',
        padding: '32px 24px',
        borderRadius: '12px',
      },
    },
    // 表单标题样式
    formTitle: {
      fontSize: '26px',
      fontWeight: 600,
      color: '#0f172a',
      marginBottom: '24px',
      textAlign: 'center',
      letterSpacing: '-0.3px',
    },
    formTitleDark: {
      color: '#f8fafc',
    },
    // 其他登录方式区域 - 确保不换行
    otherLoginContainer: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      gap: '16px',
      marginTop: '20px',
    },
    otherLoginText: {
      fontSize: '14px',
      color: '#64748b',
      margin: 0,
    },
    otherLoginTextDark: {
      color: '#94a3b8',
    },
    otherLoginIcons: {
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      gap: '4px',
    },
    // 暗色主题下的表单样式优化
    darkFormStyles: {
      '& .ant-form': {
        width: '100%',
      },
      '& .ant-form-item': {
        marginBottom: '20px',
      },
      '& .ant-form-item-label > label': {
        color: '#e2e8f0 !important',
      },
      '& .ant-input': {
        backgroundColor: '#334155',
        borderColor: '#475569',
        color: '#f8fafc',
        '&:hover': {
          backgroundColor: '#334155',
          borderColor: '#64748b',
        },
        '&:focus': {
          backgroundColor: '#334155',
          borderColor: token.colorPrimary,
          boxShadow: `0 0 0 2px ${token.colorPrimary}20`,
        },
        '&::placeholder': {
          color: '#94a3b8',
        },
      },
      '& .ant-input-affix-wrapper': {
        backgroundColor: '#334155',
        borderColor: '#475569',
        '&:hover': {
          backgroundColor: '#334155',
          borderColor: '#64748b',
        },
        '&:focus, &.ant-input-affix-wrapper-focused': {
          backgroundColor: '#334155',
          borderColor: token.colorPrimary,
          boxShadow: `0 0 0 2px ${token.colorPrimary}20`,
        },
        '& input': {
          backgroundColor: 'transparent',
          color: '#f8fafc',
          '&::placeholder': {
            color: '#94a3b8',
          },
        },
        '& .anticon': {
          color: '#94a3b8',
        },
      },
      '& .ant-tabs': {
        marginBottom: '24px',
      },
      '& .ant-tabs-nav': {
        marginBottom: '20px',
      },
      '& .ant-tabs-tab': {
        color: '#94a3b8',
        fontSize: '15px',
        padding: '8px 0',
        '&:hover': {
          color: '#cbd5e1',
        },
        '&.ant-tabs-tab-active': {
          color: '#f8fafc',
          '& .ant-tabs-tab-btn': {
            color: '#f8fafc',
          },
        },
      },
      '& .ant-tabs-ink-bar': {
        backgroundColor: token.colorPrimary,
        height: '3px',
      },
      '& .ant-checkbox-wrapper': {
        color: '#e2e8f0',
        '& .ant-checkbox-inner': {
          backgroundColor: '#334155',
          borderColor: '#475569',
        },
        '&:hover .ant-checkbox-inner': {
          borderColor: token.colorPrimary,
        },
        '& .ant-checkbox-checked .ant-checkbox-inner': {
          backgroundColor: token.colorPrimary,
          borderColor: token.colorPrimary,
        },
      },
      '& .ant-btn-primary': {
        height: '44px',
        fontSize: '16px',
        fontWeight: 500,
        borderRadius: '8px',
      },
      '& .ant-btn-link': {
        color: token.colorPrimary,
        '&:hover': {
          color: token.colorPrimaryHover,
        },
      },
      '& .ant-alert': {
        backgroundColor: '#7f1d1d',
        borderColor: '#991b1b',
        '& .ant-alert-message': {
          color: '#fecaca',
        },
        '& .ant-alert-icon': {
          color: '#fca5a5',
        },
      },
      '& a': {
        color: token.colorPrimary,
        '&:hover': {
          color: token.colorPrimaryHover,
        },
      },
    },
    // 亮色主题表单样式
    lightFormStyles: {
      '& .ant-form': {
        width: '100%',
      },
      '& .ant-form-item': {
        marginBottom: '20px',
      },
      '& .ant-tabs': {
        marginBottom: '24px',
      },
      '& .ant-tabs-nav': {
        marginBottom: '20px',
      },
      '& .ant-tabs-tab': {
        fontSize: '15px',
        padding: '8px 0',
      },
      '& .ant-tabs-ink-bar': {
        height: '3px',
      },
      '& .ant-btn-primary': {
        height: '44px',
        fontSize: '16px',
        fontWeight: 500,
        borderRadius: '8px',
      },
      '& .ant-input, & .ant-input-affix-wrapper': {
        borderRadius: '8px',
      },
    },
    // 亮色主题下的操作图标
    actionIconsLight: {
      '& .anticon': {
        color: '#64748b',
        fontSize: '28px',
        transition: 'all 0.3s',
        '&:hover': {
          color: token.colorPrimary,
          transform: 'scale(1.1)',
        },
      },
    },
    // 暗色主题下的操作图标
    actionIconsDark: {
      '& .anticon': {
        color: '#94a3b8',
        fontSize: '28px',
        transition: 'all 0.3s',
        '&:hover': {
          color: token.colorPrimary,
          transform: 'scale(1.1)',
        },
      },
    },
    // Footer 样式优化
    footerLight: {
      '& .ant-pro-global-footer': {
        '& a, & span': {
          color: '#64748b',
        },
      },
    },
    footerDark: {
      '& .ant-pro-global-footer': {
        '& a, & span': {
          color: '#94a3b8',
        },
      },
      '& .ant-pro-global-footer-copyright': {
        color: '#94a3b8',
      },
    },
  };
});


const Lang = ({ isDark }: { isDark: boolean }) => {
  const { styles } = useStyles();

  return (
    <div className={isDark ? styles.langDark : styles.lang} data-lang>
      {SelectLang && <SelectLang />}
    </div>
  );
};

const LoginThemeSwitch = ({ isDark }: { isDark: boolean }) => {
  const { styles } = useStyles();

  return (
    <div className={isDark ? styles.themeSwitchDark : styles.themeSwitch}>
      <ThemeSwitch />
    </div>
  );
};

const LoginMessage: React.FC<{
  content: string;
}> = ({ content }) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};

const Login: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<{status?: string; type?: string; message?: string}>({});
  const [type, setType] = useState<string>('account');
  const [captchaUuid, setCaptchaUuid] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const { initialState, setInitialState } = useModel('@@initialState');
  const { styles } = useStyles();
  const { message } = App.useApp();
  const intl = useIntl();
  const [form] = Form.useForm();

  const getCurrentTheme = (): 'light' | 'realDark' => {
    const themeMode = localStorage.getItem('themeMode') as 'light' | 'dark' || 'light';
    return themeMode === 'dark' ? 'realDark' : 'light';
  };

  const currentTheme = initialState?.settings?.navTheme || getCurrentTheme();
  const isDark = currentTheme === 'realDark';

  const fetchUserInfo = async () => {
    try {
      const response = await getCurrentUser();
      if (response.success && response.data) {
        flushSync(() => {
          setInitialState((s) => ({
            ...s,
            currentUser: response.data,
          }));
        });
        return response.data;
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
    }
    return null;
  };

  const handleSubmit = async (values: AUTH.LoginRequest) => {
    if (!captchaUuid) {
      message.error(intl.formatMessage({
        id: 'pages.login.captcha.getFirst',
        defaultMessage: '请先获取验证码',
      }));
      return;
    }

    setLoading(true);
    try {
      const loginParams: AUTH.LoginRequest = {
        ...values,
        captchaUuid,
        loginType: type === 'email' ? 2 : 1, // 邮箱登录:2, 账户登录:1
      };

      const response = await login(loginParams);
      if (response.success && response.data) {
        const defaultLoginSuccessMessage = intl.formatMessage({
          id: 'pages.login.success',
          defaultMessage: '登录成功！',
        });
        message.success(defaultLoginSuccessMessage);

        // 存储token
        localStorage.setItem('access_token', response.data.accessToken);
        localStorage.setItem('refreshToken', response.data.refreshToken);
        localStorage.setItem('clientId', response.data.clientId);
        localStorage.setItem('expireIn', String(response.data.expireIn ?? ''));
        localStorage.setItem('refreshExpireIn', String(response.data.refreshExpireIn ?? ''));

        // 获取用户信息
        await fetchUserInfo();

        // 跳转
        const urlParams = new URL(window.location.href).searchParams;
        window.location.href = urlParams.get('redirect') || '/';
        return;
      } else {
        setUserLoginState({
          status: 'error',
          type: 'account',
          message: response.message || '登录失败'
        });
        message.error(response.message || '登录失败，请重试！');
      }
    } catch (error) {
      console.error('登录失败:', error);
      const defaultLoginFailureMessage = intl.formatMessage({
        id: 'pages.login.failure',
        defaultMessage: '登录失败，请重试！',
      });
      message.error(defaultLoginFailureMessage);
      setUserLoginState({
        status: 'error',
        type: 'account',
        message: '网络错误，请重试'
      });
    } finally {
      setLoading(false);
    }
  };

  const { status, type: loginType } = userLoginState;

  return (
    <div className={isDark ? styles.containerDark : styles.containerLight}>
      <Helmet>
        <title>
          {intl.formatMessage({
            id: 'menu.login',
            defaultMessage: '登录页',
          })}
          {Settings.title && ` - ${Settings.title}`}
        </title>
      </Helmet>

      {/* 品牌标识 */}
      <div className={styles.brandContainer}>
        <img
          src="/logo.svg"
          alt="logo"
          className={styles.brandLogo}
        />
        <div className={styles.brandTextContainer}>
          <h1 className={`${styles.brandTitle} ${isDark ? styles.brandTitleDark : ''}`}>
            知识工坊
          </h1>
          <p className={`${styles.brandSubtitle} ${isDark ? styles.brandSubtitleDark : ''}`}>
            Refinex Knowledge Workshop
          </p>
        </div>
      </div>

      {/* 主题切换和语言切换 */}
      <LoginThemeSwitch isDark={isDark} />
      <Lang isDark={isDark} />

      {/* 登录表单区域 */}
      <div className={styles.contentWrapper}>
        <div className={isDark ? styles.formContainerDark : styles.formContainerLight}>
          <div className={isDark ? styles.darkFormStyles : styles.lightFormStyles}>
            {/* 表单标题 */}
            <h2 className={`${styles.formTitle} ${isDark ? styles.formTitleDark : ''}`}>
              {intl.formatMessage({
                id: 'pages.login.welcomeTitle',
                defaultMessage: '欢迎登录',
              })}
            </h2>

            <LoginForm
              contentStyle={{
                width: '100%',
                padding: 0,
              }}
              logo={false}
              title={false}
              subTitle={false}
              initialValues={{
                autoLogin: true,
              }}
              actions={[]}
              onFinish={async (values) => {
                await handleSubmit(values as AUTH.LoginRequest);
              }}
              form={form}
              submitter={{
                searchConfig: {
                  submitText: intl.formatMessage({
                    id: 'pages.login.submit',
                    defaultMessage: '登录',
                  }),
                },
                submitButtonProps: {
                  loading,
                  size: 'large',
                },
              }}
            >
              <Tabs
                activeKey={type}
                onChange={setType}
                centered
                items={[
                  {
                    key: 'account',
                    label: intl.formatMessage({
                      id: 'pages.login.accountLogin.tab',
                      defaultMessage: '账户密码登录',
                    }),
                  },
                  {
                    key: 'email',
                    label: intl.formatMessage({
                      id: 'pages.login.emailLogin.tab',
                      defaultMessage: '邮箱密码登录',
                    }),
                  },
                ]}
              />

              {status === 'error' && (loginType === 'account' || loginType === 'email') && (
                <LoginMessage
                  content={userLoginState.message || intl.formatMessage({
                    id: 'pages.login.accountLogin.errorMessage',
                    defaultMessage: '用户名或密码错误',
                  })}
                />
              )}

              {type === 'account' && (
                <>
                  <ProFormText
                    name="username"
                    fieldProps={{
                      size: 'large',
                      prefix: <UserOutlined />,
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.username.placeholder',
                      defaultMessage: '请输入用户名',
                    })}
                    rules={[
                      {
                        required: true,
                        message: (
                          <FormattedMessage
                            id="pages.login.username.required"
                            defaultMessage="请输入用户名!"
                          />
                        ),
                      },
                    ]}
                  />
                  <ProFormText.Password
                    name="password"
                    fieldProps={{
                      size: 'large',
                      prefix: <LockOutlined />,
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.password.placeholder',
                      defaultMessage: '请输入密码',
                    })}
                    rules={[
                      {
                        required: true,
                        message: (
                          <FormattedMessage
                            id="pages.login.password.required"
                            defaultMessage="请输入密码！"
                          />
                        ),
                      },
                    ]}
                  />

                  {/* 验证码组件 */}
                  <Form.Item
                    label={intl.formatMessage({
                      id: 'pages.login.captcha.label',
                      defaultMessage: '验证码',
                    })}
                    required
                    style={{ marginBottom: 16 }}
                  >
                    <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
                      <div style={{ flex: 1 }}>
                        <ProFormText
                          name="captchaCode"
                          fieldProps={{
                            size: 'large',
                            placeholder: intl.formatMessage({
                              id: 'pages.login.captcha.placeholder',
                              defaultMessage: '请输入验证码',
                            }),
                          }}
                          rules={[
                            {
                              required: true,
                              message: intl.formatMessage({
                                id: 'pages.login.captcha.required',
                                defaultMessage: '请输入验证码！',
                              }),
                            },
                          ]}
                        />
                      </div>
                      <Captcha
                        onUuidChange={setCaptchaUuid}
                        disabled={loading}
                      />
                    </div>
                  </Form.Item>
                </>
              )}

              {type === 'email' && (
                <>
                  <ProFormText
                    name="email"
                    fieldProps={{
                      size: 'large',
                      prefix: <MailOutlined />,
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.email.placeholder',
                      defaultMessage: '请输入邮箱地址',
                    })}
                    rules={[
                      {
                        required: true,
                        message: (
                          <FormattedMessage
                            id="pages.login.email.required"
                            defaultMessage="请输入邮箱地址!"
                          />
                        ),
                      },
                      {
                        type: 'email',
                        message: (
                          <FormattedMessage
                            id="pages.login.email.invalid"
                            defaultMessage="邮箱格式不正确!"
                          />
                        ),
                      },
                    ]}
                  />
                  <ProFormText.Password
                    name="password"
                    fieldProps={{
                      size: 'large',
                      prefix: <LockOutlined />,
                    }}
                    placeholder={intl.formatMessage({
                      id: 'pages.login.password.placeholder',
                      defaultMessage: '请输入密码',
                    })}
                    rules={[
                      {
                        required: true,
                        message: (
                          <FormattedMessage
                            id="pages.login.password.required"
                            defaultMessage="请输入密码！"
                          />
                        ),
                      },
                    ]}
                  />

                  {/* 验证码组件 */}
                  <Form.Item
                    label={intl.formatMessage({
                      id: 'pages.login.captcha.label',
                      defaultMessage: '验证码',
                    })}
                    required
                    style={{ marginBottom: 16 }}
                  >
                    <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start' }}>
                      <div style={{ flex: 1 }}>
                        <ProFormText
                          name="captchaCode"
                          fieldProps={{
                            size: 'large',
                            placeholder: intl.formatMessage({
                              id: 'pages.login.captcha.placeholder',
                              defaultMessage: '请输入验证码',
                            }),
                          }}
                          rules={[
                            {
                              required: true,
                              message: intl.formatMessage({
                                id: 'pages.login.captcha.required',
                                defaultMessage: '请输入验证码！',
                              }),
                            },
                          ]}
                        />
                      </div>
                      <Captcha
                        onUuidChange={setCaptchaUuid}
                        disabled={loading}
                      />
                    </div>
                  </Form.Item>
                </>
              )}


              <div
                style={{
                  marginBottom: 24,
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                }}
              >
                <ProFormCheckbox noStyle name="autoLogin">
                  <FormattedMessage
                    id="pages.login.rememberMe"
                    defaultMessage="自动登录"
                  />
                </ProFormCheckbox>
                <a
                  onClick={() => {
                    window.location.href = '/user/forgot-password';
                  }}
                >
                  <FormattedMessage
                    id="pages.login.forgotPassword"
                    defaultMessage="忘记密码"
                  />
                </a>
              </div>

              {/* 注册链接 */}
              <div
                style={{
                  marginTop: 32,
                  textAlign: 'center',
                  paddingTop: 16,
                  borderTop: `1px solid ${isDark ? 'rgba(148, 163, 184, 0.15)' : 'rgba(226, 232, 240, 0.8)'}`,
                }}
              >
                <span style={{ color: isDark ? '#94a3b8' : '#64748b' }}>
                  <FormattedMessage
                    id="pages.login.noAccount"
                    defaultMessage="没有账号？"
                  />
                </span>
                <a
                  onClick={() => {
                    window.location.href = '/user/register';
                  }}
                  style={{ marginLeft: 8 }}
                >
                  <FormattedMessage
                    id="pages.login.register"
                    defaultMessage="立即注册"
                  />
                </a>
              </div>
            </LoginForm>
          </div>
        </div>
      </div>

      <div className={isDark ? styles.footerDark : styles.footerLight}>
        <Footer/>
      </div>
    </div>
  );
};

export default Login;
