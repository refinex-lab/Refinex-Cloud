import { createStyles } from 'antd-style';
import { history } from '@umijs/max';
import React, { useState, useEffect } from 'react';
import { Tooltip } from 'antd';
import aiWhiteIcon from '@/assets/images/ai/ai_white_icon.svg';

const useStyles = createStyles(({ token }) => ({
  entry: {
    marginTop: 8,
    padding: '10px',
    background: 'linear-gradient(135deg, #5098fe 0%, #c2dfff 100%)',
    borderRadius: '8px',
    cursor: 'pointer',
    transition: 'all 0.3s ease',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    position: 'relative',
    overflow: 'hidden',
    boxShadow: '0 2px 8px rgba(80, 152, 254, 0.2)',
    minHeight: '40px',

    '&::before': {
      content: '""',
      position: 'absolute',
      top: '50%',
      left: '50%',
      transform: 'translate(-50%, -50%)',
      width: '150%',
      height: '150%',
      background: 'radial-gradient(circle, rgba(255,255,255,0.15) 0%, transparent 70%)',
      pointerEvents: 'none',
    },

    '&:hover': {
      transform: 'translateY(-2px)',
      boxShadow: '0 4px 12px rgba(80, 152, 254, 0.4)',
    },

    '&:active': {
      transform: 'translateY(0)',
    },
  },
  // 展开状态的样式
  entryExpanded: {
    marginLeft: 8,
    marginRight: 8,
    paddingLeft: '16px',
    paddingRight: '16px',
    justifyContent: 'flex-start',
  },
  // 折叠状态的样式
  entryCollapsed: {
    width: '40px',
    height: '40px',
    padding: '0',
    marginLeft: 'auto',
    marginRight: 'auto',
    justifyContent: 'center',
  },
  icon: {
    width: '20px',
    height: '20px',
    position: 'relative',
    zIndex: 1,
    flexShrink: 0,
    filter: 'drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1))',
  },
  text: {
    color: '#fff',
    fontSize: '14px',
    fontWeight: 500,
    position: 'relative',
    zIndex: 1,
    whiteSpace: 'nowrap',
    overflow: 'hidden',
    textOverflow: 'ellipsis',
  },
}));

const AIAssistantEntry: React.FC = () => {
  const { styles, cx } = useStyles();
  const [isCollapsed, setIsCollapsed] = useState(false);

  // 监听侧边栏折叠状态
  useEffect(() => {
    const checkCollapsed = () => {
      const sider = document.querySelector('.ant-pro-sider');
      const collapsed = sider?.classList.contains('ant-pro-sider-collapsed');
      setIsCollapsed(!!collapsed);
    };

    // 初始检查
    checkCollapsed();

    // 使用 MutationObserver 监听 class 变化
    const sider = document.querySelector('.ant-pro-sider');
    if (!sider) return;

    const observer = new MutationObserver(checkCollapsed);
    observer.observe(sider, {
      attributes: true,
      attributeFilter: ['class'],
    });

    return () => observer.disconnect();
  }, []);

  const handleClick = () => {
    // 跳转到 AI 聊天页面（保留左侧菜单）
    history.push('/ai/chat');

    // 自动折叠侧边栏以获得更多空间
    setTimeout(() => {
      const collapseButton = document.querySelector('.ant-pro-sider-collapsed-button');
      const sider = document.querySelector('.ant-pro-sider');

      // 如果侧边栏未折叠，则折叠它
      if (collapseButton && sider && !sider.classList.contains('ant-pro-sider-collapsed')) {
        (collapseButton as HTMLElement).click();
      }
    }, 100);
  };

  const entryContent = (
    <div
      className={cx(
        styles.entry,
        isCollapsed ? styles.entryCollapsed : styles.entryExpanded,
      )}
      onClick={handleClick}
    >
      <img src={aiWhiteIcon} alt="AI" className={styles.icon} />
      {!isCollapsed && <span className={styles.text}>AI 助手</span>}
    </div>
  );

  // 折叠状态下显示 Tooltip
  if (isCollapsed) {
    return (
      <Tooltip title="AI 助手" placement="right">
        {entryContent}
      </Tooltip>
    );
  }

  return entryContent;
};

export default AIAssistantEntry;
