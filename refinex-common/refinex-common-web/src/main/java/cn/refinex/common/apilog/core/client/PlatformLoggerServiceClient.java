package cn.refinex.common.apilog.core.client;

import cn.refinex.common.annotation.HttpInterfaceClient;
import cn.refinex.common.apilog.core.dto.request.LogOperationCreateRequestDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static cn.refinex.common.constants.SystemHttpServiceConstants.PLATFORM_SERVICE_NAME;

/**
 * 平台日志服务 HTTP Interface 客户端
 *
 * @author Refinex
 * @since 1.0.0
 */
@Validated
@HttpInterfaceClient(PLATFORM_SERVICE_NAME)
@HttpExchange("/logger")
public interface PlatformLoggerServiceClient {

    @PostExchange("/async")
    void saveLogOperationAsync(@RequestBody @Valid LogOperationCreateRequestDTO request);
}
