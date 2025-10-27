/**
 * MDXEditor 封装组件
 * 适配 UmiJS 框架，禁用 SSR
 */
import React, { forwardRef, useEffect, useState } from 'react';
import type { ForwardedRef } from 'react';
import type { MDXEditorMethods, MDXEditorProps } from '@mdxeditor/editor';
import { Spin } from 'antd';

export interface EditorWrapperProps extends MDXEditorProps {
  editorRef?: ForwardedRef<MDXEditorMethods> | null;
}

/**
 * MDXEditor 包装组件
 * 使用客户端动态加载确保不在 SSR 时执行
 */
export const MDXEditorWrapper = forwardRef<MDXEditorMethods, MDXEditorProps>((props, ref) => {
  const [EditorComp, setEditorComp] = useState<React.ComponentType<EditorWrapperProps> | null>(null);

  useEffect(() => {
    // 仅在客户端加载编辑器组件
    if (typeof window !== 'undefined') {
      // @ts-ignore - 动态导入在 UmiJS 中支持
      import('./InitializedMDXEditor')
        .then((module: any) => {
          setEditorComp(() => module.default);
        })
        .catch((error: any) => {
          console.error('加载编辑器失败:', error);
        });
    }
  }, []);

  if (!EditorComp) {
    return (
      <div style={{ padding: '40px', textAlign: 'center' }}>
        <Spin tip="加载编辑器中..." size="large" />
      </div>
    );
  }

  return <EditorComp {...props} editorRef={ref} />;
});

MDXEditorWrapper.displayName = 'MDXEditorWrapper';

export default MDXEditorWrapper;

