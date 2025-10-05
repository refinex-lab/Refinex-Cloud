package cn.refinex.platform.service.mail;

import cn.refinex.common.mail.domain.dto.EmailTemplateDTO;
import cn.refinex.common.mail.domain.entity.EmailTemplate;
import cn.refinex.common.mail.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Platform 邮件模板服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformEmailTemplateService {

    private final EmailTemplateService emailTemplateService;

    /**
     * 创建模板
     *
     * @param templateDTO 模板 DTO
     * @return 模板 ID
     */
    public Long createTemplate(EmailTemplateDTO templateDTO) {
        log.info("创建邮件模板: code={}, name={}", templateDTO.getTemplateCode(), templateDTO.getTemplateName());
        return emailTemplateService.createTemplate(templateDTO);
    }

    /**
     * 更新模板
     *
     * @param templateDTO 模板 DTO
     * @return 是否成功
     */
    public boolean updateTemplate(EmailTemplateDTO templateDTO) {
        log.info("更新邮件模板: id={}, code={}", templateDTO.getId(), templateDTO.getTemplateCode());
        return emailTemplateService.updateTemplate(templateDTO);
    }

    /**
     * 删除模板
     *
     * @param id 模板 ID
     * @return 是否成功
     */
    public boolean deleteTemplate(Long id) {
        log.info("删除邮件模板: id={}", id);
        return emailTemplateService.deleteTemplate(id);
    }

    /**
     * 根据 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板信息
     */
    public EmailTemplate getTemplateById(Long id) {
        return emailTemplateService.getTemplateById(id);
    }

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    public EmailTemplate getTemplateByCode(String templateCode) {
        return emailTemplateService.getTemplateByCode(templateCode);
    }

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    public List<EmailTemplate> getAllAvailableTemplates() {
        return emailTemplateService.getAllAvailableTemplates();
    }

    /**
     * 根据分类查询模板
     *
     * @param category 分类
     * @return 模板列表
     */
    public List<EmailTemplate> getTemplatesByCategory(String category) {
        return emailTemplateService.getTemplatesByCategory(category);
    }

    /**
     * 预览模板
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的内容
     */
    public String previewTemplate(String templateCode, Map<String, Object> variables) {
        log.info("预览邮件模板: code={}", templateCode);
        return emailTemplateService.previewTemplate(templateCode, variables);
    }

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态
     * @return 是否成功
     */
    public boolean updateTemplateStatus(Long id, Integer status) {
        log.info("更新邮件模板状态: id={}, status={}", id, status);
        return emailTemplateService.updateTemplateStatus(id, status);
    }
}

