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
import { Badge, Button, message, Popconfirm, Switch, Tag } from 'antd';
import React, { useRef, useState, useEffect } from 'react';
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
import SensitiveField from '@/components/SensitiveField';
import ProFormSensitiveField from '@/components/SensitiveField/ProFormSensitiveField';
import { listDictDataByTypeCode } from '@/services/system/dictionary';
import type { DictData } from '@/services/system/typings';

/**
 * AI 模型配置管理页面
 */
const ModelConfigManagement: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<ModelConfig | undefined>();
  const [providerOptions, setProviderOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [modelTypeOptions, setModelTypeOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [statusOptions, setStatusOptions] = useState<Array<{ label: string; value: string }>>([]);
  const actionRef = useRef<ActionType>(null);

  // 加载所有字典数据
  useEffect(() => {
    const loadDictionaries = async () => {
      try {
        // 并行加载所有字典数据
        const [providerRes, modelTypeRes, statusRes] = await Promise.all([
          listDictDataByTypeCode('ai_provider'),
          listDictDataByTypeCode('ai_model_type'),
          listDictDataByTypeCode('common_status'),
        ]);

        // 处理供应商字典
        if (providerRes.code === 200 && providerRes.data) {
          const options = providerRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setProviderOptions(options);
        }

        // 处理模型类型字典
        if (modelTypeRes.code === 200 && modelTypeRes.data) {
          const options = modelTypeRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setModelTypeOptions(options);
        }

        // 处理状态字典
        if (statusRes.code === 200 && statusRes.data) {
          const options = statusRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setStatusOptions(options);
        }
      } catch (error) {
        console.error('加载字典数据失败:', error);
      }
    };
    loadDictionaries();
  }, []);

  // 列定义
  const columns: ProColumns<ModelConfig>[] = [
    {
      title: '关键词',
      dataIndex: 'keyword',
      hideInTable: true,
      fieldProps: {
        placeholder: '搜索模型编码或模型名称',
      },
    },
    {
      title: '模型编码',
      dataIndex: 'modelCode',
      width: 150,
      ellipsis: true,
      fixed: 'left',
      hideInSearch: true,
    },
    {
      title: '模型版本',
      dataIndex: 'modelVersion',
      width: 180,
      ellipsis: true,
      hideInSearch: true,
      render: (_, record) => record.modelVersion || <span style={{ color: '#999' }}>-</span>,
    },
    {
      title: '模型名称',
      dataIndex: 'modelName',
      width: 180,
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: '供应商',
      dataIndex: 'provider',
      width: 120,
      valueType: 'select',
      fieldProps: {
        options: providerOptions,
      },
      render: (_, record) => {
        const providerOption = providerOptions.find((opt) => opt.value === record.provider);
        const colorMap: Record<string, string> = {
          OPENAI: 'green',
          ANTHROPIC: 'blue',
          QWEN: 'orange',
          ZHIPU: 'purple',
          DEEPSEEK: 'cyan',
        };
        return (
          <Tag color={colorMap[record.provider] || 'default'}>
            {providerOption?.label || record.provider}
          </Tag>
        );
      },
    },
    {
      title: '模型类型',
      dataIndex: 'modelType',
      width: 120,
      valueType: 'select',
      fieldProps: {
        options: modelTypeOptions,
      },
      render: (_, record) => {
        const modelTypeOption = modelTypeOptions.find((opt) => opt.value === record.modelType);
        const colorMap: Record<string, string> = {
          CHAT: 'blue',
          IMAGE: 'green',
          VIDEO: 'orange',
          EMBEDDING: 'purple',
        };
        return (
          <Tag color={colorMap[record.modelType] || 'default'}>
            {modelTypeOption?.label || record.modelType}
          </Tag>
        );
      },
    },
    {
      title: 'API密钥',
      dataIndex: 'apiKey',
      width: 330,
      hideInSearch: true,
      ellipsis: true,
      render: (_, record) => {
        if (!record.apiKey) {
          return <span style={{ color: '#999' }}>未配置</span>;
        }
        return (
          <div style={{ display: 'inline-block' }}>
            <SensitiveField
              maskedValue={record.apiKey}
              servicePath="/refinex-platform"
              tableName="ai_model_config"
              rowGuid={String(record.id)}
              fieldCode="api_key"
              copyable={true}
            />
          </div>
        );
      },
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
      render: (_, record) => {
        const enabledOption = statusOptions.find((opt) => opt.value === String(record.isEnabled));
        return (
          <Badge
            status={record.isEnabled === 1 ? 'success' : 'default'}
            text={enabledOption?.label || (record.isEnabled === 1 ? '已启用' : '未启用')}
          />
        );
      },
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      fieldProps: {
        options: statusOptions,
      },
      render: (_, record) => {
        const normalStatus = statusOptions.find((opt) => opt.value === '1');
        const disabledStatus = statusOptions.find((opt) => opt.value === '0');
        return (
          <Switch
            checked={record.status === 1}
            checkedChildren={normalStatus?.label || '正常'}
            unCheckedChildren={disabledStatus?.label || '停用'}
            onChange={async (checked) => {
              try {
                await toggleModelStatus(record.id, checked ? 1 : 0);
                message.success('状态更新成功');
                actionRef.current?.reload();
              } catch (_error) {
                message.error('状态更新失败');
              }
            }}
          />
        );
      },
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
            } catch (_error) {
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
    } catch (_error) {
      message.error(currentRecord ? '更新失败' : '创建失败');
      return false;
    }
  };

  return (
    <PageContainer
      header={{
        title: false,
        subTitle: false
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
        width={1200}
        open={modalVisible}
        onOpenChange={(visible) => {
          setModalVisible(visible);
          if (!visible) {
            setCurrentRecord(undefined);
          }
        }}
        key={currentRecord ? `edit-${currentRecord.id}` : 'create'}
        modalProps={{
          centered: true,
        }}
        initialValues={
          currentRecord
            ? {
                ...currentRecord,
                // apiKey 保留脱敏值，由 SensitiveFormField 组件处理
              }
            : {
                isEnabled: 1,
                priority: 100,
                status: 1,
                sort: 0,
                timeoutSeconds: 60,
                retryTimes: 3,
                circuitBreakerThreshold: 10,
                temperature: 0.7,
              }
        }
        onFinish={handleSubmit}
        layout="horizontal"
        labelAlign="right"
        labelCol={{ style: { width: '120px' } }}
        grid
        rowProps={{
          gutter: [16, 0],
        }}
      >
        <ProFormText
          name="modelCode"
          label="模型编码"
          placeholder="请输入模型编码，如：QWEN_MAX"
          rules={[{ required: true, message: '请输入模型编码' }]}
          disabled={!!currentRecord}
          colProps={{ span: 12 }}
        />
        <ProFormText
          name="modelVersion"
          label="模型版本"
          placeholder="请输入模型版本号，如：deepseek-chat"
          tooltip="可选字段，用于标识具体的模型版本"
          colProps={{ span: 12 }}
        />
        <ProFormText
          name="modelName"
          label="模型名称"
          placeholder="请输入模型名称"
          rules={[{ required: true, message: '请输入模型名称' }]}
          colProps={{ span: 12 }}
        />
        <ProFormSelect
          name="provider"
          label="供应商"
          options={providerOptions}
          rules={[{ required: true, message: '请选择供应商' }]}
          colProps={{ span: 12 }}
        />
        <ProFormSelect
          name="modelType"
          label="模型类型"
          options={modelTypeOptions}
          rules={[{ required: true, message: '请选择模型类型' }]}
          colProps={{ span: 12 }}
        />
        <ProFormDigit
          name="priority"
          label="优先级"
          placeholder="请输入优先级"
          rules={[{ required: true, message: '请输入优先级' }]}
          fieldProps={{ precision: 0 }}
          colProps={{ span: 12 }}
        />
        <ProFormText
          name="apiEndpoint"
          label="API接口地址"
          placeholder="请输入API接口地址"
          rules={[{ required: true, message: '请输入API接口地址' }]}
          colProps={{ span: 24 }}
        />
        <ProFormSensitiveField
          name="apiKey"
          label="API密钥"
          placeholder={currentRecord ? '留空则不修改' : '请输入API密钥'}
          rules={currentRecord ? [] : [{ required: true, message: '请输入API密钥' }]}
          isEdit={!!currentRecord}
          servicePath="/refinex-platform"
          tableName="ai_model_config"
          rowGuid={currentRecord ? String(currentRecord.id) : undefined}
          fieldCode="api_key"
          colProps={{ span: 12 }}
        />
        <ProFormDigit
          name="contextWindow"
          label="上下文窗口"
          placeholder="请输入上下文窗口大小"
          min={1}
          fieldProps={{ precision: 0 }}
          colProps={{ span: 12 }}
        />
        <ProFormDigit
          name="maxTokens"
          label="最大输出Token"
          placeholder="请输入最大输出token数"
          min={1}
          fieldProps={{ precision: 0 }}
          colProps={{ span: 12 }}
        />
        <ProFormDigit
          name="temperature"
          label="温度参数"
          placeholder="请输入温度参数"
          min={0}
          max={2}
          fieldProps={{ precision: 2, step: 0.1 }}
          colProps={{ span: 12 }}
        />
        <ProFormDigit
          name="sort"
          label="排序"
          placeholder="请输入排序值"
          fieldProps={{ precision: 0 }}
          rules={[{ required: true, message: '请输入排序值' }]}
          colProps={{ span: 12 }}
        />
        <ProFormRadio.Group
          name="isEnabled"
          label="是否启用"
          options={statusOptions.map((opt) => ({
            label: opt.label,
            value: Number(opt.value),
          }))}
          rules={[{ required: true, message: '请选择是否启用' }]}
          colProps={{ span: 12 }}
        />
        <ProFormRadio.Group
          name="status"
          label="状态"
          options={statusOptions.map((opt) => ({
            label: opt.label,
            value: Number(opt.value),
          }))}
          rules={[{ required: true, message: '请选择状态' }]}
          colProps={{ span: 12 }}
        />
        <ProFormTextArea
          name="remark"
          label="备注说明"
          placeholder="请输入备注说明"
          colProps={{ span: 24 }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default ModelConfigManagement;

