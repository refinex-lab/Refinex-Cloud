import {
  AppstoreOutlined,
  BookOutlined,
  DeleteOutlined,
  EditOutlined,
  EllipsisOutlined,
  EyeInvisibleOutlined,
  EyeOutlined,
  FileTextOutlined,
  FolderOutlined,
  GlobalOutlined,
  LockOutlined,
  PlusOutlined,
  SafetyOutlined,
  SendOutlined,
  SettingOutlined,
  StopOutlined,
  UnorderedListOutlined,
  VideoCameraOutlined,
} from '@ant-design/icons';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
} from '@ant-design/pro-components';
import { history, useIntl } from '@umijs/max';
import {
  Avatar,
  Badge,
  Button,
  Card,
  Col,
  Dropdown,
  Empty,
  Input,
  message,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Spin,
  Statistic,
  Tag,
  Tooltip,
  Typography,
} from 'antd';
import React, { useEffect, useState } from 'react';
import type {
  ContentSpace,
  ContentSpaceCreateRequest,
  ContentSpacePublishRequest,
  ContentSpaceUpdateRequest,
} from '@/services/kb/typings';
import {
  createContentSpace,
  deleteContentSpace,
  getMyContentSpaces,
  publishContentSpace,
  updateContentSpace,
} from '@/services/kb/space';
import { AccessType, PublishStatus, SpaceStatus, SpaceType } from '@/services/kb/typings';

const { Search } = Input;
const { Title, Text, Paragraph } = Typography;
const { Meta } = Card;

/**
 * 用户-我的空间页面
 * 卡片式布局，美观的空间展示，便于用户浏览和管理自己的空间
 */
const MyContentSpace: React.FC = () => {
  const intl = useIntl();
  const [loading, setLoading] = useState(false);
  const [spaces, setSpaces] = useState<ContentSpace[]>([]);
  const [filteredSpaces, setFilteredSpaces] = useState<ContentSpace[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentSpace, setCurrentSpace] = useState<ContentSpace | undefined>();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filterType, setFilterType] = useState<SpaceType | 'all'>('all');
  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');

  // 加载我的空间列表
  const loadMySpaces = async () => {
    setLoading(true);
    try {
      const response = await getMyContentSpaces();
      if (response.success && response.data) {
        setSpaces(response.data);
        setFilteredSpaces(response.data);
      }
    } catch (error) {
      console.error('加载空间列表失败:', error);
      message.error('加载空间列表失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMySpaces();
  }, []);

  // 搜索和过滤
  useEffect(() => {
    let result = spaces;

    // 按关键词搜索
    if (searchKeyword) {
      result = result.filter(
        (space) =>
          space.spaceName.toLowerCase().includes(searchKeyword.toLowerCase()) ||
          space.spaceDesc?.toLowerCase().includes(searchKeyword.toLowerCase()) ||
          space.spaceCode.toLowerCase().includes(searchKeyword.toLowerCase()),
      );
    }

    // 按类型过滤
    if (filterType !== 'all') {
      result = result.filter((space) => space.spaceType === filterType);
    }

    setFilteredSpaces(result);
  }, [searchKeyword, filterType, spaces]);

  // 处理新增/编辑
  const handleSubmit = async (values: ContentSpaceCreateRequest | ContentSpaceUpdateRequest) => {
    try {
      if (currentSpace) {
        // 编辑
        await updateContentSpace(currentSpace.id, {
          ...values,
          version: currentSpace.version,
        } as ContentSpaceUpdateRequest);
        message.success('更新空间成功');
      } else {
        // 新增
        await createContentSpace(values as ContentSpaceCreateRequest);
        message.success('创建空间成功');
      }
      setModalVisible(false);
      setCurrentSpace(undefined);
      loadMySpaces();
      return true;
    } catch (error) {
      console.error('提交空间信息失败:', error);
      return false;
    }
  };

  // 处理删除
  const handleDelete = async (space: ContentSpace) => {
    try {
      await deleteContentSpace(space.id);
      message.success('删除空间成功');
      loadMySpaces();
    } catch (error) {
      console.error('删除空间失败:', error);
    }
  };

  // 处理发布/取消发布
  const handlePublish = async (space: ContentSpace, isPublished: PublishStatus) => {
    try {
      await publishContentSpace(space.id, {
        isPublished,
        version: space.version,
      } as ContentSpacePublishRequest);
      message.success(isPublished === PublishStatus.PUBLISHED ? '发布成功' : '取消发布成功');
      loadMySpaces();
    } catch (error) {
      console.error('操作失败:', error);
    }
  };

  // 进入空间详情
  const handleEnterSpace = (space: ContentSpace) => {
    history.push(`/kb/space/${space.spaceCode}`);
  };

  // 空间类型图标和颜色
  const getSpaceTypeIcon = (type: SpaceType) => {
    switch (type) {
      case SpaceType.PERSONAL:
        return <BookOutlined style={{ fontSize: 24 }} />;
      case SpaceType.COURSE:
        return <FileTextOutlined style={{ fontSize: 24 }} />;
      case SpaceType.VIDEO:
        return <VideoCameraOutlined style={{ fontSize: 24 }} />;
      default:
        return <BookOutlined style={{ fontSize: 24 }} />;
    }
  };

  const getSpaceTypeColor = (type: SpaceType) => {
    switch (type) {
      case SpaceType.PERSONAL:
        return '#1890ff';
      case SpaceType.COURSE:
        return '#52c41a';
      case SpaceType.VIDEO:
        return '#fa8c16';
      default:
        return '#1890ff';
    }
  };

  // 访问类型图标和描述
  const getAccessTypeIcon = (type: AccessType) => {
    switch (type) {
      case AccessType.PRIVATE:
        return <LockOutlined />;
      case AccessType.PUBLIC:
        return <GlobalOutlined />;
      case AccessType.PASSWORD_PROTECTED:
        return <SafetyOutlined />;
      default:
        return <LockOutlined />;
    }
  };

  // 渲染空间卡片
  const renderSpaceCard = (space: ContentSpace) => {
    const actions = [
      <Tooltip key="enter" title="进入空间">
        <Button type="text" icon={<EyeOutlined />} onClick={() => handleEnterSpace(space)}>
          进入
        </Button>
      </Tooltip>,
      <Tooltip key="edit" title="编辑">
        <Button
          type="text"
          icon={<EditOutlined />}
          onClick={() => {
            setCurrentSpace(space);
            setModalVisible(true);
          }}
        >
          编辑
        </Button>
      </Tooltip>,
      <Dropdown
        key="more"
        menu={{
          items: [
            {
              key: 'publish',
              label:
                space.isPublished === PublishStatus.PUBLISHED ? '取消发布' : '发布',
              icon:
                space.isPublished === PublishStatus.PUBLISHED ? (
                  <StopOutlined />
                ) : (
                  <SendOutlined />
                ),
              onClick: () =>
                handlePublish(
                  space,
                  space.isPublished === PublishStatus.PUBLISHED
                    ? PublishStatus.UNPUBLISHED
                    : PublishStatus.PUBLISHED,
                ),
            },
            {
              key: 'delete',
              label: '删除',
              icon: <DeleteOutlined />,
              danger: true,
              onClick: () => {
                Modal.confirm({
                  title: '确认删除',
                  content: '删除后将无法恢复，且空间下不能有文档，确认删除？',
                  okText: '确认',
                  cancelText: '取消',
                  okButtonProps: { danger: true },
                  onOk: () => handleDelete(space),
                });
              },
            },
          ],
        }}
      >
        <Button type="text" icon={<EllipsisOutlined />}>
          更多
        </Button>
      </Dropdown>,
    ];

    return (
      <Card
        hoverable
        style={{ height: '100%' }}
        cover={
          <div
            style={{
              height: 180,
              background: space.coverImage
                ? `url(${space.coverImage}) center/cover`
                : `linear-gradient(135deg, ${getSpaceTypeColor(space.spaceType)}40 0%, ${getSpaceTypeColor(space.spaceType)}90 100%)`,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            {!space.coverImage && (
              <div style={{ color: '#fff', fontSize: 48 }}>{getSpaceTypeIcon(space.spaceType)}</div>
            )}
            <div
              style={{
                position: 'absolute',
                top: 12,
                right: 12,
                display: 'flex',
                gap: 8,
                flexWrap: 'wrap',
              }}
            >
              <Tag
                color={space.isPublished === PublishStatus.PUBLISHED ? 'success' : 'default'}
                icon={
                  space.isPublished === PublishStatus.PUBLISHED ? (
                    <EyeOutlined />
                  ) : (
                    <EyeInvisibleOutlined />
                  )
                }
              >
                {space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
              </Tag>
              <Tag
                color={
                  space.accessType === AccessType.PUBLIC
                    ? 'success'
                    : space.accessType === AccessType.PRIVATE
                      ? 'default'
                      : 'warning'
                }
                icon={getAccessTypeIcon(space.accessType)}
              >
                {space.accessTypeDesc}
              </Tag>
            </div>
            <div
              style={{
                position: 'absolute',
                bottom: 12,
                left: 12,
              }}
            >
              <Tag color={getSpaceTypeColor(space.spaceType)}>{space.spaceTypeDesc}</Tag>
            </div>
          </div>
        }
        actions={actions}
      >
        <Meta
          title={
            <Space>
              <Text strong ellipsis style={{ maxWidth: 200 }}>
                {space.spaceName}
              </Text>
              {space.status === SpaceStatus.DISABLED && <Badge status="error" text="停用" />}
            </Space>
          }
          description={
            <div>
              <Paragraph
                ellipsis={{ rows: 2 }}
                style={{ minHeight: 44, color: '#666', marginBottom: 12 }}
              >
                {space.spaceDesc || '暂无描述'}
              </Paragraph>
              <Row gutter={16}>
                <Col span={12}>
                  <Statistic
                    title="浏览"
                    value={space.viewCount}
                    prefix={<EyeOutlined />}
                    valueStyle={{ fontSize: 16 }}
                  />
                </Col>
                <Col span={12}>
                  <Statistic
                    title="创建时间"
                    value={space.createTime}
                    valueStyle={{ fontSize: 12 }}
                    formatter={(value) => {
                      const date = new Date(value as string);
                      return `${date.getMonth() + 1}/${date.getDate()}`;
                    }}
                  />
                </Col>
              </Row>
            </div>
          }
        />
      </Card>
    );
  };

  // 渲染列表模式
  const renderSpaceList = (space: ContentSpace) => {
    return (
      <Card
        hoverable
        style={{ marginBottom: 16 }}
        bodyStyle={{ padding: 16 }}
        onClick={() => handleEnterSpace(space)}
      >
        <Row gutter={16} align="middle">
          <Col flex="80px">
            <Avatar
              size={64}
              shape="square"
              style={{
                background: `linear-gradient(135deg, ${getSpaceTypeColor(space.spaceType)}40 0%, ${getSpaceTypeColor(space.spaceType)}90 100%)`,
              }}
              icon={getSpaceTypeIcon(space.spaceType)}
              src={space.coverImage}
            />
          </Col>
          <Col flex="1">
            <Space direction="vertical" size={4} style={{ width: '100%' }}>
              <Space>
                <Title level={5} style={{ margin: 0 }}>
                  {space.spaceName}
                </Title>
                <Tag color={getSpaceTypeColor(space.spaceType)}>{space.spaceTypeDesc}</Tag>
                <Tag
                  color={space.isPublished === PublishStatus.PUBLISHED ? 'success' : 'default'}
                  icon={
                    space.isPublished === PublishStatus.PUBLISHED ? (
                      <EyeOutlined />
                    ) : (
                      <EyeInvisibleOutlined />
                    )
                  }
                >
                  {space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
                </Tag>
              </Space>
              <Text type="secondary" ellipsis>
                {space.spaceDesc || '暂无描述'}
              </Text>
              <Space size="large">
                <Text type="secondary">
                  {getAccessTypeIcon(space.accessType)} {space.accessTypeDesc}
                </Text>
                <Text type="secondary">
                  <EyeOutlined /> {space.viewCount} 次浏览
                </Text>
                <Text type="secondary">{space.createTime}</Text>
              </Space>
            </Space>
          </Col>
          <Col>
            <Space>
              <Button
                type="primary"
                icon={<EyeOutlined />}
                onClick={(e) => {
                  e.stopPropagation();
                  handleEnterSpace(space);
                }}
              >
                进入
              </Button>
              <Button
                icon={<EditOutlined />}
                onClick={(e) => {
                  e.stopPropagation();
                  setCurrentSpace(space);
                  setModalVisible(true);
                }}
              >
                编辑
              </Button>
              <Dropdown
                menu={{
                  items: [
                    {
                      key: 'publish',
                      label:
                        space.isPublished === PublishStatus.PUBLISHED
                          ? '取消发布'
                          : '发布',
                      icon:
                        space.isPublished === PublishStatus.PUBLISHED ? (
                          <StopOutlined />
                        ) : (
                          <SendOutlined />
                        ),
                      onClick: () =>
                        handlePublish(
                          space,
                          space.isPublished === PublishStatus.PUBLISHED
                            ? PublishStatus.UNPUBLISHED
                            : PublishStatus.PUBLISHED,
                        ),
                    },
                    {
                      key: 'delete',
                      label: '删除',
                      icon: <DeleteOutlined />,
                      danger: true,
                      onClick: () => {
                        Modal.confirm({
                          title: '确认删除',
                          content: '删除后将无法恢复，且空间下不能有文档，确认删除？',
                          okText: '确认',
                          cancelText: '取消',
                          okButtonProps: { danger: true },
                          onOk: () => handleDelete(space),
                        });
                      },
                    },
                  ],
                }}
                onClick={(e) => e.stopPropagation()}
              >
                <Button icon={<EllipsisOutlined />} onClick={(e) => e.stopPropagation()} />
              </Dropdown>
            </Space>
          </Col>
        </Row>
      </Card>
    );
  };

  return (
    <PageContainer
      header={{
        title: '我的空间',
        subTitle: '管理和浏览您创建的所有知识空间',
        extra: [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => {
              setCurrentSpace(undefined);
              setModalVisible(true);
            }}
          >
            创建空间
          </Button>,
        ],
      }}
    >
      {/* 搜索和过滤工具栏 */}
      <Card style={{ marginBottom: 16 }} bodyStyle={{ padding: '12px 24px' }}>
        <Row gutter={16} align="middle">
          <Col flex="1">
            <Search
              placeholder="搜索空间名称、描述或编码"
              allowClear
              size="large"
              onChange={(e) => setSearchKeyword(e.target.value)}
              style={{ maxWidth: 400 }}
            />
          </Col>
          <Col>
            <Space>
              <Select
                value={filterType}
                onChange={setFilterType}
                style={{ width: 150 }}
                size="large"
              >
                <Select.Option value="all">全部类型</Select.Option>
                <Select.Option value={SpaceType.PERSONAL}>
                  <BookOutlined /> 个人知识库
                </Select.Option>
                <Select.Option value={SpaceType.COURSE}>
                  <FileTextOutlined /> 课程专栏
                </Select.Option>
                <Select.Option value={SpaceType.VIDEO}>
                  <VideoCameraOutlined /> 视频专栏
                </Select.Option>
              </Select>
              <Button.Group size="large">
                <Button
                  icon={<AppstoreOutlined />}
                  type={viewMode === 'grid' ? 'primary' : 'default'}
                  onClick={() => setViewMode('grid')}
                />
                <Button
                  icon={<UnorderedListOutlined />}
                  type={viewMode === 'list' ? 'primary' : 'default'}
                  onClick={() => setViewMode('list')}
                />
              </Button.Group>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 空间列表 */}
      <Spin spinning={loading}>
        {filteredSpaces.length === 0 ? (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={
              <Space direction="vertical" size={16}>
                <Text type="secondary">
                  {searchKeyword || filterType !== 'all' ? '未找到匹配的空间' : '暂无空间'}
                </Text>
                {!searchKeyword && filterType === 'all' && (
                  <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => {
                      setCurrentSpace(undefined);
                      setModalVisible(true);
                    }}
                  >
                    创建第一个空间
                  </Button>
                )}
              </Space>
            }
            style={{ padding: '60px 0' }}
          />
        ) : viewMode === 'grid' ? (
          <Row gutter={[16, 16]}>
            {filteredSpaces.map((space) => (
              <Col xs={24} sm={12} md={8} lg={6} xl={6} key={space.id}>
                {renderSpaceCard(space)}
              </Col>
            ))}
          </Row>
        ) : (
          <div>{filteredSpaces.map((space) => renderSpaceList(space))}</div>
        )}
      </Spin>

      {/* 新建/编辑模态框 */}
      <ModalForm<ContentSpaceCreateRequest | ContentSpaceUpdateRequest>
        title={currentSpace ? '编辑空间' : '创建空间'}
        open={modalVisible}
        width={600}
        modalProps={{
          onCancel: () => {
            setModalVisible(false);
            setCurrentSpace(undefined);
          },
          destroyOnClose: true,
        }}
        onFinish={handleSubmit}
        initialValues={
          currentSpace
            ? {
                spaceName: currentSpace.spaceName,
                spaceDesc: currentSpace.spaceDesc,
                coverImage: currentSpace.coverImage,
                spaceType: currentSpace.spaceType,
                accessType: currentSpace.accessType,
                sort: currentSpace.sort,
                remark: currentSpace.remark,
              }
            : {
                spaceType: SpaceType.PERSONAL,
                accessType: AccessType.PUBLIC,
                sort: 0,
              }
        }
      >
        <ProFormText
          name="spaceName"
          label="空间名称"
          placeholder="请输入空间名称"
          rules={[
            { required: true, message: '请输入空间名称' },
            { max: 128, message: '空间名称不能超过128个字符' },
          ]}
        />

        <ProFormTextArea
          name="spaceDesc"
          label="空间描述"
          placeholder="请输入空间描述"
          fieldProps={{
            rows: 3,
            maxLength: 1024,
            showCount: true,
          }}
        />

        <ProFormText
          name="coverImage"
          label="封面图URL"
          placeholder="请输入封面图URL（留空将使用默认封面）"
          rules={[{ max: 512, message: 'URL不能超过512个字符' }]}
        />

        <ProFormSelect
          name="spaceType"
          label="空间类型"
          placeholder="请选择空间类型"
          options={[
            { label: '📚 个人知识库', value: SpaceType.PERSONAL },
            { label: '📖 课程专栏', value: SpaceType.COURSE },
            { label: '🎬 视频专栏', value: SpaceType.VIDEO },
          ]}
          rules={[{ required: true, message: '请选择空间类型' }]}
        />

        <ProFormSelect
          name="accessType"
          label="访问类型"
          placeholder="请选择访问类型"
          options={[
            { label: '🔒 私有（仅自己可见）', value: AccessType.PRIVATE },
            { label: '🌍 公开（所有人可见）', value: AccessType.PUBLIC },
            { label: '🔑 密码访问（知道密码可见）', value: AccessType.PASSWORD_PROTECTED },
          ]}
          rules={[{ required: true, message: '请选择访问类型' }]}
        />

        <ProFormText.Password
          name="accessPassword"
          label="访问密码"
          placeholder="访问类型为密码访问时必填"
          dependencies={['accessType']}
          rules={[
            ({ getFieldValue }) => ({
              required: getFieldValue('accessType') === AccessType.PASSWORD_PROTECTED,
              message: '密码访问类型必须设置访问密码',
            }),
            { max: 255, message: '访问密码不能超过255个字符' },
          ]}
        />

        <ProFormDigit
          name="sort"
          label="排序"
          placeholder="请输入排序值（数字越小越靠前）"
          fieldProps={{
            precision: 0,
          }}
          min={0}
        />

        <ProFormTextArea
          name="remark"
          label="备注"
          placeholder="请输入备注信息（仅自己可见）"
          fieldProps={{
            rows: 2,
            maxLength: 500,
            showCount: true,
          }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default MyContentSpace;

