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
  directivesPlugin,
  AdmonitionDirectiveDescriptor,
  sandpackPlugin,
  toolbarPlugin,
  UndoRedo,
  BoldItalicUnderlineToggles,
  CodeToggle,
  ListsToggle,
  BlockTypeSelect,
  CreateLink,
  InsertImage,
  InsertTable,
  InsertThematicBreak,
  InsertCodeBlock,
  InsertAdmonition,
  InsertSandpack,
  ShowSandpackInfo,
  ConditionalContents,
  ChangeCodeMirrorLanguage,
  ChangeAdmonitionType,
  InsertFrontmatter,
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

          // Directives 插件（支持 Admonitions 等）
          directivesPlugin({
            directiveDescriptors: [AdmonitionDirectiveDescriptor]
          }),

          // Sandpack 实时代码编辑器插件
          sandpackPlugin({
            sandpackConfig: {
              defaultPreset: 'react',
              presets: [
                {
                  label: 'React',
                  name: 'react',
                  meta: 'live react',
                  sandpackTemplate: 'react',
                  sandpackTheme: 'light',
                  snippetFileName: '/App.js',
                  snippetLanguage: 'jsx',
                  initialSnippetContent: `export default function App() {
  return (
    <div>
      <h1>Hello World</h1>
      <p>Start editing to see some magic happen!</p>
    </div>
  );
}`,
                },
                {
                  label: 'React TypeScript',
                  name: 'react-ts',
                  meta: 'live',
                  sandpackTemplate: 'react-ts',
                  sandpackTheme: 'light',
                  snippetFileName: '/App.tsx',
                  snippetLanguage: 'tsx',
                  initialSnippetContent: `export default function App() {
  return (
    <div>
      <h1>Hello TypeScript</h1>
      <p>Fully typed React component!</p>
    </div>
  );
}`,
                },
                {
                  label: 'Vanilla JavaScript',
                  name: 'vanilla',
                  meta: 'live',
                  sandpackTemplate: 'vanilla',
                  sandpackTheme: 'light',
                  snippetFileName: '/index.js',
                  snippetLanguage: 'javascript',
                  initialSnippetContent: `document.getElementById('app').innerHTML = \`
<h1>Hello Vanilla JS</h1>
<p>You can use vanilla JavaScript here!</p>
\`;`,
                },
                {
                  label: 'Vue 3',
                  name: 'vue',
                  meta: 'live',
                  sandpackTemplate: 'vue',
                  sandpackTheme: 'light',
                  snippetFileName: '/src/App.vue',
                  snippetLanguage: 'vue',
                  initialSnippetContent: `<script setup>
import { ref } from 'vue'

const count = ref(0)
</script>

<template>
  <h1>Hello Vue 3!</h1>
  <button @click="count++">Count: {{ count }}</button>
</template>`,
                },
              ],
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
                {/* 撤销/重做 */}
                <UndoRedo />
                <Separator />

                {/* 条件工具栏：根据编辑器模式动态显示不同的工具组合 */}
                <ConditionalContents
                  options={[
                    {
                      // 源码模式 / Diff 模式：只显示切换按钮
                      when: (editor) =>
                        editor?.editorType === 'source' || editor?.editorType === 'diff',
                      contents: () => (
                        <DiffSourceToggleWrapper>
                          <></>
                        </DiffSourceToggleWrapper>
                      ),
                    },
                    {
                      // 代码块聚焦时：只显示语言切换
                      when: (editor) => editor?.editorType === 'codeblock',
                      contents: () => <ChangeCodeMirrorLanguage />,
                    },
                    {
                      // Sandpack 聚焦时：只显示 Sandpack 信息
                      when: (editor) => editor?.editorType === 'sandpack',
                      contents: () => <ShowSandpackInfo />,
                    },
                    {
                      // Admonition 聚焦时：显示类型切换
                      when: (editor) => editor?.editorType === 'directive',
                      contents: () => <ChangeAdmonitionType />,
                    },
                    {
                      // 默认模式：显示所有常规编辑工具（when 始终返回 true 作为 fallback）
                      when: () => true,
                      contents: () => (
                        <>
                          {/* 文本格式 */}
                          <BoldItalicUnderlineToggles />
                          <CodeToggle />
                          <Separator />

                          {/* 块类型选择 */}
                          <BlockTypeSelect />
                          <Separator />

                          {/* 列表 */}
                          <ListsToggle />
                          <Separator />

                          {/* 链接和图片 */}
                          <CreateLink />
                          <InsertImage />
                          <Separator />

                          {/* 表格、分隔线、代码块 */}
                          <InsertTable />
                          <InsertThematicBreak />
                          <InsertCodeBlock />
                          <Separator />

                          {/* Admonitions 提示框 */}
                          <InsertAdmonition />
                          <Separator />

                          {/* Sandpack 实时代码编辑器 */}
                          <InsertSandpack />
                          <Separator />

                          {/* Front-matter */}
                          <InsertFrontmatter />
                          <Separator />

                          {/* 搜索 */}
                          <SearchButton />
                        </>
                      ),
                    },
                  ]}
                />

                <Separator />

                {/* 源码模式切换按钮（始终显示在最右侧） */}
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

