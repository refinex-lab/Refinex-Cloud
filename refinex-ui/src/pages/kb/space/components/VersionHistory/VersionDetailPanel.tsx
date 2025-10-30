/**
 * 版本详情预览面板（重构版）
 * 右侧分栏展示，提供清晰的版本内容预览
 */

import React, { useEffect, useState } from 'react';
import {
  App,
  Button,
  Divider,
  Space,
  Spin,
  Tag,
  Typography,
  Tooltip,
} from 'antd';
import {
  ClockCircleOutlined,
  CloseOutlined,
  DownloadOutlined,
  FileTextOutlined,
  RollbackOutlined,
  UserOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { MarkdownViewer } from '@/components';
import type { ContentDocumentVersionDetail } from '@/services/kb/typings.d';
import { getVersionDetail, restoreVersion } from '@/services/kb/version';
import './VersionDetailPanel.less';

const { Title, Paragraph } = Typography;

interface VersionDetailPanelProps {
  documentId: number;
  versionNumber: number;
  onClose: () => void;
  onVersionRestore?: (newVersionNumber: number) => void;
}

const VersionDetailPanel: React.FC<VersionDetailPanelProps> = ({
  documentId,
  versionNumber,
  onClose,
  onVersionRestore,
}) => {
  const { message, modal } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [restoring, setRestoring] = useState(false);
  const [detail, setDetail] = useState<ContentDocumentVersionDetail | null>(null);

  // 加载版本详情
  const loadDetail = async () => {
    setLoading(true);
    try {
      const response = await getVersionDetail(documentId, versionNumber);
      if (response.success && response.data) {
        setDetail(response.data);
      }
    } catch (error) {
      message.error('加载版本详情失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (documentId && versionNumber) {
      loadDetail();
    }
  }, [documentId, versionNumber]);

  // 恢复版本
  const handleRestore = () => {
    if (!detail) return;

    modal.confirm({
      title: '确认恢复版本',
      content: (
        <div>
          <p>
            确定要恢复到 <strong>版本 #{versionNumber}</strong> 吗？
          </p>
          <p style={{ color: '#8c8c8c', fontSize: 13 }}>
            恢复后会创建一个新版本，不会覆盖历史记录
          </p>
        </div>
      ),
      onOk: async () => {
        setRestoring(true);
        try {
          const response = await restoreVersion(documentId, versionNumber);
          if (response.success && response.data) {
            message.success(response.data.message || `已恢复到版本 #${versionNumber}`);
            onVersionRestore?.(response.data.newVersionNumber);
            onClose();
          }
        } catch (error) {
          message.error('恢复版本失败');
        } finally {
          setRestoring(false);
        }
      },
    });
  };

  // 导出为 Markdown
  const handleExport = () => {
    if (!detail) return;

    const content = `# ${detail.documentTitle}\n\n> 版本 #${detail.versionNumber} - ${dayjs(
      detail.createTime,
    ).format('YYYY-MM-DD HH:mm:ss')}\n> 作者: ${detail.createdByName}\n\n${detail.contentBody}`;

    const blob = new Blob([content], { type: 'text/markdown;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${detail.documentTitle}-v${detail.versionNumber}.md`;
    link.click();
    URL.revokeObjectURL(url);

    message.success('导出成功');
  };

  return (
    <div className="version-detail-panel">
      <Spin spinning={loading}>
        {detail ? (
          <>
            {/* 顶部工具栏 */}
            <div className="detail-panel-header">
              <div className="header-info">
                <Space align="start">
                  <FileTextOutlined style={{ fontSize: 20, color: '#1890ff' }} />
                  <div>
                    <Title level={5} style={{ margin: 0 }}>
                      版本 #{detail.versionNumber}
                      {detail.isCurrent && (
                        <Tag color="blue" style={{ marginLeft: 8 }}>
                          当前版本
                        </Tag>
                      )}
                    </Title>
                    <Paragraph
                      type="secondary"
                      style={{ margin: 0, fontSize: 12, marginTop: 4 }}
                    >
                      <UserOutlined /> {detail.createdByName} •{' '}
                      <ClockCircleOutlined />{' '}
                      {dayjs(detail.createTime).format('YYYY-MM-DD HH:mm:ss')}
                    </Paragraph>
                  </div>
                </Space>
              </div>
              <div className="header-actions">
                <Space>
                  {!detail.isCurrent && (
                    <Tooltip title="恢复到此版本会创建一个新版本">
                      <Button
                        type="primary"
                        icon={<RollbackOutlined />}
                        loading={restoring}
                        onClick={handleRestore}
                      >
                        恢复
                      </Button>
                    </Tooltip>
                  )}
                  <Tooltip title="导出为 Markdown 文件">
                    <Button icon={<DownloadOutlined />} onClick={handleExport}>
                      导出
                    </Button>
                  </Tooltip>
                  <Button type="text" icon={<CloseOutlined />} onClick={onClose} />
                </Space>
              </div>
            </div>

            <Divider style={{ margin: 0 }} />

            {/* 内容预览区域 */}
            <div className="detail-panel-body">
              <MarkdownViewer
                content={detail.contentBody}
                enableHighlight={true}
                allowHtml={false}
                emptyText="此版本内容为空"
              />
            </div>
          </>
        ) : (
          <div style={{ padding: 60, textAlign: 'center', color: '#8c8c8c' }}>
            <FileTextOutlined style={{ fontSize: 48, marginBottom: 16, opacity: 0.3 }} />
            <div>暂无数据</div>
          </div>
        )}
      </Spin>
    </div>
  );
};

export default VersionDetailPanel;

