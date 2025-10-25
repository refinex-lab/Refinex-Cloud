package cn.refinex.platform.controller.dict;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;
import cn.refinex.platform.service.impl.SysDictServiceImpl;
import cn.refinex.platform.controller.dict.dto.request.DictTypeCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictTypeUpdateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictDataCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictDataUpdateRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统字典管理控制器
 *
 * @author Refinex
 * @since 1.0.0
 */
@RestController
@RequestMapping("/system/dictionaries")
@RequiredArgsConstructor
@Tag(name = "系统字典管理", description = "字典类型和字典数据的增删改查等接口")
public class SysDictController {

    private final SysDictServiceImpl dictService;

    // ===================== 字典类型 =====================

    @PostMapping("/types")
    @Operation(summary = "创建字典类型", description = "创建新的字典类型")
    @Parameter(name = "req", description = "字典类型创建请求", required = true)
    public ApiResult<Long> createDictType(@Valid @RequestBody DictTypeCreateRequestDTO req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        Long id = dictService.createDictType(
                req.getDictCode(),
                req.getDictName(),
                req.getDictDesc(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.CREATED, id);
    }

    @PutMapping("/types/{id}")
    @Operation(summary = "更新字典类型", description = "更新指定字典类型的信息")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    @Parameter(name = "req", description = "字典类型更新请求", required = true)
    public ApiResult<Boolean> updateDictType(@PathVariable("id") Long id, @Valid @RequestBody DictTypeUpdateRequestDTO req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.updateDictType(
                id,
                req.getDictName(),
                req.getDictDesc(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(ok);
    }

    @DeleteMapping("/types/{id}")
    @Operation(summary = "删除字典类型", description = "删除指定的字典类型")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    public ApiResult<Void> deleteDictType(@PathVariable("id") Long id) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        dictService.deleteDictType(id, operatorId);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "获取字典类型详情", description = "根据 ID 获取字典类型详细信息")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    public ApiResult<SysDictType> getDictType(@PathVariable("id") Long id) {
        return ApiResult.success(dictService.getDictTypeById(id));
    }

    @GetMapping("/types/by-code/{code}")
    @Operation(summary = "根据编码获取字典类型", description = "根据字典类型编码获取详细信息（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<SysDictType> getDictTypeByCode(@PathVariable("code") String code) {
        return ApiResult.success(dictService.getDictTypeByCode(code));
    }

    @GetMapping("/types")
    @Operation(summary = "分页查询字典类型", description = "根据条件分页查询字典类型")
    @Parameter(name = "dictCode", description = "字典类型编码，支持模糊查询")
    @Parameter(name = "dictName", description = "字典类型名称，支持模糊查询")
    @Parameter(name = "status", description = "状态：0正常,1停用")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<SysDictType>> searchDictTypes(
            @RequestParam(value = "dictCode", required = false) String dictCode,
            @RequestParam(value = "dictName", required = false) String dictName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize);
        return ApiResult.success(dictService.pageQueryDictTypes(dictCode, dictName, status, pr));
    }

    @GetMapping("/types/enabled")
    @Operation(summary = "获取所有启用的字典类型", description = "查询所有状态为启用的字典类型")
    public ApiResult<List<SysDictType>> listEnabledDictTypes() {
        return ApiResult.success(dictService.listEnabledTypes());
    }

    // ===================== 字典数据 =====================

    @PostMapping("/data")
    @Operation(summary = "创建字典数据", description = "创建新的字典数据项")
     @Parameter(name = "req", description = "字典数据创建请求", required = true)
    public ApiResult<Long> createDictData(@Valid @RequestBody DictDataCreateRequestDTO req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        Long id = dictService.createDictData(
                req.getDictTypeId(),
                req.getDictLabel(),
                req.getDictValue(),
                req.getDictSort(),
                req.getCssClass(),
                req.getListClass(),
                req.getIsDefault(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.CREATED, id);
    }

    @PutMapping("/data/{id}")
    @Operation(summary = "更新字典数据", description = "更新指定字典数据的信息")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    @Parameter(name = "req", description = "字典数据更新请求", required = true)
    public ApiResult<Boolean> updateDictData(@PathVariable("id") Long id, @Valid @RequestBody DictDataUpdateRequestDTO req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.updateDictData(
                id,
                req.getDictTypeId(),
                req.getDictLabel(),
                req.getDictValue(),
                req.getDictSort(),
                req.getCssClass(),
                req.getListClass(),
                req.getIsDefault(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(ok);
    }

    @DeleteMapping("/data/{id}")
    @Operation(summary = "删除字典数据", description = "删除指定的字典数据")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    public ApiResult<Void> deleteDictData(@PathVariable("id") Long id) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        dictService.deleteDictData(id, operatorId);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/data")
    @Operation(summary = "批量删除字典数据", description = "批量删除多个字典数据项")
    @Parameter(name = "ids", description = "ID 列表，逗号分隔", required = true)
    public ApiResult<Integer> batchDeleteDictData(@RequestParam("ids") String ids) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        List<Long> idList = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (String s : ids.split(",")) {
                try {
                    idList.add(Long.parseLong(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int total = dictService.batchDeleteDictData(idList, operatorId);
        return ApiResult.success(total);
    }

    @GetMapping("/data/{id}")
    @Operation(summary = "获取字典数据详情", description = "根据 ID 获取字典数据详细信息")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    public ApiResult<SysDictData> getDictData(@PathVariable("id") Long id) {
        return ApiResult.success(dictService.getDictDataById(id));
    }

    @GetMapping("/types/{code}/data")
    @Operation(summary = "根据类型编码获取字典数据", description = "根据字典类型编码获取所有字典数据（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<List<SysDictData>> listDictDataByTypeCode(@PathVariable("code") String code) {
        return ApiResult.success(dictService.listDictDataByTypeCode(code));
    }

    @GetMapping("/data")
    @Operation(summary = "分页查询字典数据", description = "根据条件分页查询字典数据")
    @Parameter(name = "dictTypeId", description = "字典类型 ID")
    @Parameter(name = "dictLabel", description = "字典标签，支持模糊查询")
    @Parameter(name = "dictValue", description = "字典值，支持模糊查询")
    @Parameter(name = "status", description = "状态：0正常,1停用")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<SysDictData>> searchDictData(
            @RequestParam(value = "dictTypeId", required = false) Long dictTypeId,
            @RequestParam(value = "dictLabel", required = false) String dictLabel,
            @RequestParam(value = "dictValue", required = false) String dictValue,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize);
        return ApiResult.success(dictService.pageQueryDictData(dictTypeId, dictLabel, dictValue, status, pr));
    }
}