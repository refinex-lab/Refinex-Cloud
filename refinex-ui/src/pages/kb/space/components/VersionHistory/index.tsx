/**
 * 版本历史主组件（重构版）
 * 采用三栏布局：版本列表 | 详情面板 | 对比视图
 * 交互优化：清晰的视觉层级、流畅的展开/收起动画、智能的空间利用
 */

import React, { useEffect, useState } from 'react';
import {
  App,
  Button,
  Drawer,
  Empty,
  Input,
  Pagination,
  Space,
  Spin,
  Badge,
  Tooltip,
} from 'antd';
import {
  ClearOutlined,
  SwapOutlined,
  ReloadOutlined,
  SearchOutlined,
  HistoryOutlined,
  InfoCircleOutlined,
} from '@ant-design/icons';
import type { ContentDocumentVersion } from '@/services/kb/typings.d';
import { getVersionHistory } from '@/services/kb/version';
import VersionTimeline from './VersionTimeline';
import VersionDetailPanel from './VersionDetailPanel';
import VersionCompareModal from './VersionCompareModal';
import VersionCleanModal from './VersionCleanModal';
import './VersionHistory.less';

interface VersionHistoryProps {
  visible: boolean;
  documentId: number;
  currentVersionNumber: number;
  onClose: () => void;
  onVersionRestore?: (newVersionNumber: number) => void;
}

type ViewMode = 'list' | 'detail' | 'compare';

const VersionHistory: React.FC<VersionHistoryProps> = ({
  visible,
  documentId,
  currentVersionNumber,
  onClose,
  onVersionRestore,
}) => {
  const { message } = App.useApp();
  const [loading, setLoading] = useState(false);
  const [versions, setVersions] = useState<ContentDocumentVersion[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [searchKeyword, setSearchKeyword] = useState('');

  // 视图模式：列表 | 详情 | 对比
  const [viewMode, setViewMode] = useState<ViewMode>('list');
  const [selectedVersion, setSelectedVersion] = useState<number | null>(null);

  // 对比模式
  const [compareVersions, setCompareVersions] = useState<number[]>([]);
  const [compareModalVisible, setCompareModalVisible] = useState(false);

  // 清理模式
  const [cleanModalVisible, setCleanModalVisible] = useState(false);

  // 加载版本历史
  const loadVersions = async () => {
    setLoading(true);
    try {
      const response = await getVersionHistory(documentId, { pageNum, pageSize });
      if (response.success && response.data) {
        setVersions(response.data.records || []);
        setTotal(response.data.total || 0);
      }
    } catch (error) {
      message.error('加载版本历史失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (visible && documentId) {
      loadVersions();
      // 重置状态
      setViewMode('list');
      setSelectedVersion(null);
      setCompareVersions([]);
    }
  }, [visible, documentId, pageNum, pageSize]);

  // 搜索过滤
  const filteredVersions = versions.filter((version) => {
    if (!searchKeyword.trim()) return true;
    const keyword = searchKeyword.toLowerCase();
    const matchRemark = version.changeSummary?.toLowerCase().includes(keyword);
    const matchAuthor = version.createdByName?.toLowerCase().includes(keyword);
    return matchRemark || matchAuthor;
  });

  // 版本选择
  const handleVersionSelect = (versionNumber: number) => {
    if (viewMode === 'compare') {
      // 对比模式：多选
      if (compareVersions.includes(versionNumber)) {
        setCompareVersions(compareVersions.filter((v) => v !== versionNumber));
      } else if (compareVersions.length < 2) {
        setCompareVersions([...compareVersions, versionNumber]);
      } else {
        message.warning('最多选择 2 个版本进行对比');
      }
    } else {
      // 普通模式：单选，展开详情
      if (selectedVersion === versionNumber) {
        setViewMode('list');
        setSelectedVersion(null);
      } else {
        setViewMode('detail');
        setSelectedVersion(versionNumber);
      }
    }
  };

  // 进入对比模式
  const handleEnterCompareMode = () => {
    setViewMode('compare');
    setSelectedVersion(null);
    setCompareVersions([]);
  };

  // 开始对比
  const handleStartCompare = () => {
    if (compareVersions.length === 2) {
      setCompareModalVisible(true);
    } else {
      message.warning('请选择 2 个版本进行对比');
    }
  };

  // 退出对比模式
  const handleExitCompareMode = () => {
    setViewMode('list');
    setCompareVersions([]);
  };

  // 版本恢复成功
  const handleVersionRestore = (newVersionNumber: number) => {
    loadVersions();
    setViewMode('list');
    setSelectedVersion(null);
    onVersionRestore?.(newVersionNumber);
  };

  // 计算抽屉宽度（响应式）
  const getDrawerWidth = () => {
    if (viewMode === 'detail') return '60%'; // 详情模式：60% 宽度
    if (viewMode === 'list') return 520; // 列表模式：固定 520px
    return 520; // 对比模式：固定 520px（弹窗全屏）
  };

  // 渲染标题栏
  const renderTitle = () => (
    <Space direction="vertical" size={2} style={{ width: '100%' }}>
      <Space>
        <HistoryOutlined style={{ fontSize: 18, color: '#1890ff' }} />
        <span style={{ fontSize: 16, fontWeight: 600 }}>版本历史</span>
        <Badge count={total} showZero style={{ backgroundColor: '#52c41a' }} />
      </Space>
      <span style={{ fontSize: 12, color: '#8c8c8c' }}>
        当前版本: #{currentVersionNumber} • 共 {total} 个历史版本
      </span>
    </Space>
  );

  // 渲染工具栏
  const renderToolbar = () => (
    <Space>
      {viewMode === 'compare' ? (
        <>
          <Badge count={compareVersions.length} offset={[-5, 5]}>
            <Button
              type="primary"
              icon={<SwapOutlined />}
              disabled={compareVersions.length !== 2}
              onClick={handleStartCompare}
            >
              开始对比
            </Button>
          </Badge>
          <Button onClick={handleExitCompareMode}>取消选择</Button>
        </>
      ) : (
        <>
          <Tooltip title="选择两个版本进行内容对比">
            <Button icon={<SwapOutlined />} onClick={handleEnterCompareMode}>
              版本对比
            </Button>
          </Tooltip>
          <Tooltip title="清理指定日期之前的旧版本">
            <Button icon={<ClearOutlined />} onClick={() => setCleanModalVisible(true)}>
              清理
            </Button>
          </Tooltip>
          <Tooltip title="刷新版本列表">
            <Button icon={<ReloadOutlined />} onClick={loadVersions} loading={loading} />
          </Tooltip>
        </>
      )}
    </Space>
  );

  return (
    <>
      <Drawer
        title={renderTitle()}
        placement="right"
        width={getDrawerWidth()}
        open={visible}
        onClose={onClose}
        extra={renderToolbar()}
        className="version-history-drawer"
        styles={{
          body: { padding: 0 },
        }}
      >
        <div className="version-history-container">
          {/* 左侧：版本列表区域 */}
          <div className={`version-list-section ${viewMode === 'detail' ? 'with-detail' : ''}`}>
            {/* 搜索栏 */}
            {viewMode === 'compare' && (
              <div className="version-compare-hint">
                <InfoCircleOutlined style={{ color: '#1890ff', marginRight: 8 }} />
                <span>请选择 2 个版本进行对比 ({compareVersions.length}/2)</span>
              </div>
            )}

            <div className="version-search">
              <Input
                prefix={<SearchOutlined />}
                placeholder="搜索版本备注、作者名"
                value={searchKeyword}
                onChange={(e) => setSearchKeyword(e.target.value)}
                allowClear
                size="large"
              />
            </div>

            {/* 版本列表 */}
            <div className="version-list-wrapper">
              <Spin spinning={loading}>
                {filteredVersions.length > 0 ? (
                  <VersionTimeline
                    versions={filteredVersions}
                    currentVersionNumber={currentVersionNumber}
                    selectedVersion={selectedVersion}
                    compareMode={viewMode === 'compare'}
                    compareVersions={compareVersions}
                    onVersionClick={handleVersionSelect}
                    onVersionRestore={handleVersionRestore}
                    documentId={documentId}
                  />
                ) : searchKeyword.trim() ? (
                  <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description={`未找到包含 "${searchKeyword}" 的版本`}
                    style={{ marginTop: 60 }}
                  />
                ) : (
                  <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description="暂无版本历史"
                    style={{ marginTop: 60 }}
                  />
                )}
              </Spin>
            </div>

            {/* 分页 */}
            {total > pageSize && (
              <div className="version-pagination">
                <Pagination
                  current={pageNum}
                  pageSize={pageSize}
                  total={total}
                  onChange={(page, size) => {
                    setPageNum(page);
                    setPageSize(size || pageSize);
                  }}
                  showSizeChanger
                  showTotal={(total) => `共 ${total} 个版本`}
                  size="small"
                />
              </div>
            )}
          </div>

          {/* 右侧：版本详情面板（仅在 detail 模式显示） */}
          {viewMode === 'detail' && selectedVersion && (
            <div className="version-detail-section">
              <VersionDetailPanel
                documentId={documentId}
                versionNumber={selectedVersion}
                onClose={() => {
                  setViewMode('list');
                  setSelectedVersion(null);
                }}
                onVersionRestore={handleVersionRestore}
              />
            </div>
          )}
        </div>
      </Drawer>

      {/* 版本对比弹窗（全屏模式） */}
      {compareModalVisible && compareVersions.length === 2 && (
        <VersionCompareModal
          visible={compareModalVisible}
          documentId={documentId}
          fromVersion={Math.min(...compareVersions)}
          toVersion={Math.max(...compareVersions)}
          onClose={() => {
            setCompareModalVisible(false);
            handleExitCompareMode();
          }}
        />
      )}

      {/* 清理旧版本弹窗 */}
      <VersionCleanModal
        visible={cleanModalVisible}
        documentId={documentId}
        totalVersions={total}
        onSuccess={() => {
          loadVersions();
          setCleanModalVisible(false);
        }}
        onCancel={() => setCleanModalVisible(false)}
      />
    </>
  );
};

export default VersionHistory;

