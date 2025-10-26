/**
 * 系统管理相关类型定义
 */

// 导入通用类型
import type { ApiResponse, PageResult, PageParams } from '@/services/typings';

/** 字典类型 */
export interface DictType {
  id: number;
  dictCode: string;
  dictName: string;
  dictDesc?: string;
  dictSort?: number;
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
  dictSort?: number;
  remark?: string;
  status?: number;
}

/** 字典类型更新请求 */
export interface DictTypeUpdateRequest {
  dictName: string;
  dictDesc?: string;
  dictSort?: number;
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

/** 分页查询参数（扩展通用 PageParams） */
export interface SystemPageParams extends PageParams {
  orderBy?: string;
  orderDirection?: string;
}

/** 字典类型查询参数 */
export interface DictTypeQueryParams extends SystemPageParams {
  dictCode?: string;
  dictName?: string;
  status?: number;
}

/** 字典数据查询参数 */
export interface DictDataQueryParams extends SystemPageParams {
  dictTypeId?: number;
  dictLabel?: string;
  dictValue?: string;
  status?: number;
}

/** 系统配置 */
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

/** 系统配置创建请求 */
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

/** 系统配置更新请求 */
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

/** 系统配置查询参数 */
export interface SysConfigQueryRequest {
  configKey?: string;
  configGroup?: string;
  configType?: string;
  isSensitive?: number;
  isFrontend?: number;
}

/** 系统配置分页查询参数 */
export interface SysConfigQueryParams extends SystemPageParams {
  configKey?: string;
  configGroup?: string;
  configType?: string;
  isSensitive?: number;
  isFrontend?: number;
}

/** 角色信息 */
export interface RoleResponse {
  id: number;
  roleCode: string;
  roleName: string;
  roleType: number;
  roleTypeLabel?: string;
  dataScope: number;
  dataScopeLabel?: string;
  isBuiltin: number;
  isBuiltinLabel?: string;
  sort?: number;
  remark?: string;
  status: number;
  statusLabel?: string;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
}

/** 角色创建请求 */
export interface RoleCreateRequest {
  roleCode: string;
  roleName: string;
  roleType: number;
  dataScope: number;
  sort?: number;
  remark?: string;
  status?: number;
}

/** 角色更新请求 */
export interface RoleUpdateRequest {
  roleName: string;
  roleType: number;
  dataScope: number;
  sort?: number;
  remark?: string;
  status?: number;
}

/** 角色查询参数 */
export interface RoleQueryParams {
  roleCode?: string;
  roleName?: string;
  roleType?: number;
  status?: number;
  orderBy?: string;
  orderDirection?: string;
  pageNum?: number;
  pageSize?: number;
}

/** 用户角色绑定请求 */
export interface UserRoleBindRequest {
  userIds: number[];
  validFrom?: string;
  validUntil?: string;
}

/** 角色用户响应 */
export interface RoleUserResponse {
  userId: number;
  username: string;
  nickname?: string;
  mobile?: string;
  email?: string;
  userStatus: number;
  userStatusLabel?: string;
  roleId: number;
  validFrom?: string;
  validUntil?: string;
  isTemporary?: boolean;
  bindTime?: string;
}

/** 角色用户查询参数 */
export interface RoleUserQueryParams extends PageParams {
  username?: string;
  nickname?: string;
  mobile?: string;
  email?: string;
}

// 重新导出通用类型，方便其他模块使用
export type { ApiResponse, PageResult };

