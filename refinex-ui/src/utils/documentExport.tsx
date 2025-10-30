/**
 * 文档导出工具
 * 支持 Markdown 和 PDF 格式导出
 */

import { Document, Page, Text, View, StyleSheet, Font, pdf } from '@react-pdf/renderer';
import type { ContentDocumentDetail } from '@/services/kb/typings.d';

// 注册中文字体（使用 CDN 字体）
// 使用 jsdelivr CDN 提供的 Noto Sans SC 字体
Font.register({
  family: 'Noto Sans SC',
  fonts: [
    {
      src: 'https://cdn.jsdelivr.net/npm/@fontsource/noto-sans-sc@5.0.18/files/noto-sans-sc-chinese-simplified-400-normal.woff',
      fontWeight: 'normal',
    },
    {
      src: 'https://cdn.jsdelivr.net/npm/@fontsource/noto-sans-sc@5.0.18/files/noto-sans-sc-chinese-simplified-700-normal.woff',
      fontWeight: 'bold',
    },
  ],
});

// PDF 样式定义
const pdfStyles = StyleSheet.create({
  page: {
    padding: 40,
    fontSize: 12,
    fontFamily: 'Noto Sans SC',
    lineHeight: 1.6,
    backgroundColor: '#ffffff',
  },
  header: {
    marginBottom: 20,
    borderBottom: '2px solid #1890ff',
    paddingBottom: 10,
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#262626',
  },
  metadata: {
    fontSize: 10,
    color: '#8c8c8c',
    marginBottom: 5,
  },
  summary: {
    fontSize: 11,
    color: '#595959',
    marginTop: 10,
    padding: 10,
    backgroundColor: '#f5f5f5',
    borderRadius: 4,
  },
  content: {
    marginTop: 20,
    fontSize: 12,
    color: '#262626',
    lineHeight: 1.8,
  },
  paragraph: {
    marginBottom: 12,
    textAlign: 'justify',
  },
  heading: {
    fontSize: 16,
    fontWeight: 'bold',
    marginTop: 16,
    marginBottom: 8,
    color: '#262626',
  },
  code: {
    fontFamily: 'Courier',
    fontSize: 10,
    backgroundColor: '#f5f5f5',
    padding: 10,
    borderRadius: 4,
    marginVertical: 8,
  },
  blockquote: {
    borderLeft: '4px solid #1890ff',
    paddingLeft: 12,
    marginVertical: 8,
    color: '#595959',
    fontStyle: 'italic',
  },
  list: {
    marginLeft: 20,
    marginBottom: 10,
  },
  listItem: {
    marginBottom: 4,
  },
  footer: {
    position: 'absolute',
    bottom: 30,
    left: 40,
    right: 40,
    textAlign: 'center',
    fontSize: 10,
    color: '#8c8c8c',
    borderTop: '1px solid #d9d9d9',
    paddingTop: 10,
  },
});

/**
 * PDF 文档组件
 */
interface PDFDocumentProps {
  document: ContentDocumentDetail;
}

const PDFDocument: React.FC<PDFDocumentProps> = ({ document }) => {
  // 简单的 Markdown 解析（将 Markdown 转换为可渲染的文本）
  const parseMarkdownContent = (markdown: string) => {
    if (!markdown) return [];

    const lines = markdown.split('\n');
    const elements: React.ReactElement[] = [];

    lines.forEach((line, index) => {
      // 标题
      if (line.startsWith('# ')) {
        elements.push(
          <Text key={`h1-${index}`} style={{ ...pdfStyles.heading, fontSize: 20 }}>
            {line.replace(/^#\s+/, '')}
          </Text>,
        );
      } else if (line.startsWith('## ')) {
        elements.push(
          <Text key={`h2-${index}`} style={{ ...pdfStyles.heading, fontSize: 18 }}>
            {line.replace(/^##\s+/, '')}
          </Text>,
        );
      } else if (line.startsWith('### ')) {
        elements.push(
          <Text key={`h3-${index}`} style={{ ...pdfStyles.heading, fontSize: 16 }}>
            {line.replace(/^###\s+/, '')}
          </Text>,
        );
      }
      // 代码块
      else if (line.startsWith('```')) {
        // 简化处理：跳过代码块标记
        return;
      }
      // 引用
      else if (line.startsWith('> ')) {
        elements.push(
          <Text key={`quote-${index}`} style={pdfStyles.blockquote}>
            {line.replace(/^>\s+/, '')}
          </Text>,
        );
      }
      // 列表
      else if (line.match(/^[-*]\s+/)) {
        elements.push(
          <Text key={`list-${index}`} style={pdfStyles.listItem}>
            • {line.replace(/^[-*]\s+/, '')}
          </Text>,
        );
      }
      // 普通段落
      else if (line.trim()) {
        elements.push(
          <Text key={`p-${index}`} style={pdfStyles.paragraph}>
            {line}
          </Text>,
        );
      }
    });

    return elements;
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <Document>
      <Page size="A4" style={pdfStyles.page}>
        {/* 文档头部 */}
        <View style={pdfStyles.header}>
          <Text style={pdfStyles.title}>{document.docTitle}</Text>
          <Text style={pdfStyles.metadata}>
            作者: {document.createByName || '未知'} | 创建时间:{' '}
            {formatDate(document.createTime)} | 更新时间: {formatDate(document.updateTime)}
          </Text>
          <Text style={pdfStyles.metadata}>
            字数: {document.wordCount || 0} 字 | 阅读时长: {document.readDuration || 0} 分钟 |
            版本: v{document.versionNumber || 1}
          </Text>
          {document.docSummary && (
            <Text style={pdfStyles.summary}>摘要: {document.docSummary}</Text>
          )}
        </View>

        {/* 文档内容 */}
        <View style={pdfStyles.content}>
          {parseMarkdownContent(document.contentBody || '')}
        </View>

        {/* 页脚 */}
        <Text
          style={pdfStyles.footer}
          render={({ pageNumber, totalPages }) =>
            `第 ${pageNumber} 页 / 共 ${totalPages} 页 - 由 Refinex-Cloud 知识库导出`
          }
          fixed
        />
      </Page>
    </Document>
  );
};

/**
 * 导出为 Markdown 文件
 */
export const exportToMarkdown = (documentData: ContentDocumentDetail) => {
  try {
    // 构建 Markdown 内容（包含元数据）
    const metadata = `---
title: ${documentData.docTitle}
author: ${documentData.createByName || '未知'}
created: ${documentData.createTime}
updated: ${documentData.updateTime}
version: ${documentData.versionNumber || 1}
words: ${documentData.wordCount || 0}
---

${documentData.docSummary ? `> ${documentData.docSummary}\n\n` : ''}`;

    const fullContent = metadata + (documentData.contentBody || '');

    // 创建 Blob
    const blob = new Blob([fullContent], { type: 'text/markdown;charset=utf-8' });

    // 创建下载链接
    const url = URL.createObjectURL(blob);
    const link = window.document.createElement('a');
    link.href = url;
    link.download = `${sanitizeFileName(documentData.docTitle)}.md`;
    window.document.body.appendChild(link);
    link.click();
    window.document.body.removeChild(link);

    // 释放 URL 对象
    URL.revokeObjectURL(url);

    return true;
  } catch (error) {
    console.error('导出 Markdown 失败:', error);
    return false;
  }
};

/**
 * 导出为 PDF 文件
 */
export const exportToPDF = async (documentData: ContentDocumentDetail) => {
  try {
    // 生成 PDF
    const blob = await pdf(<PDFDocument document={documentData} />).toBlob();

    // 创建下载链接
    const url = URL.createObjectURL(blob);
    const link = window.document.createElement('a');
    link.href = url;
    link.download = `${sanitizeFileName(documentData.docTitle)}.pdf`;
    window.document.body.appendChild(link);
    link.click();
    window.document.body.removeChild(link);

    // 释放 URL 对象
    URL.revokeObjectURL(url);

    return true;
  } catch (error) {
    console.error('导出 PDF 失败:', error);
    return false;
  }
};

/**
 * 清理文件名（移除非法字符）
 */
const sanitizeFileName = (fileName: string): string => {
  return fileName
    .replace(/[<>:"/\\|?*\x00-\x1F]/g, '') // 移除非法字符
    .replace(/\s+/g, '_') // 空格替换为下划线
    .substring(0, 200); // 限制长度
};

/**
 * 获取支持的导出格式
 */
export const getSupportedExportFormats = () => {
  return [
    {
      key: 'markdown',
      label: 'Markdown (.md)',
      icon: '📝',
      description: '原始 Markdown 格式，保留所有格式和元数据',
    },
    {
      key: 'pdf',
      label: 'PDF (.pdf)',
      icon: '📄',
      description: '便携式文档格式，适合打印和分享',
    },
  ];
};

