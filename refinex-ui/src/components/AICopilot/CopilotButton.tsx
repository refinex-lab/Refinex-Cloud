import { Tooltip } from 'antd';
import { createStyles } from 'antd-style';
import React from 'react';
import { useIntl } from '@umijs/max';
import aiBlueIcon from '@/assets/images/ai/ai_blue_icon.svg';

interface CopilotButtonProps {
  onClick: () => void;
}

const useStyles = createStyles(({ token }) => ({
  floatingButton: {
    position: 'fixed',
    right: '24px',
    bottom: '24px',
    width: '56px',
    height: '56px',
    borderRadius: '50%',
    boxShadow: '0 4px 16px rgba(59, 130, 246, 0.4), 0 2px 8px rgba(0, 0, 0, 0.1)',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    zIndex: 1000,
    border: '2px solid rgba(255, 255, 255, 0.2)',
    backdropFilter: 'blur(8px)',

    '&:hover': {
      transform: 'translateY(-4px) scale(1.05)',
      boxShadow: '0 8px 24px rgba(59, 130, 246, 0.5), 0 4px 12px rgba(0, 0, 0, 0.15)'
    },

    '&:active': {
      transform: 'translateY(-2px) scale(1.02)',
    },

    // 添加脉冲动画
    '&::before': {
      content: '""',
      position: 'absolute',
      inset: '-4px',
      borderRadius: '50%',
      opacity: 0.3,
      animation: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
    },

    '@keyframes pulse': {
      '0%, 100%': {
        opacity: 0.3,
        transform: 'scale(1)',
      },
      '50%': {
        opacity: 0.1,
        transform: 'scale(1.1)',
      },
    },
  },
  icon: {
    width: '32px',
    height: '32px',
    position: 'relative',
    zIndex: 1,
    filter: 'drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1))',
  },
}));

const CopilotButton: React.FC<CopilotButtonProps> = ({ onClick }) => {
  const intl = useIntl();
  const { styles } = useStyles();

  return (
    <Tooltip
      title={
        <span>
          {intl.formatMessage({ id: 'component.aiCopilot.trigger' })}{' '}
          <kbd style={{ marginLeft: 4 }}>⌘K</kbd>
        </span>
      }
      placement="left"
    >
      <div className={styles.floatingButton} onClick={onClick}>
        <img src={aiBlueIcon} alt="AI Copilot" className={styles.icon} />
      </div>
    </Tooltip>
  );
};

export default CopilotButton;

