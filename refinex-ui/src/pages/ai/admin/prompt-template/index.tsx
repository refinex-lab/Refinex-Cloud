import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProTable,
  ProFormRadio,
} from '@ant-design/pro-components';
import { Badge, Button, message, Popconfirm, Space, Switch, Tag, Tooltip, Modal } from 'antd';
import React, { useRef, useState } from 'react';
import type {
  PromptTemplate,
  PromptTemplateCreateRequest,
  PromptTemplateUpdateRequest,
} from '@/services/ai/typings.d';
import {
  createPromptTemplate,
  deletePromptTemplate,
  queryPromptTemplates,
  toggleTemplateStatus,
  updatePromptTemplate,
} from '@/services/ai/prompt-template';

/**
 * AI 提示词模板管理页面
 */
const PromptTemplateManagement: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<PromptTemplate | undefined>();
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewContent, setPreviewContent] = useState('');
  const actionRef = useRef<ActionType>(null);

  // 模板类型枚举
  const templateTypeEnum = {
    SYSTEM: { text: '系统模板', color: 'blue' },
    USER: { text: '用户模板', color: 'green' },
  };

  // 状态枚举
  const statusEnum = {
    0: { text: '正常', status: 'Success' },
    1: { text: '停用', status: 'Default' },
  };

  // 公开状态枚举
  const publicEnum = {
    0: { text: '私有', status: 'Default' },
    1: { text: '公开', status: 'Success' },
  };

  // 系统模板枚举
  const systemEnum = {
    0: { text: '否', status: 'Default' },
    1: { text: '是', status: 'Processing' },
  };

  // 列定义
  const columns: ProColumns<PromptTemplate>[] = [
    {
      title: '模板编码',
      dataIndex: 'templateCode',
      width: 150,
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: '模板名称',
      dataIndex: 'templateName',
      width: 180,
      ellipsis: true,
    },
    {
      title: '模板类型',
      dataIndex: 'templateType',
      width: 120,
      valueType: 'select',
      valueEnum: templateTypeEnum,
      render: (_, record) => (
        <Tag color={templateTypeEnum[record.templateType as keyof typeof templateTypeEnum]?.color}>
          {templateTypeEnum[record.templateType as keyof typeof templateTypeEnum]?.text ||
            record.templateType}
        </Tag>
      ),
    },
    {
      title: '分类',
      dataIndex: 'templateCategory',
      width: 120,
      ellipsis: true,
    },
    {
      title: '是否系统模板',
      dataIndex: 'isSystem',
      width: 120,
      hideInSearch: true,
      render: (_, record) => (
        <Badge
          status={record.isSystem === 1 ? 'processing' : 'default'}
          text={systemEnum[record.isSystem as 0 | 1]?.text}
        />
      ),
    },
    {
      title: '是否公开',
      dataIndex: 'isPublic',
      width: 100,
      valueType: 'select',
      valueEnum: publicEnum,
      render: (_, record) => (
        <Badge
          status={record.isPublic === 1 ? 'success' : 'default'}
          text={publicEnum[record.isPublic as 0 | 1]?.text}
        />
      ),
    },
    {
      title: '使用次数',
      dataIndex: 'usageCount',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '点赞数',
      dataIndex: 'likeCount',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueEnum: statusEnum,
      render: (_, record) => (
        <Switch
          checked={record.status === 0}
          checkedChildren="正常"
          unCheckedChildren="停用"
          disabled={record.isSystem === 1}
          onChange={async (checked) => {
            try {
              await toggleTemplateStatus(record.id, checked ? 0 : 1);
              message.success('状态更新成功');
              actionRef.current?.reload();
            } catch (error) {
              message.error('状态更新失败');
            }
          }}
        />
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 180,
      fixed: 'right',
      render: (_, record) => [
        <a
          key="preview"
          onClick={() => {
            setPreviewContent(record.templateContent);
            setPreviewVisible(true);
          }}
        >
          <EyeOutlined /> 预览
        </a>,
        <a
          key="edit"
          onClick={() => {
            if (record.isSystem === 1) {
              message.warning('系统模板不允许编辑');
              return;
            }
            setCurrentRecord(record);
            setModalVisible(true);
          }}
        >
          <EditOutlined /> 编辑
        </a>,
        <Popconfirm
          key="delete"
          title="确定要删除这个提示词模板吗？"
          icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
          disabled={record.isSystem === 1}
          onConfirm={async () => {
            try {
              await deletePromptTemplate(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            } catch (error) {
              message.error('删除失败');
            }
          }}
        >
          <a style={{ color: record.isSystem === 1 ? '#ccc' : 'red' }}>
            <DeleteOutlined /> 删除
          </a>
        </Popconfirm>,
      ],
    },
  ];

  // 处理表单提交
  const handleSubmit = async (
    values: PromptTemplateCreateRequest | PromptTemplateUpdateRequest,
  ) => {
    try {
      if (currentRecord) {
        // 更新
        await updatePromptTemplate(currentRecord.id, values as PromptTemplateUpdateRequest);
        message.success('更新成功');
      } else {
        // 创建
        await createPromptTemplate(values as PromptTemplateCreateRequest);
        message.success('创建成功');
      }
      setModalVisible(false);
      setCurrentRecord(undefined);
      actionRef.current?.reload();
      return true;
    } catch (error) {
      message.error(currentRecord ? '更新失败' : '创建失败');
      return false;
    }
  };

  return (
    <PageContainer
      header={{
        title: false,
        breadcrumb: {
          items: [
            { path: '/ai/model-config', title: 'AI 管理' },
            { title: '提示词管理' },
          ],
        },
      }}
    >
      <ProTable<PromptTemplate>
        headerTitle="提示词模板列表"
        actionRef={actionRef}
        rowKey="id"
        cardBordered
        search={{
          labelWidth: 120,
        }}
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => {
              setCurrentRecord(undefined);
              setModalVisible(true);
            }}
          >
            新建提示词模板
          </Button>,
        ]}
        request={async (params, sort) => {
          const { current, pageSize, category, type, isPublic, status, ...rest } = params;
          const response = await queryPromptTemplates({
            pageNum: current,
            pageSize,
            category,
            type,
            isPublic,
            status,
            keyword: rest.keyword,
            orderBy: sort && Object.keys(sort)[0],
            orderDirection: sort && Object.values(sort)[0] === 'ascend' ? 'ASC' : 'DESC',
          });

          return {
            data: response.data?.records || [],
            success: response.code === 200,
            total: response.data?.total || 0,
          };
        }}
        columns={columns}
        scroll={{ x: 1800 }}
      />

      <ModalForm<PromptTemplateCreateRequest | PromptTemplateUpdateRequest>
        title={currentRecord ? '编辑提示词模板' : '新建提示词模板'}
        width={800}
        open={modalVisible}
        onOpenChange={setModalVisible}
        initialValues={
          currentRecord
            ? currentRecord
            : {
                isSystem: 0,
                isPublic: 1,
                status: 0,
                sort: 0,
                templateType: 'USER',
              }
        }
        onFinish={handleSubmit}
        layout="horizontal"
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 16 }}
      >
        <ProFormText
          name="templateCode"
          label="模板编码"
          placeholder="请输入模板编码，如：WRITING_ASSISTANT"
          rules={[{ required: true, message: '请输入模板编码' }]}
          disabled={!!currentRecord}
        />
        <ProFormText
          name="templateName"
          label="模板名称"
          placeholder="请输入模板名称"
          rules={[{ required: true, message: '请输入模板名称' }]}
        />
        <ProFormTextArea
          name="templateContent"
          label="模板内容"
          placeholder="请输入模板内容，支持变量占位符"
          rules={[{ required: true, message: '请输入模板内容' }]}
          fieldProps={{
            rows: 6,
            showCount: true,
            maxLength: 5000,
          }}
        />
        <ProFormSelect
          name="templateType"
          label="模板类型"
          options={Object.entries(templateTypeEnum).map(([value, { text }]) => ({
            label: text,
            value,
          }))}
          rules={[{ required: true, message: '请选择模板类型' }]}
        />
        <ProFormText
          name="templateCategory"
          label="模板分类"
          placeholder="请输入模板分类，如：写作助手"
        />
        <ProFormTextArea
          name="applicableModels"
          label="适用模型"
          placeholder='请输入适用模型（JSON数组格式），如：["QWEN_MAX","GPT4"]'
          fieldProps={{
            rows: 2,
          }}
        />
        <ProFormRadio.Group
          name="isSystem"
          label="是否系统模板"
          options={[
            { label: '否', value: 0 },
            { label: '是', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择是否系统模板' }]}
          disabled={!!currentRecord}
        />
        <ProFormRadio.Group
          name="isPublic"
          label="是否公开"
          options={[
            { label: '私有', value: 0 },
            { label: '公开', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择是否公开' }]}
        />
        <ProFormRadio.Group
          name="status"
          label="状态"
          options={[
            { label: '正常', value: 0 },
            { label: '停用', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择状态' }]}
        />
        <ProFormDigit
          name="sort"
          label="排序"
          placeholder="请输入排序值"
          fieldProps={{ precision: 0 }}
        />
        <ProFormTextArea name="remark" label="备注说明" placeholder="请输入备注说明" />
      </ModalForm>

      <Modal
        title="模板内容预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={[
          <Button key="close" onClick={() => setPreviewVisible(false)}>
            关闭
          </Button>,
        ]}
        width={800}
      >
        <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word', maxHeight: 500, overflow: 'auto' }}>
          {previewContent}
        </pre>
      </Modal>
    </PageContainer>
  );
};

export default PromptTemplateManagement;

