package cn.refinex.ai.service.impl;

import cn.refinex.ai.controller.model.dto.request.ModelConfigCreateRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigQueryRequestDTO;
import cn.refinex.ai.controller.model.dto.request.ModelConfigUpdateRequestDTO;
import cn.refinex.ai.controller.model.dto.response.ModelConfigResponseDTO;
import cn.refinex.ai.converter.AiModelConfigConverter;
import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.HealthStatus;
import cn.refinex.ai.repository.AiModelConfigRepository;
import cn.refinex.ai.service.AiModelConfigService;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.common.utils.regex.RegexUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 模型配置服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiModelConfigServiceImpl implements AiModelConfigService {

    private final AiModelConfigRepository modelConfigRepository;
    private final SensitiveDataService sensitiveDataService;
    private final AiModelConfigConverter modelConfigConverter;

    /**
     * 创建模型配置
     *
     * @param request  创建请求
     * @param createBy 创建人 ID
     * @return 配置 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createModelConfig(ModelConfigCreateRequestDTO request, Long createBy) {
        // 1. 检查模型编码是否已存在
        if (modelConfigRepository.checkModelCodeExists(request.getModelCode(), null)) {
            throw new BusinessException("模型编码已存在：" + request.getModelCode());
        }

        // 2. 构建实体对象
        AiModelConfig config = BeanConverter.toBean(request, AiModelConfig.class);;
        config.setHealthStatus(HealthStatus.HEALTHY.getCode());
        config.setCreateBy(createBy);
        config.setCreateTime(LocalDateTime.now());
        config.setUpdateBy(createBy);
        config.setUpdateTime(LocalDateTime.now());
        config.setDeleted(0);
        config.setVersion(0);

        // 设置默认值
        if (config.getSort() == null) {
            config.setSort(0);
        }
        if (config.getTimeoutSeconds() == null) {
            config.setTimeoutSeconds(60);
        }
        if (config.getRetryTimes() == null) {
            config.setRetryTimes(3);
        }
        if (config.getCircuitBreakerThreshold() == null) {
            config.setCircuitBreakerThreshold(10);
        }

        // API KEY 脱敏处理
        String apiKey = config.getApiKey();
        if (StringUtils.isNotBlank(apiKey)) {
            config.setApiKey(RegexUtils.desensitizeCustom(apiKey, 4, 4, "*"));
        }

        // 4. 插入数据库
        long configId = modelConfigRepository.insert(config);
        if (configId <= 0) {
            throw new BusinessException("创建模型配置失败");
        }

        // 5. 加密存储 API Key
        if (StringUtils.isNotBlank(apiKey)) {
            sensitiveDataService.encryptAndStore("ai_model_config", String.valueOf(configId), "api_key", apiKey);
        }

        return configId;
    }

    /**
     * 更新模型配置
     *
     * @param id       配置 ID
     * @param request  更新请求
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateModelConfig(Long id, ModelConfigUpdateRequestDTO request, Long updateBy) {
        // 1. 检查配置是否存在
        AiModelConfig existingConfig = modelConfigRepository.findById(id);
        if (existingConfig == null) {
            throw new BusinessException("模型配置不存在，ID=" + id);
        }

        // 2. 更新字段
        AiModelConfig config = BeanConverter.toBean(request, AiModelConfig.class);
        config.setId(id);
        config.setUpdateBy(updateBy);
        config.setUpdateTime(LocalDateTime.now());

        // 如果 API Key 不为空且未脱敏，则视为新值，需要脱敏处理
        String apiKey = request.getApiKey();
        if (StringUtils.isNotBlank(apiKey) && !apiKey.contains("*")) {
            config.setApiKey(RegexUtils.desensitizeCustom(apiKey, 4, 4, "*"));
        } else {
            config.setApiKey(request.getApiKey());
        }

        // 3. 更新数据库
        int rows = modelConfigRepository.update(config);
        if (rows <= 0) {
            throw new BusinessException("更新模型配置失败");
        }

        // 4. 更新 API Key 时，加密存储新的 API Key
        if (StringUtils.isNotBlank(apiKey) && !apiKey.contains("*")) {
            sensitiveDataService.updateSensitiveData("ai_model_config", String.valueOf(id), "api_key", apiKey);
        }

        return true;
    }

    /**
     * 删除模型配置
     *
     * @param id       配置 ID
     * @param updateBy 更新人 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteModelConfig(Long id, Long updateBy) {
        // 1. 检查配置是否存在
        AiModelConfig existingConfig = modelConfigRepository.findById(id);
        if (existingConfig == null) {
            throw new BusinessException("模型配置不存在，ID=" + id);
        }

        // 2. 软删除
        int rows = modelConfigRepository.deleteById(id, updateBy);
        if (rows <= 0) {
            throw new BusinessException("删除模型配置失败");
        }
    }

    /**
     * 根据 ID 获取模型配置详情
     *
     * @param id 配置 ID
     * @return 模型配置详情
     */
    @Override
    public ModelConfigResponseDTO getModelConfigById(Long id) {
        AiModelConfig config = modelConfigRepository.findById(id);
        if (config == null) {
            throw new BusinessException("模型配置不存在，ID=" + id);
        }

        return modelConfigConverter.toResponse(config);
    }

    /**
     * 分页查询模型配置
     *
     * @param query       查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<ModelConfigResponseDTO> pageQueryModelConfigs(ModelConfigQueryRequestDTO query, PageRequest pageRequest) {
        // 1. 查询分页数据
        PageResult<AiModelConfig> pageResult = modelConfigRepository.pageQuery(
                query.getProvider(),
                query.getModelType(),
                query.getStatus(),
                query.getKeyword(),
                pageRequest
        );

        // 2. 转换为响应对象
        List<ModelConfigResponseDTO> responseList = pageResult.getRecords().stream()
                .map(modelConfigConverter::toResponse)
                .toList();

        return new PageResult<>(
                responseList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
    }

    /**
     * 获取所有模型配置列表（用于下拉选择）
     *
     * @return 模型配置列表
     */
    @Override
    public List<ModelConfigResponseDTO> listAllModelConfigs() {
        List<AiModelConfig> configs = modelConfigRepository.findAll();
        return configs.stream()
                .map(modelConfigConverter::toResponse)
                .toList();
    }

    /**
     * 切换模型状态
     *
     * @param id       配置 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleModelStatus(Long id, Integer status, Long updateBy) {
        // 1. 检查配置是否存在
        AiModelConfig existingConfig = modelConfigRepository.findById(id);
        if (existingConfig == null) {
            throw new BusinessException("模型配置不存在，ID=" + id);
        }

        // 2. 更新状态
        int rows = modelConfigRepository.updateStatus(id, status, updateBy);
        if (rows <= 0) {
            throw new BusinessException("更新模型状态失败");
        }

        return true;
    }
}

