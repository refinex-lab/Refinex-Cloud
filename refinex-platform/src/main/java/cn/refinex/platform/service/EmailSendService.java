package cn.refinex.platform.service;

import cn.refinex.platform.controller.email.dto.request.EmailSendRequestDTO;
import cn.refinex.platform.controller.email.dto.response.EmailSendResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * 邮件发送服务
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface EmailSendService {

    /**
     * 同步发送邮件
     *
     * @param request 发送请求
     * @return 发送结果
     */
    EmailSendResponseDTO sendSync(EmailSendRequestDTO request);

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
}
