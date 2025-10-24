package cn.refinex.platform.service;

import cn.refinex.api.platform.client.email.dto.request.EmailTemplateDTO;
import cn.refinex.platform.domain.entity.email.EmailTemplate;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface EmailTemplateService {

    /**
     * 创建模板
     *
     * @param templateDTO 模板 DTO
     * @return 模板 ID
     */
    Long createTemplate(EmailTemplateDTO templateDTO);

    /**
     * 更新模板
     *
     * @param templateDTO 模板 DTO
     * @return 是否成功
     */
    boolean updateTemplate(EmailTemplateDTO templateDTO);

    /**
     * 删除模板
     *
     * @param id 模板 ID
     * @return 是否成功
     */
    boolean deleteTemplate(Long id);

    /**
     * 根据模板 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板实体
     */
    EmailTemplate getTemplateById(Long id);

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板实体
     */
    EmailTemplate getTemplateByCode(String templateCode);

    /**
     * 渲染模板
     *
     * @param templateCode 模板编码
     * @param variables    变量
     * @return 渲染后的内容
     */
    String renderTemplate(String templateCode, Map<String, Object> variables);

    /**
     * 使用模板内容直接渲染（不从数据库加载）
     *
     * @param templateContent 模板内容
     * @param variables       变量
     * @return 渲染后的内容
     */
    String renderTemplateContent(String templateContent, Map<String, Object> variables);

    /**
     * 预览模板
     *
     * @param templateCode 模板编码
     * @param variables    变量
     * @return 预览内容
     */
    String previewTemplate(String templateCode, Map<String, Object> variables);

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    List<EmailTemplate> getAllAvailableTemplates();
    /**
     * 根据分类查询模板
     *
     * @param category 模板分类
     * @return 模板列表
     */
    List<EmailTemplate> getTemplatesByCategory(String category);

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态（0正常、1停用）
     * @return 是否成功
     */
    boolean updateTemplateStatus(Long id, Integer status);
}
