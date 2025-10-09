# CLAUDE.md

此文件为 Claude Code (claude.ai/code) 在此代码库中工作时提供指导。

## 项目概述

Refinex-Cloud 是一个基于 Spring Cloud（后端）和 Vue.js（前端）构建的现代化微服务云平台。它是一个综合性的企业级应用，拥有强大的后端 API 和优雅的 Vue.js 管理界面。

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
- **框架**: Vue 3.5.21 + TypeScript 5.9.2
- **构建工具**: Vite 7.1.5 + pnpm workspace
- **UI 库**: Naive UI 2.43.1
- **样式**: UnoCSS 自定义预设
- **状态管理**: Pinia 3.0.3

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

### 前端 (pnpm)
```bash
# 安装依赖
pnpm i

# 开发服务器
pnpm dev          # 测试环境
pnpm dev:prod     # 生产环境

# 构建
pnpm build        # 生产构建
pnpm build:test   # 测试构建

# 代码质量
pnpm lint         # ESLint
pnpm typecheck    # TypeScript 类型检查
```

## 架构概述

### 模块结构
- **refinex-common/**: 9 个共享模块，提供核心功能
  - `refinex-common-core`: 核心工具类、枚举、异常
  - `refinex-common-jdbc`: 自定义 JDBC 封装，支持 SQL 监控
  - `refinex-common-redis`: Redis 操作和缓存
  - `refinex-common-security`: 安全配置和 Sa-Token
  - `refinex-common-json`: JSON 序列化工具
  - `refinex-common-file`: 文件存储和处理
  - `refinex-common-mail`: 邮件服务
  - `refinex-common-web`: Web 层工具
  - `refinex-common-test`: 测试工具
- **refinex-api/**: 服务间通信的 API 契约
- **refinex-gateway/**: Spring Cloud Gateway 网关路由
- **refinex-auth/**: 认证服务
- **refinex-platform/**: 核心业务平台
- **refinex-vue3-admin/**: Vue.js 管理界面

### 核心架构决策
1. **非 ORM 框架**: 使用自定义 JDBC 封装实现 SQL 透明化和性能监控
2. **微服务架构**: 使用 Nacos 进行服务发现，服务边界清晰
3. **安全优先**: 内置加密、数据脱敏和 API 限流
4. **配置管理**: Nacos 分布式配置，Jasypt 加密

## 重要开发说明

### 后端开发
- 使用 `refinex-common-jdbc` 中的自定义 JDBC 封装进行数据库操作
- 遵循既定的错误处理模式和自定义错误码
- 所有敏感数据应使用内置工具进行脱敏
- API 端点应使用 OpenAPI 注解进行文档化
- 使用 Sa-Token 进行认证和授权

### 前端开发
- 所有新组件应使用 TypeScript 编写
- 遵循自动文件路由系统约定
- 统一使用 Naive UI 组件
- 实现适当的错误处理和加载状态
- 遵循既定的 i18n 模式支持多语言

### 配置管理
- 后端配置通过 Nacos 管理
- 使用 Jasypt 加密敏感配置值
- 环境特定配置位于 `nacos/` 目录
- 数据库脚本位于 `document/sql/`

### 测试
- 后端测试工具位于 `refinex-common-test`
- 前端使用 Vitest 进行单元测试
- 集成测试应覆盖自定义 JDBC 层
- 安全测试应验证数据脱敏和加密

## 数据库操作
项目使用自定义 JDBC 封装而非 ORM。主要特性：
- SQL 日志记录和监控
- 慢查询检测
- 敏感数据脱敏
- 多数据库方言支持
- 命名 SQL 管理
- 支持编程式和声明式事务管理

## 安全考虑
- 所有 API 端点应具备适当的认证和授权
- 敏感数据必须在数据访问层进行脱敏
- 对并发操作使用分布式锁
- 对公共 API 实施限流
- 包含机密信息的配置值应使用 Jasypt 加密