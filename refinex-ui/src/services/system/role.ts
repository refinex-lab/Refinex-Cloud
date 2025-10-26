import { request } from '@umijs/max';
import type { ApiResponse, PageResult } from '@/services/typings';
import type {
  RoleCreateRequest,
  RoleUpdateRequest,
  RoleResponse,
  RoleQueryParams,
} from './typings';

const API_PREFIX = '/refinex-platform/roles';

/**
 * 创建角色
 */
export async function createRole(data: RoleCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新角色
 */
export async function updateRole(id: number, data: RoleUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 更新角色状态
 */
export async function updateRoleStatus(id: number, status: number) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/${id}/status`, {
    method: 'PATCH',
    params: { status },
  });
}

/**
 * 删除角色
 */
export async function deleteRole(id: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 根据ID获取角色详情
 */
export async function getRoleById(id: number) {
  return request<ApiResponse<RoleResponse>>(`${API_PREFIX}/${id}`, {
    method: 'GET',
  });
}

/**
 * 根据编码获取角色
 */
export async function getRoleByCode(code: string) {
  return request<ApiResponse<RoleResponse>>(`${API_PREFIX}/code/${code}`, {
    method: 'GET',
  });
}

/**
 * 分页查询角色列表
 */
export async function queryRoles(params: RoleQueryParams) {
  return request<ApiResponse<PageResult<RoleResponse>>>(`${API_PREFIX}`, {
    method: 'GET',
    params,
  });
}

/**
 * 获取所有启用的角色
 */
export async function listEnabledRoles() {
  return request<ApiResponse<RoleResponse[]>>(`${API_PREFIX}/enabled`, {
    method: 'GET',
  });
}

/**
 * 获取角色最大排序值
 */
export async function getMaxRoleSort() {
  return request<ApiResponse<number>>(`${API_PREFIX}/max-sort`, {
    method: 'GET',
  });
}

