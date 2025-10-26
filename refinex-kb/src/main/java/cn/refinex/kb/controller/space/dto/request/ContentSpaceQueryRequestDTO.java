package cn.refinex.kb.controller.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 内容空间查询请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "内容空间查询请求")
public class ContentSpaceQueryRequestDTO {

    @Schema(description = "空间编码，支持模糊查询", example = "SPACE_")
    private String spaceCode;

    @Schema(description = "空间名称，支持模糊查询", example = "Java")
    private String spaceName;

    @Schema(description = "空间拥有者用户ID", example = "1001")
    private Long ownerId;

    @Schema(description = "空间类型：0个人知识库,1课程专栏,2视频专栏", example = "0")
    private Integer spaceType;

    @Schema(description = "访问类型：0私有,1公开,2密码访问", example = "1")
    private Integer accessType;

    @Schema(description = "是否已发布：0否,1是", example = "1")
    private Integer isPublished;

    @Schema(description = "状态：0正常,1停用", example = "0")
    private Integer status;
}

