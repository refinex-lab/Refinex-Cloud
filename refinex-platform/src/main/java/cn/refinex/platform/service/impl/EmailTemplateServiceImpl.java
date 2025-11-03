package cn.refinex.platform.service.impl;

import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.exception.SystemException;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.email.dto.request.EmailTemplateDTO;
import cn.refinex.platform.entity.email.EmailTemplate;
import cn.refinex.platform.repository.email.EmailTemplateRepository;
import cn.refinex.platform.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 邮件模板服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository templateRepository;
    private final TemplateEngine emailTemplateEngine;
    private final SnowflakeIdGenerator idGenerator;

    /**
     * 创建模板
     *
     * @param templateDTO 模板 DTO
     * @return 模板 ID
     */
    @Override
    public Long createTemplate(EmailTemplateDTO templateDTO) {
        // 1. 检查模板编码是否已存在
        if (templateRepository.existsByTemplateCode(templateDTO.getTemplateCode())) {
            log.error("模板编码已存在: {}", templateDTO.getTemplateCode());
            throw new SystemException("邮件模板编码已存在");
        }

        // 2. 转换为实体
        EmailTemplate template = BeanConverter.toBean(templateDTO, EmailTemplate.class);
        template.setId(idGenerator.nextId());
        template.setCreateTime(LocalDateTime.now());
        template.setUpdateTime(LocalDateTime.now());
        template.setDeleted(0);
        template.setVersion(0);

        // 3. 转换变量列表为 JSON
        if (CollectionUtils.isNotEmpty(templateDTO.getVariables())) {
            template.setVariables(JsonUtils.toJson(templateDTO.getVariables()));
        }

        // 4. 插入数据库
        int rows = templateRepository.insert(template);
        if (rows <= 0) {
            log.error("创建模板失败: {}", templateDTO.getTemplateCode());
            throw new SystemException("邮件模板创建失败");
        }

        log.info("创建模板成功: code={}, id={}", template.getTemplateCode(), template.getId());
        return template.getId();
    }

    /**
     * 更新模板
     *
     * @param templateDTO 模板 DTO
     * @return 是否成功
     */
    @Override
    public boolean updateTemplate(EmailTemplateDTO templateDTO) {
        // 1. 检查模板是否存在
        EmailTemplate existingTemplate = templateRepository.findById(templateDTO.getId());
        if (existingTemplate == null) {
            log.error("模板不存在: id={}", templateDTO.getId());
            throw new SystemException("邮件模板不存在");
        }

        // 2. 转换为实体
        EmailTemplate template = BeanConverter.toBean(templateDTO, EmailTemplate.class);
        template.setUpdateTime(LocalDateTime.now());

        // 3. 转换变量列表为 JSON
        if (CollectionUtils.isNotEmpty(templateDTO.getVariables())) {
            template.setVariables(JsonUtils.toJson(templateDTO.getVariables()));
        }

        // 4. 更新数据库
        int rows = templateRepository.update(template);
        if (rows <= 0) {
            log.error("更新模板失败: id={}", templateDTO.getId());
            return false;
        }

        log.info("更新模板成功: code={}, id={}", template.getTemplateCode(), template.getId());
        return true;
    }

    /**
     * 删除模板
     *
     * @param id 模板 ID
     * @return 是否成功
     */
    @Override
    public boolean deleteTemplate(Long id) {
        // 1. 检查模板是否存在
        EmailTemplate template = templateRepository.findById(id);
        if (Objects.isNull(template)) {
            log.error("模板不存在: id={}", id);
            throw new SystemException("邮件模板不存在");
        }

        // 2. 系统模板不允许删除
        if (Objects.nonNull(template.getIsSystem()) && template.getIsSystem() == 1) {
            log.error("系统模板不允许删除: id={}", id);
            throw new SystemException("系统模板不允许删除");
        }

        // 3. 逻辑删除
        int rows = templateRepository.deleteById(id);
        if (rows <= 0) {
            log.error("删除模板失败: id={}", id);
            return false;
        }

        log.info("删除模板成功: code={}, id={}", template.getTemplateCode(), id);
        return true;
    }

    /**
     * 根据模板 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板实体
     */
    @Override
    public EmailTemplate getTemplateById(Long id) {
        EmailTemplate template = templateRepository.findById(id);
        if (Objects.isNull(template)) {
            log.error("模板不存在: id={}", id);
            throw new SystemException("邮件模板不存在");
        }
        return template;
    }

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板实体
     */
    @Override
    public EmailTemplate getTemplateByCode(String templateCode) {
        EmailTemplate template = templateRepository.findByTemplateCode(templateCode);
        if (Objects.isNull(template)) {
            log.error("模板不存在: code={}", templateCode);
            throw new SystemException("邮件模板不存在");
        }
        return template;
    }

    /**
     * 渲染模板
     *
     * @param templateCode 模板编码
     * @param variables    变量
     * @return 渲染后的内容
     */
    @Override
    public String renderTemplate(String templateCode, Map<String, Object> variables) {
        // 1. 获取模板
        EmailTemplate template = getTemplateByCode(templateCode);

        // 2. 检查模板状态
        if (Objects.nonNull(template.getStatus()) && template.getStatus().equals(SystemStatusConstants.DISABLE_VALUE)) {
            log.error("模板已停用: code={}", templateCode);
            throw new SystemException("邮件模板已停用");
        }

        // 3. 渲染模板
        try {
            Context context = new Context();
            if (MapUtils.isNotEmpty(variables)) {
                context.setVariables(variables);
            }

            String renderedContent = emailTemplateEngine.process(
                    "inline/" + templateCode,
                    context
            );

            log.debug("模板渲染成功: code={}", templateCode);
            return renderedContent;
        } catch (Exception e) {
            log.error("模板渲染失败: code={}", templateCode, e);
            throw new SystemException("邮件模板渲染失败", e);
        }
    }

    /**
     * 使用模板内容直接渲染（不从数据库加载）
     *
     * @param templateContent 模板内容
     * @param variables       变量
     * @return 渲染后的内容
     */
    @Override
    public String renderTemplateContent(String templateContent, Map<String, Object> variables) {
        try {
            Context context = new Context();
            if (MapUtils.isNotEmpty(variables)) {
                context.setVariables(variables);
            }

            // 使用 Thymeleaf 的字符串模板模式
            StringTemplateResolver resolver = new StringTemplateResolver();
            resolver.setTemplateMode(TemplateMode.HTML);

            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setTemplateResolver(resolver);

            return engine.process(templateContent, context);
        } catch (Exception e) {
            log.error("模板内容渲染失败", e);
            throw new SystemException("邮件模板渲染失败", e);
        }
    }

    /**
     * 预览模板
     *
     * @param templateCode 模板编码
     * @param variables    变量
     * @return 预览内容
     */
    @Override
    public String previewTemplate(String templateCode, Map<String, Object> variables) {
        // 1. 获取模板
        EmailTemplate template = getTemplateByCode(templateCode);

        // 2. 使用模板内容直接渲染
        return renderTemplateContent(template.getTemplateContent(), variables);
    }

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    @Override
    public List<EmailTemplate> getAllAvailableTemplates() {
        return templateRepository.findAllAvailable();
    }

    /**
     * 根据分类查询模板
     *
     * @param category 模板分类
     * @return 模板列表
     */
    @Override
    public List<EmailTemplate> getTemplatesByCategory(String category) {
        return templateRepository.findByCategory(category);
    }

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态（0正常、1停用）
     * @return 是否成功
     */
    @Override
    public boolean updateTemplateStatus(Long id, Integer status) {
        int rows = templateRepository.updateStatus(id, status);
        if (rows <= 0) {
            log.error("更新模板状态失败: id={}, status={}", id, status);
            return false;
        }
        log.info("更新模板状态成功: id={}, status={}", id, status);
        return true;
    }
}

