package cn.refinex.ai.core.client;

import cn.refinex.ai.enums.ModelType;
import cn.refinex.common.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;

/**
 * 图像模型客户端实现
 * <p>
 * 封装 Spring AI 的 ImageModel，提供统一的图像生成模型调用接口
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
public record ImageModelClient(
        // 图像模型实例
        ImageModel imageModel,
        // 供应商名称
        String providerName,
        // 模型编码
        String modelCode
) implements AiModelClient<ImagePrompt, ImageResponse> {

    /**
     * 调用 AI 模型
     *
     * @param request 请求对象
     * @return 响应对象
     */
    @Override
    public ImageResponse invoke(ImagePrompt request) {
        try {
            log.debug("调用图像模型: provider={}, model={}", providerName, modelCode);
            return imageModel.call(request);
        } catch (Exception e) {
            log.error("图像模型调用失败: provider={}, model={}", providerName, modelCode, e);
            throw new SystemException("图像模型调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取模型类型
     *
     * @return 模型类型枚举
     */
    @Override
    public ModelType getModelType() {
        return ModelType.IMAGE_GENERATION;
    }

    /**
     * 检查模型健康状态
     *
     * @return true-健康，false-异常
     */
    @Override
    public boolean healthCheck() {
        try {
            // 图像模型健康检查：简单验证模型实例是否可用
            // 注意：实际调用图像生成可能比较昂贵，这里只做基础检查
            log.debug("图像模型健康检查通过: provider={}, model={}", providerName, modelCode);
            return imageModel != null;
        } catch (Exception e) {
            log.warn("图像模型健康检查失败: provider={}, model={}", providerName, modelCode, e);
            return false;
        }
    }

    /**
     * 获取底层 ImageModel 实例
     *
     * @return ImageModel 实例
     */
    @Override
    public ImageModel imageModel() {
        return imageModel;
    }
}

