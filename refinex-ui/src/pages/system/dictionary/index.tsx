import {
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  ModalForm,
  PageContainer,
  ProFormDigit,
  ProFormRadio,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { history, useIntl } from '@umijs/max';
import { Badge, Button, message, Popconfirm, Space, Tag, Tooltip } from 'antd';
import React, { useRef, useState } from 'react';
import type { DictType, DictTypeCreateRequest, DictTypeUpdateRequest } from '@/services/system';
import {
  createDictType,
  deleteDictType,
  getMaxDictTypeSort,
  queryDictTypes,
  updateDictType,
} from '@/services/system';

/**
 * 字典管理 - 字典类型列表（父页面）
 * 点击字典类型可进入该类型的数据管理页面
 */
const DictionaryTypeList: React.FC = () => {
  const intl = useIntl();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentType, setCurrentType] = useState<DictType | undefined>();
  const [initialDictSort, setInitialDictSort] = useState<number>(0);
  const actionRef = useRef<ActionType>(null);

  // 状态枚举
  const statusEnum = {
    1: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.normal' }), status: 'Success' },
    0: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.disabled' }), status: 'Default' },
  };

  // 字典类型列表列定义
  const columns: ProColumns<DictType>[] = [
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.code' }),
      dataIndex: 'dictCode',
      width: 160,
      ellipsis: true,
      render: (text, record) => (
        <a
          onClick={() => {
            // 跳转到字典数据页面
            history.push(`/system/dictionary/data/${record.id}?code=${record.dictCode}&name=${encodeURIComponent(record.dictName)}`);
          }}
        >
          {text}
        </a>
      ),
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.name' }),
      dataIndex: 'dictName',
      width: 180,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.desc' }),
      dataIndex: 'dictDesc',
      width: 280,
      ellipsis: true,
      hideInSearch: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.status' }),
      dataIndex: 'status',
      width: 100,
      valueEnum: statusEnum,
      render: (_, record) => (
        <Badge
          status={record.status === 1 ? 'success' : 'default'}
          text={statusEnum[record.status as 1 | 0]?.text}
        />
      ),
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.sort' }),
      dataIndex: 'dictSort',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.createTime' }),
      dataIndex: 'createTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.type.operation' }),
      valueType: 'option',
      width: 220,
      fixed: 'right',
      render: (_, record) => [
        <Tooltip title={intl.formatMessage({ id: 'pages.system.dictionary.button.viewData' })} key="view">
          <Button
            type="link"
            size="small"
            icon={<UnorderedListOutlined />}
            onClick={() => {
              history.push(`/system/dictionary/data/${record.id}?code=${record.dictCode}&name=${encodeURIComponent(record.dictName)}`);
            }}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.viewData' })}
          </Button>
        </Tooltip>,
        <Tooltip title={intl.formatMessage({ id: 'pages.system.dictionary.button.edit' })} key="edit">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => {
              setCurrentType(record);
              setModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.edit' })}
          </Button>
        </Tooltip>,
        <Popconfirm
          key="delete"
          title={intl.formatMessage({ id: 'pages.system.dictionary.message.deleteConfirmType' })}
          description={intl.formatMessage({ id: 'pages.system.dictionary.message.deleteTypeWarning' })}
          icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
          onConfirm={async () => {
            try {
              await deleteDictType(record.id);
              message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.deleteSuccess' }));
              actionRef.current?.reload();
            } catch (error) {
              // 错误提示由全局错误处理器统一处理
              console.error('删除字典类型失败:', error);
            }
          }}
        >
          <Tooltip title={intl.formatMessage({ id: 'pages.system.dictionary.button.delete' })}>
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
            >
              {intl.formatMessage({ id: 'pages.system.dictionary.button.delete' })}
            </Button>
          </Tooltip>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<DictType>
        actionRef={actionRef}
        rowKey="id"
        cardBordered
        search={{
          labelWidth: 'auto',
          span: 6,
          style: { marginBottom: 0 },
          collapsed: false,
          collapseRender: false,
          optionRender: (_, __, dom) => [
            ...dom.reverse(),
          ],
        }}
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={async () => {
              setCurrentType(undefined);
              // 获取最大排序值并设置为 maxSort + 1
              try {
                const response = await getMaxDictTypeSort();
                if (response.success && response.data !== undefined) {
                  setInitialDictSort(response.data + 1);
                } else {
                  setInitialDictSort(0);
                }
              } catch (error) {
                setInitialDictSort(0);
              }
              setModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.createType' })}
          </Button>,
        ]}
        columns={columns}
        request={async (params, sort) => {
          const sortField = Object.keys(sort || {})[0];
          const sortOrder = Object.values(sort || {})[0] as string;

          // 将前端的字段名转换为数据库字段名
          const fieldMapping: Record<string, string> = {
            'dictSort': 'dict_sort',
            'createTime': 'create_time',
          };

          const response = await queryDictTypes({
            dictCode: params.dictCode,
            dictName: params.dictName,
            status: params.status,
            pageNum: params.current || 1,
            pageSize: params.pageSize || 10,
            orderBy: sortField ? fieldMapping[sortField] || sortField : undefined,
            orderDirection: sortOrder === 'ascend' ? 'ASC' : sortOrder === 'descend' ? 'DESC' : undefined,
          });
          return {
            data: response.data?.records || [],
            success: response.success,
            total: response.data?.total || 0,
          };
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        scroll={{ x: 1200, y: 'calc(100vh - 380px)' }}
        options={{
          density: false,
          fullScreen: false,
        }}
      />

      {/* 字典类型表单 */}
      <ModalForm
        title={
          currentType
            ? intl.formatMessage({ id: 'pages.system.dictionary.button.editType' })
            : intl.formatMessage({ id: 'pages.system.dictionary.button.createType' })
        }
        width={600}
        open={modalVisible}
        onOpenChange={(visible) => {
          setModalVisible(visible);
          if (!visible) {
            // 弹窗关闭时清空当前编辑的数据
            setCurrentType(undefined);
          }
        }}
        key={currentType?.id || Date.now()}
        initialValues={currentType ? { ...currentType } : { status: 1, dictSort: initialDictSort }}
        onFinish={async (values) => {
          try {
            if (currentType) {
              await updateDictType(currentType.id, values as DictTypeUpdateRequest);
              message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.updateSuccess' }));
            } else {
              await createDictType(values as DictTypeCreateRequest);
              message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.createSuccess' }));
            }
            actionRef.current?.reload();
            return true;
          } catch (error) {
            // 错误提示由全局错误处理器统一处理
            console.error(currentType ? '更新字典类型失败:' : '创建字典类型失败:', error);
            return false;
          }
        }}
      >
        <ProFormText
          name="dictCode"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.code.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.code.placeholder' })}
          rules={[
            { required: true, message: intl.formatMessage({ id: 'pages.system.dictionary.form.code.required' }) },
            {
              pattern: /^[a-zA-Z0-9_]+$/,
              message: intl.formatMessage({ id: 'pages.system.dictionary.form.code.rule' }),
            },
          ]}
          disabled={!!currentType}
          fieldProps={{
            style: currentType ? { color: 'rgba(0, 0, 0, 0.88)' } : undefined,
          }}
        />
        <ProFormText
          name="dictName"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.name.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.name.placeholder' })}
          rules={[
            { required: true, message: intl.formatMessage({ id: 'pages.system.dictionary.form.name.required' }) },
          ]}
        />
        <ProFormTextArea
          name="dictDesc"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.desc.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.desc.placeholder' })}
          fieldProps={{ rows: 3 }}
        />
        <ProFormDigit
          name="dictSort"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.sort.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.sort.placeholder' })}
          fieldProps={{
            min: 0,
            precision: 0,
            style: { width: '100%' },
          }}
        />
        <ProFormRadio.Group
          name="status"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.status.label' })}
          options={[
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.normal' }), value: 1 },
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.disabled' }), value: 0 },
          ]}
        />
        <ProFormTextArea
          name="remark"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.remark.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.remark.placeholder' })}
          fieldProps={{ rows: 2 }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default DictionaryTypeList;
