/**
 * 通用敏感数据服务
 */

import { request } from '@umijs/max';
import type { ApiResponse } from '@/services/typings';

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

/**
 * 解密敏感数据（通用接口）
 * @param servicePath 服务路径，如 '/refinex-platform' 或 '/refinex-ai'
 * @param params 解密参数
 */
export async function decryptSensitiveData(
  servicePath: string,
  params: SensitiveDecryptRequest,
) {
  return request<ApiResponse<SensitiveDecryptResponse>>(
    `${servicePath}/common/sensitive/decrypt`,
    {
      method: 'POST',
      data: params,
    },
  );
}

