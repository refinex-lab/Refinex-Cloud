package cn.refinex.common.security.exception;

import cn.refinex.common.exception.BusinessException;

/**
 * 白名单异常
 * <p>
 * 当尝试对白名单用户执行封禁或踢人下线操作时抛出
 * </p>
 *
 * @author Refinex
 * @since 1.0.0
 */
public class WhitelistException extends BusinessException {

    private static final String MODULE = "SECURITY";
    private static final String ERROR_CODE = "SEC_1001";

    /**
     * 构造一个新的白名单异常
     *
     * @param message 异常消息
     */
    public WhitelistException(String message) {
        super(MODULE, ERROR_CODE, message);
    }

    /**
     * 构造一个新的白名单异常
     *
     * @param message 异常消息
     * @param cause   异常原因
     */
    public WhitelistException(String message, Throwable cause) {
        super(MODULE, ERROR_CODE, message, cause);
    }

    /**
     * 创建默认的白名单异常
     *
     * @param userId 用户 ID
     * @return 异常实例
     */
    public static WhitelistException create(Long userId) {
        return new WhitelistException(
                String.format("用户 [%d] 为白名单用户（管理员），不能执行封禁或踢人下线操作", userId)
        );
    }
}

