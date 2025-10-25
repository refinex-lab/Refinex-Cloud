/**
 * 字典管理相关类型定义
 */

/** 字典类型 */
export interface DictType {
  id: number;
  dictCode: string;
  dictName: string;
  dictDesc?: string;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
  deleted?: number;
  version?: number;
  remark?: string;
  status: number;
}

/** 字典数据 */
export interface DictData {
  id: number;
  dictTypeId: number;
  dictLabel: string;
  dictValue: string;
  dictSort: number;
  cssClass?: string;
  listClass?: string;
  isDefault: number;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
  deleted?: number;
  version?: number;
  remark?: string;
  status: number;
}

/** 字典类型创建请求 */
export interface DictTypeCreateRequest {
  dictCode: string;
  dictName: string;
  dictDesc?: string;
  remark?: string;
  status?: number;
}

/** 字典类型更新请求 */
export interface DictTypeUpdateRequest {
  dictName: string;
  dictDesc?: string;
  remark?: string;
  status?: number;
}

/** 字典数据创建请求 */
export interface DictDataCreateRequest {
  dictTypeId: number;
  dictLabel: string;
  dictValue: string;
  dictSort?: number;
  cssClass?: string;
  listClass?: string;
  isDefault?: number;
  remark?: string;
  status?: number;
}

/** 字典数据更新请求 */
export interface DictDataUpdateRequest {
  dictTypeId: number;
  dictLabel: string;
  dictValue: string;
  dictSort?: number;
  cssClass?: string;
  listClass?: string;
  isDefault?: number;
  remark?: string;
  status?: number;
}

/** 分页查询参数 */
export interface PageParams {
  pageNum?: number;
  pageSize?: number;
}

/** 字典类型查询参数 */
export interface DictTypeQueryParams extends PageParams {
  dictCode?: string;
  dictName?: string;
  status?: number;
}

/** 字典数据查询参数 */
export interface DictDataQueryParams extends PageParams {
  dictTypeId?: number;
  dictLabel?: string;
  dictValue?: string;
  status?: number;
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** API 响应结构 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  success: boolean;
}

