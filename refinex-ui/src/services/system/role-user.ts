import { request } from '@umijs/max';
import type { ApiResponse, PageResult } from '@/services/typings';
import type {
  UserRoleBindRequest,
  RoleUserResponse,
  RoleUserQueryParams,
} from './typings';
import { decryptSensitiveData as commonDecryptSensitiveData } from '@/services/common';

const API_PREFIX = '/refinex-platform/roles';

/**
 * 绑定用户到角色
 */
export async function bindUsers(roleId: number, data: UserRoleBindRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${roleId}/users`, {
    method: 'POST',
    data,
  });
}

/**
 * 解绑用户角色
 */
export async function unbindUser(roleId: number, userId: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${roleId}/users/${userId}`, {
    method: 'DELETE',
  });
}

/**
 * 分页查询角色下的用户
 */
export async function queryRoleUsers(roleId: number, params: RoleUserQueryParams) {
  return request<ApiResponse<PageResult<RoleUserResponse>>>(`${API_PREFIX}/${roleId}/users`, {
    method: 'GET',
    params,
  });
}

/**
 * 解密敏感数据（角色用户专用）
 */
export async function decryptRoleUserSensitiveData(params: {
  tableName: string;
  rowGuid: string;
  fieldCode: string;
}) {
  return commonDecryptSensitiveData('/refinex-platform', params);
}

