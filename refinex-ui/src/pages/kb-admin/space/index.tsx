import {
  DeleteOutlined,
  EditOutlined,
  EyeOutlined,
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
import { useIntl } from '@umijs/max';
import { Badge, Button, Image, message, Popconfirm, Space, Tag, Tooltip } from 'antd';
import React, { useRef, useState } from 'react';
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
import { AccessType, PublishStatus, SpaceStatus, SpaceType } from '@/services/kb/typings';

/**
 * 管理员-内容空间管理页面
 * 企业级空间管理界面，提供全局空间的增删改查、发布管理等功能
 */
const AdminContentSpace: React.FC = () => {
  const intl = useIntl();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentSpace, setCurrentSpace] = useState<ContentSpace | undefined>();
  const actionRef = useRef<ActionType>(null);

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
      actionRef.current?.reload();
      return true;
    } catch (error) {
      console.error('提交空间信息失败:', error);
      return false;
    }
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
            style={{ borderRadius: 4, objectFit: 'cover' }}
            placeholder
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
            }}
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
      render: (text) => <strong>{text}</strong>,
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
        title: '空间管理',
        subTitle: '管理所有内容空间的创建、编辑、发布等操作',
      }}
    >
      <ProTable<ContentSpace, ContentSpaceQueryParams>
        columns={columns}
        actionRef={actionRef}
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
        title={currentSpace ? '编辑空间' : '新建空间'}
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
          placeholder="请输入封面图URL"
          rules={[{ max: 512, message: 'URL不能超过512个字符' }]}
        />

        <ProFormSelect
          name="spaceType"
          label="空间类型"
          placeholder="请选择空间类型"
          options={[
            { label: '个人知识库', value: SpaceType.PERSONAL },
            { label: '课程专栏', value: SpaceType.COURSE },
            { label: '视频专栏', value: SpaceType.VIDEO },
          ]}
          rules={[{ required: true, message: '请选择空间类型' }]}
        />

        <ProFormSelect
          name="accessType"
          label="访问类型"
          placeholder="请选择访问类型"
          options={[
            { label: '私有', value: AccessType.PRIVATE },
            { label: '公开', value: AccessType.PUBLIC },
            { label: '密码访问', value: AccessType.PASSWORD_PROTECTED },
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
          placeholder="请输入排序值"
          fieldProps={{
            precision: 0,
          }}
          min={0}
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
          placeholder="请输入备注信息"
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

