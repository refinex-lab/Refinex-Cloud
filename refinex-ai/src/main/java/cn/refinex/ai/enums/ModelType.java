package cn.refinex.ai.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI模型类型枚举
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ModelType {

    /**
     * 文本生成模型（如 GPT-4、Claude、通义千问等）
     */
    TEXT_GENERATION("TEXT_GENERATION", "文本生成"),

    /**
     * 图像生成模型（如 DALL-E、Stable Diffusion、Midjourney等）
     */
    IMAGE_GENERATION("IMAGE_GENERATION", "图像生成"),

    /**
     * 语音识别模型（如 Whisper）
     */
    SPEECH_TO_TEXT("SPEECH_TO_TEXT", "语音识别"),

    /**
     * 语音合成模型（如 TTS）
     */
    TEXT_TO_SPEECH("TEXT_TO_SPEECH", "语音合成"),

    /**
     * 文本嵌入模型（用于向量化，如 text-embedding-ada-002）
     */
    EMBEDDING("EMBEDDING", "文本嵌入"),

    /**
     * 多模态模型（如 GPT-4V，支持图文混合）
     */
    MULTIMODAL("MULTIMODAL", "多模态"),

    /**
     * 代码生成模型（如 Codex、CodeLlama）
     */
    CODE_GENERATION("CODE_GENERATION", "代码生成"),

    /**
     * 视频生成模型（如 Sora）
     */
    VIDEO_GENERATION("VIDEO_GENERATION", "视频生成"),

    /**
     * 音频生成模型（如音乐生成）
     */
    AUDIO_GENERATION("AUDIO_GENERATION", "音频生成"),

    /**
     * 其他类型
     */
    OTHER("OTHER", "其他");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     *
     * @param code 类型代码
     * @return 枚举
     */
    public static ModelType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (ModelType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为生成类型模型
     *
     * @return 是否为生成类型
     */
    public boolean isGenerative() {
        return this == TEXT_GENERATION 
            || this == IMAGE_GENERATION 
            || this == CODE_GENERATION
            || this == VIDEO_GENERATION
            || this == AUDIO_GENERATION;
    }
}

