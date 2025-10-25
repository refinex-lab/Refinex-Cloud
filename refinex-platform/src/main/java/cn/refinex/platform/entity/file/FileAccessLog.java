package cn.refinex.platform.entity.file;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件访问日志实体类
 * <p>
 * 对应数据库表：file_access_log
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件访问日志实体")
public class FileAccessLog {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "文件ID", example = "1")
    private Long fileId;

    @Schema(description = "访问者ID", example = "1")
    private Long userId;

    @Schema(description = "访问IP", example = "192.168.1.100")
    private String accessIp;

    @Schema(description = "访问地区", example = "中国|华东|上海市|电信")
    private String accessRegion;

    @Schema(description = "用户代理", example = "Mozilla/5.0...")
    private String userAgent;

    @Schema(description = "来源页面", example = "https://example.com/page")
    private String referer;

    @Schema(description = "访问结果：0成功,1鉴权失败,2文件不存在", example = "0")
    private Integer accessResult;

    @Schema(description = "流量消耗字节数", example = "1048576")
    private Long trafficBytes;

    @Schema(description = "访问时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime accessTime;

    @Schema(description = "创建时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
