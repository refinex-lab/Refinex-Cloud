package cn.refinex.platform.controller.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.controller.user.dto.UserSessionDTO;
import cn.refinex.platform.controller.user.dto.request.KickoutRequest;
import cn.refinex.platform.service.user.UserSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户会话管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/admin/user-session")
@RequiredArgsConstructor
@Tag(name = "用户会话管理", description = "管理用户登录会话，支持踢人下线")
public class UserSessionController {

    private final UserSessionService userSessionService;

    /**
     * 查询用户登录设备列表
     *
     * @param userId 用户 ID
     * @return 会话列表
     */
    @GetMapping("/list/{userId}")
    @Operation(summary = "查询用户登录设备列表", description = "查询指定用户的所有登录设备和会话信息")
    @SaCheckLogin
    public ApiResult<List<UserSessionDTO>> listUserSessions(@PathVariable Long userId) {
        List<UserSessionDTO> sessions = userSessionService.listUserSessions(userId);
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
    @SaCheckLogin
    public ApiResult<Void> kickout(@Valid @RequestBody KickoutRequest request) {
        userSessionService.kickout(request);
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
    @SaCheckLogin
    public ApiResult<Void> kickoutAll(@PathVariable Long userId) {
        userSessionService.kickoutAll(userId);
        return ApiResult.success(null);
    }
}

