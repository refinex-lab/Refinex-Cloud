package cn.refinex.platform.service;

import cn.refinex.platform.controller.email.dto.request.EmailSendRequestDTO;
import cn.refinex.platform.controller.email.dto.request.EmailTemplateDTO;
import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.platform.controller.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.platform.controller.email.dto.response.EmailSendResponseDTO;
import cn.refinex.platform.controller.email.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.platform.entity.email.EmailTemplate;

import java.util.List;
import java.util.Map;

/**
 * 邮件服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface EmailService {

    /**
     * 同步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    EmailSendResponseDTO sendSync(EmailSendRequestDTO request);

    /**
     * 异步发送邮件（入队）
     *
     * @param request 发送请求
     * @return 发送结果
     */
    EmailSendResponseDTO sendAsync(EmailSendRequestDTO request);

    /**
     * 批量发送邮件
     *
     * @param requests 发送请求列表
     * @return 发送结果列表
     */
    List<EmailSendResponseDTO> sendBatch(List<EmailSendRequestDTO> requests);

    /**
     * 使用模板发送邮件
     *
     * @param templateCode 模板编码
     * @param email        收件人邮箱
     * @param variables    模板变量
     * @return 发送结果
     */
    EmailSendResponseDTO sendWithTemplate(String templateCode, String email, Map<String, Object> variables);

    /**
     * 发送简单邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @return 发送结果
     */
    EmailSendResponseDTO sendSimple(String to, String subject, String content);

    /**
     * 取消队列任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    boolean cancelTask(String queueId);

    /**
     * 重试失败任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    boolean retryTask(String queueId);

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
     * 根据 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板信息
     */
    EmailTemplate getTemplateById(Long id);

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    EmailTemplate getTemplateByCode(String templateCode);

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    List<EmailTemplate> getAllAvailableTemplates();

    /**
     * 根据分类查询模板
     *
     * @param category 分类
     * @return 模板列表
     */
    List<EmailTemplate> getTemplatesByCategory(String category);

    /**
     * 预览模板
     *
     * @param templateCode 模板编码
     * @param variables    模板变量
     * @return 渲染后的内容
     */
    String previewTemplate(String templateCode, Map<String, Object> variables);

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateTemplateStatus(Long id, Integer status);

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    EmailVerifyCodeResponseDTO sendVerifyCode(EmailVerifyCodeRequestDTO request);

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    boolean verifyCode(EmailVerifyCodeValidateRequestDTO request);
}
