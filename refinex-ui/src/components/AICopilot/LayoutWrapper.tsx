import React, { useEffect, useState } from 'react';
import AICopilot from './index';

interface LayoutWrapperProps {
  children: React.ReactNode;
}

const LayoutWrapper: React.FC<LayoutWrapperProps> = ({ children }) => {
  const [copilotOpen, setCopilotOpen] = useState(false);

  // 全局快捷键监听 (Cmd/Ctrl + K)
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      // Cmd/Ctrl + K
      if ((event.metaKey || event.ctrlKey) && event.key === 'k') {
        event.preventDefault();
        setCopilotOpen((prev) => !prev);
      }
      // ESC 键关闭
      if (event.key === 'Escape' && copilotOpen) {
        setCopilotOpen(false);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [copilotOpen]);

  // 通过自定义事件支持从任意组件打开 Copilot
  useEffect(() => {
    const handleOpenCopilot = () => {
      setCopilotOpen(true);
    };

    window.addEventListener('openCopilot', handleOpenCopilot);
    return () => {
      window.removeEventListener('openCopilot', handleOpenCopilot);
    };
  }, []);

  return (
    <>
      {children}
      <AICopilot open={copilotOpen} onClose={() => setCopilotOpen(false)} />
    </>
  );
};

export default LayoutWrapper;

