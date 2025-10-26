package cn.refinex.platform.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.refinex.common.constants.SystemRoleConstants;
import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.domain.model.LoginUser;
import cn.refinex.common.domain.model.SysRoleDTO;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.enums.UserSex;
import cn.refinex.common.enums.UserType;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.common.properties.RefinexBizProperties;
import cn.refinex.common.redis.id.RedisIdGenerator;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.common.utils.Fn;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.common.utils.regex.RegexUtils;
import cn.refinex.platform.controller.user.dto.request.*;
import cn.refinex.platform.controller.user.dto.response.UserDetailResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserDisableStatusResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserListResponseDTO;
import cn.refinex.platform.controller.user.dto.response.UserSessionResponseDTO;
import cn.refinex.platform.controller.user.vo.CurrentUserVo;
import cn.refinex.platform.controller.user.vo.SysUserVo;
import cn.refinex.platform.entity.sys.SysRole;
import cn.refinex.platform.entity.sys.SysUser;
import cn.refinex.platform.enums.RegisterSource;
import cn.refinex.platform.enums.UserRegisterType;
import cn.refinex.platform.enums.UserStatus;
import cn.refinex.platform.repository.sys.SysUserRepository;
import cn.refinex.platform.service.PermissionService;
import cn.refinex.platform.service.SysRoleService;
import cn.refinex.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 用户服务
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final JdbcTemplateManager jdbcManager;
    private final SysUserRepository sysUserRepository;
    private final SensitiveDataService sensitiveDataService;
    private final RedisIdGenerator redisIdGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserNotificationServiceImpl notificationService;
    private final PermissionService permissionService;
    private final RefinexBizProperties bizProperties;
    private final SysRoleService sysRoleService;

    /**
     * 根据用户 ID 获取用户名
     *
     * @param userId 用户 ID
     * @return 用户名
     */
    @Override
    public String getUsernameByUserId(Long userId) {
        return sysUserRepository.selectUsernameById(userId);
    }

    /**
     * 根据用户 ID 列表查询用户名映射
     *
     * @param userIds 用户 ID 列表
     * @return 用户名映射，键为用户 ID，值为用户名
     */
    @Override
    public Map<String, Object> getUsernameMap(List<Long> userIds) {
        return sysUserRepository.selectUsernameMap(userIds);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public LoginUser getUserInfoByUsername(String username) {
        SysUser sysUser = sysUserRepository.selectByUserName(username);
        if (Objects.isNull(sysUser)) {
            log.warn("根据用户名获取用户信息失败，用户名不存在，username: {}", username);
            throw new BusinessException("用户名不存在");
        }

        // 转换为登录用户模型
        SysUserVo sysUserVo = BeanConverter.toBean(sysUser, SysUserVo.class);
        sysUserVo.setUserId(sysUser.getId());
        return buildLoginUser(sysUserVo);
    }

    /**
     * 根据用户 ID 获取用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    @Override
    public LoginUser getUserInfoByUserId(Long userId) {
        SysUser sysUser = sysUserRepository.selectById(userId);
        if (Objects.isNull(sysUser)) {
            log.warn("根据用户 ID 获取用户信息失败，用户不存在，userId: {}", userId);
            throw new BusinessException("用户不存在");
        }

        // 转换为登录用户模型
        SysUserVo sysUserVo = BeanConverter.toBean(sysUser, SysUserVo.class);
        sysUserVo.setUserId(sysUser.getId());
        return buildLoginUser(sysUserVo);
    }

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    @Override
    public LoginUser getUserInfoByMobile(String mobile) {
        try {
            String encryptMobile = sensitiveDataService.encryptValue(mobile);
            SysUser sysUser = sysUserRepository.selectByMobile(encryptMobile);
            if (Objects.isNull(sysUser)) {
                log.warn("根据手机号获取用户信息失败，手机号不存在，mobile: {}", mobile);
                throw new BusinessException("手机号不存在");
            }

            // 转换为登录用户模型
            SysUserVo sysUserVo = BeanConverter.toBean(sysUser, SysUserVo.class);
            sysUserVo.setUserId(sysUser.getId());
            return buildLoginUser(sysUserVo);
        } catch (Exception e) {
            log.error("根据手机号获取用户信息失败，mobile: {}", mobile, e);
            throw new BusinessException(HttpStatusCode.INTERNAL_SERVER_ERROR, "根据手机号获取用户信息失败");
        }
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public LoginUser getUserInfoByEmail(String email) {
        try {
            String encryptEmail = sensitiveDataService.encryptValue(email);
            SysUser sysUser = sysUserRepository.selectByEmail(encryptEmail);
            if (Objects.isNull(sysUser)) {
                log.warn("根据邮箱获取用户信息失败，邮箱不存在，email: {}", email);
                throw new BusinessException("邮箱不存在");
            }

            // 检查用户状态
            if (!Objects.equals(sysUser.getStatus(), UserStatus.NORMAL.getValue())) {
                log.warn("根据邮箱获取用户信息失败，用户状态异常，userId: {}", sysUser.getId());
                throw new BusinessException("用户状态异常");
            }

            // 转换为登录用户模型
            SysUserVo sysUserVo = BeanConverter.toBean(sysUser, SysUserVo.class);
            return buildLoginUser(sysUserVo);
        } catch (Exception e) {
            log.error("根据邮箱获取用户信息失败，email: {}", email, e);
            throw new BusinessException(HttpStatusCode.INTERNAL_SERVER_ERROR, "根据邮箱获取用户信息失败");
        }
    }

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    @Override
    public Boolean registerUser(UserCreateRequestDTO request) {
        try {
            // 检查用户名是否重复
            String username = request.getUsername().trim();
            int count = sysUserRepository.checkUsernameExist(username);
            if (count > 0) {
                log.warn("注册用户失败，用户名已存在，username: {}", username);
                throw new BusinessException("用户名已存在");
            }

            // 密码强度校验(0-弱，1-中，2-强)
            String password = request.getPassword().trim();
            int passwordStrength = RegexUtils.getPasswordStrength(password);
            if (passwordStrength < 2) {
                log.warn("注册用户失败，密码强度不足，password: {}", password);
                throw new BusinessException("密码强度不足，必须包含字母、数字和特殊字符");
            }

            // 获取邮箱和手机号
            String email = StrUtil.isNotBlank(request.getEmail()) ? request.getEmail().trim() : null;
            String phone = StrUtil.isNotBlank(request.getMobile()) ? request.getMobile().trim() : null;

            // 根据注册类型验证主要字段
            UserRegisterType registerType = UserRegisterType.valueOf(request.getRegisterType());
            switch (registerType) {
                case EMAIL -> {
                    if (StrUtil.isBlank(email)) {
                        log.warn("注册用户失败，邮箱注册类型必须提供邮箱");
                        throw new BusinessException("邮箱注册类型必须提供邮箱");
                    }
                    // 检查邮箱是否重复
                    String emailEncrypted = sensitiveDataService.encryptValue(email);
                    int emailCount = sysUserRepository.checkEmailExist(emailEncrypted);
                    if (emailCount > 0) {
                        log.warn("注册用户失败，邮箱已存在，email: {}", email);
                        throw new BusinessException("邮箱已存在");
                    }
                }
                case MOBILE -> {
                    if (StrUtil.isBlank(phone)) {
                        log.warn("注册用户失败，手机注册类型必须提供手机号");
                        throw new BusinessException("手机注册类型必须提供手机号");
                    }
                    // 检查手机号是否重复
                    String phoneEncrypted = sensitiveDataService.encryptValue(phone);
                    count = sysUserRepository.checkPhoneExist(phoneEncrypted);
                    if (count > 0) {
                        log.warn("注册用户失败，手机号已存在，phone: {}", phone);
                        throw new BusinessException("手机号已存在");
                    }
                }
                default -> throw new BusinessException(HttpStatusCode.BAD_REQUEST, "注册类型错误");
            }

            // 如果同时提供了邮箱和手机号，都需要检查是否重复
            if (registerType == UserRegisterType.MOBILE && StrUtil.isNotBlank(email)) {
                String emailEncrypted = sensitiveDataService.encryptValue(email);
                int emailCount = sysUserRepository.checkEmailExist(emailEncrypted);
                if (emailCount > 0) {
                    log.warn("注册用户失败，邮箱已存在，email: {}", email);
                    throw new BusinessException("邮箱已存在");
                }
            }
            if (registerType == UserRegisterType.EMAIL && StrUtil.isNotBlank(phone)) {
                String phoneEncrypted = sensitiveDataService.encryptValue(phone);
                int phoneCount = sysUserRepository.checkPhoneExist(phoneEncrypted);
                if (phoneCount > 0) {
                    log.warn("注册用户失败，手机号已存在，phone: {}", phone);
                    throw new BusinessException("手机号已存在");
                }
            }

            SysUser sysUser = BeanConverter.toBean(request, SysUser.class);
            jdbcManager.executeInTransaction(manager -> {
                // 使用 Redis 自增生成用户 ID（从 1000 开始）
                Long userId = redisIdGenerator.nextId("user_id", 1000L);
                // Long userId = idGenerator.nextId();
                sysUser.setId(userId);

                // 加密存储邮箱（如果有）
                if (StrUtil.isNotBlank(email)) {
                    sensitiveDataService.encryptAndStore("sys_user", userId.toString(), "email", email);
                    sysUser.setEmail(RegexUtils.desensitizeEmail(email));
                } else {
                    sysUser.setEmail(null);
                }

                // 加密存储手机号（如果有）
                if (StrUtil.isNotBlank(phone)) {
                    sensitiveDataService.encryptAndStore("sys_user", userId.toString(), "mobile", phone);
                    sysUser.setMobile(RegexUtils.desensitizeMobile(phone));
                } else {
                    sysUser.setMobile(null);
                }

                // 密码加密
                sysUser.setPassword(passwordEncoder.encode(password));

                // 完善信息
                sysUser.setUserStatus(UserStatus.NORMAL.getValue());
                // 注册操作员默认为管理员
                sysUser.setCreateBy(LoginHelper.getUserId() != null ? LoginHelper.getUserId() : 1L);
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

    /**
     * 封禁账号
     *
     * @param userId  用户 ID
     * @param request 封禁请求
     */
    @Override
    public void disableUser(Long userId, UserDisableRequestDTO request) {
        log.info("封禁账号，userId: {}, request: {}", userId, request);

        // 1. 如果需要踢人下线，先踢下线
        if (Boolean.TRUE.equals(request.getKickout())) {
            StpUtil.kickout(userId);
            log.info("踢人下线成功，userId: {}", userId);
        }

        // 2. 执行封禁
        if (StrUtil.isNotBlank(request.getService())) {
            // 分类封禁
            StpUtil.disable(request.getUserId(), request.getService(), request.getSeconds());
            log.info("分类封禁成功，userId: {}, service: {}, seconds: {}, reason: {}",
                    request.getUserId(), request.getService(), request.getSeconds(), request.getReason());
        } else {
            // 全局封禁
            StpUtil.disable(request.getUserId(), request.getSeconds());
            log.info("全局封禁成功，userId: {}, seconds: {}, reason: {}",
                    request.getUserId(), request.getSeconds(), request.getReason());
        }

        // 3. 发送封禁通知邮件
        notificationService.sendDisableNotification(
                request.getUserId(),
                request.getService(),
                request.getSeconds(),
                request.getReason()
        );
    }

    /**
     * 解封账号
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     */
    @Override
    public void untieUser(Long userId, String service) {
        log.info("解封账号，userId: {}, service: {}", userId, service);

        if (StrUtil.isNotBlank(service)) {
            // 解封指定服务
            StpUtil.untieDisable(userId, service);
            log.info("解封指定服务成功，userId: {}, service: {}", userId, service);
        } else {
            // 解封全局
            StpUtil.untieDisable(userId);
            log.info("解封全局成功，userId: {}", userId);
        }

        // 发送解封通知邮件
        notificationService.sendUntieNotification(userId, service);
    }

    /**
     * 查询封禁状态
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     * @return 封禁状态
     */
    @Override
    public UserDisableStatusResponseDTO getUserStatus(Long userId, String service) {
        log.info("查询封禁状态，userId: {}, service: {}", userId, service);

        boolean disabled;
        long remainingTime;
        String disableType;

        if (StrUtil.isNotBlank(service)) {
            // 查询指定服务封禁状态
            disabled = StpUtil.isDisable(userId, service);
            remainingTime = StpUtil.getDisableTime(userId, service);
            disableType = service;
        } else {
            // 查询全局封禁状态
            disabled = StpUtil.isDisable(userId);
            remainingTime = StpUtil.getDisableTime(userId);
            disableType = "全局";
        }

        return UserDisableStatusResponseDTO.builder()
                .userId(userId)
                .disabled(disabled)
                .remainingTime(remainingTime)
                .disableType(disableType)
                .build();
    }

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    @Override
    public List<UserSessionResponseDTO> listUserSessions(Long userId) {
        log.info("查询用户登录设备列表，userId: {}", userId);

        List<UserSessionResponseDTO> sessions = new ArrayList<>();

        // 获取用户的所有 Token
        List<String> tokenValueList = StpUtil.getTokenValueListByLoginId(userId);

        for (String tokenValue : tokenValueList) {
            try {
                // 获取 Token 的 Session
                SaSession session = StpUtil.getSessionBySessionId("token:" + tokenValue);
                if (session == null) {
                    continue;
                }

                // 获取设备类型
                String deviceType = Fn.getString(session.get("deviceType"), null);
                if (StrUtil.isBlank(deviceType)) {
                    deviceType = "UNKNOWN";
                }

                // 获取 Token 剩余有效期
                long tokenTimeout = StpUtil.getTokenTimeout(tokenValue);

                // 构建会话信息
                // 注意：SaSession 没有 getUpdateTime() 方法，最后活跃时间使用创建时间代替
                UserSessionResponseDTO sessionDTO = UserSessionResponseDTO.builder()
                        .tokenValue(tokenValue)
                        .deviceType(deviceType)
                        .loginTime(session.getCreateTime())
                        .tokenTimeout(tokenTimeout)
                        // 使用创建时间代替
                        .lastActivityTime(session.getCreateTime())
                        .build();

                sessions.add(sessionDTO);
            } catch (Exception e) {
                log.warn("获取 Token 会话信息失败，tokenValue: {}", tokenValue, e);
            }
        }

        log.info("查询用户登录设备列表成功，userId: {}, sessionCount: {}", userId, sessions.size());
        return sessions;
    }

    /**
     * 踢人下线
     *
     * @param request 踢人下线请求
     */
    @Override
    public void kickoutUser(UserKickoutRequestDTO request) {
        log.info("踢人下线，request: {}", request);

        // 优先级：tokenValue > deviceType > userId
        if (StrUtil.isNotBlank(request.getTokenValue())) {
            // 按 Token 踢下线
            StpUtil.kickoutByTokenValue(request.getTokenValue());
            log.info("按 Token 踢人下线成功，tokenValue: {}", request.getTokenValue());
        } else if (StrUtil.isNotBlank(request.getDeviceType())) {
            // 按设备类型踢下线
            StpUtil.kickout(request.getUserId(), request.getDeviceType());
            log.info("按设备类型踢人下线成功，userId: {}, deviceType: {}", request.getUserId(), request.getDeviceType());
        } else {
            // 踢出所有设备
            StpUtil.kickout(request.getUserId());
            log.info("踢出所有设备成功，userId: {}", request.getUserId());
        }

        // 发送踢人下线通知邮件
        notificationService.sendKickoutNotification(
                request.getUserId(),
                request.getDeviceType(),
                request.getReason()
        );
    }

    /**
     * 踢出用户所有设备
     *
     * @param userId 用户 ID
     */
    @Override
    public void kickoutAll(Long userId) {
        log.info("踢出用户所有设备，userId: {}", userId);
        StpUtil.kickout(userId);
        log.info("踢出用户所有设备成功，userId: {}", userId);

        // 发送踢人下线通知邮件
        notificationService.sendKickoutNotification(userId, null, "管理员操作");
    }

    /**
     * 重置用户密码
     *
     * @param request 重置密码请求
     * @return 是否重置成功
     */
    @Override
    public Boolean resetPassword(ResetPasswordRequestDTO request) {
        try {
            // 获取密文邮箱
            String emailEncrypted = sensitiveDataService.encryptValue(request.getEmail());
            // 校验确认密码是否与新密码一致
            if (!request.getConfirmPassword().equals(request.getNewPassword())) {
                throw new BusinessException("确认密码与新密码不一致");
            }
            // 加密密码
            String passwordEncrypted = passwordEncoder.encode(request.getNewPassword());
            // 更新密码
            int updateCount = sysUserRepository.updatePassword(emailEncrypted, passwordEncrypted);
            return updateCount > 0;
        } catch (Exception e) {
            log.error("重置用户密码失败，email: {}", request.getEmail(), e);
            throw new BusinessException("重置用户密码失败");
        }
    }

    /**
     * 初始化超级管理员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initSuperAdmin() {
        // 检查是否已存在超级管理员
        SysUser sysUser = sysUserRepository.selectById(SystemRoleConstants.SUPER_ADMIN_ID);
        if (Objects.nonNull(sysUser)) {
            log.info("超级管理员已存在，跳过初始化");
            return;
        }

        log.info("开始初始化超级管理员...");

        // 创建超级管理员用户
        SysUser superAdmin = new SysUser();
        superAdmin.setId(SystemRoleConstants.SUPER_ADMIN_ID);
        superAdmin.setUsername(bizProperties.getSuperAdmin().getUsername());
        superAdmin.setMobile(RegexUtils.desensitizeMobile(bizProperties.getSuperAdmin().getMobile()));
        superAdmin.setEmail(RegexUtils.desensitizeEmail(bizProperties.getSuperAdmin().getEmail()));
        superAdmin.setPassword(passwordEncoder.encode(bizProperties.getSuperAdmin().getPassword()));
        superAdmin.setNickname(bizProperties.getSuperAdmin().getNickname());
        superAdmin.setUserStatus(UserStatus.NORMAL.getValue());
        superAdmin.setRegisterSource(RegisterSource.WEB.name());
        superAdmin.setCreateBy(SystemRoleConstants.SUPER_ADMIN_ID);
        superAdmin.setCreateTime(LocalDateTime.now());
        superAdmin.setUpdateBy(SystemRoleConstants.SUPER_ADMIN_ID);
        superAdmin.setUpdateTime(LocalDateTime.now());
        superAdmin.setSort(1);
        superAdmin.setSex(UserSex.MALE.getCode());
        superAdmin.setStatus(Convert.toInt(SystemStatusConstants.NORMAL));
        superAdmin.setUserType(UserType.SYS_USER.getCode());

        sysUserRepository.initSuperAdmin(superAdmin);

        // 维护邮箱、手机号的敏感数据
        sensitiveDataService.encryptAndStore(
                "sys_user",
                Convert.toStr(SystemRoleConstants.SUPER_ADMIN_ID),
                "mobile",
                bizProperties.getSuperAdmin().getMobile()
        );
        sensitiveDataService.encryptAndStore(
                "sys_user",
                Convert.toStr(SystemRoleConstants.SUPER_ADMIN_ID),
                "email",
                bizProperties.getSuperAdmin().getEmail()
        );

        // 绑定超级管理员角色
        sysRoleService.bindUserRole(
                SystemRoleConstants.SUPER_ADMIN_ROLE_ID,
                List.of(SystemRoleConstants.SUPER_ADMIN_ID),
                SystemRoleConstants.SUPER_ADMIN_ID)
        ;

        log.info("初始化超级管理员成功!");
    }

    //============================== 私有辅助方法 ======================================

    /**
     * 构建登录用户对象
     *
     * @param userVo 用户视图对象
     * @return 登录用户对象
     */
    private LoginUser buildLoginUser(SysUserVo userVo) {
        Long userId = userVo.getUserId();

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUserStatus(userVo.getUserStatus());
        loginUser.setUsername(userVo.getUsername());
        loginUser.setNickname(userVo.getNickname());
        loginUser.setPassword(userVo.getPassword());
        loginUser.setUserType(userVo.getUserType());
        loginUser.setMenuPermission(permissionService.getUserMenuPermissions(userId));
        loginUser.setRolePermission(permissionService.getUserRolePermissions(userId));

        List<SysRole> userRoles = permissionService.getUserRoles(userId);
        List<SysRoleDTO> sysRoleVos = BeanConverter.copyToList(userRoles, SysRoleDTO.class);
        loginUser.setRoles(sysRoleVos);

        return loginUser;
    }

    @Override
    public CurrentUserVo buildCurrentUserVo(Long userId) {
        SysUser sysUser = sysUserRepository.selectById(userId);
        if (Objects.isNull(sysUser)) {
            throw new BusinessException("用户不存在");
        }

        CurrentUserVo vo = new CurrentUserVo();
        vo.setUserId(sysUser.getId());
        vo.setUsername(sysUser.getUsername());
        vo.setNickname(sysUser.getNickname());
        vo.setSex(sysUser.getSex());
        vo.setAvatar(sysUser.getAvatar());
        vo.setEmail(sysUser.getEmail()); // 已是脱敏邮箱
        vo.setMobile(sysUser.getMobile()); // 已是脱敏手机号
        vo.setUserStatus(sysUser.getUserStatus());
        vo.setUserType(sysUser.getUserType());
        vo.setLastLoginIp(sysUser.getLastLoginIp());
        vo.setLastLoginTime(sysUser.getLastLoginTime());

        // 角色与权限
        List<SysRole> userRoles = permissionService.getUserRoles(userId);
        vo.setRoles(userRoles.stream().map(SysRole::getRoleCode).toList());
        vo.setPermissions(permissionService.getUserMenuPermissions(userId).stream().toList());
        return vo;
    }

    /**
     * 根据用户名关键词模糊搜索用户名列表
     *
     * @param keyword 用户名关键词
     * @param limit   返回数量限制
     * @return 用户名列表
     */
    @Override
    public List<String> searchUsernames(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        // 默认返回最多 10 条
        int queryLimit = (limit != null && limit > 0 && limit <= 50) ? limit : 10;

        return sysUserRepository.searchUsernamesByKeyword(keyword.trim(), queryLimit);
    }

    /**
     * 分页查询用户列表
     *
     * @param request 查询条件
     * @return 用户列表分页结果
     */
    @Override
    public PageResult<UserListResponseDTO> listUsers(UserQueryRequestDTO request) {
        return sysUserRepository.listUsers(request);
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @Override
    public UserDetailResponseDTO getUserDetail(Long userId) {
        SysUser sysUser = sysUserRepository.selectById(userId);
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }
        return BeanConverter.toBean(sysUser, UserDetailResponseDTO.class);
    }

    /**
     * 更新用户信息
     *
     * @param request 更新请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(UserUpdateRequestDTO request) {
        // 检查用户是否存在
        SysUser sysUser = sysUserRepository.selectById(request.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }

        // 超级管理员禁止更新状态
        if (LoginHelper.isSuperAdmin(request.getUserId())) {
            throw new BusinessException(HttpStatusCode.FORBIDDEN, "超级管理员禁止更新状态");
        }

        // 如果更新手机号，需要检查重复并加密存储
        if (StrUtil.isNotBlank(request.getMobile()) && !request.getMobile().equals(sysUser.getMobile())) {
            // 检查手机号是否已存在
            SysUser existingUser = sysUserRepository.selectByMobile(request.getMobile());
            if (existingUser != null && !existingUser.getId().equals(request.getUserId())) {
                throw new BusinessException(HttpStatusCode.CONFLICT, "手机号已被其他用户使用");
            }

            // 加密存储手机号
            sensitiveDataService.encryptAndStore("sys_user", String.valueOf(request.getUserId()), "mobile", request.getMobile());

            // 更新脱敏手机号
            sysUser.setMobile(RegexUtils.desensitizeMobile(request.getMobile()));
        }

        // 如果更新邮箱，需要检查重复并加密存储
        if (StrUtil.isNotBlank(request.getEmail()) && !request.getEmail().equals(sysUser.getEmail())) {
            // 检查邮箱是否已存在
            SysUser existingUser = sysUserRepository.selectByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(request.getUserId())) {
                throw new BusinessException(HttpStatusCode.CONFLICT, "邮箱已被其他用户使用");
            }

            // 加密存储邮箱
            sensitiveDataService.encryptAndStore("sys_user", String.valueOf(request.getUserId()), "email", request.getEmail());

            // 更新脱敏邮箱
            sysUser.setEmail(RegexUtils.desensitizeEmail(request.getEmail()));
        }

        // 更新其他字段
        if (StrUtil.isNotBlank(request.getNickname())) {
            sysUser.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getSex())) {
            sysUser.setSex(request.getSex());
        }
        if (StrUtil.isNotBlank(request.getAvatar())) {
            sysUser.setAvatar(request.getAvatar());
        }
        if (StrUtil.isNotBlank(request.getRemark())) {
            sysUser.setRemark(request.getRemark());
        }

        sysUser.setUpdateTime(LocalDateTime.now());
        sysUserRepository.update(sysUser);
    }

    /**
     * 更新用户状态
     *
     * @param request 状态更新请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(UserStatusUpdateRequestDTO request) {
        SysUser sysUser = sysUserRepository.selectById(request.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }

        // 超级管理员禁止更新状态
        if (LoginHelper.isSuperAdmin(request.getUserId())) {
            throw new BusinessException(HttpStatusCode.FORBIDDEN, "超级管理员禁止更新状态");
        }

        // 验证状态值合法性
        if (request.getUserStatus() < 0 || request.getUserStatus() > 3) {
            throw new BusinessException(HttpStatusCode.BAD_REQUEST, "用户状态值不合法");
        }

        sysUser.setUserStatus(request.getUserStatus());
        sysUser.setUpdateTime(LocalDateTime.now());
        if (StrUtil.isNotBlank(request.getReason())) {
            sysUser.setRemark(request.getReason());
        }

        sysUserRepository.update(sysUser);

        // 如果设置为冻结或注销状态，踢出所有在线会话
        if (request.getUserStatus() == 2 || request.getUserStatus() == 3) {
            kickoutAll(request.getUserId());
        }
    }

    /**
     * 管理员重置用户密码
     *
     * @param request 重置密码请求参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(AdminResetPasswordRequestDTO request) {
        SysUser sysUser = sysUserRepository.selectById(request.getUserId());
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }

        // 超级管理员禁止重制密码
        if (LoginHelper.isSuperAdmin(request.getUserId())) {
            throw new BusinessException(HttpStatusCode.FORBIDDEN, "超级管理员禁止重置密码");
        }

        // 加密新密码
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        sysUser.setPassword(encodedPassword);
        sysUser.setUpdateTime(LocalDateTime.now());

        sysUserRepository.update(sysUser);

        // 踢出所有在线会话，强制重新登录
        kickoutAll(request.getUserId());

        // TODO: 发送通知
        // if (StrUtil.isNotBlank(sysUser.getEmail())) {
        //     notificationService.notifyPasswordReset(sysUser, request.getReason());
        // }
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        SysUser sysUser = sysUserRepository.selectById(userId);
        if (Objects.isNull(sysUser)) {
            throw new BusinessException(HttpStatusCode.NOT_FOUND, "用户不存在");
        }

        // 不允许删除超级管理员
        if (LoginHelper.isSuperAdmin(userId)) {
            throw new BusinessException(HttpStatusCode.FORBIDDEN, "不允许删除超级管理员");
        }

        // 逻辑删除
        sysUserRepository.delete(userId);

        // 踢出所有在线会话
        kickoutAll(userId);
    }
}
