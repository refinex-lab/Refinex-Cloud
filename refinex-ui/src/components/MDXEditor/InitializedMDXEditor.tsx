/**
 * MDXEditor 初始化组件
 * 配置所有插件和功能
 */
import { useState, useEffect } from 'react';
import type { ForwardedRef } from 'react';
import { createPortal } from 'react-dom';
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
  searchPlugin,
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
import { Button } from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import '@mdxeditor/editor/style.css';
import SearchToolbar from './SearchToolbar';

// 创建一个全局变量用于存储搜索工具栏的容器
let searchToolbarContainer: HTMLElement | null = null;

export interface InitializedMDXEditorProps extends MDXEditorProps {
  editorRef?: ForwardedRef<MDXEditorMethods> | null;
}

/**
 * 搜索按钮和工具栏组件（在 MDXEditor 工具栏中使用，通过 Portal 渲染到外部）
 */
const SearchButton = () => {
  const [searchVisible, setSearchVisible] = useState(false);

  // 确保容器存在
  useEffect(() => {
    if (!searchToolbarContainer) {
      searchToolbarContainer = document.getElementById('mdx-search-toolbar-portal');
    }
  }, []);

  return (
    <>
      <Button
        size="small"
        icon={<SearchOutlined />}
        onClick={() => setSearchVisible(!searchVisible)}
        type={searchVisible ? 'primary' : 'default'}
      >
        查找
      </Button>

      {/* 使用 Portal 将 SearchToolbar 渲染到外部容器，但保持在 MDXEditor Context 中 */}
      {searchToolbarContainer && createPortal(
        <SearchToolbar
          visible={searchVisible}
          onClose={() => setSearchVisible(false)}
        />,
        searchToolbarContainer
      )}
    </>
  );
};

/**
 * 已初始化的 MDXEditor 组件
 * 包含常用的 Markdown 编辑功能
 */
export default function InitializedMDXEditor({
  editorRef,
  ...props
}: InitializedMDXEditorProps) {
  return (
    <div className="mdx-editor-wrapper" style={{ position: 'relative' }}>
      {/* Portal 容器：用于渲染搜索工具栏 */}
      <div id="mdx-search-toolbar-portal" style={{ position: 'absolute', top: 0, right: 0, zIndex: 1000 }} />

      {/* MDXEditor */}
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

          // 搜索和替换插件
          searchPlugin(),

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
                <SearchButton />
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
    </div>
  );
}

