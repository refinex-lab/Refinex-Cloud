package cn.refinex.platform.controller.dict;

import cn.refinex.common.apilog.core.annotation.LogOperation;
import cn.refinex.common.apilog.core.enums.OperateTypeEnum;
import cn.refinex.common.domain.ApiResult;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.satoken.core.util.LoginHelper;
import cn.refinex.platform.controller.dict.dto.converter.DictConverter;
import cn.refinex.platform.controller.dict.dto.request.DictDataCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictDataUpdateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictTypeCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictTypeUpdateRequestDTO;
import cn.refinex.platform.controller.dict.dto.response.DictDataResponseDTO;
import cn.refinex.platform.controller.dict.dto.response.DictTypeResponseDTO;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;
import cn.refinex.platform.service.SysDictService;
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
@RequestMapping("/dicts")
@RequiredArgsConstructor
@Tag(name = "系统字典管理", description = "字典类型和字典数据的增删改查等接口")
public class SysDictController {

    private final SysDictService dictService;

    // ===================== 字典类型 =====================

    @PostMapping("/types")
    @LogOperation(operateDesc = "创建字典类型", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建字典类型", description = "创建新的字典类型")
    @Parameter(name = "req", description = "字典类型创建请求", required = true)
    public ApiResult<Long> createDictType(@Valid @RequestBody DictTypeCreateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        Long id = dictService.createDictType(
                req.getDictCode(),
                req.getDictName(),
                req.getDictDesc(),
                req.getDictSort(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.CREATED, id);
    }

    @PutMapping("/types/{id}")
    @LogOperation(operateDesc = "更新字典类型", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新字典类型", description = "更新指定字典类型的信息")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    @Parameter(name = "req", description = "字典类型更新请求", required = true)
    public ApiResult<Boolean> updateDictType(@PathVariable("id") Long id, @Valid @RequestBody DictTypeUpdateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        boolean ok = dictService.updateDictType(
                id,
                req.getDictName(),
                req.getDictDesc(),
                req.getDictSort(),
                req.getRemark(),
                req.getStatus(),
                operatorId
        );
        return ApiResult.success(ok);
    }

    @DeleteMapping("/types/{id}")
    @LogOperation(operateDesc = "删除字典类型", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除字典类型", description = "删除指定的字典类型")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    public ApiResult<Void> deleteDictType(@PathVariable("id") Long id) {
        Long operatorId = LoginHelper.getUserId();
        dictService.deleteDictType(id, operatorId);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }

    @GetMapping("/types/{id}")
    @Operation(summary = "获取字典类型详情", description = "根据 ID 获取字典类型详细信息")
    @Parameter(name = "id", description = "字典类型 ID", required = true)
    public ApiResult<DictTypeResponseDTO> getDictType(@PathVariable("id") Long id) {
        SysDictType dictType = dictService.getDictTypeById(id);
        return ApiResult.success(DictConverter.toTypeResponseDTO(dictType));
    }

    @GetMapping("/types/by-code/{code}")
    @Operation(summary = "根据编码获取字典类型", description = "根据字典类型编码获取详细信息（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<DictTypeResponseDTO> getDictTypeByCode(@PathVariable("code") String code) {
        SysDictType dictType = dictService.getDictTypeByCode(code);
        return ApiResult.success(DictConverter.toTypeResponseDTO(dictType));
    }

    @GetMapping("/types")
    @Operation(summary = "分页查询字典类型", description = "根据条件分页查询字典类型")
    @Parameter(name = "dictCode", description = "字典类型编码，支持模糊查询")
    @Parameter(name = "dictName", description = "字典类型名称，支持模糊查询")
    @Parameter(name = "status", description = "状态：0正常,1停用")
    @Parameter(name = "orderBy", description = "排序字段，如：dict_sort, create_time")
    @Parameter(name = "orderDirection", description = "排序方向：ASC 或 DESC")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<DictTypeResponseDTO>> searchDictTypes(
            @RequestParam(value = "dictCode", required = false) String dictCode,
            @RequestParam(value = "dictName", required = false) String dictName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize, orderBy, orderDirection);
        PageResult<SysDictType> pageResult = dictService.pageQueryDictTypes(dictCode, dictName, status, pr);
        
        // 转换为响应 DTO
        List<DictTypeResponseDTO> dtoList = DictConverter.toTypeResponseDTOList(pageResult.getRecords());
        PageResult<DictTypeResponseDTO> dtoPageResult = new PageResult<>(
                dtoList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
        
        return ApiResult.success(dtoPageResult);
    }

    @GetMapping("/types/enabled")
    @Operation(summary = "获取所有启用的字典类型", description = "查询所有状态为启用的字典类型")
    public ApiResult<List<DictTypeResponseDTO>> listEnabledDictTypes() {
        List<SysDictType> dictTypes = dictService.listEnabledTypes();
        return ApiResult.success(DictConverter.toTypeResponseDTOList(dictTypes));
    }

    @GetMapping("/types/max-sort")
    @Operation(summary = "获取字典类型最大排序值", description = "获取当前字典类型的最大排序值，用于新增时自动计算排序")
    public ApiResult<Integer> getMaxDictTypeSort() {
        Integer maxSort = dictService.getMaxDictTypeSort();
        return ApiResult.success(maxSort);
    }

    // ===================== 字典数据 =====================

    @PostMapping("/data")
    @LogOperation(operateDesc = "创建字典数据", operationType = OperateTypeEnum.CREATE)
    @Operation(summary = "创建字典数据", description = "创建新的字典数据项")
    @Parameter(name = "req", description = "字典数据创建请求", required = true)
    public ApiResult<Long> createDictData(@Valid @RequestBody DictDataCreateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        Long id = dictService.createDictData(req, operatorId);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.CREATED, id);
    }

    @PutMapping("/data/{id}")
    @LogOperation(operateDesc = "更新字典数据", operationType = OperateTypeEnum.UPDATE)
    @Operation(summary = "更新字典数据", description = "更新指定字典数据的信息")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    @Parameter(name = "req", description = "字典数据更新请求", required = true)
    public ApiResult<Boolean> updateDictData(@PathVariable("id") Long id, @Valid @RequestBody DictDataUpdateRequestDTO req) {
        Long operatorId = LoginHelper.getUserId();
        boolean ok = dictService.updateDictData(id, req, operatorId);
        return ApiResult.success(ok);
    }

    @DeleteMapping("/data/{id}")
    @LogOperation(operateDesc = "删除字典数据", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "删除字典数据", description = "删除指定的字典数据")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    public ApiResult<Void> deleteDictData(@PathVariable("id") Long id) {
        Long operatorId = LoginHelper.getUserId();
        dictService.deleteDictData(id, operatorId);
        return ApiResult.success(cn.refinex.common.enums.HttpStatusCode.NO_CONTENT, null);
    }

    @DeleteMapping("/data")
    @LogOperation(operateDesc = "批量删除字典数据", operationType = OperateTypeEnum.DELETE)
    @Operation(summary = "批量删除字典数据", description = "批量删除多个字典数据项")
    @Parameter(name = "ids", description = "ID 列表，逗号分隔", required = true)
    public ApiResult<Integer> batchDeleteDictData(@RequestParam("ids") String ids) {
        Long operatorId = LoginHelper.getUserId();
        List<Long> idList = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            for (String s : ids.split(",")) {
                try {
                    idList.add(Long.parseLong(s.trim()));
                } catch (NumberFormatException ignored) {
                    // ignore invalid ID
                }
            }
        }
        int total = dictService.batchDeleteDictData(idList, operatorId);
        return ApiResult.success(total);
    }

    @GetMapping("/data/{id}")
    @Operation(summary = "获取字典数据详情", description = "根据 ID 获取字典数据详细信息")
    @Parameter(name = "id", description = "字典数据 ID", required = true)
    public ApiResult<DictDataResponseDTO> getDictData(@PathVariable("id") Long id) {
        SysDictData dictData = dictService.getDictDataById(id);
        return ApiResult.success(DictConverter.toDataResponseDTO(dictData));
    }

    @GetMapping("/types/{code}/data")
    @Operation(summary = "根据类型编码获取字典数据", description = "根据字典类型编码获取所有字典数据（带缓存）")
    @Parameter(name = "code", description = "字典类型编码", required = true)
    public ApiResult<List<DictDataResponseDTO>> listDictDataByTypeCode(@PathVariable("code") String code) {
        List<SysDictData> dictDataList = dictService.listDictDataByTypeCode(code);
        return ApiResult.success(DictConverter.toDataResponseDTOList(dictDataList));
    }

    @GetMapping("/data")
    @Operation(summary = "分页查询字典数据", description = "根据条件分页查询字典数据")
    @Parameter(name = "dictTypeId", description = "字典类型 ID")
    @Parameter(name = "dictLabel", description = "字典标签，支持模糊查询")
    @Parameter(name = "dictValue", description = "字典值，支持模糊查询")
    @Parameter(name = "status", description = "状态：0正常,1停用")
    @Parameter(name = "orderBy", description = "排序字段，如：dict_sort, create_time")
    @Parameter(name = "orderDirection", description = "排序方向：ASC 或 DESC")
    @Parameter(name = "pageNum", description = "页码，从1开始")
    @Parameter(name = "pageSize", description = "每页数量")
    public ApiResult<PageResult<DictDataResponseDTO>> searchDictData(
            @RequestParam(value = "dictTypeId", required = false) Long dictTypeId,
            @RequestParam(value = "dictLabel", required = false) String dictLabel,
            @RequestParam(value = "dictValue", required = false) String dictValue,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "orderDirection", required = false) String orderDirection,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") int pageSize
    ) {
        PageRequest pr = new PageRequest(pageNum, pageSize, orderBy, orderDirection);
        PageResult<SysDictData> pageResult = dictService.pageQueryDictData(dictTypeId, dictLabel, dictValue, status, pr);
        
        // 转换为响应 DTO
        List<DictDataResponseDTO> dtoList = DictConverter.toDataResponseDTOList(pageResult.getRecords());
        PageResult<DictDataResponseDTO> dtoPageResult = new PageResult<>(
                dtoList,
                pageResult.getTotal(),
                pageResult.getPageNum(),
                pageResult.getPageSize()
        );
        
        return ApiResult.success(dtoPageResult);
    }

    @GetMapping("/data/max-sort")
    @Operation(summary = "获取字典数据最大排序值", description = "获取指定字典类型下的最大排序值，用于新增时自动计算排序")
    @Parameter(name = "dictTypeId", description = "字典类型 ID", required = true)
    public ApiResult<Integer> getMaxDictDataSort(@RequestParam("dictTypeId") Long dictTypeId) {
        Integer maxSort = dictService.getMaxDictDataSort(dictTypeId);
        return ApiResult.success(maxSort);
    }
}