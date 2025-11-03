/**
 * 主题提供者组件
 * 负责在 DOM 上应用暗色模式类名，使 CSS 选择器能够正常工作
 */
import { useModel } from '@umijs/max';
import { useEffect } from 'react';

export const ThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { initialState } = useModel('@@initialState');

  useEffect(() => {
    const isDark = initialState?.settings?.navTheme === 'realDark';
    const html = document.documentElement;

    // 方案1: 添加类名到 <html>
    if (isDark) {
      html.classList.add('realDark');
      html.classList.remove('light');
    } else {
      html.classList.add('light');
      html.classList.remove('realDark');
    }

    // 方案2: 同时添加 data-theme 属性（兼容性更好）
    html.setAttribute('data-theme', isDark ? 'dark' : 'light');

    // 方案3: 也在 body 上添加类名（某些组件可能需要）
    const body = document.body;
    if (isDark) {
      body.classList.add('realDark');
      body.classList.remove('light');
    } else {
      body.classList.add('light');
      body.classList.remove('realDark');
    }
  }, [initialState?.settings?.navTheme]);

  return <>{children}</>;
};

export default ThemeProvider;

