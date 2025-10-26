package cn.refinex.platform.controller.logger.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 登录日志响应DTO
 *
 * @author Refinex
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录日志响应DTO")
public class LogLoginResponseDTO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "登录用户名", example = "admin")
    private String username;

    @Schema(description = "登录方式", example = "PASSWORD")
    private String loginType;

    @Schema(description = "登录IP", example = "192.168.1.100")
    private String loginIp;

    @Schema(description = "登录地点", example = "中国|华东|上海市|电信")
    private String loginLocation;

    @Schema(description = "浏览器信息", example = "Chrome 120")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "设备类型", example = "PC")
    private String deviceType;

    @Schema(description = "登录状态：0成功,1失败", example = "0")
    private Integer loginStatus;

    @Schema(description = "失败原因", example = "密码错误")
    private String failReason;

    @Schema(description = "登录时间", example = "2025-10-05 14:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

