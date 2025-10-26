import { PlusOutlined, DeleteOutlined, ArrowLeftOutlined, ClockCircleOutlined, EyeOutlined } from '@ant-design/icons';
import type { ProColumns, ActionType } from '@ant-design/pro-components';
import { PageContainer, ProTable, ModalForm, ProFormSelect, ProFormDateTimePicker, ProForm } from '@ant-design/pro-components';
import { Button, message, Popconfirm, Tag, Space, Alert, Tooltip, AutoComplete } from 'antd';
import React, { useRef, useState, useEffect } from 'react';
import { history, useParams } from '@umijs/max';
import { queryRoleUsers, bindUsers, unbindUser, decryptRoleUserSensitiveData } from '@/services/system/role-user';
import { getRoleById } from '@/services/system/role';
import { getUserList, searchUsernames } from '@/services/system/user';
import { listDictDataByTypeCode } from '@/services/system';
import type { RoleUserResponse, UserRoleBindRequest, RoleResponse } from '@/services/system/typings';
import dayjs from 'dayjs';

/**
 * 角色用户管理页面
 */
const RoleUserManagement: React.FC = () => {
  const params = useParams<{ id: string }>();
  const roleId = Number(params.id);
  const actionRef = useRef<ActionType>(undefined);
  const [modalVisible, setModalVisible] = useState(false);
  const [roleInfo, setRoleInfo] = useState<RoleResponse | undefined>();
  const [userOptions, setUserOptions] = useState<{ label: string; value: number }[]>([]);
  const [userStatusEnum, setUserStatusEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [usernameOptions, setUsernameOptions] = useState<{ value: string }[]>([]);
  const [decryptedData, setDecryptedData] = useState<Record<string, string>>({});

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

  // 加载角色信息
  const loadRoleInfo = async () => {
    try {
      const response = await getRoleById(roleId);
      if (response.success && response.data) {
        setRoleInfo(response.data);
      }
    } catch (error) {
      console.error('加载角色信息失败:', error);
    }
  };

  // 加载用户选项
  const loadUserOptions = async () => {
    try {
      const response = await getUserList({ pageNum: 1, pageSize: 1000, status: 0 });
      if (response?.data?.records) {
        const options = response.data.records.map((user) => ({
          label: `${user.username}${user.nickname ? ` (${user.nickname})` : ''}`,
          value: user.id,
        }));
        setUserOptions(options);
      }
    } catch (error) {
      console.error('加载用户列表失败:', error);
    }
  };

  // 加载字典数据
  const loadDictionaries = async () => {
    try {
      const userStatusRes = await listDictDataByTypeCode('user_status');
      if (userStatusRes.success && userStatusRes.data) {
        const usEnum: Record<string, { text: string; color: string }> = {};
        userStatusRes.data.forEach((item) => {
          usEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setUserStatusEnum(usEnum);
      }
    } catch (error) {
      console.error('加载字典数据失败:', error);
    }
  };

  // 页面初始化
  useEffect(() => {
    loadRoleInfo();
    loadUserOptions();
    loadDictionaries();
  }, [roleId]);

  // 判断是否为临时授权
  const isTemporary = (record: RoleUserResponse) => {
    return record.validUntil !== undefined && record.validUntil !== null;
  };

  // 判断是否已过期
  const isExpired = (record: RoleUserResponse) => {
    if (!record.validUntil) return false;
    return dayjs(record.validUntil).isBefore(dayjs());
  };

  // 渲染用户状态标签
  const renderUserStatusTag = (userStatus: number) => {
    const statusInfo = userStatusEnum[String(userStatus)];
    return <Tag color={statusInfo?.color || 'default'}>{statusInfo?.text || userStatus}</Tag>;
  };

  // 用户名搜索联想
  const handleUsernameSearch = async (value: string) => {
    if (!value || value.trim().length === 0) {
      setUsernameOptions([]);
      return;
    }
    try {
      const response = await searchUsernames(value, 10);
      if (response?.data && response.data.length > 0) {
        setUsernameOptions(response.data.map((username: string) => ({ value: username })));
      } else {
        setUsernameOptions([]);
      }
    } catch (error) {
      console.error('搜索用户名失败:', error);
      setUsernameOptions([]);
    }
  };

  // 查看敏感数据明文
  const handleViewSensitive = async (userId: number, fieldCode: string) => {
    const cacheKey = `${userId}_${fieldCode}`;
    if (decryptedData[cacheKey]) {
      return;
    }

    try {
      const response = await decryptRoleUserSensitiveData({
        tableName: 'sys_user',
        rowGuid: String(userId),
        fieldCode,
      });

      if (response?.data?.plainValue) {
        setDecryptedData((prev) => ({
          ...prev,
          [cacheKey]: response.data.plainValue,
        }));
        message.success('明文已显示');
      }
    } catch (error) {
      console.error('获取明文失败:', error);
    }
  };

  // 表格列定义
  const columns: ProColumns<RoleUserResponse>[] = [
    {
      title: '用户ID',
      dataIndex: 'userId',
      width: 100,
      hideInSearch: true,
      hideInTable: true,
      copyable: true,
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 150,
      renderFormItem: () => (
        <AutoComplete
          options={usernameOptions}
          onSearch={handleUsernameSearch}
          placeholder="输入用户名搜索"
          allowClear
        />
      ),
    },
    {
      title: '昵称',
      dataIndex: 'nickname',
      width: 150,
    },
    {
      title: '手机号',
      dataIndex: 'mobile',
      width: 180,
      render: (_, record) => {
        const cacheKey = `${record.userId}_mobile`;
        const plainValue = decryptedData[cacheKey];
        const displayValue = plainValue || record.mobile || '-';

        return (
          <Space>
            <span style={{ color: plainValue ? '#52c41a' : undefined }}>{displayValue}</span>
            {record.mobile && !plainValue && (
              <Tooltip title="查看明文">
                <EyeOutlined
                  style={{ cursor: 'pointer', color: '#1890ff' }}
                  onClick={() => handleViewSensitive(record.userId, 'mobile')}
                />
              </Tooltip>
            )}
          </Space>
        );
      },
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      width: 200,
      ellipsis: true,
      render: (_, record) => {
        const cacheKey = `${record.userId}_email`;
        const plainValue = decryptedData[cacheKey];
        const displayValue = plainValue || record.email || '-';

        return (
          <Space>
            <span style={{ color: plainValue ? '#52c41a' : undefined }}>{displayValue}</span>
            {record.email && !plainValue && (
              <Tooltip title="查看明文">
                <EyeOutlined
                  style={{ cursor: 'pointer', color: '#1890ff' }}
                  onClick={() => handleViewSensitive(record.userId, 'email')}
                />
              </Tooltip>
            )}
          </Space>
        );
      },
    },
    {
      title: '用户状态',
      dataIndex: 'userStatus',
      width: 120,
      hideInSearch: true,
      render: (_, record) => renderUserStatusTag(record.userStatus),
    },
    {
      title: '授权类型',
      dataIndex: 'isTemporary',
      width: 120,
      hideInSearch: true,
      render: (_, record) => {
        if (isTemporary(record)) {
          const expired = isExpired(record);
          return (
            <Tag color={expired ? 'error' : 'warning'} icon={<ClockCircleOutlined />}>
              临时授权{expired ? '（已过期）' : ''}
            </Tag>
          );
        }
        return <Tag color="success">永久授权</Tag>;
      },
    },
    {
      title: '有效期开始',
      dataIndex: 'validFrom',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '有效期结束',
      dataIndex: 'validUntil',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '绑定时间',
      dataIndex: 'bindTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 120,
      fixed: 'right',
      render: (_, record) => [
        <Popconfirm
          key="unbind"
          title="解绑确认"
          description={`确定要将用户"${record.username}"从该角色中移除吗？`}
          onConfirm={async () => {
            try {
              await unbindUser(roleId, record.userId);
              message.success('解绑成功');
              actionRef.current?.reload();
            } catch (error) {
              console.error('解绑失败:', error);
            }
          }}
        >
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            解绑
          </Button>
        </Popconfirm>,
      ],
    },
  ];

  return (
    <PageContainer
      header={{
        title: roleInfo ? `${roleInfo.roleName} - 用户管理` : '角色用户管理',
        breadcrumb: {
          items: [
            { path: '/system', title: '系统管理' },
            { path: '/system/role', title: '角色管理' },
            { title: '用户管理' },
          ],
        },
        extra: [
          <Button
            key="back"
            icon={<ArrowLeftOutlined />}
            onClick={() => history.push('/system/role')}
          >
            返回角色列表
          </Button>,
        ],
      }}
    >
      <ProTable<RoleUserResponse>
        columns={columns}
        actionRef={actionRef}
        cardBordered
        request={async (params) => {
          // 每次查询时清空已解密的敏感数据缓存
          setDecryptedData({});

          const response = await queryRoleUsers(roleId, {
            username: params.username,
            nickname: params.nickname,
            mobile: params.mobile,
            email: params.email,
            pageNum: params.current,
            pageSize: params.pageSize,
          });

          return {
            data: response.data?.records || [],
            success: response.success,
            total: response.data?.total || 0,
          };
        }}
        rowKey="userId"
        search={{
          labelWidth: 'auto',
        }}
        pagination={{
          pageSize: 10,
          showSizeChanger: true,
          showQuickJumper: true,
        }}
        dateFormatter="string"
        headerTitle="用户列表"
        toolBarRender={() => [
          <Button
            key="bind"
            type="primary"
            icon={<PlusOutlined />}
            onClick={() => setModalVisible(true)}
          >
            添加用户
          </Button>,
        ]}
        scroll={{ x: 1600, y: 'calc(100vh - 450px)' }}
        options={{
          density: false,
          fullScreen: false,
        }}
      />

      {/* 添加用户表单 */}
      <ModalForm<UserRoleBindRequest>
        title="添加用户到角色"
        open={modalVisible}
        onOpenChange={setModalVisible}
        modalProps={{
          destroyOnClose: true,
          width: 800,
          centered: true,
        }}
        onFinish={async (values) => {
          try {
            await bindUsers(roleId, values);
            message.success('添加用户成功');
            actionRef.current?.reload();
            return true;
          } catch (error) {
            console.error('添加用户失败:', error);
            return false;
          }
        }}
      >
        <ProFormSelect
          name="userIds"
          label="选择用户"
          mode="multiple"
          showSearch
          placeholder="请选择要添加的用户"
          options={userOptions}
          rules={[{ required: true, message: '请至少选择一个用户' }]}
          fieldProps={{
            filterOption: (input, option) =>
              ((option?.label as string) ?? '').toLowerCase().includes(input.toLowerCase()),
          }}
        />
        <ProForm.Group>
          <ProFormDateTimePicker
            name="validFrom"
            label="有效期开始"
            placeholder="请选择有效期开始时间（可选）"
            extra="不填则立即生效"
            colProps={{ span: 12 }}
            fieldProps={{
              style: { width: '100%' },
            }}
          />
          <ProFormDateTimePicker
            name="validUntil"
            label="有效期结束"
            placeholder="请选择有效期结束时间（可选）"
            extra="不填则永久有效，填写则为临时授权"
            colProps={{ span: 12 }}
            fieldProps={{
              style: { width: '100%' },
            }}
          />
        </ProForm.Group>
      </ModalForm>
    </PageContainer>
  );
};

export default RoleUserManagement;

