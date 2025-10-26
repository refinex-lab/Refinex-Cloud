import { request } from '@umijs/max';
import type { ApiResponse, PageResult, PageParams } from '@/services/typings';

/** 用户列表项 */
export interface UserListItem {
  id: number;
  username: string;
  mobile?: string;
  email?: string;
  nickname?: string;
  sex?: string;
  avatar?: string;
  userStatus: number;
  userType: string;
  registerSource?: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  createTime: string;
  status: number;
  remark?: string;
}

/** 用户详情 */
export interface UserDetail {
  id: number;
  username: string;
  mobile?: string;
  email?: string;
  nickname?: string;
  sex?: string;
  avatar?: string;
  userStatus: number;
  userType: string;
  registerSource?: string;
  lastLoginTime?: string;
  lastLoginIp?: string;
  createBy?: number;
  createTime: string;
  updateBy?: number;
  updateTime: string;
  status: number;
  remark?: string;
  sort: number;
  extraData?: string;
}

/** 用户查询参数 */
export interface UserQueryParams extends PageParams {
  username?: string;
  mobile?: string;
  email?: string;
  nickname?: string;
  userStatus?: number;
  userType?: string;
  registerSource?: string;
  status?: number;
}

/** 用户更新请求 */
export interface UserUpdateRequest {
  userId: number;
  mobile?: string;
  email?: string;
  nickname?: string;
  sex?: string;
  avatar?: string;
  remark?: string;
}

/** 用户状态更新请求 */
export interface UserStatusUpdateRequest {
  userId: number;
  userStatus: number;
  reason?: string;
}

/** 管理员重置密码请求 */
export interface AdminResetPasswordRequest {
  userId: number;
  newPassword: string;
  reason?: string;
}

/** 敏感数据解密请求 */
export interface SensitiveDecryptRequest {
  tableName: string;
  rowGuid: string;
  fieldCode: string;
}

/** 敏感数据解密响应 */
export interface SensitiveDecryptResponse {
  plainValue: string;
}

const API_PREFIX = '/refinex-platform';

/**
 * 获取用户列表
 */
export async function getUserList(params: UserQueryParams & { current?: number }) {
  return request<ApiResponse<PageResult<UserListItem>>>(`${API_PREFIX}/users/list`, {
    method: 'GET',
    params: {
      ...params,
      pageNum: params.pageNum || params.current,
      pageSize: params.pageSize,
    },
  });
}

/**
 * 获取用户详情
 */
export async function getUserDetail(userId: number) {
  return request<ApiResponse<UserDetail>>(`${API_PREFIX}/users/${userId}/detail`, {
    method: 'GET',
  });
}

/**
 * 更新用户信息
 */
export async function updateUser(userId: number, data: UserUpdateRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/users/${userId}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 更新用户状态
 */
export async function updateUserStatus(userId: number, data: UserStatusUpdateRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/users/${userId}/status`, {
    method: 'PUT',
    data,
  });
}

/**
 * 管理员重置用户密码
 */
export async function adminResetPassword(userId: number, data: AdminResetPasswordRequest) {
  return request<ApiResponse<void>>(`${API_PREFIX}/users/${userId}/reset-password`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除用户
 */
export async function deleteUser(userId: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/users/${userId}`, {
    method: 'DELETE',
  });
}

/**
 * 解密敏感数据
 */
export async function decryptSensitiveData(params: SensitiveDecryptRequest) {
  return request<ApiResponse<SensitiveDecryptResponse>>(`${API_PREFIX}/common/sensitive/decrypt`, {
    method: 'POST',
    data: params,
  });
}

/**
 * 模糊搜索用户名
 * @param keyword 用户名关键词
 * @param limit 返回数量限制（默认10，最大50）
 */
export async function searchUsernames(keyword: string, limit?: number) {
  return request<ApiResponse<string[]>>(`${API_PREFIX}/users/search-usernames`, {
    method: 'GET',
    params: {
      keyword,
      limit,
    },
  });
}

