/**
 * 内容标签
 */
export interface ContentTag {
  /** 标签ID */
  id: number;
  /** 标签名称 */
  tagName: string;
  /** 标签颜色 */
  tagColor: string;
  /** 标签类型：0系统标签,1用户自定义标签 */
  tagType: number;
  /** 使用次数 */
  usageCount: number;
  /** 创建者ID */
  creatorId: number;
  /** 创建时间 */
  createTime: string;
  /** 更新时间 */
  updateTime: string;
  /** 备注 */
  remark?: string;
  /** 状态：0正常,1停用 */
  status: number;
}

/**
 * 标签分页查询参数
 */
export interface TagPageParams {
  /** 标签名称（可选） */
  tagName?: string;
  /** 标签类型（可选） */
  tagType?: number;
  /** 创建者ID（可选，管理端） */
  creatorId?: number;
  /** 页码 */
  pageNum: number;
  /** 每页数量 */
  pageSize: number;
}

/**
 * 标签分页结果
 */
export interface TagPageResult {
  /** 记录列表 */
  records: ContentTag[];
  /** 总记录数 */
  total: number;
  /** 当前页码 */
  pageNum: number;
  /** 每页数量 */
  pageSize: number;
}

