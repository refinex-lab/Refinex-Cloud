/**
 * 知识库-文档版本 API
 */

import { request } from '@umijs/max';
import type {
  ApiResponse,
  PageParams,
  PageResult,
  ContentDocumentVersion,
  ContentDocumentVersionDetail,
  VersionCompareResult,
  VersionCleanResult,
  VersionRestoreResult,
} from './typings.d';

const API_PREFIX = '/refinex-kb/documents';

/**
 * 查询版本历史（分页）
 */
export async function getVersionHistory(documentId: number, params: PageParams) {
  return request<ApiResponse<PageResult<ContentDocumentVersion>>>(
    `${API_PREFIX}/${documentId}/versions`,
    {
      method: 'GET',
      params,
    },
  );
}

/**
 * 查询版本详情
 */
export async function getVersionDetail(documentId: number, versionNumber: number) {
  return request<ApiResponse<ContentDocumentVersionDetail>>(
    `${API_PREFIX}/${documentId}/versions/${versionNumber}`,
    {
      method: 'GET',
    },
  );
}

/**
 * 恢复版本
 */
export async function restoreVersion(documentId: number, versionNumber: number) {
  return request<ApiResponse<VersionRestoreResult>>(
    `${API_PREFIX}/${documentId}/versions/${versionNumber}/restore`,
    {
      method: 'POST',
    },
  );
}

/**
 * 版本对比
 */
export async function compareVersions(
  documentId: number,
  fromVersion: number,
  toVersion: number,
) {
  return request<ApiResponse<VersionCompareResult>>(
    `${API_PREFIX}/${documentId}/versions/compare`,
    {
      method: 'GET',
      params: { fromVersion, toVersion },
    },
  );
}

/**
 * 清理旧版本
 */
export async function cleanOldVersions(documentId: number, keepCount: number) {
  return request<ApiResponse<VersionCleanResult>>(
    `${API_PREFIX}/${documentId}/versions/clean`,
    {
      method: 'DELETE',
      params: { keepCount },
    },
  );
}

/**
 * 删除指定版本
 */
export async function deleteVersion(documentId: number, versionNumber: number) {
  return request<ApiResponse<boolean>>(
    `${API_PREFIX}/${documentId}/versions/${versionNumber}`,
    {
      method: 'DELETE',
    },
  );
}

