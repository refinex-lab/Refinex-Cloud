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
  ArrowLeftOutlined,
} from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import { history, useParams } from '@umijs/max';
import {
  Avatar,
  Badge,
  Button,
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
  Input,
} from 'antd';
import React, { useEffect, useState } from 'react';
import type { ContentSpaceDetail as ContentSpaceDetailType } from '@/services/kb/typings';
import {
  getContentSpaceDetailByCode,
  validateContentSpaceAccess,
} from '@/services/kb/space';
import { AccessType, PublishStatus, SpaceType } from '@/services/kb/typings.d';
import { listDictDataByTypeCode } from '@/services/system/dictionary';
import { encryptPassword, getRsaPublicKey } from '@/utils/crypto';

const { Title, Text, Paragraph } = Typography;

/**
 * 空间详情页面
 * 展示空间的详细信息、统计数据，以及空间下的内容
 */
const ContentSpaceDetail: React.FC = () => {
  const { spaceCode } = useParams<{ spaceCode: string }>();
  const [loading, setLoading] = useState(false);
  const [space, setSpace] = useState<ContentSpaceDetailType | null>(null);
  const [isLocked, setIsLocked] = useState(false); // 是否处于锁定状态
  const [password, setPassword] = useState('');
  const [validating, setValidating] = useState(false);

  // 字典数据映射
  const [spaceTypeDictMap, setSpaceTypeDictMap] = useState<Record<number, string>>({});
  const [accessTypeDictMap, setAccessTypeDictMap] = useState<Record<number, string>>({});

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

  // 加载字典数据
  const loadDictionaries = async () => {
    try {
      const [spaceTypeRes, accessTypeRes] = await Promise.all([
        listDictDataByTypeCode('kb_space_type'),
        listDictDataByTypeCode('kb_access_type'),
      ]);

      if (spaceTypeRes.success && spaceTypeRes.data) {
        const typeMap: Record<number, string> = {};
        spaceTypeRes.data.forEach((item) => {
          typeMap[Number(item.dictValue)] = item.dictLabel;
        });
        setSpaceTypeDictMap(typeMap);
      }

      if (accessTypeRes.success && accessTypeRes.data) {
        const accessMap: Record<number, string> = {};
        accessTypeRes.data.forEach((item) => {
          accessMap[Number(item.dictValue)] = item.dictLabel;
        });
        setAccessTypeDictMap(accessMap);
      }
    } catch (error) {
      console.error('加载字典数据失败:', error);
    }
  };

  useEffect(() => {
    loadDictionaries();
    loadSpaceDetail();
  }, [spaceCode]);

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

  // 空间类型配置
  const getSpaceTypeConfig = (type: SpaceType) => {
    const text = spaceTypeDictMap[type] || '未知类型';
    switch (type) {
      case SpaceType.PERSONAL:
        return { icon: <BookOutlined />, color: '#1890ff', text };
      case SpaceType.COURSE:
        return { icon: <FileTextOutlined />, color: '#52c41a', text };
      case SpaceType.VIDEO:
        return { icon: <FileTextOutlined />, color: '#fa8c16', text };
      default:
        return { icon: <BookOutlined />, color: '#1890ff', text };
    }
  };

  // 访问类型配置
  const getAccessTypeConfig = (type: AccessType) => {
    const text = accessTypeDictMap[type] || '未知';
    switch (type) {
      case AccessType.PRIVATE:
        return { icon: <LockOutlined />, color: 'default', text };
      case AccessType.PUBLIC:
        return { icon: <GlobalOutlined />, color: 'success', text };
      case AccessType.PASSWORD_PROTECTED:
        return { icon: <SafetyOutlined />, color: 'warning', text };
      default:
        return { icon: <LockOutlined />, color: 'default', text };
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
                  {space.spaceName}
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
      title={space.spaceName}
      tags={[
        <Tag key="type" color={spaceTypeConfig.color} icon={spaceTypeConfig.icon}>
          {spaceTypeDictMap[space.spaceType] || space.spaceTypeDesc}
        </Tag>,
        <Badge
          key="status"
          status={space.isPublished === PublishStatus.PUBLISHED ? 'success' : 'default'}
          text={space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
        />,
      ]}
      extra={[
        <Tag key="access" color={accessTypeConfig.color} icon={accessTypeConfig.icon}>
          {accessTypeDictMap[space.accessType] || space.accessTypeDesc}
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
    </PageContainer>
  );
};

export default ContentSpaceDetail;

