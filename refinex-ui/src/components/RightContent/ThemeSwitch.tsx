import {MoonOutlined, SunOutlined} from '@ant-design/icons';
import {useModel} from '@umijs/max';
import {createStyles} from 'antd-style';
import React, {useEffect, useState} from 'react';
import type {MenuProps} from 'antd';
import HeaderDropdown from '../HeaderDropdown';
import {executeThemeTransition, getRecommendedConfig} from '@/utils/themeTransition';

type ThemeMode = 'light' | 'dark';

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      display: 'inline-flex',
      alignItems: 'center',
      padding: '4px',
      fontSize: '18px',
      color: 'inherit',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      transition: 'all 0.3s',
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
    iconWrapper: {
      display: 'inline-flex',
      alignItems: 'center',
      justifyContent: 'center',
      width: '18px',
      height: '18px',
      position: 'relative',
      transition: 'transform 0.3s ease-in-out',
      '&:hover': {
        transform: 'rotate(20deg) scale(1.1)',
      },
    },
  };
});

export const ThemeSwitch: React.FC = () => {
  const { styles } = useStyles();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [currentThemeMode, setCurrentThemeMode] = useState<ThemeMode>('light');

  // 从 localStorage 获取保存的主题模式
  const getThemeMode = (): ThemeMode => {
    const saved = localStorage.getItem('themeMode') as ThemeMode;
    return saved || 'light';
  };

  // 获取实际应用的主题
  const getCurrentTheme = (): 'light' | 'realDark' => {
    const themeMode = currentThemeMode;
    return themeMode === 'dark' ? 'realDark' : 'light';
  };

  // 初始化主题模式
  useEffect(() => {
    setCurrentThemeMode(getThemeMode());
  }, []);

  const currentTheme = initialState?.settings?.navTheme || getCurrentTheme();

  // 应用主题（带过渡动画）
  const applyTheme = (themeMode: ThemeMode): void => {
    const newTheme = themeMode === 'dark' ? 'realDark' : 'light';

    // 使用 View Transitions API 或传统过渡效果
    // 使用 elegant 模式：600ms 优雅过渡动画
    executeThemeTransition(() => {
      setCurrentThemeMode(themeMode);

      setInitialState((preInitialState) => ({
        ...preInitialState,
        settings: {
          ...preInitialState?.settings,
          navTheme: newTheme,
        },
      }));

      // 保存主题模式到 localStorage
      localStorage.setItem('themeMode', themeMode);
    }, getRecommendedConfig('elegant'));
  };

  const menuItems: MenuProps['items'] = [
    {
      key: 'light',
      icon: <SunOutlined />,
      label: '亮色主题',
      onClick: () => applyTheme('light'),
    },
    {
      key: 'dark',
      icon: <MoonOutlined />,
      label: '暗色主题',
      onClick: () => applyTheme('dark'),
    },
  ];

  // 根据当前主题模式显示对应图标（带动画包裹）
  const getCurrentIcon = () => {
    const icon = currentTheme === 'light' ? <SunOutlined /> : <MoonOutlined />;
    return <span className={styles.iconWrapper}>{icon}</span>;
  };

  return (
    <HeaderDropdown
      menu={{
        items: menuItems,
        selectedKeys: [currentThemeMode],
      }}
      placement="bottomRight"
    >
      <div className={styles.action} title="切换主题">
        {getCurrentIcon()}
      </div>
    </HeaderDropdown>
  );
};
