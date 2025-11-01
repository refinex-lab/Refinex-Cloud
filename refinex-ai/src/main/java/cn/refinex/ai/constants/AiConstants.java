package cn.refinex.ai.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * AI模块通用常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AiConstants {

    /**
     * AI模型相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Model {

        // ==================== 默认配置 ====================

        /** 默认温度参数 */
        public static final Double DEFAULT_TEMPERATURE = 0.7;

        /** 默认最大Token数 */
        public static final Integer DEFAULT_MAX_TOKENS = 2000;

        /** 默认Top P参数 */
        public static final Double DEFAULT_TOP_P = 1.0;

        /** 默认频率惩罚 */
        public static final Double DEFAULT_FREQUENCY_PENALTY = 0.0;

        /** 默认存在惩罚 */
        public static final Double DEFAULT_PRESENCE_PENALTY = 0.0;

        // ==================== 限流配置 ====================

        /** 默认每分钟请求数限制（RPM） */
        public static final Integer DEFAULT_RPM_LIMIT = 60;

        /** 默认每分钟Token数限制（TPM） */
        public static final Integer DEFAULT_TPM_LIMIT = 100000;

        /** 默认请求超时时间（秒） */
        public static final Integer DEFAULT_TIMEOUT_SECONDS = 60;

        /** 默认重试次数 */
        public static final Integer DEFAULT_RETRY_TIMES = 3;

        /** 默认熔断器阈值（失败次数） */
        public static final Integer DEFAULT_CIRCUIT_BREAKER_THRESHOLD = 5;

        // ==================== 健康检查配置 ====================

        /** 健康检查间隔（秒） */
        public static final Integer HEALTH_CHECK_INTERVAL = 300;

        /** 健康检查超时时间（秒） */
        public static final Integer HEALTH_CHECK_TIMEOUT = 10;

        // ==================== 模型代码常量 ====================

        /** OpenAI GPT-4 */
        public static final String MODEL_GPT_4 = "gpt-4";

        /** OpenAI GPT-4 Turbo */
        public static final String MODEL_GPT_4_TURBO = "gpt-4-turbo";

        /** OpenAI GPT-3.5 Turbo */
        public static final String MODEL_GPT_3_5_TURBO = "gpt-3.5-turbo";

        /** Claude 3 Opus */
        public static final String MODEL_CLAUDE_3_OPUS = "claude-3-opus";

        /** Claude 3 Sonnet */
        public static final String MODEL_CLAUDE_3_SONNET = "claude-3-sonnet";

        /** 通义千问 */
        public static final String MODEL_QWEN_TURBO = "qwen-turbo";

        /** 文心一言 */
        public static final String MODEL_ERNIE_BOT = "ernie-bot";

        /** 讯飞星火 */
        public static final String MODEL_SPARK = "spark";
    }

    /**
     * AI对话相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Conversation {

        // ==================== 上下文配置 ====================

        /** 默认最大上下文消息数 */
        public static final Integer DEFAULT_MAX_CONTEXT_MESSAGES = 10;

        /** 默认上下文策略 */
        public static final String DEFAULT_CONTEXT_STRATEGY = "SLIDING_WINDOW";

        /** 默认单次请求最大Token数 */
        public static final Integer DEFAULT_MAX_TOKENS_PER_REQUEST = 4000;

        // ==================== 对话过期配置 ====================

        /** 对话默认过期时间（天） */
        public static final Integer DEFAULT_EXPIRE_DAYS = 30;

        /** 对话分享默认有效期（小时） */
        public static final Integer DEFAULT_SHARE_EXPIRE_HOURS = 24;

        // ==================== 对话标题 ====================

        /** 默认对话标题 */
        public static final String DEFAULT_CONVERSATION_TITLE = "新对话";

        /** 对话标题最大长度 */
        public static final Integer MAX_TITLE_LENGTH = 100;

        // ==================== 消息限制 ====================

        /** 单条消息最大长度（字符） */
        public static final Integer MAX_MESSAGE_LENGTH = 10000;

        /** 对话最大消息数 */
        public static final Integer MAX_MESSAGES_PER_CONVERSATION = 1000;
    }

    /**
     * AI配额相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Quota {

        // ==================== 默认配额 ====================

        /** 新用户默认Token配额 */
        public static final Long DEFAULT_TOKEN_QUOTA = 100000L;

        /** 新用户默认请求次数配额 */
        public static final Long DEFAULT_REQUEST_QUOTA = 1000L;

        /** 新用户默认金额配额（元） */
        public static final Long DEFAULT_AMOUNT_QUOTA = 100L;

        // ==================== 配额周期 ====================

        /** 日配额周期（天） */
        public static final String PERIOD_DAILY = "DAILY";

        /** 月配额周期（天） */
        public static final String PERIOD_MONTHLY = "MONTHLY";

        /** 年配额周期（天） */
        public static final String PERIOD_YEARLY = "YEARLY";

        /** 永久配额 */
        public static final String PERIOD_UNLIMITED = "UNLIMITED";

        // ==================== 预警阈值 ====================

        /** 默认预警阈值（百分比） */
        public static final Integer DEFAULT_WARNING_THRESHOLD = 80;

        /** 高优先级预警阈值（百分比） */
        public static final Integer HIGH_WARNING_THRESHOLD = 90;

        /** 严重预警阈值（百分比） */
        public static final Integer CRITICAL_WARNING_THRESHOLD = 95;

        // ==================== 配额调整原因 ====================

        /** 系统赠送 */
        public static final String ADJUST_REASON_GIFT = "系统赠送";

        /** 购买充值 */
        public static final String ADJUST_REASON_PURCHASE = "购买充值";

        /** 活动奖励 */
        public static final String ADJUST_REASON_REWARD = "活动奖励";

        /** 管理员调整 */
        public static final String ADJUST_REASON_ADMIN = "管理员调整";

        /** 系统扣减 */
        public static final String ADJUST_REASON_DEDUCT = "系统扣减";

        /** 过期清零 */
        public static final String ADJUST_REASON_EXPIRE = "过期清零";
    }

    /**
     * AI流控相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RateLimit {

        // ==================== 时间窗口 ====================

        /** 分钟窗口 */
        public static final String WINDOW_MINUTE = "minute";

        /** 小时窗口 */
        public static final String WINDOW_HOUR = "hour";

        /** 天窗口 */
        public static final String WINDOW_DAY = "day";

        // ==================== 默认限流配置 ====================

        /** 用户级别默认每分钟请求数 */
        public static final Integer USER_DEFAULT_RPM = 20;

        /** 用户级别默认每小时请求数 */
        public static final Integer USER_DEFAULT_RPH = 500;

        /** 用户级别默认每天请求数 */
        public static final Integer USER_DEFAULT_RPD = 5000;

        /** IP级别默认每分钟请求数 */
        public static final Integer IP_DEFAULT_RPM = 60;

        /** 全局默认每秒请求数 */
        public static final Integer GLOBAL_DEFAULT_RPS = 1000;

        // ==================== 流控优先级 ====================

        /** 最高优先级 */
        public static final Integer PRIORITY_HIGHEST = 9;

        /** 高优先级 */
        public static final Integer PRIORITY_HIGH = 7;

        /** 普通优先级 */
        public static final Integer PRIORITY_NORMAL = 5;

        /** 低优先级 */
        public static final Integer PRIORITY_LOW = 3;

        /** 最低优先级 */
        public static final Integer PRIORITY_LOWEST = 1;
    }

    /**
     * AI提示词相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Prompt {

        // ==================== 提示词类型 ====================

        /** 系统提示词 */
        public static final String TYPE_SYSTEM = "SYSTEM";

        /** 用户自定义提示词 */
        public static final String TYPE_USER = "USER";

        /** 官方推荐提示词 */
        public static final String TYPE_OFFICIAL = "OFFICIAL";

        // ==================== 提示词分类 ====================

        /** 写作助手 */
        public static final String CATEGORY_WRITING = "WRITING";

        /** 代码助手 */
        public static final String CATEGORY_CODING = "CODING";

        /** 翻译助手 */
        public static final String CATEGORY_TRANSLATION = "TRANSLATION";

        /** 数据分析 */
        public static final String CATEGORY_ANALYSIS = "ANALYSIS";

        /** 创意生成 */
        public static final String CATEGORY_CREATIVE = "CREATIVE";

        /** 教育学习 */
        public static final String CATEGORY_EDUCATION = "EDUCATION";

        /** 商业咨询 */
        public static final String CATEGORY_BUSINESS = "BUSINESS";

        /** 其他 */
        public static final String CATEGORY_OTHER = "OTHER";

        // ==================== 提示词长度限制 ====================

        /** 提示词标题最大长度 */
        public static final Integer MAX_TITLE_LENGTH = 100;

        /** 提示词内容最大长度 */
        public static final Integer MAX_CONTENT_LENGTH = 5000;

        /** 提示词描述最大长度 */
        public static final Integer MAX_DESCRIPTION_LENGTH = 500;

        // ==================== 提示词变量 ====================

        /** 变量前缀 */
        public static final String VARIABLE_PREFIX = "{{";

        /** 变量后缀 */
        public static final String VARIABLE_SUFFIX = "}}";

        /** 变量正则表达式 */
        public static final String VARIABLE_PATTERN = "\\{\\{([^}]+)\\}\\}";
    }

    /**
     * AI向量存储相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Vector {

        // ==================== 分块配置 ====================

        /** 默认分块大小（字符数） */
        public static final Integer DEFAULT_CHUNK_SIZE = 1000;

        /** 默认分块重叠大小（字符数） */
        public static final Integer DEFAULT_CHUNK_OVERLAP = 200;

        /** 最小分块大小 */
        public static final Integer MIN_CHUNK_SIZE = 100;

        /** 最大分块大小 */
        public static final Integer MAX_CHUNK_SIZE = 5000;

        // ==================== 向量维度 ====================

        /** OpenAI text-embedding-ada-002 向量维度 */
        public static final Integer OPENAI_EMBEDDING_DIMENSION = 1536;

        /** OpenAI text-embedding-3-small 向量维度 */
        public static final Integer OPENAI_EMBEDDING_3_SMALL_DIMENSION = 1536;

        /** OpenAI text-embedding-3-large 向量维度 */
        public static final Integer OPENAI_EMBEDDING_3_LARGE_DIMENSION = 3072;

        /** 通用向量维度 */
        public static final Integer DEFAULT_VECTOR_DIMENSION = 1536;

        // ==================== 相似度配置 ====================

        /** 默认相似度阈值 */
        public static final Double DEFAULT_SIMILARITY_THRESHOLD = 0.7;

        /** 高相似度阈值 */
        public static final Double HIGH_SIMILARITY_THRESHOLD = 0.85;

        /** 低相似度阈值 */
        public static final Double LOW_SIMILARITY_THRESHOLD = 0.5;

        /** 默认检索Top K数量 */
        public static final Integer DEFAULT_TOP_K = 5;

        /** 最大检索Top K数量 */
        public static final Integer MAX_TOP_K = 50;

        // ==================== 同步状态 ====================

        /** 待同步 */
        public static final String SYNC_STATUS_PENDING = "PENDING";

        /** 同步中 */
        public static final String SYNC_STATUS_SYNCING = "SYNCING";

        /** 已同步 */
        public static final String SYNC_STATUS_SYNCED = "SYNCED";

        /** 同步失败 */
        public static final String SYNC_STATUS_FAILED = "FAILED";
    }

    /**
     * AI任务相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Task {

        // ==================== 任务队列 ====================

        /** 高优先级队列 */
        public static final String QUEUE_HIGH = "ai_task_high";

        /** 普通优先级队列 */
        public static final String QUEUE_NORMAL = "ai_task_normal";

        /** 低优先级队列 */
        public static final String QUEUE_LOW = "ai_task_low";

        // ==================== 任务类型 ====================

        /** 文本生成任务 */
        public static final String TYPE_TEXT_GENERATION = "TEXT_GENERATION";

        /** 图像生成任务 */
        public static final String TYPE_IMAGE_GENERATION = "IMAGE_GENERATION";

        /** 视频生成任务 */
        public static final String TYPE_VIDEO_GENERATION = "VIDEO_GENERATION";

        /** 音频生成任务 */
        public static final String TYPE_AUDIO_GENERATION = "AUDIO_GENERATION";

        /** 批量处理任务 */
        public static final String TYPE_BATCH_PROCESSING = "BATCH_PROCESSING";

        // ==================== 任务配置 ====================

        /** 默认最大重试次数 */
        public static final Integer DEFAULT_MAX_RETRY = 3;

        /** 任务超时时间（秒） */
        public static final Integer DEFAULT_TASK_TIMEOUT = 300;

        /** 长任务超时时间（秒） */
        public static final Integer LONG_TASK_TIMEOUT = 1800;

        // ==================== 回调状态 ====================

        /** 待回调 */
        public static final String CALLBACK_PENDING = "PENDING";

        /** 回调成功 */
        public static final String CALLBACK_SUCCESS = "SUCCESS";

        /** 回调失败 */
        public static final String CALLBACK_FAILED = "FAILED";
    }

    /**
     * AI消费相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Consumption {

        // ==================== 消费类型 ====================

        /** 对话消费 */
        public static final String TYPE_CONVERSATION = "CONVERSATION";

        /** 图像生成消费 */
        public static final String TYPE_IMAGE_GENERATION = "IMAGE_GENERATION";

        /** 语音识别消费 */
        public static final String TYPE_SPEECH_TO_TEXT = "SPEECH_TO_TEXT";

        /** 语音合成消费 */
        public static final String TYPE_TEXT_TO_SPEECH = "TEXT_TO_SPEECH";

        /** 向量化消费 */
        public static final String TYPE_EMBEDDING = "EMBEDDING";

        // ==================== 异常消费阈值 ====================

        /** 异常Token消耗阈值 */
        public static final Integer ABNORMAL_TOKEN_THRESHOLD = 50000;

        /** 异常金额阈值（元） */
        public static final Double ABNORMAL_AMOUNT_THRESHOLD = 100.0;

        /** 异常响应时间阈值（毫秒） */
        public static final Integer ABNORMAL_RESPONSE_TIME_THRESHOLD = 30000;

        // ==================== Token价格（元/1K tokens）====================

        /** GPT-4 输入价格 */
        public static final Double PRICE_GPT4_INPUT = 0.03;

        /** GPT-4 输出价格 */
        public static final Double PRICE_GPT4_OUTPUT = 0.06;

        /** GPT-3.5 输入价格 */
        public static final Double PRICE_GPT35_INPUT = 0.0015;

        /** GPT-3.5 输出价格 */
        public static final Double PRICE_GPT35_OUTPUT = 0.002;
    }

    /**
     * AI审核相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Moderation {

        // ==================== 审核状态 ====================

        /** 待审核 */
        public static final String STATUS_PENDING = "PENDING";

        /** 审核通过 */
        public static final String STATUS_APPROVED = "APPROVED";

        /** 审核拒绝 */
        public static final String STATUS_REJECTED = "REJECTED";

        /** 人工审核 */
        public static final String STATUS_MANUAL_REVIEW = "MANUAL_REVIEW";

        // ==================== 审核类型 ====================

        /** 内容安全审核 */
        public static final String TYPE_CONTENT_SAFETY = "CONTENT_SAFETY";

        /** 敏感词审核 */
        public static final String TYPE_SENSITIVE_WORDS = "SENSITIVE_WORDS";

        /** 图片审核 */
        public static final String TYPE_IMAGE = "IMAGE";

        // ==================== 违规类型 ====================

        /** 色情内容 */
        public static final String VIOLATION_SEXUAL = "SEXUAL";

        /** 暴力内容 */
        public static final String VIOLATION_VIOLENCE = "VIOLENCE";

        /** 仇恨言论 */
        public static final String VIOLATION_HATE = "HATE";

        /** 自残内容 */
        public static final String VIOLATION_SELF_HARM = "SELF_HARM";
    }

    /**
     * AI系统配置相关常量
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class System {

        // ==================== 系统角色 ====================

        /** 系统管理员角色 */
        public static final String ROLE_ADMIN = "ADMIN";

        /** 普通用户角色 */
        public static final String ROLE_USER = "USER";

        /** VIP用户角色 */
        public static final String ROLE_VIP = "VIP";

        // ==================== 日志级别 ====================

        /** 调试日志 */
        public static final String LOG_LEVEL_DEBUG = "DEBUG";

        /** 信息日志 */
        public static final String LOG_LEVEL_INFO = "INFO";

        /** 警告日志 */
        public static final String LOG_LEVEL_WARN = "WARN";

        /** 错误日志 */
        public static final String LOG_LEVEL_ERROR = "ERROR";

        // ==================== 功能开关 ====================

        /** 启用流式输出 */
        public static final String FEATURE_STREAMING = "streaming";

        /** 启用内容审核 */
        public static final String FEATURE_MODERATION = "moderation";

        /** 启用向量检索 */
        public static final String FEATURE_VECTOR_SEARCH = "vector_search";

        /** 启用配额限制 */
        public static final String FEATURE_QUOTA_LIMIT = "quota_limit";
    }
}

