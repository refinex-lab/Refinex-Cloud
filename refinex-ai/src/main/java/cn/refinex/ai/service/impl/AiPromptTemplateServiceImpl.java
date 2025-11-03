package cn.refinex.ai.service.impl;

import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateCreateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateQueryRequestDTO;
import cn.refinex.ai.controller.prompt.dto.request.PromptTemplateUpdateRequestDTO;
import cn.refinex.ai.controller.prompt.dto.response.PromptTemplateResponseDTO;
import cn.refinex.ai.converter.AiPromptTemplateConverter;
import cn.refinex.ai.entity.AiPromptTemplate;
import cn.refinex.ai.repository.AiPromptTemplateRepository;
import cn.refinex.ai.service.AiPromptTemplateService;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.common.utils.pinyin.PinyinUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 提示词模板服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiPromptTemplateServiceImpl implements AiPromptTemplateService {

    private final AiPromptTemplateRepository templateRepository;
    private final AiPromptTemplateConverter templateConverter;

    /**
     * 创建提示词模板
     *
     * @param request  创建请求
     * @param createBy 创建人 ID
     * @return 模板 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(PromptTemplateCreateRequestDTO request, Long createBy) {
        // 1. 自动生成模板编码（如果未提供）
        String templateCode = request.getTemplateCode();
        if (StrUtil.isBlank(templateCode)) {
            templateCode = generateTemplateCode(request.getTemplateName());
            log.info("自动生成模板编码：{} -> {}", request.getTemplateName(), templateCode);
        }

        // 2. 检查模板编码是否已存在
        if (templateRepository.checkTemplateCodeExists(templateCode, null)) {
            throw new BusinessException("模板编码已存在：" + templateCode);
        }

        // 3. 构建实体对象
        AiPromptTemplate template = BeanConverter.toBean(request, AiPromptTemplate.class);
        template.setTemplateCode(templateCode);
        // 初始版本号为 1
        template.setVersionNumber(1);
        template.setCreatorId(createBy);
        template.setUsageCount(0L);
        template.setLikeCount(0L);
        template.setCreateBy(createBy);
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateBy(createBy);
        template.setUpdateTime(LocalDateTime.now());
        template.setDeleted(0);
        template.setVersion(0);

        // 设置默认值
        if (template.getSort() == null) {
            template.setSort(0);
        }

        // 4. 插入数据库
        long id = templateRepository.insert(template);
        if (id <= 0) {
            throw new BusinessException("创建提示词模板失败");
        }

        return id;
    }

    /**
     * 根据模板名称生成唯一的模板编码
     * <p>
     * 规则：
     * 1. 提取中文首字母（大写）
     * 2. 如果编码已存在，则追加数字后缀（_1, _2, ...）
     * 3. 最大长度限制为 50 字符
     *
     * @param templateName 模板名称
     * @return 唯一的模板编码
     */
    private String generateTemplateCode(String templateName) {
        if (StrUtil.isBlank(templateName)) {
            throw new BusinessException("模板名称不能为空");
        }

        // 提取首字母并转大写
        String baseCode = PinyinUtil.toFirstLettersUpperCase(templateName);
        
        // 如果全是非中文字符，直接转大写
        if (StrUtil.isBlank(baseCode) || baseCode.equals(templateName)) {
            baseCode = templateName.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        }
        
        // 限制基础编码长度（留出空间给后缀）
        if (baseCode.length() > 45) {
            baseCode = baseCode.substring(0, 45);
        }

        // 检查编码是否已存在，如果存在则追加数字后缀
        String finalCode = baseCode;
        int suffix = 1;
        while (templateRepository.checkTemplateCodeExists(finalCode, null)) {
            finalCode = baseCode + "_" + suffix;
            suffix++;
            
            // 防止无限循环
            if (suffix > 1000) {
                throw new BusinessException("无法生成唯一的模板编码，请手动指定");
            }
        }

        return finalCode;
    }

    /**
     * 更新提示词模板
     *
     * @param id       模板 ID
     * @param request  更新请求
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTemplate(Long id, PromptTemplateUpdateRequestDTO request, Long updateBy) {
        // 1. 检查模板是否存在
        AiPromptTemplate existingTemplate = templateRepository.findById(id);
        if (existingTemplate == null) {
            throw new BusinessException("提示词模板不存在，ID=" + id);
        }

        // 2. 系统模板不允许修改
        if (existingTemplate.getIsSystem() != null && existingTemplate.getIsSystem() == 1) {
            throw new BusinessException("系统模板不允许修改");
        }

        // 3. 更新字段
        AiPromptTemplate template = BeanConverter.toBean(request, AiPromptTemplate.class);
        template.setId(id);
        template.setUpdateBy(updateBy);
        template.setUpdateTime(LocalDateTime.now());

        // 4. 更新数据库
        int rows = templateRepository.update(template);
        if (rows <= 0) {
            throw new BusinessException("更新提示词模板失败");
        }

        return true;
    }

    /**
     * 删除提示词模板
     *
     * @param id       模板 ID
     * @param updateBy 更新人 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id, Long updateBy) {
        // 1. 检查模板是否存在
        AiPromptTemplate existingTemplate = templateRepository.findById(id);
        if (existingTemplate == null) {
            throw new BusinessException("提示词模板不存在，ID=" + id);
        }

        // 2. 系统模板不允许删除
        if (existingTemplate.getIsSystem() != null && existingTemplate.getIsSystem() == 1) {
            throw new BusinessException("系统模板不允许删除");
        }

        // 3. 软删除
        int rows = templateRepository.deleteById(id, updateBy);
        if (rows <= 0) {
            throw new BusinessException("删除提示词模板失败");
        }
    }

    /**
     * 根据 ID 获取提示词模板详情
     *
     * @param id 模板 ID
     * @return 提示词模板详情
     */
    @Override
    public PromptTemplateResponseDTO getTemplateById(Long id) {
        AiPromptTemplate template = templateRepository.findById(id);
        if (template == null) {
            throw new BusinessException("提示词模板不存在，ID=" + id);
        }

        return templateConverter.toResponse(template);
    }

    /**
     * 根据编码获取提示词模板
     *
     * @param code 模板编码
     * @return 提示词模板
     */
    @Override
    public PromptTemplateResponseDTO getTemplateByCode(String code) {
        AiPromptTemplate template = templateRepository.findByCode(code);
        if (template == null) {
            throw new BusinessException("提示词模板不存在，编码=" + code);
        }

        return templateConverter.toResponse(template);
    }

    /**
     * 分页查询提示词模板
     *
     * @param query       查询条件
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    @Override
    public PageResult<PromptTemplateResponseDTO> pageQueryTemplates(PromptTemplateQueryRequestDTO query, PageRequest pageRequest) {
        // 1. 查询分页数据
        PageResult<AiPromptTemplate> pageResult = templateRepository.pageQuery(
                query.getCategory(),
                query.getType(),
                query.getIsPublic(),
                query.getStatus(),
                query.getKeyword(),
                pageRequest
        );

        // 2. 转换为响应对象
        List<PromptTemplateResponseDTO> responseList = pageResult.getRecords().stream()
                .map(templateConverter::toResponse)
                .toList();

        return new PageResult<>(
                responseList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
    }

    /**
     * 获取所有提示词模板列表（用于下拉选择）
     *
     * @return 提示词模板列表
     */
    @Override
    public List<PromptTemplateResponseDTO> listAllTemplates() {
        List<AiPromptTemplate> templates = templateRepository.findAll();
        return templates.stream()
                .map(templateConverter::toResponse)
                .toList();
    }

    /**
     * 切换模板状态
     *
     * @param id       模板 ID
     * @param status   状态
     * @param updateBy 更新人 ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleTemplateStatus(Long id, Integer status, Long updateBy) {
        // 1. 检查模板是否存在
        AiPromptTemplate existingTemplate = templateRepository.findById(id);
        if (existingTemplate == null) {
            throw new BusinessException("提示词模板不存在，ID=" + id);
        }

        // 2. 更新状态
        int rows = templateRepository.updateStatus(id, status, updateBy);
        if (rows <= 0) {
            throw new BusinessException("更新模板状态失败");
        }

        return true;
    }
}

