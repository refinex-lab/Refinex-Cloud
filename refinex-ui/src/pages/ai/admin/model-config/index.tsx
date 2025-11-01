import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ExclamationCircleOutlined,
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
import { Badge, Button, message, Popconfirm, Space, Switch, Tag, Tooltip } from 'antd';
import React, { useRef, useState } from 'react';
import type {
  ModelConfig,
  ModelConfigCreateRequest,
  ModelConfigUpdateRequest,
} from '@/services/ai/typings.d';
import {
  createModelConfig,
  deleteModelConfig,
  queryModelConfigs,
  toggleModelStatus,
  updateModelConfig,
} from '@/services/ai/model-config';

/**
 * AI 模型配置管理页面
 */
const ModelConfigManagement: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<ModelConfig | undefined>();
  const actionRef = useRef<ActionType>(null);

  // 供应商枚举
  const providerEnum = {
    OPENAI: { text: 'OpenAI', color: 'green' },
    ANTHROPIC: { text: 'Anthropic', color: 'blue' },
    QWEN: { text: '通义千问', color: 'orange' },
    ZHIPU: { text: '智谱AI', color: 'purple' },
    DEEPSEEK: { text: 'DeepSeek', color: 'cyan' },
  };

  // 模型类型枚举
  const modelTypeEnum = {
    CHAT: { text: '对话模型', color: 'blue' },
    IMAGE: { text: '图像模型', color: 'green' },
    VIDEO: { text: '视频模型', color: 'orange' },
    EMBEDDING: { text: '向量模型', color: 'purple' },
  };

  // 状态枚举
  const statusEnum = {
    0: { text: '正常', status: 'Success' },
    1: { text: '停用', status: 'Default' },
  };

  // 启用状态枚举
  const enabledEnum = {
    0: { text: '未启用', status: 'Default' },
    1: { text: '已启用', status: 'Success' },
  };

  // 列定义
  const columns: ProColumns<ModelConfig>[] = [
    {
      title: '模型编码',
      dataIndex: 'modelCode',
      width: 150,
      ellipsis: true,
      fixed: 'left',
    },
    {
      title: '模型名称',
      dataIndex: 'modelName',
      width: 180,
      ellipsis: true,
    },
    {
      title: '供应商',
      dataIndex: 'provider',
      width: 120,
      valueType: 'select',
      valueEnum: providerEnum,
      render: (_, record) => (
        <Tag color={providerEnum[record.provider as keyof typeof providerEnum]?.color}>
          {providerEnum[record.provider as keyof typeof providerEnum]?.text || record.provider}
        </Tag>
      ),
    },
    {
      title: '模型类型',
      dataIndex: 'modelType',
      width: 120,
      valueType: 'select',
      valueEnum: modelTypeEnum,
      render: (_, record) => (
        <Tag color={modelTypeEnum[record.modelType as keyof typeof modelTypeEnum]?.color}>
          {modelTypeEnum[record.modelType as keyof typeof modelTypeEnum]?.text || record.modelType}
        </Tag>
      ),
    },
    {
      title: '优先级',
      dataIndex: 'priority',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '是否启用',
      dataIndex: 'isEnabled',
      width: 100,
      hideInSearch: true,
      render: (_, record) => (
        <Badge
          status={record.isEnabled === 1 ? 'success' : 'default'}
          text={enabledEnum[record.isEnabled as 0 | 1]?.text}
        />
      ),
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
          onChange={async (checked) => {
            try {
              await toggleModelStatus(record.id, checked ? 0 : 1);
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
      width: 150,
      fixed: 'right',
      render: (_, record) => [
        <a
          key="edit"
          onClick={() => {
            setCurrentRecord(record);
            setModalVisible(true);
          }}
        >
          <EditOutlined /> 编辑
        </a>,
        <Popconfirm
          key="delete"
          title="确定要删除这个模型配置吗？"
          icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
          onConfirm={async () => {
            try {
              await deleteModelConfig(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            } catch (error) {
              message.error('删除失败');
            }
          }}
        >
          <a style={{ color: 'red' }}>
            <DeleteOutlined /> 删除
          </a>
        </Popconfirm>,
      ],
    },
  ];

  // 处理表单提交
  const handleSubmit = async (values: ModelConfigCreateRequest | ModelConfigUpdateRequest) => {
    try {
      if (currentRecord) {
        // 更新
        await updateModelConfig(currentRecord.id, values as ModelConfigUpdateRequest);
        message.success('更新成功');
      } else {
        // 创建
        await createModelConfig(values as ModelConfigCreateRequest);
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
            { title: '模型管理' },
          ],
        },
      }}
    >
      <ProTable<ModelConfig>
        headerTitle="模型配置列表"
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
            新建模型配置
          </Button>,
        ]}
        request={async (params, sort) => {
          const { current, pageSize, provider, modelType, status, ...rest } = params;
          const response = await queryModelConfigs({
            pageNum: current,
            pageSize,
            provider,
            modelType,
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
        scroll={{ x: 1500 }}
      />

      <ModalForm<ModelConfigCreateRequest | ModelConfigUpdateRequest>
        title={currentRecord ? '编辑模型配置' : '新建模型配置'}
        width={800}
        open={modalVisible}
        onOpenChange={setModalVisible}
        initialValues={
          currentRecord
            ? {
                ...currentRecord,
                apiKey: undefined, // 编辑时不显示密钥
              }
            : {
                isEnabled: 1,
                priority: 100,
                status: 0,
                sort: 0,
                timeoutSeconds: 60,
                retryTimes: 3,
                circuitBreakerThreshold: 10,
                temperature: 0.7,
              }
        }
        onFinish={handleSubmit}
        layout="horizontal"
        labelCol={{ span: 6 }}
        wrapperCol={{ span: 16 }}
      >
        <ProFormText
          name="modelCode"
          label="模型编码"
          placeholder="请输入模型编码，如：QWEN_MAX"
          rules={[{ required: true, message: '请输入模型编码' }]}
          disabled={!!currentRecord}
        />
        <ProFormText
          name="modelName"
          label="模型名称"
          placeholder="请输入模型名称"
          rules={[{ required: true, message: '请输入模型名称' }]}
        />
        <ProFormSelect
          name="provider"
          label="供应商"
          options={Object.entries(providerEnum).map(([value, { text }]) => ({
            label: text,
            value,
          }))}
          rules={[{ required: true, message: '请选择供应商' }]}
        />
        <ProFormSelect
          name="modelType"
          label="模型类型"
          options={Object.entries(modelTypeEnum).map(([value, { text }]) => ({
            label: text,
            value,
          }))}
          rules={[{ required: true, message: '请选择模型类型' }]}
        />
        <ProFormText
          name="apiEndpoint"
          label="API接口地址"
          placeholder="请输入API接口地址"
          rules={[{ required: true, message: '请输入API接口地址' }]}
        />
        <ProFormText.Password
          name="apiKey"
          label="API密钥"
          placeholder={currentRecord ? '留空则不修改' : '请输入API密钥'}
          rules={currentRecord ? [] : [{ required: true, message: '请输入API密钥' }]}
        />
        <ProFormDigit
          name="contextWindow"
          label="上下文窗口"
          placeholder="请输入上下文窗口大小"
          min={1}
          fieldProps={{ precision: 0 }}
        />
        <ProFormDigit
          name="maxTokens"
          label="最大输出Token"
          placeholder="请输入最大输出token数"
          min={1}
          fieldProps={{ precision: 0 }}
        />
        <ProFormDigit
          name="temperature"
          label="温度参数"
          placeholder="请输入温度参数"
          min={0}
          max={2}
          fieldProps={{ precision: 2, step: 0.1 }}
        />
        <ProFormDigit
          name="priority"
          label="优先级"
          placeholder="请输入优先级"
          rules={[{ required: true, message: '请输入优先级' }]}
          fieldProps={{ precision: 0 }}
        />
        <ProFormRadio.Group
          name="isEnabled"
          label="是否启用"
          options={[
            { label: '启用', value: 1 },
            { label: '不启用', value: 0 },
          ]}
          rules={[{ required: true, message: '请选择是否启用' }]}
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
        <ProFormTextArea name="remark" label="备注说明" placeholder="请输入备注说明" />
      </ModalForm>
    </PageContainer>
  );
};

export default ModelConfigManagement;

