import {
  BookOutlined,
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
  FolderOutlined,
  GlobalOutlined,
  LockOutlined,
  PlusOutlined,
  SafetyOutlined,
  SendOutlined,
  StopOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormRadio,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { history, useIntl } from '@umijs/max';
import { Badge, Button, Col, Image, message, Popconfirm, Row, Space, Tag, Tooltip, Typography, Upload } from 'antd';
import type { UploadFile, UploadProps } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import type {
  ContentSpace,
  ContentSpaceCreateRequest,
  ContentSpacePublishRequest,
  ContentSpaceQueryParams,
  ContentSpaceUpdateRequest,
} from '@/services/kb/typings';
import {
  createContentSpace,
  deleteContentSpace,
  publishContentSpace,
  queryContentSpaces,
  updateContentSpace,
} from '@/services/kb/space';
import { AccessType, PublishStatus, SpaceStatus, SpaceType } from '@/services/kb/typings.d';
import { listDictDataByTypeCode } from '@/services/system/dictionary';
import { encryptPassword, getRsaPublicKey } from '@/utils/crypto';

const { Text } = Typography;

/**
 * 管理员-内容空间管理页面
 * 企业级空间管理界面，提供全局空间的增删改查、发布管理等功能
 */
const AdminContentSpace: React.FC = () => {
  const intl = useIntl();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentSpace, setCurrentSpace] = useState<ContentSpace | undefined>();
  const actionRef = useRef<ActionType>(null);
  const [fileList, setFileList] = useState<UploadFile[]>([]);

  // 字典数据
  const [spaceTypeDict, setSpaceTypeDict] = useState<Array<{ label: string; value: number }>>([]);
  const [accessTypeDict, setAccessTypeDict] = useState<Array<{ label: string; value: number }>>([]);

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
      }

      if (accessTypeRes.success && accessTypeRes.data) {
        const accessTypes = accessTypeRes.data.map((item) => ({
          label: item.dictLabel,
          value: Number(item.dictValue),
        }));
        setAccessTypeDict(accessTypes);
      }
    } catch (error) {
      console.error('加载字典数据失败:', error);
      message.error('加载字典数据失败');
    }
  };

  useEffect(() => {
    loadDictionaries();
  }, []);

  // 空间类型枚举
  const spaceTypeEnum = {
    [SpaceType.PERSONAL]: { text: '个人知识库', color: 'blue' },
    [SpaceType.COURSE]: { text: '课程专栏', color: 'green' },
    [SpaceType.VIDEO]: { text: '视频专栏', color: 'orange' },
  };

  // 访问类型枚举
  const accessTypeEnum = {
    [AccessType.PRIVATE]: { text: '私有', status: 'Default', icon: <LockOutlined /> },
    [AccessType.PUBLIC]: { text: '公开', status: 'Success', icon: <GlobalOutlined /> },
    [AccessType.PASSWORD_PROTECTED]: {
      text: '密码访问',
      status: 'Warning',
      icon: <SafetyOutlined />,
    },
  };

  // 发布状态枚举
  const publishStatusEnum = {
    [PublishStatus.UNPUBLISHED]: { text: '未发布', status: 'Default' },
    [PublishStatus.PUBLISHED]: { text: '已发布', status: 'Success' },
  };

  // 状态枚举
  const statusEnum = {
    [SpaceStatus.NORMAL]: { text: '正常', status: 'Success' },
    [SpaceStatus.DISABLED]: { text: '停用', status: 'Error' },
  };

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
      actionRef.current?.reload();
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
  const handleDelete = async (record: ContentSpace) => {
    try {
      await deleteContentSpace(record.id);
      message.success('删除空间成功');
      actionRef.current?.reload();
    } catch (error) {
      console.error('删除空间失败:', error);
    }
  };

  // 处理发布/取消发布
  const handlePublish = async (record: ContentSpace, isPublished: PublishStatus) => {
    try {
      await publishContentSpace(record.id, {
        isPublished,
        version: record.version,
      } as ContentSpacePublishRequest);
      message.success(isPublished === PublishStatus.PUBLISHED ? '发布成功' : '取消发布成功');
      actionRef.current?.reload();
    } catch (error) {
      console.error('操作失败:', error);
    }
  };

  // 表格列定义
  const columns: ProColumns<ContentSpace>[] = [
    {
      title: '空间编码',
      dataIndex: 'spaceCode',
      width: 180,
      ellipsis: true,
      copyable: true,
      fixed: 'left',
    },
    {
      title: '封面',
      dataIndex: 'coverImage',
      width: 80,
      search: false,
      render: (_, record) =>
        record.coverImage ? (
          <Image
            src={record.coverImage}
            alt={record.spaceName}
            width={50}
            height={50}
            style={{ borderRadius: 4, objectFit: 'cover', cursor: 'pointer' }}
            placeholder
            onClick={() => history.push(`/kb-admin/space/detail/${record.id}`)}
          />
        ) : (
          <div
            style={{
              width: 50,
              height: 50,
              background: '#f0f0f0',
              borderRadius: 4,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#999',
              cursor: 'pointer',
            }}
            onClick={() => history.push(`/kb-admin/space/detail/${record.id}`)}
          >
            无
          </div>
        ),
    },
    {
      title: '空间名称',
      dataIndex: 'spaceName',
      width: 200,
      ellipsis: true,
      fixed: 'left',
      render: (text, record) => (
        <a onClick={() => history.push(`/kb-admin/space/detail/${record.id}`)} style={{ fontWeight: 500 }}>
          {text}
        </a>
      ),
    },
    {
      title: '空间描述',
      dataIndex: 'spaceDesc',
      width: 250,
      ellipsis: true,
      search: false,
    },
    {
      title: '拥有者',
      dataIndex: 'ownerName',
      width: 120,
      search: false,
      render: (text, record) => (
        <Tooltip title={`用户ID: ${record.ownerId}`}>
          <Tag color="blue">{text || `ID:${record.ownerId}`}</Tag>
        </Tooltip>
      ),
    },
    {
      title: '空间类型',
      dataIndex: 'spaceType',
      width: 120,
      valueEnum: spaceTypeEnum,
      render: (_, record) => (
        <Tag color={spaceTypeEnum[record.spaceType]?.color}>
          {record.spaceTypeDesc || spaceTypeEnum[record.spaceType]?.text}
        </Tag>
      ),
    },
    {
      title: '访问类型',
      dataIndex: 'accessType',
      width: 120,
      valueEnum: accessTypeEnum,
      render: (_, record) => (
        <Badge
          status={accessTypeEnum[record.accessType]?.status as any}
          text={
            <Space size={4}>
              {accessTypeEnum[record.accessType]?.icon}
              {record.accessTypeDesc || accessTypeEnum[record.accessType]?.text}
            </Space>
          }
        />
      ),
    },
    {
      title: '发布状态',
      dataIndex: 'isPublished',
      width: 120,
      valueEnum: publishStatusEnum,
      render: (_, record) => (
        <Badge
          status={publishStatusEnum[record.isPublished]?.status as any}
          text={publishStatusEnum[record.isPublished]?.text}
        />
      ),
    },
    {
      title: '浏览次数',
      dataIndex: 'viewCount',
      width: 100,
      search: false,
      sorter: true,
      render: (text) => <Tag color="purple">{text}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueEnum: statusEnum,
      render: (_, record) => (
        <Badge
          status={statusEnum[record.status]?.status as any}
          text={statusEnum[record.status]?.text}
        />
      ),
    },
    {
      title: '发布时间',
      dataIndex: 'publishTime',
      width: 170,
      search: false,
      valueType: 'dateTime',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 170,
      search: false,
      valueType: 'dateTime',
      sorter: true,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 200,
      fixed: 'right',
      render: (_, record) => [
        <Tooltip key="edit" title="编辑">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => {
              setCurrentSpace(record);
              setModalVisible(true);
            }}
          />
        </Tooltip>,
        record.isPublished === PublishStatus.UNPUBLISHED ? (
          <Tooltip key="publish" title="发布">
            <Popconfirm
              title="确认发布此空间？"
              onConfirm={() => handlePublish(record, PublishStatus.PUBLISHED)}
            >
              <Button type="link" size="small" icon={<SendOutlined />} />
            </Popconfirm>
          </Tooltip>
        ) : (
          <Tooltip key="unpublish" title="取消发布">
            <Popconfirm
              title="确认取消发布此空间？"
              onConfirm={() => handlePublish(record, PublishStatus.UNPUBLISHED)}
            >
              <Button type="link" size="small" danger icon={<StopOutlined />} />
            </Popconfirm>
          </Tooltip>
        ),
        <Tooltip key="manage" title="管理知识库">
          <Button
            type="link"
            size="small"
            icon={<FolderOutlined />}
            onClick={() => {
              history.push(`/kb-admin/space/detail/${record.id}`);
            }}
          />
        </Tooltip>,
        <Tooltip key="view" title="查看详情">
          <Button
            type="link"
            size="small"
            icon={<EyeOutlined />}
            onClick={() => {
              window.open(`/kb/space/${record.spaceCode}`, '_blank');
            }}
          />
        </Tooltip>,
        <Tooltip key="delete" title="删除">
          <Popconfirm
            title="确认删除此空间？"
            description="删除后将无法恢复，且空间下不能有文档"
            onConfirm={() => handleDelete(record)}
          >
            <Button type="link" size="small" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Tooltip>,
      ],
    },
  ];

  return (
    <PageContainer
      header={{
        title: false,
        subTitle: false,
      }}
    >
      <ProTable<ContentSpace, ContentSpaceQueryParams>
        columns={columns}
        actionRef={actionRef}
        cardBordered
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        scroll={{ x: 1800 }}
        request={async (params, sort) => {
          try {
            const response = await queryContentSpaces({
              ...params,
              pageNum: params.current,
              pageSize: params.pageSize,
            });

            if (response.success && response.data) {
              return {
                data: response.data.records || [],
                success: true,
                total: response.data.total || 0,
              };
            }
            return {
              data: [],
              success: false,
              total: 0,
            };
          } catch (error) {
            console.error('查询空间列表失败:', error);
            return {
              data: [],
              success: false,
              total: 0,
            };
          }
        }}
        toolbar={{
          actions: [
            <Button
              key="create"
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => {
                setCurrentSpace(undefined);
                setModalVisible(true);
              }}
            >
              新建空间
            </Button>,
          ],
        }}
      />

      {/* 新建/编辑模态框 */}
      <ModalForm<ContentSpaceCreateRequest | ContentSpaceUpdateRequest>
        title={
          <Space>
            <BookOutlined />
            {currentSpace ? '编辑空间' : '新建空间'}
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
                status: currentSpace.status,
                remark: currentSpace.remark,
              }
            : {
                spaceType: SpaceType.PERSONAL,
                accessType: AccessType.PUBLIC,
                status: SpaceStatus.NORMAL,
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

        {currentSpace && (
          <ProFormRadio.Group
            name="status"
            label="状态"
            options={[
              { label: '正常', value: SpaceStatus.NORMAL },
              { label: '停用', value: SpaceStatus.DISABLED },
            ]}
          />
        )}

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

export default AdminContentSpace;

