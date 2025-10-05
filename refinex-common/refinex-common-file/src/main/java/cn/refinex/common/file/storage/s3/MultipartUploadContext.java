package cn.refinex.common.file.storage.s3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Base64;

/**
 * 分片上传上下文
 * <p>
 * 用于在分片上传过程中传递上下文信息（configId、bucketName、key、uploadId）
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultipartUploadContext {

    /**
     * 存储配置 ID
     */
    private Long configId;

    /**
     * 存储桶名称
     */
    private String bucketName;

    /**
     * 对象 key
     */
    private String key;

    /**
     * S3 分片上传 ID
     */
    private String s3UploadId;

    /**
     * 编码为 Base64 字符串
     *
     * @return Base64 编码的上下文字符串
     */
    public String encode() {
        String contextStr = configId + ":" + bucketName + ":" + key + ":" + s3UploadId;
        return Base64.getUrlEncoder().encodeToString(contextStr.getBytes());
    }

    /**
     * 从 Base64 字符串解码
     *
     * @param encoded Base64 编码的上下文字符串
     * @return 分片上传上下文
     */
    public static MultipartUploadContext decode(String encoded) {
        String contextStr = new String(Base64.getUrlDecoder().decode(encoded));
        String[] parts = contextStr.split(":", 4);
        return new MultipartUploadContext(
                Long.parseLong(parts[0]),
                parts[1],
                parts[2],
                parts[3]
        );
    }
}

