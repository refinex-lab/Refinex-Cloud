/**
 * 版本时间线组件
 * 展示版本历史列表，支持选择、对比、恢复等操作
 */

import React, { useState } from 'react';
import { App, Badge, Button, Checkbox, Dropdown, Space, Tooltip } from 'antd';
import type { MenuProps } from 'antd';
import {
  ClockCircleOutlined,
  DeleteOutlined,
  EyeOutlined,
  RollbackOutlined,
  MoreOutlined,
  UserOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/zh-cn';
import type { ContentDocumentVersion } from '@/services/kb/typings.d';
import { deleteVersion, restoreVersion } from '@/services/kb/version';
import './VersionTimeline.less';

dayjs.extend(relativeTime);
dayjs.locale('zh-cn');

interface VersionTimelineProps {
  versions: ContentDocumentVersion[];
  currentVersionNumber: number;
  selectedVersion: number | null;
  compareMode: boolean;
  compareVersions: number[];
  documentId: number;
  onVersionClick: (versionNumber: number) => void;
  onVersionRestore?: (newVersionNumber: number) => void;
}

const VersionTimeline: React.FC<VersionTimelineProps> = ({
  versions,
  currentVersionNumber,
  selectedVersion,
  compareMode,
  compareVersions,
  documentId,
  onVersionClick,
  onVersionRestore,
}) => {
  const { message, modal } = App.useApp();
  const [restoringVersion, setRestoringVersion] = useState<number | null>(null);

  // 计算字数变化
  const getWordCountDiff = (index: number): { value: number; type: 'positive' | 'negative' | 'neutral' } => {
    if (index >= versions.length - 1) {
      return { value: 0, type: 'neutral' };
    }
    const current = versions[index].wordCount || 0;
    const previous = versions[index + 1].wordCount || 0;
    const diff = current - previous;
    return {
      value: diff,
      type: diff > 0 ? 'positive' : diff < 0 ? 'negative' : 'neutral',
    };
  };

  // 恢复版本
  const handleRestoreVersion = async (versionNumber: number) => {
    modal.confirm({
      title: '确认恢复版本',
      content: (
        <div>
          <p>确定要恢复到 <strong>版本 #{versionNumber}</strong> 吗？</p>
          <p style={{ color: '#8c8c8c', fontSize: 13 }}>
            恢复后会创建一个新版本，不会覆盖历史记录
          </p>
        </div>
      ),
      onOk: async () => {
        setRestoringVersion(versionNumber);
        try {
          const response = await restoreVersion(documentId, versionNumber);
          if (response.success && response.data) {
            message.success(response.data.message || `已恢复到版本 #${versionNumber}`);
            onVersionRestore?.(response.data.newVersionNumber);
          }
        } catch (error) {
          message.error('恢复版本失败');
        } finally {
          setRestoringVersion(null);
        }
      },
    });
  };

  // 删除版本
  const handleDeleteVersion = async (versionNumber: number) => {
    modal.confirm({
      title: '确认删除版本',
      content: `确定要删除版本 #${versionNumber} 吗？此操作不可恢复。`,
      okText: '删除',
      okType: 'danger',
      onOk: async () => {
        try {
          const response = await deleteVersion(documentId, versionNumber);
          if (response.success) {
            message.success('版本已删除');
            // 刷新列表（由父组件处理）
            onVersionRestore?.(currentVersionNumber);
          }
        } catch (error) {
          message.error('删除版本失败');
        }
      },
    });
  };

  // 版本操作菜单
  const getVersionMenu = (version: ContentDocumentVersion): MenuProps['items'] => [
    {
      key: 'view',
      label: '查看详情',
      icon: <EyeOutlined />,
      onClick: () => onVersionClick(version.versionNumber),
    },
    {
      key: 'restore',
      label: '恢复此版本',
      icon: <RollbackOutlined />,
      disabled: version.isCurrent,
      onClick: () => handleRestoreVersion(version.versionNumber),
    },
    {
      type: 'divider',
    },
    {
      key: 'delete',
      label: '删除此版本',
      icon: <DeleteOutlined />,
      danger: true,
      disabled: version.isCurrent,
      onClick: () => handleDeleteVersion(version.versionNumber),
    },
  ];

  return (
    <div className="version-timeline">
      {versions.map((version, index) => {
        const wordDiff = getWordCountDiff(index);
        const isSelected = selectedVersion === version.versionNumber;
        const isCompareSelected = compareVersions.includes(version.versionNumber);

        return (
          <div
            key={version.id}
            className={`version-item ${version.isCurrent ? 'current' : ''} ${
              isSelected ? 'selected' : ''
            } ${isCompareSelected ? 'compare-selected' : ''}`}
            onClick={() => !compareMode && onVersionClick(version.versionNumber)}
          >
            {/* 对比模式复选框 */}
            {compareMode && (
              <div className="version-checkbox">
                <Checkbox
                  checked={isCompareSelected}
                  onChange={() => onVersionClick(version.versionNumber)}
                />
              </div>
            )}

            {/* 版本卡片主体 */}
            <div className="version-card-body">
              {/* 头部：版本号和操作 */}
              <div className="version-header">
                <Space size={8}>
                  <span className="version-number">#{version.versionNumber}</span>
                  {version.isCurrent && <Badge status="success" text="当前" />}
                </Space>

                {/* 操作菜单 */}
                {!compareMode && (
                  <Dropdown menu={{ items: getVersionMenu(version) }} trigger={['click']}>
                    <Button
                      type="text"
                      size="small"
                      icon={<MoreOutlined />}
                      onClick={(e) => e.stopPropagation()}
                    />
                  </Dropdown>
                )}
              </div>

              {/* 中部：时间和作者 */}
              <div className="version-meta">
                <Tooltip title={dayjs(version.createTime).format('YYYY-MM-DD HH:mm:ss')}>
                  <span className="meta-item time">
                    <ClockCircleOutlined />
                    {dayjs(version.createTime).fromNow()}
                  </span>
                </Tooltip>
                <span className="meta-item author">
                  <UserOutlined />
                  {version.createdByName || '未知'}
                </span>
              </div>

              {/* 底部：变更说明和统计 */}
              <div className="version-footer">
                {/* 变更备注 */}
                {version.changeSummary && (
                  <div className="change-remark">{version.changeSummary}</div>
                )}

                {/* 统计信息 */}
                <div className="version-stats">
                  <span className="word-count">{version.wordCount || 0} 字</span>
                  {wordDiff.value !== 0 && (
                    <span className={`word-diff ${wordDiff.type}`}>
                      {wordDiff.type === 'positive' ? '+' : ''}
                      {wordDiff.value}
                    </span>
                  )}
                </div>
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default VersionTimeline;

