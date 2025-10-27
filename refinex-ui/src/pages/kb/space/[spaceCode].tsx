import {
  ArrowLeftOutlined,
  FileTextOutlined,
  LockOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  SafetyOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams } from '@umijs/max';
import { Button, Card, Empty, Input, Layout, message, Space, Spin, Typography } from 'antd';
import React, { useEffect, useState } from 'react';
import DirectoryTree from './components/DirectoryTree';
import type { ContentDirectory, ContentSpaceDetail } from '@/services/kb/typings.d';
import { getContentSpaceDetailByCode, validateContentSpaceAccess } from '@/services/kb/space';
import { AccessType } from '@/services/kb/typings.d';
import { encryptPassword, getRsaPublicKey } from '@/utils/crypto';
import './detail/detail.less';

const { Sider, Content } = Layout;
const { Title, Text, Paragraph } = Typography;

/**
 * 空间详情页面 - 用户端
 * 展示知识库目录树和内容
 */
const ContentSpaceDetail: React.FC = () => {
  const { spaceCode } = useParams<{ spaceCode: string }>();
  const [loading, setLoading] = useState(false);
  const [space, setSpace] = useState<ContentSpaceDetail | null>(null);
  const [isLocked, setIsLocked] = useState(false); // 是否处于锁定状态
  const [password, setPassword] = useState('');
  const [validating, setValidating] = useState(false);
  const [selectedDirectory, setSelectedDirectory] = useState<ContentDirectory | null>(null);
  const [collapsed, setCollapsed] = useState(false);
  const [siderWidth] = useState(280);

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

  // 目录选中回调
  const handleDirectorySelect = (directoryId: number | null, directory: ContentDirectory | null) => {
    setSelectedDirectory(directory);
    // TODO: 加载目录下的文档列表
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
              boxShadow: '0 4px 12px rgba(0, 0, 0, 0.08)',
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
                <SafetyOutlined /> 您的密码使用 RSA + AES 混合加密传输，请放心输入
              </Text>
            </Space>
          </Card>
        </div>
      </PageContainer>
    );
  }

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
          {space?.id && (
            <DirectoryTree
              spaceId={space.id}
              onSelect={handleDirectorySelect}
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
                      欢迎访问 {space?.spaceName || '知识库'}
                    </Title>
                    <Paragraph type="secondary" style={{ fontSize: 14 }}>
                      请从左侧选择一个目录开始浏览知识库内容
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

export default ContentSpaceDetail;

