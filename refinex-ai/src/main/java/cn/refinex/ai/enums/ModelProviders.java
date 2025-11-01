package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 模型供应商枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ModelProviders {

    /**
     * OpenAI
     */
    OPENAI("OPENAI", "OpenAI"),

    /**
     * 智谱AI
     */
    ZHIPUAI("ZHIPUAI", "智谱AI"),

    /**
     * DeepSeek
     */
    DEEPSEEK("DEEPSEEK", "DeepSeek"),

    /**
     * 千问（Qwen）
     */
    QWEN("QWEN", "千问（Qwen）");

    /**
     * 模型供应商编码
     */
    private final String code;

    /**
     * 模型供应商名称
     */
    private final String name;

    /**
     * 根据模型供应商编码获取模型供应商枚举
     *
     * @param code 模型供应商编码
     * @return 模型供应商枚举
     */
    public static ModelProviders fromCode(String code) {
        for (ModelProviders modelProvider : ModelProviders.values()) {
            if (modelProvider.getCode().equals(code)) {
                return modelProvider;
            }
        }
        throw new IllegalArgumentException("No model provider found with code: " + code);
    }
}
