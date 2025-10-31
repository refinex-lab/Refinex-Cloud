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
    path: '/form',
    icon: 'form',
    name: 'form',
    routes: [
      {
        path: '/form',
        redirect: '/form/basic-form',
      },
      {
        name: 'basic-form',
        icon: 'smile',
        path: '/form/basic-form',
        component: './form/basic-form',
      },
      {
        name: 'step-form',
        icon: 'smile',
        path: '/form/step-form',
        component: './form/step-form',
      },
      {
        name: 'advanced-form',
        icon: 'smile',
        path: '/form/advanced-form',
        component: './form/advanced-form',
      },
    ],
  },
  {
    path: '/list',
    icon: 'table',
    name: 'list',
    routes: [
      {
        path: '/list/search',
        name: 'search-list',
        component: './list/search',
        routes: [
          {
            path: '/list/search',
            redirect: '/list/search/articles',
          },
          {
            name: 'articles',
            icon: 'smile',
            path: '/list/search/articles',
            component: './list/search/articles',
          },
          {
            name: 'projects',
            icon: 'smile',
            path: '/list/search/projects',
            component: './list/search/projects',
          },
          {
            name: 'applications',
            icon: 'smile',
            path: '/list/search/applications',
            component: './list/search/applications',
          },
        ],
      },
      {
        path: '/list',
        redirect: '/list/table-list',
      },
      {
        name: 'table-list',
        icon: 'smile',
        path: '/list/table-list',
        component: './table-list',
      },
      {
        name: 'basic-list',
        icon: 'smile',
        path: '/list/basic-list',
        component: './list/basic-list',
      },
      {
        name: 'card-list',
        icon: 'smile',
        path: '/list/card-list',
        component: './list/card-list',
      },
    ],
  },
  {
    path: '/profile',
    name: 'profile',
    icon: 'profile',
    routes: [
      {
        path: '/profile',
        redirect: '/profile/basic',
      },
      {
        name: 'basic',
        icon: 'smile',
        path: '/profile/basic',
        component: './profile/basic',
      },
      {
        name: 'advanced',
        icon: 'smile',
        path: '/profile/advanced',
        component: './profile/advanced',
      },
    ],
  },
  {
    name: 'result',
    icon: 'CheckCircleOutlined',
    path: '/result',
    routes: [
      {
        path: '/result',
        redirect: '/result/success',
      },
      {
        name: 'success',
        icon: 'smile',
        path: '/result/success',
        component: './result/success',
      },
      {
        name: 'fail',
        icon: 'smile',
        path: '/result/fail',
        component: './result/fail',
      },
    ],
  },
  {
    name: 'exception',
    icon: 'warning',
    path: '/exception',
    routes: [
      {
        path: '/exception',
        redirect: '/exception/403',
      },
      {
        name: '403',
        icon: 'smile',
        path: '/exception/403',
        component: './exception/403',
      },
      {
        name: '404',
        icon: 'smile',
        path: '/exception/404',
        component: './exception/404',
      },
      {
        name: '500',
        icon: 'smile',
        path: '/exception/500',
        component: './exception/500',
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
    name: 'ai',
    icon: 'robot',
    path: '/ai',
    routes: [
      {
        path: '/ai',
        redirect: '/ai/chat',
      },
      {
        name: 'chat',
        icon: 'comment',
        path: '/ai/chat',
        component: './ai/chat',
      },
    ],
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
