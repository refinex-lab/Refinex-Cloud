/**
 * 版本对比弹窗（重构版 - GitHub 风格）
 * 全屏模式，提供专业的版本差异对比体验
 * 使用 GitHub 风格的 unified diff 展示
 */
import React, { useEffect, useState } from 'react';
import { App, Button, Modal, Space, Tag } from 'antd';
import {
  CloseOutlined,
  SwapOutlined,
  FileTextOutlined,
  SyncOutlined,
  CodeOutlined,
  FullscreenOutlined,
  FullscreenExitOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import type { VersionCompareResult } from '@/services/kb/typings.d';
import { compareVersions } from '@/services/kb/version';
import UnifiedDiffViewer from '@/components/UnifiedDiffViewer';
import './VersionCompareModal.less';

interface VersionCompareModalProps {
  visible: boolean;
  documentId: number;
  fromVersion: number;
  toVersion: number;
  onClose: () => void;
}

const VersionCompareModal: React.FC<VersionCompareModalProps> = ({
  visible,
  documentId,
  fromVersion,
  toVersion,
  onClose,
}) => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [compareResult, setCompareResult] = useState<VersionCompareResult | null>(null);
  const [swapped, setSwapped] = useState(false);
  const [isFullscreen, setIsFullscreen] = useState(false);

  // 加载对比数据
  const loadCompareData = async () => {
    setLoading(true);
    setSwapped(false);
    try {
      const response = await compareVersions(documentId, fromVersion, toVersion);
      if (response.success && response.data) {
        setCompareResult(response.data);
      } else {
        message.error(response.message || '加载对比数据失败');
      }
    } catch (error) {
      console.error('版本对比加载失败:', error);
      message.error('加载对比数据失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (visible && documentId && fromVersion && toVersion) {
      loadCompareData();
    }
    // 清空数据当弹窗关闭时
    if (!visible) {
      setCompareResult(null);
      setSwapped(false);
    }
  }, [visible, documentId, fromVersion, toVersion]);

  // 交换版本（重新加载数据）
  const handleSwap = async () => {
    if (!compareResult) return;

    setLoading(true);
    try {
      const response = await compareVersions(
        documentId,
        compareResult.toVersion,
        compareResult.fromVersion,
      );
      if (response.success && response.data) {
        setCompareResult(response.data);
        setSwapped(!swapped);
        message.success('版本已交换');
      }
    } catch (error) {
      message.error('交换版本失败');
    } finally {
      setLoading(false);
    }
  };

  // 切换全屏
  const toggleFullscreen = () => {
    setIsFullscreen(!isFullscreen);
  };

  return (
    <Modal
      title={
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Space size="large">
            <Space>
              <CodeOutlined style={{ fontSize: 20, color: '#1890ff' }} />
              <span style={{ fontSize: 18, fontWeight: 600 }}>版本对比</span>
            </Space>
            {compareResult && (
              <Space size="middle">
                <Space direction="vertical" size={0} style={{ textAlign: 'center' }}>
                  <Tag color="red" style={{ fontSize: 13, fontWeight: 600 }}>
                    版本 #{compareResult.fromVersion}
                  </Tag>
                  <span style={{ fontSize: 12, color: '#8c8c8c' }}>
                    {dayjs(compareResult.fromVersionTime).format('MM-DD HH:mm')}
                  </span>
                </Space>

                <SwapOutlined
                  style={{
                    fontSize: 14,
                    color: '#8c8c8c',
                  }}
                />

                <Space direction="vertical" size={0} style={{ textAlign: 'center' }}>
                  <Tag color="green" style={{ fontSize: 13, fontWeight: 600 }}>
                    版本 #{compareResult.toVersion}
                  </Tag>
                  <span style={{ fontSize: 12, color: '#8c8c8c' }}>
                    {dayjs(compareResult.toVersionTime).format('MM-DD HH:mm')}
                  </span>
                </Space>
              </Space>
            )}
          </Space>

          {compareResult?.summary && (
            <Space size="large">
              <Space size="small">
                <Tag color="success" style={{ margin: 0 }}>
                  +{compareResult.summary.insertions}
                </Tag>
                <Tag color="error" style={{ margin: 0 }}>
                  -{compareResult.summary.deletions}
                </Tag>
                <Tag color="processing" style={{ margin: 0 }}>
                  {compareResult.summary.changes} 处修改
                </Tag>
              </Space>
            </Space>
          )}
        </div>
      }
      open={visible}
      onCancel={onClose}
      width={isFullscreen ? '100vw' : '90vw'}
      style={
        isFullscreen
          ? { top: 0, maxWidth: '100vw', paddingBottom: 0 }
          : { top: '5vh', paddingBottom: 0 }
      }
      styles={
        isFullscreen
          ? {
              body: { height: 'calc(100vh - 195px)', overflow: 'hidden' },
            }
          : {
              body: { height: '70vh', maxHeight: '800px', overflow: 'hidden' },
            }
      }
      footer={
        <div className="compare-modal-footer">
          <Space>
            <Button
              icon={<SwapOutlined />}
              onClick={handleSwap}
              loading={loading}
              disabled={!compareResult}
            >
              交换版本
            </Button>
            <Button
              icon={<SyncOutlined />}
              onClick={loadCompareData}
              loading={loading}
              disabled={!compareResult}
            >
              刷新
            </Button>
          </Space>
          <Space>
            <Button
              icon={isFullscreen ? <FullscreenExitOutlined /> : <FullscreenOutlined />}
              onClick={toggleFullscreen}
            >
              {isFullscreen ? '退出全屏' : '全屏'}
            </Button>
            <Button type="primary" icon={<CloseOutlined />} onClick={onClose}>
              关闭
            </Button>
          </Space>
        </div>
      }
      className="version-compare-modal"
      destroyOnHidden
    >
      <div className="compare-content" style={{ height: '100%', overflow: 'auto' }}>
        {compareResult ? (
          <UnifiedDiffViewer
            unifiedDiff={compareResult.unifiedDiff || ''}
            showHeader={false}
            emptyText="两个版本内容完全相同，没有差异"
          />
        ) : (
          !loading && (
            <div style={{ padding: 60, textAlign: 'center', color: '#8c8c8c' }}>
              <FileTextOutlined style={{ fontSize: 48, marginBottom: 16, opacity: 0.3 }} />
              <div>暂无对比数据</div>
            </div>
          )
        )}
      </div>
    </Modal>
  );
};

export default VersionCompareModal;
