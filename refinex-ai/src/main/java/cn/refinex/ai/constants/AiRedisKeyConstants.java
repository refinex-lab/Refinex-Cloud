package cn.refinex.ai.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * AI模块 Redis 缓存键常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AiRedisKeyConstants {

    /**
     * AI模型配置相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Model {

        // ==================== 前缀定义 ====================

        /** AI模型缓存键前缀 */
        private static final String MODEL_PREFIX = "ai:model:";

        // ==================== 缓存过期时间 ====================

        /** 模型配置缓存过期时间（秒）- 1 小时 */
        public static final long MODEL_CONFIG_CACHE_TTL = 3600L;

        /** 模型列表缓存过期时间（秒）- 30 分钟 */
        public static final long MODEL_LIST_CACHE_TTL = 1800L;

        /** 模型健康状态缓存过期时间（秒）- 5 分钟 */
        public static final long MODEL_HEALTH_CACHE_TTL = 300L;

        // ==================== 模型配置缓存键 ====================

        /**
         * 模型配置缓存键（按模型代码）
         * <p>
         * Redis数据类型：String（JSON格式的模型配置信息）
         * 缓存内容：模型详细配置，包括API密钥、端点、限流等
         * 失效时间：3600秒（1小时）
         * </p>
         *
         * @param modelCode 模型代码
         * @return 缓存键，格式：ai:model:config:{modelCode}
         */
        public static String modelConfig(String modelCode) {
            return MODEL_PREFIX + "config:" + modelCode;
        }

        /**
         * 启用的模型列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的模型列表）
         * 缓存内容：所有启用状态的模型列表
         * 失效时间：1800秒（30分钟）
         * </p>
         *
         * @return 缓存键，格式：ai:model:enabled_list
         */
        public static String enabledModelList() {
            return MODEL_PREFIX + "enabled_list";
        }

        /**
         * 模型健康状态缓存键
         * <p>
         * Redis数据类型：String（健康状态枚举值）
         * 缓存内容：模型的健康检查结果
         * 失效时间：300秒（5分钟）
         * </p>
         *
         * @param modelCode 模型代码
         * @return 缓存键，格式：ai:model:health:{modelCode}
         */
        public static String modelHealth(String modelCode) {
            return MODEL_PREFIX + "health:" + modelCode;
        }

        /**
         * 模型统计数据缓存键
         * <p>
         * Redis数据类型：String（JSON格式的统计数据）
         * 缓存内容：模型的使用统计（请求数、Token消耗等）
         * 失效时间：600秒（10分钟）
         * </p>
         *
         * @param modelCode 模型代码
         * @param date 日期（格式：yyyyMMdd）
         * @return 缓存键，格式：ai:model:stats:{modelCode}:{date}
         */
        public static String modelStatistics(String modelCode, String date) {
            return MODEL_PREFIX + "stats:" + modelCode + ":" + date;
        }
    }

    /**
     * AI对话会话相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Conversation {

        // ==================== 前缀定义 ====================

        /** AI对话缓存键前缀 */
        private static final String CONVERSATION_PREFIX = "ai:conversation:";

        // ==================== 缓存过期时间 ====================

        /** 对话详情缓存过期时间（秒）- 30 分钟 */
        public static final long CONVERSATION_CACHE_TTL = 1800L;

        /** 对话列表缓存过期时间（秒）- 10 分钟 */
        public static final long CONVERSATION_LIST_CACHE_TTL = 600L;

        /** 对话上下文缓存过期时间（秒）- 1 小时 */
        public static final long CONVERSATION_CONTEXT_CACHE_TTL = 3600L;

        // ==================== 对话缓存键 ====================

        /**
         * 对话详情缓存键
         * <p>
         * Redis数据类型：String（JSON格式的对话信息）
         * 缓存内容：对话的详细信息
         * 失效时间：1800秒（30分钟）
         * </p>
         *
         * @param conversationId 对话ID
         * @return 缓存键，格式：ai:conversation:detail:{conversationId}
         */
        public static String conversationDetail(Long conversationId) {
            return CONVERSATION_PREFIX + "detail:" + conversationId;
        }

        /**
         * 用户对话列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的对话列表）
         * 缓存内容：用户的对话列表
         * 失效时间：600秒（10分钟）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：ai:conversation:user_list:{userId}
         */
        public static String userConversationList(Long userId) {
            return CONVERSATION_PREFIX + "user_list:" + userId;
        }

        /**
         * 对话上下文缓存键
         * <p>
         * Redis数据类型：String（JSON格式的消息列表）
         * 缓存内容：对话的上下文消息列表
         * 失效时间：3600秒（1小时）
         * </p>
         *
         * @param conversationId 对话ID
         * @return 缓存键，格式：ai:conversation:context:{conversationId}
         */
        public static String conversationContext(Long conversationId) {
            return CONVERSATION_PREFIX + "context:" + conversationId;
        }

        /**
         * 对话分享令牌缓存键
         * <p>
         * Redis数据类型：String（对话ID）
         * 缓存内容：分享令牌对应的对话ID
         * 失效时间：根据分享过期时间动态设置
         * </p>
         *
         * @param shareToken 分享令牌
         * @return 缓存键，格式：ai:conversation:share:{shareToken}
         */
        public static String conversationShare(String shareToken) {
            return CONVERSATION_PREFIX + "share:" + shareToken;
        }
    }

    /**
     * AI配额相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Quota {

        // ==================== 前缀定义 ====================

        /** AI配额缓存键前缀 */
        private static final String QUOTA_PREFIX = "ai:quota:";

        // ==================== 缓存过期时间 ====================

        /** 配额信息缓存过期时间（秒）- 10 分钟 */
        public static final long QUOTA_CACHE_TTL = 600L;

        /** 配额使用量缓存过期时间（秒）- 5 分钟 */
        public static final long QUOTA_USAGE_CACHE_TTL = 300L;

        // ==================== 配额缓存键 ====================

        /**
         * 用户配额信息缓存键
         * <p>
         * Redis数据类型：String（JSON格式的配额信息）
         * 缓存内容：用户的配额详细信息
         * 失效时间：600秒（10分钟）
         * </p>
         *
         * @param userId 用户ID
         * @param quotaType 配额类型
         * @return 缓存键，格式：ai:quota:user:{userId}:{quotaType}
         */
        public static String userQuota(Long userId, String quotaType) {
            return QUOTA_PREFIX + "user:" + userId + ":" + quotaType;
        }

        /**
         * 用户配额使用量缓存键（实时计数）
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：用户当前周期的配额使用量
         * 失效时间：300秒（5分钟）
         * </p>
         *
         * @param userId 用户ID
         * @param quotaType 配额类型
         * @return 缓存键，格式：ai:quota:usage:{userId}:{quotaType}
         */
        public static String quotaUsage(Long userId, String quotaType) {
            return QUOTA_PREFIX + "usage:" + userId + ":" + quotaType;
        }

        /**
         * 配额预警标记缓存键
         * <p>
         * Redis数据类型：String（布尔值）
         * 缓存内容：是否已发送配额预警
         * 失效时间：86400秒（24小时）
         * </p>
         *
         * @param userId 用户ID
         * @param quotaType 配额类型
         * @return 缓存键，格式：ai:quota:warned:{userId}:{quotaType}
         */
        public static String quotaWarned(Long userId, String quotaType) {
            return QUOTA_PREFIX + "warned:" + userId + ":" + quotaType;
        }
    }

    /**
     * AI流控相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class RateLimit {

        // ==================== 前缀定义 ====================

        /** AI流控缓存键前缀 */
        private static final String RATE_LIMIT_PREFIX = "ai:rate_limit:";

        // ==================== 流控窗口时间 ====================

        /** 每分钟流控窗口（秒） */
        public static final long RATE_LIMIT_WINDOW_MINUTE = 60L;

        /** 每小时流控窗口（秒） */
        public static final long RATE_LIMIT_WINDOW_HOUR = 3600L;

        /** 每天流控窗口（秒） */
        public static final long RATE_LIMIT_WINDOW_DAY = 86400L;

        // ==================== 流控缓存键 ====================

        /**
         * 用户级别流控计数器
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：用户在时间窗口内的请求次数
         * 失效时间：根据窗口类型动态设置
         * </p>
         *
         * @param userId 用户ID
         * @param window 时间窗口（minute/hour/day）
         * @return 缓存键，格式：ai:rate_limit:user:{userId}:{window}
         */
        public static String userRateLimit(Long userId, String window) {
            return RATE_LIMIT_PREFIX + "user:" + userId + ":" + window;
        }

        /**
         * 模型级别流控计数器
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：模型在时间窗口内的请求次数
         * 失效时间：根据窗口类型动态设置
         * </p>
         *
         * @param modelCode 模型代码
         * @param window 时间窗口（minute/hour/day）
         * @return 缓存键，格式：ai:rate_limit:model:{modelCode}:{window}
         */
        public static String modelRateLimit(String modelCode, String window) {
            return RATE_LIMIT_PREFIX + "model:" + modelCode + ":" + window;
        }

        /**
         * IP级别流控计数器
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：IP在时间窗口内的请求次数
         * 失效时间：根据窗口类型动态设置
         * </p>
         *
         * @param ip IP地址
         * @param window 时间窗口（minute/hour/day）
         * @return 缓存键，格式：ai:rate_limit:ip:{ip}:{window}
         */
        public static String ipRateLimit(String ip, String window) {
            return RATE_LIMIT_PREFIX + "ip:" + ip + ":" + window;
        }

        /**
         * 全局流控计数器
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：系统在时间窗口内的总请求次数
         * 失效时间：根据窗口类型动态设置
         * </p>
         *
         * @param window 时间窗口（minute/hour/day）
         * @return 缓存键，格式：ai:rate_limit:global:{window}
         */
        public static String globalRateLimit(String window) {
            return RATE_LIMIT_PREFIX + "global:" + window;
        }

        /**
         * Token消耗流控计数器（TPM - Tokens Per Minute）
         * <p>
         * Redis数据类型：String（数值）
         * 缓存内容：用户在时间窗口内消耗的Token数量
         * 失效时间：60秒（1分钟）
         * </p>
         *
         * @param userId 用户ID
         * @param modelCode 模型代码
         * @return 缓存键，格式：ai:rate_limit:tpm:{userId}:{modelCode}
         */
        public static String tokenRateLimit(Long userId, String modelCode) {
            return RATE_LIMIT_PREFIX + "tpm:" + userId + ":" + modelCode;
        }
    }

    /**
     * AI提示词模板相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Prompt {

        // ==================== 前缀定义 ====================

        /** AI提示词缓存键前缀 */
        private static final String PROMPT_PREFIX = "ai:prompt:";

        // ==================== 缓存过期时间 ====================

        /** 提示词模板缓存过期时间（秒）- 1 小时 */
        public static final long PROMPT_TEMPLATE_CACHE_TTL = 3600L;

        /** 提示词列表缓存过期时间（秒）- 30 分钟 */
        public static final long PROMPT_LIST_CACHE_TTL = 1800L;

        // ==================== 提示词缓存键 ====================

        /**
         * 提示词模板缓存键（按模板代码）
         * <p>
         * Redis数据类型：String（JSON格式的模板信息）
         * 缓存内容：提示词模板详细信息
         * 失效时间：3600秒（1小时）
         * </p>
         *
         * @param templateCode 模板代码
         * @return 缓存键，格式：ai:prompt:template:{templateCode}
         */
        public static String promptTemplate(String templateCode) {
            return PROMPT_PREFIX + "template:" + templateCode;
        }

        /**
         * 分类提示词列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的模板列表）
         * 缓存内容：指定分类下的提示词模板列表
         * 失效时间：1800秒（30分钟）
         * </p>
         *
         * @param category 分类
         * @return 缓存键，格式：ai:prompt:category:{category}
         */
        public static String promptsByCategory(String category) {
            return PROMPT_PREFIX + "category:" + category;
        }

        /**
         * 热门提示词列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的模板列表）
         * 缓存内容：使用次数最多的提示词模板列表
         * 失效时间：1800秒（30分钟）
         * </p>
         *
         * @return 缓存键，格式：ai:prompt:hot_list
         */
        public static String hotPrompts() {
            return PROMPT_PREFIX + "hot_list";
        }
    }

    /**
     * AI向量存储相关缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Vector {

        // ==================== 前缀定义 ====================

        /** AI向量缓存键前缀 */
        private static final String VECTOR_PREFIX = "ai:vector:";

        // ==================== 缓存过期时间 ====================

        /** 向量数据库配置缓存过期时间（秒）- 1 小时 */
        public static final long VECTOR_DB_CONFIG_CACHE_TTL = 3600L;

        /** 向量检索结果缓存过期时间（秒）- 10 分钟 */
        public static final long VECTOR_SEARCH_CACHE_TTL = 600L;

        // ==================== 向量缓存键 ====================

        /**
         * 向量数据库配置缓存键
         * <p>
         * Redis数据类型：String（JSON格式的配置信息）
         * 缓存内容：向量数据库的连接配置
         * 失效时间：3600秒（1小时）
         * </p>
         *
         * @param dbType 数据库类型
         * @return 缓存键，格式：ai:vector:db_config:{dbType}
         */
        public static String vectorDbConfig(String dbType) {
            return VECTOR_PREFIX + "db_config:" + dbType;
        }

        /**
         * 向量检索结果缓存键
         * <p>
         * Redis数据类型：String（JSON格式的检索结果）
         * 缓存内容：向量相似度检索的结果
         * 失效时间：600秒（10分钟）
         * </p>
         *
         * @param queryHash 查询向量的哈希值
         * @param topK 返回结果数量
         * @return 缓存键，格式：ai:vector:search:{queryHash}:{topK}
         */
        public static String vectorSearchResult(String queryHash, Integer topK) {
            return VECTOR_PREFIX + "search:" + queryHash + ":" + topK;
        }

        /**
         * 文档分块缓存键
         * <p>
         * Redis数据类型：String（JSON格式的分块列表）
         * 缓存内容：文档的分块结果
         * 失效时间：1800秒（30分钟）
         * </p>
         *
         * @param documentId 文档ID
         * @return 缓存键，格式：ai:vector:chunks:{documentId}
         */
        public static String documentChunks(Long documentId) {
            return VECTOR_PREFIX + "chunks:" + documentId;
        }
    }

    /**
     * AI分布式锁相关键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Lock {

        // ==================== 前缀定义 ====================

        /** AI分布式锁键前缀 */
        private static final String LOCK_PREFIX = "ai:lock:";

        // ==================== 锁过期时间 ====================

        /** 默认锁过期时间（秒）- 10 秒 */
        public static final long DEFAULT_LOCK_TTL = 10L;

        /** 长时间锁过期时间（秒）- 60 秒 */
        public static final long LONG_LOCK_TTL = 60L;

        // ==================== 分布式锁键 ====================

        /**
         * 对话操作锁
         * <p>
         * 用途：防止并发修改同一对话
         * 失效时间：10秒
         * </p>
         *
         * @param conversationId 对话ID
         * @return 锁键，格式：ai:lock:conversation:{conversationId}
         */
        public static String conversationLock(Long conversationId) {
            return LOCK_PREFIX + "conversation:" + conversationId;
        }

        /**
         * 配额扣减锁
         * <p>
         * 用途：防止配额并发扣减导致超额
         * 失效时间：10秒
         * </p>
         *
         * @param userId 用户ID
         * @param quotaType 配额类型
         * @return 锁键，格式：ai:lock:quota:{userId}:{quotaType}
         */
        public static String quotaLock(Long userId, String quotaType) {
            return LOCK_PREFIX + "quota:" + userId + ":" + quotaType;
        }

        /**
         * 模型健康检查锁
         * <p>
         * 用途：防止并发健康检查
         * 失效时间：60秒
         * </p>
         *
         * @param modelCode 模型代码
         * @return 锁键，格式：ai:lock:health_check:{modelCode}
         */
        public static String healthCheckLock(String modelCode) {
            return LOCK_PREFIX + "health_check:" + modelCode;
        }

        /**
         * 向量同步锁
         * <p>
         * 用途：防止向量数据并发同步
         * 失效时间：60秒
         * </p>
         *
         * @param documentId 文档ID
         * @return 锁键，格式：ai:lock:vector_sync:{documentId}
         */
        public static String vectorSyncLock(Long documentId) {
            return LOCK_PREFIX + "vector_sync:" + documentId;
        }
    }
}

