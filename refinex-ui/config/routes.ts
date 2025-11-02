/**
 * @name umi 的路由配置
 * @description 只支持 path,component,routes,redirect,wrappers,name,icon 的配置
 * @param path  path 只支持两种占位符配置，第一种是动态参数 :id 的形式，第二种是 * 通配符，通配符只能出现路由字符串的最后。
 * @param component 配置 location 和 path 匹配后用于渲染的 React 组件路径。可以是绝对路径，也可以是相对路径，如果是相对路径，会从 src/pages 开始找起。
 * @param routes 配置子路由，通常在需要为多个路径增加 layout 组件时使用。
 * @param redirect 配置路由跳转
 * @param wrappers 配置路由组件的包装组件，通过包装组件可以为当前的路由组件组合进更多的功能。 比如，可以用于路由级别的权限校验
 * @param name 配置路由的标题，默认读取国际化文件 menu.ts 中 menu.xxxx 的值，如配置 name 为 login，则读取 menu.ts 中 menu.login 的取值作为标题
 * @param icon 配置路由的图标，取值参考 https://ant.design/components/icon-cn， 注意去除风格后缀和大小写，如想要配置图标为 <StepBackwardOutlined /> 则取值应为 stepBackward 或 StepBackward，如想要配置图标为 <UserOutlined /> 则取值应为 user 或者 User
 * @doc https://umijs.org/docs/guides/routes
 */
export default [
  {
    path: '/user',
    layout: false,
    routes: [
      {
        path: '/user/login',
        layout: false,
        name: 'login',
        component: './user/login',
      },
      {
        path: '/user/forgot-password',
        layout: false,
        name: 'forgot-password',
        component: './user/ForgotPassword',
      },
      {
        path: '/user',
        redirect: '/user/login',
      },
      {
        name: 'register-result',
        icon: 'smile',
        path: '/user/register-result',
        component: './user/register-result',
      },
      {
        name: 'register',
        icon: 'smile',
        path: '/user/register',
        component: './user/register',
      },
      {
        component: '404',
        path: '/user/*',
      },
    ],
  },
  {
    path: '/dashboard',
    name: 'dashboard',
    icon: 'dashboard',
    routes: [
      {
        path: '/dashboard',
        redirect: '/dashboard/analysis',
      },
      {
        name: 'analysis',
        icon: 'smile',
        path: '/dashboard/analysis',
        component: './dashboard/analysis',
      },
      {
        name: 'monitor',
        icon: 'smile',
        path: '/dashboard/monitor',
        component: './dashboard/monitor',
      },
      {
        name: 'workplace',
        icon: 'smile',
        path: '/dashboard/workplace',
        component: './dashboard/workplace',
      },
    ],
  },
  {
    name: 'kb',
    icon: 'read',
    path: '/kb',
    routes: [
      {
        path: '/kb',
        redirect: '/kb/space',
      },
      {
        name: 'space',
        icon: 'appstore',
        path: '/kb/space',
        component: './kb/space',
      },
      {
        name: 'space-detail',
        path: '/kb/space/:spaceCode',
        component: './kb/space/[spaceCode]',
        hideInMenu: true,
      },
      {
        name: 'tag',
        icon: 'tags',
        path: '/kb/tag',
        component: './kb/tag',
      },
    ],
  },
  {
    name: 'kb-admin',
    icon: 'book',
    path: '/kb-admin',
    routes: [
      {
        path: '/kb-admin',
        redirect: '/kb-admin/space',
      },
      {
        name: 'space',
        icon: 'appstore',
        path: '/kb-admin/space',
        component: './kb-admin/space',
      },
      {
        name: 'space-detail',
        icon: 'folder',
        path: '/kb-admin/space/detail/:spaceId',
        component: './kb/space/detail/[spaceId]',
        hideInMenu: true,
      },
      {
        name: 'tag',
        icon: 'tags',
        path: '/kb-admin/tag',
        component: './kb-admin/tag',
      },
    ],
  },
  {
    name: 'ai-admin',
    icon: 'robot',
    path: '/ai',
    routes: [
      {
        path: '/ai',
        redirect: '/ai/model-config',
      },
      {
        name: 'model-config',
        icon: 'api',
        path: '/ai/model-config',
        component: './ai/admin/model-config',
      },
      {
        name: 'prompt-template',
        icon: 'fileText',
        path: '/ai/prompt-template',
        component: './ai/admin/prompt-template',
      },
    ],
  },
  {
    name: 'system',
    icon: 'setting',
    path: '/system',
    routes: [
      {
        path: '/system',
        redirect: '/system/user',
      },
      {
        name: 'user',
        icon: 'team',
        path: '/system/user',
        component: './system/user',
      },
      {
        name: 'role',
        icon: 'safety',
        path: '/system/role',
        component: './system/role',
      },
      {
        name: 'role.users',
        path: '/system/role/:id/users',
        component: './system/role/users',
        hideInMenu: true,
      },
      {
        name: 'dictionary',
        icon: 'book',
        path: '/system/dictionary',
        component: './system/dictionary',
      },
      {
        name: 'dictionary-data',
        path: '/system/dictionary/data/:id',
        component: './system/dictionary/data/[id]',
        hideInMenu: true,
      },
      {
        name: 'config',
        icon: 'control',
        path: '/system/config',
        component: './system/config',
      },
    ],
  },
  {
    name: 'logs',
    icon: 'history',
    path: '/logs',
    routes: [
      {
        path: '/logs',
        redirect: '/logs/operation',
      },
      {
        name: 'operation',
        icon: 'control',
        path: '/logs/operation',
        component: './logs/operation/index',
      },
      {
        name: 'login',
        icon: 'login',
        path: '/logs/login',
        component: './logs/login/index',
      },
    ],
  },
  {
    name: 'account',
    icon: 'user',
    path: '/account',
    routes: [
      {
        path: '/account',
        redirect: '/account/center',
      },
      {
        name: 'center',
        icon: 'smile',
        path: '/account/center',
        component: './account/center',
      },
      {
        name: 'settings',
        icon: 'smile',
        path: '/account/settings',
        component: './account/settings',
      },
    ],
  },
  {
    path: '/ai/chat',
    name: 'ai-chat',
    icon: 'message',
    component: './ai/chat',
    hideInMenu: true,
  },
  {
    path: '/',
    redirect: '/dashboard/analysis',
  },
  {
    component: '404',
    path: '/*',
  },
];
