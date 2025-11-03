import { PlusOutlined, EditOutlined, DeleteOutlined, ExclamationCircleOutlined, TeamOutlined } from '@ant-design/icons';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { PageContainer, ProTable, ModalForm, ProFormText, ProFormSelect, ProFormDigit, ProFormTextArea } from '@ant-design/pro-components';
import { Button, message, Popconfirm, Tag, Tooltip, Switch, Modal, Space } from 'antd';
import React, { useRef, useState, useEffect } from 'react';
import { useIntl, history } from '@umijs/max';
import {
  queryRoles,
  createRole,
  updateRole,
  updateRoleStatus,
  deleteRole,
  getMaxRoleSort,
} from '@/services/system/role';
import { listDictDataByTypeCode } from '@/services/system';
import type { RoleResponse, RoleCreateRequest, RoleUpdateRequest } from '@/services/system/typings';

/**
 * 角色管理页面
 */
const RoleManagement: React.FC = () => {
  const intl = useIntl();
  const actionRef = useRef<ActionType>(undefined);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRole, setCurrentRole] = useState<RoleResponse | undefined>();
  const [initialSort, setInitialSort] = useState<number>(0);

  // 动态字典数据
  const [roleTypeOptions, setRoleTypeOptions] = useState<{ label: string; value: number }[]>([]);
  const [roleTypeEnum, setRoleTypeEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [statusOptions, setStatusOptions] = useState<{ label: string; value: number }[]>([]);
  const [statusEnum, setStatusEnum] = useState<Record<string, { text: string; status: string }>>({});
  const [dataScopeOptions, setDataScopeOptions] = useState<{ label: string; value: number }[]>([]);
  const [dataScopeEnum, setDataScopeEnum] = useState<Record<string, { text: string; color: string }>>({});

  // 通用颜色池
  const colorPool = ['blue', 'green', 'orange', 'red', 'purple', 'cyan', 'magenta', 'volcano', 'gold', 'lime'];

  // 根据字符串哈希值分配颜色
  const getColorForValue = (value: string): string => {
    if (!value) return 'default';
    let hash = 0;
    for (let i = 0; i < value.length; i++) {
      hash = value.charCodeAt(i) + ((hash << 5) - hash);
    }
    const index = Math.abs(hash) % colorPool.length;
    return colorPool[index];
  };

  // 加载字典数据
  const loadDictionaries = async () => {
    try {
      const [roleTypeRes, statusRes, dataScopeRes] = await Promise.all([
        listDictDataByTypeCode('role_type'),
        listDictDataByTypeCode('common_status'),
        listDictDataByTypeCode('data_scope'),
      ]);

      // 角色类型字典
      if (roleTypeRes.success && roleTypeRes.data) {
        const rtOptions: { label: string; value: number }[] = [];
        const rtEnum: Record<string, { text: string; color: string }> = {};
        roleTypeRes.data.forEach((item) => {
          const numValue = Number(item.dictValue);
          rtOptions.push({ label: item.dictLabel, value: numValue });
          rtEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setRoleTypeOptions(rtOptions);
        setRoleTypeEnum(rtEnum);
      }

      // 状态字典
      if (statusRes.success && statusRes.data) {
        const stOptions: { label: string; value: number }[] = [];
        const stEnum: Record<string, { text: string; status: string }> = {};
        statusRes.data.forEach((item) => {
          const numValue = Number(item.dictValue);
          stOptions.push({ label: item.dictLabel, value: numValue });

          let status = 'Default';
          if (item.dictValue === '1') {
            status = 'Success';
          } else if (item.dictValue === '0') {
            status = 'Error';
          }

          stEnum[item.dictValue] = {
            text: item.dictLabel,
            status: status,
          };
        });
        setStatusOptions(stOptions);
        setStatusEnum(stEnum);
      }

      // 数据权限范围字典
      if (dataScopeRes.success && dataScopeRes.data) {
        const dsOptions: { label: string; value: number }[] = [];
        const dsEnum: Record<string, { text: string; color: string }> = {};
        dataScopeRes.data.forEach((item) => {
          const numValue = Number(item.dictValue);
          dsOptions.push({ label: item.dictLabel, value: numValue });
          dsEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setDataScopeOptions(dsOptions);
        setDataScopeEnum(dsEnum);
      }
    } catch (error) {
      console.error('加载字典数据失败:', error);
      message.error('加载字典数据失败');
    }
  };

  // 页面初始化时加载字典数据
  useEffect(() => {
    loadDictionaries();
  }, []);

  // 渲染角色类型标签
  const renderRoleTypeTag = (roleType: number) => {
    const typeInfo = roleTypeEnum[String(roleType)];
    return <Tag color={typeInfo?.color || 'default'}>{typeInfo?.text || roleType}</Tag>;
  };

  // 渲染数据权限标签
  const renderDataScopeTag = (dataScope: number) => {
    const scopeInfo = dataScopeEnum[String(dataScope)];
    return <Tag color={scopeInfo?.color || 'default'}>{scopeInfo?.text || dataScope}</Tag>;
  };

  // 渲染内置标签
  const renderBuiltinTag = (isBuiltin: number) => {
    return isBuiltin === 1 ? (
      <Tag color="purple">内置</Tag>
    ) : null;
  };

  // 渲染状态标签
  const renderStatusTag = (status: number) => {
    const statusInfo = statusEnum[String(status)];
    const color = statusInfo?.status === 'Success' ? 'success' : 'error';
    return <Tag color={color}>{statusInfo?.text || status}</Tag>;
  };

  // 变更角色状态
  const handleStatusChange = async (record: RoleResponse, checked: boolean) => {
    const newStatus = checked ? 1 : 0;
    try {
      await updateRoleStatus(record.id, newStatus);
      message.success(checked ? '角色已启用' : '角色已停用');
      actionRef.current?.reload();
    } catch (error) {
      // 错误提示由全局错误处理器统一处理
      actionRef.current?.reload(); // 恢复之前的状态
      console.error('变更角色状态失败:', error);
    }
  };

  // 表格列定义
  const columns: ProColumns<RoleResponse>[] = [
    {
      title: '角色ID',
      dataIndex: 'id',
      width: 100,
      hideInSearch: true,
      hideInTable: true,
      copyable: true,
    },
    {
      title: '角色名称',
      dataIndex: 'roleName',
      width: 150,
      ellipsis: true,
      order: 4,
    },
    {
      title: '角色类型',
      dataIndex: 'roleType',
      width: 120,
      render: (_, record) => renderRoleTypeTag(record.roleType),
      valueType: 'select',
      valueEnum: roleTypeEnum,
      order: 3,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 120,
      render: (_, record) => (
        <Space>
          {renderStatusTag(record.status)}
          <Switch
            checked={record.status === 1}
            disabled={record.isBuiltin === 1}
            onChange={(checked) => handleStatusChange(record, checked)}
            checkedChildren="正常"
            unCheckedChildren="停用"
            size="small"
          />
        </Space>
      ),
      valueType: 'select',
      valueEnum: statusEnum,
      order: 2,
    },
    {
      title: '角色编码',
      dataIndex: 'roleCode',
      width: 180,
      ellipsis: true,
      copyable: true,
      order: 1,
    },
    {
      title: '数据权限',
      dataIndex: 'dataScope',
      width: 130,
      render: (_, record) => renderDataScopeTag(record.dataScope),
      hideInSearch: true,
    },
    {
      title: '内置角色',
      dataIndex: 'isBuiltin',
      width: 100,
      render: (_, record) => renderBuiltinTag(record.isBuiltin),
      hideInSearch: true,
    },
    {
      title: '排序',
      dataIndex: 'sort',
      width: 80,
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      ellipsis: true,
      hideInSearch: true,
      width: 200,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
      sorter: true,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 250,
      fixed: 'right',
      render: (_, record) => [
        <Tooltip key="users" title="管理用户">
          <Button
            type="link"
            size="small"
            icon={<TeamOutlined />}
            onClick={() => {
              history.push(`/system/role/${record.id}/users`);
            }}
          >
            用户
          </Button>
        </Tooltip>,
        <Tooltip key="edit" title="编辑角色">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            disabled={record.isBuiltin === 1}
            onClick={async () => {
              setCurrentRole(record);
              setModalVisible(true);
            }}
          >
            编辑
          </Button>
        </Tooltip>,
        <Popconfirm
          key="delete"
          title="删除确认"
          description={
            <>
              <div>确定要删除角色"{record.roleName}"吗？</div>
              {record.isBuiltin === 1 && (
                <div style={{ color: 'red', marginTop: 8 }}>
                  ⚠️ 这是内置角色，禁止删除
                </div>
              )}
            </>
          }
          icon={<ExclamationCircleOutlined style={{ color: 'red' }} />}
          disabled={record.isBuiltin === 1}
          onConfirm={async () => {
            try {
              await deleteRole(record.id);
              message.success('删除成功');
              actionRef.current?.reload();
            } catch (error) {
              // 错误提示由全局错误处理器统一处理
              console.error('删除角色失败:', error);
            }
          }}
        >
          <Tooltip title={record.isBuiltin === 1 ? '内置角色禁止删除' : '删除角色'}>
            <Button
              type="link"
              size="small"
              danger
              icon={<DeleteOutlined />}
              disabled={record.isBuiltin === 1}
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
        subTitle: false
      }}
    >
      <ProTable<RoleResponse>
        columns={columns}
        actionRef={actionRef}
        cardBordered
        request={async (params, sort) => {
          const sortField = Object.keys(sort || {})[0];
          const sortOrder = Object.values(sort || {})[0] as string;

          // 将前端的字段名转换为数据库字段名
          const fieldMapping: Record<string, string> = {
            'sort': 'sort',
            'createTime': 'create_time',
          };

          const response = await queryRoles({
            roleCode: params.roleCode,
            roleName: params.roleName,
            roleType: params.roleType,
            status: params.status,
            pageNum: params.current,
            pageSize: params.pageSize,
            orderBy: sortField ? fieldMapping[sortField] || sortField : 'sort',
            orderDirection: sortOrder === 'ascend' ? 'ASC' : 'DESC',
          });

          return {
            data: response.data?.records || [],
            success: response.success,
            total: response.data?.total || 0,
          };
        }}
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        dateFormatter="string"
        headerTitle="角色列表"
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={async () => {
              setCurrentRole(undefined);
              // 获取最大排序值并设置为 maxSort + 1
              try {
                const response = await getMaxRoleSort();
                if (response.success && response.data !== undefined) {
                  setInitialSort(response.data + 1);
                } else {
                  setInitialSort(0);
                }
              } catch (error) {
                setInitialSort(0);
              }
              setModalVisible(true);
            }}
          >
            新建角色
          </Button>,
        ]}
        scroll={{ x: 1600, y: 'calc(100vh - 380px)' }}
        options={{
          density: false,
          fullScreen: false,
        }}
      />

      {/* 角色表单 */}
      <ModalForm<RoleCreateRequest | RoleUpdateRequest>
        title={currentRole ? '编辑角色' : '新建角色'}
        open={modalVisible}
        onOpenChange={setModalVisible}
        modalProps={{
          destroyOnClose: true,
          width: 600,
        }}
        initialValues={
          currentRole
            ? {
                roleCode: currentRole.roleCode,
                roleName: currentRole.roleName,
                roleType: currentRole.roleType,
                dataScope: currentRole.dataScope,
                sort: currentRole.sort,
                remark: currentRole.remark,
                status: currentRole.status,
              }
            : {
                roleType: 1,
                dataScope: 3,
                sort: initialSort,
                status: 0,
              }
        }
        onFinish={async (values) => {
          try {
            if (currentRole) {
              await updateRole(currentRole.id, values as RoleUpdateRequest);
              message.success('角色更新成功');
            } else {
              await createRole(values as RoleCreateRequest);
              message.success('角色创建成功');
            }
            actionRef.current?.reload();
            return true;
          } catch (error) {
            // 错误提示由全局错误处理器统一处理
            console.error(currentRole ? '更新角色失败:' : '创建角色失败:', error);
            return false;
          }
        }}
        grid
        rowProps={{ gutter: 16 }}
      >
        <ProFormText
          colProps={{ span: 24 }}
          name="roleCode"
          label="角色编码"
          placeholder="请输入角色编码（如：ROLE_MANAGER）"
          rules={[
            { required: true, message: '请输入角色编码' },
            {
              pattern: /^[A-Z][A-Z0-9_]*$/,
              message: '角色编码必须以大写字母开头，只能包含大写字母、数字和下划线',
            },
          ]}
          disabled={!!currentRole}
          fieldProps={{
            showCount: true,
            maxLength: 50,
          }}
          extra={currentRole ? '角色编码不可修改' : '建议格式：ROLE_XXX，如 ROLE_MANAGER'}
        />
        <ProFormText
          colProps={{ span: 24 }}
          name="roleName"
          label="角色名称"
          placeholder="请输入角色名称"
          rules={[{ required: true, message: '请输入角色名称' }]}
          fieldProps={{
            showCount: true,
            maxLength: 50,
          }}
        />
        <ProFormSelect
          colProps={{ span: 12 }}
          name="roleType"
          label="角色类型"
          options={roleTypeOptions}
          rules={[{ required: true, message: '请选择角色类型' }]}
          extra="前台角色用于APP端，后台角色用于管理端"
        />
        <ProFormSelect
          colProps={{ span: 12 }}
          name="dataScope"
          label="数据权限范围"
          options={dataScopeOptions}
          rules={[{ required: true, message: '请选择数据权限范围' }]}
          extra="控制该角色可以访问的数据范围"
        />
        <ProFormDigit
          colProps={{ span: 12 }}
          name="sort"
          label="排序"
          placeholder="请输入排序值"
          fieldProps={{
            precision: 0,
            min: 0,
          }}
          extra="数字越小越靠前"
        />
        <ProFormSelect
          colProps={{ span: 12 }}
          name="status"
          label="状态"
          options={statusOptions}
          extra="停用后该角色将无法使用"
        />
        <ProFormTextArea
          colProps={{ span: 24 }}
          name="remark"
          label="备注"
          placeholder="请输入备注信息"
          fieldProps={{
            showCount: true,
            maxLength: 500,
            rows: 4,
          }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default RoleManagement;

