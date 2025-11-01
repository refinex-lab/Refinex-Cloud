import { LinkOutlined } from '@ant-design/icons';
import type { Settings as LayoutSettings } from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RequestConfig, RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import React from 'react';
import avatarMale from '@/assets/images/user/avatar_male.png';
import avatarFemale from '@/assets/images/user/avatar_female.png';
import {
  AvatarDropdown,
  AvatarName,
  Footer,
  Question,
  SelectLang,
  ThemeSwitch,
} from '@/components';
import CopilotButton from '@/components/AICopilot/CopilotButton';
import LayoutWrapper from '@/components/AICopilot/LayoutWrapper';
import AIAssistantEntry from '@/components/AIAssistantEntry';
import { getCurrentUser } from '@/services/auth';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';
import '@ant-design/v5-patch-for-react-19';

// 是否测试环境
const isDev = process.env.NODE_ENV === 'development' || process.env.CI;
// 登录地址路由
const loginPath = '/user/login';

/**
 * 获取初始化状态
 *
 * @see https://umijs.org/docs/api/runtime-config#getinitialstate
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: AUTH.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<AUTH.CurrentUser | undefined>;
}> {
  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const msg = await getCurrentUser();
      const apiUser = msg.data as AUTH.CurrentUser | undefined;
      const resolvedAvatar = !apiUser?.avatar
        ? apiUser?.sex === 'female'
          ? avatarFemale
          : avatarMale
        : apiUser.avatar;
      const normalizedUser = apiUser
        ? { ...apiUser, avatar: resolvedAvatar }
        : undefined;
      // 将当前用户缓存，便于页面级快速读取
      localStorage.setItem('current_user', JSON.stringify(normalizedUser || {}));
      return normalizedUser;
    } catch (_error) {
      history.push(loginPath);
    }
    return undefined;
  };

  // 从 localStorage 读取保存的主题模式
  const getThemeFromMode = () => {
    const themeMode = localStorage.getItem('themeMode') as 'light' | 'dark';
    if (!themeMode) {
      return 'light';
    }
    return themeMode === 'dark' ? 'realDark' : 'light';
  };

  const settingsWithTheme = {
    ...defaultSettings,
    navTheme: getThemeFromMode() as 'light' | 'realDark',
  };

  // 如果不是登录页面，执行
  const { location } = history;
  if (
    ![loginPath, '/user/register', '/user/register-result'].includes(
      location.pathname,
    )
  ) {
    const currentUser = await fetchUserInfo();
    return {
      fetchUserInfo,
      currentUser,
      settings: settingsWithTheme as Partial<LayoutSettings>,
    };
  }
  return {
    fetchUserInfo,
    settings: settingsWithTheme as Partial<LayoutSettings>,
  };
}

// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({
  initialState,
  setInitialState,
}) => {
  return {
    actionsRender: () => [
      <Question key="doc" />,
      <SelectLang key="SelectLang" />,
      <ThemeSwitch key="ThemeSwitch" />,
      <CopilotButton key="Copilot" onClick={() => window.dispatchEvent(new Event('openCopilot'))} />,
    ],
    avatarProps: {
      src: initialState?.currentUser?.avatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown menu>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.nickname || initialState?.currentUser?.username,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    bgLayoutImgList: [
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
        left: 85,
        bottom: 100,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
        bottom: -68,
        right: -45,
        height: '303px',
      },
      {
        src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
        bottom: 0,
        left: 0,
        width: '331px',
      },
    ],
    links: isDev
      ? [
          <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
            <LinkOutlined />
            <span>OpenAPI 文档</span>
          </Link>,
        ]
      : [],
    menuHeaderRender: undefined,
    menuExtraRender: () => <AIAssistantEntry />,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <LayoutWrapper>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </LayoutWrapper>
      );
    },
    ...initialState?.settings,
    // 根据主题模式动态设置 token
    token: {
      ...defaultSettings.token,
      // 暗色模式使用深色背景，亮色模式使用白色背景
      bgLayout: initialState?.settings?.navTheme === 'realDark' ? '#141414' : '#ffffff',
    },
  };
};

/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request: RequestConfig = {
  // 给所有请求添加 /api 前缀，使代理 refinex-ui/config/proxy.ts 生效
  // 生产环境不会生效代理，这里使用三目判断下，非开发环境就保持 '/'
  baseURL: isDev ? '/api' : '/',
  ...errorConfig,
};
