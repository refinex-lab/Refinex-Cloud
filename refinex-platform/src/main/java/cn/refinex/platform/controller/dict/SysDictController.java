package cn.refinex.platform.controller.dict;

import cn.dev33.satoken.stp.StpUtil;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.domain.entity.sys.SysDictData;
import cn.refinex.platform.domain.entity.sys.SysDictType;
import cn.refinex.platform.service.dict.SysDictService;
import cn.refinex.platform.controller.dict.dto.request.DictTypeCreateRequest;
import cn.refinex.platform.controller.dict.dto.request.DictTypeUpdateRequest;
import cn.refinex.platform.controller.dict.dto.request.DictDataCreateRequest;
import cn.refinex.platform.controller.dict.dto.request.DictDataUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/platform/dict")
@RequiredArgsConstructor
@Tag(name = "字典接口", description = "字典类型与字典数据管理")

public class SysDictController {

    private final SysDictService dictService;

    // ===================== 字典类型 =====================

    @PostMapping("/type")
    @Operation(summary = "创建字典类型")
    @Parameter(name = "req", description = "字典类型创建请求参数", required = true)
    public ApiResult<Long> createType(@Valid @RequestBody DictTypeCreateRequest req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        Long id = dictService.createDictType(req.getDictCode(), req.getDictName(), req.getDictDesc(), req.getRemark(), req.getStatus(), operatorId);
        return ApiResult.success(id);
    }

    @PutMapping("/type")
    @Operation(summary = "更新字典类型")
    @Parameter(name = "req", description = "字典类型更新请求参数", required = true)
    public ApiResult<Boolean> updateType(@Valid @RequestBody DictTypeUpdateRequest req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.updateDictType(req.getId(), req.getDictName(), req.getDictDesc(), req.getRemark(), req.getStatus(), operatorId);
        return ApiResult.success(ok);
    }

    @DeleteMapping("/type/{id}")
    @Operation(summary = "删除字典类型")
    @Parameter(name = "id", description = "字典类型ID", required = true)
    public ApiResult<Boolean> deleteType(@Parameter(description = "字典类型ID") @PathVariable("id") Long id) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.deleteDictType(id, operatorId);
        return ApiResult.success(ok);
    }

    @GetMapping("/type/{id}")
    @Operation(summary = "根据ID获取字典类型")
    @Parameter(name = "id", description = "字典类型ID", required = true)
    public ApiResult<SysDictType> getTypeById(@Parameter(description = "字典类型ID") @PathVariable("id") Long id) {
        return ApiResult.success(dictService.getDictTypeById(id));
    }

    @GetMapping("/type/code/{code}")
    @Operation(summary = "根据编码获取字典类型（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<SysDictType> getTypeByCode(@Parameter(description = "字典类型编码") @PathVariable("code") String code) {
        return ApiResult.success(dictService.getDictTypeByCode(code));
    }

    @GetMapping("/type/page")
    @Operation(summary = "分页查询字典类型")
    public ApiResult<PageResult<SysDictType>> pageType(
            @Parameter(description = "字典类型编码，支持模糊") @RequestParam(value = "dictCode", required = false) String dictCode,
            @Parameter(description = "字典类型名称，支持模糊") @RequestParam(value = "dictName", required = false) String dictName,
            @Parameter(description = "状态：0正常,1停用") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "页码，从1开始") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize);
        return ApiResult.success(dictService.pageQueryDictTypes(dictCode, dictName, status, pr));
    }

    @GetMapping("/type/enabled")
    @Operation(summary = "查询所有启用的字典类型")
    public ApiResult<List<SysDictType>> listEnabledTypes() {
        return ApiResult.success(dictService.listEnabledTypes());
    }

    // ===================== 字典数据 =====================

    @PostMapping("/data")
    @Operation(summary = "创建字典数据")
    @Parameter(name = "req", description = "字典数据创建请求参数", required = true)
    public ApiResult<Long> createData(@Valid @RequestBody DictDataCreateRequest req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        Long id = dictService.createDictData(req.getDictTypeId(), req.getDictLabel(), req.getDictValue(), req.getDictSort(), req.getCssClass(), req.getListClass(), req.getIsDefault(), req.getRemark(), req.getStatus(), operatorId);
        return ApiResult.success(id);
    }

    @PutMapping("/data")
    @Operation(summary = "更新字典数据")
    @Parameter(name = "req", description = "字典数据更新请求参数", required = true)
    public ApiResult<Boolean> updateData(@Valid @RequestBody DictDataUpdateRequest req) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.updateDictData(req.getId(), req.getDictTypeId(), req.getDictLabel(), req.getDictValue(), req.getDictSort(), req.getCssClass(), req.getListClass(), req.getIsDefault(), req.getRemark(), req.getStatus(), operatorId);
        return ApiResult.success(ok);
    }

    @DeleteMapping("/data/{id}")
    @Operation(summary = "删除字典数据")
    @Parameter(name = "id", description = "字典数据ID", required = true)
    public ApiResult<Boolean> deleteData(@Parameter(description = "字典数据ID") @PathVariable("id") Long id) {
        Long operatorId = StpUtil.getLoginIdAsLong();
        boolean ok = dictService.deleteDictData(id, operatorId);
        return ApiResult.success(ok);
    }

    @DeleteMapping("/data/batch")
    @Operation(summary = "批量删除字典数据")
    @Parameter(name = "ids", description = "ID列表，逗号分隔", required = true)
    public ApiResult<Integer> batchDeleteData(@Parameter(description = "ID列表，逗号分隔") @RequestParam("ids") String ids) {
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
    @Operation(summary = "根据ID获取字典数据")
    @Parameter(name = "id", description = "字典数据ID", required = true)
    public ApiResult<SysDictData> getDataById(@Parameter(description = "字典数据ID") @PathVariable("id") Long id) {
        return ApiResult.success(dictService.getDictDataById(id));
    }

    @GetMapping("/data/type/{code}")
    @Operation(summary = "根据类型编码获取字典数据列表（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<List<SysDictData>> listDataByTypeCode(@Parameter(description = "字典类型编码") @PathVariable("code") String code) {
        return ApiResult.success(dictService.listDictDataByTypeCode(code));
    }

    @GetMapping("/data/page")
    @Operation(summary = "分页查询字典数据")
    public ApiResult<PageResult<SysDictData>> pageData(
            @Parameter(description = "字典类型ID") @RequestParam(value = "dictTypeId", required = false) Long dictTypeId,
            @Parameter(description = "字典标签，支持模糊") @RequestParam(value = "dictLabel", required = false) String dictLabel,
            @Parameter(description = "字典值，支持模糊") @RequestParam(value = "dictValue", required = false) String dictValue,
            @Parameter(description = "状态：0正常,1停用") @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "页码，从1开始") @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize);
        return ApiResult.success(dictService.pageQueryDictData(dictTypeId, dictLabel, dictValue, status, pr));
    }
}