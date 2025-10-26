/**
 * 通用类型定义
 * 所有服务共享的类型定义
 */

/** 后端统一响应格式 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  success: boolean;
  /** 是否为 2xx 成功状态码 */
  '2xxSuccess'?: boolean;
  /** 是否为客户端错误 */
  clientError?: boolean;
  /** 是否为服务器错误 */
  serverError?: boolean;
  /** 是否为错误 */
  error?: boolean;
  /** 时间戳 */
  timestamp?: number;
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 分页参数 */
export interface PageParams {
  pageNum?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: 'asc' | 'desc' | 'ascend' | 'descend';
}

