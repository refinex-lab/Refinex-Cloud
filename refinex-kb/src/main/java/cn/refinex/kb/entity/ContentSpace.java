package cn.refinex.kb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 内容空间实体类
 * <p>
 * 对应数据库表：content_space
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "内容空间实体")
public class ContentSpace {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "空间编码，全局唯一", example = "SPACE_20251026001")
    private String spaceCode;

    @Schema(description = "空间名称", example = "Java技术专栏")
    private String spaceName;

    @Schema(description = "空间描述", example = "分享Java相关的技术文章和教程")
    private String spaceDesc;

    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "空间拥有者用户ID", example = "1001")
    private Long ownerId;

    @Schema(description = "空间类型：0个人知识库,1课程专栏,2视频专栏", example = "0")
    private Integer spaceType;

    @Schema(description = "访问类型：0私有,1公开,2密码访问", example = "1")
    private Integer accessType;

    @Schema(description = "访问密码，加密存储")
    private String accessPassword;

    @Schema(description = "是否已发布：0否,1是", example = "1")
    private Integer isPublished;

    @Schema(description = "发布时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    @Schema(description = "浏览次数", example = "1250")
    private Long viewCount;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标记：0未删除,1已删除", example = "0")
    private Integer deleted;

    @Schema(description = "乐观锁版本号", example = "0")
    private Integer version;

    @Schema(description = "备注说明", example = "技术分享专栏")
    private String remark;

    @Schema(description = "排序字段", example = "0")
    private Integer sort;

    @Schema(description = "状态：1正常,0停用", example = "1")
    private Integer status;

    @Schema(description = "扩展数据（JSON格式）")
    private String extraData;
}

