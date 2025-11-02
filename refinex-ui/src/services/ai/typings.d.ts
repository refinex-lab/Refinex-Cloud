/**
 * AI 模块类型定义
 */

import type { ApiResponse, PageResult, PageParams } from '@/services/typings';

/** ============ 模型配置管理 ============ */

/** 模型配置响应 */
export interface ModelConfig {
  id: number;
  modelCode: string;
  modelVersion?: string;
  modelName: string;
  provider: string;
  modelType: string;
  apiEndpoint: string;
  apiKey?: string;
  apiVersion?: string;
  modelCapabilities?: string;
  contextWindow?: number;
  maxTokens?: number;
  temperature?: number;
  pricingInput?: number;
  pricingOutput?: number;
  rpmLimit?: number;
  tpmLimit?: number;
  timeoutSeconds?: number;
  retryTimes?: number;
  circuitBreakerThreshold?: number;
  fallbackModelCode?: string;
  healthCheckUrl?: string;
  lastHealthCheckTime?: string;
  healthStatus?: number;
  isEnabled: number;
  priority: number;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
  remark?: string;
  sort?: number;
  status: number;
  extraData?: string;
  version?: number;
}

/** 模型配置创建请求 */
export interface ModelConfigCreateRequest {
  modelCode: string;
  modelVersion?: string;
  modelName: string;
  provider: string;
  modelType: string;
  apiEndpoint: string;
  apiKey: string;
  apiVersion?: string;
  modelCapabilities?: string;
  contextWindow?: number;
  maxTokens?: number;
  temperature?: number;
  pricingInput?: number;
  pricingOutput?: number;
  rpmLimit?: number;
  tpmLimit?: number;
  timeoutSeconds?: number;
  retryTimes?: number;
  circuitBreakerThreshold?: number;
  fallbackModelCode?: string;
  healthCheckUrl?: string;
  isEnabled: number;
  priority: number;
  remark?: string;
  sort: number;
  status: number;
  extraData?: string;
}

/** 模型配置更新请求 */
export interface ModelConfigUpdateRequest {
  modelVersion?: string;
  modelName: string;
  provider: string;
  modelType: string;
  apiEndpoint: string;
  apiKey?: string;
  apiVersion?: string;
  modelCapabilities?: string;
  contextWindow?: number;
  maxTokens?: number;
  temperature?: number;
  pricingInput?: number;
  pricingOutput?: number;
  rpmLimit?: number;
  tpmLimit?: number;
  timeoutSeconds?: number;
  retryTimes?: number;
  circuitBreakerThreshold?: number;
  fallbackModelCode?: string;
  healthCheckUrl?: string;
  priority: number;
  remark?: string;
  sort: number;
  status: number;
  extraData?: string;
}

/** 模型配置查询参数 */
export interface ModelConfigQueryParams extends PageParams {
  provider?: string;
  modelType?: string;
  status?: number;
  keyword?: string;
  orderBy?: string;
  orderDirection?: 'ASC' | 'DESC';
}

/** ============ 提示词模板管理 ============ */

/** 提示词模板响应 */
export interface PromptTemplate {
  id: number;
  templateCode: string;
  versionNumber?: number;
  parentTemplateId?: number;
  templateName: string;
  templateContent: string;
  templateType: string;
  templateCategory?: string;
  applicableModels?: string;
  isSystem: number;
  isPublic: number;
  creatorId?: number;
  usageCount?: number;
  likeCount?: number;
  avgTokenUsage?: number;
  avgCost?: number;
  successRate?: number;
  avgSatisfaction?: number;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
  remark?: string;
  sort?: number;
  status: number;
  extraData?: string;
  version?: number;
}

/** 提示词模板创建请求 */
export interface PromptTemplateCreateRequest {
  templateCode: string;
  templateName: string;
  templateContent: string;
  templateType: string;
  templateCategory?: string;
  applicableModels?: string;
  isSystem: number;
  isPublic: number;
  remark?: string;
  sort: number;
  status: number;
  extraData?: string;
}

/** 提示词模板更新请求 */
export interface PromptTemplateUpdateRequest {
  templateName: string;
  templateContent: string;
  templateType: string;
  templateCategory?: string;
  applicableModels?: string;
  isPublic: number;
  remark?: string;
  sort: number;
  status: number;
  extraData?: string;
}

/** 提示词模板查询参数 */
export interface PromptTemplateQueryParams extends PageParams {
  category?: string;
  type?: string;
  isPublic?: number;
  status?: number;
  keyword?: string;
  orderBy?: string;
  orderDirection?: 'ASC' | 'DESC';
}

// 重新导出通用类型
export type { ApiResponse, PageResult, PageParams };

