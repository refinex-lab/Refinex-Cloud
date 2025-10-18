# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

Refinex-Cloud 是一个基于 Spring Cloud（后端）和 Ant Design Pro（前端）构建的现代化微服务云平台。它是一个综合性的企业级应用，拥有强大的后端 API 和优雅的前端管理界面。

## 技术栈

### 后端
- **框架**: Spring Boot 3.5.6 + Spring Cloud 2025.0.0
- **Java 版本**: Java 17
- **构建工具**: Maven 多模块结构
- **认证**: Sa-Token 1.44.0（基于 JWT）
- **数据库**: 自定义 JDBC 封装（refinex-common-jdbc）- 非 ORM 框架
- **缓存**: Redis + Redisson 3.51.0 分布式锁
- **API 文档**: SpringDoc OpenAPI + Knife4j 4.4.0
- **配置**: Nacos 分布式配置

### 前端
- **框架**: React 19.1.0 + TypeScript 5.6.3
- **构建工具**: UmiJS 4.3.24
- **UI 库**: Ant Design 5.25.4 + Ant Design Pro Components 2.7.19
- **代码质量**: Biome 2.0.6 + ESLint
- **测试**: Jest 30.0.4 + Testing Library

## 开发命令

### 后端 (Maven)
```bash
# 构建整个项目
mvn clean install

# 运行特定服务
mvn spring-boot:run -pl refinex-auth
mvn spring-boot:run -pl refinex-gateway
mvn spring-boot:run -pl refinex-platform

# 使用特定环境构建
mvn clean package -Pprod
```

### 前端 (refinex-ui)
```bash
# 安装依赖
npm install

# 开发服务器
npm run start:dev    # 开发环境（默认）
npm run start:no-mock # 无 Mock 数据
npm run start:test   # 测试环境
npm run start:pre    # 预发布环境

# 构建
npm run build        # 生产构建
npm run analyze      # 构建分析

# 代码质量
npm run lint         # Biome + TypeScript 检查
npm run biome:lint   # 仅 Biome 检查
npm run tsc          # 仅 TypeScript 类型检查

# 测试
npm test             # 运行测试
npm run test:coverage # 测试覆盖率
```

## 架构概述

### 模块结构
- **refinex-common/**: 13 个共享模块，提供核心功能
  - `refinex-common-core`: 核心工具类、枚举、异常
  - `refinex-common-jdbc`: 自定义 JDBC 封装，支持 SQL 监控
  - `refinex-common-redis`: Redis 操作和缓存
  - `refinex-common-security`: 安全配置和 Sa-Token
  - `refinex-common-json`: JSON 序列化工具
  - `refinex-common-file`: 文件存储和处理
  - `refinex-common-mail`: 邮件服务
  - `refinex-common-web`: Web 层工具
  - `refinex-common-test`: 测试工具
  - `refinex-common-satoken`: Sa-Token 权限认证
  - `refinex-common-protection`: 安全防护
  - `refinex-common-nacos`: Nacos 配置管理
  - `refinex-common-mq`: 消息队列
  - `refinex-common-job`: 定时任务
- **refinex-api/**: 服务间通信的 API 契约
- **refinex-gateway/**: Spring Cloud Gateway 网关路由
- **refinex-auth/**: 认证服务（端口 8081）
- **refinex-platform/**: 核心业务平台（端口 8083）
- **refinex-ui/**: Ant Design Pro 前端界面

### 文件夹结构 (前端)

```text
├── config                   # umi 配置，包含路由，构建等配置
├── mock                     # 本地模拟数据
├── public
│   └── favicon.png          # Favicon
├── src
│   ├── assets               # 本地静态资源
│   ├── components           # 业务通用组件
│   ├── e2e                  # 集成测试用例
│   ├── layouts              # 通用布局
│   ├── models               # 全局 dva model
│   ├── pages                # 业务页面入口和常用模板
│   ├── services             # 后台接口服务
│   ├── utils                # 工具库
│   ├── locales              # 国际化资源
│   ├── global.less          # 全局样式
│   └── global.ts            # 全局 JS
├── tests                    # 测试工具
├── README.md
└── package.json
```

### 页面代码结构推荐 (前端)

为了让项目代码组织更加规范，让开发能够更方便的定位到相关页面组件代码，我们定义了一套规范，该规范当前只作为推荐的指导，并非强制。

```text
src
├── components
└── pages
    ├── Welcome        // 路由组件下不应该再包含其他路由组件，基于这个约定就能清楚的区分路由组件和非路由组件了
    |   ├── components // 对于复杂的页面可以再自己做更深层次的组织，但建议不要超过三层
    |   ├── Form.tsx
    |   ├── index.tsx  // 页面组件的代码
    |   └── index.less // 页面样式
    ├── Order          // 路由组件下不应该再包含其他路由组件，基于这个约定就能清楚的区分路由组件和非路由组件了
    |   ├── index.tsx
    |   └── index.less
    ├── User
    |   ├── components // group 下公用的组件集合
    |   ├── Login      // group 下的页面 Login
    |   ├── Register   // group 下的页面 Register
    |   └── util.ts    // 这里可以有一些共用方法之类，不做推荐和约束，看业务场景自行做组织
    └── *              // 其它页面组件代码
```

所有路由组件（会配置在路由配置中的组件）我们推荐以大驼峰命名打平到 pages 下面第一级（复杂的项目可以增加 group 层级，在 group 下放置 pages）。不建议在路由组件内部再嵌套路由组件 - 不方便分辨一个组件是否是路由组件，而且不方便快速从全局定位到路由组件。

我们推荐尽可能的拆分路由组件为更细粒度的组件，对于多个页面可能会用到的组件我们推荐放到 src/components 中，对于只是被单个页面依赖的（区块）组件，我们推荐就近维护到路由组件文件夹下即可。

### 核心架构决策
1. **非 ORM 框架**: 使用自定义 JDBC 封装实现 SQL 透明化和性能监控
2. **微服务架构**: 使用 Nacos 进行服务发现，服务边界清晰
3. **安全优先**: 内置加密、数据脱敏和 API 限流
4. **配置管理**: Nacos 分布式配置，支持环境隔离

## 重要开发说明

### 后端开发
- 使用 `refinex-common-jdbc` 中的自定义 JDBC 封装进行数据库操作
- 遵循既定的错误处理模式和自定义错误码
- 所有敏感数据应使用内置工具进行脱敏
- API 端点应使用 OpenAPI 注解进行文档化
- 使用 Sa-Token 进行认证和授权

### 前端开发
- 基于 Ant Design Pro 脚手架开发
- 使用 TypeScript 进行类型安全开发
- 遵循 UmiJS 约定式路由
- 统一使用 Ant Design 组件库
- 实现适当的错误处理和加载状态

### 配置管理
- 后端配置通过 Nacos 管理，支持环境变量替换
- 配置文件结构：`refinex-common.yml`（公共配置）+ `${service-name}.yml`（服务特定配置）
- 敏感配置使用环境变量注入

### 数据库操作
项目使用自定义 JDBC 封装而非 ORM。主要特性：
- SQL 日志记录和监控
- 慢查询检测
- 敏感数据脱敏
- 多数据库方言支持
- 命名 SQL 管理
- 支持编程式事务管理

## 安全考虑
- 所有 API 端点应具备适当的认证和授权
- 敏感数据必须在数据访问层进行脱敏
- 对并发操作使用分布式锁
- 对公共 API 实施限流
- 配置值通过环境变量管理，避免硬编码

## 端口分配
- Gateway: 8080
- Auth Service: 8081
- Platform Service: 8083
- Frontend Dev Server: 8000 (默认)