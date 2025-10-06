package cn.refinex.platform.controller.user;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.platform.client.user.UserClient;
import cn.refinex.platform.client.user.dto.request.CreateUserRequest;
import cn.refinex.platform.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户服务控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor
public class UserController implements UserClient {

    private final UserService userService;

    /**
     * 注册用户
     *
     * @param request 创建用户请求参数
     * @return 注册结果
     */
    @Override
    public ApiResult<Boolean> registerUser(CreateUserRequest request) {
        return ApiResult.success(userService.registerUser(request));
    }
}
