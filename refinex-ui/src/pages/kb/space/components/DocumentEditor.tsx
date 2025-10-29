/**
 * 文档编辑器组件
 * 集成 MDXEditor 实现 Markdown 文档的所见即所得编辑
 * 深度集成后端 API，支持真实的文档加载、编辑、保存
 */
import React, { useRef, useState, useEffect } from 'react';
import { Card, Button, Space, message, Input, Spin, Empty, Tooltip, Tag } from 'antd';
import {
  SaveOutlined,
  CloseOutlined,
  FileMarkdownOutlined,
  ClockCircleOutlined,
  CheckOutlined,
  StopOutlined,
} from '@ant-design/icons';
import type { MDXEditorMethods } from '@mdxeditor/editor';
import MDXEditorWrapper from '@/components/MDXEditor';
import type { ContentDocumentDetail } from '@/services/kb/typings.d';
import { DocumentStatus } from '@/services/kb/typings.d';
import {
  getDocumentByGuid,
  saveDocumentContent,
  updateDocument,
  publishDocument,
  offlineDocument,
} from '@/services/kb/document';
import './DocumentEditor.less';

interface DocumentEditorProps {
  docGuid?: string; // 文档 GUID
  spaceId: number;
  onClose?: () => void; // 关闭编辑器回调
  onTitleChange?: (newTitle: string) => void; // 标题变更回调
}

/**
 * 文档编辑器
 * 显示在目录树的右侧，用于编辑和预览 Markdown 文档
 */
const DocumentEditor: React.FC<DocumentEditorProps> = ({
  docGuid,
  spaceId,
  onClose,
  onTitleChange,
}) => {
  const editorRef = useRef<MDXEditorMethods>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [document, setDocument] = useState<ContentDocumentDetail | null>(null);
  const [documentTitle, setDocumentTitle] = useState('');
  const [markdown, setMarkdown] = useState('');
  const [lastSaveTime, setLastSaveTime] = useState<Date | null>(null);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);
  const [autoSaveTimer, setAutoSaveTimer] = useState<NodeJS.Timeout | null>(null);

  // 加载文档
  const loadDocument = async () => {
    if (!docGuid) return;

    setLoading(true);
    try {
      const response = await getDocumentByGuid(docGuid);
      if (response.success && response.data) {
        setDocument(response.data);
        setDocumentTitle(response.data.docTitle);
        setMarkdown(response.data.contentBody || '');
        setHasUnsavedChanges(false);
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
      // 清空编辑器
      setDocument(null);
      setDocumentTitle('');
      setMarkdown('');
      setHasUnsavedChanges(false);
    }

    // 清理定时器
    return () => {
      if (autoSaveTimer) {
        clearTimeout(autoSaveTimer);
      }
    };
  }, [docGuid]);

  // 保存文档内容
  const handleSave = async (isAutoSave = false) => {
    if (!document?.id) {
      message.warning('文档信息异常');
      return;
    }

    setSaving(true);
    try {
      const content = editorRef.current?.getMarkdown() || '';

      const response = await saveDocumentContent(document.id, {
        contentBody: content,
        changeRemark: isAutoSave ? '自动保存' : undefined,
      });

      if (response.success) {
        setLastSaveTime(new Date());
        setHasUnsavedChanges(false);
        if (!isAutoSave) {
          message.success('保存成功');
        }
      }
    } catch (error) {
      console.error('保存文档失败:', error);
      if (!isAutoSave) {
        message.error('保存失败');
      }
    } finally {
      setSaving(false);
    }
  };

  // 自动保存（编辑后 3 秒）
  useEffect(() => {
    if (hasUnsavedChanges && document?.id) {
      if (autoSaveTimer) {
        clearTimeout(autoSaveTimer);
      }
      const timer = setTimeout(() => {
        handleSave(true);
      }, 3000);
      setAutoSaveTimer(timer);
    }

    return () => {
      if (autoSaveTimer) {
        clearTimeout(autoSaveTimer);
      }
    };
  }, [hasUnsavedChanges, markdown]);

  // 编辑器内容变化
  const handleEditorChange = (newMarkdown: string) => {
    setMarkdown(newMarkdown);
    setHasUnsavedChanges(true);
  };

  // 标题编辑（失焦保存）
  const handleTitleBlur = async () => {
    if (!document || documentTitle === document.docTitle) return;

    if (!documentTitle.trim()) {
      message.warning('文档标题不能为空');
      setDocumentTitle(document.docTitle); // 恢复原标题
      return;
    }

    try {
      await updateDocument(document.id, {
        docTitle: documentTitle,
        version: document.version,
      });
      onTitleChange?.(documentTitle);
      message.success('标题已更新');
      // 更新本地文档对象
      setDocument({ ...document, docTitle: documentTitle });
    } catch (error) {
      console.error('更新标题失败:', error);
      message.error('更新标题失败');
      setDocumentTitle(document.docTitle); // 恢复原标题
    }
  };

  // 发布文档
  const handlePublish = async () => {
    if (!document?.id) return;

    // 如果有未保存的更改，先保存
    if (hasUnsavedChanges) {
      await handleSave(false);
    }

    try {
      await publishDocument(document.id);
      message.success('文档已发布');
      // 刷新文档信息
      loadDocument();
    } catch (error) {
      console.error('发布文档失败:', error);
      message.error('发布文档失败');
    }
  };

  // 下架文档
  const handleOffline = async () => {
    if (!document?.id) return;

    try {
      await offlineDocument(document.id);
      message.success('文档已下架');
      // 刷新文档信息
      loadDocument();
    } catch (error) {
      console.error('下架文档失败:', error);
      message.error('下架文档失败');
    }
  };

  // 如果没有选择文档
  if (!docGuid) {
    return (
      <div className="document-editor-container">
        <Card className="editor-card">
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
        </Card>
      </div>
    );
  }

  return (
    <div className="document-editor-container">
      <Card
        className="editor-card"
        title={
          <div className="editor-header">
            <Space size="middle">
              <Input
                value={documentTitle}
                onChange={(e) => setDocumentTitle(e.target.value)}
                onBlur={handleTitleBlur}
                onPressEnter={handleTitleBlur}
                placeholder="请输入文档标题"
                bordered={false}
                className="document-title-input"
                style={{ fontSize: 18, fontWeight: 600, width: 300 }}
                disabled={loading}
              />
              {/*{document?.docStatus === DocumentStatus.DRAFT && (*/}
              {/*  <Tag color="default">草稿</Tag>*/}
              {/*)}*/}
              {/*{document?.docStatus === DocumentStatus.PUBLISHED && (*/}
              {/*  <Tag color="success">已发布</Tag>*/}
              {/*)}*/}
              {/*{document?.docStatus === DocumentStatus.OFFLINE && (*/}
              {/*  <Tag color="error">已下架</Tag>*/}
              {/*)}*/}
              {/*{hasUnsavedChanges && (*/}
              {/*  <Tag color="warning">未保存</Tag>*/}
              {/*)}*/}
            </Space>
          </div>
        }
        extra={
          <Space size="middle">
            {lastSaveTime && (
              <Tooltip title={`上次保存: ${lastSaveTime.toLocaleString('zh-CN')}`}>
                <span style={{ fontSize: 12, color: '#8c8c8c' }}>
                  <ClockCircleOutlined /> {lastSaveTime.toLocaleTimeString('zh-CN')}
                </span>
              </Tooltip>
            )}
            <Button onClick={onClose} icon={<CloseOutlined />}>
              关闭
            </Button>
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={() => handleSave(false)}
              loading={saving}
              disabled={loading}
            >
              保存
            </Button>
            {document?.docStatus === DocumentStatus.DRAFT && (
              <Button
                type="primary"
                icon={<CheckOutlined />}
                onClick={handlePublish}
                disabled={loading || saving}
                style={{ backgroundColor: '#52c41a', borderColor: '#52c41a' }}
              >
                发布
              </Button>
            )}
            {document?.docStatus === DocumentStatus.PUBLISHED && (
              <Button
                danger
                icon={<StopOutlined />}
                onClick={handleOffline}
                disabled={loading || saving}
              >
                下架
              </Button>
            )}
          </Space>
        }
        bordered={false}
      >
        <Spin spinning={loading} tip="加载文档中...">
          <div className="editor-content">
            {!loading && document && (
              <MDXEditorWrapper
                ref={editorRef}
                markdown={markdown}
                onChange={handleEditorChange}
                className="mdx-editor"
              />
            )}
          </div>
        </Spin>
      </Card>
    </div>
  );
};

export default DocumentEditor;
