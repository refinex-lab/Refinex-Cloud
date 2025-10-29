/**
 * 知识库模块类型定义
 */

import type { ApiResponse, PageResult, PageParams } from '@/services/typings';

/** 空间类型枚举 */
export enum SpaceType {
  /** 个人知识库 */
  PERSONAL = 0,
  /** 课程专栏 */
  COURSE = 1,
  /** 视频专栏 */
  VIDEO = 2,
}

/** 访问类型枚举 */
export enum AccessType {
  /** 私有 */
  PRIVATE = 0,
  /** 公开 */
  PUBLIC = 1,
  /** 密码访问 */
  PASSWORD_PROTECTED = 2,
}

/** 发布状态枚举 */
export enum PublishStatus {
  /** 未发布 */
  UNPUBLISHED = 0,
  /** 已发布 */
  PUBLISHED = 1,
}

/** 空间状态枚举 */
export enum SpaceStatus {
  /** 正常 */
  NORMAL = 0,
  /** 停用 */
  DISABLED = 1,
}

/** 内容空间响应 */
export interface ContentSpace {
  id: number;
  spaceCode: string;
  spaceName: string;
  spaceDesc?: string;
  coverImage?: string;
  ownerId: number;
  ownerName?: string;
  spaceType: SpaceType;
  spaceTypeDesc?: string;
  accessType: AccessType;
  accessTypeDesc?: string;
  isPublished: PublishStatus;
  publishTime?: string;
  viewCount: number;
  sort?: number;
  status: SpaceStatus;
  remark?: string;
  createTime: string;
  updateTime: string;
  version: number;
}

/** 内容空间详情响应 */
export interface ContentSpaceDetail extends ContentSpace {
  documentCount: number;
  directoryCount: number;
  createBy?: number;
  updateBy?: number;
  extraData?: string;
}

/** 内容空间创建请求 */
export interface ContentSpaceCreateRequest {
  spaceName: string;
  spaceDesc?: string;
  coverImage?: string;
  spaceType: SpaceType;
  accessType: AccessType;
  accessPassword?: string;
  sort?: number;
  remark?: string;
}

/** 内容空间更新请求 */
export interface ContentSpaceUpdateRequest {
  spaceName?: string;
  spaceDesc?: string;
  coverImage?: string;
  spaceType?: SpaceType;
  accessType?: AccessType;
  accessPassword?: string;
  sort?: number;
  status?: SpaceStatus;
  remark?: string;
  version: number;
}

/** 内容空间发布请求 */
export interface ContentSpacePublishRequest {
  isPublished: PublishStatus;
  version: number;
}

/** 内容空间查询参数 */
export interface ContentSpaceQueryParams extends PageParams {
  spaceCode?: string;
  spaceName?: string;
  ownerId?: number;
  spaceType?: SpaceType;
  accessType?: AccessType;
  isPublished?: PublishStatus;
  status?: SpaceStatus;
}

/** 内容空间访问验证请求 */
export interface ContentSpaceAccessValidateRequest {
  password?: string;
}

/** ============ 目录管理 ============ */

/** 内容目录响应 */
export interface ContentDirectory {
  id: number;
  spaceId: number;
  parentId: number;
  directoryName: string;
  directoryPath: string;
  depthLevel: number;
  sort: number;
  status: number;
  remark?: string;
  createBy?: number;
  createTime: string;
  updateTime: string;
  hasChildren?: boolean;
  children?: ContentDirectory[];
}

/** 内容目录树节点（用于前端树形展示） */
export interface ContentDirectoryTreeNode {
  key: string;
  title: string;
  id: number;
  parentId: number;
  directoryName: string;
  directoryPath?: string;
  depthLevel: number;
  sort: number;
  remark?: string;
  status?: number;
  spaceId?: number;
  createBy?: number;
  createTime?: string;
  updateTime?: string;
  hasChildren?: boolean;
  isLeaf: boolean;
  children?: ContentDirectoryTreeNode[];
}

/** 创建目录请求 */
export interface ContentDirectoryCreateRequest {
  spaceId: number;
  parentId: number;
  directoryName: string;
  sort?: number;
  remark?: string;
}

/** 更新目录请求 */
export interface ContentDirectoryUpdateRequest {
  id: number;
  directoryName: string;
  sort?: number;
  remark?: string;
}

/** 移动目录请求 */
export interface ContentDirectoryMoveRequest {
  id: number;
  targetParentId: number;
  targetSort?: number;
}

/** 批量排序项 */
export interface DirectorySortItem {
  id: number;
  sort: number;
}

/** 批量更新目录排序请求 */
export interface ContentDirectoryBatchSortRequest {
  sortItems: DirectorySortItem[];
}

/** ============ 统一树节点（目录+文档） ============ */

/** 节点类型枚举 */
export enum TreeNodeType {
  DIRECTORY = 'directory',
  DOCUMENT = 'document',
}

/** 统一树节点（目录+文档） */
export interface ContentTreeNode {
  nodeType: TreeNodeType;
  key: string;
  title: string;

  // 目录字段
  directoryId?: number;
  directoryName?: string;
  directoryPath?: string;

  // 文档字段
  documentId?: number;
  docGuid?: string;
  docTitle?: string;
  docStatus?: number;
  docStatusDesc?: string;
  accessType?: number;

  // 文档元信息字段
  docSummary?: string;
  coverImage?: string;
  wordCount?: number;
  readDuration?: number;
  viewCount?: number;
  likeCount?: number;
  collectCount?: number;
  commentCount?: number;
  createBy?: number;
  createByName?: string;
  createTime?: string;
  updateTime?: string;

  // 文档标签
  tags?: Array<{
    id: number;
    tagName: string;
    tagColor: string;
    tagType: number;
    usageCount?: number;
  }>;

  // 共用字段
  parentId: number;
  depthLevel?: number;
  sort: number;
  isLeaf: boolean;
  children?: ContentTreeNode[];
}

/** ============ 文档管理 ============ */

/** 文档状态枚举 */
export enum DocumentStatus {
  /** 草稿 */
  DRAFT = 0,
  /** 已发布 */
  PUBLISHED = 1,
  /** 已下架 */
  OFFLINE = 2,
}

/** 文档访问类型 */
export enum DocumentAccessType {
  /** 继承空间 */
  INHERIT_SPACE = 0,
  /** 自定义私有 */
  PRIVATE = 1,
  /** 自定义公开 */
  PUBLIC = 2,
}

/** 文档详情响应 */
export interface ContentDocumentDetail {
  id: number;
  docGuid: string;
  spaceId: number;
  directoryId: number;
  docTitle: string;
  docSummary?: string;
  contentBody?: string; // Markdown 内容
  contentType: string;
  coverImage?: string;
  accessType: number;
  isPaid: number;
  docStatus: number;
  docStatusDesc: string;
  publishTime?: string;
  wordCount: number;
  readDuration: number;
  viewCount: number;
  likeCount: number;
  collectCount: number;
  commentCount: number;
  versionNumber: number;
  tags?: Array<{
    id: number;
    tagName: string;
    tagColor: string;
    tagType: number;
  }>;
  directoryName?: string;
  directoryPath?: string;
  isLiked?: boolean;
  isCollected?: boolean;
  canEdit?: boolean;
  canDelete?: boolean;
  createBy: number;
  createByName?: string;
  createTime: string;
  updateTime: string;
  version: number;
}

/** 创建文档请求 */
export interface ContentDocumentCreateRequest {
  spaceId: number;
  directoryId: number;
  docTitle: string;
  docSummary?: string;
  contentType?: string;
  accessType?: number;
  tagIds?: number[];
}

/** 保存文档内容请求 */
export interface ContentDocumentSaveContentRequest {
  contentBody: string;
  changeRemark?: string;
}

/** 更新文档请求 */
export interface ContentDocumentUpdateRequest {
  docTitle?: string;
  docSummary?: string;
  coverImage?: string;
  accessType?: number;
  directoryId?: number;
  tagIds?: number[];
  version: number;
}

// 重新导出通用类型
export type { ApiResponse, PageResult };

