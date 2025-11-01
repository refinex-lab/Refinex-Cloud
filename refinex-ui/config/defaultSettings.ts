import type {ProLayoutProps} from '@ant-design/pro-components';

/**
 * 全局布局配置
 */
const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
} = {
  // 导航主题
  navTheme: 'light',
  // 主色调
  colorPrimary: '#1890ff',
  // 布局模式
  layout: 'mix',
  // 内容宽度
  contentWidth: 'Fluid',
  // 是否固定 Header
  fixedHeader: false,
  // 是否固定 Siderbar
  fixSiderbar: true,
  // 是否显示 Footer
  footerRender: false,
  // 是否开启弱色模式
  colorWeak: false,
  // 标题
  title: '知识工坊',
  // 是否拆分菜单
  splitMenus: false,
  // Siderbar 菜单类型
  siderMenuType: "sub",
  // 是否开启 PWA
  pwa: true,
  // logo 图片地址
  logo: '/logo.svg',
  // iconfont 字体地址
  iconfontUrl: '',
  // token 配置
  token: {
    // 参见ts声明，demo 见文档，通过token 修改样式
    //https://procomponents.ant.design/components/layout#%E9%80%9A%E8%BF%87-token-%E4%BF%AE%E6%94%B9%E6%A0%B7%E5%BC%8F

    // Layout 的背景颜色将在 app.tsx 中根据主题模式动态设置
  },
};

export default Settings;
