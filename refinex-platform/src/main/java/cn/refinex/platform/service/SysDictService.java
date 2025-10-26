package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.controller.dict.dto.request.DictDataCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictDataUpdateRequestDTO;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;

import java.util.List;

/**
 * 系统字典服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysDictService {

    /**
     * 创建字典类型
     *
     * @param dictCode   字典类型编码
     * @param dictName   字典类型名称
     * @param dictDesc   字典类型描述
     * @param dictSort   字典类型排序
     * @param remark     备注
     * @param status     状态
     * @param operatorId 操作人ID
     * @return 字典类型ID
     */
    Long createDictType(String dictCode, String dictName, String dictDesc, Integer dictSort, String remark, Integer status, Long operatorId);

    /**
     * 更新字典类型
     *
     * @param id         字典类型ID
     * @param dictName   字典类型名称
     * @param dictDesc   字典类型描述
     * @param dictSort   字典类型排序
     * @param remark     备注
     * @param status     状态
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateDictType(Long id, String dictName, String dictDesc, Integer dictSort, String remark, Integer status, Long operatorId);

    /**
     * 删除字典类型（软删除）
     *
     * @param id         字典类型ID
     * @param operatorId 操作人ID
     */
    void deleteDictType(Long id, Long operatorId);

    /**
     * 根据ID查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型实体
     */
    SysDictType getDictTypeById(Long id);

    /**
     * 根据编码查询字典类型
     *
     * @param dictCode 字典编码
     * @return 字典类型实体
     */
    SysDictType getDictTypeByCode(String dictCode);

    /**
     * 分页查询字典类型
     *
     * @param dictCode    字典编码
     * @param dictName    字典名称
     * @param status      状态
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    PageResult<SysDictType> pageQueryDictTypes(String dictCode, String dictName, Integer status, PageRequest pageRequest);

    /**
     * 查询所有启用的字典类型
     *
     * @return 字典类型列表
     */
    List<SysDictType> listEnabledTypes();

    /**
     * 创建字典数据
     *
     * @param request    字典数据创建请求
     * @param operatorId 操作人ID
     * @return 字典数据ID
     */
    Long createDictData(DictDataCreateRequestDTO request, Long operatorId);

    /**
     * 更新字典数据
     *
     * @param id         字典数据ID
     * @param request    字典数据更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateDictData(Long id, DictDataUpdateRequestDTO request, Long operatorId);

    /**
     * 删除字典数据（逻辑删除）
     *
     * @param id         字典数据ID
     * @param operatorId 操作人ID
     */
    void deleteDictData(Long id, Long operatorId);

    /**
     * 批量删除字典数据（逻辑删除）
     *
     * @param ids        字典数据ID列表
     * @param operatorId 操作人ID
     * @return 实际删除的字典数据数量
     */
    int batchDeleteDictData(List<Long> ids, Long operatorId);

    /**
     * 根据ID查询字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据实体
     */
    SysDictData getDictDataById(Long id);

    /**
     * 根据字典编码查询字典数据列表（按类型）
     *
     * @param dictCode 字典编码
     * @return 字典数据实体列表
     */
    List<SysDictData> listDictDataByTypeCode(String dictCode);

    /**
     * 分页查询字典数据
     *
     * @param dictTypeId  字典类型ID
     * @param dictLabel   字典标签
     * @param dictValue   字典值
     * @param status      状态
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    PageResult<SysDictData> pageQueryDictData(Long dictTypeId, String dictLabel, String dictValue, Integer status, PageRequest pageRequest);

    /**
     * 获取字典类型的最大排序值
     *
     * @return 最大排序值
     */
    Integer getMaxDictTypeSort();

    /**
     * 获取指定字典类型下的最大排序值
     *
     * @param dictTypeId 字典类型ID
     * @return 最大排序值
     */
    Integer getMaxDictDataSort(Long dictTypeId);

    // ===================== 缓存管理 =====================

    /**
     * 预热字典缓存
     * <p>
     * 系统启动时或需要时，预加载常用字典数据到缓存中，提升首次访问性能
     * </p>
     */
    void warmUpCache();

}
