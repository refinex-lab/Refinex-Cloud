import { CloseOutlined, HolderOutlined } from '@ant-design/icons';
import { Tabs } from 'antd';
import type { TabsProps } from 'antd';
import { createStyles } from 'antd-style';
import React, { useCallback, useMemo, useState, useEffect } from 'react';
import { useIntl, useLocation, useModel } from '@umijs/max';
import {
  DndContext,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from '@dnd-kit/core';
import {
  arrayMove,
  SortableContext,
  useSortable,
  horizontalListSortingStrategy,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import ContextMenu from './ContextMenu';

// 可拖拽的标签项组件
interface DraggableTabLabelProps {
  tabKey: string;
  title: string;
  closable: boolean;
  tabs: Array<{ key: string; title: string; closable: boolean }>;
  onClose: () => void;
  onRefresh: () => void;
  onCloseOthers: () => void;
  onCloseLeft: () => void;
  onCloseRight: () => void;
  onCloseAll: () => void;
  getTabTitle: (title: string) => string;
  className: string;
}

const DraggableTabLabel: React.FC<DraggableTabLabelProps> = ({
  tabKey,
  title,
  closable,
  tabs,
  onClose,
  onRefresh,
  onCloseOthers,
  onCloseLeft,
  onCloseRight,
  onCloseAll,
  getTabTitle,
  className,
}) => {
  const { attributes, listeners, setNodeRef } = useSortable({
    id: tabKey,
  });

  const tabIndex = tabs.findIndex((t) => t.key === tabKey);
  const disableCloseLeft =
    tabIndex === 0 || tabs.slice(0, tabIndex).every((t) => !t.closable);
  const disableCloseRight =
    tabIndex === tabs.length - 1 ||
    tabs.slice(tabIndex + 1).every((t) => !t.closable);

  return (
    <ContextMenu
      targetKey={tabKey}
      closable={closable}
      disableCloseLeft={disableCloseLeft}
      disableCloseRight={disableCloseRight}
      onClose={onClose}
      onRefresh={onRefresh}
      onCloseOthers={onCloseOthers}
      onCloseLeft={onCloseLeft}
      onCloseRight={onCloseRight}
      onCloseAll={onCloseAll}
    >
      <span ref={setNodeRef} className={className}>
        <HolderOutlined
          className="drag-handle"
          {...listeners}
          {...attributes}
        />
        <span className="tab-title">{getTabTitle(title)}</span>
      </span>
    </ContextMenu>
  );
};

const useStyles = createStyles(({ token, css, prefixCls }) => {
  const isDark = token.colorBgContainer === '#141414';
  return {
    wrapper: css`
      background: ${token.colorBgContainer};
      height: 100%;
      display: flex;
      align-items: center;
      overflow: hidden;
      transition: margin-left 0.3s cubic-bezier(0.645, 0.045, 0.355, 1),
                  padding-left 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
    `,
    tabsContainer: css`
      flex: 1;
      background: ${token.colorBgContainer};
      height: 100%;
      display: flex;
      align-items: center;
      padding-left: 120px;

      .${prefixCls}-tabs {
        margin: 0;
        height: 100%;
        width: 100%;
      }

      .${prefixCls}-tabs-nav {
        margin: 0 !important;
        padding: 0;
        background: transparent;
        height: 100%;

        &::before {
          border: none;
        }
      }

      .${prefixCls}-tabs-nav-wrap {
        height: 100%;
      }

      .${prefixCls}-tabs-nav-list {
        height: 100%;
        display: flex;
        align-items: center;
      }

      .${prefixCls}-tabs-tab {
        position: relative;
        background: transparent !important;
        border: none !important;
        border-radius: 0 !important;
        margin: 0 !important;
        padding: 8px 16px !important;
        font-size: 13px;
        transition: all 0.15s ease;

        /* 右侧分隔线 */
        &::after {
          content: '';
          position: absolute;
          right: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 1px;
          height: 16px;
          background: ${isDark ? 'rgba(255, 255, 255, 0.12)' : '#e8e8e8'};
        }

        /* 最后一个标签不显示分隔线 */
        &:last-child::after {
          display: none;
        }

        /* 激活标签后面的分隔线隐藏 */
        &.${prefixCls}-tabs-tab-active::after {
          display: none;
        }

        /* 激活标签前面的标签分隔线隐藏 */
        &.${prefixCls}-tabs-tab-active + .${prefixCls}-tabs-tab::before {
          display: none;
        }

        &:hover:not(.${prefixCls}-tabs-tab-active) {
          background: ${isDark ? 'rgba(255, 255, 255, 0.04)' : 'rgba(0, 0, 0, 0.02)'} !important;

          .${prefixCls}-tabs-tab-btn {
            color: ${isDark ? 'rgba(255, 255, 255, 0.88)' : 'rgba(0, 0, 0, 0.88)'};
          }

          .${prefixCls}-tabs-tab-remove {
            color: ${isDark ? 'rgba(255, 255, 255, 0.65)' : 'rgba(0, 0, 0, 0.65)'};
          }
        }

        &.${prefixCls}-tabs-tab-active {
          background: transparent !important;
          box-shadow: inset 0 -2px 0 0 #3B82F6;

          .${prefixCls}-tabs-tab-btn {
            color: #3B82F6 !important;
            font-weight: 500;
          }

          .${prefixCls}-tabs-tab-remove {
            color: #3B82F6 !important;

            &:hover {
              color: #2563eb !important;
              background-color: rgba(59, 130, 246, 0.1) !important;
            }
          }
        }
      }

      .${prefixCls}-tabs-tab-btn {
        color: ${isDark ? 'rgba(255, 255, 255, 0.65)' : 'rgba(0, 0, 0, 0.65)'};
        transition: color 0.15s ease;
        font-size: 13px;
      }

      .${prefixCls}-tabs-tab-remove {
        margin-left: 6px;
        margin-right: -2px;
        color: ${isDark ? 'rgba(255, 255, 255, 0.45)' : 'rgba(0, 0, 0, 0.45)'};
        font-size: 14px;
        opacity: 0.7;
        transition: all 0.15s ease;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 16px;
        height: 16px;
        border-radius: 3px;

        &:hover {
          opacity: 1;
          color: ${isDark ? 'rgba(255, 255, 255, 0.85)' : 'rgba(0, 0, 0, 0.75)'} !important;
          background-color: ${isDark ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.06)'} !important;
        }
      }

      /* 隐藏操作按钮 */
      .${prefixCls}-tabs-nav-operations {
        display: none !important;
      }

      /* 底部分隔线 */
      .${prefixCls}-tabs-ink-bar {
        display: none;
      }
    `,
    tabLabel: css`
      display: inline-flex;
      align-items: center;
      gap: 4px;
      max-width: 140px;

      .tab-title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 1.5;
      }

      .drag-handle {
        display: none;
        margin-right: 2px;
        color: ${token.colorTextTertiary};
        cursor: move;
        opacity: 0;
        transition: opacity 0.2s;
        font-size: 12px;
      }
    `,
    dragging: css`
      opacity: 0.5;
    `,
  };
});

// 可拖拽的标签节点
interface DraggableTabNodeProps {
  id: string;
  children: React.ReactNode;
}

const DraggableTabNode: React.FC<DraggableTabNodeProps> = ({ id, children }) => {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id,
  });

  const style: React.CSSProperties = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes}>
      {children}
    </div>
  );
};

const TabsLayout: React.FC = () => {
  const { styles } = useStyles();
  const intl = useIntl();
  const location = useLocation();
  const {
    tabs,
    activeKey,
    removeTab,
    closeOtherTabs,
    closeLeftTabs,
    closeRightTabs,
    closeAllTabs,
    switchTab,
    refreshTab,
    reorderTabs,
  } = useModel('tabsModel');

  // 拖拽传感器
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 8,
      },
    }),
  );

  // 处理拖拽结束
  const handleDragEnd = useCallback(
    (event: DragEndEvent) => {
      const { active, over } = event;

      if (over && active.id !== over.id) {
        const oldIndex = tabs.findIndex((tab) => tab.key === active.id);
        const newIndex = tabs.findIndex((tab) => tab.key === over.id);
        const newTabs = arrayMove(tabs, oldIndex, newIndex);
        reorderTabs(newTabs);
      }
    },
    [tabs, reorderTabs],
  );

  // 获取标签页的实际标题
  const getTabTitle = useCallback(
    (title: string) => {
      // 如果是国际化 key，进行翻译
      if (title.startsWith('menu.')) {
        return intl.formatMessage({ id: title });
      }
      return title;
    },
    [intl],
  );


  // 标签页切换
  const handleTabChange = useCallback(
    (key: string) => {
      switchTab(key);
    },
    [switchTab],
  );

  // 标签页关闭
  const handleTabEdit = useCallback(
    (targetKey: string | React.MouseEvent | React.KeyboardEvent) => {
      if (typeof targetKey === 'string') {
        const tab = tabs.find((t) => t.key === targetKey);
        if (tab?.closable) {
          removeTab(targetKey);
        }
      }
    },
    [removeTab, tabs],
  );

  // 渲染标签页标签
  const renderTabLabel = useCallback(
    (tab: { key: string; title: string; closable: boolean }) => {
      return (
        <DraggableTabLabel
          tabKey={tab.key}
          title={tab.title}
          closable={tab.closable}
          tabs={tabs}
          onClose={() => removeTab(tab.key)}
          onRefresh={() => refreshTab(tab.key)}
          onCloseOthers={() => closeOtherTabs(tab.key)}
          onCloseLeft={() => closeLeftTabs(tab.key)}
          onCloseRight={() => closeRightTabs(tab.key)}
          onCloseAll={closeAllTabs}
          getTabTitle={getTabTitle}
          className={styles.tabLabel}
        />
      );
    },
    [
      tabs,
      styles.tabLabel,
      getTabTitle,
      removeTab,
      refreshTab,
      closeOtherTabs,
      closeLeftTabs,
      closeRightTabs,
      closeAllTabs,
    ],
  );

  // 构建标签页项
  const tabItems = useMemo(
    () =>
      tabs.map((tab) => ({
        key: tab.key,
        label: renderTabLabel(tab),
        closable: tab.closable,
        closeIcon: (
          <CloseOutlined
            onClick={(e) => {
              e.stopPropagation();
              if (tab.closable) {
                removeTab(tab.key);
              }
            }}
          />
        ),
      })),
    [tabs, renderTabLabel, removeTab],
  );

  // 检测侧边栏宽度
  const [siderWidth, setSiderWidth] = useState(208);

  useEffect(() => {
    const updateSiderWidth = () => {
      // 查找 ProLayout 的侧边栏元素
      const sider = document.querySelector('.ant-pro-sider');
      if (sider) {
        const width = (sider as HTMLElement).offsetWidth;
        setSiderWidth(width);
      }
    };

    // 初始化
    updateSiderWidth();

    // 监听窗口大小变化
    window.addEventListener('resize', updateSiderWidth);

    // 使用 MutationObserver 监听侧边栏的变化
    const observer = new MutationObserver(updateSiderWidth);
    const sider = document.querySelector('.ant-pro-sider');
    if (sider) {
      observer.observe(sider, {
        attributes: true,
        attributeFilter: ['style', 'class'],
      });
    }

    return () => {
      window.removeEventListener('resize', updateSiderWidth);
      observer.disconnect();
    };
  }, []);

  if (tabs.length === 0) {
    return null;
  }

  return (
    <div className={styles.wrapper} style={{ marginLeft: -siderWidth, paddingLeft: siderWidth }}>
      <DndContext sensors={sensors} onDragEnd={handleDragEnd}>
        <SortableContext items={tabs.map(tab => tab.key)} strategy={horizontalListSortingStrategy}>
          <div className={styles.tabsContainer}>
            <Tabs
              hideAdd
              type="editable-card"
              activeKey={activeKey}
              items={tabItems}
              onChange={handleTabChange}
              onEdit={handleTabEdit}
            />
          </div>
        </SortableContext>
      </DndContext>
    </div>
  );
};

export default TabsLayout;

