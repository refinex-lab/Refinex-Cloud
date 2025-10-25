import { request } from '@umijs/max';
import type {
  ApiResponse,
  DictData,
  DictDataCreateRequest,
  DictDataQueryParams,
  DictDataUpdateRequest,
  DictType,
  DictTypeCreateRequest,
  DictTypeQueryParams,
  DictTypeUpdateRequest,
  PageResult,
} from './typings';

const API_PREFIX = '/refinex-platform/dicts';

/**
 * 字典类型管理 API
 */

/** 创建字典类型 */
export async function createDictType(data: DictTypeCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}/types`, {
    method: 'POST',
    data,
  });
}

/** 更新字典类型 */
export async function updateDictType(id: number, data: DictTypeUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/types/${id}`, {
    method: 'PUT',
    data,
  });
}

/** 删除字典类型 */
export async function deleteDictType(id: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/types/${id}`, {
    method: 'DELETE',
  });
}

/** 获取字典类型详情 */
export async function getDictType(id: number) {
  return request<ApiResponse<DictType>>(`${API_PREFIX}/types/${id}`, {
    method: 'GET',
  });
}

/** 根据编码获取字典类型 */
export async function getDictTypeByCode(code: string) {
  return request<ApiResponse<DictType>>(`${API_PREFIX}/types/by-code/${code}`, {
    method: 'GET',
  });
}

/** 分页查询字典类型 */
export async function queryDictTypes(params: DictTypeQueryParams) {
  return request<ApiResponse<PageResult<DictType>>>(`${API_PREFIX}/types`, {
    method: 'GET',
    params,
  });
}

/** 获取所有启用的字典类型 */
export async function listEnabledDictTypes() {
  return request<ApiResponse<DictType[]>>(`${API_PREFIX}/types/enabled`, {
    method: 'GET',
  });
}

/**
 * 字典数据管理 API
 */

/** 创建字典数据 */
export async function createDictData(data: DictDataCreateRequest) {
  return request<ApiResponse<number>>(`${API_PREFIX}/data`, {
    method: 'POST',
    data,
  });
}

/** 更新字典数据 */
export async function updateDictData(id: number, data: DictDataUpdateRequest) {
  return request<ApiResponse<boolean>>(`${API_PREFIX}/data/${id}`, {
    method: 'PUT',
    data,
  });
}

/** 删除字典数据 */
export async function deleteDictData(id: number) {
  return request<ApiResponse<void>>(`${API_PREFIX}/data/${id}`, {
    method: 'DELETE',
  });
}

/** 批量删除字典数据 */
export async function batchDeleteDictData(ids: number[]) {
  return request<ApiResponse<number>>(`${API_PREFIX}/data`, {
    method: 'DELETE',
    params: { ids: ids.join(',') },
  });
}

/** 获取字典数据详情 */
export async function getDictData(id: number) {
  return request<ApiResponse<DictData>>(`${API_PREFIX}/data/${id}`, {
    method: 'GET',
  });
}

/** 根据类型编码获取字典数据 */
export async function listDictDataByTypeCode(code: string) {
  return request<ApiResponse<DictData[]>>(`${API_PREFIX}/types/${code}/data`, {
    method: 'GET',
  });
}

/** 分页查询字典数据 */
export async function queryDictData(params: DictDataQueryParams) {
  return request<ApiResponse<PageResult<DictData>>>(`${API_PREFIX}/data`, {
    method: 'GET',
    params,
  });
}

