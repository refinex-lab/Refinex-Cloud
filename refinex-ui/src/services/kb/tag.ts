import { request } from '@umijs/max';
import type { ApiResponse } from '../typings';
import type { ContentTag, TagPageParams, TagPageResult } from './tag.d';

const API_PREFIX = '/refinex-kb';

/**
 * 创建标签
 */
export async function createTag(data: {
  tagName: string;
  tagColor?: string;
  tagType?: number;
  remark?: string;
}): Promise<ApiResponse<number>> {
  return request(`${API_PREFIX}/tags`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新标签
 */
export async function updateTag(
  id: number,
  data: {
    tagName: string;
    tagColor?: string;
    remark?: string;
  },
): Promise<ApiResponse<boolean>> {
  return request(`${API_PREFIX}/tags/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除标签
 */
export async function deleteTag(id: number): Promise<ApiResponse<void>> {
  return request(`${API_PREFIX}/tags/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 批量删除标签
 */
export async function batchDeleteTags(ids: number[]): Promise<ApiResponse<void>> {
  return request(`${API_PREFIX}/tags/batch`, {
    method: 'DELETE',
    data: ids,
  });
}

/**
 * 获取标签详情
 */
export async function getTag(id: number): Promise<ApiResponse<ContentTag>> {
  return request(`${API_PREFIX}/tags/${id}`, {
    method: 'GET',
  });
}

/**
 * 获取我的标签列表
 */
export async function getMyTags(): Promise<ApiResponse<ContentTag[]>> {
  return request(`${API_PREFIX}/tags/list`, {
    method: 'GET',
  });
}

/**
 * 获取系统标签列表
 */
export async function getSystemTags(): Promise<ApiResponse<ContentTag[]>> {
  return request(`${API_PREFIX}/tags/system`, {
    method: 'GET',
  });
}

/**
 * 分页查询我的标签（用户端）
 */
export async function pageMyTags(params: {
  tagName?: string;
  pageNum: number;
  pageSize: number;
}): Promise<ApiResponse<TagPageResult>> {
  return request(`${API_PREFIX}/tags/page`, {
    method: 'GET',
    params,
  });
}

/**
 * 分页查询所有标签（管理端）
 */
export async function pageAllTags(params: TagPageParams): Promise<ApiResponse<TagPageResult>> {
  return request(`${API_PREFIX}/admin/tags/page`, {
    method: 'GET',
    params,
  });
}

/**
 * 增加标签使用次数（内部使用）
 */
export async function incrementTagUsage(tagId: number): Promise<ApiResponse<void>> {
  return request(`${API_PREFIX}/tags/${tagId}/increment`, {
    method: 'POST',
  });
}

/**
 * 减少标签使用次数（内部使用）
 */
export async function decrementTagUsage(tagId: number): Promise<ApiResponse<void>> {
  return request(`${API_PREFIX}/tags/${tagId}/decrement`, {
    method: 'POST',
  });
}

