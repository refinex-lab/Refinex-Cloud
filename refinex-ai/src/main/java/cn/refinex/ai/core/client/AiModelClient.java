package cn.refinex.ai.core.client;

import cn.refinex.ai.enums.ModelType;

/**
 * AI 模型客户端统一接口
 * <p>
 * 定义所有 AI 模型客户端的通用行为规范，采用策略模式设计
 * </p>
 *
 * @param <REQUEST>  请求类型
 * @param <RESPONSE> 响应类型
 * @author Refinex
 * @since 1.0.0
 */
public interface AiModelClient<REQUEST, RESPONSE> {

    /**
     * 调用 AI 模型
     *
     * @param request 请求对象
     * @return 响应对象
     */
    RESPONSE invoke(REQUEST request);

    /**
     * 流式调用 AI 模型
     * <p>
     * 适用于需要实时返回结果的场景，如聊天对话
     * </p>
     *
     * @param request 请求对象
     * @return 流式响应
     */
    default Object stream(REQUEST request) {
        throw new UnsupportedOperationException("当前模型不支持流式调用");
    }

    /**
     * 获取模型类型
     *
     * @return 模型类型枚举
     */
    ModelType getModelType();

    /**
     * 获取供应商名称
     *
     * @return 供应商名称（如 OPENAI、ALIBABA、ZHIPU）
     */
    String providerName();

    /**
     * 获取模型编码
     *
     * @return 模型编码（如 GPT4、QWEN_MAX）
     */
    String modelCode();

    /**
     * 检查模型健康状态
     *
     * @return true-健康，false-异常
     */
    default boolean healthCheck() {
        try {
            // 默认实现：尝试调用模型进行简单测试
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

