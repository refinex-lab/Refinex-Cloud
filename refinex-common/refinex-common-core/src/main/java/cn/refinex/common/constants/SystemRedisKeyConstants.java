package cn.refinex.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 全局 Redis 键常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SystemRedisKeyConstants {

    /**
     * 权限系统相关缓存键
     * <p>
     * 包含用户角色、角色权限、权限资源、菜单等权限相关数据的缓存键定义
     * </p>
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Permission {

        // ==================== 前缀定义 ====================

        /** 权限系统缓存键前缀 */
        private static final String PERMISSION_PREFIX = "permission:";

        /** 用户相关缓存键前缀 */
        private static final String USER_PREFIX = PERMISSION_PREFIX + "user:";

        /** 角色相关缓存键前缀 */
        private static final String ROLE_PREFIX = PERMISSION_PREFIX + "role:";

        /** 权限资源相关缓存键前缀 */
        private static final String RESOURCE_PREFIX = PERMISSION_PREFIX + "resource:";

        /** 菜单相关缓存键前缀 */
        private static final String MENU_PREFIX = PERMISSION_PREFIX + "menu:";

        // ==================== 用户角色权限缓存键 ====================

        /**
         * 用户角色列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的角色编码列表）
         * 缓存内容：用户拥有的所有角色编码列表
         * 建议失效时间：1800秒（30分钟）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:roles:{userId}
         */
        public static String userRoles(Long userId) {
            return USER_PREFIX + "roles:" + userId;
        }

        /**
         * 用户权限列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的权限编码列表）
         * 缓存内容：用户通过所有角色获得的权限编码列表
         * 建议失效时间：1800秒（30分钟）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:permissions:{userId}
         */
        public static String userPermissions(Long userId) {
            return USER_PREFIX + "permissions:" + userId;
        }

        /**
         * 用户菜单列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的菜单树结构）
         * 缓存内容：用户可访问的菜单树形结构数据
         * 建议失效时间：3600秒（1小时）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:menus:{userId}
         */
        public static String userMenus(Long userId) {
            return USER_PREFIX + "menus:" + userId;
        }

        // ==================== 角色权限映射缓存键 ====================

        /**
         * 角色权限映射缓存键
         * <p>
         * Redis数据类型：String（JSON格式的权限编码列表）
         * 缓存内容：角色拥有的所有权限编码列表
         * 建议失效时间：7200秒（2小时）
         * </p>
         *
         * @param roleId 角色ID
         * @return 缓存键，格式：permission:role:permissions:{roleId}
         */
        public static String rolePermissions(Long roleId) {
            return ROLE_PREFIX + "permissions:" + roleId;
        }

        /**
         * 角色详情缓存键
         * <p>
         * Redis数据类型：String（JSON格式的角色信息）
         * 缓存内容：角色详细信息，包括角色编码、名称、类型等
         * 建议失效时间：7200秒（2小时）
         * </p>
         *
         * @param roleId 角色ID
         * @return 缓存键，格式：permission:role:detail:{roleId}
         */
        public static String roleDetail(Long roleId) {
            return ROLE_PREFIX + "detail:" + roleId;
        }

        /**
         * 角色编码映射缓存键
         * <p>
         * Redis数据类型：String
         * 缓存内容：角色编码到角色ID的映射关系
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param roleCode 角色编码
         * @return 缓存键，格式：permission:role:code_to_id:{roleCode}
         */
        public static String roleCodeToId(String roleCode) {
            return ROLE_PREFIX + "code_to_id:" + roleCode;
        }

        // ==================== 权限资源缓存键 ====================

        /**
         * 权限详情缓存键
         * <p>
         * Redis数据类型：String（JSON格式的权限信息）
         * 缓存内容：权限详细信息，包括权限编码、名称、类型、资源路径等
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param permissionId 权限ID
         * @return 缓存键，格式：permission:resource:detail:{permissionId}
         */
        public static String permissionDetail(Long permissionId) {
            return RESOURCE_PREFIX + "detail:" + permissionId;
        }

        /**
         * 权限编码映射缓存键
         * <p>
         * Redis数据类型：String
         * 缓存内容：权限编码到权限ID的映射关系
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param permissionCode 权限编码
         * @return 缓存键，格式：permission:resource:code_to_id:{permissionCode}
         */
        public static String permissionCodeToId(String permissionCode) {
            return RESOURCE_PREFIX + "code_to_id:" + permissionCode;
        }

        /**
         * 权限树缓存键
         * <p>
         * Redis数据类型：String（JSON格式的权限树结构）
         * 缓存内容：完整的权限树形结构数据，支持父子关系查询
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @return 缓存键，格式：permission:resource:tree
         */
        public static String permissionTree() {
            return RESOURCE_PREFIX + "tree";
        }

        /**
         * 模块权限列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的权限列表）
         * 缓存内容：指定模块的所有权限列表
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param moduleName 模块名称
         * @return 缓存键，格式：permission:resource:module:{moduleName}
         */
        public static String modulePermissions(String moduleName) {
            return RESOURCE_PREFIX + "module:" + moduleName;
        }

        // ==================== 菜单权限缓存键 ====================

        /**
         * 菜单详情缓存键
         * <p>
         * Redis数据类型：String（JSON格式的菜单信息）
         * 缓存内容：菜单详细信息，包括菜单名称、路由路径、组件路径等
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param menuId 菜单ID
         * @return 缓存键，格式：permission:menu:detail:{menuId}
         */
        public static String menuDetail(Long menuId) {
            return MENU_PREFIX + "detail:" + menuId;
        }

        /**
         * 菜单树缓存键
         * <p>
         * Redis数据类型：String（JSON格式的菜单树结构）
         * 缓存内容：完整的菜单树形结构数据，支持父子关系查询
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @return 缓存键，格式：permission:menu:tree
         */
        public static String menuTree() {
            return MENU_PREFIX + "tree";
        }

        /**
         * 用户可访问路由缓存键
         * <p>
         * Redis数据类型：String（JSON格式的路由列表）
         * 缓存内容：用户可访问的所有前端路由信息
         * 建议失效时间：3600秒（1小时）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:routes:{userId}
         */
        public static String userRoutes(Long userId) {
            return USER_PREFIX + "routes:" + userId;
        }

        /**
         * 用户按钮权限缓存键
         * <p>
         * Redis数据类型：String（JSON格式的按钮权限列表）
         * 缓存内容：用户拥有的所有按钮权限编码列表
         * 建议失效时间：1800秒（30分钟）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:buttons:{userId}
         */
        public static String userButtons(Long userId) {
            return USER_PREFIX + "buttons:" + userId;
        }

        // ==================== 权限验证缓存键 ====================

        /**
         * 用户API权限验证缓存键
         * <p>
         * Redis数据类型：String（JSON格式的API权限映射）
         * 缓存内容：用户对特定API的访问权限验证结果
         * 建议失效时间：900秒（15分钟）
         * </p>
         *
         * @param userId 用户ID
         * @param apiPath API路径
         * @return 缓存键，格式：permission:user:api_check:{userId}:{apiPath}
         */
        public static String userApiPermission(Long userId, String apiPath) {
            return USER_PREFIX + "api_check:" + userId + ":" + apiPath;
        }

        /**
         * 用户资源访问权限缓存键
         * <p>
         * Redis数据类型：String（布尔值或权限级别）
         * 缓存内容：用户对特定资源的访问权限验证结果
         * 建议失效时间：900秒（15分钟）
         * </p>
         *
         * @param userId 用户ID
         * @param resourceType 资源类型
         * @param resourceId 资源ID
         * @return 缓存键，格式：permission:user:resource_check:{userId}:{resourceType}:{resourceId}
         */
        public static String userResourcePermission(Long userId, String resourceType, String resourceId) {
            return USER_PREFIX + "resource_check:" + userId + ":" + resourceType + ":" + resourceId;
        }

        // ==================== 权限配置缓存键 ====================

        /**
         * 权限系统配置缓存键
         * <p>
         * Redis数据类型：String（JSON格式的配置信息）
         * 缓存内容：权限系统的全局配置，如超级管理员角色、默认权限等
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @return 缓存键，格式：permission:config
         */
        public static String permissionConfig() {
            return PERMISSION_PREFIX + "config";
        }

        /**
         * 用户权限版本缓存键
         * <p>
         * Redis数据类型：String（版本号字符串）
         * 缓存内容：用户权限数据的版本号，用于缓存失效判断
         * 建议失效时间：86400秒（24小时）
         * </p>
         *
         * @param userId 用户ID
         * @return 缓存键，格式：permission:user:version:{userId}
         */
        public static String userPermissionVersion(Long userId) {
            return USER_PREFIX + "version:" + userId;
        }
    }

    /**
     * 字典模块缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Dictionary {

        // ==================== 前缀定义 ====================

        /** 字典模块缓存键前缀 */
        private static final String DICT_PREFIX = "dict:";

        /** 字典类型缓存 Key 前缀 */
        private static final String DICT_TYPE_PREFIX = DICT_PREFIX + "type:";

        /** 字典数据缓存 Key 前缀 */
        private static final String DICT_DATA_PREFIX = DICT_PREFIX + "data:";

        /** 字典锁 Key 前缀 */
        private static final String DICT_LOCK_PREFIX = DICT_PREFIX + "lock:";

        // ==================== 缓存过期时间 ====================

        /**
         * 字典类型缓存过期时间（秒）- 1 小时
         * <p>字典类型变更频率低，可以设置较长的过期时间</p>
         */
        public static final long DICT_TYPE_CACHE_TTL = 3600L;

        /**
         * 字典数据列表缓存过期时间（秒）- 30 分钟
         * <p>字典数据是热点数据，设置适中的过期时间</p>
         */
        public static final long DICT_DATA_LIST_CACHE_TTL = 1800L;

        /**
         * 字典详情缓存过期时间（秒）- 30 分钟
         * <p>单个字典数据详情的缓存时间</p>
         */
        public static final long DICT_DATA_DETAIL_CACHE_TTL = 1800L;

        /**
         * 启用的字典类型列表缓存过期时间（秒）- 1 小时
         * <p>启用的字典类型列表变更频率低</p>
         */
        public static final long DICT_ENABLED_TYPES_CACHE_TTL = 3600L;

        /**
         * 分页查询缓存过期时间（秒）- 3 分钟
         * <p>分页查询结果缓存时间较短，避免数据不一致</p>
         */
        public static final long DICT_PAGE_QUERY_CACHE_TTL = 180L;

        /**
         * 空值缓存过期时间（秒）- 5 分钟
         * <p>防止缓存穿透，对不存在的数据缓存空值，但设置较短的过期时间</p>
         */
        public static final long DICT_NULL_CACHE_TTL = 300L;

        /**
         * 缓存随机偏移量最大值（秒）- 300 秒（5 分钟）
         * <p>防止缓存雪崩，在基础 TTL 上增加随机偏移量</p>
         */
        public static final long DICT_CACHE_TTL_OFFSET = 300L;

        // ==================== 字典类型缓存键 ====================

        /**
         * 字典类型缓存键（按编码）
         * <p>
         * Redis数据类型：String（JSON格式的字典类型信息）
         * 缓存内容：字典类型详细信息
         * 失效时间：3600秒（1小时）+ 随机偏移
         * </p>
         *
         * @param dictCode 字典编码
         * @return 缓存键，格式：dict:type:code:{dictCode}
         */
        public static String dictTypeByCode(String dictCode) {
            return DICT_TYPE_PREFIX + "code:" + dictCode;
        }

        /**
         * 字典类型缓存键（按ID）
         * <p>
         * Redis数据类型：String（JSON格式的字典类型信息）
         * 缓存内容：字典类型详细信息
         * 失效时间：3600秒（1小时）+ 随机偏移
         * </p>
         *
         * @param dictTypeId 字典类型ID
         * @return 缓存键，格式：dict:type:id:{dictTypeId}
         */
        public static String dictTypeById(Long dictTypeId) {
            return DICT_TYPE_PREFIX + "id:" + dictTypeId;
        }

        /**
         * 启用的字典类型列表缓存键
         * <p>
         * Redis数据类型：String（JSON格式的字典类型列表）
         * 缓存内容：所有启用状态的字典类型列表
         * 失效时间：3600秒（1小时）+ 随机偏移
         * </p>
         *
         * @return 缓存键，格式：dict:type:enabled_list
         */
        public static String dictTypeEnabledList() {
            return DICT_TYPE_PREFIX + "enabled_list";
        }

        /**
         * 字典类型分页查询缓存键
         * <p>
         * Redis数据类型：String（JSON格式的分页结果）
         * 缓存内容：字典类型分页查询结果
         * 失效时间：180秒（3分钟）
         * 注意：分页查询缓存时间较短，避免数据不一致
         * </p>
         *
         * @param dictCode 字典编码（可选）
         * @param dictName 字典名称（可选）
         * @param status 状态（可选）
         * @param pageNum 页码
         * @param pageSize 每页大小
         * @param orderBy 排序字段
         * @param orderDirection 排序方向
         * @return 缓存键，格式：dict:type:page_query:{hash}
         */
        public static String dictTypePageQuery(String dictCode, String dictName, Integer status,
                                                int pageNum, int pageSize, String orderBy, String orderDirection) {
            // 构建查询条件字符串
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append("code:").append(dictCode == null ? "null" : dictCode);
            keyBuilder.append("|name:").append(dictName == null ? "null" : dictName);
            keyBuilder.append("|status:").append(status == null ? "null" : status);
            keyBuilder.append("|page:").append(pageNum);
            keyBuilder.append("|size:").append(pageSize);
            keyBuilder.append("|order:").append(orderBy == null ? "null" : orderBy);
            keyBuilder.append("|dir:").append(orderDirection == null ? "null" : orderDirection);

            // 使用 hashCode 生成简短的缓存键
            int hash = keyBuilder.toString().hashCode();
            return DICT_TYPE_PREFIX + "page_query:" + hash;
        }

        /**
         * 字典类型分页查询缓存通配符
         * <p>
         * 用于批量删除所有分页查询缓存
         * </p>
         *
         * @return 缓存键通配符，格式：dict:type:page_query:*
         */
        public static String dictTypePageQueryPattern() {
            return DICT_TYPE_PREFIX + "page_query:*";
        }

        // ==================== 字典数据缓存键 ====================

        /**
         * 字典数据列表缓存键（按类型编码）
         * <p>
         * Redis数据类型：String（JSON格式的字典数据列表）
         * 缓存内容：指定字典类型下的所有数据项列表
         * 失效时间：1800秒（30分钟）+ 随机偏移
         * </p>
         *
         * @param dictCode 字典编码
         * @return 缓存键，格式：dict:data:list:{dictCode}
         */
        public static String dictDataListByCode(String dictCode) {
            return DICT_DATA_PREFIX + "list:" + dictCode;
        }

        /**
         * 字典数据详情缓存键（按ID）
         * <p>
         * Redis数据类型：String（JSON格式的字典数据信息）
         * 缓存内容：单个字典数据详细信息
         * 失效时间：1800秒（30分钟）+ 随机偏移
         * </p>
         *
         * @param dictDataId 字典数据ID
         * @return 缓存键，格式：dict:data:id:{dictDataId}
         */
        public static String dictDataById(Long dictDataId) {
            return DICT_DATA_PREFIX + "id:" + dictDataId;
        }

        // ==================== 分布式锁键 ====================

        /**
         * 字典类型加载锁键
         * <p>
         * Redis数据类型：String
         * 用途：防止缓存击穿，在加载字典类型时使用分布式锁
         * 失效时间：10秒
         * </p>
         *
         * @param dictCode 字典编码
         * @return 锁键，格式：dict:lock:type:{dictCode}
         */
        public static String dictTypeLock(String dictCode) {
            return DICT_LOCK_PREFIX + "type:" + dictCode;
        }

        /**
         * 字典数据列表加载锁键
         * <p>
         * Redis数据类型：String
         * 用途：防止缓存击穿，在加载字典数据列表时使用分布式锁
         * 失效时间：10秒
         * </p>
         *
         * @param dictCode 字典编码
         * @return 锁键，格式：dict:lock:data:{dictCode}
         */
        public static String dictDataListLock(String dictCode) {
            return DICT_LOCK_PREFIX + "data:" + dictCode;
        }
    }

    /**
     * 登录模块缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Login {

        /**
         * 登录账户密码错误次数缓存 Key 前缀
         * KEY 格式：login:error_count:{username}
         */
        public static final String LOGIN_ERROR_COUNT_CACHE_PREFIX = "login:error_count:";

        /**
         * 构建登录账户密码错误次数缓存 Key
         *
         * @param username 用户名
         * @return 缓存 Key
         */
        public static String buildLoginErrorCountCacheKey(String username) {
            return LOGIN_ERROR_COUNT_CACHE_PREFIX + username;
        }
    }

    /**
     * 系统配置模块缓存键
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SysConfig {

        /**
         * 前端可见配置缓存 Key 前缀
         * KEY 格式：config:frontend:{configKey}
         */
        public static final String FRONTEND_CONFIG_CACHE_PREFIX = "config:frontend:";

        /**
         * 构建前端可见配置缓存 Key
         *
         * @param configKey 配置键
         * @return 缓存 Key
         */
        public static String buildFrontendConfigCacheKey(String configKey) {
            return FRONTEND_CONFIG_CACHE_PREFIX + configKey;
        }
    }
}
