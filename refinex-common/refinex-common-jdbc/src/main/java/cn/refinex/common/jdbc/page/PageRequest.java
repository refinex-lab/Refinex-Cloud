package cn.refinex.common.jdbc.page;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ToString
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码（从1开始）
     */
    private int pageNum;

    /**
     * 每页记录数（默认15条）
     */
    private int pageSize;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 排序方向（ASC/DESC）
     */
    private String orderDirection;

    /**
     * 默认构造函数
     */
    public PageRequest() {
        this.pageNum = 1;
        this.pageSize = 15;
    }

    /**
     * 构造函数
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     */
    public PageRequest(int pageNum, int pageSize) {
        this.pageNum = Math.max(pageNum, 1);
        this.pageSize = Math.max(pageSize, 1);
    }

    /**
     * 构造函数
     *
     * @param pageNum        当前页码
     * @param pageSize       每页记录数
     * @param orderBy        排序字段
     * @param orderDirection 排序方向
     */
    public PageRequest(int pageNum, int pageSize, String orderBy, String orderDirection) {
        this(pageNum, pageSize);
        this.orderBy = orderBy;
        this.orderDirection = orderDirection;
    }

    /**
     * 获取偏移量
     * <p>
     * 计算公式：(当前页码 - 1) * 每页记录数
     * <p>
     * 注意: SQL 的 LIMIT 子句的偏移量是从0开始的，而不是从1开始的。所以这里需要减1。
     *
     * @return 偏移量
     */
    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }

    /**
     * 获取限制数量
     *
     * @return 限制数量
     */
    public int getLimit() {
        return pageSize;
    }

    /**
     * 验证分页参数有效性
     *
     * @throws IllegalArgumentException 参数无效时抛出
     */
    public void validate() {
        if (pageNum < 1) {
            throw new IllegalArgumentException("页码必须大于等于1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("每页记录数必须大于等于1");
        }
        if (pageSize > 1000) {
            throw new IllegalArgumentException("每页记录数不能超过1000");
        }
    }
}
