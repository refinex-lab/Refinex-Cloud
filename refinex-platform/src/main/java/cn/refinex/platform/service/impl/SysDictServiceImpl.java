package cn.refinex.platform.service.impl;

import cn.refinex.common.constants.SystemRedisKeyConstants;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.algorithm.SnowflakeIdGenerator;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;
import cn.refinex.platform.repository.sys.SysDictDataRepository;
import cn.refinex.platform.repository.sys.SysDictTypeRepository;
import cn.refinex.platform.service.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 字典业务服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictTypeRepository dictTypeRepository;
    private final SysDictDataRepository dictDataRepository;
    private final RedisService redisService;
    private final SnowflakeIdGenerator idGenerator;

    // ===================== 字典类型 =====================

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
    @Override
    public Long createDictType(String dictCode, String dictName, String dictDesc, Integer dictSort, String remark, Integer status, Long operatorId) {
        // 校验字典类型编码唯一性
        if (dictTypeRepository.existsByCode(dictCode, null)) {
            throw new BusinessException("字典类型编码已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        SysDictType type = SysDictType.builder()
                .dictCode(dictCode)
                .dictName(dictName)
                .dictDesc(dictDesc)
                .dictSort(dictSort)
                .createBy(operatorId)
                .createTime(now)
                .updateBy(operatorId)
                .updateTime(now)
                .deleted(0)
                .version(0)
                .remark(remark)
                .status(status == null ? 0 : status)
                .build();

        int rows = dictTypeRepository.insert(type);
        if (rows > 0) {
            // 写入缓存（类型）
            writeTypeCache(type);
            return type.getId();
        }

        return null;
    }

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
    @Override
    public boolean updateDictType(Long id, String dictName, String dictDesc, Integer dictSort, String remark, Integer status, Long operatorId) {
        // 校验字典类型是否存在
        SysDictType exist = dictTypeRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典类型不存在");
        }

        // 更新字段
        exist.setDictName(dictName);
        exist.setDictDesc(dictDesc);
        exist.setDictSort(dictSort);
        exist.setRemark(remark);
        exist.setStatus(status);
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = dictTypeRepository.update(exist);
        if (rows > 0) {
            // 更新缓存
            writeTypeCache(exist);
            // 同步数据列表缓存可能需要重建，直接失效
            invalidateCachesByDictCode(exist.getDictCode());
            return true;
        }
        return false;
    }

    /**
     * 删除字典类型（软删除）
     *
     * @param id         字典类型ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictType(Long id, Long operatorId) {
        // 校验字典类型是否存在
        SysDictType exist = dictTypeRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典类型不存在");
        }

        // 检查是否有数据使用该类型, 有则不能删除
        List<SysDictData> dataList = dictDataRepository.selectListByTypeId(id);
        if (CollectionUtils.isNotEmpty(dataList)) {
            throw new BusinessException("字典类型已被使用，无法删除");
        }

        // 软删除字典类型
        int rows = dictTypeRepository.softDeleteById(id, operatorId);
        if (rows > 0) {
            // 失效缓存（类型）
            invalidateCachesByDictCode(exist.getDictCode());
            return true;
        }

        return false;
    }

    /**
     * 根据ID查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型实体
     */
    @Override
    public SysDictType getDictTypeById(Long id) {
        return dictTypeRepository.selectById(id);
    }

    /**
     * 根据编码查询字典类型
     *
     * @param dictCode 字典编码
     * @return 字典类型实体
     */
    @Override
    public SysDictType getDictTypeByCode(String dictCode) {
        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.buildDictTypeCacheKey(dictCode);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.fromJson(cacheJson, SysDictType.class);
            }
        } catch (Exception e) {
            log.warn("读取字典类型缓存失败, code={}", dictCode, e);
        }

        // 缓存未命中，从数据库查询
        SysDictType type = dictTypeRepository.selectByCode(dictCode);
        if (type != null) {
            // 写入缓存（类型）
            writeTypeCache(type);
        }

        return type;
    }

    /**
     * 分页查询字典类型
     *
     * @param dictCode    字典编码
     * @param dictName    字典名称
     * @param status      状态
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysDictType> pageQueryDictTypes(String dictCode, String dictName, Integer status, PageRequest pageRequest) {
        return dictTypeRepository.pageQuery(dictCode, dictName, status, pageRequest);
    }

    /**
     * 查询所有启用的字典类型
     *
     * @return 字典类型列表
     */
    @Override
    public List<SysDictType> listEnabledTypes() {
        return dictTypeRepository.selectEnabledList();
    }

    // ===================== 字典数据 =====================

    /**
     * 创建字典数据
     *
     * @param dictTypeId 字典类型ID
     * @param dictLabel  字典标签
     * @param dictValue  字典值
     * @param dictSort   排序
     * @param cssClass   CSS类名
     * @param listClass  列表类名
     * @param isDefault  是否默认
     * @param remark     备注
     * @param status     状态
     * @param operatorId 操作人ID
     * @return 字典数据ID
     */
    @Override
    public Long createDictData(Long dictTypeId, String dictLabel, String dictValue, Integer dictSort, String cssClass, String listClass, Integer isDefault, String remark, Integer status, Long operatorId) {
        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectById(dictTypeId);
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && type.getStatus() == 1) {
            throw new BusinessException("字典类型已停用");
        }
        // 校验字典标签、值的唯一性
        if (dictDataRepository.existsLabelInType(dictTypeId, dictLabel, null)) {
            throw new BusinessException("字典数据标签已存在");
        }
        if (dictDataRepository.existsValueInType(dictTypeId, dictValue, null)) {
            throw new BusinessException("字典数据值已存在");
        }

        // 创建字典数据实体
        LocalDateTime now = LocalDateTime.now();
        SysDictData data = SysDictData.builder()
                .dictTypeId(dictTypeId)
                .dictLabel(dictLabel)
                .dictValue(dictValue)
                .dictSort(dictSort == null ? 0 : dictSort)
                .cssClass(cssClass)
                .listClass(listClass)
                .isDefault(isDefault == null ? 0 : isDefault)
                .createBy(operatorId)
                .createTime(now)
                .updateBy(operatorId)
                .updateTime(now)
                .deleted(0)
                .version(0)
                .remark(remark)
                .status(status == null ? 0 : status)
                .build();

        int rows = dictDataRepository.insert(data);
        if (rows > 0) {
            // 刷新缓存，这里直接删除缓存，下次查询时会重新加载
            invalidateCachesByDictCode(type.getDictCode());
            return data.getId();
        }

        return null;
    }

    /**
     * 更新字典数据
     *
     * @param id         字典数据ID
     * @param dictTypeId 字典类型ID
     * @param dictLabel  字典标签
     * @param dictValue  字典值
     * @param dictSort   排序
     * @param cssClass   CSS类名
     * @param listClass  列表类名
     * @param isDefault  是否默认
     * @param remark     备注
     * @param status     状态
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean updateDictData(Long id, Long dictTypeId, String dictLabel, String dictValue, Integer dictSort, String cssClass, String listClass, Integer isDefault, String remark, Integer status, Long operatorId) {
        // 校验字典数据是否存在
        SysDictData exist = dictDataRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典数据不存在");
        }
        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectById(dictTypeId);
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && type.getStatus() == 1) {
            throw new BusinessException("字典类型已停用");
        }
        // 校验字典标签、值的唯一性（排除当前字典数据）
        if (dictDataRepository.existsLabelInType(dictTypeId, dictLabel, id)) {
            throw new BusinessException("字典数据标签已存在");
        }
        if (dictDataRepository.existsValueInType(dictTypeId, dictValue, id)) {
            throw new BusinessException("字典数据值已存在");
        }

        // 更新字典数据实体
        exist.setDictTypeId(dictTypeId);
        exist.setDictLabel(dictLabel);
        exist.setDictValue(dictValue);
        exist.setDictSort(dictSort);
        exist.setCssClass(cssClass);
        exist.setListClass(listClass);
        exist.setIsDefault(isDefault);
        exist.setRemark(remark);
        exist.setStatus(status);
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = dictDataRepository.update(exist);
        if (rows > 0) {
            // 刷新缓存，这里直接删除缓存，下次查询时会重新加载
            invalidateCachesByDictCode(type.getDictCode());
            return true;
        }

        return false;
    }

    /**
     * 删除字典数据（逻辑删除）
     *
     * @param id         字典数据ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteDictData(Long id, Long operatorId) {
        // 校验字典数据是否存在
        SysDictData exist = dictDataRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典数据不存在");
        }

        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectById(exist.getDictTypeId());
        int rows = dictDataRepository.softDeleteById(id, operatorId);
        if (rows > 0 && type != null) {
            // 刷新缓存，这里直接删除缓存，下次查询时会重新加载
            invalidateCachesByDictCode(type.getDictCode());
            return true;
        }

        return rows > 0;
    }

    /**
     * 批量删除字典数据（逻辑删除）
     *
     * @param ids        字典数据ID列表
     * @param operatorId 操作人ID
     * @return 实际删除的字典数据数量
     */
    @Override
    public int batchDeleteDictData(List<Long> ids, Long operatorId) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        int total = 0;
        Set<String> impactedCodes = new HashSet<>();

        for (Long id : ids) {
            // 校验字典数据是否存在
            SysDictData exist = dictDataRepository.selectById(id);
            if (exist == null) {
                continue;
            }

            // 校验字典类型是否存在
            SysDictType type = dictTypeRepository.selectById(exist.getDictTypeId());
            int rows = dictDataRepository.softDeleteById(id, operatorId);
            total += rows;

            if (type != null) {
                // 记录受影响的字典编码
                impactedCodes.add(type.getDictCode());
            }
        }

        // 刷新受影响的字典类型缓存
        for (String code : impactedCodes) {
            invalidateCachesByDictCode(code);
        }

        return total;
    }

    /**
     * 根据ID查询字典数据
     *
     * @param id 字典数据ID
     * @return 字典数据实体
     */
    @Override
    public SysDictData getDictDataById(Long id) {
        return dictDataRepository.selectById(id);
    }

    /**
     * 根据字典编码查询字典数据列表（按类型）
     *
     * @param dictCode 字典编码
     * @return 字典数据实体列表
     */
    @Override
    public List<SysDictData> listDictDataByTypeCode(String dictCode) {
        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectByCode(dictCode);
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && type.getStatus() == 1) {
            throw new BusinessException("字典类型已停用");
        }

        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.buildDictDataListCacheKey(dictCode);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.toList(cacheJson, SysDictData.class);
            }
        } catch (Exception e) {
            log.warn("读取字典数据缓存失败, code={}", dictCode, e);
        }

        // 从数据库查询并缓存
        List<SysDictData> list = dictDataRepository.selectListByTypeCode(dictCode);
        writeDataListCache(dictCode, list);
        return list;
    }

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
    @Override
    public PageResult<SysDictData> pageQueryDictData(Long dictTypeId, String dictLabel, String dictValue, Integer status, PageRequest pageRequest) {
        return dictDataRepository.pageQuery(dictTypeId, dictLabel, dictValue, status, pageRequest);
    }

    // ===================== 缓存 =====================

    /**
     * 写入字典类型缓存
     *
     * @param type 字典类型实体
     */
    private void writeTypeCache(SysDictType type) {
        if (type == null) {
            return;
        }

        try {
            String key = SystemRedisKeyConstants.Dictionary.buildDictTypeCacheKey(type.getDictCode());
            String json = JsonUtils.toJson(type);
            redisService.string().set(key, json, Duration.ofSeconds(SystemRedisKeyConstants.Dictionary.DEFAULT_CACHE_TTL));
        } catch (Exception e) {
            log.warn("写入字典类型缓存失败, code={}", type.getDictCode(), e);
        }
    }

    /**
     * 写入字典数据列表缓存
     *
     * @param dictCode 字典编码
     * @param list     字典数据实体列表
     */
    private void writeDataListCache(String dictCode, List<SysDictData> list) {
        try {
            String key = SystemRedisKeyConstants.Dictionary.buildDictDataListCacheKey(dictCode);
            String json = JsonUtils.toJson(list == null ? List.of() : list);
            redisService.string().set(key, json, Duration.ofSeconds(SystemRedisKeyConstants.Dictionary.DEFAULT_CACHE_TTL));
        } catch (Exception e) {
            log.warn("写入字典数据缓存失败, code={}", dictCode, e);
        }
    }

    /**
     * 失效指定字典编码的缓存
     *
     * @param dictCode 字典编码
     */
    private void invalidateCachesByDictCode(String dictCode) {
        try {
            String typeKey = SystemRedisKeyConstants.Dictionary.buildDictTypeCacheKey(dictCode);
            String dataKey = SystemRedisKeyConstants.Dictionary.buildDictDataListCacheKey(dictCode);
            redisService.delete(typeKey);
            redisService.delete(dataKey);
        } catch (Exception e) {
            log.warn("删除字典缓存失败, code={}", dictCode, e);
        }
    }
}