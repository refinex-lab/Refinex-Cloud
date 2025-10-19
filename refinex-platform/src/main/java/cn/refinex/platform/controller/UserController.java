package cn.refinex.platform.controller;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.api.domain.vo.CurrentUserVo;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.UserDisableRequest;
import cn.refinex.platform.domain.dto.request.UserKickoutRequest;
import cn.refinex.platform.domain.dto.response.UserDisableStatusResponse;
import cn.refinex.platform.domain.model.UserSessionDTO;
import cn.refinex.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 用户控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "管理用户相关操作")
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息（安全字段）")
    public ApiResult<CurrentUserVo> getCurrentUser() {
        Long userId = LoginHelper.getUserId();
        if (Objects.isNull(userId)) {
            throw new BusinessException("用户 ID 不能为空");
        }
        CurrentUserVo currentUser = userService.buildCurrentUserVo(userId);
        return ApiResult.success(currentUser);
    }

    /**
     * 封禁账号
     *
     * @param request 封禁请求
     * @return 成功响应
     */
    @PostMapping("/disable")
    @Operation(summary = "封禁账号", description = "封禁指定用户账号，支持全局封禁和分类封禁")
    public ApiResult<Void> disable(@Valid @RequestBody UserDisableRequest request) {
        userService.disableUser(request);
        return ApiResult.success(null);
    }

    /**
     * 解封账号
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     * @return 成功响应
     */
    @PostMapping("/untie/{userId}")
    @Operation(summary = "解封账号", description = "解封指定用户账号")
    public ApiResult<Void> untie(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        userService.untieUser(userId, service);
        return ApiResult.success(null);
    }

    /**
     * 查询封禁状态
     *
     * @param userId  用户 ID
     * @param service 服务类型（可选）
     * @return 封禁状态
     */
    @GetMapping("/status/{userId}")
    @Operation(summary = "查询封禁状态", description = "查询指定用户的封禁状态")
    public ApiResult<UserDisableStatusResponse> getStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        UserDisableStatusResponse status = userService.getUserStatus(userId, service);
        return ApiResult.success(status);
    }

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    @GetMapping("/list/{userId}")
    @Operation(summary = "查询用户登录设备列表", description = "查询指定用户的所有登录设备和会话信息")
    public ApiResult<List<UserSessionDTO>> listUserSessions(@PathVariable Long userId) {
        List<UserSessionDTO> sessions = userService.listUserSessions(userId);
        return ApiResult.success(sessions);
    }

    /**
     * 踢人下线
     *
     * @param request 踢人下线请求
     * @return 成功响应
     */
    @PostMapping("/kickout")
    @Operation(summary = "踢人下线", description = "将指定用户踢下线，支持按设备类型或 Token 踢出")
    public ApiResult<Void> kickout(@Valid @RequestBody UserKickoutRequest request) {
        userService.kickoutUser(request);
        return ApiResult.success(null);
    }

    /**
     * 踢出用户所有设备
     *
     * @param userId 用户 ID
     * @return 成功响应
     */
    @PostMapping("/kickout-all/{userId}")
    @Operation(summary = "踢出用户所有设备", description = "将指定用户的所有设备踢下线")
    public ApiResult<Void> kickoutAll(@PathVariable Long userId) {
        userService.kickoutAll(userId);
        return ApiResult.success(null);
    }
}

