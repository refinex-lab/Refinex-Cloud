import {
  LockOutlined,
  MailOutlined,
  SafetyCertificateOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormText,
} from '@ant-design/pro-components';
import {
  FormattedMessage,
  Helmet,
  SelectLang,
  useIntl,
  useModel,
  history,
} from '@umijs/max';
import { Alert, App, Button, Form, Steps } from 'antd';
import { createStyles } from 'antd-style';
import React, { useState } from 'react';
import { Footer } from '@/components';
import { ThemeSwitch } from '@/components/RightContent';
import { sendEmailVerifyCode, resetPassword } from '@/services/auth';
import Settings from '../../../../config/defaultSettings';

const useStyles = createStyles(({ token }) => {
  return {
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
    containerLight: {
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
      background: 'linear-gradient(180deg, #e0f2fe 0%, #f0f9ff 25%, #ffffff 50%, #f0f9ff 75%, #e0f2fe 100%)',
      position: 'relative',
    },
    containerDark: {
      display: 'flex',
      flexDirection: 'column',
      minHeight: '100vh',
      background: 'linear-gradient(180deg, #0a0e1a 0%, #131825 25%, #1a1f35 50%, #131825 75%, #0a0e1a 100%)',
      position: 'relative',
    },
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
    stepsContainer: {
      marginBottom: '32px',
    },
    backToLogin: {
      textAlign: 'center',
      marginTop: '24px',
    },
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
      '& .ant-steps': {
        '& .ant-steps-item-title': {
          color: '#e2e8f0 !important',
        },
        '& .ant-steps-item-description': {
          color: '#94a3b8 !important',
        },
        '& .ant-steps-item-wait .ant-steps-item-icon': {
          backgroundColor: 'transparent',
          borderColor: 'transparent',
          '& .ant-steps-icon': {
            color: '#94a3b8',
          },
        },
        '& .ant-steps-item-process .ant-steps-item-icon': {
          backgroundColor: 'transparent',
          borderColor: 'transparent',
          '& .ant-steps-icon': {
            color: token.colorPrimary,
          },
        },
        '& .ant-steps-item-finish .ant-steps-item-icon': {
          backgroundColor: 'transparent',
          borderColor: 'transparent',
          '& .ant-steps-icon': {
            color: token.colorPrimary,
          },
        },
        '& .ant-steps-item-tail::after': {
          backgroundColor: '#475569',
        },
        '& .ant-steps-item-finish .ant-steps-item-tail::after': {
          backgroundColor: token.colorPrimary,
        },
      },
    },
    lightFormStyles: {
      '& .ant-form': {
        width: '100%',
      },
      '& .ant-form-item': {
        marginBottom: '20px',
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

const ForgotPasswordThemeSwitch = ({ isDark }: { isDark: boolean }) => {
  const { styles } = useStyles();
  return (
    <div className={isDark ? styles.themeSwitchDark : styles.themeSwitch}>
      <ThemeSwitch />
    </div>
  );
};

const ErrorMessage: React.FC<{ content: string }> = ({ content }) => {
  return (
    <Alert
      style={{ marginBottom: 24 }}
      message={content}
      type="error"
      showIcon
    />
  );
};

const SuccessMessage: React.FC<{ content: string }> = ({ content }) => {
  return (
    <Alert
      style={{ marginBottom: 24 }}
      message={content}
      type="success"
      showIcon
    />
  );
};

const ForgotPassword: React.FC = () => {
  const [currentStep, setCurrentStep] = useState(0);
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const { initialState } = useModel('@@initialState');
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

  const handleSendCode = async (values: { email: string }) => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const response = await sendEmailVerifyCode({
        email: values.email,
        codeType: 'RESET_PASSWORD',
      });
      if (response.success) {
        setEmail(values.email);
        setCurrentStep(1);
        setSuccess('验证码已发送到您的邮箱，请查收');
        message.success('验证码发送成功');
      } else {
        setError(response.message || intl.formatMessage({
          id: 'pages.forgotPassword.sendCodeFailed',
          defaultMessage: '发送验证码失败，请重试',
        }));
      }
    } catch (error) {
      console.error('Failed to send verification code:', error);
      setError('网络错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (values: {
    verifyCode: string;
    newPassword: string;
    confirmPassword: string;
  }) => {
    if (values.newPassword !== values.confirmPassword) {
      setError('两次输入的密码不一致');
      return;
    }
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const response = await resetPassword({
        email,
        emailCode: values.verifyCode,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword,
      });
      if (response.success) {
        setCurrentStep(2);
        setSuccess('密码重置成功，请使用新密码登录');
        message.success('密码重置成功');
      } else {
        setError(response.message || '密码重置失败，请重试');
      }
    } catch (error) {
      console.error('密码重置失败:', error);
      setError('网络错误，请重试');
    } finally {
      setLoading(false);
    }
  };

  const goToLogin = () => {
    history.push('/user/login');
  };

  const restart = () => {
    setCurrentStep(0);
    setEmail('');
    setError('');
    setSuccess('');
    form.resetFields();
  };

  const steps = [
    {
      title: intl.formatMessage({
        id: 'pages.forgotPassword.step.verifyEmail',
        defaultMessage: '验证邮箱',
      }),
      icon: <MailOutlined />,
    },
    {
      title: intl.formatMessage({
        id: 'pages.forgotPassword.step.verifyIdentity',
        defaultMessage: '验证身份',
      }),
      icon: <SafetyCertificateOutlined />,
    },
    {
      title: intl.formatMessage({
        id: 'pages.forgotPassword.step.resetPassword',
        defaultMessage: '重置密码',
      }),
      icon: <LockOutlined />,
    },
  ];

  const renderStepContent = () => {
    switch (currentStep) {
      case 0:
        return (
          <LoginForm
            contentStyle={{
              width: '100%',
              padding: 0,
            }}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.forgotPassword.sendCode',
                  defaultMessage: '发送验证码',
                }),
              },
              submitButtonProps: {
                loading,
                size: 'large',
              },
            }}
            logo={false}
            title={false}
            subTitle={false}
            onFinish={handleSendCode}
            form={form}
          >
            <ProFormText
              name="email"
              fieldProps={{
                size: 'large',
                prefix: <MailOutlined />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.forgotPassword.email.placeholder',
                defaultMessage: '请输入您的邮箱地址',
              })}
              rules={[
                {
                  required: true,
                  message: '请输入邮箱地址！',
                },
                {
                  type: 'email',
                  message: '邮箱格式不正确！',
                },
              ]}
            />
          </LoginForm>
        );
      case 1:
        return (
          <LoginForm
            contentStyle={{
              width: '100%',
              padding: 0,
            }}
            submitter={{
              searchConfig: {
                submitText: intl.formatMessage({
                  id: 'pages.forgotPassword.resetPassword',
                  defaultMessage: '重置密码',
                }),
              },
              submitButtonProps: {
                loading,
                size: 'large',
              },
            }}
            logo={false}
            title={false}
            subTitle={false}
            onFinish={handleResetPassword}
            form={form}
          >
            <div style={{ marginBottom: 16, color: isDark ? '#94a3b8' : '#64748b' }}>
              验证码已发送至：<strong>{email}</strong>
            </div>

            <ProFormText
              name="verifyCode"
              fieldProps={{
                size: 'large',
                prefix: <SafetyCertificateOutlined />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.forgotPassword.verifyCode.placeholder',
                defaultMessage: '请输入邮箱验证码',
              })}
              rules={[
                {
                  required: true,
                  message: '请输入验证码！',
                },
              ]}
            />

            <ProFormText.Password
              name="newPassword"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.forgotPassword.newPassword.placeholder',
                defaultMessage: '请输入新密码',
              })}
              rules={[
                {
                  required: true,
                  message: '请输入新密码！',
                },
                {
                  min: 6,
                  message: '密码至少6位字符！',
                },
              ]}
            />

            <ProFormText.Password
              name="confirmPassword"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined />,
              }}
              placeholder={intl.formatMessage({
                id: 'pages.forgotPassword.confirmPassword.placeholder',
                defaultMessage: '请确认新密码',
              })}
              rules={[
                {
                  required: true,
                  message: '请确认新密码！',
                },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('两次输入的密码不一致！'));
                  },
                }),
              ]}
            />

            <div style={{ textAlign: 'center', marginTop: 16 }}>
              <Button type="link" onClick={restart}>
{intl.formatMessage({
                  id: 'pages.forgotPassword.resendCode',
                  defaultMessage: '重新发送验证码',
                })}
              </Button>
            </div>
          </LoginForm>
        );
      case 2:
        return (
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontSize: '48px', color: '#52c41a', marginBottom: '16px' }}>
              ✓
            </div>
            <h3 style={{ color: isDark ? '#f8fafc' : '#0f172a', marginBottom: '16px' }}>
              密码重置成功
            </h3>
            <p style={{ color: isDark ? '#94a3b8' : '#64748b', marginBottom: '24px' }}>
              您的密码已成功重置，请使用新密码登录
            </p>
            <Button type="primary" size="large" onClick={goToLogin}>
              返回登录
            </Button>
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className={isDark ? styles.containerDark : styles.containerLight}>
      <Helmet>
        <title>
          {intl.formatMessage({
            id: 'pages.forgotPassword.title',
            defaultMessage: '忘记密码',
          })}
          {Settings.title && ` - ${Settings.title}`}
        </title>
      </Helmet>

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

      <ForgotPasswordThemeSwitch isDark={isDark} />
      <Lang isDark={isDark} />

      <div className={styles.contentWrapper}>
        <div className={isDark ? styles.formContainerDark : styles.formContainerLight}>
          <div className={isDark ? styles.darkFormStyles : styles.lightFormStyles}>
            <h2 className={`${styles.formTitle} ${isDark ? styles.formTitleDark : ''}`}>
              {intl.formatMessage({
                id: 'pages.forgotPassword.title',
                defaultMessage: '忘记密码',
              })}
            </h2>

            {currentStep < 2 && (
              <div className={styles.stepsContainer}>
                <Steps
                  current={currentStep}
                  items={steps}
                  size="small"
                />
              </div>
            )}

            {error && <ErrorMessage content={error} />}
            {success && <SuccessMessage content={success} />}

            {renderStepContent()}

            {currentStep < 2 && (
              <div className={styles.backToLogin}>
                <Button type="link" onClick={goToLogin}>
                  <FormattedMessage
                    id="pages.forgotPassword.backToLogin"
                    defaultMessage="返回登录"
                  />
                </Button>
              </div>
            )}
          </div>
        </div>
      </div>

      <div className={isDark ? styles.footerDark : styles.footerLight}>
        <Footer />
      </div>
    </div>
  );
};

export default ForgotPassword;
