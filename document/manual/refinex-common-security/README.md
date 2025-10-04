# Refinex Common Security

## Sa-Token 集成方案

1. 认证方案 : JWT + Redis混合模式
   - JWT承载用户基础信息
   - Redis存储会话状态和权限缓存
2. 权限模型 : 结合现有RBAC设计
   - 利用 sys_user_role 和 sys_role_permission 表
   - 实现Sa-Token的 StpInterface 接口
3. 会话管理 : 分布式Session方案
   - 利用现有Redis模块
   - 支持多端登录和会话共享

模块开发优先级：
1. 高优先级 : 基础认证功能（登录、注销、权限校验）
2. 中优先级 : 会员权益集成、多端登录管理
3. 低优先级 : OAuth2.0、单点登录等高级功能

架构设计建议：
在 refinex-common-security 模块中实现：
- 自动配置类（Sa-Token配置）
- 权限接口实现（StpInterface）
- 统一鉴权注解
- 会话管理工具类
- 与现有异常体系集成

关键技术决策：
认证模式 : JWT + Redis 混合模式
- JWT存储用户基础信息（用户ID、用户名、角色列表）
- Redis存储完整会话状态和权限缓存
- 支持Token刷新和会话延期

权限模型 : 基于现有RBAC设计
- 用户 → 角色 → 权限的三层权限模型
- 支持角色优先级和权限继承
- 集成会员等级权益系统

会话策略 : 分布式多端登录
- 支持同一用户多设备登录
- 基于设备类型的会话隔离
- 提供强制下线和会话管理功能

实施检查清单：
1. 创建Sa-Token自动配置类，定义核心配置属性和Bean注册
2. 实现StpInterface权限接口，对接sys_user_role和sys_role_permission表
3. 扩展现有异常体系，创建SecurityException和相关错误码枚举
4. 开发AuthService认证服务，实现登录、注销、权限校验核心功能
5. 创建UserContextService用户上下文服务，提供当前用户信息获取
6. 实现SecurityUtils安全工具类，封装常用的权限判断和用户操作
7. 开发JwtUtils JWT工具类，处理令牌生成、解析和刷新逻辑
8. 配置Redis会话存储，集成refinex-common-redis模块
9. 创建权限注解和全局异常处理器
10. 编写Spring Boot自动配置导入文件，实现开箱即用