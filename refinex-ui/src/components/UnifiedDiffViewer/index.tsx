/**
 * UnifiedDiffViewer - GitHub 风格的 Unified Diff 查看器
 *
 * 用于展示版本对比的差异，采用 GitHub 风格的 unified diff 格式
 * 支持高亮显示新增、删除和修改的行
 *
 * @author Refinex Team
 */

import React, { useMemo } from 'react';
import { Empty } from 'antd';
import classNames from 'classnames';
import './index.less';

export interface UnifiedDiffViewerProps {
  /** Unified diff 内容 */
  unifiedDiff: string;
  /** 自定义类名 */
  className?: string;
  /** 自定义样式 */
  style?: React.CSSProperties;
  /** 空状态提示 */
  emptyText?: string;
  /** 是否显示文件头信息 */
  showHeader?: boolean;
}

/** Diff 行类型 */
type DiffLineType = 'header' | 'hunk' | 'add' | 'delete' | 'context' | 'no-newline';

/** 解析后的 Diff 行 */
interface ParsedDiffLine {
  type: DiffLineType;
  content: string;
  oldLineNum?: number;
  newLineNum?: number;
  rawLine: string;
}

/**
 * 解析 unified diff 内容
 */
const parseUnifiedDiff = (diffText: string): ParsedDiffLine[] => {
  if (!diffText || diffText.trim() === '') {
    return [];
  }

  const lines = diffText.split('\n');
  const parsedLines: ParsedDiffLine[] = [];
  let oldLineNum = 0;
  let newLineNum = 0;

  for (const line of lines) {
    // 文件头（--- 和 +++）
    if (line.startsWith('---') || line.startsWith('+++')) {
      parsedLines.push({
        type: 'header',
        content: line,
        rawLine: line,
      });
      continue;
    }

    // Hunk 头（@@ 标记）
    if (line.startsWith('@@')) {
      const hunkMatch = line.match(/@@ -(\d+)(?:,(\d+))? \+(\d+)(?:,(\d+))? @@/);
      if (hunkMatch) {
        oldLineNum = parseInt(hunkMatch[1], 10);
        newLineNum = parseInt(hunkMatch[3], 10);
      }
      parsedLines.push({
        type: 'hunk',
        content: line,
        rawLine: line,
      });
      continue;
    }

    // No newline at end of file
    if (line.startsWith('\\ No newline')) {
      parsedLines.push({
        type: 'no-newline',
        content: line,
        rawLine: line,
      });
      continue;
    }

    // 新增行（+）
    if (line.startsWith('+')) {
      parsedLines.push({
        type: 'add',
        content: line.substring(1),
        newLineNum: newLineNum++,
        rawLine: line,
      });
      continue;
    }

    // 删除行（-）
    if (line.startsWith('-')) {
      parsedLines.push({
        type: 'delete',
        content: line.substring(1),
        oldLineNum: oldLineNum++,
        rawLine: line,
      });
      continue;
    }

    // 上下文行（空格或无前缀）
    parsedLines.push({
      type: 'context',
      content: line.startsWith(' ') ? line.substring(1) : line,
      oldLineNum: oldLineNum++,
      newLineNum: newLineNum++,
      rawLine: line,
    });
  }

  return parsedLines;
};

/**
 * 渲染行号
 */
const renderLineNumber = (num?: number) => {
  if (num === undefined) return null;
  return <span className="line-number">{num}</span>;
};

/**
 * UnifiedDiffViewer 组件
 */
const UnifiedDiffViewer: React.FC<UnifiedDiffViewerProps> = ({
  unifiedDiff,
  className,
  style,
  emptyText = '暂无差异',
  showHeader = true,
}) => {
  // 解析 diff 内容
  const parsedLines = useMemo(() => parseUnifiedDiff(unifiedDiff), [unifiedDiff]);

  // 空状态处理
  if (!unifiedDiff || parsedLines.length === 0) {
    return (
      <div className={classNames('unified-diff-viewer-empty', className)} style={style}>
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={emptyText}
          style={{ padding: '40px 0' }}
        />
      </div>
    );
  }

  return (
    <div className={classNames('unified-diff-viewer', className)} style={style}>
      <div className="diff-table-wrapper">
        <table className="diff-table">
          <tbody>
            {parsedLines.map((line, index) => {
              const key = `diff-line-${index}`;

              // 文件头
              if (line.type === 'header') {
                if (!showHeader) return null;
                return (
                  <tr key={key} className="diff-line diff-header">
                    <td colSpan={3} className="diff-content">
                      <span className="diff-header-text">{line.content}</span>
                    </td>
                  </tr>
                );
              }

              // Hunk 头
              if (line.type === 'hunk') {
                return (
                  <tr key={key} className="diff-line diff-hunk">
                    <td colSpan={3} className="diff-content">
                      <span className="diff-hunk-text">{line.content}</span>
                    </td>
                  </tr>
                );
              }

              // No newline 提示
              if (line.type === 'no-newline') {
                return (
                  <tr key={key} className="diff-line diff-no-newline">
                    <td colSpan={3} className="diff-content">
                      <span className="diff-no-newline-text">{line.content}</span>
                    </td>
                  </tr>
                );
              }

              // 新增行
              if (line.type === 'add') {
                return (
                  <tr key={key} className="diff-line diff-add">
                    <td className="diff-line-num old-line-num"></td>
                    <td className="diff-line-num new-line-num">
                      {renderLineNumber(line.newLineNum)}
                    </td>
                    <td className="diff-content">
                      <span className="diff-marker">+</span>
                      <span className="diff-text">{line.content}</span>
                    </td>
                  </tr>
                );
              }

              // 删除行
              if (line.type === 'delete') {
                return (
                  <tr key={key} className="diff-line diff-delete">
                    <td className="diff-line-num old-line-num">
                      {renderLineNumber(line.oldLineNum)}
                    </td>
                    <td className="diff-line-num new-line-num"></td>
                    <td className="diff-content">
                      <span className="diff-marker">-</span>
                      <span className="diff-text">{line.content}</span>
                    </td>
                  </tr>
                );
              }

              // 上下文行
              return (
                <tr key={key} className="diff-line diff-context">
                  <td className="diff-line-num old-line-num">
                    {renderLineNumber(line.oldLineNum)}
                  </td>
                  <td className="diff-line-num new-line-num">
                    {renderLineNumber(line.newLineNum)}
                  </td>
                  <td className="diff-content">
                    <span className="diff-marker"></span>
                    <span className="diff-text">{line.content}</span>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default UnifiedDiffViewer;

