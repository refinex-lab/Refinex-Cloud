package cn.refinex.ai.service;

import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateCreateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateQueryRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateUpdateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.response.PromptTemplateResponseDTO;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;

import java.util.List;

/**
 * AI 提示词模板服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface AiPromptTemplateService {

    /**
     * 创建提示词模板
     *
     * @param request  创建请求
     * @param createBy 创建人 ID
     * @return 模板 ID
     */
    Long createTemplate(PromptTemplateCreateRequestDTO request, Long createBy);

    /**
     * 更新提示词模板
     *
     * @param id       模板 ID
     * @param request  更新请求
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    boolean updateTemplate(Long id, PromptTemplateUpdateRequestDTO request, Long updateBy);

    /**
     * 删除提示词模板
     *
     * @param id       模板 ID
     * @param updateBy 更新人 ID
     */
    void deleteTemplate(Long id, Long updateBy);

    /**
     * 根据 ID 获取提示词模板详情
     *
     * @param id 模板 ID
     * @return 提示词模板详情
     */
    PromptTemplateResponseDTO getTemplateById(Long id);

    /**
     * 根据编码获取提示词模板
     *
     * @param code 模板编码
     * @return 提示词模板
     */
    PromptTemplateResponseDTO getTemplateByCode(String code);

    /**
     * 分页查询提示词模板
     *
     * @param query       查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<PromptTemplateResponseDTO> pageQueryTemplates(PromptTemplateQueryRequestDTO query, PageRequest pageRequest);

    /**
     * 获取所有提示词模板列表（用于下拉选择）
     *
     * @return 提示词模板列表
     */
    List<PromptTemplateResponseDTO> listAllTemplates();

    /**
     * 切换模板状态
     *
     * @param id       模板 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    boolean toggleTemplateStatus(Long id, Integer status, Long updateBy);
}

