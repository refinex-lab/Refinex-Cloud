/**
 * 标签选择器组件
 * 支持多选、搜索、新建标签
 */
import React, { useState, useEffect } from 'react';
import { Tag, Input, Popover, Space, Button, message, Empty, Spin } from 'antd';
import { PlusOutlined, SearchOutlined, CloseOutlined, CheckOutlined } from '@ant-design/icons';
import { pageMyTags } from '@/services/kb/tag';
import type { ContentTag } from '@/services/kb/tag.d';
import './TagSelector.less';

interface TagSelectorProps {
  /** 已选中的标签ID列表 */
  value?: number[];
  /** 标签变化回调 */
  onChange?: (tagIds: number[]) => void;
  /** 空间ID（用于过滤标签范围） */
  spaceId: number;
  /** 是否只读 */
  readonly?: boolean;
  /** 最大标签数量 */
  maxCount?: number;
}

/**
 * 标签选择器
 * 用于文档编辑器头部,支持快速选择和管理标签
 */
const TagSelector: React.FC<TagSelectorProps> = ({
  value = [],
  onChange,
  spaceId,
  readonly = false,
  maxCount = 5,
}) => {
  const [allTags, setAllTags] = useState<ContentTag[]>([]);
  const [filteredTags, setFilteredTags] = useState<ContentTag[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [popoverVisible, setPopoverVisible] = useState(false);

  // 加载所有标签
  const loadTags = async () => {
    setLoading(true);
    try {
      const response = await pageMyTags({
        pageNum: 1,
        pageSize: 100, // 假设标签不会太多,一次加载所有
      });
      if (response.success && response.data) {
        setAllTags(response.data.records || []);
        setFilteredTags(response.data.records || []);
      }
    } catch (error) {
      console.error('加载标签失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (popoverVisible) {
      loadTags();
    }
  }, [popoverVisible, spaceId]);

  // 搜索过滤
  useEffect(() => {
    if (!searchKeyword.trim()) {
      setFilteredTags(allTags);
    } else {
      const keyword = searchKeyword.toLowerCase();
      setFilteredTags(
        allTags.filter((tag) => tag.tagName.toLowerCase().includes(keyword)),
      );
    }
  }, [searchKeyword, allTags]);

  // 选中标签
  const handleSelectTag = (tagId: number) => {
    if (readonly) return;

    if (value.includes(tagId)) {
      // 取消选中
      onChange?.(value.filter((id) => id !== tagId));
    } else {
      // 选中
      if (value.length >= maxCount) {
        message.warning(`最多只能选择 ${maxCount} 个标签`);
        return;
      }
      onChange?.([...value, tagId]);
    }
  };

  // 移除标签
  const handleRemoveTag = (tagId: number, e: React.MouseEvent) => {
    e.stopPropagation();
    if (readonly) return;
    onChange?.(value.filter((id) => id !== tagId));
  };

  // 获取已选中的标签对象
  const selectedTags = allTags.filter((tag) => value.includes(tag.id));

  // Popover 内容
  const popoverContent = (
    <div className="tag-selector-popover">
      {/* 搜索框 */}
      <Input
        placeholder="搜索标签..."
        prefix={<SearchOutlined />}
        value={searchKeyword}
        onChange={(e) => setSearchKeyword(e.target.value)}
        allowClear
        style={{ marginBottom: 12 }}
      />

      {/* 标签列表 */}
      <div className="tag-list-container">
        <Spin spinning={loading}>
          {filteredTags.length > 0 ? (
            <div className="tag-list">
              {filteredTags.map((tag) => {
                const isSelected = value.includes(tag.id);
                return (
                  <div
                    key={tag.id}
                    className={`tag-item ${isSelected ? 'selected' : ''}`}
                    onClick={() => handleSelectTag(tag.id)}
                  >
                    <Tag color={tag.tagColor || 'blue'}>{tag.tagName}</Tag>
                    {isSelected && (
                      <CheckOutlined style={{ color: '#1890ff', marginLeft: 8 }} />
                    )}
                  </div>
                );
              })}
            </div>
          ) : (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={searchKeyword ? '未找到匹配的标签' : '暂无标签'}
              style={{ padding: '20px 0' }}
            />
          )}
        </Spin>
      </div>

      {/* 底部提示 */}
      <div className="tag-selector-footer">
        <span className="tag-count">
          已选 {value.length}/{maxCount}
        </span>
        <Button type="link" size="small" onClick={() => setPopoverVisible(false)}>
          完成
        </Button>
      </div>
    </div>
  );

  // 控制显示的标签数量
  const maxVisibleTags = 3;
  const visibleTags = selectedTags.slice(0, maxVisibleTags);
  const hiddenTagsCount = selectedTags.length - maxVisibleTags;

  return (
    <div className="tag-selector-container">
      <Space size={8} wrap={false}>
        {/* 已选标签展示（最多显示3个） */}
        {visibleTags.map((tag) => (
          <Tag
            key={tag.id}
            color={tag.tagColor || 'blue'}
            closable={!readonly}
            onClose={(e) => handleRemoveTag(tag.id, e)}
            style={{ cursor: readonly ? 'default' : 'pointer', margin: 0 }}
          >
            {tag.tagName}
          </Tag>
        ))}

        {/* 超出部分显示 +N，点击展开所有标签 */}
        {hiddenTagsCount > 0 && (
          <Popover
            content={
              <div className="tag-selector-all-tags">
                <div className="all-tags-title">全部标签 ({selectedTags.length})</div>
                <Space size={8} wrap style={{ maxWidth: 300 }}>
                  {selectedTags.map((tag) => (
                    <Tag
                      key={tag.id}
                      color={tag.tagColor || 'blue'}
                      closable={!readonly}
                      onClose={(e) => handleRemoveTag(tag.id, e)}
                      style={{ margin: 0 }}
                    >
                      {tag.tagName}
                    </Tag>
                  ))}
                </Space>
              </div>
            }
            title={null}
            trigger="click"
            placement="bottomLeft"
            overlayClassName="tag-selector-all-overlay"
          >
            <Tag
              style={{
                background: '#f0f0f0',
                border: '1px solid #d9d9d9',
                cursor: 'pointer',
                margin: 0,
              }}
            >
              +{hiddenTagsCount}
            </Tag>
          </Popover>
        )}

        {/* 添加标签按钮 */}
        {!readonly && value.length < maxCount && (
          <Popover
            content={popoverContent}
            title="选择标签"
            trigger="click"
            open={popoverVisible}
            onOpenChange={setPopoverVisible}
            placement="bottomLeft"
            overlayClassName="tag-selector-overlay"
          >
            <Tag
              icon={<PlusOutlined />}
              style={{
                background: '#fafafa',
                borderStyle: 'dashed',
                cursor: 'pointer',
                margin: 0,
              }}
            >
              添加标签
            </Tag>
          </Popover>
        )}
      </Space>
    </div>
  );
};

export default TagSelector;

