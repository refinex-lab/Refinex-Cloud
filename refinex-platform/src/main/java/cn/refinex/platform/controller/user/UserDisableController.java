package cn.refinex.platform.controller.user;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.controller.user.dto.request.DisableRequest;
import cn.refinex.platform.controller.user.dto.response.DisableStatusResponse;
import cn.refinex.platform.service.user.UserDisableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 账号封禁管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/admin/user-disable")
@RequiredArgsConstructor
@Tag(name = "账号封禁管理", description = "管理用户账号封禁，支持全局封禁和分类封禁")
public class UserDisableController {

    private final UserDisableService userDisableService;

    /**
     * 封禁账号
     *
     * @param request 封禁请求
     * @return 成功响应
     */
    @PostMapping("/disable")
    @Operation(summary = "封禁账号", description = "封禁指定用户账号，支持全局封禁和分类封禁")
    @SaCheckLogin
    public ApiResult<Void> disable(@Valid @RequestBody DisableRequest request) {
        userDisableService.disable(request);
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
    @SaCheckLogin
    public ApiResult<Void> untie(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        userDisableService.untie(userId, service);
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
    @SaCheckLogin
    public ApiResult<DisableStatusResponse> getStatus(
            @PathVariable Long userId,
            @RequestParam(required = false) String service
    ) {
        DisableStatusResponse status = userDisableService.getStatus(userId, service);
        return ApiResult.success(status);
    }
}

