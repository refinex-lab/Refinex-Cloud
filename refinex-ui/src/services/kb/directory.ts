/**
 * 知识库-内容目录 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  ContentDirectory,
  ContentDirectoryBatchSortRequest,
  ContentDirectoryCreateRequest,
  ContentDirectoryMoveRequest,
  ContentDirectoryTreeNode,
  ContentDirectoryUpdateRequest,
  ContentTreeNode,
} from './typings.d';

const API_PREFIX = '/refinex-kb/directory';

/**
 * 创建目录
 */
export async function createDirectory(data: ContentDirectoryCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}/create`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新目录
 */
export async function updateDirectory(data: ContentDirectoryUpdateRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/update`, {
    method: 'PUT',
    data,
  });
}

/**
 * 移动目录（拖拽排序/层级迁移）
 */
export async function moveDirectory(data: ContentDirectoryMoveRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/move`, {
    method: 'PUT',
    data,
  });
}

/**
 * 批量更新目录排序
 */
export async function batchUpdateSort(data: ContentDirectoryBatchSortRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/batch-sort`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除目录（级联删除子目录）
 */
export async function deleteDirectory(directoryId: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${directoryId}`, {
    method: 'DELETE',
  });
}

/**
 * 查询目录详情
 */
export async function getDirectoryById(directoryId: number) {
  return request<ApiResponse<ContentDirectory>>(`${API_PREFIX}/${directoryId}`, {
    method: 'GET',
  });
}

/**
 * 查询目录树（树形结构）
 */
export async function getDirectoryTree(spaceId: number) {
  return request<ApiResponse<ContentDirectoryTreeNode[]>>(`${API_PREFIX}/tree/${spaceId}`, {
    method: 'GET',
  });
}

/**
 * 查询目录列表（平铺列表）
 */
export async function getDirectoryList(spaceId: number) {
  return request<ApiResponse<ContentDirectory[]>>(`${API_PREFIX}/list/${spaceId}`, {
    method: 'GET',
  });
}

/**
 * 查询子目录列表
 */
export async function getChildDirectories(spaceId: number, parentId: number) {
  return request<ApiResponse<ContentDirectory[]>>(
    `${API_PREFIX}/children/${spaceId}/${parentId}`,
    {
      method: 'GET',
    },
  );
}

/**
 * 查询目录树（包含文档节点）
 */
export async function getDirectoryTreeWithDocs(spaceId: number) {
  return request<ApiResponse<ContentTreeNode[]>>(`${API_PREFIX}/tree-with-docs/${spaceId}`, {
    method: 'GET',
  });
}

