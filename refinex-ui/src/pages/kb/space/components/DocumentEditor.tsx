/**
 * 文档编辑器组件
 * 集成 MDXEditor 实现 Markdown 文档的所见即所得编辑
 */
import React, { useRef, useState, useEffect } from 'react';
import { Card, Button, Space, message, Input, Spin, Empty, Tooltip } from 'antd';
import {
  SaveOutlined,
  EyeOutlined,
  FileMarkdownOutlined,
  ClockCircleOutlined,
} from '@ant-design/icons';
import type { MDXEditorMethods } from '@mdxeditor/editor';
import MDXEditorWrapper from '@/components/MDXEditor';
import type { ContentDirectory } from '@/services/kb/typings.d';
import './DocumentEditor.less';

interface DocumentEditorProps {
  directory: ContentDirectory | null;
  spaceId: number;
}

/**
 * 文档编辑器
 * 显示在目录树的右侧，用于编辑和预览 Markdown 文档
 */
const DocumentEditor: React.FC<DocumentEditorProps> = ({ directory, spaceId }) => {
  const editorRef = useRef<MDXEditorMethods>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [documentTitle, setDocumentTitle] = useState('');
  const [lastSaveTime, setLastSaveTime] = useState<Date | null>(null);

  // 临时的 markdown 内容（后续会从后端 API 获取）
  const [markdown, setMarkdown] = useState('');

  useEffect(() => {
    if (directory) {
      loadDocument();
    } else {
      // 清空编辑器
      setDocumentTitle('');
      setMarkdown('');
    }
  }, [directory]);

  // 加载文档内容
  const loadDocument = async () => {
    if (!directory) return;

    setLoading(true);
    try {
      // TODO: 调用后端 API 获取文档内容
      // const response = await getDocument(directory.id);

      // 临时模拟数据
      setDocumentTitle(directory.directoryName);
      setMarkdown(`# ${directory.directoryName}\n\n这是一个示例文档。\n\n## 功能特性\n\n- 支持 Markdown 语法\n- 所见即所得编辑\n- 实时预览\n\n\`\`\`javascript\nconsole.log('Hello, MDXEditor!');\n\`\`\`\n\n> 这是一个引用块\n\n---\n\n**粗体文本** 和 *斜体文本*`);

      message.success('文档加载成功');
    } catch (error) {
      console.error('加载文档失败:', error);
      message.error('加载文档失败');
    } finally {
      setLoading(false);
    }
  };

  // 保存文档
  const handleSave = async () => {
    if (!directory) {
      message.warning('请先选择一个目录');
      return;
    }

    setSaving(true);
    try {
      const content = editorRef.current?.getMarkdown() || '';

      // TODO: 调用后端 API 保存文档
      // await saveDocument({
      //   directoryId: directory.id,
      //   title: documentTitle,
      //   content,
      // });

      console.log('保存文档:', {
        directoryId: directory.id,
        title: documentTitle,
        content,
      });

      setLastSaveTime(new Date());
      message.success('文档保存成功');
    } catch (error) {
      console.error('保存文档失败:', error);
      message.error('保存文档失败');
    } finally {
      setSaving(false);
    }
  };

  // 编辑器内容变化
  const handleEditorChange = (newMarkdown: string) => {
    setMarkdown(newMarkdown);
  };

  // 如果没有选择目录
  if (!directory) {
    return (
      <div className="document-editor-container">
        <Card className="editor-card">
          <Empty
            image={Empty.PRESENTED_IMAGE_SIMPLE}
            description={
              <div>
                <FileMarkdownOutlined style={{ fontSize: 48, color: '#d9d9d9', marginBottom: 16 }} />
                <p style={{ fontSize: 16, color: '#8c8c8c' }}>请从左侧目录树选择一个目录</p>
                <p style={{ fontSize: 14, color: '#bfbfbf' }}>选择后可以查看和编辑该目录下的文档</p>
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
            <Input
              value={documentTitle}
              onChange={(e) => setDocumentTitle(e.target.value)}
              placeholder="请输入文档标题"
              bordered={false}
              className="document-title-input"
              style={{ fontSize: 18, fontWeight: 600 }}
            />
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
            <Button
              type="primary"
              icon={<SaveOutlined />}
              onClick={handleSave}
              loading={saving}
            >
              保存
            </Button>
          </Space>
        }
      >
        <Spin spinning={loading} tip="加载文档中...">
          <div className="editor-content">
            {!loading && (
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

