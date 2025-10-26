import {
  BookOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  FileTextOutlined,
  FolderOutlined,
  GlobalOutlined,
  LockOutlined,
  SafetyOutlined,
  UserOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams } from '@umijs/max';
import {
  Avatar,
  Badge,
  Card,
  Col,
  Descriptions,
  Empty,
  message,
  Row,
  Space,
  Spin,
  Statistic,
  Tag,
  Typography,
  Modal,
  Input,
} from 'antd';
import React, { useEffect, useState } from 'react';
import type { ContentSpaceDetail } from '@/services/kb/typings';
import {
  getContentSpaceDetailByCode,
  validateContentSpaceAccess,
} from '@/services/kb/space';
import { AccessType, PublishStatus, SpaceType } from '@/services/kb/typings';

const { Title, Text, Paragraph } = Typography;

/**
 * 空间详情页面
 * 展示空间的详细信息、统计数据，以及空间下的内容
 */
const ContentSpaceDetail: React.FC = () => {
  const { spaceCode } = useParams<{ spaceCode: string }>();
  const [loading, setLoading] = useState(false);
  const [space, setSpace] = useState<ContentSpaceDetail | null>(null);
  const [passwordModalVisible, setPasswordModalVisible] = useState(false);
  const [password, setPassword] = useState('');
  const [validating, setValidating] = useState(false);

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
          // 密码保护的空间，需要验证密码
          setPasswordModalVisible(true);
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

  // 验证访问密码
  const handleValidatePassword = async () => {
    if (!password) {
      message.warning('请输入访问密码');
      return;
    }

    setValidating(true);
    try {
      const response = await validateContentSpaceAccess(space!.id, password);
      if (response.success && response.data) {
        message.success('密码正确，欢迎访问');
        setPasswordModalVisible(false);
      } else {
        message.error('密码错误');
      }
    } catch (error) {
      console.error('验证密码失败:', error);
    } finally {
      setValidating(false);
    }
  };

  // 空间类型配置
  const getSpaceTypeConfig = (type: SpaceType) => {
    switch (type) {
      case SpaceType.PERSONAL:
        return { icon: <BookOutlined />, color: '#1890ff', text: '个人知识库' };
      case SpaceType.COURSE:
        return { icon: <FileTextOutlined />, color: '#52c41a', text: '课程专栏' };
      case SpaceType.VIDEO:
        return { icon: <FileTextOutlined />, color: '#fa8c16', text: '视频专栏' };
      default:
        return { icon: <BookOutlined />, color: '#1890ff', text: '未知类型' };
    }
  };

  // 访问类型配置
  const getAccessTypeConfig = (type: AccessType) => {
    switch (type) {
      case AccessType.PRIVATE:
        return { icon: <LockOutlined />, color: 'default', text: '私有' };
      case AccessType.PUBLIC:
        return { icon: <GlobalOutlined />, color: 'success', text: '公开' };
      case AccessType.PASSWORD_PROTECTED:
        return { icon: <SafetyOutlined />, color: 'warning', text: '密码访问' };
      default:
        return { icon: <LockOutlined />, color: 'default', text: '未知' };
    }
  };

  if (loading) {
    return (
      <PageContainer>
        <Card>
          <Spin tip="加载中..." style={{ display: 'block', textAlign: 'center', padding: 60 }} />
        </Card>
      </PageContainer>
    );
  }

  if (!space) {
    return (
      <PageContainer>
        <Card>
          <Empty description="空间不存在" />
        </Card>
      </PageContainer>
    );
  }

  const spaceTypeConfig = getSpaceTypeConfig(space.spaceType);
  const accessTypeConfig = getAccessTypeConfig(space.accessType);

  return (
    <PageContainer
      title={space.spaceName}
      tags={[
        <Tag key="type" color={spaceTypeConfig.color} icon={spaceTypeConfig.icon}>
          {space.spaceTypeDesc}
        </Tag>,
        <Badge
          key="status"
          status={space.isPublished === PublishStatus.PUBLISHED ? 'success' : 'default'}
          text={space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
        />,
      ]}
      extra={[
        <Tag key="access" color={accessTypeConfig.color} icon={accessTypeConfig.icon}>
          {space.accessTypeDesc}
        </Tag>,
      ]}
      content={
        <Space direction="vertical" size={16} style={{ width: '100%' }}>
          {space.coverImage && (
            <img
              src={space.coverImage}
              alt={space.spaceName}
              style={{
                width: '100%',
                maxHeight: 300,
                objectFit: 'cover',
                borderRadius: 8,
              }}
            />
          )}
          <Paragraph style={{ fontSize: 16, color: '#666' }}>
            {space.spaceDesc || '暂无描述'}
          </Paragraph>
        </Space>
      }
    >
      <Row gutter={[16, 16]}>
        {/* 统计信息 */}
        <Col xs={24} sm={24} md={6}>
          <Card>
            <Statistic
              title="浏览次数"
              value={space.viewCount}
              prefix={<EyeOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="文档数量"
              value={space.documentCount}
              prefix={<FileTextOutlined />}
              valueStyle={{ color: '#1890ff' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card>
            <Statistic
              title="目录数量"
              value={space.directoryCount}
              prefix={<FolderOutlined />}
              valueStyle={{ color: '#fa8c16' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={24} md={6}>
          <Card>
            <Statistic
              title="发布时间"
              value={space.publishTime || '未发布'}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ fontSize: 14 }}
            />
          </Card>
        </Col>

        {/* 空间详情 */}
        <Col span={24}>
          <Card title="空间信息" bordered={false}>
            <Descriptions column={{ xs: 1, sm: 2, md: 3 }}>
              <Descriptions.Item label="空间编码">{space.spaceCode}</Descriptions.Item>
              <Descriptions.Item label="拥有者">
                <Space>
                  <Avatar size="small" icon={<UserOutlined />} />
                  {space.ownerName || `ID:${space.ownerId}`}
                </Space>
              </Descriptions.Item>
              <Descriptions.Item label="排序">{space.sort}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{space.createTime}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{space.updateTime}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Badge status={space.status === 0 ? 'success' : 'error'} text={space.status === 0 ? '正常' : '停用'} />
              </Descriptions.Item>
              {space.remark && (
                <Descriptions.Item label="备注" span={3}>
                  {space.remark}
                </Descriptions.Item>
              )}
            </Descriptions>
          </Card>
        </Col>

        {/* 内容区域 - 待开发：文档列表、目录树等 */}
        <Col span={24}>
          <Card title="空间内容" bordered={false}>
            <Empty description="暂无内容，敬请期待" />
          </Card>
        </Col>
      </Row>

      {/* 密码验证模态框 */}
      <Modal
        title="请输入访问密码"
        open={passwordModalVisible}
        onOk={handleValidatePassword}
        onCancel={() => {
          setPasswordModalVisible(false);
          history.push('/kb/space');
        }}
        confirmLoading={validating}
        closable={false}
        maskClosable={false}
      >
        <Space direction="vertical" style={{ width: '100%' }}>
          <Text type="secondary">此空间需要密码才能访问</Text>
          <Input.Password
            placeholder="请输入访问密码"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onPressEnter={handleValidatePassword}
            size="large"
          />
        </Space>
      </Modal>
    </PageContainer>
  );
};

export default ContentSpaceDetail;

