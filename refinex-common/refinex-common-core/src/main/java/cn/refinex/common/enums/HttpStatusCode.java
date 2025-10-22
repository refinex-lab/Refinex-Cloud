package cn.refinex.common.enums;

import cn.refinex.common.exception.code.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP 状态码枚举
 * <p>
 * 用于规范 REST API 中 HTTP 状态码的正确使用，确保 API 响应的语义准确性。
 * <p>
 * 状态码分为五大类：
 * <ul>
 *   <li>2xx - 成功状态码：表示请求已成功被服务器接收、理解并处理</li>
 *   <li>3xx - 重定向状态码：表示需要客户端采取进一步的操作才能完成请求</li>
 *   <li>4xx - 客户端错误状态码：表示客户端请求有错误</li>
 *   <li>5xx - 服务器错误状态码：表示服务器在处理请求时发生了错误</li>
 * </ul>
 *
 * @author Refinex
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "HTTP 状态码")
public enum HttpStatusCode implements ErrorCode {

    // ==================== 2xx 成功状态码 ====================

    @Schema(description = "请求成功")
    OK(200, "OK", "操作成功", "请求已成功处理，适用于 GET、PUT、PATCH 等操作"),

    @Schema(description = "资源已创建")
    CREATED(201, "Created", "创建成功", "资源已成功创建，适用于 POST 请求创建新资源"),

    @Schema(description = "请求已接受")
    ACCEPTED(202, "Accepted", "请求已接受", "请求已被接受但尚未处理完成，适用于异步操作"),

    @Schema(description = "无返回内容")
    NO_CONTENT(204, "No Content", "操作成功", "请求已成功处理但无需返回内容，适用于 DELETE 操作或无返回值的更新操作"),

    // ==================== 3xx 重定向状态码 ====================

    @Schema(description = "资源已永久移动")
    MOVED_PERMANENTLY(301, "Moved Permanently", "资源已移动", "请求的资源已永久移动到新位置，API 端点已永久迁移"),

    @Schema(description = "资源临时移动")
    FOUND(302, "Found", "临时重定向", "请求的资源临时移动到其他位置"),

    @Schema(description = "资源未修改")
    NOT_MODIFIED(304, "Not Modified", "资源未变化", "资源未修改，可使用缓存的版本"),

    // ==================== 4xx 客户端错误状态码 ====================

    @Schema(description = "错误的请求")
    BAD_REQUEST(400, "Bad Request", "请求参数有误", "请求参数验证失败或格式不正确，请检查请求内容"),

    @Schema(description = "未授权访问")
    UNAUTHORIZED(401, "Unauthorized", "身份验证失败", "缺少或无效的认证信息，请先登录或提供有效的访问凭证"),

    @Schema(description = "禁止访问")
    FORBIDDEN(403, "Forbidden", "没有访问权限", "已通过身份验证但无权访问该资源，请联系管理员"),

    @Schema(description = "资源不存在")
    NOT_FOUND(404, "Not Found", "资源不存在", "请求的资源未找到，请确认资源标识是否正确"),

    @Schema(description = "方法不允许")
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "请求方法不支持", "HTTP 请求方法不被允许，请检查使用的 HTTP 方法"),

    @Schema(description = "资源冲突")
    CONFLICT(409, "Conflict", "操作冲突", "请求操作与资源的当前状态冲突，如重复注册、并发修改等"),

    @Schema(description = "资源已删除")
    GONE(410, "Gone", "资源已永久删除", "请求的资源已被永久删除且不再可用"),

    @Schema(description = "前置条件失败")
    PRECONDITION_FAILED(412, "Precondition Failed", "条件验证失败", "请求的前置条件验证失败，如 If-Match 头检查失败、乐观锁冲突等"),

    @Schema(description = "无法处理的实体")
    UNPROCESSABLE_ENTITY(422, "Unprocessable Entity", "业务处理失败", "请求格式正确但语义错误，业务逻辑验证未通过"),

    @Schema(description = "请求过于频繁")
    TOO_MANY_REQUESTS(429, "Too Many Requests", "请求过于频繁", "超过了 API 调用频率限制，请稍后再试"),

    // ==================== 5xx 服务器错误状态码 ====================

    @Schema(description = "服务器内部错误")
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "服务器错误", "服务器遇到了意外错误，请稍后重试或联系技术支持"),

    @Schema(description = "网关错误")
    BAD_GATEWAY(502, "Bad Gateway", "网关错误", "网关或代理服务器从上游服务器收到无效响应"),

    @Schema(description = "服务不可用")
    SERVICE_UNAVAILABLE(503, "Service Unavailable", "服务暂时不可用", "服务器暂时无法处理请求，可能正在维护或过载，请稍后重试"),

    @Schema(description = "网关超时")
    GATEWAY_TIMEOUT(504, "Gateway Timeout", "网关超时", "网关或代理服务器未能及时从上游服务器收到响应");

    /**
     * HTTP 状态码
     */
    @Schema(description = "HTTP 状态码")
    private final int code;

    /**
     * 英文标准名称（Reason Phrase）
     */
    @Schema(description = "英文标准名称")
    private final String reasonPhrase;

    /**
     * 中文用户友好提示
     */
    @Schema(description = "中文用户提示")
    private final String message;

    /**
     * 中文详细描述
     */
    @Schema(description = "中文详细描述")
    private final String description;

    /**
     * 根据状态码获取枚举
     *
     * @param code HTTP 状态码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果状态码不存在
     */
    public static HttpStatusCode fromCode(int code) {
        for (HttpStatusCode status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的 HTTP 状态码：" + code);
    }

    /**
     * 根据状态码获取枚举（安全模式，不抛出异常）
     *
     * @param code HTTP 状态码
     * @return 对应的枚举值，如果不存在则返回 null
     */
    public static HttpStatusCode fromCodeOrNull(int code) {
        for (HttpStatusCode status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否为成功状态码（2xx）
     *
     * @return 如果状态码在 200-299 范围内则返回 true
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    /**
     * 判断是否为重定向状态码（3xx）
     *
     * @return 如果状态码在 300-399 范围内则返回 true
     */
    public boolean isRedirection() {
        return code >= 300 && code < 400;
    }

    /**
     * 判断是否为客户端错误状态码（4xx）
     *
     * @return 如果状态码在 400-499 范围内则返回 true
     */
    public boolean isClientError() {
        return code >= 400 && code < 500;
    }

    /**
     * 判断是否为服务器错误状态码（5xx）
     *
     * @return 如果状态码在 500-599 范围内则返回 true
     */
    public boolean isServerError() {
        return code >= 500 && code < 600;
    }

    /**
     * 判断是否为错误状态码（4xx 或 5xx）
     *
     * @return 如果状态码在 400-599 范围内则返回 true
     */
    public boolean isError() {
        return isClientError() || isServerError();
    }
}

