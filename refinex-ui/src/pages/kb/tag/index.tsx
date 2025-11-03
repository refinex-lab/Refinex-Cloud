import {
  DeleteOutlined,
  EditOutlined,
  PlusOutlined,
  TagsOutlined,
} from '@ant-design/icons';
import { ModalForm, PageContainer, ProFormText } from '@ant-design/pro-components';
import { useIntl } from '@umijs/max';
import {
  Button,
  Card,
  Col,
  Empty,
  Input,
  message,
  Popconfirm,
  Row,
  Space,
  Spin,
  Statistic,
  Table,
  Tag,
  Tooltip,
  Typography,
} from 'antd';
import type { ColumnsType } from 'antd/es/table';
import React, { useEffect, useState } from 'react';
import {
  createTag,
  deleteTag,
  batchDeleteTags,
  getMyTags,
  getSystemTags,
  updateTag,
} from '@/services/kb/tag';
import type { ContentTag } from '@/services/kb/tag.d';
import { ColorPicker } from 'antd';
import type { Color } from 'antd/es/color-picker';

const { Search } = Input;
const { Title, Text } = Typography;

/**
 * 用户端-标签管理页面
 * 用户可以创建和管理自己的标签，标签名在同一用户下唯一
 */
const TagManagement: React.FC = () => {
  const intl = useIntl();
  const [loading, setLoading] = useState(false);
  const [myTags, setMyTags] = useState<ContentTag[]>([]);
  const [systemTags, setSystemTags] = useState<ContentTag[]>([]);
  const [filteredTags, setFilteredTags] = useState<ContentTag[]>([]);
  const [modalVisible, setModalVisible] = useState(false);
  const [currentTag, setCurrentTag] = useState<ContentTag | undefined>();
  const [searchKeyword, setSearchKeyword] = useState('');
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [tagColor, setTagColor] = useState<string>('#1890ff');

  // 加载我的标签
  const loadMyTags = async () => {
    setLoading(true);
    try {
      const response = await getMyTags();
      if (response.success) {
        setMyTags(response.data || []);
        filterTags(response.data || [], searchKeyword);
      }
    } catch (error) {
      console.error('加载标签失败:', error);
      message.error('加载标签失败');
    } finally {
      setLoading(false);
    }
  };

  // 加载系统标签
  const loadSystemTags = async () => {
    try {
      const response = await getSystemTags();
      if (response.success) {
        setSystemTags(response.data || []);
      }
    } catch (error) {
      console.error('加载系统标签失败:', error);
    }
  };

  // 过滤标签
  const filterTags = (tags: ContentTag[], keyword: string) => {
    if (!keyword) {
      setFilteredTags(tags);
      return;
    }
    const filtered = tags.filter((tag) =>
      tag.tagName.toLowerCase().includes(keyword.toLowerCase()),
    );
    setFilteredTags(filtered);
  };

  // 搜索处理
  const handleSearch = (value: string) => {
    setSearchKeyword(value);
    filterTags(myTags, value);
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
          loadMyTags();
          return true;
        }
      } else {
        // 创建标签
        const response = await createTag(params);
        if (response.success) {
          message.success('创建标签成功');
          loadMyTags();
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
        loadMyTags();
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
        loadMyTags();
      }
    } catch (error) {
      message.error('批量删除标签失败');
    }
  };

  // 表格列定义
  const columns: ColumnsType<ContentTag> = [
    {
      title: '标签名称',
      dataIndex: 'tagName',
      key: 'tagName',
      width: 200,
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
      width: 120,
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
      title: '使用次数',
      dataIndex: 'usageCount',
      key: 'usageCount',
      width: 120,
      align: 'center',
      sorter: (a, b) => (a.usageCount || 0) - (b.usageCount || 0),
      render: (count) => (
        <Tag color={count > 0 ? 'success' : 'default'}>{count || 0}</Tag>
      ),
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
    loadMyTags();
    loadSystemTags();
  }, []);

  return (
    <PageContainer
      header={{
        title: false,
        subTitle: false
      }}
    >
      {/* 统计卡片 */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={8}>
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
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>我的标签</span>}
              value={myTags.length}
              suffix="个"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
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
              value={systemTags.length}
              suffix="个"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={8}>
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
              title={<span style={{ color: 'rgba(255,255,255,0.9)', fontSize: 14 }}>总使用次数</span>}
              value={myTags.reduce((sum, tag) => sum + (tag.usageCount || 0), 0)}
              suffix="次"
              prefix={<TagsOutlined style={{ fontSize: 20 }} />}
              valueStyle={{ color: '#fff', fontWeight: 600, fontSize: 28 }}
            />
          </Card>
        </Col>
      </Row>

      {/* 系统标签展示 */}
      {systemTags.length > 0 && (
        <Card
          title={
            <Space>
              <TagsOutlined style={{ color: '#52c41a' }} />
              <Text strong>系统标签</Text>
              <Tag color="success" style={{ marginLeft: 8 }}>
                共享
              </Tag>
            </Space>
          }
          style={{ marginBottom: 24 }}
          bodyStyle={{ paddingTop: 16, paddingBottom: 16 }}
        >
          <Space wrap size={[12, 12]}>
            {systemTags.map((tag) => (
              <Tag
                key={tag.id}
                color={tag.tagColor}
                style={{
                  fontSize: '14px',
                  padding: '6px 14px',
                  borderRadius: '6px',
                  cursor: 'default',
                }}
              >
                {tag.tagName}
                <Text
                  style={{
                    marginLeft: 8,
                    fontSize: 12,
                    opacity: 0.8,
                  }}
                >
                  ({tag.usageCount || 0})
                </Text>
              </Tag>
            ))}
          </Space>
        </Card>
      )}

      {/* 我的标签 */}
      <Card
        title={
          <Space>
            <TagsOutlined style={{ color: '#1890ff' }} />
            <Text strong>我的标签</Text>
            {selectedRowKeys.length > 0 && (
              <Tag color="processing">已选择 {selectedRowKeys.length} 项</Tag>
            )}
          </Space>
        }
        extra={
          <Space size="middle">
            <Search
              placeholder="搜索标签名称"
              allowClear
              onSearch={handleSearch}
              onChange={(e) => handleSearch(e.target.value)}
              style={{ width: 240 }}
            />
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
        }
      >
        <Spin spinning={loading}>
          {filteredTags.length === 0 ? (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={
                <span>
                  {searchKeyword ? '没有找到相关标签' : '还没有标签，点击上方按钮创建第一个标签吧'}
                </span>
              }
            />
          ) : (
            <Table
              rowSelection={rowSelection}
              columns={columns}
              dataSource={filteredTags}
              rowKey="id"
              scroll={{ x: 1200 }}
              pagination={{
                pageSize: 10,
                showSizeChanger: true,
                showQuickJumper: true,
                showTotal: (total) => `共 ${total} 条`,
                pageSizeOptions: ['10', '20', '50', '100'],
              }}
              size="middle"
            />
          )}
        </Spin>
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
                    '#1890ff', // 拂晓蓝
                    '#52c41a', // 极光绿
                    '#fa8c16', // 日暮黄
                    '#f5222d', // 薄暮红
                    '#722ed1', // 酱紫
                    '#13c2c2', // 明青
                    '#eb2f96', // 法式洋红
                    '#faad14', // 金盏花
                    '#a0d911', // 青柠
                    '#2f54eb', // 极客蓝
                  ],
                },
              ]}
            />
            <Tag color={tagColor} style={{ fontSize: 14, padding: '4px 12px', margin: 0 }}>
              预览效果
            </Tag>
          </div>
        </ProFormText>

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

export default TagManagement;

