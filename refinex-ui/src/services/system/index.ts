// 导出字典和配置 API
export * from './dictionary';
export * from './config';

// 导出系统模块特定类型
export type {
  DictType,
  DictData,
  DictTypeCreateRequest,
  DictTypeUpdateRequest,
  DictDataCreateRequest,
  DictDataUpdateRequest,
  DictTypeQueryParams,
  DictDataQueryParams,
  SysConfig,
  SysConfigCreateRequest,
  SysConfigUpdateRequest,
  SysConfigQueryParams,
  SysConfigQueryRequest,
} from './typings';

// 从通用 typings 导出常用类型（方便使用）
export type { PageParams, PageResult, ApiResponse } from '@/services/typings';

