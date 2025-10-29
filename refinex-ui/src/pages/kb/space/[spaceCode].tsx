import {
  ArrowLeftOutlined,
  FileTextOutlined,
  LockOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams, useLocation } from '@umijs/max';
import { Button, Card, Empty, Input, Layout, message, Space, Spin, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import DirectoryTree from './components/DirectoryTree';
import DocumentEditor from './components/DocumentEditor';
import DirectoryView from './components/DirectoryView';
import DocumentFormModal from './components/DocumentFormModal';
import type { ContentSpaceDetail, ContentTreeNode } from '@/services/kb/typings.d';
import { AccessType, TreeNodeType } from '@/services/kb/typings.d';
import { getContentSpaceDetailByCode, validateContentSpaceAccess } from '@/services/kb/space';
import { encryptPassword, getRsaPublicKey } from '@/utils/crypto';
import './detail/detail.less';

const { Sider, Content } = Layout;
const { Title, Text } = Typography;

/**
 * 空间详情页面 - 用户端
 * 展示知识库目录树和内容（支持目录视图和文档编辑）
 */
const ContentSpaceDetail: React.FC = () => {
  const { spaceCode } = useParams<{ spaceCode: string }>();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [space, setSpace] = useState<ContentSpaceDetail | null>(null);
  const [isLocked, setIsLocked] = useState(false); // 是否处于锁定状态
  const [password, setPassword] = useState('');
  const [validating, setValidating] = useState(false);
  const [collapsed, setCollapsed] = useState(false);
  const [siderWidth] = useState(280);

  // 状态管理
  const [selectedNode, setSelectedNode] = useState<ContentTreeNode | null>(null);
  const [currentDocGuid, setCurrentDocGuid] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'directory' | 'document' | 'empty'>('empty');

  // 文档弹窗状态
  const [docFormVisible, setDocFormVisible] = useState(false);
  const [docFormDirectory, setDocFormDirectory] = useState<ContentTreeNode | null>(null);

  // 加载空间详情
  const loadSpaceDetail = async () => {
    if (!spaceCode) {
      message.error('空间编码不存在');
      history.push('/kb/space');
      return;
    }

    setLoading(true);
    try {
      const response = await getContentSpaceDetailByCode(spaceCode);
      if (response.success && response.data) {
        setSpace(response.data);

        // 检查访问权限
        if (response.data.accessType === AccessType.PASSWORD_PROTECTED) {
          // 密码保护的空间，显示锁定页面
          setIsLocked(true);
        }
      }
    } catch (error: any) {
      console.error('加载空间详情失败:', error);
      if (error.response?.status === 403) {
        message.error('无权访问此空间');
        history.push('/kb/space');
      } else {
        message.error('加载空间详情失败');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSpaceDetail();
  }, [spaceCode]);

  // URL 同步：从 URL 读取文档 GUID
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const urlDocGuid = searchParams.get('doc');

    if (urlDocGuid && urlDocGuid !== currentDocGuid) {
      setCurrentDocGuid(urlDocGuid);
      setViewMode('document');
    } else if (!urlDocGuid && currentDocGuid) {
      // URL 中没有文档参数，但当前有打开的文档，清空状态
      setCurrentDocGuid(null);
      if (selectedNode && selectedNode.nodeType === TreeNodeType.DIRECTORY) {
        setViewMode('directory');
      } else {
        setViewMode('empty');
      }
    }
  }, [location.search]);

  // 目录选中回调
  const handleDirectorySelect = (node: ContentTreeNode | null) => {
    setSelectedNode(node);
    setViewMode('directory');
    setCurrentDocGuid(null);
    // 清除 URL 中的文档参数
    if (location.search.includes('doc=')) {
      history.replace(`/kb/space/${spaceCode}`);
    }
  };

  // 文档打开回调
  const handleDocumentOpen = (docGuid: string, node: ContentTreeNode) => {
    setCurrentDocGuid(docGuid);
    setViewMode('document');
    setSelectedNode(node);

    // 更新 URL（使用 history.replace 避免历史记录堆积）
    history.replace(`/kb/space/${spaceCode}?doc=${docGuid}`);
  };

  // 关闭文档编辑器
  const handleCloseEditor = () => {
    setCurrentDocGuid(null);
    if (selectedNode && selectedNode.nodeType === TreeNodeType.DIRECTORY) {
      setViewMode('directory');
    } else {
      setViewMode('empty');
    }
    history.replace(`/kb/space/${spaceCode}`);
  };

  // 返回空间列表
  const handleBack = () => {
    history.push('/kb/space');
  };

  // 验证访问密码
  const handleValidatePassword = async () => {
    if (!password) {
      message.warning('请输入访问密码');
      return;
    }

    if (!space?.id) {
      message.error('空间信息异常');
      return;
    }

    setValidating(true);
    try {
      // 使用 RSA + AES 混合加密密码
      let encryptedData: string;
      try {
        const publicKey = getRsaPublicKey();
        const result = await encryptPassword(password, publicKey);
        // 格式化为后端期望的格式：encryptedKey|encryptedData
        encryptedData = `${result.encryptedKey}|${result.encryptedData}`;
      } catch (error) {
        console.error('密码加密失败:', error);
        message.error('密码加密失败，请检查系统配置');
        setValidating(false);
        return;
      }

      const response = await validateContentSpaceAccess(space.id, encryptedData);
      if (response.success && response.data) {
        message.success('密码正确，欢迎访问');
        setIsLocked(false); // 解锁页面
        setPassword(''); // 清空密码
      } else {
        message.error('密码错误，请重试');
      }
    } catch (error) {
      console.error('验证密码失败:', error);
      message.error('验证失败，请稍后重试');
    } finally {
      setValidating(false);
    }
  };

  // 如果空间被锁定，显示密码验证页面
  if (isLocked) {
    return (
      <PageContainer>
        <div
          style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: 'calc(100vh - 200px)',
          }}
        >
          <Card
            style={{
              maxWidth: 480,
              width: '100%',
              textAlign: 'center',
            }}
            bordered={false}
          >
            <Space direction="vertical" size={24} style={{ width: '100%' }}>
              {/* 锁定图标 */}
              <div
                style={{
                  width: 80,
                  height: 80,
                  borderRadius: '50%',
                  backgroundColor: '#fff7e6',
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  margin: '0 auto',
                }}
              >
                <LockOutlined style={{ fontSize: 40, color: '#faad14' }} />
              </div>

              {/* 标题和描述 */}
              <div>
                <Title level={3} style={{ marginBottom: 8 }}>
                  {space?.spaceName || '知识库空间'}
                </Title>
                <Text type="secondary" style={{ fontSize: 16 }}>
                  此空间需要密码才能访问
                </Text>
              </div>

              {/* 密码输入框 */}
              <div style={{ width: '100%' }}>
                <Input.Password
                  placeholder="请输入访问密码"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  onPressEnter={handleValidatePassword}
                  size="large"
                  prefix={<SafetyOutlined />}
                  style={{ fontSize: 16 }}
                />
              </div>

              {/* 操作按钮 */}
              <Space size={12} style={{ width: '100%', justifyContent: 'center' }}>
                <Button
                  size="large"
                  icon={<ArrowLeftOutlined />}
                  onClick={() => history.push('/kb/space')}
                >
                  返回
                </Button>
                <Button
                  type="primary"
                  size="large"
                  loading={validating}
                  onClick={handleValidatePassword}
                  icon={<LockOutlined />}
                  style={{ minWidth: 120 }}
                >
                  {validating ? '验证中' : '确定'}
                </Button>
              </Space>

              {/* 提示信息 */}
              <Text type="secondary" style={{ fontSize: 14 }}>
                <SafetyOutlined /> 您的密码已加密传输，请放心输入
              </Text>
            </Space>
          </Card>
        </div>
      </PageContainer>
    );
  }

  return (
    <PageContainer title={false} loading={loading} className="space-detail-container">
      <Layout className="space-detail-layout">
        {/* 左侧目录树 */}
        <Sider
          className="space-detail-sider"
          width={siderWidth}
          collapsedWidth={0}
          collapsed={collapsed}
          trigger={null}
          theme="light"
          style={{
            overflow: 'hidden',
            height: 'calc(100vh - 112px)',
            position: 'sticky',
            top: 0,
            left: 0,
          }}
        >
          {space?.id && (
            <DirectoryTree
              spaceId={space.id}
              onSelect={handleDirectorySelect}
              onDocumentOpen={handleDocumentOpen}
              selectedKey={currentDocGuid || selectedNode?.key}
              showBackButton
              onBack={handleBack}
              spaceName={space.spaceName}
            />
          )}
        </Sider>

        {/* 折叠/展开按钮 */}
        <div className="sider-trigger">
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            className="trigger-button"
          />
        </div>

        {/* 右侧内容区域 */}
        <Content className="space-detail-content">
          {viewMode === 'document' && currentDocGuid && space ? (
            <DocumentEditor
              docGuid={currentDocGuid}
              spaceId={space.id}
              onClose={handleCloseEditor}
              onTitleChange={(newTitle) => {
              }}
            />
          ) : viewMode === 'directory' && selectedNode && space ? (
            <DirectoryView
              directory={selectedNode}
              spaceId={space.id}
              onDocumentOpen={handleDocumentOpen}
              onCreateDocument={(directoryId) => {
                // 打开新建文档弹窗
                setDocFormDirectory(selectedNode);
                setDocFormVisible(true);
              }}
            />
          ) : (
            <Card className="editor-card" bordered={false} style={{ boxShadow: "none"}}>
              <Empty
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description={
                  <div>
                    <p style={{ fontSize: 16, color: '#8c8c8c' }}>请从左侧选择目录或文档</p>
                    <p style={{ fontSize: 14, color: '#bfbfbf' }}>
                      选择目录可查看文档列表，选择文档可查看和编辑内容
                    </p>
                  </div>
                }
              />
            </Card>
          )}
        </Content>
      </Layout>

      {/* 文档创建弹窗 */}
      <DocumentFormModal
        visible={docFormVisible}
        spaceId={space?.id || 0}
        directoryId={docFormDirectory?.directoryId}
        onSuccess={(docGuid) => {
          setDocFormVisible(false);
          loadSpaceDetail();
          // 自动打开新建的文档
          if (docGuid) {
            handleDocumentOpen(docGuid, docFormDirectory!);
          }
        }}
        onCancel={() => setDocFormVisible(false)}
      />
    </PageContainer>
  );
};

export default ContentSpaceDetail;
