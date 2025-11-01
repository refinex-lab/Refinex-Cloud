package cn.refinex.ai.service;

import cn.refinex.ai.controller.model.dto.request.ModelConfigCreateRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigQueryRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigUpdateRequestDTO;
import cn.refinex.ai.controller.model.dto.response.ModelConfigResponseDTO;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;

import java.util.List;

/**
 * AI 模型配置服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface AiModelConfigService {

    /**
     * 创建模型配置
     *
     * @param request  创建请求
     * @param createBy 创建人 ID
     * @return 配置 ID
     */
    Long createModelConfig(ModelConfigCreateRequestDTO request, Long createBy);

    /**
     * 更新模型配置
     *
     * @param id       配置 ID
     * @param request  更新请求
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    boolean updateModelConfig(Long id, ModelConfigUpdateRequestDTO request, Long updateBy);

    /**
     * 删除模型配置
     *
     * @param id       配置 ID
     * @param updateBy 更新人 ID
     */
    void deleteModelConfig(Long id, Long updateBy);

    /**
     * 根据 ID 获取模型配置详情
     *
     * @param id 配置 ID
     * @return 模型配置详情
     */
    ModelConfigResponseDTO getModelConfigById(Long id);

    /**
     * 分页查询模型配置
     *
     * @param query       查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<ModelConfigResponseDTO> pageQueryModelConfigs(ModelConfigQueryRequestDTO query, PageRequest pageRequest);

    /**
     * 获取所有模型配置列表（用于下拉选择）
     *
     * @return 模型配置列表
     */
    List<ModelConfigResponseDTO> listAllModelConfigs();

    /**
     * 切换模型状态
     *
     * @param id       配置 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    boolean toggleModelStatus(Long id, Integer status, Long updateBy);
}

