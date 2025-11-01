package cn.refinex.ai.converter;

import cn.refinex.ai.controller.model.dto.response.ModelConfigResponseDTO;
import cn.refinex.ai.entity.AiModelConfig;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 模型配置转换接口
 *
 * @author Refinex
 * @since 1.0.0
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AiModelConfigConverter {

    /**
     * 转换为模型配置响应
     *
     * @param aiModelConfig 模型配置实体
     * @return 模型配置响应
     */
    ModelConfigResponseDTO toResponse(AiModelConfig aiModelConfig);
}
