package cn.refinex.platform.controller.common;

import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.service.SensitiveDataService;
import cn.refinex.platform.controller.common.dto.request.SensitiveDecryptRequestDTO;
import cn.refinex.platform.controller.common.dto.response.SensitiveDecryptResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 敏感数据管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/common/sensitive")
@RequiredArgsConstructor
@Tag(name = "敏感数据管理", description = "敏感数据解密等接口")
public class SensitiveDataController {

    private final SensitiveDataService sensitiveDataService;

    @PostMapping("/decrypt")
    @Operation(summary = "解密敏感数据", description = "根据表名、记录GUID和字段代码解密敏感数据明文")
    @Parameter(name = "request", description = "解密请求参数", required = true)
    public ApiResult<SensitiveDecryptResponseDTO> decryptSensitiveData(@Valid @RequestBody SensitiveDecryptRequestDTO request) {
        String plainValue = sensitiveDataService.queryAndDecrypt(
                request.getTableName(),
                request.getRowGuid(),
                request.getFieldCode()
        );

        return ApiResult.success(new SensitiveDecryptResponseDTO(plainValue));
    }
}

