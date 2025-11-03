/**
 * 文档大纲（目录）组件
 * 支持：
 * - 解析 Markdown 标题生成目录树
 * - 点击目录项平滑滚动到对应标题
 * - 支持收起/展开
 * - 全部展开/折叠功能
 * - 高亮当前阅读位置
 */
import React, { useState, useEffect, useMemo } from 'react';
import { Button, Space, Tooltip, Tree, Empty } from 'antd';
import { AlignLeftOutlined } from '@ant-design/icons';
import { LuListCollapse } from 'react-icons/lu';
import type { DataNode } from 'antd/es/tree';
import './TableOfContents.less';

interface HeadingNode {
  level: number; // 1-6
  text: string;
  id: string;
  children: HeadingNode[];
}

interface TableOfContentsProps {
  content: string; // Markdown 内容
  containerRef?: React.RefObject<HTMLElement>; // 滚动容器引用
  className?: string;
}

/**
 * 解析 Markdown 内容，提取标题生成大纲树
 */
const parseHeadings = (markdown: string): HeadingNode[] => {
  if (!markdown) return [];

  const lines = markdown.split('\n');
  const headings: HeadingNode[] = [];
  const stack: HeadingNode[] = [];

  // 使用全局计数器跟踪每个级别的标题索引（与 MarkdownViewer 保持一致）
  const headingCounters: { [level: number]: number } = {
    1: 0,
    2: 0,
    3: 0,
    4: 0,
    5: 0,
    6: 0,
  };

  lines.forEach((line) => {
    // 匹配 ATX 格式标题：# Heading
    const match = line.match(/^(#{1,6})\s+(.+)$/);
    if (match) {
      const level = match[1].length;
      const text = match[2].trim();

      // 获取当前级别的索引
      const currentIndex = headingCounters[level];

      // 生成唯一 ID（与 MarkdownViewer 保持一致 - 使用标题索引而非行号）
      const id = `heading-${level}-${currentIndex}-${text
        .toLowerCase()
        .replace(/[^\w\u4e00-\u9fa5]+/g, '-')}`;

      // 递增当前级别的计数器
      headingCounters[level]++;

      const node: HeadingNode = {
        level,
        text,
        id,
        children: [],
      };

      // 构建树形结构
      while (stack.length > 0 && stack[stack.length - 1].level >= level) {
        stack.pop();
      }

      if (stack.length === 0) {
        headings.push(node);
      } else {
        stack[stack.length - 1].children.push(node);
      }

      stack.push(node);
    }
  });

  return headings;
};

/**
 * 将 HeadingNode 转换为 Ant Design Tree 的 DataNode
 */
const convertToTreeData = (nodes: HeadingNode[]): DataNode[] => {
  return nodes.map((node) => ({
    key: node.id,
    title: (
      <div className="toc-item-title" data-level={node.level}>
        {node.text}
      </div>
    ),
    children: node.children.length > 0 ? convertToTreeData(node.children) : undefined,
  }));
};

/**
 * 获取所有节点的 key（用于全部展开）
 */
const getAllKeys = (nodes: HeadingNode[]): string[] => {
  const keys: string[] = [];
  const traverse = (nodeList: HeadingNode[]) => {
    nodeList.forEach((node) => {
      keys.push(node.id);
      if (node.children.length > 0) {
        traverse(node.children);
      }
    });
  };
  traverse(nodes);
  return keys;
};

/**
 * 获取一级标题的 key（用于折叠到二级）
 * 折叠到二级意味着：只展开一级标题，这样就能看到一级和二级标题
 */
const getLevel1Keys = (nodes: HeadingNode[]): string[] => {
  const keys: string[] = [];
  const traverse = (nodeList: HeadingNode[]) => {
    nodeList.forEach((node) => {
      // 只收集 level 1 的节点
      if (node.level === 1) {
        keys.push(node.id);
      }
      // 继续遍历子节点以找到所有一级标题
      if (node.children.length > 0) {
        traverse(node.children);
      }
    });
  };
  traverse(nodes);
  return keys;
};

/**
 * TableOfContents 组件
 */
const TableOfContents: React.FC<TableOfContentsProps> = ({
  content,
  containerRef,
  className = '',
}) => {
  const [collapsed, setCollapsed] = useState(false); // 收起/展开状态
  const [expandedKeys, setExpandedKeys] = useState<string[]>([]); // 展开的节点
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]); // 选中的节点（高亮当前阅读位置）
  const [allExpanded, setAllExpanded] = useState(true); // 是否全部展开

  // 解析标题
  const headings = useMemo(() => parseHeadings(content), [content]);

  // 转换为 Tree 数据
  const treeData = useMemo(() => convertToTreeData(headings), [headings]);

  // 获取所有 key
  const allKeys = useMemo(() => getAllKeys(headings), [headings]);

  // 获取一级标题的 key（用于折叠到二级标题）
  const level1Keys = useMemo(() => getLevel1Keys(headings), [headings]);

  // 初始化：默认全部展开
  useEffect(() => {
    if (allKeys.length > 0) {
      setExpandedKeys(allKeys);
      setAllExpanded(true);
    }
  }, [allKeys]);

  // 监听滚动，高亮当前阅读位置
  useEffect(() => {
    const container = containerRef?.current;
    if (!container || allKeys.length === 0) return;

    const handleScroll = () => {
      // 获取所有标题元素（h1-h6）
      const allHeadings = container.querySelectorAll('h1, h2, h3, h4, h5, h6');

      if (allHeadings.length === 0) return;

      // 找到当前在视口内的第一个标题
      const containerTop = container.getBoundingClientRect().top;

      let currentHeading: HTMLElement | null = null;
      let currentId: string | null = null;

      for (const element of Array.from(allHeadings) as HTMLElement[]) {
        const rect = element.getBoundingClientRect();
        const elementTop = rect.top - containerTop;

        // 标题在视口上方或刚进入视口
        if (elementTop <= 100) {
          currentHeading = element;

          // 尝试获取 ID（优先使用 data-heading-id，其次使用 id 属性）
          currentId = element.getAttribute('data-heading-id') || element.id;

          // 如果没有 ID，尝试根据文本内容匹配
          if (!currentId) {
            const level = element.tagName.toLowerCase().substring(1); // 'h1' -> '1'
            const text = element.textContent?.trim() || '';
            const slug = text.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-');

            // 在 allKeys 中查找匹配的 ID
            currentId = allKeys.find(key => {
              const parts = key.split('-');
              if (parts.length >= 4 && parts[1] === level) {
                const keyText = parts.slice(3).join('-');
                return keyText === slug || slug.includes(keyText);
              }
              return false;
            }) || null;
          }
        } else {
          break;
        }
      }

      if (currentId && allKeys.includes(currentId)) {
        setSelectedKeys([currentId]);
      }
    };

    // 初始化时执行一次
    handleScroll();

    // 监听滚动事件
    container.addEventListener('scroll', handleScroll, { passive: true });

    return () => {
      container.removeEventListener('scroll', handleScroll);
    };
  }, [containerRef, allKeys]);

  // 点击目录项，滚动到对应标题
  const handleSelect = (selectedKeysValue: React.Key[]) => {
    if (selectedKeysValue.length === 0) return;

    const id = selectedKeysValue[0] as string;
    const container = containerRef?.current;

    if (!container) {
      return;
    }

    // 首先尝试通过 data-heading-id 查找
    let element = container.querySelector(`[data-heading-id="${id}"]`) as HTMLElement | null;

    // 如果找不到，尝试通过 ID 直接查找（对于 MarkdownViewer）
    if (!element) {
      element = container.querySelector(`#${id}`) as HTMLElement | null;
    }

    // 如果还是找不到，尝试通过文本内容查找标题
    if (!element) {
      // 从 ID 中提取标题信息（ID 格式：heading-{level}-{index}-{text}）
      const parts = id.split('-');

      if (parts.length >= 4 && parts[0] === 'heading') {
        const level = parts[1];
        const index = parseInt(parts[2], 10);
        const slug = parts.slice(3).join('-');

        // 查找所有该级别的标题
        const headings = Array.from(container.querySelectorAll(`h${level}`)) as HTMLElement[];

        // 方法1：通过索引匹配（如果索引在范围内）
        if (index >= 0 && index < headings.length) {
          element = headings[index];
        }

        // 方法2：如果索引不准确，通过文本内容匹配
        if (!element) {
          for (const heading of headings) {
            const headingText = heading.textContent?.trim() || '';
            const headingSlug = headingText.toLowerCase().replace(/[^\w\u4e00-\u9fa5]+/g, '-');

            // 精确匹配或包含匹配
            if (headingSlug === slug || headingSlug.includes(slug) || slug.includes(headingSlug)) {
              element = heading;
              break;
            }
          }
        }
      }
    }

    if (element) {
      // 获取容器的顶部位置
      const containerRect = container.getBoundingClientRect();
      const elementRect = element.getBoundingClientRect();

      // 计算需要滚动的距离（减去一些偏移量以留出空间）
      const scrollTop = container.scrollTop;
      const offset = elementRect.top - containerRect.top - 20;

      container.scrollTo({
        top: scrollTop + offset,
        behavior: 'smooth'
      });

      setSelectedKeys([id]);
    }
  };

  // 切换全部展开/折叠
  const toggleExpandAll = () => {
    if (allExpanded) {
      // 折叠到二级标题（只展开一级标题，这样可以看到一级和二级）
      setExpandedKeys(level1Keys);
      setAllExpanded(false);
    } else {
      // 全部展开
      setExpandedKeys(allKeys);
      setAllExpanded(true);
    }
  };

  // 如果没有标题，不显示组件
  if (headings.length === 0) {
    return null;
  }

  return (
    <div className={`table-of-contents ${collapsed ? 'collapsed' : ''} ${className}`}>
      {/* 头部 */}
      <div className="toc-header">
        {!collapsed && (
          <>
            <Tooltip title="收起大纲" placement="left">
              <div className="toc-title" onClick={() => setCollapsed(true)} style={{ cursor: 'pointer' }}>
                <AlignLeftOutlined />
                <span>大纲</span>
              </div>
            </Tooltip>
            <Space size={4}>
              <Tooltip title={allExpanded ? '折叠到二级标题' : '全部展开'}>
                <Button
                  type="text"
                  size="small"
                  icon={<LuListCollapse />}
                  onClick={toggleExpandAll}
                />
              </Tooltip>
            </Space>
          </>
        )}
        {collapsed && (
          <Tooltip title="展开大纲" placement="left">
            <Button
              type="text"
              size="small"
              icon={<AlignLeftOutlined />}
              onClick={() => setCollapsed(false)}
            />
          </Tooltip>
        )}
      </div>

      {/* 目录树 */}
      {!collapsed && (
        <div className="toc-content">
          {treeData.length > 0 ? (
            <Tree
              treeData={treeData}
              expandedKeys={expandedKeys}
              selectedKeys={selectedKeys}
              onExpand={(keys) => {
                setExpandedKeys(keys as string[]);
                setAllExpanded(keys.length === allKeys.length);
              }}
              onSelect={handleSelect}
              showLine={false}
              showIcon={false}
              blockNode
              className="toc-tree"
            />
          ) : (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description="暂无大纲"
              style={{ marginTop: 60 }}
            />
          )}
        </div>
      )}
    </div>
  );
};

export default TableOfContents;
