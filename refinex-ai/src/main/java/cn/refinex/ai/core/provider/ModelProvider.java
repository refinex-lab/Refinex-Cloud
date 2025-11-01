package cn.refinex.ai.core.provider;

import cn.refinex.ai.entity.AiModelConfig;
import cn.refinex.ai.enums.ModelType;

/**
 * AI 模型供应商接口
 * <p>
 * 定义不同供应商创建模型实例的统一规范
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface ModelProvider {

    /**
     * 创建聊天模型实例
     *
     * @param config 模型配置
     * @return 聊天模型实例
     */
    Object createChatModel(AiModelConfig config);

    /**
     * 创建图像模型实例
     *
     * @param config 模型配置
     * @return 图像模型实例
     */
    Object createImageModel(AiModelConfig config);

    /**
     * 创建嵌入模型实例
     *
     * @param config 模型配置
     * @return 嵌入模型实例
     */
    Object createEmbeddingModel(AiModelConfig config);

    /**
     * 获取供应商名称
     *
     * @return 供应商名称
     */
    String getProviderName();

    /**
     * 检查是否支持指定的模型类型
     *
     * @param modelType 模型类型
     * @return true-支持，false-不支持
     */
    boolean supports(ModelType modelType);
}

