package cn.refinex.common.jdbc.page;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页结果对象
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@ToString
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页记录数
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private int pages;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    /**
     * 是否有上一页
     */
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private boolean hasNext;

    /**
     * 默认构造函数
     */
    public PageResult() {
        this.records = Collections.emptyList();
    }

    /**
     * 构造函数
     *
     * @param records  数据列表
     * @param total    总记录数
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     */
    public PageResult(List<T> records, long total, int pageNum, int pageSize) {
        this.records = records != null ? records : Collections.emptyList();
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (int) ((total + pageSize - 1) / pageSize) : 0;
        this.hasPrevious = pageNum > 1;
        this.hasNext = pageNum < pages;
    }

    /**
     * 创建空分页结果
     *
     * @param pageNum  当前页码
     * @param pageSize 每页记录数
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(int pageNum, int pageSize) {
        return new PageResult<>(Collections.emptyList(), 0, pageNum, pageSize);
    }
}
