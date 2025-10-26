import {request} from '@umijs/max';

// API 接口定义
const API_PREFIX = '/refinex-platform/users';

/**
 * 模糊搜索用户名
 * @param keyword 用户名关键词
 * @param limit 返回数量限制（默认10，最大50）
 */
export async function searchUsernames(keyword: string, limit?: number) {
  return request<AUTH.ApiResult<string[]>>(`${API_PREFIX}/search-usernames`, {
    method: 'GET',
    params: {
      keyword,
      limit,
    },
  });
}

