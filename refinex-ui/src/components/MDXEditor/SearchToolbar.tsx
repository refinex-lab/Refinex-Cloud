/**
 * MDXEditor 搜索悬浮窗组件
 * 提供查找和替换功能（独立悬浮窗，参考 VSCode/Notion 设计）
 */
import React, { useState, useEffect, useRef } from 'react';
import { Input, Button, Space, message, Tooltip } from 'antd';
import {
  SearchOutlined,
  CloseOutlined,
  UpOutlined,
  DownOutlined,
  SwapOutlined,
  EnterOutlined,
} from '@ant-design/icons';
import { useEditorSearch } from '@mdxeditor/editor';
import './SearchToolbar.less';

export interface SearchToolbarProps {
  visible: boolean;
  onClose: () => void;
}

/**
 * 搜索悬浮窗组件
 * 独立悬浮在编辑器右上角，提供优雅的搜索和替换交互
 */
export const SearchToolbar: React.FC<SearchToolbarProps> = ({ visible, onClose }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [replaceTerm, setReplaceTerm] = useState('');
  const [showReplace, setShowReplace] = useState(false);
  const searchInputRef = useRef<any>(null);

  // 使用 MDXEditor 的搜索 hook
  const { cursor, total, next, prev, replace, replaceAll, setSearch } = useEditorSearch();

  // 打开时自动聚焦搜索框
  useEffect(() => {
    if (visible && searchInputRef.current) {
      setTimeout(() => {
        searchInputRef.current?.focus();
        searchInputRef.current?.select();
      }, 100);
    }
  }, [visible]);

  // 处理搜索
  const handleSearch = (value: string) => {
    setSearchTerm(value);
    setSearch(value || null);
  };

  // 处理替换当前匹配项
  const handleReplace = () => {
    if (!searchTerm) {
      message.warning('请先输入搜索内容');
      return;
    }
    if (!replaceTerm) {
      message.warning('请输入替换内容');
      return;
    }
    replace(replaceTerm);
    message.success('已替换当前匹配项');
  };

  // 处理全部替换
  const handleReplaceAll = () => {
    if (!searchTerm) {
      message.warning('请先输入搜索内容');
      return;
    }
    if (!replaceTerm) {
      message.warning('请输入替换内容');
      return;
    }
    replaceAll(replaceTerm);
    message.success(`已替换全部 ${total} 个匹配项`);
  };

  // 清空搜索
  const handleClear = () => {
    setSearchTerm('');
    setReplaceTerm('');
    setSearch(null);
    setShowReplace(false);
  };

  if (!visible) {
    return null;
  }

  return (
    <div className="mdx-search-float-panel">
      <div className="search-panel-header">
        {/* 搜索输入框 */}
        <div className="search-input-wrapper">
          <Input
            ref={searchInputRef}
            placeholder="查找"
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
            onPressEnter={() => next()}
            prefix={<SearchOutlined style={{ color: '#8c8c8c' }} />}
            suffix={
              <Space size={4}>
                {/* 匹配计数 */}
                <span className="search-result-count">
                  {total > 0 ? `${cursor}/${total}` : 'No results'}
                </span>

                {/* 导航按钮 */}
                <Tooltip title="上一个 (Shift+Enter)">
                  <Button
                    type="text"
                    size="small"
                    icon={<UpOutlined style={{ fontSize: 12 }} />}
                    onClick={prev}
                    disabled={total === 0}
                    className="nav-button"
                  />
                </Tooltip>
                <Tooltip title="下一个 (Enter)">
                  <Button
                    type="text"
                    size="small"
                    icon={<DownOutlined style={{ fontSize: 12 }} />}
                    onClick={next}
                    disabled={total === 0}
                    className="nav-button"
                  />
                </Tooltip>
              </Space>
            }
            bordered={false}
            className="search-main-input"
          />
        </div>

        {/* 顶部操作按钮 */}
        <div className="search-panel-actions">
          <Tooltip title={showReplace ? '隐藏替换' : '显示替换'}>
            <Button
              type="text"
              size="small"
              icon={<SwapOutlined />}
              onClick={() => setShowReplace(!showReplace)}
              className={showReplace ? 'action-button active' : 'action-button'}
            />
          </Tooltip>
          <Tooltip title="关闭 (Esc)">
            <Button
              type="text"
              size="small"
              icon={<CloseOutlined />}
              onClick={() => {
                handleClear();
                onClose();
              }}
              className="action-button"
            />
          </Tooltip>
        </div>
      </div>

      {/* 替换区域 */}
      {showReplace && (
        <div className="replace-panel-content">
          <div className="replace-input-wrapper">
            <Input
              placeholder="替换"
              value={replaceTerm}
              onChange={(e) => setReplaceTerm(e.target.value)}
              onPressEnter={handleReplace}
              prefix={<SwapOutlined style={{ color: '#8c8c8c' }} />}
              suffix={
                <Space size={4}>
                  <Tooltip title="替换 (Ctrl+Shift+1)">
                    <Button
                      type="text"
                      size="small"
                      onClick={handleReplace}
                      disabled={total === 0 || !searchTerm}
                      className="nav-button"
                    >
                      替换
                    </Button>
                  </Tooltip>
                  <Tooltip title="全部替换 (Ctrl+Shift+Enter)">
                    <Button
                      type="text"
                      size="small"
                      onClick={handleReplaceAll}
                      disabled={total === 0 || !searchTerm}
                      className="nav-button"
                      danger
                    >
                      全部
                    </Button>
                  </Tooltip>
                </Space>
              }
              bordered={false}
              className="replace-main-input"
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default SearchToolbar;

