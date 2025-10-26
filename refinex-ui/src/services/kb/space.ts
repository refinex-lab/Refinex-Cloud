/**
 * 知识库-内容空间 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  ContentSpace,
  ContentSpaceCreateRequest,
  ContentSpaceDetail,
  ContentSpacePublishRequest,
  ContentSpaceQueryParams,
  ContentSpaceUpdateRequest,
  PageResult,
} from './typings';

const API_PREFIX = '/refinex-kb/spaces';

/**
 * 创建内容空间
 */
export async function createContentSpace(data: ContentSpaceCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新内容空间
 */
export async function updateContentSpace(spaceId: number, data: ContentSpaceUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${spaceId}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 发布/取消发布空间
 */
export async function publishContentSpace(spaceId: number, data: ContentSpacePublishRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${spaceId}/publish`, {
    method: 'PATCH',
    data,
  });
}

/**
 * 删除内容空间
 */
export async function deleteContentSpace(spaceId: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${spaceId}`, {
    method: 'DELETE',
  });
}

/**
 * 根据ID获取空间详情
 */
export async function getContentSpaceDetail(spaceId: number) {
  return request<ApiResponse<ContentSpaceDetail>>(`${API_PREFIX}/${spaceId}`, {
    method: 'GET',
  });
}

/**
 * 根据空间编码获取空间详情
 */
export async function getContentSpaceDetailByCode(spaceCode: string) {
  return request<ApiResponse<ContentSpaceDetail>>(`${API_PREFIX}/code/${spaceCode}`, {
    method: 'GET',
  });
}

/**
 * 获取我的空间列表
 */
export async function getMyContentSpaces() {
  return request<ApiResponse<ContentSpace[]>>(`${API_PREFIX}/my`, {
    method: 'GET',
  });
}

/**
 * 分页查询空间列表
 */
export async function queryContentSpaces(params: ContentSpaceQueryParams) {
  const { pageNum = 1, pageSize = 10, ...queryParams } = params;
  return request<ApiResponse<PageResult<ContentSpace>>>(`${API_PREFIX}/list`, {
    method: 'GET',
    params: {
      pageNum,
      pageSize,
      ...queryParams,
    },
  });
}

/**
 * 根据拥有者ID获取空间列表
 */
export async function getContentSpacesByOwner(ownerId: number) {
  return request<ApiResponse<ContentSpace[]>>(`${API_PREFIX}/owner/${ownerId}`, {
    method: 'GET',
  });
}

/**
 * 校验空间访问权限
 */
export async function validateContentSpaceAccess(spaceId: number, password?: string) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${spaceId}/validate-access`, {
    method: 'POST',
    params: {
      password,
    },
  });
}

