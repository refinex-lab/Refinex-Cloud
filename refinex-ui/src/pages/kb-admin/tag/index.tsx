import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  SearchOutlined,
  TagsOutlined,
} from '@ant-design/icons';
import { ModalForm, PageContainer, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import { useIntl } from '@umijs/max';
import {
  Button,
  Card,
  Col,
  ColorPicker,
  Empty,
  Input,
  message,
  Popconfirm,
  Row,
  Select,
  Space,
  Spin,
  Statistic,
  Table,
  Tag,
  Tooltip,
  Typography,
} from 'antd';
import type { Color } from 'antd/es/color-picker';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect, useState } from 'react';
import {
  createTag,
  deleteTag,
  batchDeleteTags,
  pageAllTags,
  updateTag,
} from '@/services/kb/tag';
import type { ContentTag, TagPageParams } from '@/services/kb/tag.d';

const { Search } = Input;
const { Title, Text } = Typography;

/**
 * 管理端-标签管理页面
 * 管理员可以查看和管理所有用户的标签
 */
const TagAdminManagement: React.FC = () => {
  const intl = useIntl();
  const [loading, setLoading] = useState(false);
  const [tags, setTags] = useState<ContentTag[]>([]);
  const [total, setTotal] = useState(0);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentTag, setCurrentTag] = useState<ContentTag | undefined>();
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [tagColor, setTagColor] = useState<string>('#1890ff');

  // 查询参数
  const [searchParams, setSearchParams] = useState<TagPageParams>({
    pageNum: 1,
    pageSize: 10,
  });

  // 统计数据
  const [statistics, setStatistics] = useState({
    totalTags: 0,
    systemTags: 0,
    userTags: 0,
    totalUsage: 0,
  });

  // 加载标签列表
  const loadTags = async (params: TagPageParams = searchParams) => {
    setLoading(true);
    try {
      const response = await pageAllTags(params);
      if (response.success) {
        setTags(response.data?.records || []);
        setTotal(response.data?.total || 0);

        // 计算统计数据
        const allTags = response.data?.records || [];
        setStatistics({
          totalTags: response.data?.total || 0,
          systemTags: allTags.filter((t) => t.tagType === 0).length,
          userTags: allTags.filter((t) => t.tagType === 1).length,
          totalUsage: allTags.reduce((sum, t) => sum + (t.usageCount || 0), 0),
        });
      }
    } catch (error) {
      console.error('加载标签失败:', error);
      message.error('加载标签失败');
    } finally {
      setLoading(false);
    }
  };

  // 搜索处理
  const handleSearch = (value: string) => {
    const params = {
      ...searchParams,
      tagName: value || undefined,
      pageNum: 1,
    };
    setSearchParams(params);
    loadTags(params);
  };

  // 筛选类型
  const handleFilterType = (tagType: number | undefined) => {
    const params = {
      ...searchParams,
      tagType,
      pageNum: 1,
    };
    setSearchParams(params);
    loadTags(params);
  };

  // 打开创建/编辑标签弹窗
  const handleOpenModal = (tag?: ContentTag) => {
    setCurrentTag(tag);
    setTagColor(tag?.tagColor || '#1890ff');
    setModalVisible(true);
  };

  // 创建或更新标签
  const handleSubmit = async (values: any) => {
    try {
      const params = {
        ...values,
        tagColor: tagColor,
      };

      if (currentTag) {
        // 更新标签
        const response = await updateTag(currentTag.id, params);
        if (response.success) {
          message.success('更新标签成功');
          loadTags();
          return true;
        }
      } else {
        // 创建标签
        const response = await createTag(params);
        if (response.success) {
          message.success('创建标签成功');
          loadTags();
          return true;
        }
      }
      return false;
    } catch (error) {
      message.error(currentTag ? '更新标签失败' : '创建标签失败');
      return false;
    }
  };

  // 删除标签
  const handleDelete = async (id: number) => {
    try {
      const response = await deleteTag(id);
      if (response.success) {
        message.success('删除标签成功');
        loadTags();
      }
    } catch (error) {
      message.error('删除标签失败');
    }
  };

  // 批量删除标签
  const handleBatchDelete = async () => {
    if (selectedRowKeys.length === 0) {
      message.warning('请选择要删除的标签');
      return;
    }

    try {
      const response = await batchDeleteTags(selectedRowKeys as number[]);
      if (response.success) {
        message.success(`成功删除 ${selectedRowKeys.length} 个标签`);
        setSelectedRowKeys([]);
        loadTags();
      }
    } catch (error) {
      message.error('批量删除标签失败');
    }
  };

  // 分页变化
  const handleTableChange = (pagination: any) => {
    const params = {
      ...searchParams,
      pageNum: pagination.current,
      pageSize: pagination.pageSize,
    };
    setSearchParams(params);
    loadTags(params);
  };

  // 表格列定义
  const columns: ColumnsType<ContentTag> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      align: 'center',
      render: (id) => <Text type="secondary">#{id}</Text>,
    },
    {
      title: '标签名称',
      dataIndex: 'tagName',
      key: 'tagName',
      width: 180,
      render: (text, record) => (
        <Tag color={record.tagColor} style={{ fontSize: '14px', padding: '4px 12px' }}>
          {text}
        </Tag>
      ),
    },
    {
      title: '颜色',
      dataIndex: 'tagColor',
      key: 'tagColor',
      width: 140,
      align: 'center',
      render: (color) => (
        <Space>
          <div
            style={{
              width: 32,
              height: 32,
              backgroundColor: color,
              borderRadius: 6,
              border: '1px solid #d9d9d9',
              boxShadow: '0 1px 2px rgba(0,0,0,0.1)',
            }}
          />
          <Text type="secondary" style={{ fontSize: 12, fontFamily: 'monospace' }}>
            {color}
          </Text>
        </Space>
      ),
    },
    {
      title: '类型',
      dataIndex: 'tagType',
      key: 'tagType',
      width: 110,
      align: 'center',
      render: (type) => (
        <Tag color={type === 0 ? 'processing' : 'success'} icon={<TagsOutlined />}>
          {type === 0 ? '系统' : '用户'}
        </Tag>
      ),
    },
    {
      title: '创建者',
      dataIndex: 'creatorId',
      key: 'creatorId',
      width: 100,
      align: 'center',
      render: (id) =>
        id === 0 ? (
          <Tag color="blue" icon={<TagsOutlined />}>
            系统
          </Tag>
        ) : (
          <Text>用户 #{id}</Text>
        ),
    },
    {
      title: '使用次数',
      dataIndex: 'usageCount',
      key: 'usageCount',
      width: 110,
      align: 'center',
      sorter: (a, b) => (a.usageCount || 0) - (b.usageCount || 0),
      render: (count) => <Tag color={count > 0 ? 'success' : 'default'}>{count || 0}</Tag>,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      sorter: (a, b) => new Date(a.createTime).getTime() - new Date(b.createTime).getTime(),
      render: (time) => (
        <Text type="secondary">{time ? new Date(time).toLocaleString('zh-CN') : '-'}</Text>
      ),
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      ellipsis: {
        showTitle: false,
      },
      render: (remark) => (
        <Tooltip placement="topLeft" title={remark}>
          <Text type="secondary">{remark || '-'}</Text>
        </Tooltip>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      align: 'center',
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Button
            type="link"
            size="small"
            icon={<EditOutlined />}
            onClick={() => handleOpenModal(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除这个标签吗？"
            description="删除后将无法恢复，请谨慎操作"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger size="small" icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  // 行选择配置
  const rowSelection = {
    selectedRowKeys,
    onChange: (keys: React.Key[]) => {
      setSelectedRowKeys(keys);
    },
  };

  useEffect(() => {
    loadTags();
  }, []);

  return (
    <PageContainer
      header={{
        title: '标签管理',
        subTitle: '管理所有用户的标签'
      }}
    >
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card
            bordered={false}
            style={{
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              boxShadow: '0 4px 12px rgba(102, 126, 234, 0.15)',
            }}
            styles={{
              body: { padding: '20px 24px' },
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>总标签数</span>}
              value={statistics.totalTags}
              suffix="个"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            bordered={false}
            style={{
              background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
              boxShadow: '0 4px 12px rgba(240, 147, 251, 0.15)',
            }}
            styles={{
              body: { padding: '20px 24px' },
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>系统标签</span>}
              value={statistics.systemTags}
              suffix="个"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            bordered={false}
            style={{
              background: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
              boxShadow: '0 4px 12px rgba(79, 172, 254, 0.15)',
            }}
            styles={{
              body: { padding: '20px 24px' },
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>用户标签</span>}
              value={statistics.userTags}
              suffix="个"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card
            bordered={false}
            style={{
              background: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
              boxShadow: '0 4px 12px rgba(67, 233, 123, 0.15)',
            }}
            styles={{
              body: { padding: '20px 24px' },
            }}
          >
            <Statistic
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>总使用次数</span>}
              value={statistics.totalUsage}
              suffix="次"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
      </Row>

      {/* 标签列表 */}
      <Card
        title={
          <Space>
            <TagsOutlined style={{ color: '#1890ff' }} />
            <Text strong>标签列表</Text>
            {selectedRowKeys.length > 0 && (
              <Tag color="processing">已选择 {selectedRowKeys.length} 项</Tag>
            )}
          </Space>
        }
      >
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          {/* 搜索和筛选 */}
          <Space style={{ width: '100%', justifyContent: 'space-between' }}>
            <Space size="middle">
              <Search
                placeholder="搜索标签名称"
                allowClear
                onSearch={handleSearch}
                style={{ width: 280 }}
                enterButton={<SearchOutlined />}
              />
              <Select
                placeholder="标签类型"
                allowClear
                style={{ width: 160 }}
                onChange={handleFilterType}
                defaultValue={undefined}
                options={[
                  { label: '全部类型', value: undefined },
                  { label: '系统标签', value: 0 },
                  { label: '用户标签', value: 1 },
                ]}
              />
            </Space>
            <Space size="middle">
              {selectedRowKeys.length > 0 && (
                <Popconfirm
                  title={`确定删除选中的 ${selectedRowKeys.length} 个标签吗？`}
                  description="此操作不可撤销，请谨慎操作"
                  onConfirm={handleBatchDelete}
                  okText="确定删除"
                  cancelText="取消"
                >
                  <Button danger icon={<DeleteOutlined />}>
                    批量删除 ({selectedRowKeys.length})
                  </Button>
                </Popconfirm>
              )}
              <Button type="primary" icon={<PlusOutlined />} onClick={() => handleOpenModal()}>
                新建标签
              </Button>
            </Space>
          </Space>

          {/* 表格 */}
          <Spin spinning={loading}>
            {tags.length === 0 && !loading ? (
              <Empty
                image={Empty.PRESENTED_IMAGE_SIMPLE}
                description="暂无标签数据"
              />
            ) : (
              <Table
                rowSelection={rowSelection}
                columns={columns}
                dataSource={tags}
                rowKey="id"
                pagination={{
                  current: searchParams.pageNum,
                  pageSize: searchParams.pageSize,
                  total: total,
                  showSizeChanger: true,
                  showQuickJumper: true,
                  showTotal: (total) => `共 ${total} 条`,
                  pageSizeOptions: ['10', '20', '50', '100'],
                }}
                onChange={handleTableChange}
                scroll={{ x: 1300 }}
                size="middle"
              />
            )}
          </Spin>
        </Space>
      </Card>

      {/* 创建/编辑标签弹窗 */}
      <ModalForm
        title={
          <Space>
            <TagsOutlined />
            {currentTag ? '编辑标签' : '新建标签'}
          </Space>
        }
        open={modalVisible}
        width={560}
        onOpenChange={setModalVisible}
        onFinish={handleSubmit}
        initialValues={currentTag}
        modalProps={{
          destroyOnClose: true,
        }}
        submitter={{
          searchConfig: {
            submitText: currentTag ? '保存' : '创建',
            resetText: '取消',
          },
        }}
      >
        <ProFormText
          name="tagName"
          label="标签名称"
          placeholder="请输入标签名称，例如：前端开发、React"
          rules={[
            { required: true, message: '请输入标签名称' },
            { max: 50, message: '标签名称不能超过50个字符' },
            {
              pattern: /^[\u4e00-\u9fa5a-zA-Z0-9\s]+$/,
              message: '标签名称只能包含中英文、数字和空格',
            },
          ]}
          fieldProps={{
            showCount: true,
            maxLength: 50,
          }}
        />

        <ProFormText
          label="标签颜色"
          tooltip="选择一个代表性的颜色，有助于快速识别标签"
          rules={[{ required: true, message: '请选择标签颜色' }]}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <ColorPicker
              value={tagColor}
              onChange={(color: Color) => {
                setTagColor(color.toHexString());
              }}
              showText
              size="large"
              presets={[
                {
                  label: '推荐色',
                  colors: [
                    '#1890ff',
                    '#52c41a',
                    '#fa8c16',
                    '#f5222d',
                    '#722ed1',
                    '#13c2c2',
                    '#eb2f96',
                    '#faad14',
                    '#a0d911',
                    '#2f54eb',
                  ],
                },
              ]}
            />
            <Tag color={tagColor} style={{ fontSize: 14, padding: '4px 12px', margin: 0 }}>
              预览效果
            </Tag>
          </div>
        </ProFormText>

        <ProFormSelect
          name="tagType"
          label="标签类型"
          placeholder="请选择标签类型"
          tooltip="系统标签对所有用户可见，用户标签仅对创建者可见"
          options={[
            { label: '系统标签（所有用户共享）', value: 0 },
            { label: '用户标签（仅创建者可见）', value: 1 },
          ]}
          rules={[{ required: true, message: '请选择标签类型' }]}
        />

        <ProFormText
          name="remark"
          label="备注"
          placeholder="请输入备注说明（可选），例如：用于前端相关文档分类"
          rules={[{ max: 500, message: '备注不能超过500个字符' }]}
          fieldProps={{
            showCount: true,
            maxLength: 500,
          }}
        />
      </ModalForm>
    </PageContainer>
  );
};

export default TagAdminManagement;

