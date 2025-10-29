/**
 * 目录内容视图组件
 * 显示目录下的文档列表（支持卡片/列表两种视图）
 */
import React, { useEffect, useState, useMemo } from 'react';
import {
  Avatar,
  Button,
  Card,
  Empty,
  Input,
  List,
  Space,
  Tag,
  Tooltip,
  Typography,
  Segmented,
  Row,
  Col,
} from 'antd';
import {
  AppstoreOutlined,
  ClockCircleOutlined,
  CommentOutlined,
  EyeOutlined,
  FileTextOutlined,
  FolderOpenFilled,
  LikeOutlined,
  PlusOutlined,
  SearchOutlined,
  StarOutlined,
  TagsOutlined,
  UnorderedListOutlined,
  UserOutlined,
} from '@ant-design/icons';
import type { ContentTreeNode } from '@/services/kb/typings.d';
import { DocumentStatus } from '@/services/kb/typings.d';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import './DirectoryView.less';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

const { Paragraph, Text } = Typography;

interface DirectoryViewProps {
  directory: ContentTreeNode;
  spaceId: number;
  onDocumentOpen: (docGuid: string, document: ContentTreeNode) => void;
  onCreateDocument: (directoryId: number) => void;
}

type ViewMode = 'card' | 'list';

const DirectoryView: React.FC<DirectoryViewProps> = ({
  directory,
  spaceId,
  onDocumentOpen,
  onCreateDocument,
}) => {
  const [documents, setDocuments] = useState<ContentTreeNode[]>([]);
  const [viewMode, setViewMode] = useState<ViewMode>('card');
  const [searchKeyword, setSearchKeyword] = useState('');

  // 从目录的 children 中提取文档节点
  useEffect(() => {
    if (directory.children) {
      // 筛选出文档类型的子节点
      const docNodes = directory.children.filter((child) => child.nodeType === 'document');
      setDocuments(docNodes);
    } else {
      setDocuments([]);
    }
  }, [directory.children, directory.directoryId]);

  // 根据搜索关键词过滤文档
  const filteredDocuments = useMemo(() => {
    if (!searchKeyword.trim()) {
      return documents;
    }
    const keyword = searchKeyword.toLowerCase().trim();
    return documents.filter(
      (doc) =>
        doc.docTitle?.toLowerCase().includes(keyword) ||
        doc.title?.toLowerCase().includes(keyword) ||
        doc.docSummary?.toLowerCase().includes(keyword) ||
        doc.createByName?.toLowerCase().includes(keyword),
    );
  }, [documents, searchKeyword]);

  // 渲染文档状态标签
  const renderStatusTag = (doc: ContentTreeNode) => {
    if (doc.docStatus === DocumentStatus.DRAFT) {
      return (
        <Tag color="default" style={{ fontSize: 12, background: "transparent" }}>
          草稿
        </Tag>
      );
    }
    if (doc.docStatus === DocumentStatus.OFFLINE) {
      return (
        <Tag color="error" style={{ fontSize: 12 }}>
          已下架
        </Tag>
      );
    }
    return null;
  };

  // 格式化时间显示
  const formatTime = (timeStr?: string) => {
    if (!timeStr) return '未知';
    return dayjs(timeStr).fromNow();
  };

  // 渲染卡片视图
  const renderCardView = () => (
    <List
      grid={{
        gutter: 16,
        xs: 1,
        sm: 2,
        md: 2,
        lg: 3,
        xl: 3,
        xxl: 4,
      }}
      dataSource={filteredDocuments}
      renderItem={(doc) => (
        <List.Item>
          <Card
            hoverable
            className="document-card"
            onClick={() => onDocumentOpen(doc.docGuid!, doc)}
            cover={
              <div className="document-card-cover-wrapper">
                {doc.coverImage ? (
                  <div className="document-card-cover-image">
                    <img alt={doc.docTitle || doc.title} src={doc.coverImage} />
                  </div>
                ) : (
                  <div className="document-card-cover">
                    <FileTextOutlined />
                  </div>
                )}

                {/* 标签悬浮层 - 在封面内部 */}
                {doc.tags && doc.tags.length > 0 && (
                  <div className="document-card-tags-overlay">
                    {/* 标签云图标 */}
                    <div className="tags-cloud-indicator">
                      <TagsOutlined />
                      <span className="tags-count">{doc.tags.length}</span>
                    </div>

                    {/* 悬浮显示的标签列表 */}
                    <div className="tags-popover-content">
                      <Space size={6} wrap>
                        {doc.tags.map((tag) => (
                          <Tag
                            key={tag.id}
                            color={tag.tagColor || 'blue'}
                            style={{
                              fontSize: 12,
                              padding: '2px 8px',
                              margin: 0,
                              cursor: 'pointer',
                            }}
                            onClick={(e) => {
                              e.stopPropagation();
                              // TODO: 点击标签筛选同标签文档
                              console.log('Filter by tag:', tag.tagName);
                            }}
                          >
                            {tag.tagName}
                          </Tag>
                        ))}
                      </Space>
                    </div>
                  </div>
                )}
              </div>
            }
          >
            <Card.Meta
              title={
                <div className="document-card-title-wrapper">
                  <Tooltip title={doc.docTitle || doc.title}>
                    <span className="document-card-title">{doc.docTitle || doc.title}</span>
                  </Tooltip>
                  {renderStatusTag(doc)}
                </div>
              }
              description={
                <div className="document-card-description">
                  <Paragraph
                    ellipsis={{ rows: 2 }}
                    className="document-summary"
                  >
                    {doc.docSummary || '暂无简介'}
                  </Paragraph>

                  <div className="document-meta">
                    <Space size={4} className="document-author">
                      <Avatar size={20} icon={<UserOutlined />} />
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        {doc.createByName || '未知'}
                      </Text>
                    </Space>
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      {formatTime(doc.updateTime)}
                    </Text>
                  </div>

                  <div className="document-stats">
                    <Tooltip title="浏览数">
                      <span>
                        <EyeOutlined /> {doc.viewCount || 0}
                      </span>
                    </Tooltip>
                    <Tooltip title="点赞数">
                      <span>
                        <LikeOutlined /> {doc.likeCount || 0}
                      </span>
                    </Tooltip>
                    <Tooltip title="收藏数">
                      <span>
                        <StarOutlined /> {doc.collectCount || 0}
                      </span>
                    </Tooltip>
                    {doc.readDuration && doc.readDuration > 0 && (
                      <Tooltip title="预计阅读时长">
                        <span>
                          <ClockCircleOutlined /> {doc.readDuration}分钟
                        </span>
                      </Tooltip>
                    )}
                  </div>
                </div>
              }
            />
          </Card>
        </List.Item>
      )}
    />
  );

  // 渲染列表视图
  const renderListView = () => (
    <List
      className="document-list-view"
      itemLayout="vertical"
      dataSource={filteredDocuments}
      renderItem={(doc) => (
        <List.Item
          className="document-list-item"
          onClick={() => onDocumentOpen(doc.docGuid!, doc)}
          extra={
            doc.coverImage && (
              <img
                width={200}
                alt={doc.docTitle || doc.title}
                src={doc.coverImage}
                className="document-list-cover"
              />
            )
          }
        >
          <List.Item.Meta
            avatar={
              !doc.coverImage && (
                <div className="document-list-icon">
                  <FileTextOutlined />
                </div>
              )
            }
            title={
              <Space size="small">
                <span className="document-list-title">{doc.docTitle || doc.title}</span>
                {renderStatusTag(doc)}
              </Space>
            }
            description={
              <div className="document-list-description">
                <Paragraph
                  ellipsis={{ rows: 2 }}
                  style={{ marginBottom: 12, color: '#595959', fontSize: 14 }}
                >
                  {doc.docSummary || '暂无简介'}
                </Paragraph>

                {/* 标签展示 */}
                {doc.tags && doc.tags.length > 0 && (
                  <div style={{ marginBottom: 12 }}>
                    <Space size={6} wrap>
                      {doc.tags.slice(0, 5).map((tag) => (
                        <Tag
                          key={tag.id}
                          color={tag.tagColor || 'blue'}
                          style={{
                            fontSize: 12,
                            padding: '2px 8px',
                            margin: 0,
                            cursor: 'pointer',
                          }}
                          onClick={(e) => {
                            e.stopPropagation();
                            // TODO: 点击标签筛选同标签文档
                            console.log('Filter by tag:', tag.tagName);
                          }}
                        >
                          {tag.tagName}
                        </Tag>
                      ))}
                      {doc.tags.length > 5 && (
                        <Tag
                          style={{
                            fontSize: 12,
                            padding: '2px 8px',
                            margin: 0,
                            background: '#f0f0f0',
                            border: '1px solid #d9d9d9',
                          }}
                        >
                          +{doc.tags.length - 5}
                        </Tag>
                      )}
                    </Space>
                  </div>
                )}

                <Row gutter={[16, 8]} className="document-list-meta">
                  <Col>
                    <Space size={4}>
                      <Avatar size={20} icon={<UserOutlined />} />
                      <Text type="secondary" style={{ fontSize: 13 }}>
                        {doc.createByName || '未知'}
                      </Text>
                    </Space>
                  </Col>
                  <Col>
                    <Text type="secondary" style={{ fontSize: 13 }}>
                      创建于 {doc.createTime ? dayjs(doc.createTime).format('YYYY-MM-DD HH:mm') : '未知'}
                    </Text>
                  </Col>
                  <Col>
                    <Text type="secondary" style={{ fontSize: 13 }}>
                      更新于 {formatTime(doc.updateTime)}
                    </Text>
                  </Col>
                </Row>

                <div className="document-list-stats">
                  <Space size={16}>
                    <Tooltip title="浏览数">
                      <span>
                        <EyeOutlined /> {doc.viewCount || 0}
                      </span>
                    </Tooltip>
                    <Tooltip title="点赞数">
                      <span>
                        <LikeOutlined /> {doc.likeCount || 0}
                      </span>
                    </Tooltip>
                    <Tooltip title="收藏数">
                      <span>
                        <StarOutlined /> {doc.collectCount || 0}
                      </span>
                    </Tooltip>
                    {doc.commentCount && doc.commentCount > 0 && (
                      <Tooltip title="评论数">
                        <span>
                          <CommentOutlined /> {doc.commentCount}
                        </span>
                      </Tooltip>
                    )}
                    {doc.wordCount && doc.wordCount > 0 && (
                      <Tooltip title="字数">
                        <span>{doc.wordCount} 字</span>
                      </Tooltip>
                    )}
                    {doc.readDuration && doc.readDuration > 0 && (
                      <Tooltip title="预计阅读时长">
                        <span>
                          <ClockCircleOutlined /> {doc.readDuration}分钟
                        </span>
                      </Tooltip>
                    )}
                  </Space>
                </div>
              </div>
            }
          />
        </List.Item>
      )}
    />
  );

  return (
    <div className="directory-view-container">
      <Card
        title={
          <Space>
            <FolderOpenFilled style={{ color: '#1890ff', fontSize: 20 }} />
            <span style={{ fontSize: 18, fontWeight: 600 }}>{directory.directoryName}</span>
          </Space>
        }
        extra={
          <Space size="middle">
            <Segmented
              value={viewMode}
              onChange={(value) => setViewMode(value as ViewMode)}
              options={[
                {
                  label: '卡片',
                  value: 'card',
                  icon: <AppstoreOutlined />,
                },
                {
                  label: '列表',
                  value: 'list',
                  icon: <UnorderedListOutlined />,
                },
              ]}
            />
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => onCreateDocument(directory.directoryId!)}
            >
              新建文档
            </Button>
          </Space>
        }
        bordered={false}
        className="directory-view-card"
      >
        {/* 搜索框 */}
        {documents.length > 0 && (
          <div className="directory-view-search">
            <Input.Search
              placeholder="搜索文档标题、摘要、作者..."
              allowClear
              enterButton={<SearchOutlined />}
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
              onSearch={(value) => setSearchKeyword(value)}
            />
          </div>
        )}

        {/* 文档列表 */}
        {documents.length > 0 ? (
          filteredDocuments.length > 0 ? (
            viewMode === 'card' ? (
              renderCardView()
            ) : (
              renderListView()
            )
          ) : (
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={`未找到包含"${searchKeyword}"的文档`}
              className="document-empty-state"
              style={{ padding: '60px 0' }}
            >
              <Button onClick={() => setSearchKeyword('')}>清空搜索</Button>
            </Empty>
          )
        ) : (
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description="暂无文档"
            className="document-empty-state"
            style={{ padding: '60px 0' }}
          >
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={() => onCreateDocument(directory.directoryId!)}
            >
              创建第一个文档
            </Button>
          </Empty>
        )}
      </Card>
    </div>
  );
};

export default DirectoryView;
