import {
  CloseOutlined,
  ReloadOutlined,
  CloseCircleOutlined,
  VerticalLeftOutlined,
  VerticalRightOutlined,
  MinusCircleOutlined,
} from '@ant-design/icons';
import { Dropdown } from 'antd';
import { createStyles } from 'antd-style';
import type { MenuProps } from 'antd';
import React, { useCallback, useMemo } from 'react';
import { useIntl } from '@umijs/max';

const useStyles = createStyles(({ token }) => {
  const isDark = token.colorBgContainer === '#141414';
  
  return {
    contextMenuWrapper: {
      '.ant-dropdown': {
        '.ant-dropdown-menu': {
          borderRadius: '8px',
          boxShadow: isDark 
            ? '0 6px 16px 0 rgba(0, 0, 0, 0.72), 0 3px 6px -4px rgba(0, 0, 0, 0.68), 0 9px 28px 8px rgba(0, 0, 0, 0.60)'
            : '0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 9px 28px 8px rgba(0, 0, 0, 0.05)',
          padding: '6px',
          backdropFilter: 'blur(12px)',
          background: isDark ? 'rgba(30, 30, 30, 0.95)' : 'rgba(255, 255, 255, 0.95)',
          border: isDark ? '1px solid rgba(255, 255, 255, 0.08)' : '1px solid rgba(0, 0, 0, 0.06)',
        },
        '.ant-dropdown-menu-item': {
          borderRadius: '6px',
          padding: '8px 12px',
          margin: '2px 0',
          transition: 'all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1)',
          fontSize: '14px',
          lineHeight: '22px',
          
          '&:hover:not(.ant-dropdown-menu-item-disabled)': {
            background: isDark 
              ? 'linear-gradient(90deg, rgba(22, 119, 255, 0.15) 0%, rgba(22, 119, 255, 0.10) 100%)'
              : 'linear-gradient(90deg, rgba(22, 119, 255, 0.10) 0%, rgba(22, 119, 255, 0.05) 100%)',
            color: token.colorPrimary,
            
            '.ant-dropdown-menu-title-content': {
              color: token.colorPrimary,
            },
            '.anticon': {
              color: token.colorPrimary,
              transform: 'scale(1.1)',
            },
          },
          
          '&.ant-dropdown-menu-item-disabled': {
            opacity: 0.4,
            cursor: 'not-allowed',
          },
          
          '.anticon': {
            fontSize: '14px',
            marginRight: '10px',
            transition: 'all 0.2s cubic-bezier(0.645, 0.045, 0.355, 1)',
          },
        },
        '.ant-dropdown-menu-item-divider': {
          margin: '6px 0',
          background: isDark ? 'rgba(255, 255, 255, 0.12)' : 'rgba(0, 0, 0, 0.06)',
        },
      },
    },
  };
});

export interface ContextMenuProps {
  targetKey: string;
  children: React.ReactElement;
  onClose?: () => void;
  onRefresh?: () => void;
  onCloseOthers?: () => void;
  onCloseLeft?: () => void;
  onCloseRight?: () => void;
  onCloseAll?: () => void;
  disableCloseLeft?: boolean;
  disableCloseRight?: boolean;
  closable?: boolean;
}

const ContextMenu: React.FC<ContextMenuProps> = ({
  targetKey,
  children,
  onClose,
  onRefresh,
  onCloseOthers,
  onCloseLeft,
  onCloseRight,
  onCloseAll,
  disableCloseLeft = false,
  disableCloseRight = false,
  closable = true,
}) => {
  const intl = useIntl();
  const { styles } = useStyles();

  const handleMenuClick = useCallback<Required<MenuProps>['onClick']>(
    ({ key, domEvent }) => {
      domEvent.stopPropagation();
      switch (key) {
        case 'refresh':
          onRefresh?.();
          break;
        case 'close':
          onClose?.();
          break;
        case 'closeOthers':
          onCloseOthers?.();
          break;
        case 'closeLeft':
          onCloseLeft?.();
          break;
        case 'closeRight':
          onCloseRight?.();
          break;
        case 'closeAll':
          onCloseAll?.();
          break;
        default:
          break;
      }
    },
    [onClose, onRefresh, onCloseOthers, onCloseLeft, onCloseRight, onCloseAll],
  );

  const menuItems = useMemo<MenuProps['items']>(
    () => [
      {
        key: 'refresh',
        icon: <ReloadOutlined style={{ color: '#52c41a' }} />,
        label: intl.formatMessage({ id: 'component.tabs.refresh' }),
      },
      {
        type: 'divider',
      },
      {
        key: 'close',
        icon: <CloseOutlined style={{ color: '#ff4d4f' }} />,
        label: intl.formatMessage({ id: 'component.tabs.close' }),
        disabled: !closable,
      },
      {
        key: 'closeOthers',
        icon: <MinusCircleOutlined style={{ color: '#faad14' }} />,
        label: intl.formatMessage({ id: 'component.tabs.closeOthers' }),
      },
      {
        type: 'divider',
      },
      {
        key: 'closeLeft',
        icon: <VerticalRightOutlined />,
        label: intl.formatMessage({ id: 'component.tabs.closeLeft' }),
        disabled: disableCloseLeft,
      },
      {
        key: 'closeRight',
        icon: <VerticalLeftOutlined />,
        label: intl.formatMessage({ id: 'component.tabs.closeRight' }),
        disabled: disableCloseRight,
      },
      {
        type: 'divider',
      },
      {
        key: 'closeAll',
        icon: <CloseCircleOutlined style={{ color: '#ff4d4f' }} />,
        label: intl.formatMessage({ id: 'component.tabs.closeAll' }),
      },
    ],
    [intl, closable, disableCloseLeft, disableCloseRight],
  );

  return (
    <div className={styles.contextMenuWrapper}>
      <Dropdown
        menu={{ items: menuItems, onClick: handleMenuClick }}
        trigger={['contextMenu']}
      >
        {children}
      </Dropdown>
    </div>
  );
};

export default ContextMenu;

