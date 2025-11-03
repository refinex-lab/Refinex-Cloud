import {
  AppstoreOutlined,
  BookOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  DeleteOutlined,
  EditOutlined,
  EllipsisOutlined,
  EyeOutlined,
  FileTextOutlined,
  GlobalOutlined,
  LockOutlined,
  PlusOutlined,
  SafetyOutlined,
  SearchOutlined,
  SendOutlined,
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
  Row,
  Segmented,
  Select,
  Space,
  Spin,
  Statistic,
  Tag,
  Tooltip,
  Typography,
  Upload,
} from 'antd';
import type { UploadFile, UploadProps } from 'antd';
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
import { AccessType, PublishStatus, SpaceStatus, SpaceType } from '@/services/kb/typings.d';
import { listDictDataByTypeCode } from '@/services/system/dictionary';
import { encryptPassword, getRsaPublicKey } from '@/utils/crypto';
import './index.less';

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
  const [fileList, setFileList] = useState<UploadFile[]>([]);

  // 字典数据
  const [spaceTypeDict, setSpaceTypeDict] = useState<Array<{ label: string; value: number }>>([]);
  const [accessTypeDict, setAccessTypeDict] = useState<Array<{ label: string; value: number }>>([]);
  const [spaceTypeDictMap, setSpaceTypeDictMap] = useState<Record<number, string>>({});
  const [accessTypeDictMap, setAccessTypeDictMap] = useState<Record<number, string>>({});

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

  // 加载字典数据
  const loadDictionaries = async () => {
    try {
      const [spaceTypeRes, accessTypeRes] = await Promise.all([
        listDictDataByTypeCode('kb_space_type'),
        listDictDataByTypeCode('kb_access_type'),
      ]);

      if (spaceTypeRes.success && spaceTypeRes.data) {
        const spaceTypes = spaceTypeRes.data.map((item) => ({
          label: item.dictLabel,
          value: Number(item.dictValue),
        }));
        setSpaceTypeDict(spaceTypes);

        // 同时创建映射表，用于显示
        const typeMap: Record<number, string> = {};
        spaceTypeRes.data.forEach((item) => {
          typeMap[Number(item.dictValue)] = item.dictLabel;
        });
        setSpaceTypeDictMap(typeMap);
      }

      if (accessTypeRes.success && accessTypeRes.data) {
        const accessTypes = accessTypeRes.data.map((item) => ({
          label: item.dictLabel,
          value: Number(item.dictValue),
        }));
        setAccessTypeDict(accessTypes);

        // 同时创建映射表，用于显示
        const accessMap: Record<number, string> = {};
        accessTypeRes.data.forEach((item) => {
          accessMap[Number(item.dictValue)] = item.dictLabel;
        });
        setAccessTypeDictMap(accessMap);
      }
    } catch (error) {
      console.error('加载字典数据失败:', error);
      message.error('加载字典数据失败');
    }
  };

  useEffect(() => {
    loadDictionaries();
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
      // 如果有上传的文件，将 URL 添加到表单数据中
      const submitData = {
        ...values,
        coverImage: fileList.length > 0 && fileList[0].url ? fileList[0].url : values.coverImage,
      };

      // 如果是密码保护类型且设置了密码，使用 RSA + AES 混合加密
      if (
        submitData.accessType === AccessType.PASSWORD_PROTECTED &&
        submitData.accessPassword &&
        submitData.accessPassword.trim() !== ''
      ) {
        try {
          const publicKey = getRsaPublicKey();
          const result = await encryptPassword(submitData.accessPassword, publicKey);
          // 格式化为后端期望的格式：encryptedKey|encryptedData
          submitData.accessPassword = `${result.encryptedKey}|${result.encryptedData}`;
        } catch (error) {
          console.error('密码加密失败:', error);
          message.error('密码加密失败，请检查系统配置');
          return false;
        }
      }

      if (currentSpace) {
        // 编辑
        await updateContentSpace(currentSpace.id, {
          ...submitData,
          version: currentSpace.version,
        } as ContentSpaceUpdateRequest);
        message.success('更新空间成功');
      } else {
        // 新增
        await createContentSpace(submitData as ContentSpaceCreateRequest);
        message.success('创建空间成功');
      }
      setModalVisible(false);
      setCurrentSpace(undefined);
      setFileList([]);
      loadMySpaces();
      return true;
    } catch (error) {
      console.error('提交空间信息失败:', error);
      return false;
    }
  };

  // 处理文件上传（暂时模拟，后期接入真实接口）
  const handleUploadChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
    setFileList(newFileList);
  };

  // 自定义上传请求（暂时返回预览 URL）
  const customUploadRequest: UploadProps['customRequest'] = (options) => {
    const { file, onSuccess, onError } = options;

    // 模拟上传过程
    setTimeout(() => {
      // 创建本地预览 URL
      if (file instanceof File) {
        const reader = new FileReader();
        reader.onload = (e) => {
          // 模拟成功，使用本地预览 URL
          onSuccess?.({
            url: e.target?.result as string,
          });
        };
        reader.onerror = () => {
          onError?.(new Error('文件读取失败'));
        };
        reader.readAsDataURL(file);
      }
    }, 500);
  };

  // 验证文件
  const beforeUpload = (file: File) => {
    const isImage = file.type.startsWith('image/');
    if (!isImage) {
      message.error('只能上传图片文件！');
      return false;
    }
    const isLt5M = file.size / 1024 / 1024 < 5;
    if (!isLt5M) {
      message.error('图片大小不能超过 5MB！');
      return false;
    }
    return true;
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
              onClick: () => {
                handlePublish(
                  space,
                  space.isPublished === PublishStatus.PUBLISHED
                    ? PublishStatus.UNPUBLISHED
                    : PublishStatus.PUBLISHED,
                );
              },
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
        style={{ height: '100%' }}
        className="space-card"
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
                className={`space-status-tag ${space.isPublished === PublishStatus.PUBLISHED ? 'published' : 'unpublished'}`}
                icon={
                  space.isPublished === PublishStatus.PUBLISHED ? (
                    <CheckCircleOutlined />
                  ) : (
                    <CloseCircleOutlined />
                  )
                }
              >
                {space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
              </Tag>
              <Tag
                className={`space-access-tag ${space.accessType === AccessType.PUBLIC ? 'public' : space.accessType === AccessType.PRIVATE ? 'private' : 'limited'}`}
                icon={getAccessTypeIcon(space.accessType)}
              >
                {accessTypeDictMap[space.accessType] || space.accessTypeDesc}
              </Tag>
            </div>
            <div
              style={{
                position: 'absolute',
                bottom: 12,
                left: 12,
              }}
            >
              <Tag className="space-type-tag" color={getSpaceTypeColor(space.spaceType)}>
                {spaceTypeDictMap[space.spaceType] || space.spaceTypeDesc}
              </Tag>
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
        style={{ marginBottom: 16 }}
        styles={{ body: { padding: 16 } }}
        className="space-list-card"
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
                <Tag className="space-type-tag" color={getSpaceTypeColor(space.spaceType)}>
                  {spaceTypeDictMap[space.spaceType] || space.spaceTypeDesc}
                </Tag>
                <Tag
                  className={`space-status-tag ${space.isPublished === PublishStatus.PUBLISHED ? 'published' : 'unpublished'}`}
                  icon={
                    space.isPublished === PublishStatus.PUBLISHED ? (
                      <CheckCircleOutlined />
                    ) : (
                      <CloseCircleOutlined />
                    )
                  }
                >
                  {space.isPublished === PublishStatus.PUBLISHED ? '已发布' : '未发布'}
                </Tag>
                <Tag
                  className={`space-access-tag ${space.accessType === AccessType.PUBLIC ? 'public' : space.accessType === AccessType.PRIVATE ? 'private' : 'limited'}`}
                  icon={getAccessTypeIcon(space.accessType)}
                >
                  {accessTypeDictMap[space.accessType] || space.accessTypeDesc}
                </Tag>
              </Space>
              <Text type="secondary" ellipsis>
                {space.spaceDesc || '暂无描述'}
              </Text>
              <Space size="large">
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
                      onClick: (info) => {
                        info.domEvent.stopPropagation();
                        handlePublish(
                          space,
                          space.isPublished === PublishStatus.PUBLISHED
                            ? PublishStatus.UNPUBLISHED
                            : PublishStatus.PUBLISHED,
                        );
                      },
                    },
                    {
                      key: 'delete',
                      label: '删除',
                      icon: <DeleteOutlined />,
                      danger: true,
                      onClick: (info) => {
                        info.domEvent.stopPropagation();
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
        title: false,
        subTitle: false,
      }}
    >
      {/* 搜索和过滤工具栏 */}
      <Card style={{ marginBottom: 16 }} bodyStyle={{ padding: '16px 24px' }}>
        <Row gutter={16} align="middle">
          <Col flex="1">
            <Input
              prefix={<SearchOutlined style={{ color: 'rgba(0, 0, 0, 0.45)' }} />}
              placeholder="搜索空间名称、描述或编码"
              allowClear
              size="large"
              onChange={(e) => setSearchKeyword(e.target.value)}
              style={{ maxWidth: 400 }}
              className="space-search-input"
            />
          </Col>
          <Col>
            <Space size="middle">
              <Select
                value={filterType}
                onChange={setFilterType}
                style={{ width: 150 }}
                size="large"
              >
                <Select.Option value="all">全部类型</Select.Option>
                {spaceTypeDict.map((type) => (
                  <Select.Option key={type.value} value={type.value}>
                    {type.label}
                  </Select.Option>
                ))}
              </Select>
              <Segmented
                value={viewMode}
                onChange={(value) => setViewMode(value as 'grid' | 'list')}
                options={[
                  {
                    label: '卡片',
                    value: 'grid',
                    icon: <AppstoreOutlined />,
                  },
                  {
                    label: '列表',
                    value: 'list',
                    icon: <UnorderedListOutlined />,
                  },
                ]}
                size="large"
              />
              <Button
                size="large"
                icon={<PlusOutlined />}
                onClick={() => {
                  setCurrentSpace(undefined);
                  setModalVisible(true);
                }}
                className="create-space-btn"
              >
                创建空间
              </Button>
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
        title={
          <Space>
            <BookOutlined />
            {currentSpace ? '编辑空间' : '创建空间'}
          </Space>
        }
        open={modalVisible}
        width={720}
        layout="horizontal"
        labelCol={{ span: 5 }}
        wrapperCol={{ span: 19 }}
        modalProps={{
          onCancel: () => {
            setModalVisible(false);
            setCurrentSpace(undefined);
            setFileList([]);
          },
          destroyOnClose: true,
          centered: true,
          styles: {
            body: {
              maxHeight: 'calc(100vh - 200px)',
              overflowY: 'auto',
            },
          },
        }}
        onFinish={handleSubmit}
        onOpenChange={(visible) => {
          if (visible && currentSpace?.coverImage) {
            // 编辑时，如果有封面图，初始化文件列表
            setFileList([
              {
                uid: '-1',
                name: '封面图片',
                status: 'done',
                url: currentSpace.coverImage,
              },
            ]);
          } else if (!visible) {
            setFileList([]);
          }
        }}
        initialValues={
          currentSpace
            ? {
                spaceName: currentSpace.spaceName,
                spaceDesc: currentSpace.spaceDesc,
                coverImage: currentSpace.coverImage,
                spaceType: currentSpace.spaceType,
                accessType: currentSpace.accessType,
                accessPassword: undefined,
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
          placeholder="输入空间描述，让访客更好地了解这个空间"
          fieldProps={{
            rows: 3,
            maxLength: 1024,
            showCount: true,
          }}
        />

        {/* 封面图上传 */}
        <ProFormText name="coverImage" hidden />
        <Row>
          <Col span={5} style={{ textAlign: 'right', paddingRight: 8, paddingTop: 8 }}>
            <label>封面图片</label>
          </Col>
          <Col span={19} style={{ marginBottom: 24 }}>
            <Upload
              listType="picture-card"
              fileList={fileList}
              onChange={handleUploadChange}
              beforeUpload={beforeUpload}
              customRequest={customUploadRequest}
              maxCount={1}
              accept="image/*"
            >
              {fileList.length === 0 && (
                <div>
                  <PlusOutlined />
                  <div style={{ marginTop: 8 }}>上传封面</div>
                </div>
              )}
            </Upload>
            <div>
              <Text type="secondary" style={{ fontSize: 12 }}>
                支持 JPG、PNG、GIF 格式，大小不超过 5MB，建议尺寸 16:9
              </Text>
            </div>
          </Col>
        </Row>

        <ProFormSelect
          name="spaceType"
          label="空间类型"
          placeholder="选择类型"
          options={spaceTypeDict}
          rules={[{ required: true, message: '请选择空间类型' }]}
        />

        <ProFormDigit
          name="sort"
          label="排序"
          placeholder="数字越小越靠前"
          fieldProps={{
            precision: 0,
          }}
          min={0}
        />

        <ProFormSelect
          name="accessType"
          label="访问类型"
          placeholder="请选择访问类型"
          options={accessTypeDict}
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

        <ProFormTextArea
          name="remark"
          label="备注"
          placeholder="输入备注信息（仅自己可见）"
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

