package cn.refinex.platform.controller;

import cn.refinex.api.platform.client.user.vo.CurrentUserVo;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.enums.HttpStatusCode;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.domain.dto.request.UserDisableRequest;
import cn.refinex.platform.domain.dto.request.UserKickoutRequest;
import cn.refinex.platform.domain.dto.response.UserDisableStatusResponse;
import cn.refinex.platform.domain.model.UserSessionDTO;
import cn.refinex.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 用户管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户信息、账号状态、会话管理等接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息")
    public ApiResult<CurrentUserVo> getCurrentUser() {
        Long userId = LoginHelper.getUserId();
        if (Objects.isNull(userId)) {
            throw new BusinessException(HttpStatusCode.UNAUTHORIZED, "用户未登录");
        }
        CurrentUserVo currentUser = userService.buildCurrentUserVo(userId);
        return ApiResult.success(currentUser);
    }

    @PostMapping("/{userId}/disable")
    @Operation(summary = "封禁用户账号", description = "封禁指定用户账号，支持全局封禁和分类封禁")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "request", description = "封禁请求", required = true)
    public ApiResult<Void> disableUser(@PathVariable Long userId, @Valid @RequestBody UserDisableRequest request) {
        userService.disableUser(userId, request);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/{userId}/disable")
    @Operation(summary = "解封用户账号", description = "解封指定用户账号")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "service", description = "服务类型", required = false)
    public ApiResult<Void> enableUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        userService.untieUser(userId, service);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/{userId}/disable-status")
    @Operation(summary = "查询用户封禁状态", description = "查询指定用户的封禁状态")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    @Parameter(name = "service", description = "服务类型")
    public ApiResult<UserDisableStatusResponse> getDisableStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        UserDisableStatusResponse status = userService.getUserStatus(userId, service);
        return ApiResult.success(status);
    }

    @GetMapping("/{userId}/sessions")
    @Operation(summary = "查询用户登录会话列表", description = "查询指定用户的所有登录设备和会话信息")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    public ApiResult<List<UserSessionDTO>> listUserSessions(@PathVariable Long userId) {
        List<UserSessionDTO> sessions = userService.listUserSessions(userId);
        return ApiResult.success(sessions);
    }

    @DeleteMapping("/sessions")
    @Operation(summary = "踢除用户会话", description = "将指定用户会话踢下线，支持按设备类型或 Token 踢出")
    @Parameter(name = "request", description = "踢除请求", required = true)
    public ApiResult<Void> kickoutSession(@Valid @RequestBody UserKickoutRequest request) {
        userService.kickoutUser(request);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/{userId}/sessions")
    @Operation(summary = "踢除用户所有会话", description = "将指定用户的所有设备踢下线")
    @Parameter(name = "userId", description = "用户 ID", required = true)
    public ApiResult<Void> kickoutAllSessions(@PathVariable Long userId) {
        userService.kickoutAll(userId);
        return ApiResult.success(HttpStatusCode.NO_CONTENT, null);
    }
}

