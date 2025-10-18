import { MoonOutlined, SunOutlined } from '@ant-design/icons';
import { useModel } from '@umijs/max';
import { createStyles } from 'antd-style';
import React, { useEffect, useState } from 'react';
import { flushSync } from 'react-dom';
import type { MenuProps } from 'antd';
import { Dropdown } from 'antd';
import HeaderDropdown from '../HeaderDropdown';

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

  // 应用主题
  const applyTheme = (themeMode: ThemeMode): void => {
    const newTheme = themeMode === 'dark' ? 'realDark' : 'light';

    setCurrentThemeMode(themeMode);

    flushSync(() => {
      setInitialState((preInitialState) => ({
        ...preInitialState,
        settings: {
          ...preInitialState?.settings,
          navTheme: newTheme,
        },
      }));
    });

    // 保存主题模式到 localStorage
    localStorage.setItem('themeMode', themeMode);
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

  // 根据当前主题模式显示对应图标
  const getCurrentIcon = () => {
    return currentTheme === 'light' ? <SunOutlined /> : <MoonOutlined />;
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