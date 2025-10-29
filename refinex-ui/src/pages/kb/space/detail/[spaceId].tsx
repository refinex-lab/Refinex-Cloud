import {
  ArrowLeftOutlined,
  FileTextOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams, useLocation } from '@umijs/max';
import { Button, Card, Empty, Layout, Spin, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import DirectoryTree from '../components/DirectoryTree';
import DocumentEditor from '../components/DocumentEditor';
import DirectoryView from '../components/DirectoryView';
import type { ContentDirectory, ContentSpaceDetail, ContentTreeNode } from '@/services/kb/typings.d';
import { getContentSpaceDetail } from '@/services/kb/space';
import './detail.less';

const { Sider, Content } = Layout;
const { Title, Paragraph } = Typography;

const SpaceDetail: React.FC = () => {
  const { spaceId } = useParams<{ spaceId: string }>();
  const location = useLocation();
  const [space, setSpace] = useState<ContentSpaceDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [selectedNode, setSelectedNode] = useState<ContentTreeNode | null>(null);
  const [currentDocGuid, setCurrentDocGuid] = useState<string | null>(null);
  const [viewMode, setViewMode] = useState<'directory' | 'document'>('directory');
  const [collapsed, setCollapsed] = useState(false);
  const [siderWidth, setSiderWidth] = useState(280);

  // 加载空间信息
  const loadSpace = async () => {
    if (!spaceId) return;

    setLoading(true);
    try {
      const response = await getContentSpaceDetail(Number(spaceId));
      if (response.success && response.data) {
        setSpace(response.data);
      }
    } catch (error) {
      console.error('加载空间信息失败:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSpace();
  }, [spaceId]);

  // 从 URL 读取文档 GUID
  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const urlDocGuid = searchParams.get('doc');
    if (urlDocGuid && urlDocGuid !== currentDocGuid) {
      setCurrentDocGuid(urlDocGuid);
      setViewMode('document');
    }
  }, [location.search]);

  // 目录选中回调
  const handleDirectorySelect = (node: ContentTreeNode | null) => {
    setSelectedNode(node);
    setViewMode('directory');
    setCurrentDocGuid(null);
  };

  // 文档打开回调
  const handleDocumentOpen = (docGuid: string, node: ContentTreeNode) => {
    console.log('handleDocumentOpen 被调用');
    console.log('docGuid:', docGuid);
    console.log('node:', node);

    setCurrentDocGuid(docGuid);
    setViewMode('document');
    setSelectedNode(node);

    // 更新 URL
    history.replace(`/kb/space/detail/${spaceId}?doc=${docGuid}`);

    console.log('URL已更新，currentDocGuid:', docGuid);
    console.log('viewMode:', 'document');
  };

  // 关闭文档编辑器
  const handleCloseEditor = () => {
    setCurrentDocGuid(null);
    setViewMode('directory');
    history.replace(`/kb/space/detail/${spaceId}`);
  };

  // 返回空间列表
  const handleBack = () => {
    history.push('/kb-admin/space');
  };

  return (
    <PageContainer
      title={false}
      loading={loading}
      className="space-detail-container"
    >
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
          {spaceId && (
            <DirectoryTree
              spaceId={Number(spaceId)}
              onSelect={handleDirectorySelect}
              onDocumentOpen={handleDocumentOpen}
              selectedKey={currentDocGuid || selectedNode?.key}
              showBackButton
              onBack={handleBack}
              spaceName={space?.spaceName}
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
          {viewMode === 'document' && currentDocGuid ? (
            <DocumentEditor
              docGuid={currentDocGuid}
              spaceId={Number(spaceId)}
              onClose={handleCloseEditor}
            />
          ) : selectedNode && selectedNode.nodeType === 'directory' ? (
            <DirectoryView
              directory={selectedNode}
              spaceId={Number(spaceId)}
              onDocumentOpen={(docGuid, doc) => handleDocumentOpen(docGuid, doc)}
              onCreateDocument={(directoryId) => {
                // TODO: 打开新建文档弹窗
                console.log('创建文档在目录:', directoryId);
              }}
            />
          ) : (
            <div className="welcome-content">
              <Empty
                image="/images/welcome-kb.svg"
                imageStyle={{ height: 200 }}
                description={
                  <div>
                    <Title level={4} style={{ marginBottom: 8 }}>
                      欢迎使用知识库管理系统
                    </Title>
                    <Paragraph type="secondary" style={{ fontSize: 14 }}>
                      请从左侧选择一个目录或文档开始管理您的知识库内容
                    </Paragraph>
                    {collapsed && (
                      <Paragraph type="secondary" style={{ fontSize: 13, marginTop: 12 }}>
                        <Button
                          type="link"
                          icon={<MenuUnfoldOutlined />}
                          onClick={() => setCollapsed(false)}
                        >
                          展开目录树
                        </Button>
                      </Paragraph>
                    )}
                  </div>
                }
              />
            </div>
          )}
        </Content>
      </Layout>
    </PageContainer>
  );
};

export default SpaceDetail;
