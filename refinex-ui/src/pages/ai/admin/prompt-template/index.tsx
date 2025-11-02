import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  ExclamationCircleOutlined,
  EyeOutlined,
  QuestionCircleOutlined,
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
} from '@ant-design/pro-components';
import { Badge, Button, message, Popconfirm, Space, Switch, Tag, Tooltip, Modal, Card, Typography, Alert } from 'antd';
import React, { useRef, useState, useEffect } from 'react';
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
import { listDictDataByTypeCode } from '@/services/system/dictionary';
import type { DictData } from '@/services/system/typings';

/**
 * AI 提示词模板管理页面
 */
const { Text, Paragraph } = Typography;

const PromptTemplateManagement: React.FC = () => {
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<PromptTemplate | undefined>();
  const [previewVisible, setPreviewVisible] = useState(false);
  const [previewContent, setPreviewContent] = useState('');
  const [placeholderRuleVisible, setPlaceholderRuleVisible] = useState(false);
  const [categoryOptions, setCategoryOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [templateTypeOptions, setTemplateTypeOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [statusOptions, setStatusOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [publicStatusOptions, setPublicStatusOptions] = useState<Array<{ label: string; value: string }>>([]);
  const [yesNoOptions, setYesNoOptions] = useState<Array<{ label: string; value: string }>>([]);
  const actionRef = useRef<ActionType>(null);

  // 加载所有字典数据
  useEffect(() => {
    const loadDictionaries = async () => {
      try {
        // 并行加载所有字典数据
        const [categoryRes, templateTypeRes, statusRes, publicStatusRes, yesNoRes] = await Promise.all([
          listDictDataByTypeCode('ai_prompt_category'),
          listDictDataByTypeCode('ai_template_type'),
          listDictDataByTypeCode('common_status'),
          listDictDataByTypeCode('common_public_status'),
          listDictDataByTypeCode('boolean'),
        ]);

        // 处理模板分类
        if (categoryRes.code === 200 && categoryRes.data) {
          const options = categoryRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setCategoryOptions(options);
        }

        // 处理模板类型
        if (templateTypeRes.code === 200 && templateTypeRes.data) {
          const options = templateTypeRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setTemplateTypeOptions(options);
        }

        // 处理状态
        if (statusRes.code === 200 && statusRes.data) {
          const options = statusRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setStatusOptions(options);
        }

        // 处理公开状态
        if (publicStatusRes.code === 200 && publicStatusRes.data) {
          const options = publicStatusRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setPublicStatusOptions(options);
        }

        // 处理是否状态
        if (yesNoRes.code === 200 && yesNoRes.data) {
          const options = yesNoRes.data
            .sort((a, b) => (a.dictSort || 0) - (b.dictSort || 0))
            .map((item: DictData) => ({
              label: item.dictLabel,
              value: item.dictValue,
            }));
          setYesNoOptions(options);
        }
      } catch (error) {
        console.error('加载字典数据失败:', error);
      }
    };
    loadDictionaries();
  }, []);

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
      fieldProps: {
        options: templateTypeOptions,
      },
      render: (_, record) => {
        const typeOption = templateTypeOptions.find((opt) => opt.value === record.templateType);
        const colorMap: Record<string, string> = {
          SYSTEM: 'blue',
          USER: 'green',
        };
        return (
          <Tag color={colorMap[record.templateType] || 'default'}>
            {typeOption?.label || record.templateType}
          </Tag>
        );
      },
    },
    {
      title: '分类',
      dataIndex: 'templateCategory',
      width: 120,
      ellipsis: true,
      valueType: 'select',
      fieldProps: {
        options: categoryOptions,
      },
    },
    {
      title: '是否系统模板',
      dataIndex: 'isSystem',
      width: 120,
      hideInSearch: true,
      render: (_, record) => {
        const systemOption = yesNoOptions.find((opt) => opt.value === String(record.isSystem));
        return (
          <Badge
            status={record.isSystem === 1 ? 'processing' : 'default'}
            text={systemOption?.label || (record.isSystem === 1 ? '是' : '否')}
          />
        );
      },
    },
    {
      title: '是否公开',
      dataIndex: 'isPublic',
      width: 100,
      valueType: 'select',
      fieldProps: {
        options: publicStatusOptions,
      },
      render: (_, record) => {
        const publicOption = publicStatusOptions.find((opt) => opt.value === String(record.isPublic));
        return (
          <Badge
            status={record.isPublic === 1 ? 'success' : 'default'}
            text={publicOption?.label || (record.isPublic === 1 ? '公开' : '私有')}
          />
        );
      },
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
            disabled={record.isSystem === 1}
            onChange={async (checked) => {
              try {
                await toggleTemplateStatus(record.id, checked ? 1 : 0);
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
            } catch (_error) {
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
        // 创建 - 移除空的 templateCode，让后端自动生成
        const createData: any = { ...values };
        if (!createData.templateCode || createData.templateCode.trim() === '') {
          delete createData.templateCode;
        }
        await createPromptTemplate(createData as PromptTemplateCreateRequest);
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
        modalProps={{
          centered: true,
          destroyOnClose: true,
        }}
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
          label={
            <Space>
              <span>模板编码</span>
              <Tooltip title="新建时，模板编码将根据模板名称自动生成（如：'写作助手' → 'XZZS'）；编辑时不可修改">
                <QuestionCircleOutlined style={{ color: '#1890ff' }} />
              </Tooltip>
            </Space>
          }
          disabled={!currentRecord}
          placeholder={currentRecord ? undefined : '将根据模板名称自动生成'}
          fieldProps={{
            style: currentRecord ? undefined : { color: '#999' },
          }}
        />
        <ProFormText
          name="templateName"
          label="模板名称"
          placeholder="请输入模板名称"
          rules={[{ required: true, message: '请输入模板名称' }]}
        />
        <ProFormTextArea
          name="templateContent"
          label={
            <Space>
              <span>模板内容</span>
              <Tooltip title="点击查看变量占位符规则">
                <QuestionCircleOutlined
                  style={{ color: '#1890ff', cursor: 'pointer' }}
                  onClick={() => setPlaceholderRuleVisible(true)}
                />
              </Tooltip>
            </Space>
          }
          placeholder="请输入模板内容，支持变量占位符，如：{{变量名}}"
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
          options={templateTypeOptions}
          showSearch
          rules={[{ required: true, message: '请选择模板类型' }]}
        />
        <ProFormSelect
          name="templateCategory"
          label="模板分类"
          placeholder="请选择模板分类"
          options={categoryOptions}
          showSearch
          allowClear
        />
        <ProFormTextArea
          name="applicableModels"
          label="适用模型"
          placeholder='请输入适用模型（JSON数组格式），如：["QWEN_MAX","GPT4"]'
          fieldProps={{
            rows: 2,
          }}
        />
        <ProFormSelect
          name="isSystem"
          label="是否系统模板"
          options={yesNoOptions.map(opt => ({
            label: opt.label,
            value: Number(opt.value),
          }))}
          showSearch
          rules={[{ required: true, message: '请选择是否系统模板' }]}
          disabled={!!currentRecord}
        />
        <ProFormSelect
          name="isPublic"
          label="是否公开"
          options={publicStatusOptions.map(opt => ({
            label: opt.label,
            value: Number(opt.value),
          }))}
          showSearch
          rules={[{ required: true, message: '请选择是否公开' }]}
        />
        <ProFormSelect
          name="status"
          label="状态"
          options={statusOptions.map(opt => ({
            label: opt.label,
            value: Number(opt.value),
          }))}
          showSearch
          rules={[{ required: true, message: '请选择状态' }]}
        />
        <ProFormDigit
          name="sort"
          label="排序"
          placeholder="请输入排序值"
          fieldProps={{ precision: 0 }}
          rules={[{ required: true, message: '请输入排序值' }]}
        />
        <ProFormTextArea name="remark" label="备注说明" placeholder="请输入备注说明" />
      </ModalForm>

      <Modal
        title="模板内容预览"
        open={previewVisible}
        onCancel={() => setPreviewVisible(false)}
        footer={[
          <Button
            key="copy"
            onClick={() => {
              navigator.clipboard.writeText(previewContent).then(() => {
                message.success('复制成功');
              }).catch(() => {
                message.error('复制失败');
              });
            }}
          >
            复制
          </Button>,
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

      <Modal
        title="变量占位符使用规则"
        open={placeholderRuleVisible}
        onCancel={() => setPlaceholderRuleVisible(false)}
        footer={[
          <Button key="close" type="primary" onClick={() => setPlaceholderRuleVisible(false)}>
            我知道了
          </Button>,
        ]}
        width={700}
        centered
      >
        <Card bordered={false}>
          <Paragraph>
            <Text strong>变量占位符格式：</Text>
            <Text code>{'{{变量名}}'}</Text>
          </Paragraph>

          <Paragraph>
            <Text strong>使用规则：</Text>
          </Paragraph>
          <ul style={{ paddingLeft: 20 }}>
            <li>
              <Text>使用双花括号包裹变量名，例如：</Text>
              <Text code>{'{{用户名}}'}</Text>
              <Text>、</Text>
              <Text code>{'{{主题}}'}</Text>
            </li>
            <li>
              <Text>变量名支持中文、英文、数字和下划线</Text>
            </li>
            <li>
              <Text>变量名区分大小写</Text>
            </li>
            <li>
              <Text>同一个变量可以在模板中多次使用</Text>
            </li>
          </ul>

          <Paragraph>
            <Text strong>示例模板：</Text>
          </Paragraph>
          <Card type="inner" size="small" style={{ backgroundColor: '#f5f5f5' }}>
            <pre style={{ margin: 0, whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>
              {`你好，{{用户名}}！

请帮我写一篇关于{{主题}}的文章，要求如下：
1. 字数：{{字数}}字左右
2. 风格：{{风格}}
3. 目标读者：{{目标读者}}

请确保内容专业、准确，并且易于理解。`}
            </pre>
          </Card>

          <Paragraph style={{ marginTop: 16 }}>
            <Text strong>常用变量示例：</Text>
          </Paragraph>
          <Space wrap>
            <Tag color="blue">{'{{用户名}}'}</Tag>
            <Tag color="blue">{'{{主题}}'}</Tag>
            <Tag color="blue">{'{{内容}}'}</Tag>
            <Tag color="blue">{'{{语言}}'}</Tag>
            <Tag color="blue">{'{{风格}}'}</Tag>
            <Tag color="blue">{'{{字数}}'}</Tag>
            <Tag color="blue">{'{{要求}}'}</Tag>
            <Tag color="blue">{'{{上下文}}'}</Tag>
          </Space>

          <Alert
            message="提示"
            description="在实际使用时，系统会自动将占位符替换为用户输入的实际值"
            type="info"
            showIcon
            style={{ marginTop: 16 }}
          />
        </Card>
      </Modal>
    </PageContainer>
  );
};

export default PromptTemplateManagement;

