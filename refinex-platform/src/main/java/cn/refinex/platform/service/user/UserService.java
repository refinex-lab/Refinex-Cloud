package cn.refinex.platform.service.user;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.common.utils.regex.RegexUtils;
import cn.refinex.platform.client.user.dto.request.CreateUserRequest;
import cn.refinex.platform.domain.entity.sys.SysUser;
import cn.refinex.platform.enums.UserRegisterType;
import cn.refinex.platform.enums.UserStatus;
import cn.refinex.platform.repository.sys.SysUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static cn.refinex.common.constants.ModuleConstants.PLATFORM;
import static cn.refinex.common.exception.code.ResultCode.BAD_REQUEST;
import static cn.refinex.platform.exception.PlatformErrorCode.*;

/**
 * 用户服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final JdbcTemplateManager jdbcManager;
    private final SysUserRepository sysUserRepository;
    private final SensitiveDataService sensitiveDataService;
    private final SnowflakeIdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    public Boolean registerUser(CreateUserRequest request) {
        try {
            // 检查用户名是否重复
            String username = request.getUsername().trim();
            int count = sysUserRepository.checkUsernameExist(username);
            if (count > 0) {
                log.warn("注册用户失败，用户名已存在，username: {}", username);
                throw new BusinessException(PLATFORM, USERNAME_EXIST.getCode(), USERNAME_EXIST.getMessage());
            }

            // 密码强度校验(0-弱，1-中，2-强)
            String password = request.getPassword().trim();
            int passwordStrength = RegexUtils.getPasswordStrength(password);
            if (passwordStrength < 2) {
                log.warn("注册用户失败，密码强度不足，password: {}", password);
                throw new BusinessException(PLATFORM, PASSWORD_STRENGTH.getCode(), PASSWORD_STRENGTH.getMessage());
            }

            // 根据注册类型判断需要加密的字段
            String email = request.getEmail().trim();
            String phone = request.getMobile().trim();

            UserRegisterType registerType = UserRegisterType.valueOf(request.getRegisterType());
            switch (registerType) {
                case EMAIL -> {
                    // 检查邮箱是否重复
                    String emailEncrypted = sensitiveDataService.encryptValue(email);
                    int emailCount = sysUserRepository.checkEmailExist(emailEncrypted);
                    if (emailCount > 0) {
                        log.warn("注册用户失败，邮箱已存在，email: {}", email);
                        throw new BusinessException(PLATFORM, USERNAME_EXIST.getCode(), USERNAME_EXIST.getMessage());
                    }
                }
                case MOBILE -> {
                    // 检查手机号是否重复
                    String phoneEncrypted = sensitiveDataService.encryptValue(phone);
                    count = sysUserRepository.checkPhoneExist(phoneEncrypted);
                    if (count > 0) {
                        log.warn("注册用户失败，手机号已存在，phone: {}", phone);
                        throw new BusinessException(PLATFORM, PHONE_EXIST.getCode(), PHONE_EXIST.getMessage());
                    }
                }
                default -> throw new BusinessException(PLATFORM, BAD_REQUEST.getCode(), "注册类型错误");
            }

            SysUser sysUser = BeanConverter.toBean(request, SysUser.class);
            jdbcManager.executeInTransaction(manager -> {
                Long userId = idGenerator.nextId();
                sysUser.setId(userId);

                // 先维护敏感信息
                String fieldCode = registerType == UserRegisterType.EMAIL ? "email" : "mobile";
                String plainValue = registerType == UserRegisterType.EMAIL ? email : phone;
                sensitiveDataService.encryptAndStore("sys_user", userId.toString(), fieldCode, plainValue);

                // 脱敏敏感信息
                if (registerType == UserRegisterType.EMAIL) {
                    sysUser.setEmail(RegexUtils.desensitizeEmail(plainValue));
                } else {
                    sysUser.setMobile(RegexUtils.desensitizeMobile(plainValue));
                }

                // 密码加密
                sysUser.setPassword(passwordEncoder.encode(password));

                // 完善信息
                sysUser.setUserStatus(UserStatus.NORMAL.getValue());
                // 注册操作员默认为管理员
                sysUser.setCreateBy(1L);
                sysUser.setCreateTime(LocalDateTime.now());
                sysUser.setUpdateTime(LocalDateTime.now());

                // 插入用户信息
                sysUserRepository.register(sysUser);
                return true;
            });
        } catch (Exception e) {
            log.error("注册用户失败", e);
            return false;
        }

        return true;
    }
}
