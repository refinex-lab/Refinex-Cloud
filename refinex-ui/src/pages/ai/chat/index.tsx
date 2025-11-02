import {
  ApiOutlined,
  AppstoreAddOutlined,
  CloudUploadOutlined,
  CopyOutlined,
  DeleteOutlined,
  DislikeOutlined,
  DownOutlined,
  EditOutlined,
  ExperimentOutlined,
  FileSearchOutlined,
  FileImageOutlined,
  FolderOutlined,
  GlobalOutlined,
  LikeOutlined,
  LinkOutlined,
  PaperClipOutlined,
  ProductOutlined,
  QuestionCircleOutlined,
  RedoOutlined,
  ReloadOutlined,
  RobotOutlined,
  ScheduleOutlined,
  SearchOutlined,
  VideoCameraOutlined,
} from '@ant-design/icons';
import {
  Actions,
  type ActionsProps,
  Attachments,
  Bubble,
  Conversations,
  Prompts,
  Sender,
  Suggestion,
  Welcome,
  useXAgent,
  useXChat,
} from '@ant-design/x';
import { Avatar, Button, Divider, Dropdown, Flex, type GetProp, Input, Modal, Space, Spin, Switch, message, theme } from 'antd';
import { createStyles } from 'antd-style';
import dayjs from 'dayjs';
import React, { useEffect, useRef, useState } from 'react';
import AiBlueIcon from '@/assets/images/ai/ai_blue_icon.svg';
import MarkdownViewer from '@/components/MarkdownViewer';
import type { BubbleProps } from '@ant-design/x';
import { GPTVis } from '@antv/gpt-vis';

type BubbleDataType = {
  role: string;
  content: string;
};

// 快捷指令类型定义
type SuggestionItems = Exclude<GetProp<typeof Suggestion, 'items'>, () => void>;

const DEFAULT_CONVERSATIONS_ITEMS = [
  {
    key: 'default-0',
    label: '什么是 Refinex AI？',
    group: '今天',
  },
  {
    key: 'default-1',
    label: '如何快速开始使用？',
    group: '今天',
  },
  {
    key: 'default-2',
    label: '新的 AI 混合界面',
    group: '昨天',
  },
];


// 模拟的模型数据
const MOCK_MODELS = [
  { key: 'gpt-4', label: 'GPT-4', description: 'OpenAI GPT-4' },
  { key: 'gpt-3.5', label: 'GPT-3.5 Turbo', description: 'OpenAI GPT-3.5' },
  { key: 'claude-3', label: 'Claude 3', description: 'Anthropic Claude 3' },
  { key: 'deepseek', label: 'DeepSeek', description: 'DeepSeek AI' },
];

// 模拟的知识库数据
const MOCK_KNOWLEDGE_BASES = [
  { key: 'kb-1', label: '产品文档', description: '产品使用文档知识库' },
  { key: 'kb-2', label: '技术规范', description: '技术开发规范知识库' },
  { key: 'kb-3', label: '常见问题', description: 'FAQ 知识库' },
  { key: 'kb-4', label: '最佳实践', description: '最佳实践知识库' },
];

// 🌟 侧边栏功能菜单项
const FEATURE_MENU_ITEMS = [
  { key: 'new-chat', label: '新聊天', icon: <EditOutlined /> },
  { key: 'search-chat', label: '搜索聊天', icon: <SearchOutlined /> },
  { key: 'image-gen', label: '图片生成', icon: <FileImageOutlined /> },
  { key: 'video-gen', label: '视频生成', icon: <VideoCameraOutlined /> },
  { key: 'project', label: '项目', icon: <FolderOutlined /> },
];

// 快捷指令数据
const SUGGESTION_ITEMS: SuggestionItems = [
  {
    label: '📝 撰写内容',
    value: 'writing',
    icon: <EditOutlined />,
    children: [
      { label: '写一份报告', value: 'write-report' },
      { label: '写一篇文章', value: 'write-article' },
      { label: '写邮件', value: 'write-email' },
      { label: '写周报', value: 'write-weekly' },
    ],
  },
  {
    label: '💡 创意生成',
    value: 'creative',
    icon: <ExperimentOutlined />,
    children: [
      { label: '头脑风暴', value: 'brainstorm' },
      { label: '取名字', value: 'naming' },
      { label: '生成方案', value: 'generate-plan' },
    ],
  },
  {
    label: '📚 知识问答',
    value: 'knowledge',
    icon: <FileSearchOutlined />,
    children: [
      { label: '关于 React', value: 'about-react' },
      { label: '关于 Spring Boot', value: 'about-spring' },
      { label: '关于 AI', value: 'about-ai' },
      { label: '关于数据库', value: 'about-database' },
    ],
  },
  {
    label: '🔧 代码助手',
    value: 'code',
    icon: <RobotOutlined />,
    children: [
      { label: '代码审查', value: 'code-review' },
      { label: '优化代码', value: 'optimize-code' },
      { label: '生成测试', value: 'generate-test' },
      { label: '解释代码', value: 'explain-code' },
    ],
  },
  {
    label: '🔍 数据分析',
    value: 'analysis',
    icon: <ScheduleOutlined />,
    children: [
      { label: '分析数据', value: 'analyze-data' },
      { label: '生成图表', value: 'generate-chart' },
      { label: '制作仪表板', value: 'create-dashboard' },
    ],
  },
  {
    label: '🎨 设计辅助',
    value: 'design',
    icon: <AppstoreAddOutlined />,
    children: [
      { label: '设计界面', value: 'design-ui' },
      { label: '生成配色', value: 'generate-colors' },
      { label: '制作原型', value: 'create-prototype' },
    ],
  },
];

const useStyle = createStyles(({ token, css }) => {
  return {
    layout: css`
      width: 100%;
      min-width: 1000px;
      height: 100vh;
      display: flex;
      background: ${token.colorBgContainer};
      font-family: AlibabaPuHuiTi, ${token.fontFamily}, sans-serif;

      /* 覆盖 ant-pro-layout 的默认 padding */
      margin: -32px -40px;
      width: calc(100% + 80px);
      height: calc(100vh + 0px);
    `,
    // sider 样式
    sider: css`
      background: ${token.colorBgContainer};
      border-right: 1px solid ${token.colorBorderSecondary};
      width: 280px;
      height: 100%;
      display: flex;
      flex-direction: column;
      padding: 12px;
      box-sizing: border-box;
      transition: width 0.2s ease, padding 0.2s ease;
      overflow: hidden;
      position: relative;

      &.collapsed {
        width: 60px;
        padding: 12px 8px;
      }
    `,
    siderHeader: css`
      display: flex;
      align-items: center;
      justify-content: flex-end;
      height: 40px;
      margin-bottom: 12px;
      padding: 0 8px;
    `,
    collapseBtn: css`
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s ease;
      color: ${token.colorTextSecondary};

      &:hover {
        background: ${token.colorPrimaryBg};
        color: ${token.colorPrimary};
      }
    `,
    collapseBtnWrapper: css`
      position: absolute;
      top: 50%;
      right: -12px; /* 向右偏移，让按钮完全露出 */
      transform: translateY(-50%);
      z-index: 1000;
    `,
    collapseBtnFloating: css`
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: ${token.colorBgContainer};
      border: 1px solid ${token.colorBorder};
      border-radius: 50%;
      cursor: pointer;
      color: ${token.colorTextSecondary};
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      font-size: 10px;
    `,
    featureMenu: css`
      display: flex;
      flex-direction: column;
      gap: 4px;
      margin-bottom: 12px;
    `,
    featureMenuItem: css`
      display: flex;
      align-items: center;
      gap: 12px;
      height: 40px;
      padding: 8px 12px;
      background: transparent;
      border-radius: 8px;
      color: ${token.colorText};
      font-size: 14px;
      cursor: pointer;
      transition: all 0.2s ease;
      user-select: none;

      &:hover {
        background: ${token.colorFillTertiary};
      }

      .anticon {
        font-size: 16px;
        color: ${token.colorTextSecondary};
      }

      &.active {
        background: ${token.colorPrimaryBg};
        color: ${token.colorPrimary};

        .anticon {
          color: ${token.colorPrimary};
        }
      }
    `,
    featureDivider: css`
      height: 1px;
      background: ${token.colorBorderSecondary};
      margin: 8px 0;
    `,
    // 🔍 搜索弹窗样式
    searchModal: css`
      .ant-modal-content {
        padding: 0;
        border-radius: 12px;
        overflow: hidden;
      }

      .ant-modal-body {
        padding: 0;
      }
    `,
    searchModalHeader: css`
      padding: 16px 20px;
      border-bottom: 1px solid ${token.colorBorderSecondary};
    `,
    searchInput: css`
      .ant-input {
        font-size: 16px;
        border: none;
        box-shadow: none !important;

        &:focus {
          border: none;
          box-shadow: none !important;
        }
      }

      .ant-input-prefix {
        margin-right: 12px;
        color: ${token.colorTextSecondary};
        font-size: 18px;
      }
    `,
    searchResults: css`
      max-height: 500px;
      overflow-y: auto;
      padding: 8px 0;
    `,
    searchResultItem: css`
      padding: 12px 20px;
      cursor: pointer;
      transition: all 0.2s ease;
      border-bottom: 1px solid ${token.colorBorderSecondary};

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background: ${token.colorFillTertiary};
      }
    `,
    searchResultTitle: css`
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;
      font-size: 14px;
      font-weight: 500;
      color: ${token.colorText};
    `,
    searchResultContent: css`
      font-size: 13px;
      color: ${token.colorTextSecondary};
      line-height: 1.6;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;

      mark {
        background-color: ${token.colorWarningBg};
        color: ${token.colorWarningText};
        padding: 0 2px;
        border-radius: 2px;
      }
    `,
    searchResultTime: css`
      margin-top: 4px;
      font-size: 12px;
      color: ${token.colorTextTertiary};
    `,
    searchEmpty: css`
      padding: 60px 20px;
      text-align: center;
      color: ${token.colorTextSecondary};

      .anticon {
        font-size: 48px;
        margin-bottom: 16px;
        color: ${token.colorTextQuaternary};
      }
    `,
    // 🧠 思考过程样式（DeepSeek 风格）
    thinkingBlock: css`
      margin-bottom: 12px;
      border: 1px solid ${token.colorBorder};
      border-radius: 8px;
      overflow: hidden;
      background: transparent;
    `,
    thinkingHeader: css`
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 16px;
      background: transparent;
      cursor: pointer;
      user-select: none;
    `,
    thinkingIcon: css`
      font-size: 16px;
      line-height: 1;
    `,
    thinkingTitle: css`
      font-size: 13px;
      font-weight: 500;
      color: ${token.colorTextSecondary};
    `,
    thinkingContent: css`
      padding: 16px;
      background: transparent;
      border-top: 1px solid ${token.colorBorderSecondary};
      font-size: 13px;
      line-height: 1.8;
      color: ${token.colorTextSecondary};

      /* 思考内容的 Markdown 样式调整 */
      p {
        margin-bottom: 8px;
        &:last-child {
          margin-bottom: 0;
        }
      }

      ul, ol {
        margin: 8px 0;
        padding-left: 24px;
      }

      li {
        margin-bottom: 4px;
      }

      code {
        background: ${token.colorFillTertiary};
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 12px;
      }

      pre {
        background: ${token.colorFillTertiary};
        border-radius: 6px;
        overflow-x: auto;
        margin: 8px 0;

        code {
          background: transparent;
          padding: 0;
        }
      }
    `,
    addBtn: css`
      display: flex;
      align-items: center;
      gap: 8px;
      height: 44px;
      padding: 12px;
      margin-bottom: 8px;
      background: transparent;
      border: 1px solid ${token.colorBorder};
      border-radius: 8px;
      color: ${token.colorText};
      font-size: 14px;
      font-weight: 400;
      cursor: pointer;
      transition: all 0.2s ease;
      justify-content: flex-start;
      width: 100%;

      &:hover {
        background: ${token.colorBgTextHover};
        border-color: ${token.colorBorder};
        color: ${token.colorText};
      }

      .anticon {
        font-size: 16px;
        color: ${token.colorText};
      }
    `,
    conversations: css`
      flex: 1;
      overflow-y: auto;
      margin-top: 12px;
      padding: 0;

      .ant-conversations-list {
        padding-inline-start: 0;
      }
    `,
    siderFooter: css`
      border-top: 1px solid ${token.colorBorderSecondary};
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    `,
    // chat list 样式
    chat: css`
      height: 100%;
      width: 100%;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      padding-block: ${token.paddingLG}px;
      gap: 16px;
    `,
    chatPrompt: css`
      .ant-prompts-label {
        color: #000000e0 !important;
      }
      .ant-prompts-desc {
        color: #000000a6 !important;
        width: 100%;
      }
      .ant-prompts-icon {
        color: #000000a6 !important;
      }
    `,
    chatList: css`
      flex: 1;
      overflow: auto;
      display: flex;
      flex-direction: column;
      justify-content: center;

      /* 🎨 隐藏滚动条，保持美观 */
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: ${token.colorBorderSecondary};
        border-radius: 3px;
        transition: background 0.2s;
      }
      &::-webkit-scrollbar-thumb:hover {
        background: ${token.colorBorder};
      }
      /* Firefox */
      scrollbar-width: thin;
      scrollbar-color: ${token.colorBorderSecondary} transparent;

      /* 🎨 DeepSeek 风格：强制 AI 消息全宽显示 */
      .ant-bubble-list {
        width: 100%;
      }

      /* AI 消息（placement: start）全宽 - 使用更强的选择器优先级 */
      .ant-bubble-list-item-placement-start {
        width: 100% !important;
        max-width: 100% !important;
        display: flex !important;
        flex-direction: column !important;
      }

      /* 覆盖 .ant-bubble-start 的默认宽度限制 - 使用多重选择器提高优先级 */
      .ant-bubble-list-item-placement-start > .ant-bubble,
      .ant-bubble-list-item-placement-start > .ant-bubble-start,
      .ant-bubble-list-item-placement-start > .ant-bubble.ant-bubble-start,
      .ant-bubble-list-item-placement-start .ant-bubble[role="assistant"] {
        width: 100% !important;
        max-width: none !important;
        min-width: 100% !important;
        flex: 1 !important;
      }

      /* 强制覆盖所有子元素的宽度 */
      .ant-bubble-list-item-placement-start .ant-bubble-content-wrapper,
      .ant-bubble-list-item-placement-start .ant-bubble-content,
      .ant-bubble-list-item-placement-start .ant-bubble-content-filled,
      .ant-bubble-list-item-placement-start .ant-bubble-footer,
      .ant-bubble-list-item-placement-start .markdown-viewer,
      .ant-bubble-list-item-placement-start .markdown-viewer > * {
        width: 100% !important;
        max-width: none !important;
        box-sizing: border-box !important;
      }

      /* 用户消息（placement: end）限制宽度 */
      .ant-bubble-list-item-placement-end {
        display: flex;
        justify-content: flex-end;
      }

      .ant-bubble-list-item-placement-end .ant-bubble {
        max-width: 90%; /* 🎨 用户消息最大宽度 90% */
      }
    `,
    loadingMessage: css`
      background-image: linear-gradient(90deg, #ff6b23 0%, #af3cb8 31%, #53b6ff 89%);
      background-size: 100% 2px;
      background-repeat: no-repeat;
      background-position: bottom;
    `,
    placeholder: css`
      padding-top: 32px;
    `,
    // sender 样式
    senderWrapper: css`
      width: 90%; /* 🎨 90% 宽度，与聊天区域保持一致 */
      margin: 0 auto; /* 🎨 居中显示 */
      padding: 0 24px; /* 🎨 内部左右留白 */
    `,
    sender: css`
      width: 100%;

      /* 去除聚焦时的蓝色边框和阴影 - 针对所有可能的选择器 */
      .ant-input-outlined:focus,
      .ant-input-outlined:focus-within,
      .ant-input:focus,
      .ant-input:focus-within,
      .ant-input-textarea:focus,
      .ant-input-textarea:focus-within,
      .ant-input-affix-wrapper:focus,
      .ant-input-affix-wrapper:focus-within,
      .ant-input-affix-wrapper-focused,
      textarea:focus,
      textarea:focus-within,
      textarea.ant-input:focus,
      textarea.ant-input:focus-within {
        border-color: ${token.colorBorder} !important;
        box-shadow: none !important;
        outline: none !important;
      }

      /* 去除 hover 时的边框变化 */
      .ant-input-outlined:hover,
      .ant-input:hover,
      .ant-input-textarea:hover,
      .ant-input-affix-wrapper:hover,
      .ant-input-affix-wrapper-focused:hover,
      textarea:hover,
      textarea.ant-input:hover {
        border-color: ${token.colorBorder} !important;
      }

      /* 针对 Ant Design X Sender 组件的特殊处理 */
      & .ant-input-affix-wrapper,
      & .ant-input-affix-wrapper-focused,
      & .ant-input-outlined {
        border-color: ${token.colorBorder} !important;
        box-shadow: none !important;
      }

      /* 覆盖所有可能的聚焦状态 */
      &:focus-within .ant-input-affix-wrapper,
      &:focus-within .ant-input,
      &:focus-within textarea {
        border-color: ${token.colorBorder} !important;
        box-shadow: none !important;
      }
    `,
    speechButton: css`
      font-size: 18px;
      color: ${token.colorText} !important;
    `,
    senderPrompt: css`
      width: 100%;
      max-width: 700px;
      margin: 0 auto;
      color: ${token.colorText};
    `,
  };
});

const AIChatPage: React.FC = () => {
  const { styles } = useStyle();
  const { token } = theme.useToken();
  const abortController = useRef<AbortController | null>(null);

  // ==================== State ====================
  const [messageHistory, setMessageHistory] = useState<Record<string, any>>({});

  const [conversations, setConversations] = useState(DEFAULT_CONVERSATIONS_ITEMS);
  const [curConversation, setCurConversation] = useState(DEFAULT_CONVERSATIONS_ITEMS[0].key);

  const [attachmentsOpen, setAttachmentsOpen] = useState(false);
  const [attachedFiles, setAttachedFiles] = useState<GetProp<typeof Attachments, 'items'>>([]);

  const [inputValue, setInputValue] = useState('');
  const [siderCollapsed, setSiderCollapsed] = useState(false);

  // 模型和知识库选择状态
  const [selectedModel, setSelectedModel] = useState(MOCK_MODELS[0]);
  const [selectedKnowledgeBase, setSelectedKnowledgeBase] = useState<typeof MOCK_KNOWLEDGE_BASES[0] | null>(null);

  // 深度思考和联网搜索状态
  const [deepThinkEnabled, setDeepThinkEnabled] = useState(false);
  const [webSearchEnabled, setWebSearchEnabled] = useState(false);

  // 🌟 当前激活的功能菜单项
  const [activeFeature, setActiveFeature] = useState('new-chat');

  // 🔍 搜索弹窗状态
  const [searchModalVisible, setSearchModalVisible] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchResults, setSearchResults] = useState<Array<{
    conversationKey: string;
    conversationLabel: string;
    messageIndex: number;
    messageRole: 'user' | 'assistant';
    messageContent: string;
    timestamp: string;
  }>>([]);

  /**
   * 🔔 Please replace the BASE_URL, PATH, MODEL, API_KEY with your own values.
   */

  // ==================== Runtime ====================
  const [agent] = useXAgent<BubbleDataType>({
    baseURL: 'https://api.deepseek.com/chat/completions',
    model: 'deepseek-reasoner',
    dangerouslyApiKey: 'Bearer sk-5555ec224cd34d1583dedc1000fb9dba',
  });
  const loading = agent.isRequesting();

  const { onRequest, messages, setMessages } = useXChat({
    agent,
    requestFallback: (_, { error }) => {
      if (error.name === 'AbortError') {
      return {
        content: '请求已取消',
        role: 'assistant',
      };
      }
      return {
        content: '请求失败，请重试！',
        role: 'assistant',
      };
    },
    transformMessage: (info) => {
      const { originMessage, chunk } = info || {};
      let currentContent = '';
      let currentThink = '';
      try {
        if (chunk?.data && !chunk?.data.includes('DONE')) {
          const message = JSON.parse(chunk?.data);
          currentThink = message?.choices?.[0]?.delta?.reasoning_content || '';
          currentContent = message?.choices?.[0]?.delta?.content || '';
        }
      } catch (error) {
        console.error(error);
      }

      let content = '';

      if (!originMessage?.content && currentThink) {
        content = `<think>${currentThink}`;
      } else if (
        originMessage?.content?.includes('<think>') &&
        !originMessage?.content.includes('</think>') &&
        currentContent
      ) {
        content = `${originMessage?.content}</think>${currentContent}`;
      } else {
        content = `${originMessage?.content || ''}${currentThink}${currentContent}`;
      }
      return {
        content: content,
        role: 'assistant',
      };
    },
    resolveAbortController: (controller) => {
      abortController.current = controller;
    },
  });

  // ==================== Event ====================
  const onSubmit = (val: string) => {
    if (!val) return;

    if (loading) {
      message.error('正在请求中，请等待请求完成');
      return;
    }

    onRequest({
      stream: true,
      message: { role: 'user', content: val },
    });
  };

  // ==================== Actions ====================
  // 操作按钮配置
  const actionItems = [
    {
      key: 'retry',
      icon: <RedoOutlined />,
      label: '重试',
    },
    {
      key: 'copy',
      icon: <CopyOutlined />,
      label: '复制',
    },
    {
      key: 'like',
      icon: <LikeOutlined />,
      label: '点赞',
    },
    {
      key: 'dislike',
      icon: <DislikeOutlined />,
      label: '踩',
    },
  ];

  // 🌟 处理功能菜单点击事件
  const handleFeatureMenuClick = (key: string) => {
    if (key === 'new-chat') {
      // 新聊天功能
      if (agent.isRequesting()) {
        message.error('正在请求中，请等待请求完成后再创建新会话...');
        return;
      }

      const now = dayjs().valueOf().toString();
      setConversations([
        {
          key: now,
          label: `新对话 ${conversations.length + 1}`,
          group: '今天',
        },
        ...conversations,
      ]);
      setCurConversation(now);
      setMessages([]);
      setActiveFeature(key);
    } else if (key === 'search-chat') {
      // 搜索聊天功能
      setSearchModalVisible(true);
      setActiveFeature(key);
    } else {
      // 其他功能暂未实现
      setActiveFeature(key);
      message.info('敬请期待 🎉');
    }
  };

  // 🔍 搜索消息函数
  const handleSearch = (keyword: string) => {
    if (!keyword.trim()) {
      setSearchResults([]);
      return;
    }

    const results: typeof searchResults = [];
    const lowerKeyword = keyword.toLowerCase();

    // 遍历所有对话历史
    Object.keys(messageHistory).forEach((convKey) => {
      const conversation = conversations.find((c) => c.key === convKey);
      if (!conversation) return;

      const messages = messageHistory[convKey] || [];
      messages.forEach((msg: any, index: number) => {
        const content = msg.message?.content || '';
        // 移除 think 标签内容
        const contentWithoutThink = content.replace(/<think>[\s\S]*?<\/think>/g, '').trim();

        if (contentWithoutThink.toLowerCase().includes(lowerKeyword)) {
          results.push({
            conversationKey: convKey,
            conversationLabel: conversation.label,
            messageIndex: index,
            messageRole: msg.message?.role || 'user',
            messageContent: contentWithoutThink,
            timestamp: msg.timestamp || dayjs().format('YYYY-MM-DD HH:mm'),
          });
        }
      });
    });

    // 按时间倒序排序
    results.sort((a, b) => {
      return dayjs(b.timestamp).valueOf() - dayjs(a.timestamp).valueOf();
    });

    setSearchResults(results);
  };

  // 🔍 高亮搜索关键词
  const highlightKeyword = (text: string, keyword: string) => {
    if (!keyword.trim()) return text;

    const regex = new RegExp(`(${keyword})`, 'gi');
    return text.replace(regex, '<mark>$1</mark>');
  };

  // 🔍 点击搜索结果跳转到对话
  const handleSearchResultClick = (result: typeof searchResults[0]) => {
    // 切换到对应的对话
    setCurConversation(result.conversationKey);

    // 恢复对话消息
    const conversationMessages = messageHistory[result.conversationKey] || [];
    setMessages(conversationMessages);

    // 关闭搜索弹窗
    setSearchModalVisible(false);
    setSearchKeyword('');
    setSearchResults([]);

    message.success(`已跳转到对话：${result.conversationLabel}`);
  };

  // 处理 Actions 点击事件
  const handleActionsClick = (
    info: Parameters<NonNullable<ActionsProps['onClick']>>[0],
    messageContent: string
  ) => {
    const action = info.keyPath[0];

    switch (action) {
      case 'retry':
        // 重试：重新发送上一条用户消息
        if (messages.length >= 2) {
          const lastUserMessage = messages[messages.length - 2];
          if (lastUserMessage?.message?.role === 'user') {
            onSubmit(lastUserMessage.message.content);
            message.success('正在重新生成回复...');
          }
        }
        break;

      case 'copy':
        // 复制回复内容到剪贴板
        if (messageContent) {
          // 移除 think 标签内容，只复制正文
          const contentWithoutThink = messageContent.replace(/<think>[\s\S]*?<\/think>/g, '').trim();
          navigator.clipboard.writeText(contentWithoutThink).then(() => {
            message.success('已复制到剪贴板');
          }).catch(() => {
            message.error('复制失败');
          });
        }
        break;

      case 'like':
        message.success('感谢您的反馈 👍');
        break;

      case 'dislike':
        message.info('感谢您的反馈，我们会持续改进 🙏');
        break;

      default:
        break;
    }
  };

  // 🧠 解析消息内容，分离思考过程和正文
  const parseMessageContent = (content: string) => {
    if (typeof content !== 'string') {
      return { thinkingContent: null, mainContent: content, isThinkingComplete: false };
    }

    // 🧠 检测是否有 <think> 开始标签
    const hasThinkStart = content.includes('<think>');
    if (!hasThinkStart) {
      return { thinkingContent: null, mainContent: content, isThinkingComplete: false };
    }

    // 🧠 检测是否有完整的 <think></think> 标签对
    const completeThinkMatch = content.match(/<think>([\s\S]*?)<\/think>/);

    if (completeThinkMatch) {
      // ✅ 思考完成：提取完整的思考内容
      const thinkingContent = completeThinkMatch[1].trim();
      const mainContent = content.replace(/<think>[\s\S]*?<\/think>/g, '').trim();
      return { thinkingContent, mainContent, isThinkingComplete: true };
    } else {
      // ⏳ 思考中：提取 <think> 之后的所有内容作为思考内容
      const thinkStartIndex = content.indexOf('<think>');
      const thinkingContent = content.substring(thinkStartIndex + 7).trim(); // 7 = '<think>'.length
      return { thinkingContent, mainContent: '', isThinkingComplete: false };
    }
  };

  // 🧠 思考过程展示组件（DeepSeek 风格）
  const ThinkingBlock: React.FC<{ content: string; isStreaming?: boolean }> = ({ content, isStreaming = false }) => {
    const [collapsed, setCollapsed] = useState(false);

    return (
      <div className={styles.thinkingBlock}>
        {/* 思考过程头部 */}
        <div
          className={styles.thinkingHeader}
          onClick={() => setCollapsed(!collapsed)}
        >
          <Space size={8}>
            <span className={styles.thinkingIcon}>🧠</span>
            <span className={styles.thinkingTitle}>
              {isStreaming ? '正在思考' : '思考过程'}
            </span>
            {isStreaming && <Spin size="small" />}
          </Space>
          <DownOutlined
            style={{
              fontSize: 12,
              transform: collapsed ? 'rotate(-90deg)' : 'rotate(0deg)',
              transition: 'transform 0.2s ease',
            }}
          />
        </div>

        {/* 思考内容 */}
        {!collapsed && (
          <div className={styles.thinkingContent}>
            <MarkdownViewer
              content={content}
              enableHighlight={true}
              allowHtml={false}
            />
          </div>
        )}
      </div>
    );
  };

  // 🌟 自定义 Markdown 渲染函数（支持图表可视化 + 思考过程展示）
  const renderMarkdown: BubbleProps['messageRender'] = (content) => {
    if (typeof content !== 'string') {
      return content;
    }

    // 🧠 解析思考过程和正文
    const { thinkingContent, mainContent, isThinkingComplete } = parseMessageContent(content);

    // 🌟 检测是否包含图表代码块（vis-chart）
    const hasVisChart = /```vis-chart[\s\S]*?```/.test(mainContent);

    // 渲染正文内容
    let mainContentRender: React.ReactNode;
    if (hasVisChart) {
      // 如果包含图表，使用 GPTVis 渲染
      mainContentRender = <GPTVis>{mainContent}</GPTVis>;
    } else {
      // 否则使用 MarkdownViewer 渲染
      mainContentRender = (
        <MarkdownViewer
          content={mainContent}
          enableHighlight={true}
          allowHtml={false}
        />
      );
    }

    // 🧠 如果有思考过程，先展示思考过程，再展示正文
    if (thinkingContent) {
      return (
        <>
          <ThinkingBlock content={thinkingContent} isStreaming={!isThinkingComplete} />
          {mainContent && <div style={{ marginTop: 16 }}>{mainContentRender}</div>}
        </>
      );
    }

    return mainContentRender;
  };

  // ==================== Nodes ====================
  const chatSider = (
    <div className={`${styles.sider} ${siderCollapsed ? 'collapsed' : ''}`}>
      {/* 🌟 折叠按钮（垂直居中悬浮） */}
      <div className={styles.collapseBtnWrapper}>
        <div
          className={styles.collapseBtnFloating}
          onClick={() => setSiderCollapsed(!siderCollapsed)}
          title={siderCollapsed ? '展开' : '收起'}
        >
          {siderCollapsed ? '›' : '‹'}
        </div>
      </div>

      {/* 🌟 功能菜单 */}
      {!siderCollapsed && (
        <>
          <div className={styles.featureMenu}>
            {FEATURE_MENU_ITEMS.map((item) => (
              <div
                key={item.key}
                className={`${styles.featureMenuItem} ${activeFeature === item.key ? 'active' : ''}`}
                onClick={() => handleFeatureMenuClick(item.key)}
              >
                {item.icon}
                <span>{item.label}</span>
              </div>
            ))}
          </div>
          {/* 分隔线 */}
          <div className={styles.featureDivider} />
        </>
      )}

      {siderCollapsed && (
        <>
          <div className={styles.featureMenu}>
            {FEATURE_MENU_ITEMS.map((item) => (
              <div
                key={item.key}
                className={`${styles.featureMenuItem} ${activeFeature === item.key ? 'active' : ''}`}
                onClick={() => handleFeatureMenuClick(item.key)}
                title={item.label}
                style={{ justifyContent: 'center' }}
              >
                {item.icon}
              </div>
            ))}
          </div>
          {/* 分隔线 */}
          <div className={styles.featureDivider} />
        </>
      )}

      {/* 🌟 会话管理 */}
      {!siderCollapsed && (
        <Conversations
          items={conversations}
          className={styles.conversations}
          activeKey={curConversation}
          onActiveChange={async (val) => {
            abortController.current?.abort();
            // The abort execution will trigger an asynchronous requestFallback, which may lead to timing issues.
            // In future versions, the sessionId capability will be added to resolve this problem.
            setTimeout(() => {
              setCurConversation(val);
              setMessages(messageHistory?.[val] || []);
            }, 100);
          }}
          groupable
          styles={{ item: { padding: '0 8px' } }}
          menu={(conversation) => ({
            items: [
              {
                label: '重命名',
                key: 'rename',
                icon: <EditOutlined />,
              },
              {
                label: '删除',
                key: 'delete',
                icon: <DeleteOutlined />,
                danger: true,
                onClick: () => {
                  const newList = conversations.filter((item) => item.key !== conversation.key);
                  const newKey = newList?.[0]?.key;
                  setConversations(newList);
                  // The delete operation modifies curConversation and triggers onActiveChange, so it needs to be executed with a delay to ensure it overrides correctly at the end.
                  // This feature will be fixed in a future version.
                  setTimeout(() => {
                    if (conversation.key === curConversation) {
                      setCurConversation(newKey);
                      setMessages(messageHistory?.[newKey] || []);
                    }
                  }, 200);
                },
              },
            ],
          })}
        />
      )}

      {!siderCollapsed && (
        <div className={styles.siderFooter}>
          <Avatar size={24} />
          <Button type="text" icon={<QuestionCircleOutlined />} />
        </div>
      )}
    </div>
  );

  const chatList = (
    <div className={styles.chatList}>
      {messages?.length ? (
        /* 🌟 消息列表 */
        <Bubble.List
          items={messages?.map((i) => ({
            ...i.message,
            classNames: {
              content: i.status === 'loading' ? styles.loadingMessage : '',
            },
            // 🎨 为 AI 消息添加自定义样式
            style:
              i.message.role === 'assistant'
                ? {
                    width: '100%',
                    maxWidth: 'none',
                  }
                : undefined,
            // 🌟 loading 状态：正在加载时显示加载状态（使用 loadingRender）
            loading: i.status === 'loading',
            // 🌟 typing 打字效果：流式输出时启用，与 messageRender 完美兼容
            // typing 在内容更新时自动检测前缀并继续输出（智能打字）
            typing: i.status === 'loading' ? { step: 5, interval: 20, suffix: <>💗</> } : false,
          }))}
          style={{
            height: '100%',
            width: '90%', // 🎨 90% 宽度
            margin: '0 auto', // 🎨 居中显示
            paddingInline: '24px', // 🎨 内部左右留白
          }}
          roles={{
            assistant: {
              placement: 'start',
              // 🌟 Markdown 渲染：AI 回复使用 Markdown 渲染
              messageRender: renderMarkdown,
              // 🌟 Actions 操作按钮
              footer: (messageContent: string, info: { key?: string | number }) => (
                <Actions
                  items={actionItems}
                  onClick={(actionInfo) => handleActionsClick(actionInfo, messageContent)}
                />
              ),
              // 🌟 自定义加载状态渲染
              loadingRender: () => <Spin size="small" />,
              // 🎨 DeepSeek 风格：AI 消息无背景色，全宽显示
              styles: {
                content: {
                  background: 'transparent',
                  padding: 0,
                  width: '100%',
                  maxWidth: 'none',
                },
              },
            },
            user: {
              placement: 'end',
              // 用户消息也支持 Markdown
              messageRender: renderMarkdown,
              // 🎨 DeepSeek 风格：用户消息保持浅灰色背景
              styles: {
                content: {
                  background: token.colorFillTertiary,
                  borderRadius: token.borderRadiusLG,
                  padding: '12px 16px',
                },
              },
            },
          }}
        />
      ) : (
        <Welcome
          variant="borderless"
          icon={
            <img
              src={AiBlueIcon}
              alt="Refinex AI"
              width={48}
              height={48}
              style={{
                filter: 'drop-shadow(0 4px 12px rgba(24, 144, 255, 0.3)) drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1))',
              }}
            />
          }
          title="你好，我是 Refinex AI"
          description="基于先进的 AI 技术，为您提供智能对话和问题解决方案"
          style={{ maxWidth: '1200px', margin: '0 auto' }} // 🎨 与消息列表宽度保持一致
        />
      )}
    </div>
  );

  const senderHeader = (
    <Sender.Header
      title="上传文件"
      open={attachmentsOpen}
      onOpenChange={setAttachmentsOpen}
      styles={{ content: { padding: 0 } }}
    >
      <Attachments
        beforeUpload={() => false}
        items={attachedFiles}
        onChange={(info) => setAttachedFiles(info.fileList)}
        placeholder={(type) =>
          type === 'drop'
            ? { title: '拖放文件到此处' }
            : {
                icon: <CloudUploadOutlined />,
                title: '上传文件',
                description: '点击或拖拽文件到此区域上传',
              }
        }
      />
    </Sender.Header>
  );

  // 🌟 按照 Ant Design X 官方推荐的 Footer 布局
  const iconStyle: React.CSSProperties = {
    fontSize: 18,
    color: token.colorText,
  };

  const renderSenderFooter = ({ components }: { components: any }) => {
    const { SendButton, LoadingButton, SpeechButton } = components;

    return (
      <Flex justify="space-between" align="center">
        {/* 左侧：附件、深度思考、联网搜索 */}
        <Flex gap="small" align="center">
          <Button
            style={iconStyle}
            type="text"
            icon={<PaperClipOutlined />}
            onClick={() => setAttachmentsOpen(!attachmentsOpen)}
            title="上传附件"
          />
          <Divider type="vertical" />
          深度思考
          <Switch
            size="small"
            checked={deepThinkEnabled}
            onChange={setDeepThinkEnabled}
          />
          <Divider type="vertical" />
          <Button
            icon={<SearchOutlined />}
            type={webSearchEnabled ? 'primary' : 'default'}
            ghost={webSearchEnabled}
            onClick={() => setWebSearchEnabled(!webSearchEnabled)}
          >
            联网搜索
          </Button>
        </Flex>

        {/* 右侧：MCP、语音输入、发送按钮 */}
        <Flex align="center">
          <Button
            type="text"
            style={iconStyle}
            icon={<ApiOutlined />}
            title="MCP 服务"
          />
          <Divider type="vertical" />
          <SpeechButton style={iconStyle} />
          <Divider type="vertical" />
          {loading ? (
            <LoadingButton type="default" />
          ) : (
            <SendButton type="primary" disabled={false} />
          )}
        </Flex>
      </Flex>
    );
  };

  const chatSender = (
    <div className={styles.senderWrapper}>
      {/* 🌟 模型和知识库选择 */}
      <Flex gap={8} style={{ marginBottom: 8 }}>
        <Dropdown
          menu={{
            items: MOCK_MODELS.map(model => ({
              key: model.key,
              label: model.label,
              onClick: () => setSelectedModel(model),
            })),
          }}
          trigger={['click']}
        >
          <Button
            type="text"
            icon={<RobotOutlined />}
            style={{
              padding: '4px 12px',
              height: 'auto',
              fontSize: '13px',
              color: '#666',
            }}
          >
            {selectedModel.label}
            <DownOutlined style={{ fontSize: '10px', marginLeft: '4px' }} />
          </Button>
        </Dropdown>

        <Dropdown
          menu={{
            items: [
              {
                key: 'none',
                label: '不使用知识库',
                onClick: () => setSelectedKnowledgeBase(null),
              },
              { type: 'divider' },
              ...MOCK_KNOWLEDGE_BASES.map(kb => ({
                key: kb.key,
                label: kb.label,
                onClick: () => setSelectedKnowledgeBase(kb),
              })),
            ],
          }}
          trigger={['click']}
        >
          <Button
            type="text"
            icon={<FileSearchOutlined />}
            style={{
              padding: '4px 12px',
              height: 'auto',
              fontSize: '13px',
              color: '#666',
            }}
          >
            {selectedKnowledgeBase?.label || '知识库'}
            <DownOutlined style={{ fontSize: '10px', marginLeft: '4px' }} />
          </Button>
        </Dropdown>
      </Flex>

      {/* 🌟 快捷指令 + 输入框 */}
      <Suggestion
        items={SUGGESTION_ITEMS}
        onSelect={(itemVal) => {
          // 选中快捷指令后，设置对应的值
          const selectedLabel = SUGGESTION_ITEMS.flatMap(item =>
            item.children ? item.children : [item]
          ).find(item => item.value === itemVal)?.label || itemVal;

          setInputValue(`[${selectedLabel}]: `);
        }}
      >
        {({ onTrigger, onKeyDown }) => (
          <Sender
            value={inputValue}
            onChange={(nextVal) => {
              // 当输入 / 时触发快捷指令
              if (nextVal.endsWith('/')) {
                onTrigger();
              } else if (!nextVal) {
                onTrigger(false);
              }
              setInputValue(nextVal);
            }}
            onKeyDown={onKeyDown}
            autoSize={{ minRows: 2, maxRows: 6 }}
            placeholder="提问或输入 / 使用技能"
            header={senderHeader}
            footer={renderSenderFooter}
            actions={false}
            onSubmit={() => {
              onSubmit(inputValue);
              setInputValue('');
            }}
            onCancel={() => {
              abortController.current?.abort();
            }}
            loading={loading}
            allowSpeech
            className={styles.sender}
          />
        )}
      </Suggestion>
    </div>
  );

  useEffect(() => {
    // history mock
    if (messages?.length) {
      setMessageHistory((prev) => ({
        ...prev,
        [curConversation]: messages,
      }));
    }
  }, [messages, curConversation]);

  // ==================== Render =================
  return (
    <div className={styles.layout}>
      {chatSider}

      <div className={styles.chat}>
        {chatList}
        {chatSender}
      </div>

      {/* 🔍 搜索聊天弹窗 */}
      <Modal
        open={searchModalVisible}
        onCancel={() => {
          setSearchModalVisible(false);
          setSearchKeyword('');
          setSearchResults([]);
        }}
        footer={null}
        width={600}
        className={styles.searchModal}
        closeIcon={null}
        destroyOnHidden
      >
        {/* 搜索输入框 */}
        <div className={styles.searchModalHeader}>
          <Input
            className={styles.searchInput}
            prefix={<SearchOutlined />}
            placeholder="搜索聊天记录..."
            value={searchKeyword}
            onChange={(e) => {
              const value = e.target.value;
              setSearchKeyword(value);
              handleSearch(value);
            }}
            autoFocus
            allowClear
          />
        </div>

        {/* 搜索结果列表 */}
        <div className={styles.searchResults}>
          {searchKeyword && searchResults.length === 0 ? (
            <div className={styles.searchEmpty}>
              <FileSearchOutlined />
              <div>未找到相关聊天记录</div>
            </div>
          ) : searchResults.length > 0 ? (
            searchResults.map((result, index) => (
              <div
                key={`${result.conversationKey}-${result.messageIndex}-${index}`}
                className={styles.searchResultItem}
                onClick={() => handleSearchResultClick(result)}
              >
                <div className={styles.searchResultTitle}>
                  {result.messageRole === 'user' ? '👤 你' : '🤖 AI'}
                  <span style={{ marginLeft: 4 }}>·</span>
                  <span style={{ fontWeight: 'normal', color: token.colorTextSecondary }}>
                    {result.conversationLabel}
                  </span>
                </div>
                <div
                  className={styles.searchResultContent}
                  dangerouslySetInnerHTML={{
                    __html: highlightKeyword(
                      result.messageContent.slice(0, 200) + (result.messageContent.length > 200 ? '...' : ''),
                      searchKeyword
                    ),
                  }}
                />
                <div className={styles.searchResultTime}>{result.timestamp}</div>
              </div>
            ))
          ) : (
            <div className={styles.searchEmpty}>
              <SearchOutlined />
              <div>输入关键词搜索聊天记录</div>
            </div>
          )}
        </div>
      </Modal>
    </div>
  );
};

export default AIChatPage;


