import { EyeOutlined, EditOutlined, DeleteOutlined, LockOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { PageContainer, ProTable } from '@ant-design/pro-components';
import { Button, message, Popconfirm, Tag, Space, Tooltip, Modal, Form, Input, Select, Avatar } from 'antd';
import React, { useRef, useState } from 'react';
import {
  getUserList,
  updateUser,
  updateUserStatus,
  adminResetPassword,
  deleteUser,
  decryptSensitiveData,
  registerUser,
  type UserListItem,
} from '@/services/system/user';
import { listDictDataByTypeCode } from '@/services/system';
import { useEffect } from 'react';

const { Option } = Select;

const UserManagement: React.FC = () => {
  const actionRef = useRef<ActionType>(undefined);
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [resetPasswordModalVisible, setResetPasswordModalVisible] = useState(false);
  const [currentUser, setCurrentUser] = useState<UserListItem | undefined>();
  const [createForm] = Form.useForm();
  const [form] = Form.useForm();
  const [passwordForm] = Form.useForm();

  // 存储已解密的敏感数据: { userId_fieldCode: plainValue }
  const [decryptedData, setDecryptedData] = useState<Record<string, string>>({});

  // 动态字典数据
  const [userStatusEnum, setUserStatusEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [userTypeEnum, setUserTypeEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [registerSourceEnum, setRegisterSourceEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [registerTypeEnum, setRegisterTypeEnum] = useState<Record<string, { text: string; color: string }>>({});
  const [sexEnum, setSexEnum] = useState<Record<string, { text: string }>>({});
  const [commonStatusEnum, setCommonStatusEnum] = useState<Record<string, { text: string; status: string }>>({});

  // 通用颜色池（Ant Design 支持的标签颜色）
  const colorPool = [
    'blue',
    'green',
    'orange',
    'red',
    'purple',
    'cyan',
    'magenta',
    'volcano',
    'gold',
    'lime',
    'geekblue',
    'pink',
    'yellow',
  ];

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
      const [userStatusRes, userTypeRes, registerSourceRes, registerTypeRes, sexRes, commonStatusRes] = await Promise.all([
        listDictDataByTypeCode('user_status'),
        listDictDataByTypeCode('user_type'),
        listDictDataByTypeCode('register_source'),
        listDictDataByTypeCode('register_type'),
        listDictDataByTypeCode('sex'),
        listDictDataByTypeCode('common_status'),
      ]);

      // 用户状态字典
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

      // 用户类型字典
      if (userTypeRes.success && userTypeRes.data) {
        const utEnum: Record<string, { text: string; color: string }> = {};
        userTypeRes.data.forEach((item) => {
          utEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setUserTypeEnum(utEnum);
      }

      // 注册来源字典
      if (registerSourceRes.success && registerSourceRes.data) {
        const rsEnum: Record<string, { text: string; color: string }> = {};
        registerSourceRes.data.forEach((item) => {
          rsEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setRegisterSourceEnum(rsEnum);
      }

      // 注册类型字典
      if (registerTypeRes.success && registerTypeRes.data) {
        const rtEnum: Record<string, { text: string; color: string }> = {};
        registerTypeRes.data.forEach((item) => {
          rtEnum[item.dictValue] = {
            text: item.dictLabel,
            color: getColorForValue(item.dictValue),
          };
        });
        setRegisterTypeEnum(rtEnum);
      }

      // 性别字典
      if (sexRes.success && sexRes.data) {
        const sEnum: Record<string, { text: string }> = {};
        sexRes.data.forEach((item) => {
          sEnum[item.dictValue] = {
            text: item.dictLabel,
          };
        });
        setSexEnum(sEnum);
      }

      // 通用状态字典
      if (commonStatusRes.success && commonStatusRes.data) {
        const csEnum: Record<string, { text: string; status: string }> = {};
        commonStatusRes.data.forEach((item) => {
          // 根据字典值映射 ProTable 的状态
          let status = 'Default';
          if (item.dictValue === '0' || item.dictValue === 'normal' || item.dictValue === 'enabled') {
            status = 'Success';
          } else if (item.dictValue === '1' || item.dictValue === 'disabled' || item.dictValue === 'stopped') {
            status = 'Error';
          }

          csEnum[item.dictValue] = {
            text: item.dictLabel,
            status: status,
          };
        });
        setCommonStatusEnum(csEnum);
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

  // 查看敏感数据明文 - 局部更新显示
  const handleViewSensitive = async (userId: number, fieldCode: string) => {
    const cacheKey = `${userId}_${fieldCode}`;

    // 如果已经解密过，则不再请求
    if (decryptedData[cacheKey]) {
      return;
    }

    try {
      const response = await decryptSensitiveData({
        tableName: 'sys_user',
        rowGuid: String(userId),
        fieldCode,
      });

      if (response?.data?.plainValue) {
        // 更新已解密数据状态，触发表格重新渲染
        setDecryptedData(prev => ({
          ...prev,
          [cacheKey]: response.data.plainValue,
        }));
        message.success('明文已显示');
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      console.error('获取明文失败:', error);
    }
  };

  // 新增用户
  const handleCreate = () => {
    createForm.resetFields();
    setCreateModalVisible(true);
  };

  // 提交新增
  const handleCreateSubmit = async () => {
    try {
      const values = await createForm.validateFields();

      const response = await registerUser({
        username: values.username,
        nickname: values.nickname,
        mobile: values.mobile,
        email: values.email,
        password: values.password,
        registerSource: values.registerSource,
        registerType: values.registerType,
      });

      if (response?.success) {
        message.success('用户创建成功');
        setCreateModalVisible(false);
        actionRef.current?.reload();
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      console.error('创建用户失败:', error);
    }
  };

  // 编辑用户
  const handleEdit = (record: UserListItem) => {
    setCurrentUser(record);
    form.setFieldsValue({
      nickname: record.nickname,
      sex: record.sex,
      remark: record.remark,
    });
    setEditModalVisible(true);
  };

  // 提交编辑
  const handleEditSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (!currentUser) return;

      const response = await updateUser(currentUser.id, {
        ...values,
        userId: currentUser.id,
      });

      if (response?.success) {
        message.success('用户信息更新成功');
        setEditModalVisible(false);
        actionRef.current?.reload();
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      console.error('更新用户信息失败:', error);
    }
  };

  // 变更用户状态
  const handleStatusChange = async (userId: number, newStatus: number) => {
    try {
      const response = await updateUserStatus(userId, {
        userId,
        userStatus: newStatus,
      });

      if (response?.success) {
        message.success('用户状态变更成功');
        actionRef.current?.reload();
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      // 刷新表格以恢复之前的状态（因为 Select 已经改变了显示值）
      actionRef.current?.reload();
      console.error('变更用户状态失败:', error);
    }
  };

  // 重置密码
  const handleResetPassword = (record: UserListItem) => {
    setCurrentUser(record);
    passwordForm.resetFields();
    setResetPasswordModalVisible(true);
  };

  // 提交重置密码
  const handleResetPasswordSubmit = async () => {
    try {
      const values = await passwordForm.validateFields();
      if (!currentUser) return;

      const response = await adminResetPassword(currentUser.id, {
        userId: currentUser.id,
        newPassword: values.newPassword,
        reason: values.reason,
      });

      if (response?.success) {
        message.success('密码重置成功');
        setResetPasswordModalVisible(false);
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      console.error('重置密码失败:', error);
    }
  };

  // 删除用户
  const handleDelete = async (userId: number) => {
    try {
      const response = await deleteUser(userId);

      if (response?.success) {
        message.success('用户删除成功');
        actionRef.current?.reload();
      }
      // 错误提示由全局错误处理器统一处理
    } catch (error) {
      // 静默处理，错误提示由全局错误处理器统一处理
      console.error('删除用户失败:', error);
    }
  };

  const columns: ProColumns<UserListItem>[] = [
    {
      title: 'ID',
      dataIndex: 'id',
      width: 80,
      hideInSearch: true,
      hideInTable: true,
    },
    {
      title: '头像',
      dataIndex: 'avatar',
      width: 80,
      hideInSearch: true,
      render: (_, record) => (
        <Avatar src={record.avatar} size={40}>
          {record.nickname?.[0] || record.username[0]}
        </Avatar>
      ),
    },
    {
      title: '用户名',
      dataIndex: 'username',
      width: 150,
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
        const cacheKey = `${record.id}_mobile`;
        const plainValue = decryptedData[cacheKey];
        const displayValue = plainValue || record.mobile || '-';

        return (
          <Space>
            <span style={{ color: plainValue ? '#52c41a' : undefined }}>
              {displayValue}
            </span>
            {record.mobile && !plainValue && (
              <Tooltip title="查看明文">
                <EyeOutlined
                  style={{ cursor: 'pointer', color: '#1890ff' }}
                  onClick={() => handleViewSensitive(record.id, 'mobile')}
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
      render: (_, record) => {
        const cacheKey = `${record.id}_email`;
        const plainValue = decryptedData[cacheKey];
        const displayValue = plainValue || record.email || '-';

        return (
          <Space>
            <span style={{ color: plainValue ? '#52c41a' : undefined }}>
              {displayValue}
            </span>
            {record.email && !plainValue && (
              <Tooltip title="查看明文">
                <EyeOutlined
                  style={{ cursor: 'pointer', color: '#1890ff' }}
                  onClick={() => handleViewSensitive(record.id, 'email')}
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
      valueType: 'select',
      valueEnum: userStatusEnum,
      render: (_, record) => {
        const statusInfo = userStatusEnum[record.userStatus];
        return <Tag color={statusInfo?.color || 'default'}>{statusInfo?.text || record.userStatus}</Tag>;
      },
    },
    {
      title: '用户类型',
      dataIndex: 'userType',
      width: 120,
      valueType: 'select',
      valueEnum: userTypeEnum,
      render: (_, record) => {
        if (!record.userType) return '-';
        const typeInfo = userTypeEnum[record.userType];
        return <Tag color={typeInfo?.color || 'default'}>{typeInfo?.text || record.userType}</Tag>;
      },
    },
    {
      title: '注册来源',
      dataIndex: 'registerSource',
      width: 120,
      valueType: 'select',
      valueEnum: registerSourceEnum,
      render: (_, record) => {
        if (!record.registerSource) return '-';
        const sourceInfo = registerSourceEnum[record.registerSource];
        return <Tag color={sourceInfo?.color || 'default'}>{sourceInfo?.text || record.registerSource}</Tag>;
      },
    },
    {
      title: '性别',
      dataIndex: 'sex',
      width: 80,
      valueType: 'select',
      valueEnum: sexEnum,
      hideInSearch: true,
    },
    {
      title: '最后登录时间',
      dataIndex: 'lastLoginTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '最后登录IP',
      dataIndex: 'lastLoginIp',
      width: 150,
      hideInSearch: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      width: 180,
      valueType: 'dateTime',
      hideInSearch: true,
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      valueType: 'select',
      valueEnum: commonStatusEnum,
    },
    {
      title: '操作',
      valueType: 'option',
      width: 350,
      fixed: 'right',
      render: (_, record) => [
        <Button
          key="edit"
          type="link"
          size="small"
          icon={<EditOutlined />}
          onClick={() => handleEdit(record)}
        >
          编辑
        </Button>,
        <Button
          key="resetPassword"
          type="link"
          size="small"
          icon={<LockOutlined />}
          onClick={() => handleResetPassword(record)}
        >
          重置密码
        </Button>,
        <Select
          key="changeStatus"
          size="small"
          value={record.userStatus}
          style={{ width: 100 }}
          onChange={(value) => handleStatusChange(record.id, value)}
          options={Object.entries(userStatusEnum).map(([value, info]) => ({
            label: info.text,
            value: Number(value),
          }))}
        />,
        <Popconfirm
          key="delete"
          title="确认删除该用户吗？"
          description="删除后将无法恢复"
          onConfirm={() => handleDelete(record.id)}
          okText="确认"
          cancelText="取消"
        >
          <Button type="link" size="small" danger icon={<DeleteOutlined />}>
            删除
          </Button>
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
              title: '用户管理',
            },
          ],
        },
      }}
    >
      <ProTable<UserListItem>
        headerTitle="用户列表"
        actionRef={actionRef}
        rowKey="id"
        search={{
          labelWidth: 'auto',
        }}
        toolBarRender={() => [
          <Button
            key="create"
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreate}
          >
            新增用户
          </Button>,
        ]}
        request={async (params) => {
          // 每次刷新或查询时，清空已解密的敏感数据缓存
          setDecryptedData({});

          const response = await getUserList({
            username: params.username,
            nickname: params.nickname,
            mobile: params.mobile,
            email: params.email,
            userStatus: params.userStatus,
            userType: params.userType,
            registerSource: params.registerSource,
            status: params.status,
            pageNum: params.current,
            pageSize: params.pageSize,
          });

          return {
            data: response?.data?.records || [],
            success: response?.success,
            total: response?.data?.total || 0,
          };
        }}
        columns={columns}
        scroll={{ x: 1800 }}
      />

      {/* 新增用户Modal */}
      <Modal
        title="新增用户"
        open={createModalVisible}
        onOk={handleCreateSubmit}
        onCancel={() => setCreateModalVisible(false)}
        width={700}
        centered
      >
        <Form form={createForm} layout="vertical">
          <Form.Item
            label="用户名"
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input placeholder="请输入用户名" />
          </Form.Item>
          <Form.Item label="昵称" name="nickname">
            <Input placeholder="请输入昵称（可选，为空则后台随机生成）" />
          </Form.Item>
          <Form.Item
            label="密码"
            name="password"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码长度不能小于6位' },
              { max: 20, message: '密码长度不能大于20位' },
              {
                pattern: /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[^a-zA-Z0-9]).+$/,
                message: '密码必须包含字母、数字和特殊字符',
              },
            ]}
          >
            <Input.Password placeholder="请输入密码（6-20位，包含字母、数字和特殊字符）" />
          </Form.Item>
          <Form.Item
            label="确认密码"
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请再次输入密码" />
          </Form.Item>
          <Form.Item
            label="手机号"
            name="mobile"
            rules={[
              {
                pattern: /^1[3-9]\d{9}$/,
                message: '请输入正确的手机号',
              },
            ]}
          >
            <Input placeholder="请输入手机号（可选）" />
          </Form.Item>
          <Form.Item
            label="邮箱"
            name="email"
            rules={[
              {
                type: 'email',
                message: '请输入正确的邮箱地址',
              },
            ]}
          >
            <Input placeholder="请输入邮箱（可选）" />
          </Form.Item>
          <Form.Item
            label="注册来源"
            name="registerSource"
            rules={[{ required: true, message: '请选择注册来源' }]}
          >
            <Select placeholder="请选择注册来源">
              {Object.entries(registerSourceEnum).map(([value, info]) => (
                <Option key={value} value={value}>
                  {info.text}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            label="注册类型"
            name="registerType"
            rules={[{ required: true, message: '请选择注册类型' }]}
          >
            <Select placeholder="请选择注册类型">
              {Object.entries(registerTypeEnum).map(([value, info]) => (
                <Option key={value} value={value}>
                  {info.text}
                </Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* 编辑用户Modal */}
      <Modal
        title="编辑用户"
        open={editModalVisible}
        onOk={handleEditSubmit}
        onCancel={() => setEditModalVisible(false)}
        width={600}
      >
        <Form form={form} layout="vertical">
          <Form.Item label="昵称" name="nickname">
            <Input placeholder="请输入昵称" />
          </Form.Item>
          <Form.Item label="性别" name="sex">
            <Select placeholder="请选择性别">
              {Object.entries(sexEnum).map(([value, info]) => (
                <Option key={value} value={value}>
                  {info.text}
                </Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item label="备注" name="remark">
            <Input.TextArea rows={3} placeholder="请输入备注" />
          </Form.Item>
        </Form>
      </Modal>

      {/* 重置密码Modal */}
      <Modal
        title="重置密码"
        open={resetPasswordModalVisible}
        onOk={handleResetPasswordSubmit}
        onCancel={() => setResetPasswordModalVisible(false)}
        width={500}
      >
        <Form form={passwordForm} layout="vertical">
          <Form.Item
            label="新密码"
            name="newPassword"
            rules={[
              { required: true, message: '请输入新密码' },
              {
                pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/,
                message: '密码至少8位，包含大小写字母和数字',
              },
            ]}
          >
            <Input.Password placeholder="请输入新密码" />
          </Form.Item>
          <Form.Item
            label="确认密码"
            name="confirmPassword"
            dependencies={['newPassword']}
            rules={[
              { required: true, message: '请确认新密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('newPassword') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password placeholder="请再次输入新密码" />
          </Form.Item>
          <Form.Item label="重置原因" name="reason">
            <Input.TextArea rows={3} placeholder="请输入重置原因（选填）" />
          </Form.Item>
        </Form>
      </Modal>

    </PageContainer>
  );
};

export default UserManagement;

