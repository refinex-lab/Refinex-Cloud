package cn.refinex.kb.controller.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 内容空间创建请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "内容空间创建请求")
public class ContentSpaceCreateRequestDTO {

    @NotBlank(message = "空间名称不能为空")
    @Size(max = 128, message = "空间名称长度不能超过128个字符")
    @Schema(description = "空间名称", example = "Java技术专栏", requiredMode = Schema.RequiredMode.REQUIRED)
    private String spaceName;

    @Size(max = 1024, message = "空间描述长度不能超过1024个字符")
    @Schema(description = "空间描述", example = "分享Java相关的技术文章和教程")
    private String spaceDesc;

    @Size(max = 512, message = "封面图URL长度不能超过512个字符")
    @Schema(description = "封面图URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @NotNull(message = "空间类型不能为空")
    @Schema(description = "空间类型：0个人知识库,1课程专栏,2视频专栏", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer spaceType;

    @NotNull(message = "访问类型不能为空")
    @Schema(description = "访问类型：0私有,1公开,2密码访问", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accessType;

    @Size(max = 255, message = "访问密码长度不能超过255个字符")
    @Schema(description = "访问密码（访问类型为2时必填）", example = "123456")
    private String accessPassword;

    @Schema(description = "排序", example = "0")
    private Integer sort;

    @Schema(description = "备注说明", example = "技术分享专栏")
    private String remark;
}

