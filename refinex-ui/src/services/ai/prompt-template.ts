/**
 * AI 提示词模板 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  PageResult,
  PromptTemplate,
  PromptTemplateCreateRequest,
  PromptTemplateQueryParams,
  PromptTemplateUpdateRequest,
} from './typings.d';

const API_PREFIX = '/refinex-ai/prompt-templates';

/**
 * 查询提示词模板
 */
export async function queryPromptTemplates(params: PromptTemplateQueryParams) {
  return request<ApiResponse<PageResult<PromptTemplate>>>(`${API_PREFIX}`, {
    method: 'GET',
    params,
  });
}

/**
 * 获取所有提示词模板
 */
export async function listAllPromptTemplates() {
  return request<ApiResponse<PromptTemplate[]>>(`${API_PREFIX}/all`, {
    method: 'GET',
  });
}

/**
 * 获取提示词模板详情
 */
export async function getPromptTemplate(id: number) {
  return request<ApiResponse<PromptTemplate>>(`${API_PREFIX}/${id}`, {
    method: 'GET',
  });
}

/**
 * 根据编码获取模板
 */
export async function getPromptTemplateByCode(code: string) {
  return request<ApiResponse<PromptTemplate>>(`${API_PREFIX}/by-code/${code}`, {
    method: 'GET',
  });
}

/**
 * 创建提示词模板
 */
export async function createPromptTemplate(data: PromptTemplateCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新提示词模板
 */
export async function updatePromptTemplate(id: number, data: PromptTemplateUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除提示词模板
 */
export async function deletePromptTemplate(id: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 切换模板状态
 */
export async function toggleTemplateStatus(id: number, status: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}/status`, {
    method: 'PUT',
    params: { status },
  });
}
