import { request } from '@umijs/max';

// 系统配置相关类型定义
export interface SysConfig {
  id: number;
  configKey: string;
  configValue: string;
  configType: string;
  configGroup?: string;
  configLabel?: string;
  configDesc?: string;
  isSensitive: number;
  isFrontend: number;
  sort: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

// 系统配置创建请求
export interface SysConfigCreateRequest {
  configKey: string;
  configValue: string;
  configType: string;
  configGroup?: string;
  configLabel?: string;
  configDesc?: string;
  isSensitive: number;
  isFrontend: number;
  sort?: number;
  remark?: string;
}

// 系统配置更新请求
export interface SysConfigUpdateRequest {
  configValue: string;
  configType: string;
  configGroup?: string;
  configLabel?: string;
  configDesc?: string;
  isSensitive: number;
  isFrontend: number;
  sort?: number;
  remark?: string;
}

// 系统配置查询参数
export interface SysConfigQueryParams {
  configKey?: string;
  configGroup?: string;
  configType?: string;
  isSensitive?: number;
  isFrontend?: number;
  pageNum?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: string;
}

// 系统配置查询请求
export interface SysConfigQueryRequest {
  configKey?: string;
  configGroup?: string;
  configType?: string;
  isSensitive?: number;
  isFrontend?: number;
}

// API 接口定义
const API_PREFIX = '/refinex-platform/system/configs';

/**
 * 创建系统配置
 */
export async function createSysConfig(data: SysConfigCreateRequest): Promise<{ data: number }> {
  return request(`${API_PREFIX}`, {
    method: 'POST',
    data,
  });
}

/**
 * 更新系统配置
 */
export async function updateSysConfig(id: number, data: SysConfigUpdateRequest): Promise<{ data: boolean }> {
  return request(`${API_PREFIX}/${id}`, {
    method: 'PUT',
    data,
  });
}

/**
 * 删除系统配置
 */
export async function deleteSysConfig(id: number): Promise<void> {
  return request(`${API_PREFIX}/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 获取配置详情
 */
export async function getSysConfig(id: number): Promise<{ data: SysConfig }> {
  return request(`${API_PREFIX}/${id}`, {
    method: 'GET',
  });
}

/**
 * 根据配置键获取配置
 */
export async function getSysConfigByKey(configKey: string): Promise<{ data: SysConfig }> {
  return request(`${API_PREFIX}/by-key/${configKey}`, {
    method: 'GET',
  });
}

/**
 * 根据分组获取配置列表
 */
export async function getSysConfigByGroup(group: string): Promise<{ data: SysConfig[] }> {
  return request(`${API_PREFIX}/by-group/${group}`, {
    method: 'GET',
  });
}

/**
 * 分页查询系统配置
 */
export async function querySysConfig(params: SysConfigQueryRequest & { pageNum?: number; pageSize?: number; sortField?: string; sortOrder?: string }): Promise<{ data: { records: SysConfig[]; total: number; pageNum: number; pageSize: number; pages: number } }> {
  return request(`${API_PREFIX}/search`, {
    method: 'POST',
    params: {
      pageNum: params.pageNum || 1,
      pageSize: params.pageSize || 10,
      sortField: params.sortField,
      sortOrder: params.sortOrder,
    },
    data: {
      configKey: params.configKey,
      configGroup: params.configGroup,
      configType: params.configType,
      isSensitive: params.isSensitive,
      isFrontend: params.isFrontend,
    },
  });
}

/**
 * 修改前端可见性
 */
export async function updateSysConfigFrontendVisibility(id: number, visible: number): Promise<{ data: boolean }> {
  return request(`${API_PREFIX}/${id}/frontend-visibility`, {
    method: 'PATCH',
    params: { visible },
  });
}
