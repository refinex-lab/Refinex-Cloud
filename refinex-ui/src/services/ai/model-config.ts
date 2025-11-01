/**
 * AI 模型配置 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  ModelConfig,
  ModelConfigCreateRequest,
  ModelConfigQueryParams,
  ModelConfigUpdateRequest,
  PageResult,
} from './typings.d';

const API_PREFIX = '/refinex-ai/model-configs';

/**
 * 查询模型配置
 */
export async function queryModelConfigs(params: ModelConfigQueryParams) {
  return request<ApiResponse<PageResult<ModelConfig>>>(`${API_PREFIX}`, {
    method: 'GET',
    params,
  });
}

/**
 * 获取所有模型配置
 */
export async function listAllModelConfigs() {
  return request<ApiResponse<ModelConfig[]>>(`${API_PREFIX}/all`, {
    method: 'GET',
  });
}

/**
 * 获取模型配置详情
 */
export async function getModelConfig(id: number) {
  return request<ApiResponse<ModelConfig>>(`${API_PREFIX}/${id}`, {
    method: 'GET',
  });
}

/**
 * 创建模型配置
 */
export async function createModelConfig(data: ModelConfigCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新模型配置
 */
export async function updateModelConfig(id: number, data: ModelConfigUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除模型配置
 */
export async function deleteModelConfig(id: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 切换模型状态
 */
export async function toggleModelStatus(id: number, status: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}/status`, {
    method: 'PUT',
    params: { status },
  });
}
