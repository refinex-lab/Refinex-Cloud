# 知识库文档管理 API 端点汇总

## 📌 基础路径
- **文档管理**: `/api/kb/documents`
- **版本管理**: `/api/kb/documents/{documentId}/versions`

---

## 📝 文档管理 API (ContentDocumentController)

### 文档 CRUD

#### 1. 创建文档
- **Method**: `POST /api/kb/documents`
- **描述**: 创建新文档，默认状态为草稿
- **请求体**: `ContentDocumentCreateRequestDTO`
- **响应**: 文档ID

#### 2. 更新文档
- **Method**: `PUT /api/kb/documents/{id}`
- **描述**: 更新文档基本信息（不包括内容）
- **请求体**: `ContentDocumentUpdateRequestDTO`
- **响应**: 是否成功

#### 3. 保存文档内容 ⭐ 核心API
- **Method**: `POST /api/kb/documents/{id}/content`
- **描述**: 保存文档内容，自动创建版本记录（MDXEditor集成）
- **请求体**: `ContentDocumentSaveContentRequestDTO`
- **响应**: `{ versionNumber, message }`
- **特性**:
  - 自动统计字数和阅读时长
  - 自动创建版本记录
  - 支持自动生成摘要
  - 可选择更新文档标题

#### 4. 删除文档
- **Method**: `DELETE /api/kb/documents/{id}`
- **描述**: 逻辑删除文档
- **响应**: 是否成功

### 文档查询

#### 5. 根据ID查询文档详情
- **Method**: `GET /api/kb/documents/{id}`
- **描述**: 查询文档详情，包含完整内容
- **响应**: `ContentDocumentDetailResponseDTO`

#### 6. 根据GUID查询文档
- **Method**: `GET /api/kb/documents/guid/{docGuid}`
- **描述**: 通过全局唯一标识查询文档详情
- **响应**: `ContentDocumentDetailResponseDTO`

#### 7. 分页查询文档
- **Method**: `GET /api/kb/documents`
- **描述**: 支持多条件查询文档列表
- **查询参数**: `ContentDocumentQueryRequestDTO` + `PageRequest`
- **响应**: `PageResult<ContentDocumentResponseDTO>`

#### 8. 查询空间下的文档
- **Method**: `GET /api/kb/documents/space/{spaceId}`
- **描述**: 根据空间ID查询文档列表
- **响应**: `List<ContentDocumentResponseDTO>`

#### 9. 查询目录下的文档
- **Method**: `GET /api/kb/documents/directory/{directoryId}`
- **描述**: 根据目录ID查询文档列表
- **响应**: `List<ContentDocumentResponseDTO>`

### 文档操作

#### 10. 发布文档
- **Method**: `POST /api/kb/documents/{id}/publish`
- **描述**: 将草稿或已下架的文档发布为公开状态
- **响应**: 是否成功

#### 11. 下架文档
- **Method**: `POST /api/kb/documents/{id}/offline`
- **描述**: 将已发布的文档下架
- **响应**: 是否成功

#### 12. 移动文档
- **Method**: `PUT /api/kb/documents/{id}/move`
- **描述**: 将文档移动到其他目录
- **查询参数**: `directoryId`（null表示根目录）
- **响应**: 是否成功

#### 13. 复制文档
- **Method**: `POST /api/kb/documents/{id}/copy`
- **描述**: 复制文档到指定空间和目录
- **查询参数**: `spaceId`, `directoryId`
- **响应**: 新文档ID
- **特性**:
  - 复制文档基本信息
  - 复制最新版本内容
  - 复制标签关联
  - 重置统计数据

### 标签管理

#### 14. 绑定标签
- **Method**: `POST /api/kb/documents/{id}/tags`
- **描述**: 为文档绑定标签（会替换原有标签）
- **请求体**: `List<Long>` tagIds
- **响应**: 是否成功

#### 15. 解绑标签
- **Method**: `DELETE /api/kb/documents/{id}/tags/{tagId}`
- **描述**: 从文档中移除指定标签
- **响应**: 是否成功

### 用户交互

#### 16. 点赞文档
- **Method**: `POST /api/kb/documents/{id}/like`
- **描述**: 为文档点赞
- **响应**: 是否成功

#### 17. 取消点赞
- **Method**: `DELETE /api/kb/documents/{id}/like`
- **描述**: 取消对文档的点赞
- **响应**: 是否成功

#### 18. 收藏文档
- **Method**: `POST /api/kb/documents/{id}/collect`
- **描述**: 将文档添加到收藏
- **响应**: 是否成功

#### 19. 取消收藏
- **Method**: `DELETE /api/kb/documents/{id}/collect`
- **描述**: 从收藏中移除文档
- **响应**: 是否成功

#### 20. 记录浏览
- **Method**: `POST /api/kb/documents/{id}/view`
- **描述**: 记录文档浏览，增加浏览计数
- **响应**: 是否成功

---

## 🔄 文档版本管理 API (ContentDocumentVersionController)

### 版本查询

#### 1. 查询版本历史
- **Method**: `GET /api/kb/documents/{documentId}/versions`
- **描述**: 分页查询文档的版本历史记录
- **查询参数**: `PageRequest`
- **响应**: `PageResult<ContentDocumentVersionResponseDTO>`

#### 2. 查询版本详情
- **Method**: `GET /api/kb/documents/{documentId}/versions/{versionNumber}`
- **描述**: 查询指定版本的详细内容
- **响应**: `ContentDocumentVersionDetailResponseDTO`

### 版本操作

#### 3. 恢复版本
- **Method**: `POST /api/kb/documents/{documentId}/versions/{versionNumber}/restore`
- **描述**: 将文档恢复到指定历史版本，会创建新的版本记录
- **响应**: `{ newVersionNumber, message }`
- **特性**:
  - 创建新版本（内容来自历史版本）
  - 更新文档当前内容和版本号
  - 重新统计字数和阅读时长

#### 4. 清理旧版本
- **Method**: `DELETE /api/kb/documents/{documentId}/versions/clean`
- **描述**: 清理文档的旧版本，保留最近N个版本
- **查询参数**: `keepCount`（默认10，范围1-100）
- **响应**: `{ deletedCount, keepCount, message }`

#### 5. 删除版本
- **Method**: `DELETE /api/kb/documents/{documentId}/versions/{versionNumber}`
- **描述**: 删除指定版本（不能删除当前版本）
- **响应**: 是否成功

#### 6. 版本对比 (TODO)
- **Method**: `GET /api/kb/documents/{documentId}/versions/compare`
- **描述**: 对比两个版本的差异
- **查询参数**: `fromVersion`, `toVersion`
- **响应**: 版本差异信息
- **状态**: 待实现（需要 diff 算法）

---

## 🔑 核心功能说明

### MDXEditor 保存流程

```
前端 MDXEditor
    ↓
POST /api/kb/documents/{id}/content
    {
        "contentBody": "# 文档标题\n\n内容...",
        "docTitle": "可选的标题更新",
        "changeSummary": "修改说明",
        "autoGenerateSummary": true
    }
    ↓
Service 处理:
    1. 验证权限
    2. 统计字数: MarkdownUtils.countWords()
    3. 计算阅读时长: 基于400字/分钟
    4. 获取当前版本号 (如: 5)
    5. 创建版本记录 (版本号: 6)
    6. 更新文档内容和版本号
    7. 可选更新标题和摘要
    ↓
返回: { versionNumber: 6, message: "保存成功" }
```

### 版本管理策略

1. **自动版本**: 每次保存内容自动创建新版本
2. **版本保留**: 默认保留最近10个版本，可配置
3. **版本清理**: 定时任务清理旧版本
4. **版本恢复**: 可恢复到任意历史版本
5. **版本对比**: 支持查看两个版本的差异（TODO）

### 权限控制

1. **文档创建**: 需要空间访问权限
2. **文档编辑**: 只有创建者可以编辑
3. **文档查看**: 
   - 草稿状态：只有创建者可见
   - 已发布：根据访问类型判断
   - 已下架：只有创建者可见

### 统计功能

- **浏览次数**: `incrementViewCount()`
- **点赞数**: `incrementLikeCount()` / `decrementLikeCount()`
- **收藏数**: `incrementCollectCount()` / `decrementCollectCount()`
- **评论数**: 由评论服务管理
- **分享次数**: 待实现

---

## 📊 API 分类统计

| 类别 | 数量 | 说明 |
|------|------|------|
| 文档 CRUD | 4 | 创建、更新、保存内容、删除 |
| 文档查询 | 5 | 按ID、GUID、分页、空间、目录 |
| 文档操作 | 4 | 发布、下架、移动、复制 |
| 标签管理 | 2 | 绑定、解绑 |
| 用户交互 | 5 | 点赞、收藏、浏览 |
| 版本管理 | 6 | 查询、恢复、清理、对比等 |
| **总计** | **26** | **完整的文档管理功能** |

---

## 🛡️ 安全和性能

### 安全特性
- ✅ Sa-Token 认证保护
- ✅ 权限验证（创建者/空间拥有者）
- ✅ 乐观锁并发控制
- ✅ 参数校验（@Valid）
- ✅ 业务异常处理

### 性能优化
- ✅ 批量查询优化（标签Map结构）
- ✅ 分页查询支持
- ✅ 数据库索引优化
- ✅ 计数器增量更新（避免全表更新）
- ✅ Markdown 字数统计优化

---

**生成时间**: 2025-10-27  
**API 版本**: v1.0.0  
**状态**: ✅ 已完成

