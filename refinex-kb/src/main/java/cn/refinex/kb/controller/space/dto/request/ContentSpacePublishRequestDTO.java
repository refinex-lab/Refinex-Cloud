package cn.refinex.kb.controller.space.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 内容空间发布请求
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Schema(description = "内容空间发布请求")
public class ContentSpacePublishRequestDTO {

    @NotNull(message = "发布状态不能为空")
    @Schema(description = "是否发布：0取消发布,1发布", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer isPublished;

    @NotNull(message = "版本号不能为空")
    @Schema(description = "乐观锁版本号", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer version;
}

