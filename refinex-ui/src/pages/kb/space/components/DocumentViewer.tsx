/**
 * 文档查看器组件（阅读模式）
 * 使用 MarkdownViewer 渲染文档内容，只读模式
 * 提供编辑按钮切换到编辑模式
 */
import React, { useState, useEffect, useRef } from 'react';
import { Button, Space, message, Spin, Empty, Tag, Divider } from 'antd';
import {
  EditOutlined,
  CloseOutlined,
  FileMarkdownOutlined,
  CheckOutlined,
  StopOutlined,
  TagsOutlined,
  HistoryOutlined,
} from '@ant-design/icons';
import type { ContentDocumentDetail } from '@/services/kb/typings.d';
import { DocumentStatus } from '@/services/kb/typings.d';
import { getDocumentByGuid } from '@/services/kb/document';
import MarkdownViewer from '@/components/MarkdownViewer';
import VersionHistory from './VersionHistory';
import TableOfContents from './TableOfContents';
import './DocumentEditor.less';

interface DocumentViewerProps {
  docGuid?: string; // 文档 GUID
  spaceId: number;
  onClose?: () => void; // 关闭查看器回调
  onEdit?: () => void; // 切换到编辑模式回调
}

/**
 * 文档查看器（阅读模式）
 * 显示在目录树的右侧，用于查看 Markdown 文档
 */
const DocumentViewer: React.FC<DocumentViewerProps> = ({
  docGuid,
  spaceId,
  onClose,
  onEdit,
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const viewerContentRef = useRef<HTMLDivElement>(null);
  const editorBodyRef = useRef<HTMLDivElement>(null); // 真正的滚动容器

  const [loading, setLoading] = useState(false);
  const [document, setDocument] = useState<ContentDocumentDetail | null>(null);
  const [versionHistoryVisible, setVersionHistoryVisible] = useState(false);

  // 加载文档
  const loadDocument = async () => {
    if (!docGuid) return;

    setLoading(true);
    try {
      const response = await getDocumentByGuid(docGuid);
      if (response.success && response.data) {
        setDocument(response.data);
      }
    } catch (error) {
      console.error('加载文档失败:', error);
      message.error('加载文档失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (docGuid) {
      loadDocument();
    } else {
      // 清空文档
      setDocument(null);
    }
  }, [docGuid]);

  // 如果没有选择文档
  if (!docGuid) {
    return (
      <div className="document-editor-container" ref={containerRef}>
        <div className="editor-content-wrapper">
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={
              <div>
                <FileMarkdownOutlined style={{ fontSize: 48, color: '#d9d9d9', marginBottom: 16 }} />
                <p style={{ fontSize: 16, color: '#8c8c8c' }}>请从左侧目录树选择一个文档</p>
                <p style={{ fontSize: 14, color: '#bfbfbf' }}>
                  选择后可以查看和编辑文档内容
                </p>
              </div>
            }
          />
        </div>
      </div>
    );
  }

  return (
    <div className="document-editor-container" ref={containerRef}>
      {/* 独立的 Header 区域 */}
      <div className="editor-header">
        <div className="editor-header-left">
          <FileMarkdownOutlined style={{ fontSize: 24, marginRight: 12, color: '#1890ff' }} />
          <Space size="middle" split={<Divider type="vertical" />}>
            {/* 文档标题 */}
            <div style={{ fontSize: 18, fontWeight: 600 }}>
              {document?.docTitle || '加载中...'}
            </div>

            {/* 文档状态 */}
            {document && (
              <Tag
                color={document.docStatus === DocumentStatus.PUBLISHED ? 'success' : 'default'}
                icon={document.docStatus === DocumentStatus.PUBLISHED ? <CheckOutlined /> : undefined}
              >
                {document.docStatus === DocumentStatus.PUBLISHED ? '已发布' : '草稿'}
              </Tag>
            )}

            {/* 标签展示 */}
            {document?.tags && document.tags.length > 0 && (
              <div className="editor-tags">
                <TagsOutlined style={{ color: '#8c8c8c', marginRight: 8 }} />
                <Space size={8}>
                  {document.tags.map((tag) => (
                    <Tag key={tag.id} color={tag.tagColor || 'blue'}>
                      {tag.tagName}
                    </Tag>
                  ))}
                </Space>
              </div>
            )}
          </Space>
        </div>

        <div className="editor-header-right">
          <Space size="middle">
            {/* 阅读模式标识 */}
            {/* <Tag color="blue">
              阅读模式
            </Tag> */}

            {/* 版本历史按钮 */}
            <Button
              icon={<HistoryOutlined />}
              onClick={() => setVersionHistoryVisible(true)}
              disabled={!document}
            >
              版本历史
            </Button>

            <Button onClick={onClose} icon={<CloseOutlined />}>
              关闭
            </Button>

            {/* 编辑按钮 */}
            <Button
              type="primary"
              icon={<EditOutlined />}
              onClick={onEdit}
              disabled={loading}
            >
              编辑
            </Button>
          </Space>
        </div>
      </div>

      {/* 内容区域和大纲的容器 */}
      <div className="editor-body" ref={editorBodyRef}>
        {/* 独立的内容区域 */}
        <div className="editor-content-wrapper" ref={viewerContentRef}>
          <Spin spinning={loading} tip="加载文档中...">
            <div className="editor-content">
              {!loading && document && (
                <MarkdownViewer
                  content={document.contentBody || ''}
                  enableMermaid={true}
                  enableHighlight={true}
                  emptyText="该文档暂无内容"
                />
              )}
            </div>
          </Spin>
        </div>

        {/* 右侧大纲（目录） */}
        {!loading && document && document.contentBody && (
          <TableOfContents
            content={document.contentBody}
            containerRef={editorBodyRef as React.RefObject<HTMLElement>}
          />
        )}
      </div>

      {/* 版本历史抽屉 */}
      {document && (
        <VersionHistory
          visible={versionHistoryVisible}
          documentId={document.id}
          currentVersionNumber={document.versionNumber}
          onClose={() => setVersionHistoryVisible(false)}
          onVersionRestore={(newVersionNumber) => {
            message.success(`已恢复到历史版本，当前版本: v${newVersionNumber}`);
            loadDocument(); // 重新加载文档
          }}
        />
      )}
    </div>
  );
};

export default DocumentViewer;

