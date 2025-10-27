/**
 * MDXEditor 初始化组件
 * 配置所有插件和功能
 */
import type { ForwardedRef } from 'react';
import {
  MDXEditor,
  headingsPlugin,
  listsPlugin,
  quotePlugin,
  thematicBreakPlugin,
  markdownShortcutPlugin,
  linkPlugin,
  linkDialogPlugin,
  imagePlugin,
  tablePlugin,
  codeBlockPlugin,
  codeMirrorPlugin,
  diffSourcePlugin,
  frontmatterPlugin,
  toolbarPlugin,
  UndoRedo,
  BoldItalicUnderlineToggles,
  ListsToggle,
  BlockTypeSelect,
  CreateLink,
  InsertImage,
  InsertTable,
  InsertThematicBreak,
  InsertCodeBlock,
  DiffSourceToggleWrapper,
  Separator,
  type MDXEditorMethods,
  type MDXEditorProps,
} from '@mdxeditor/editor';
import '@mdxeditor/editor/style.css';

export interface InitializedMDXEditorProps extends MDXEditorProps {
  editorRef?: ForwardedRef<MDXEditorMethods> | null;
}

/**
 * 已初始化的 MDXEditor 组件
 * 包含常用的 Markdown 编辑功能
 */
export default function InitializedMDXEditor({
  editorRef,
  ...props
}: InitializedMDXEditorProps) {
  return (
    <MDXEditor
      plugins={[
        // 基础插件
        headingsPlugin(),
        listsPlugin(),
        quotePlugin(),
        thematicBreakPlugin(),
        markdownShortcutPlugin(),

        // 链接插件
        linkPlugin(),
        linkDialogPlugin(),

        // 图片插件
        imagePlugin({
          imageUploadHandler: async (file) => {
            // TODO: 实现图片上传逻辑
            return '/api/placeholder/400/300'; // 临时占位符
          },
        }),

        // 表格插件
        tablePlugin(),

        // 代码块插件
        codeBlockPlugin({ defaultCodeBlockLanguage: 'javascript' }),
        codeMirrorPlugin({
          codeBlockLanguages: {
            javascript: 'JavaScript',
            typescript: 'TypeScript',
            python: 'Python',
            java: 'Java',
            css: 'CSS',
            html: 'HTML',
            sql: 'SQL',
            bash: 'Bash',
            json: 'JSON',
            yaml: 'YAML',
            markdown: 'Markdown',
          },
        }),

        // Diff/Source 模式切换
        diffSourcePlugin({ viewMode: 'rich-text' }),

        // Front-matter 支持
        frontmatterPlugin(),

        // 工具栏
        toolbarPlugin({
          toolbarContents: () => (
            <>
              <UndoRedo />
              <Separator />
              <BoldItalicUnderlineToggles />
              <Separator />
              <BlockTypeSelect />
              <Separator />
              <ListsToggle />
              <Separator />
              <CreateLink />
              <InsertImage />
              <Separator />
              <InsertTable />
              <InsertThematicBreak />
              <InsertCodeBlock />
              <Separator />
              <DiffSourceToggleWrapper>
                <></>
              </DiffSourceToggleWrapper>
            </>
          ),
        }),
      ]}
      {...props}
      ref={editorRef}
    />
  );
}

