package cn.refinex.platform.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.security.util.SecurityUtils;
import cn.refinex.platform.domain.dto.UserCreateRequest;
import cn.refinex.platform.domain.dto.UserUpdateRequest;
import cn.refinex.platform.domain.dto.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * <p>
 * 演示 Sa-Token 注解式鉴权的使用
 * </p>
 *
 * @author Refinex
 * @since 2025-10-04
 */
@Slf4j
@RestController
@RequestMapping("/platform/user")
@Tag(name = "用户管理", description = "用户信息管理接口")
public class UserController {

    /**
     * 查询用户列表（需要登录）
     *
     * @return 用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询用户列表", description = "查询所有用户信息")
    @SaCheckLogin
    public ApiResult<List<UserVO>> list() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        log.info("当前用户 ID: {}", currentUserId);

        // 业务逻辑（示例）
        return ApiResult.success(Collections.emptyList());
    }

    /**
     * 查询用户详情（需要 user:view 权限）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据 ID 查询用户详情")
    @SaCheckPermission("user:view")
    public ApiResult<UserVO> getById(@PathVariable Long id) {
        log.info("查询用户详情：id={}", id);

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }

    /**
     * 创建用户（需要 user:add 权限）
     *
     * @param request 创建用户请求
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "创建用户", description = "创建新用户")
    @SaCheckPermission("user:add")
    public ApiResult<Void> create(@Valid @RequestBody UserCreateRequest request) {
        log.info("创建用户：username={}", request.getUsername());

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }

    /**
     * 更新用户（需要 user:edit 权限）
     *
     * @param id      用户ID
     * @param request 更新用户请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户信息")
    @SaCheckPermission("user:edit")
    public ApiResult<Void> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        log.info("更新用户：id={}, nickname={}", id, request.getNickname());

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }

    /**
     * 删除用户（需要 user:delete 权限）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定用户")
    @SaCheckPermission("user:delete")
    public ApiResult<Void> delete(@PathVariable Long id) {
        log.info("删除用户：id={}", id);

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }

    /**
     * 管理员专用接口（需要 ROLE_ADMIN 角色）
     *
     * @return 用户统计信息
     */
    @GetMapping("/admin/statistics")
    @Operation(summary = "用户统计", description = "查询用户统计信息（管理员）")
    @SaCheckRole("ROLE_ADMIN")
    public ApiResult<Map<String, Object>> statistics() {
        log.info("查询用户统计信息");

        // 业务逻辑（示例）
        return ApiResult.success(Collections.emptyMap());
    }

    /**
     * 组合权限校验（需要同时满足多个权限）
     *
     * @param ids 用户ID列表
     * @return 操作结果
     */
    @PostMapping("/batch-delete")
    @Operation(summary = "批量删除用户", description = "批量删除用户（需要多个权限）")
    @SaCheckPermission(value = {"user:delete", "user:batch"}, mode = SaMode.AND)
    public ApiResult<Void> batchDelete(@RequestBody List<Long> ids) {
        log.info("批量删除用户：ids={}", ids);

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }

    /**
     * 或权限校验（满足任一权限即可）
     *
     * @return 操作结果
     */
    @GetMapping("/export")
    @Operation(summary = "导出用户", description = "导出用户数据")
    @SaCheckPermission(value = {"user:export", "admin:export"}, mode = SaMode.OR)
    public ApiResult<Void> export() {
        log.info("导出用户数据");

        // 业务逻辑（示例）
        return ApiResult.success(null);
    }
}

