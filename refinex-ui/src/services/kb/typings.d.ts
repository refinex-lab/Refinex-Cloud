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

// 重新导出通用类型
export type { ApiResponse, PageResult };

