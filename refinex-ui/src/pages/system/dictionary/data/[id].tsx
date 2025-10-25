import {
  ArrowLeftOutlined,
  DeleteOutlined,
  EditOutlined,
  ExclamationCircleOutlined,
  PlusOutlined,
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
import { history, useIntl, useParams, useSearchParams } from '@umijs/max';
import { Badge, Button, message, Modal, Space, Tag } from 'antd';
import React, { useRef, useState } from 'react';
import type { DictData, DictDataCreateRequest, DictDataUpdateRequest } from '@/services/system';
import {
  batchDeleteDictData,
  createDictData,
  deleteDictData,
  queryDictData,
  updateDictData,
} from '@/services/system';

/**
 * 字典管理 - 字典数据列表（子页面）
 * 展示某个字典类型下的所有数据项
 */
const DictionaryDataList: React.FC = () => {
  const intl = useIntl();
  const params = useParams();
  const [searchParams] = useSearchParams();
  const [modalVisible, setModalVisible] = useState(false);
  const [currentData, setCurrentData] = useState<DictData | undefined>();
  const actionRef = useRef<ActionType>(null);

  // 从 URL 获取字典类型信息
  const dictTypeId = Number(params.id);
  const dictCode = searchParams.get('code') || '';
  const dictName = searchParams.get('name') || '';

  // 状态枚举
  const statusEnum = {
    0: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.normal' }), status: 'Success' },
    1: { text: intl.formatMessage({ id: 'pages.system.dictionary.status.disabled' }), status: 'Default' },
  };

  // 字典数据列表列定义
  const columns: ProColumns<DictData>[] = [
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.label' }),
      dataIndex: 'dictLabel',
      width: 150,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.value' }),
      dataIndex: 'dictValue',
      width: 150,
      ellipsis: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.sort' }),
      dataIndex: 'dictSort',
      width: 100,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.cssClass' }),
      dataIndex: 'cssClass',
      width: 120,
      ellipsis: true,
      hideInSearch: true,
      render: (text) => text && <Tag>{text}</Tag>,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.isDefault' }),
      dataIndex: 'isDefault',
      width: 100,
      hideInSearch: true,
      valueEnum: {
        0: { text: intl.formatMessage({ id: 'pages.system.dictionary.form.isDefault.no' }), status: 'Default' },
        1: { text: intl.formatMessage({ id: 'pages.system.dictionary.form.isDefault.yes' }), status: 'Success' },
      },
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.status' }),
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
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.createTime' }),
      dataIndex: 'createTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: intl.formatMessage({ id: 'pages.system.dictionary.data.operation' }),
      valueType: 'option',
      width: 150,
      fixed: 'right',
      render: (_, record) => [
        <a
          key="edit"
          style={{ color: '#faad14' }}
          onClick={() => {
            setCurrentData(record);
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
              title: intl.formatMessage({ id: 'pages.system.dictionary.message.deleteConfirm' }),
              icon: <ExclamationCircleOutlined />,
              onOk: async () => {
                try {
                  await deleteDictData(record.id);
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
    <PageContainer
      header={{
        title: dictName,
        onBack: () => history.push('/system/dictionary'),
        breadcrumb: {
          items: [
            {
              path: '/system',
              title: intl.formatMessage({ id: 'menu.system' }),
            },
            {
              path: '/system/dictionary',
              title: intl.formatMessage({ id: 'menu.system.dictionary' }),
            },
            {
              title: intl.formatMessage({ id: 'pages.system.dictionary.data.title' }),
            },
          ],
        },
      }}
    >
      <ProTable<DictData>
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
            key="back"
            icon={<ArrowLeftOutlined />}
            onClick={() => history.push('/system/dictionary')}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.back' })}
          </Button>,
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => {
              setCurrentData(undefined);
              setModalVisible(true);
            }}
          >
            {intl.formatMessage({ id: 'pages.system.dictionary.button.createData' })}
          </Button>,
        ]}
        columns={columns}
        request={async (params, sort) => {
          const response = await queryDictData({
            dictTypeId,
            dictLabel: params.dictLabel,
            dictValue: params.dictValue,
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
        rowSelection={{
          onChange: (_, selectedRows) => {
            // Handle selection
          },
        }}
        tableAlertRender={({ selectedRowKeys, onCleanSelected }) => (
          <Space size={24}>
            <span>
              {intl.formatMessage(
                { id: 'pages.system.dictionary.selected' },
                { count: selectedRowKeys.length },
              )}
            </span>
            <a onClick={onCleanSelected}>
              {intl.formatMessage({ id: 'pages.system.dictionary.button.clearSelection' })}
            </a>
          </Space>
        )}
        tableAlertOptionRender={({ selectedRowKeys }) => (
          <Space size={16}>
            <a
              onClick={() => {
                Modal.confirm({
                  title: intl.formatMessage(
                    { id: 'pages.system.dictionary.message.batchDeleteConfirm' },
                    { count: selectedRowKeys.length },
                  ),
                  icon: <ExclamationCircleOutlined />,
                  onOk: async () => {
                    try {
                      await batchDeleteDictData(selectedRowKeys as number[]);
                      message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.deleteSuccess' }));
                      actionRef.current?.reload();
                    } catch (error) {
                      message.error(intl.formatMessage({ id: 'pages.system.dictionary.message.deleteFailed' }));
                    }
                  },
                });
              }}
            >
              {intl.formatMessage({ id: 'pages.system.dictionary.button.batchDelete' })}
            </a>
          </Space>
        )}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        scroll={{ x: 1200, y: 'calc(100vh - 420px)' }}
        options={{
          density: false,
          fullScreen: false,
        }}
      />

      {/* 字典数据表单 */}
      <ModalForm
        title={
          currentData
            ? intl.formatMessage({ id: 'pages.system.dictionary.button.editData' })
            : intl.formatMessage({ id: 'pages.system.dictionary.button.createData' })
        }
        width={800}
        open={modalVisible}
        onOpenChange={(visible) => {
          setModalVisible(visible);
          if (!visible) {
            // 弹窗关闭时清空当前编辑的数据
            setCurrentData(undefined);
          }
        }}
        initialValues={currentData ? { ...currentData } : { dictTypeId, status: 0, isDefault: 0, dictSort: 0 }}
        grid
        rowProps={{ gutter: 16 }}
        onFinish={async (values) => {
          try {
            if (currentData) {
              await updateDictData(currentData.id, values as DictDataUpdateRequest);
              message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.updateSuccess' }));
            } else {
              await createDictData(values as DictDataCreateRequest);
              message.success(intl.formatMessage({ id: 'pages.system.dictionary.message.createSuccess' }));
            }
            actionRef.current?.reload();
            return true;
          } catch (error) {
            // message.error(
            //   currentData
            //     ? intl.formatMessage({ id: 'pages.system.dictionary.message.updateFailed' })
            //     : intl.formatMessage({ id: 'pages.system.dictionary.message.createFailed' }),
            // );
            return false;
          }
        }}
      >
        <ProFormSelect
          colProps={{ span: 24 }}
          name="dictTypeId"
          label={intl.formatMessage({ id: 'pages.system.dictionary.data.typeId' })}
          disabled
          initialValue={dictTypeId}
          options={[{ label: `${dictCode} - ${dictName}`, value: dictTypeId }]}
        />
        <ProFormText
          colProps={{ span: 12 }}
          name="dictLabel"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.label.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.label.placeholder' })}
          rules={[
            { required: true, message: intl.formatMessage({ id: 'pages.system.dictionary.form.label.required' }) },
          ]}
        />
        <ProFormText
          colProps={{ span: 12 }}
          name="dictValue"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.value.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.value.placeholder' })}
          rules={[
            { required: true, message: intl.formatMessage({ id: 'pages.system.dictionary.form.value.required' }) },
          ]}
        />
        <ProFormDigit
          colProps={{ span: 12 }}
          name="dictSort"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.sort.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.sort.placeholder' })}
          min={0}
          fieldProps={{ precision: 0 }}
        />
        <ProFormRadio.Group
          colProps={{ span: 12 }}
          name="isDefault"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.isDefault.label' })}
          options={[
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.isDefault.no' }), value: 0 },
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.isDefault.yes' }), value: 1 },
          ]}
        />
        <ProFormText
          colProps={{ span: 12 }}
          name="cssClass"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.cssClass.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.cssClass.placeholder' })}
        />
        <ProFormText
          colProps={{ span: 12 }}
          name="listClass"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.listClass.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.listClass.placeholder' })}
        />
        <ProFormRadio.Group
          colProps={{ span: 24 }}
          name="status"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.status.label' })}
          options={[
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.normal' }), value: 0 },
            { label: intl.formatMessage({ id: 'pages.system.dictionary.form.status.disabled' }), value: 1 },
          ]}
        />
        <ProFormTextArea
          colProps={{ span: 24 }}
          name="remark"
          label={intl.formatMessage({ id: 'pages.system.dictionary.form.remark.label' })}
          placeholder={intl.formatMessage({ id: 'pages.system.dictionary.form.remark.placeholder' })}
          fieldProps={{ rows: 2 }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default DictionaryDataList;

