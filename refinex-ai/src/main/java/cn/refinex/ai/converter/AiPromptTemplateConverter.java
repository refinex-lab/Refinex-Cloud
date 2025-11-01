package cn.refinex.ai.converter;

import cn.refinex.ai.controller.prompt.dto.response.PromptTemplateResponseDTO;
import cn.refinex.ai.entity.AiPromptTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 提示词模板转换接口
 *
 * @author Refinex
 * @since 1.0.0
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AiPromptTemplateConverter {

    /**
     * 转换为提示词模板响应
     *
     * @param aiPromptTemplate 提示词模板实体
     * @return 提示词模板响应
     */
    PromptTemplateResponseDTO toResponse(AiPromptTemplate aiPromptTemplate);
}
