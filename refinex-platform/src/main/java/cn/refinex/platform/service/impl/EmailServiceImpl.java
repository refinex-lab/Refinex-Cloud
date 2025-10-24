package cn.refinex.platform.service.impl;

import cn.refinex.api.platform.client.email.dto.request.EmailSendRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailTemplateDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeRequestDTO;
import cn.refinex.api.platform.client.email.dto.request.EmailVerifyCodeValidateRequestDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailSendResponseDTO;
import cn.refinex.api.platform.client.email.dto.response.EmailVerifyCodeResponseDTO;
import cn.refinex.platform.domain.entity.email.EmailTemplate;
import cn.refinex.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 邮件发送服务实现类
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EmailSendServiceImpl emailSendService;
    private final EmailQueueServiceImpl emailQueueService;
    private final EmailTemplateServiceImpl emailTemplateService;
    private final EmailVerifyCodeServiceImpl verifyCodeService;

    /**
     * 同步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    @Override
    public EmailSendResponseDTO sendSync(EmailSendRequestDTO request) {
        return emailSendService.sendSync(request);
    }

    /**
     * 异步发送邮件（入队）
     *
     * @param request 发送请求
     * @return 发送结果
     */
    @Override
    public EmailSendResponseDTO sendAsync(EmailSendRequestDTO request) {
        return emailQueueService.enqueue(request);
    }

    /**
     * 批量发送邮件
     *
     * @param requests 发送请求列表
     * @return 发送结果列表
     */
    @Override
    public List<EmailSendResponseDTO> sendBatch(List<EmailSendRequestDTO> requests) {
        return emailSendService.sendBatch(requests);
    }

    /**
     * 使用模板发送邮件
     *
     * @param templateCode 模板编码
     * @param email        收件人邮箱
     * @param variables    模板变量
     * @return 发送结果
     */
    @Override
    public EmailSendResponseDTO sendWithTemplate(String templateCode, String email, Map<String, Object> variables) {
        return emailSendService.sendWithTemplate(templateCode, email, variables);
    }

    /**
     * 发送简单邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     * @return 发送结果
     */
    @Override
    public EmailSendResponseDTO sendSimple(String to, String subject, String content) {
        return emailSendService.sendSimple(to, subject, content);
    }

    /**
     * 取消队列任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    @Override
    public boolean cancelTask(String queueId) {
        return emailQueueService.cancelTask(queueId);
    }

    /**
     * 重试失败任务
     *
     * @param queueId 队列 ID
     * @return 是否成功
     */
    @Override
    public boolean retryTask(String queueId) {
        return emailQueueService.retryFailedTask(queueId);
    }

    /**
     * 创建模板
     *
     * @param templateDTO 模板 DTO
     * @return 模板 ID
     */
    @Override
    public Long createTemplate(EmailTemplateDTO templateDTO) {
        return emailTemplateService.createTemplate(templateDTO);
    }

    /**
     * 更新模板
     *
     * @param templateDTO 模板 DTO
     * @return 是否成功
     */
    @Override
    public boolean updateTemplate(EmailTemplateDTO templateDTO) {
        return emailTemplateService.updateTemplate(templateDTO);
    }

    /**
     * 删除模板
     *
     * @param id 模板 ID
     * @return 是否成功
     */
    @Override
    public boolean deleteTemplate(Long id) {
        return emailTemplateService.deleteTemplate(id);
    }

    /**
     * 根据 ID 获取模板
     *
     * @param id 模板 ID
     * @return 模板信息
     */
    @Override
    public EmailTemplate getTemplateById(Long id) {
        return emailTemplateService.getTemplateById(id);
    }

    /**
     * 根据模板编码获取模板
     *
     * @param templateCode 模板编码
     * @return 模板信息
     */
    @Override
    public EmailTemplate getTemplateByCode(String templateCode) {
        return emailTemplateService.getTemplateByCode(templateCode);
    }

    /**
     * 查询所有可用模板
     *
     * @return 模板列表
     */
    @Override
    public List<EmailTemplate> getAllAvailableTemplates() {
        return emailTemplateService.getAllAvailableTemplates();
    }

    /**
     * 根据分类查询模板
     *
     * @param category 分类
     * @return 模板列表
     */
    @Override
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
    @Override
    public String previewTemplate(String templateCode, Map<String, Object> variables) {
        return emailTemplateService.previewTemplate(templateCode, variables);
    }

    /**
     * 更新模板状态
     *
     * @param id     模板 ID
     * @param status 状态
     * @return 是否成功
     */
    @Override
    public boolean updateTemplateStatus(Long id, Integer status) {
        return emailTemplateService.updateTemplateStatus(id, status);
    }

    /**
     * 发送验证码
     *
     * @param request 验证码请求
     * @return 验证码结果
     */
    @Override
    public EmailVerifyCodeResponseDTO sendVerifyCode(EmailVerifyCodeRequestDTO request) {
        return verifyCodeService.sendVerifyCode(request);
    }

    /**
     * 验证验证码
     *
     * @param request 验证请求
     * @return 是否验证成功
     */
    @Override
    public boolean verifyCode(EmailVerifyCodeValidateRequestDTO request) {
        return verifyCodeService.verifyCode(request);
    }
}
