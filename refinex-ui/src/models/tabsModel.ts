import { history } from '@umijs/max';
import { useState, useCallback } from 'react';

export interface TabItem {
  key: string;
  path: string;
  title: string;
  closable: boolean;
}

export default () => {
  const [tabs, setTabs] = useState<TabItem[]>([
    {
      key: '/dashboard/analysis',
      path: '/dashboard/analysis',
      title: 'menu.dashboard.analysis',
      closable: false, // 首页不可关闭
    },
  ]);
  const [activeKey, setActiveKey] = useState<string>('/dashboard/analysis');

  // 添加标签页
  const addTab = useCallback((tab: Omit<TabItem, 'closable'>) => {
    setTabs((prevTabs) => {
      const exists = prevTabs.find((t) => t.key === tab.key);
      if (!exists) {
        return [
          ...prevTabs,
          {
            ...tab,
            closable: tab.key !== '/dashboard/analysis', // 首页不可关闭
          },
        ];
      }
      return prevTabs;
    });
    setActiveKey(tab.key);
  }, []);

  // 移除标签页
  const removeTab = useCallback(
    (targetKey: string) => {
      setTabs((prevTabs) => {
        const targetIndex = prevTabs.findIndex((tab) => tab.key === targetKey);
        const newTabs = prevTabs.filter((tab) => tab.key !== targetKey);

        // 如果关闭的是当前激活的标签，需要切换到相邻标签
        if (targetKey === activeKey) {
          let newActiveKey = activeKey;
          if (newTabs.length > 0) {
            if (targetIndex === prevTabs.length - 1) {
              // 如果关闭的是最后一个，激活前一个
              newActiveKey = newTabs[targetIndex - 1]?.key || newTabs[0].key;
            } else {
              // 否则激活后一个
              newActiveKey = newTabs[targetIndex]?.key || newTabs[0].key;
            }
          }
          setActiveKey(newActiveKey);
          history.push(newActiveKey);
        }

        return newTabs;
      });
    },
    [activeKey],
  );

  // 关闭其他标签
  const closeOtherTabs = useCallback((targetKey: string) => {
    setTabs((prevTabs) => {
      return prevTabs.filter(
        (tab) => tab.key === targetKey || !tab.closable,
      );
    });
    setActiveKey(targetKey);
    history.push(targetKey);
  }, []);

  // 关闭左侧标签
  const closeLeftTabs = useCallback((targetKey: string) => {
    setTabs((prevTabs) => {
      const targetIndex = prevTabs.findIndex((tab) => tab.key === targetKey);
      return prevTabs.filter(
        (tab, index) => index >= targetIndex || !tab.closable,
      );
    });
  }, []);

  // 关闭右侧标签
  const closeRightTabs = useCallback((targetKey: string) => {
    setTabs((prevTabs) => {
      const targetIndex = prevTabs.findIndex((tab) => tab.key === targetKey);
      return prevTabs.filter(
        (tab, index) => index <= targetIndex || !tab.closable,
      );
    });
  }, []);

  // 关闭所有标签
  const closeAllTabs = useCallback(() => {
    setTabs((prevTabs) => {
      const unclosableTabs = prevTabs.filter((tab) => !tab.closable);
      if (unclosableTabs.length > 0) {
        const firstTab = unclosableTabs[0];
        setActiveKey(firstTab.key);
        history.push(firstTab.key);
      }
      return unclosableTabs;
    });
  }, []);

  // 切换标签
  const switchTab = useCallback((key: string) => {
    setActiveKey(key);
    history.push(key);
  }, []);

  // 刷新标签页（重新加载当前页面）
  const refreshTab = useCallback((key: string) => {
    // 触发页面重新加载，可以通过路由参数或其他方式实现
    window.location.reload();
  }, []);

  // 重新排序标签
  const reorderTabs = useCallback((newTabs: TabItem[]) => {
    setTabs(newTabs);
  }, []);

  return {
    tabs,
    activeKey,
    addTab,
    removeTab,
    closeOtherTabs,
    closeLeftTabs,
    closeRightTabs,
    closeAllTabs,
    switchTab,
    refreshTab,
    reorderTabs,
  };
};

