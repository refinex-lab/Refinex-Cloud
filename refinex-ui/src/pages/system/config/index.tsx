import {
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  KeyOutlined,
  PlusOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormSelect,
  ProFormText,
  ProFormTextArea,
  ProFormDigit,
  ProFormRadio,
  ProTable,
} from '@ant-design/pro-components';
import { useIntl } from '@umijs/max';
import { Badge, Button, message, Space, Tag, Tooltip, Typography, Switch, Popconfirm } from 'antd';
import React, { useRef, useState } from 'react';
import type { SysConfig, SysConfigCreateRequest, SysConfigUpdateRequest } from '@/services/system';
import {
  createSysConfig,
  deleteSysConfig,
  querySysConfig,
  updateSysConfig,
  updateSysConfigFrontendVisibility,
} from '@/services/system';

const { Text } = Typography;

/**
 * 系统配置管理页面
 * 企业级系统配置管理界面，提供配置的增删改查、可见性控制等功能
 */
const SystemConfig: React.FC = () => {
  const intl = useIntl();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentConfig, setCurrentConfig] = useState<SysConfig | undefined>();
  const actionRef = useRef<ActionType>(null);

  // 状态枚举定义
  const configTypeEnum = {
    STRING: { text: '字符串', color: 'blue' },
    NUMBER: { text: '数字', color: 'green' },
    BOOLEAN: { text: '布尔值', color: 'orange' },
    JSON: { text: 'JSON', color: 'purple' },
  };

  const sensitiveEnum = {
    0: { text: '普通配置', status: 'Success' },
    1: { text: '敏感配置', status: 'Warning' },
  };

  const frontendEnum = {
    0: { text: '后端专用', status: 'Default' },
    1: { text: '前端可见', status: 'Success' },
  };

  // 渲染配置值（敏感配置需要脱敏显示）
  const renderConfigValue = (record: SysConfig) => {
    const { configValue, configType, isSensitive } = record;

    if (isSensitive === 1) {
      return (
        <Tooltip title="敏感配置已脱敏显示">
          <Text code style={{ color: '#ff4d4f' }}>
            {'*'.repeat(Math.min(configValue?.length || 8, 12))}
          </Text>
        </Tooltip>
      );
    }

    // 根据配置类型进行不同显示
    switch (configType) {
      case 'BOOLEAN':
        return (
          <Tag color={configValue === 'true' ? 'green' : 'red'}>
            {configValue === 'true' ? '是' : '否'}
          </Tag>
        );
      case 'NUMBER':
        return <Text code>{configValue}</Text>;
      case 'JSON':
        return (
          <Tooltip title={configValue}>
            <Text code ellipsis style={{ maxWidth: 200 }}>
              {configValue}
            </Text>
          </Tooltip>
        );
      default:
        return (
          <Tooltip title={configValue}>
            <Text ellipsis style={{ maxWidth: 200 }}>
              {configValue}
            </Text>
          </Tooltip>
        );
    }
  };

  // 切换前端可见性
  const handleToggleFrontendVisibility = async (record: SysConfig, checked: boolean) => {
    try {
      await updateSysConfigFrontendVisibility(record.id, checked ? 1 : 0);
      message.success(checked ? '已设置为前端可见' : '已设置为后端专用');
      actionRef.current?.reload();
    } catch (error) {
      // 错误提示由全局错误处理器统一处理
      actionRef.current?.reload(); // 恢复之前的状态
      console.error('切换前端可见性失败:', error);
    }
  };

  // 表格列定义
  const columns: ProColumns<SysConfig>[] = [
    {
      title: '配置键',
      dataIndex: 'configKey',
      width: 250,
      ellipsis: true,
      copyable: true,
      render: (text, record) => (
        <Space>
          <KeyOutlined style={{ color: '#1890ff' }} />
          <Text strong>{text}</Text>
          {record.isSensitive === 1 && (
            <Tag color="warning">敏感</Tag>
          )}
        </Space>
      ),
    },
    {
      title: '配置值',
      dataIndex: 'configValue',
      width: 250,
      ellipsis: true,
      search: false,
      render: (_, record) => renderConfigValue(record),
    },
    {
      title: '配置类型',
      dataIndex: 'configType',
      width: 100,
      valueEnum: configTypeEnum,
      render: (_, record) => (
        <Tag color={configTypeEnum[record.configType as keyof typeof configTypeEnum]?.color}>
          {configTypeEnum[record.configType as keyof typeof configTypeEnum]?.text}
        </Tag>
      ),
    },
    {
      title: '配置分组',
      dataIndex: 'configGroup',
      width: 120,
      ellipsis: true,
      render: (text) => text && <Tag>{text}</Tag>,
    },
    {
      title: '配置标签',
      dataIndex: 'configLabel',
      width: 150,
      ellipsis: true,
      search: false,
    },
    {
      title: '配置说明',
      dataIndex: 'configDesc',
      width: 200,
      ellipsis: true,
      search: false,
    },
    {
      title: '是否敏感',
      dataIndex: 'isSensitive',
      width: 100,
      valueEnum: sensitiveEnum,
      render: (_, record) => (
        <Badge
          status={record.isSensitive === 0 ? 'success' : 'warning'}
          text={sensitiveEnum[record.isSensitive as 0 | 1]?.text}
        />
      ),
    },
    {
      title: '前端可见',
      dataIndex: 'isFrontend',
      width: 150,
      valueEnum: frontendEnum,
      render: (_, record) => (
        <Space>
          <Badge
            status={record.isFrontend === 1 ? 'success' : 'default'}
            text={frontendEnum[record.isFrontend as 0 | 1]?.text}
          />
          <Switch
            size="small"
            checked={record.isFrontend === 1}
            onChange={(checked) => handleToggleFrontendVisibility(record, checked)}
            title="切换前端可见性"
          />
        </Space>
      ),
    },
    {
      title: '排序',
      dataIndex: 'sort',
      width: 80,
      search: false,
      sorter: true,
      align: 'center',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      valueType: 'dateTime',
      search: false,
      sorter: true,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 180,
      fixed: 'right',
      render: (_, record) => [
        <Tooltip title="编辑配置" key="edit">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => {
              setCurrentConfig(record);
              setModalVisible(true);
            }}
          >
            编辑
          </Button>
        </Tooltip>,
        <Popconfirm
          key="delete"
          title="删除确认"
          description={`确定要删除配置"${record.configKey}"吗？此操作不可恢复。`}
          icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
          onConfirm={async () => {
            try {
              await deleteSysConfig(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            } catch (error) {
              // 错误提示由全局错误处理器统一处理
              console.error('删除配置失败:', error);
            }
          }}
        >
          <Tooltip title="删除配置">
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
            >
              删除
            </Button>
          </Tooltip>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <PageContainer
      header={{
        title: false,
        breadcrumb: {
          items: [
            {
              path: '/system',
              title: '系统管理',
            },
            {
              title: '系统配置',
            },
          ],
        },
      }}
    >
      <ProTable<SysConfig>
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 'auto',
          span: 6,
          style: { marginBottom: 0 },
          collapsed: false,
          collapseRender: false,
          optionRender: (_, __, dom) => [...dom.reverse()],
        }}
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => {
              setCurrentConfig(undefined);
              setModalVisible(true);
            }}
          >
            新增配置
          </Button>,
        ]}
        columns={columns}
        request={async (params, sort) => {
          const response = await querySysConfig({
            configKey: params.configKey,
            configGroup: params.configGroup,
            configType: params.configType,
            isSensitive: params.isSensitive,
            isFrontend: params.isFrontend,
            pageNum: params.current || 1,
            pageSize: params.pageSize || 15,
            sortField: Object.keys(sort || {})[0],
            sortOrder: Object.values(sort || {})[0] as string,
          });
          return {
            data: response.data?.records || [],
            success: true,
            total: response.data?.total || 0,
          };
        }}
        pagination={{
          pageSize: 15,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条/共 ${total} 条`,
        }}
        scroll={{ x: 1400, y: 'calc(100vh - 380px)' }}
        options={{
          density: false,
          fullScreen: false,
          setting: {
            listsHeight: 400,
          },
        }}
        rowClassName={(record) =>
          record.isSensitive === 1 ? 'sensitive-row' : ''
        }
      />

      {/* 配置表单弹窗 */}
      <ModalForm
        title={
          currentConfig
            ? '编辑系统配置'
            : '新增系统配置'
        }
        width={800}
        open={modalVisible}
        onOpenChange={(visible) => {
          setModalVisible(visible);
          if (!visible) {
            setCurrentConfig(undefined);
          }
        }}
        initialValues={currentConfig ? { ...currentConfig } : {
          configType: 'STRING',
          isSensitive: 0,
          isFrontend: 1,
          sort: 0
        }}
        key={currentConfig?.id || 'new'}
        onFinish={async (values) => {
          try {
            if (currentConfig) {
              await updateSysConfig(currentConfig.id, values as SysConfigUpdateRequest);
              message.success('配置更新成功');
            } else {
              await createSysConfig(values as SysConfigCreateRequest);
              message.success('配置创建成功');
            }
            actionRef.current?.reload();
            return true;
          } catch (error) {
            // 错误提示由全局错误处理器统一处理
            console.error(currentConfig ? '配置更新失败:' : '配置创建失败:', error);
            return false;
          }
        }}
        grid
        rowProps={{ gutter: 16 }}
      >
        <ProFormText
          colProps={{ span: 12 }}
          name="configKey"
          label="配置键"
          placeholder="如：system.title"
          rules={[
            { required: true, message: '请输入配置键' },
            { pattern: /^[a-zA-Z][a-zA-Z0-9_.]*$/, message: '配置键格式不正确，应包含字母、数字、点或下划线' },
            { max: 128, message: '配置键长度不能超过128个字符' },
          ]}
          disabled={!!currentConfig}
          fieldProps={{
            prefix: <KeyOutlined />,
          }}
        />

        <ProFormSelect
          colProps={{ span: 12 }}
          name="configType"
          label="配置类型"
          options={[
            { label: '字符串 (STRING)', value: 'STRING' },
            { label: '数字 (NUMBER)', value: 'NUMBER' },
            { label: '布尔值 (BOOLEAN)', value: 'BOOLEAN' },
            { label: 'JSON', value: 'JSON' },
          ]}
          rules={[{ required: true, message: '请选择配置类型' }]}
        />

        <ProFormText
          colProps={{ span: 12 }}
          name="configGroup"
          label="配置分组"
          placeholder="如：system"
          fieldProps={{
            prefix: <SettingOutlined />,
          }}
        />

        <ProFormText
          colProps={{ span: 12 }}
          name="configLabel"
          label="配置标签"
          placeholder="如：系统标题"
        />

        <ProFormTextArea
          colProps={{ span: 24 }}
          name="configValue"
          label="配置值"
          placeholder="请输入配置值"
          rules={[
            { required: true, message: '请输入配置值' },
            { max: 2048, message: '配置值长度不能超过2048个字符' },
          ]}
          fieldProps={{
            rows: 3,
            ...(currentConfig?.configType === 'JSON' ? {
              style: { fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace' }
            } : {})
          }}
        />

        <ProFormTextArea
          colProps={{ span: 24 }}
          name="configDesc"
          label="配置说明"
          placeholder="请输入配置说明"
          fieldProps={{ rows: 2 }}
          rules={[{ max: 512, message: '配置说明长度不能超过512个字符' }]}
        />

        <ProFormDigit
          colProps={{ span: 8 }}
          name="sort"
          label="排序"
          placeholder="0"
          min={0}
          fieldProps={{ precision: 0 }}
        />

        <ProFormRadio.Group
          colProps={{ span: 8 }}
          name="isSensitive"
          label="是否敏感"
          options={[
            { label: '普通配置', value: 0 },
            { label: '敏感配置', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择是否为敏感配置' }]}
        />

        <ProFormRadio.Group
          colProps={{ span: 8 }}
          name="isFrontend"
          label="前端可见"
          options={[
            { label: '后端专用', value: 0 },
            { label: '前端可见', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择前端可见性' }]}
        />

        <ProFormTextArea
          colProps={{ span: 24 }}
          name="remark"
          label="备注"
          placeholder="请输入备注信息"
          fieldProps={{ rows: 2 }}
        />
      </ModalForm>

      <style>{`
        :global(.sensitive-row) {
          background-color: #fff7e6;
        }
        :global(.sensitive-row:hover) {
          background-color: #ffe7ba !important;
        }
      `}</style>
    </PageContainer>
  );
};

export default SystemConfig;
