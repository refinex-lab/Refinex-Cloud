import {
  CaretDownOutlined,
  CaretRightOutlined,
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  FileAddOutlined,
  FileMarkdownOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  FolderAddOutlined,
  MoreOutlined,
  PlusOutlined,
  SearchOutlined,
  SwapOutlined,
} from '@ant-design/icons';
import { BsFolder2, BsFolder2Open } from 'react-icons/bs';
import { IoDocumentTextOutline } from 'react-icons/io5';
import { FiBox } from 'react-icons/fi';
import { GrHomeRounded } from 'react-icons/gr';
import { App, Dropdown, Empty, Input, Modal, Spin, Tree } from 'antd';
import type { MenuProps } from 'antd';
import type { DataNode } from 'antd/es/tree';
import React, { useEffect, useMemo, useState } from 'react';
import { history, useLocation } from '@umijs/max';
import type { ContentSpace, ContentTreeNode } from '@/services/kb/typings.d';
import { TreeNodeType } from '@/services/kb/typings.d';
import {
  deleteDirectory,
  getDirectoryTreeWithDocs,
  moveDirectory,
} from '@/services/kb/directory';
import { deleteDocument, getDocumentByGuid } from '@/services/kb/document';
import { getMyContentSpaces } from '@/services/kb/space';
import { exportToMarkdown, exportToPDF } from '@/utils/documentExport';
import DirectoryFormModal from './DirectoryFormModal';
import DocumentFormModal from './DocumentFormModal';
import './DirectoryTree.less';

// 扩展 DataNode 类型以包含自定义数据
interface ExtendedDataNode extends DataNode {
  data?: ContentTreeNode;
}

interface DirectoryTreeProps {
  spaceId: number;
  onSelect?: (node: ContentTreeNode | null) => void;
  onDocumentOpen?: (docGuid: string, document: ContentTreeNode) => void;
  selectedKey?: string;
  showBackButton?: boolean;
  onBack?: () => void;
  spaceName?: string;
}

const DirectoryTree: React.FC<DirectoryTreeProps> = ({
  spaceId,
  onSelect,
  onDocumentOpen,
  selectedKey,
  showBackButton = false,
  onBack,
  spaceName,
}) => {
  const { message } = App.useApp();
  const location = useLocation();
  const [treeData, setTreeData] = useState<ContentTreeNode[]>([]);
  const [loading, setLoading] = useState(false);
  const [expandedKeys, setExpandedKeys] = useState<React.Key[]>([]);
  const [selectedKeys, setSelectedKeys] = useState<React.Key[]>([]);
  const [searchValue, setSearchValue] = useState('');
  const [autoExpandParent, setAutoExpandParent] = useState(true);
  const [hoverKey, setHoverKey] = useState<React.Key | null>(null);
  const [dropdownOpenKey, setDropdownOpenKey] = useState<React.Key | null>(null);

  // 空间列表
  const [spaceList, setSpaceList] = useState<ContentSpace[]>([]);
  const [loadingSpaces, setLoadingSpaces] = useState(false);

  // 目录弹窗状态
  const [formModalVisible, setFormModalVisible] = useState(false);
  const [formMode, setFormMode] = useState<'create' | 'edit'>('create');
  const [currentDirectory, setCurrentDirectory] = useState<ContentTreeNode | null>(null);
  const [parentDirectory, setParentDirectory] = useState<ContentTreeNode | null>(null);

  // 文档弹窗状态
  const [docFormVisible, setDocFormVisible] = useState(false);
  const [docFormDirectory, setDocFormDirectory] = useState<ContentTreeNode | null>(null);

  // 加载目录树（包含文档）
  const loadTree = async () => {
    setLoading(true);
    try {
      const response = await getDirectoryTreeWithDocs(spaceId);
      const data = response.data || [];
      setTreeData(data);

      // 默认展开第一层
      const firstLevelKeys = data.map((item: ContentTreeNode) => item.key);
      setExpandedKeys(firstLevelKeys);
    } catch (error) {
      console.error('加载目录树失败:', error);
      message.error('加载目录树失败');
    } finally {
      setLoading(false);
    }
  };

  // 加载空间列表
  const loadSpaceList = async () => {
    setLoadingSpaces(true);
    try {
      const response = await getMyContentSpaces();
      if (response.success && response.data) {
        setSpaceList(response.data);
      }
    } catch (error) {
      console.error('加载空间列表失败:', error);
    } finally {
      setLoadingSpaces(false);
    }
  };

  useEffect(() => {
    loadTree();
    loadSpaceList();
  }, [spaceId]);

  // 同步外部选中状态
  useEffect(() => {
    if (selectedKey) {
      setSelectedKeys([selectedKey]);
    }
  }, [selectedKey]);

  // 转换为 Ant Design Tree 数据格式
  const convertToTreeData = (nodes: ContentTreeNode[]): ExtendedDataNode[] => {
    return nodes.map((node) => ({
      key: node.key,
      title: node.title,
      children: node.children ? convertToTreeData(node.children) : undefined,
      isLeaf: node.isLeaf,
      data: node, // 保存原始数据
    }));
  };

  // 递归获取所有节点的 key
  const getAllKeys = (nodes: ContentTreeNode[]): React.Key[] => {
    const keys: React.Key[] = [];
    const traverse = (items: ContentTreeNode[]) => {
      items.forEach((item) => {
        keys.push(item.key);
        if (item.children && item.children.length > 0) {
          traverse(item.children);
        }
      });
    };
    traverse(nodes);
    return keys;
  };

  // 递归获取节点及其所有子孙节点的 key
  const getAllDescendantKeys = (node: ContentTreeNode): React.Key[] => {
    const keys: React.Key[] = [node.key];
    if (node.children && node.children.length > 0) {
      node.children.forEach((child) => {
        keys.push(...getAllDescendantKeys(child));
      });
    }
    return keys;
  };

  // 搜索过滤
  const getFilteredData = useMemo(() => {
    if (!searchValue) {
      return convertToTreeData(treeData);
    }

    const filterTree = (nodes: ContentTreeNode[]): ExtendedDataNode[] => {
      const result: ExtendedDataNode[] = [];

      nodes.forEach((node) => {
        const match = node.title.toLowerCase().includes(searchValue.toLowerCase());
        const children = node.children ? filterTree(node.children) : [];

        if (match || children.length > 0) {
          result.push({
            key: node.key,
            title: highlightText(node.title, searchValue),
            children: children.length > 0 ? children : undefined,
            isLeaf: node.isLeaf,
            data: node,
          });
        }
      });

      return result;
    };

    return filterTree(treeData);
  }, [treeData, searchValue]);

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
      const firstLevelKeys = treeData.map((item) => item.key);
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
      const selectedNode = info.node.data as ContentTreeNode;
      handleNodeClick(selectedNode);
    }
  };

  // 节点点击处理
  const handleNodeClick = (nodeData: ContentTreeNode) => {
    if (nodeData.nodeType === TreeNodeType.DOCUMENT) {
      // 点击文档 -> 打开编辑器
      onDocumentOpen?.(nodeData.docGuid!, nodeData);
    } else {
      // 点击目录 -> 触发选中回调（右侧显示目录内容）
      onSelect?.(nodeData);
    }
  };

  // 查找节点
  const findNodeById = (
    nodes: ContentTreeNode[],
    id: number,
    isDocument: boolean = false,
  ): ContentTreeNode | null => {
    for (const node of nodes) {
      if (isDocument) {
        if (node.nodeType === TreeNodeType.DOCUMENT && node.documentId === id) {
          return node;
        }
      } else {
        if (node.nodeType === TreeNodeType.DIRECTORY && node.directoryId === id) {
          return node;
        }
      }
      if (node.children) {
        const found = findNodeById(node.children, id, isDocument);
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
  const handleCreateDirectory = (directory: ContentTreeNode) => {
    setFormMode('create');
    setCurrentDirectory(null);
    setParentDirectory(directory);
    setFormModalVisible(true);
  };

  // 编辑目录
  const handleEditDirectory = (directory: ContentTreeNode) => {
    setFormMode('edit');
    setCurrentDirectory(directory);

    // 查找父目录
    if (directory.parentId !== 0) {
      const parent = findNodeById(treeData, directory.parentId, false);
      setParentDirectory(parent || null);
    } else {
      setParentDirectory(null);
    }

    setFormModalVisible(true);
  };

  // 删除目录
  const handleDeleteDirectory = (directory: ContentTreeNode) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除目录"${directory.directoryName}"吗？删除后将级联删除所有子目录和文档，且无法恢复！`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDirectory(directory.directoryId!);
          message.success('删除成功');
          loadTree();
        } catch (error) {
          console.error('删除目录失败:', error);
        }
      },
    });
  };

  // 创建文档
  const handleCreateDocument = (directory: ContentTreeNode) => {
    setDocFormDirectory(directory);
    setDocFormVisible(true);
  };

  // 删除文档
  const handleDeleteDocument = (node: ContentTreeNode) => {
    Modal.confirm({
      title: '确认删除',
      content: `确定要删除文档"${node.docTitle}"吗？删除后无法恢复！`,
      okText: '确认',
      okType: 'danger',
      cancelText: '取消',
      onOk: async () => {
        try {
          await deleteDocument(node.documentId!);
          message.success('删除成功');
          loadTree();
        } catch (error) {
          console.error('删除文档失败:', error);
          message.error('删除文档失败');
        }
      },
    });
  };

  // 导出文档（Markdown 格式）
  const handleExportMarkdown = async (node: ContentTreeNode) => {
    if (!node.docGuid) {
      message.error('文档 GUID 不存在，无法导出');
      return;
    }

    const hide = message.loading('正在导出 Markdown...', 0);
    try {
      // 获取完整的文档数据
      const response = await getDocumentByGuid(node.docGuid);
      if (response.success && response.data) {
        const success = exportToMarkdown(response.data);
        if (success) {
          hide();
          message.success('Markdown 导出成功');
        } else {
          hide();
          message.error('Markdown 导出失败');
        }
      } else {
        hide();
        message.error('获取文档内容失败');
      }
    } catch (error) {
      hide();
      console.error('导出 Markdown 失败:', error);
      message.error('导出 Markdown 失败');
    }
  };

  // 导出文档（PDF 格式）
  const handleExportPDF = async (node: ContentTreeNode) => {
    if (!node.docGuid) {
      message.error('文档 GUID 不存在，无法导出');
      return;
    }

    const hide = message.loading('正在生成 PDF，请稍候...', 0);
    try {
      // 获取完整的文档数据
      const response = await getDocumentByGuid(node.docGuid);
      if (response.success && response.data) {
        const success = await exportToPDF(response.data);
        if (success) {
          hide();
          message.success('PDF 导出成功');
        } else {
          hide();
          message.error('PDF 导出失败');
        }
      } else {
        hide();
        message.error('获取文档内容失败');
      }
    } catch (error) {
      hide();
      console.error('导出 PDF 失败:', error);
      message.error('导出 PDF 失败');
    }
  };

  // 拖拽放置（仅支持目录）
  const onDrop = async (info: any) => {
    const dropNode = info.node.data as ContentTreeNode;
    const dragNode = info.dragNode.data as ContentTreeNode;

    // 只支持目录拖拽
    if (dragNode.nodeType !== TreeNodeType.DIRECTORY) {
      message.warning('文档节点不支持拖拽');
      return;
    }

    if (dropNode.nodeType !== TreeNodeType.DIRECTORY) {
      message.warning('不能拖拽到文档节点上');
      return;
    }

    const dropKey = dropNode.key;
    const dragKey = dragNode.key;

    // 不能拖到自己上
    if (dragKey === dropKey) {
      message.warning('不能拖拽到自己上');
      return;
    }

    try {
      const dropPos = info.node.pos.split('-');
      const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);

      // 判断是否是层级迁移
      if (!info.dropToGap) {
        // 层级迁移：拖到目标节点上，成为其子节点
        await moveDirectory({
          id: dragNode.directoryId!,
          targetParentId: dropNode.directoryId!,
          targetSort: 0,
        });
        message.success('移动成功');
        loadTree();
      } else {
        // 同级排序：拖到目标节点的上方或下方
        await moveDirectory({
          id: dragNode.directoryId!,
          targetParentId: dropNode.parentId,
          targetSort: dropPosition < 0 ? dropNode.sort - 1 : dropNode.sort + 1,
        });
        message.success('移动成功');
        loadTree();
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

  // 文档创建成功回调
  const handleDocumentSuccess = (docGuid: string) => {
    setDocFormVisible(false);
    loadTree();
    // 自动打开新建的文档
    if (docFormDirectory) {
      onDocumentOpen?.(docGuid, { ...docFormDirectory, docGuid } as ContentTreeNode);
    }
  };

  // 右键菜单
  const getContextMenu = (node: ContentTreeNode): MenuProps['items'] => {
    if (node.nodeType === TreeNodeType.DIRECTORY) {
      return [
        {
          key: 'create-doc',
          label: '新建文档',
          icon: <FileAddOutlined />,
          onClick: () => {
            setDropdownOpenKey(null);
            handleCreateDocument(node);
          },
        },
        {
          key: 'create-dir',
          label: '新建子目录',
          icon: <FolderAddOutlined />,
          onClick: () => {
            setDropdownOpenKey(null);
            handleCreateDirectory(node);
          },
        },
        {
          type: 'divider',
        },
        {
          key: 'edit',
          label: '编辑目录',
          icon: <EditOutlined />,
          onClick: () => {
            setDropdownOpenKey(null);
            handleEditDirectory(node);
          },
        },
        {
          key: 'delete',
          label: '删除目录',
          icon: <DeleteOutlined />,
          danger: true,
          onClick: () => {
            setDropdownOpenKey(null);
            handleDeleteDirectory(node);
          },
        },
      ];
    } else {
      // 文档节点菜单
      return [
        {
          key: 'open',
          label: '打开',
          icon: <FileTextOutlined />,
          onClick: () => {
            setDropdownOpenKey(null);
            if (node.docGuid) {
              onDocumentOpen?.(node.docGuid, node);
            } else {
              message.error('文档 GUID 不存在，无法打开');
            }
          },
        },
        {
          type: 'divider',
        },
        {
          key: 'export',
          label: '导出',
          icon: <DownloadOutlined />,
          children: [
            {
              key: 'export-markdown',
              label: 'Markdown (.md)',
              icon: <FileMarkdownOutlined />,
              onClick: () => {
                setDropdownOpenKey(null);
                handleExportMarkdown(node);
              },
            },
            {
              key: 'export-pdf',
              label: 'PDF (.pdf)',
              icon: <FilePdfOutlined />,
              onClick: () => {
                setDropdownOpenKey(null);
                handleExportPDF(node);
              },
            },
          ],
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
            handleDeleteDocument(node);
          },
        },
      ];
    }
  };

  // 自定义树节点标题（完全自控：图标 + 文字 + 操作按钮）
  const renderTreeTitle = (node: any) => {
    const nodeData = node.data as ContentTreeNode;
    const isHover = hoverKey === node.key;
    const isDropdownOpen = dropdownOpenKey === node.key;
    const isExpanded = expandedKeys.includes(node.key);
    const isSearching = !!searchValue;

    const isDirectory = nodeData.nodeType === TreeNodeType.DIRECTORY;
    const isDocument = nodeData.nodeType === TreeNodeType.DOCUMENT;

    // 选择图标
    let IconComponent: React.ComponentType<any> = BsFolder2;
    let iconColor = '#1890ff';

    if (isDirectory) {
      IconComponent = isExpanded ? BsFolder2Open : BsFolder2;
      iconColor = isSearching && node.title !== nodeData.directoryName ? '#faad14' : '#1890ff';
    } else if (isDocument) {
      IconComponent = IoDocumentTextOutline;
    }

    // 点击节点内容区域
    const handleNodeContentClick = (e: React.MouseEvent) => {
      // 对于目录，切换展开状态
      if (isDirectory && node.children && node.children.length > 0) {
        if (isExpanded) {
          // 折叠时，需要移除当前节点及其所有子孙节点的 key
          const keysToRemove = getAllDescendantKeys(nodeData);
          setExpandedKeys(expandedKeys.filter((k) => !keysToRemove.includes(k)));
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
          onClick={handleNodeContentClick}
          style={{ display: 'flex', alignItems: 'center', gap: '6px', flex: 1, cursor: 'pointer' }}
        >
          {/* 文件夹/文档图标 */}
          <IconComponent className="tree-node-icon" style={{ color: iconColor }} />

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

  // 空间切换菜单
  const getSpaceMenuItems = (): MenuProps['items'] => {
    const items: MenuProps['items'] = [
      {
        key: 'home',
        label: (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <GrHomeRounded style={{ fontSize: 16 }} />
            <span>空间首页</span>
          </div>
        ),
        onClick: () => {
          history.push('/kb/space');
        },
      },
    ];

    // 如果有空间列表，添加分隔线
    if (spaceList.length > 0) {
      items.push({
        key: 'divider',
        type: 'divider',
      });
    }

    // 添加空间列表（显示最多 10 个空间）
    // 判断是前台还是后台路由
    const isAdminRoute = location.pathname.startsWith('/kb-admin');

    const spaceItems = spaceList
      .slice(0, 10) // 最多显示 10 个
      .map((space) => {
        const isCurrent = space.id === spaceId;
        return {
          key: `space-${space.id}`,
          label: (
            <div className="space-menu-item">
              <FiBox style={{ fontSize: 14, color: '#1890ff' }} />
              <span className="space-menu-label">{space.spaceName}</span>
              {isCurrent && <div className="current-indicator" />}
            </div>
          ),
          disabled: isCurrent, // 当前空间禁用点击
          onClick: () => {
            if (!isCurrent) {
              // 根据当前路由判断跳转路径
              if (isAdminRoute) {
                // 后台管理：使用 spaceId
                history.push(`/kb-admin/space/detail/${space.id}`);
              } else {
                // 前台用户：使用 spaceCode
                history.push(`/kb/space/${space.spaceCode}`);
              }
            }
          },
        };
      });

    return [...items, ...spaceItems];
  };

  return (
    <div className="directory-tree-container">
      {/* 顶部工具栏 */}
      <div className="tree-toolbar">
        <div className="toolbar-row">
          <Input
            placeholder="搜索目录或文档..."
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
              draggable={(node: any) => {
                // 只有目录节点可拖拽
                const nodeData = node.data as ContentTreeNode;
                return nodeData.nodeType === TreeNodeType.DIRECTORY;
              }}
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
              description={searchValue ? '未找到匹配的目录或文档' : '暂无目录'}
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
        directory={currentDirectory ? {
          id: currentDirectory.directoryId!,
          directoryName: currentDirectory.directoryName!,
          directoryPath: currentDirectory.directoryPath!,
          parentId: currentDirectory.parentId,
          spaceId: spaceId,
          sort: currentDirectory.sort || 0,
          depthLevel: currentDirectory.depthLevel || 0,
          key: currentDirectory.key,
          title: currentDirectory.title,
          isLeaf: currentDirectory.isLeaf || false,
        } : null}
        parentDirectory={parentDirectory ? {
          id: parentDirectory.directoryId!,
          directoryName: parentDirectory.directoryName!,
          directoryPath: parentDirectory.directoryPath!,
          parentId: parentDirectory.parentId,
          spaceId: spaceId,
          sort: parentDirectory.sort || 0,
          depthLevel: parentDirectory.depthLevel || 0,
          key: parentDirectory.key,
          title: parentDirectory.title,
          isLeaf: parentDirectory.isLeaf || false,
        } : null}
        onSuccess={handleFormSuccess}
        onCancel={() => setFormModalVisible(false)}
      />

      {/* 文档表单弹窗 */}
      <DocumentFormModal
        visible={docFormVisible}
        spaceId={spaceId}
        directoryId={docFormDirectory?.directoryId}
        onSuccess={handleDocumentSuccess}
        onCancel={() => setDocFormVisible(false)}
      />

      {/* 底部空间切换器 */}
      {showBackButton && (
        <div className="tree-footer">
          <Dropdown
            menu={{ items: getSpaceMenuItems() }}
            trigger={['click']}
            placement="topLeft"
            overlayClassName="space-switcher-dropdown"
            disabled={loadingSpaces}
          >
            <button
              type="button"
              className="space-switcher-btn"
              title="切换空间"
            >
              <FiBox className="space-icon" />
              <span className="space-name">{spaceName || '当前空间'}</span>
              <SwapOutlined className="switch-icon" />
            </button>
          </Dropdown>
        </div>
      )}
    </div>
  );
};

export default DirectoryTree;
