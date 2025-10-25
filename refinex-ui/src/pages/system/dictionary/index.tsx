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
  ProFormRadio,
  ProFormText,
  ProFormTextArea,
  ProTable,
} from '@ant-design/pro-components';
import { history, useIntl } from '@umijs/max';
import { Badge, Button, message, Modal, Space, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import type { DictType, DictTypeCreateRequest, DictTypeUpdateRequest } from '@/services/system';
import {
  createDictType,
  deleteDictType,
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
  const actionRef = useRef<ActionType>(null);

  // 状态枚举
  const statusEnum = {
    0: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.normal' }), status: 'Success' },
    1: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.disabled' }), status: 'Default' },
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
          status={record.status === 0 ? 'success' : 'default'}
          text={statusEnum[record.status as 0 | 1]?.text}
        />
      ),
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
      width: 200,
      fixed: 'right',
      render: (_, record) => [
        <a
          key="view"
          style={{ color: '#1890ff' }}
          onClick={() => {
            history.push(`/system/dictionary/data/${record.id}?code=${record.dictCode}&name=${encodeURIComponent(record.dictName)}`);
          }}
        >
          <UnorderedListOutlined /> {intl.formatMessage({ id: 'pages.system.dictionary.button.viewData' })}
        </a>,
        <a
          key="edit"
          style={{ color: '#faad14' }}
          onClick={() => {
            setCurrentType(record);
            setModalVisible(true);
          }}
        >
          <EditOutlined /> {intl.formatMessage({ id: 'pages.system.dictionary.button.edit' })}
        </a>,
        <a
          key="delete"
          style={{ color: '#ff4d4f' }}
          onClick={() => {
            Modal.confirm({
              title: intl.formatMessage({ id: 'pages.system.dictionary.message.deleteConfirmType' }),
              icon: <ExclamationCircleOutlined />,
              content: intl.formatMessage({ id: 'pages.system.dictionary.message.deleteTypeWarning' }),
              onOk: async () => {
                try {
                  await deleteDictType(record.id);
                  message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.deleteSuccess' }));
                  actionRef.current?.reload();
                } catch (error) {
                  message.error(intl.formatMessage({ id: 'pages.system.dictionary.message.deleteFailed' }));
                }
              },
            });
          }}
        >
          <DeleteOutlined /> {intl.formatMessage({ id: 'pages.system.dictionary.button.delete' })}
        </a>,
      ],
    },
  ];

  return (
    <PageContainer title={false}>
      <ProTable<DictType>
        actionRef={actionRef}
        rowKey="id"
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
            onClick={() => {
              setCurrentType(undefined);
              setModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.createType' })}
          </Button>,
        ]}
        columns={columns}
        request={async (params, sort) => {
          const response = await queryDictTypes({
            dictCode: params.dictCode,
            dictName: params.dictName,
            status: params.status,
            pageNum: params.current || 1,
            pageSize: params.pageSize || 10,
            sortField: Object.keys(sort || {})[0],
            sortOrder: Object.values(sort || {})[0] as string,
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
        initialValues={currentType ? { ...currentType } : { status: 0 }}
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
            message.error(
              currentType
                ? intl.formatMessage({ id: 'pages.system.dictionary.message.updateFailed' })
                : intl.formatMessage({ id: 'pages.system.dictionary.message.createFailed' }),
            );
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
        <ProFormRadio.Group
          name="status"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.status.label' })}
          options={[
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.normal' }), value: 0 },
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.disabled' }), value: 1 },
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
