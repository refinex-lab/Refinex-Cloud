import {
  ArrowLeftOutlined,
  FileTextOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams } from '@umijs/max';
import { Button, Card, Empty, Layout, Spin, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import DirectoryTree from '../components/DirectoryTree';
import type { ContentDirectory, ContentSpaceDetail } from '@/services/kb/typings.d';
import { getContentSpaceDetail } from '@/services/kb/space';
import './detail.less';

const { Sider, Content } = Layout;
const { Title, Paragraph } = Typography;

const SpaceDetail: React.FC = () => {
  const { spaceId } = useParams<{ spaceId: string }>();
  const [space, setSpace] = useState<ContentSpaceDetail | null>(null);
  const [loading, setLoading] = useState(false);
  const [selectedDirectory, setSelectedDirectory] = useState<ContentDirectory | null>(null);
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

  // 目录选中回调
  const handleDirectorySelect = (directoryId: number | null, directory: ContentDirectory | null) => {
    setSelectedDirectory(directory);
    // TODO: 加载目录下的文档列表
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
          {selectedDirectory ? (
            <div className="directory-content">
              <div className="directory-header">
                <FileTextOutlined className="directory-icon" />
                <Title level={3} style={{ margin: 0 }}>
                  {selectedDirectory.directoryName}
                </Title>
              </div>

              {selectedDirectory.remark && (
                <Paragraph
                  type="secondary"
                  style={{ marginTop: 8, marginBottom: 24, fontSize: 14 }}
                >
                  {selectedDirectory.remark}
                </Paragraph>
              )}

              <div className="document-list">
                <Empty
                  image={Empty.PRESENTED_IMAGE_SIMPLE}
                  description="文档管理功能即将上线"
                  style={{ marginTop: 60 }}
                >
                  <Paragraph type="secondary" style={{ fontSize: 14 }}>
                    敬请期待...
                  </Paragraph>
                </Empty>
              </div>
            </div>
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
                      请从左侧选择一个目录开始管理您的知识库内容
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
