package cn.refinex.ai.core.client;

import cn.refinex.ai.enums.ModelType;
import cn.refinex.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * 聊天模型客户端实现
 * <p>
 * 封装 Spring AI 的 ChatModel，提供统一的聊天模型调用接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public record ChatModelClient(
        // 聊天模型实例
        ChatModel chatModel,
        // 供应商名称
        String providerName,
        // 模型编码
        String modelCode
) implements AiModelClient<Prompt, ChatResponse> {

    /**
     * 调用 AI 模型
     *
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    public ChatResponse invoke(Prompt request) {
        try {
            log.debug("调用聊天模型: provider={}, model={}", providerName, modelCode);
            return chatModel.call(request);
        } catch (Exception e) {
            log.error("聊天模型调用失败: provider={}, model={}", providerName, modelCode, e);
            throw new SystemException("聊天模型调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式调用 AI 模型
     * <p>
     * 适用于需要实时返回结果的场景，如聊天对话
     * </p>
     *
     * @param request 请求对象
     * @return 流式响应
     */
    @Override
    public Flux<ChatResponse> stream(Prompt request) {
        try {
            log.debug("流式调用聊天模型: provider={}, model={}", providerName, modelCode);
            return chatModel.stream(request);
        } catch (Exception e) {
            log.error("聊天模型流式调用失败: provider={}, model={}", providerName, modelCode, e);
            throw new SystemException("聊天模型流式调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取模型类型
     *
     * @return 模型类型枚举
     */
    @Override
    public ModelType getModelType() {
        return ModelType.TEXT_GENERATION;
    }

    /**
     * 检查模型健康状态
     *
     * @return true-健康，false-异常
     */
    @Override
    public boolean healthCheck() {
        try {
            // 简单的健康检查：发送一个测试消息
            Prompt testPrompt = new Prompt("hello");
            chatModel.call(testPrompt);
            log.debug("聊天模型健康检查通过: provider={}, model={}", providerName, modelCode);
            return true;
        } catch (Exception e) {
            log.warn("聊天模型健康检查失败: provider={}, model={}", providerName, modelCode, e);
            return false;
        }
    }

    /**
     * 获取底层 ChatModel 实例
     *
     * @return ChatModel 实例
     */
    @Override
    public ChatModel chatModel() {
        return chatModel;
    }
}

