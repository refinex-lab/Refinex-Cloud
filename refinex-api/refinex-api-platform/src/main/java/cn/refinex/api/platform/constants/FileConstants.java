package cn.refinex.api.platform.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 文件服务常量
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileConstants {

    /**
     * 文件服务路径前缀
     */
    public static final String FILE_API_PREFIX = "/platform/files";

    /**
     * 业务类型
     */
    public static final class BizType {
        public static final String AVATAR = "AVATAR";           // 用户头像
        public static final String DOCUMENT = "DOCUMENT";       // 文档
        public static final String IMAGE = "IMAGE";             // 图片
        public static final String VIDEO = "VIDEO";             // 视频
        public static final String AUDIO = "AUDIO";             // 音频
        public static final String ATTACHMENT = "ATTACHMENT";   // 附件
    }

    /**
     * 存储类型
     */
    public static final class StorageType {
        public static final String S3 = "S3";            // AWS S3
        public static final String OSS = "OSS";          // 阿里云 OSS
        public static final String COS = "COS";          // 腾讯云 COS
        public static final String KODO = "KODO";        // 七牛云 Kodo
        public static final String MINIO = "MINIO";      // MinIO
        public static final String DATABASE = "DATABASE"; // 数据库
    }

    /**
     * 访问权限
     */
    public static final class AccessType {
        public static final Integer PRIVATE = 0;  // 私有
        public static final Integer PUBLIC = 1;   // 公开
    }
}

