import {
  ArrowLeftOutlined,
  CaretDownOutlined,
  CaretRightOutlined,
  DeleteOutlined,
  EditOutlined,
  FolderFilled,
  FolderOpenFilled,
  MoreOutlined,
  PlusOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { Dropdown, Empty, Input, message, Modal, Spin, Tree } from 'antd';
import type { MenuProps } from 'antd';
import type { DataNode } from 'antd/es/tree';
import React, { useEffect, useMemo, useState } from 'react';
import type {
  ContentDirectory,
  ContentDirectoryTreeNode,
} from '@/services/kb/typings.d';
import {
  deleteDirectory,
  getDirectoryTree,
  moveDirectory,
} from '@/services/kb/directory';
import DirectoryFormModal from './DirectoryFormModal';
import './DirectoryTree.less';

// 扩展 DataNode 类型以包含自定义数据
interface ExtendedDataNode extends DataNode {
  data?: ContentDirectoryTreeNode;
}

interface DirectoryTreeProps {
  spaceId: number;
  onSelect?: (directoryId: number | null, directory: ContentDirectory | null) => void;
  showBackButton?: boolean;
  onBack?: () => void;
  spaceName?: string;
}

const DirectoryTree: React.FC<DirectoryTreeProps> = ({
  spaceId,
  onSelect,
  showBackButton = false,
  onBack,
  spaceName,
}) => {
  const [treeData, setTreeData] = useState<ContentDirectoryTreeNode[]>([]);
  const [loading, setLoading] = useState(false);
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<React.Key[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [hoverKey, setHoverKey] = useState<React.Key | null>(null);
  const [dropdownOpenKey, setDropdownOpenKey] = useState<React.Key | null>(null);

  // 弹窗状态
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [formMode, setFormMode] = useState<'create' | 'edit'>('create');
  const [currentDirectory, setCurrentDirectory] = useState<ContentDirectoryTreeNode | null>(null);
  const [parentDirectory, setParentDirectory] = useState<ContentDirectoryTreeNode | null>(null);

  // 加载目录树
  const loadTree = async () => {
    setLoading(true);
    try {
      const response = await getDirectoryTree(spaceId);
      const data = response.data || [];
      setTreeData(data);

      // 默认展开第一层
      const firstLevelKeys = data.map((item: ContentDirectoryTreeNode) => item.id);
      setExpandedKeys(firstLevelKeys);
    } catch (error) {
      console.error('加载目录树失败:', error);
      message.error('加载目录树失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTree();
  }, [spaceId]);

  // 转换为 Ant Design Tree 数据格式
  const convertToTreeData = (nodes: ContentDirectoryTreeNode[]): ExtendedDataNode[] => {
    return nodes.map((node) => ({
      key: node.id,
      title: node.directoryName,
      // 不再使用 Tree 的 icon 属性，在 titleRender 里统一渲染
      children: node.children ? convertToTreeData(node.children) : undefined,
      isLeaf: !(node.children && node.children.length > 0) && !node.hasChildren,
      data: node, // 保存原始数据
    }));
  };

  // 递归获取所有节点的 key
  const getAllKeys = (nodes: ContentDirectoryTreeNode[]): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (items: ContentDirectoryTreeNode[]) => {
      items.forEach((item) => {
        keys.push(item.id);
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    };
    traverse(nodes);
    return keys;
  };

  // 搜索过滤
  const getFilteredData = useMemo(() => {
    if (!searchValue) {
      return convertToTreeData(treeData);
    }

    const filterTree = (nodes: ContentDirectoryTreeNode[]): ExtendedDataNode[] => {
      const result: ExtendedDataNode[] = [];

      nodes.forEach((node) => {
        const match = node.directoryName.toLowerCase().includes(searchValue.toLowerCase());
        const children = node.children ? filterTree(node.children) : [];

        if (match || children.length > 0) {
          result.push({
            key: node.id,
            title: highlightText(node.directoryName, searchValue),
            // 不再使用 Tree 的 icon 属性，在 titleRender 里统一渲染
            children: children.length > 0 ? children : undefined,
            isLeaf: !(node.children && node.children.length > 0) && !node.hasChildren,
            data: node,
          });
        }
      });

      return result;
    };

    return filterTree(treeData);
  }, [treeData, searchValue, expandedKeys]);

  // 高亮搜索文本
  const highlightText = (text: string, search: string) => {
    if (!search) return text;

    const parts = text.split(new RegExp(`(${search})`, 'gi'));
    return (
      <span>
        {parts.map((part, index) =>
          part.toLowerCase() === search.toLowerCase() ? (
            <span key={index} style={{ backgroundColor: '#ffc069', fontWeight: 'bold' }}>
              {part}
            </span>
          ) : (
            part
          ),
        )}
      </span>
    );
  };

  // 搜索时自动展开匹配的节点
  useEffect(() => {
    if (searchValue) {
      const allKeys = getAllKeys(treeData);
      setExpandedKeys(allKeys);
      setAutoExpandParent(true);
    } else {
      // 恢复默认展开第一层
      const firstLevelKeys = treeData.map((item) => item.id);
      setExpandedKeys(firstLevelKeys);
    }
  }, [searchValue, treeData]);

  // 树节点展开/收起
  const onExpand = (expandedKeysValue: React.Key[]) => {
    setExpandedKeys(expandedKeysValue);
    setAutoExpandParent(false);
  };

  // 树节点选中
  const onTreeSelect = (selectedKeysValue: React.Key[], info: any) => {
    setSelectedKeys(selectedKeysValue);
    if (selectedKeysValue.length > 0) {
      const selectedNode = info.node.data as ContentDirectoryTreeNode;
      onSelect?.(selectedNode.id, selectedNode as unknown as ContentDirectory);
    } else {
      onSelect?.(null, null);
    }
  };

  // 查找节点
  const findNodeById = (
    nodes: ContentDirectoryTreeNode[],
    id: number,
  ): ContentDirectoryTreeNode | null => {
    for (const node of nodes) {
      if (node.id === id) {
        return node;
      }
      if (node.children) {
        const found = findNodeById(node.children, id);
        if (found) return found;
      }
    }
    return null;
  };

  // 创建根目录
  const handleCreateRoot = () => {
    setFormMode('create');
    setCurrentDirectory(null);
    setParentDirectory(null);
    setFormModalVisible(true);
  };

  // 创建子目录
  const handleCreateChild = (directory: ContentDirectoryTreeNode) => {
    setFormMode('create');
    setCurrentDirectory(null);
    setParentDirectory(directory);
    setFormModalVisible(true);
  };

  // 编辑目录
  const handleEdit = (directory: ContentDirectoryTreeNode) => {
    setFormMode('edit');
    setCurrentDirectory(directory);

    // 查找父目录
    if (directory.parentId !== 0) {
      const parent = findNodeById(treeData, directory.parentId);
      setParentDirectory(parent || null);
    } else {
      setParentDirectory(null);
    }

    setFormModalVisible(true);
  };

  // 删除目录
  const handleDelete = (directory: ContentDirectoryTreeNode) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除目录"${directory.directoryName}"吗？删除后将级联删除所有子目录，且无法恢复！`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDirectory(directory.id);
          message.success('删除成功');
          loadTree();
        } catch (error) {
          console.error('删除目录失败:', error);
        }
      },
    });
  };

  // 拖拽放置
  const onDrop = async (info: any) => {
    const dropKey = info.node.key;
    const dragKey = info.dragNode.key;
    const dropPos = info.node.pos.split('-');
    const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

    // 不能拖到自己上
    if (dragKey === dropKey) {
      message.warning('不能拖拽到自己上');
      return;
    }

    try {
      // 判断是否是层级迁移（dropToGap=false 表示拖到节点上，作为子节点）
      if (!info.dropToGap) {
        // 层级迁移：拖到目标节点上，成为其子节点
        await moveDirectory({
          id: dragKey as number,
          targetParentId: dropKey as number,
          targetSort: 0, // 移到最前面
        });
        message.success('移动成功');
        loadTree();
      } else {
        // 同级排序：拖到目标节点的上方或下方
        const dropNode = findNodeById(treeData, dropKey as number);
        if (dropNode) {
          await moveDirectory({
            id: dragKey as number,
            targetParentId: dropNode.parentId,
            targetSort: dropPosition < 0 ? dropNode.sort - 1 : dropNode.sort + 1,
          });
          message.success('移动成功');
          loadTree();
        }
      }
    } catch (error) {
      console.error('移动失败:', error);
    }
  };

  // 表单提交成功回调
  const handleFormSuccess = () => {
    setFormModalVisible(false);
    loadTree();
  };

  // 右键菜单
  const getContextMenu = (node: ContentDirectoryTreeNode): MenuProps['items'] => {
    return [
      {
        key: 'create',
        label: '新建子目录',
        icon: <PlusOutlined />,
        onClick: () => {
          setDropdownOpenKey(null);
          handleCreateChild(node);
        },
      },
      {
        key: 'edit',
        label: '编辑',
        icon: <EditOutlined />,
        onClick: () => {
          setDropdownOpenKey(null);
          handleEdit(node);
        },
      },
      {
        type: 'divider',
      },
      {
        key: 'delete',
        label: '删除',
        icon: <DeleteOutlined />,
        danger: true,
        onClick: () => {
          setDropdownOpenKey(null);
          handleDelete(node);
        },
      },
    ];
  };

  // 自定义树节点标题（完全自控：图标 + 文字 + 操作按钮）
  const renderTreeTitle = (node: any) => {
    const nodeData = node.data as ContentDirectoryTreeNode;
    const isHover = hoverKey === node.key;
    const isDropdownOpen = dropdownOpenKey === node.key;
    const isExpanded = expandedKeys.includes(node.key);
    const isSearching = !!searchValue;

    // 选择图标
    const FolderIcon = isExpanded ? FolderOpenFilled : FolderFilled;
    const iconColor = isSearching && node.title !== node.data?.directoryName ? '#faad14' : '#1890ff';

    // 点击节点内容区域展开/收起
    const handleNodeClick = (e: React.MouseEvent) => {
      // 不阻止事件冒泡，让 Tree 组件处理选中逻辑
      // 切换展开状态
      if (node.children && node.children.length > 0) {
        if (isExpanded) {
          setExpandedKeys(expandedKeys.filter((k) => k !== node.key));
        } else {
          setExpandedKeys([...expandedKeys, node.key]);
        }
      }
    };

    return (
      <div
        className={`tree-node-content ${isHover || isDropdownOpen ? 'tree-node-hover' : ''}`}
        onMouseEnter={() => setHoverKey(node.key)}
        onMouseLeave={() => {
          if (!isDropdownOpen) {
            setHoverKey(null);
          }
        }}
      >
        {/* 可点击的节点主体区域（图标 + 文字）*/}
        <div
          className="tree-node-main"
          onClick={handleNodeClick}
          style={{ display: 'flex', alignItems: 'center', gap: '6px', flex: 1, cursor: 'pointer' }}
        >
          {/* 文件夹图标 */}
          <FolderIcon className="tree-node-icon" style={{ color: iconColor }} />

          {/* 文字标签 */}
          <span className="tree-node-label">{node.title}</span>
        </div>

        {/* 操作按钮（始终渲染，通过 opacity 控制显示） */}
        <div
          className="tree-node-actions"
          onClick={(e) => e.stopPropagation()}
          onMouseEnter={() => setHoverKey(node.key)}
        >
          <Dropdown
            menu={{ items: getContextMenu(nodeData) }}
            trigger={['click']}
            placement="bottomRight"
            overlayClassName="tree-node-dropdown"
            open={isDropdownOpen}
            onOpenChange={(open) => {
              setDropdownOpenKey(open ? node.key : null);
              if (!open) {
                setHoverKey(null);
              }
            }}
            getPopupContainer={() => document.body}
          >
            <MoreOutlined className="tree-node-more-btn" />
          </Dropdown>
        </div>
      </div>
    );
  };

  return (
    <div className="directory-tree-container">
      {/* 顶部工具栏 */}
      <div className="tree-toolbar">
        {/* 返回按钮和空间名称 */}
        {showBackButton && (
          <div className="toolbar-header">
            <button
              type="button"
              className="tree-back-btn"
              onClick={onBack}
              title="返回空间列表"
            >
              <ArrowLeftOutlined />
            </button>
            {spaceName && <div className="space-name">{spaceName}</div>}
          </div>
        )}

        <div className="toolbar-row">
          <Input
            placeholder="搜索目录..."
            prefix={<SearchOutlined />}
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            allowClear
            className="tree-search-input"
          />
          <button type="button" className="tree-create-btn" onClick={handleCreateRoot} title="新建目录">
            <PlusOutlined />
          </button>
        </div>
      </div>

      {/* 目录树 */}
      <div className="tree-content">
        <Spin spinning={loading}>
          {getFilteredData.length > 0 ? (
            <Tree
              className="custom-directory-tree"
              treeData={getFilteredData}
              expandedKeys={expandedKeys}
              selectedKeys={selectedKeys}
              autoExpandParent={autoExpandParent}
              onExpand={onExpand}
              onSelect={onTreeSelect}
              draggable
              onDrop={onDrop}
              blockNode
              showLine={false}
              showIcon={false}
              switcherIcon={({ expanded }) =>
                expanded ? <CaretDownOutlined /> : <CaretRightOutlined />
              }
              titleRender={renderTreeTitle}
            />
          ) : (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={searchValue ? '未找到匹配的目录' : '暂无目录'}
            >
              {!searchValue && (
                <button
                  type="button"
                  className="empty-create-btn"
                  onClick={handleCreateRoot}
                >
                  <PlusOutlined /> 创建第一个目录
                </button>
              )}
            </Empty>
          )}
        </Spin>
      </div>

      {/* 目录表单弹窗 */}
      <DirectoryFormModal
        visible={formModalVisible}
        mode={formMode}
        spaceId={spaceId}
        directory={currentDirectory}
        parentDirectory={parentDirectory}
        onSuccess={handleFormSuccess}
        onCancel={() => setFormModalVisible(false)}
      />
    </div>
  );
};

export default DirectoryTree;
