/**
 * 知识库-文档 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  ContentDocumentDetail,
  ContentDocumentCreateRequest,
  ContentDocumentSaveContentRequest,
  ContentDocumentUpdateRequest,
} from './typings.d';

const API_PREFIX = '/refinex-kb/documents';

/**
 * 创建文档
 */
export async function createDocument(data: ContentDocumentCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 保存文档内容（MDXEditor 核心接口）
 */
export async function saveDocumentContent(
  documentId: number,
  data: ContentDocumentSaveContentRequest,
) {
  return request<ApiResponse<{ versionNumber: number; message: string }>>(
    `${API_PREFIX}/${documentId}/content`,
    {
      method: 'POST',
      data,
    },
  );
}

/**
 * 根据 ID 查询文档详情
 */
export async function getDocumentById(documentId: number) {
  return request<ApiResponse<ContentDocumentDetail>>(`${API_PREFIX}/${documentId}`, {
    method: 'GET',
  });
}

/**
 * 根据 GUID 查询文档详情
 */
export async function getDocumentByGuid(docGuid: string) {
  return request<ApiResponse<ContentDocumentDetail>>(`${API_PREFIX}/guid/${docGuid}`, {
    method: 'GET',
  });
}

/**
 * 根据目录 ID 查询文档列表
 */
export async function getDocumentsByDirectoryId(directoryId: number) {
  return request<ApiResponse<ContentDocumentDetail[]>>(
    `${API_PREFIX}/directory/${directoryId}`,
    {
      method: 'GET',
    },
  );
}

/**
 * 更新文档
 */
export async function updateDocument(
  documentId: number,
  data: ContentDocumentUpdateRequest,
) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除文档
 */
export async function deleteDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}`, {
    method: 'DELETE',
  });
}

/**
 * 发布文档
 */
export async function publishDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/publish`, {
    method: 'POST',
  });
}

/**
 * 下架文档
 */
export async function offlineDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/offline`, {
    method: 'POST',
  });
}

/**
 * 点赞文档
 */
export async function likeDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/like`, {
    method: 'POST',
  });
}

/**
 * 取消点赞
 */
export async function unlikeDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/like`, {
    method: 'DELETE',
  });
}

/**
 * 收藏文档
 */
export async function collectDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/collect`, {
    method: 'POST',
  });
}

/**
 * 取消收藏
 */
export async function uncollectDocument(documentId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/collect`, {
    method: 'DELETE',
  });
}

/**
 * 记录浏览文档
 */
export async function recordView(documentId: number, duration?: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/view`, {
    method: 'POST',
    params: { duration },
  });
}

/**
 * 绑定标签
 */
export async function bindDocumentTags(documentId: number, tagIds: number[]) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/tags`, {
    method: 'POST',
    data: tagIds,
  });
}

/**
 * 解绑标签
 */
export async function unbindDocumentTag(documentId: number, tagId: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${documentId}/tags/${tagId}`, {
    method: 'DELETE',
  });
}

