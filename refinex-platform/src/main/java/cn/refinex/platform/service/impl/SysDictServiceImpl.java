package cn.refinex.platform.service.impl;

import cn.refinex.common.constants.SystemRedisKeyConstants;
import cn.refinex.common.constants.SystemStatusConstants;
import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.platform.controller.dict.dto.request.DictDataCreateRequestDTO;
import cn.refinex.platform.controller.dict.dto.request.DictDataUpdateRequestDTO;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;
import cn.refinex.platform.repository.sys.SysDictDataRepository;
import cn.refinex.platform.repository.sys.SysDictTypeRepository;
import cn.refinex.platform.service.SysDictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private final RedissonClient redissonClient;

    /**
     * 空值标记，用于防止缓存穿透
     */
    private static final String NULL_VALUE_MARKER = "NULL";

    /**
     * 随机数生成器，用于缓存过期时间随机偏移
     */
    private static final Random RANDOM = new Random();

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
            writeDictTypeByCodeCache(type);
            writeDictTypeByIdCache(type);
            // 失效启用的字典类型列表缓存
            String enabledListKey = SystemRedisKeyConstants.Dictionary.dictTypeEnabledList();
            redisService.delete(enabledListKey);
            // 清理分页查询缓存
            invalidatePageQueryCache();
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
            writeDictTypeByCodeCache(exist);
            writeDictTypeByIdCache(exist);
            // 同步数据列表缓存可能需要重建，直接失效
            invalidateCachesByDictCode(exist.getDictCode());
            // 清理分页查询缓存
            invalidatePageQueryCache();
            return true;
        }
        return false;
    }

    /**
     * 删除字典类型（软删除）
     *
     * @param id         字典类型ID
     * @param operatorId 操作人ID
     */
    @Override
    public void deleteDictType(Long id, Long operatorId) {
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
            // 清理分页查询缓存
            invalidatePageQueryCache();
        }

    }

    /**
     * 根据ID查询字典类型
     *
     * @param id 字典类型ID
     * @return 字典类型实体
     */
    @Override
    public SysDictType getDictTypeById(Long id) {
        if (id == null) {
            return null;
        }

        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictTypeById(id);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                // 检查是否为空值标记（防穿透）
                if (NULL_VALUE_MARKER.equals(cacheJson)) {
                    return null;
                }
                return JsonUtils.fromJson(cacheJson, SysDictType.class);
            }
        } catch (Exception e) {
            log.warn("读取字典类型缓存失败, id={}", id, e);
        }

        // 缓存未命中，从数据库查询
        SysDictType type = dictTypeRepository.selectById(id);

        // 写入缓存
        if (type != null) {
            writeDictTypeByIdCache(type);
        } else {
            // 缓存空值，防止缓存穿透
            writeNullCache(cacheKey, SystemRedisKeyConstants.Dictionary.DICT_NULL_CACHE_TTL);
        }

        return type;
    }

    /**
     * 根据编码查询字典类型（带分布式锁防击穿）
     *
     * @param dictCode 字典编码
     * @return 字典类型实体
     */
    @Override
    public SysDictType getDictTypeByCode(String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            return null;
        }

        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictTypeByCode(dictCode);
        SysDictType cachedType = getCachedDictType(cacheKey, dictCode);
        if (cachedType != null || isCachedAsNull(cacheKey)) {
            return cachedType;
        }

        // 缓存未命中，使用分布式锁防止缓存击穿
        return loadDictTypeWithLock(dictCode, cacheKey);
    }

    /**
     * 使用分布式锁加载字典类型
     *
     * @param dictCode 字典编码
     * @param cacheKey 缓存键
     * @return 字典类型实体
     */
    private SysDictType loadDictTypeWithLock(String dictCode, String cacheKey) {
        String lockKey = SystemRedisKeyConstants.Dictionary.dictTypeLock(dictCode);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间10秒
            if (lock.tryLock(3, 10, TimeUnit.SECONDS)) {
                try {
                    return loadDictTypeFromDbAndCache(dictCode, cacheKey);
                } finally {
                    lock.unlock();
                }
            }
            // 获取锁失败，直接查询数据库（降级策略）
            log.warn("获取字典类型锁失败，降级查询数据库, code={}", dictCode);
            return dictTypeRepository.selectByCode(dictCode);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取字典类型锁被中断, code={}", dictCode, e);
            return dictTypeRepository.selectByCode(dictCode);
        } catch (Exception e) {
            log.error("查询字典类型异常, code={}", dictCode, e);
            return dictTypeRepository.selectByCode(dictCode);
        }
    }

    /**
     * 从数据库加载字典类型并写入缓存
     *
     * @param dictCode 字典编码
     * @param cacheKey 缓存键
     * @return 字典类型实体
     */
    private SysDictType loadDictTypeFromDbAndCache(String dictCode, String cacheKey) {
        // 再次检查缓存（双重检查）
        String cacheJson = redisService.string().get(cacheKey, String.class);
        if (StringUtils.isNotBlank(cacheJson)) {
            if (NULL_VALUE_MARKER.equals(cacheJson)) {
                return null;
            }
            return JsonUtils.fromJson(cacheJson, SysDictType.class);
        }

        // 从数据库查询
        SysDictType type = dictTypeRepository.selectByCode(dictCode);

        // 写入缓存
        if (type != null) {
            writeDictTypeByCodeCache(type);
        } else {
            writeNullCache(cacheKey, SystemRedisKeyConstants.Dictionary.DICT_NULL_CACHE_TTL);
        }

        return type;
    }

    /**
     * 从缓存获取字典类型
     *
     * @param cacheKey 缓存键
     * @param dictCode 字典编码
     * @return 字典类型实体
     */
    private SysDictType getCachedDictType(String cacheKey, String dictCode) {
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson) && !NULL_VALUE_MARKER.equals(cacheJson)) {
                return JsonUtils.fromJson(cacheJson, SysDictType.class);
            }
        } catch (Exception e) {
            log.warn("读取字典类型缓存失败, code={}", dictCode, e);
        }
        return null;
    }

    /**
     * 检查缓存是否为空值标记
     *
     * @param cacheKey 缓存键
     * @return 是否为空值标记
     */
    private boolean isCachedAsNull(String cacheKey) {
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            return NULL_VALUE_MARKER.equals(cacheJson);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 分页查询字典类型（带缓存）
     *
     * @param dictCode    字典编码
     * @param dictName    字典名称
     * @param status      状态
     * @param pageRequest 分页请求参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysDictType> pageQueryDictTypes(String dictCode, String dictName, Integer status, PageRequest pageRequest) {
        // 生成缓存键
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictTypePageQuery(
                dictCode, dictName, status,
                pageRequest.getPageNum(), pageRequest.getPageSize(),
                pageRequest.getOrderBy(), pageRequest.getOrderDirection()
        );

        // 尝试从缓存读取
        PageResult<SysDictType> cachedResult = getCachedPageResult(cacheKey);
        if (cachedResult != null) {
            log.debug("字典类型分页查询命中缓存: code={}, name={}, status={}, page={}/{}",
                    dictCode, dictName, status, pageRequest.getPageNum(), pageRequest.getPageSize());
            return cachedResult;
        }

        // 缓存未命中，从数据库查询
        PageResult<SysDictType> result = dictTypeRepository.pageQuery(dictCode, dictName, status, pageRequest);

        // 写入缓存（短期缓存，3分钟）
        writeDictTypePageQueryCache(cacheKey, result);

        return result;
    }

    /**
     * 从缓存获取分页查询结果
     *
     * @param cacheKey 缓存键
     * @return 分页查询结果
     */
    private PageResult<SysDictType> getCachedPageResult(String cacheKey) {
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.fromJson(cacheJson, 
                    new com.fasterxml.jackson.core.type.TypeReference<PageResult<SysDictType>>() {});
            }
        } catch (Exception e) {
            log.warn("读取字典类型分页查询缓存失败", e);
        }
        return null;
    }

    /**
     * 写入分页查询结果到缓存
     *
     * @param cacheKey 缓存键
     * @param result 分页查询结果
     */
    private void writeDictTypePageQueryCache(String cacheKey, PageResult<SysDictType> result) {
        try {
            String json = JsonUtils.toJson(result);
            redisService.string().set(
                    cacheKey,
                    json,
                    Duration.ofSeconds(SystemRedisKeyConstants.Dictionary.DICT_PAGE_QUERY_CACHE_TTL)
            );
            log.debug("写入字典类型分页查询缓存成功: key={}", cacheKey);
        } catch (Exception e) {
            log.warn("写入字典类型分页查询缓存失败", e);
        }
    }

    /**
     * 查询所有启用的字典类型（带缓存）
     *
     * @return 字典类型列表
     */
    @Override
    public List<SysDictType> listEnabledTypes() {
        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictTypeEnabledList();
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.toList(cacheJson, SysDictType.class);
            }
        } catch (Exception e) {
            log.warn("读取启用字典类型列表缓存失败", e);
        }

        // 缓存未命中，从数据库查询
        List<SysDictType> list = dictTypeRepository.selectEnabledList();

        // 写入缓存（带随机偏移防雪崩）
        writeDictTypeEnabledListCache(list);

        return list;
    }

    // ===================== 字典数据 =====================

    /**
     * 创建字典数据
     *
     * @param request    字典数据创建请求
     * @param operatorId 操作人ID
     * @return 字典数据ID
     */
    @Override
    public Long createDictData(DictDataCreateRequestDTO request, Long operatorId) {
        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectById(request.getDictTypeId());
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && Objects.equals(type.getStatus(), SystemStatusConstants.DISABLE_VALUE)) {
            throw new BusinessException("字典类型已停用");
        }
        // 校验字典标签、值的唯一性
        if (dictDataRepository.existsLabelInType(request.getDictTypeId(), request.getDictLabel(), null)) {
            throw new BusinessException("字典数据标签已存在");
        }
        if (dictDataRepository.existsValueInType(request.getDictTypeId(), request.getDictValue(), null)) {
            throw new BusinessException("字典数据值已存在");
        }

        // 创建字典数据实体
        LocalDateTime now = LocalDateTime.now();
        SysDictData data = SysDictData.builder()
                .dictTypeId(request.getDictTypeId())
                .dictLabel(request.getDictLabel())
                .dictValue(request.getDictValue())
                .dictSort(request.getDictSort() == null ? 0 : request.getDictSort())
                .cssClass(request.getCssClass())
                .listClass(request.getListClass())
                .isDefault(request.getIsDefault() == null ? 0 : request.getIsDefault())
                .createBy(operatorId)
                .createTime(now)
                .updateBy(operatorId)
                .updateTime(now)
                .deleted(0)
                .version(0)
                .remark(request.getRemark())
                .status(request.getStatus() == null ? 0 : request.getStatus())
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
     * @param request    字典数据更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean updateDictData(Long id, DictDataUpdateRequestDTO request, Long operatorId) {
        // 校验字典数据是否存在
        SysDictData exist = dictDataRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("字典数据不存在");
        }
        // 校验字典类型是否存在
        SysDictType type = dictTypeRepository.selectById(request.getDictTypeId());
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && Objects.equals(type.getStatus(), SystemStatusConstants.DISABLE_VALUE)) {
            throw new BusinessException("字典类型已停用");
        }
        // 校验字典标签、值的唯一性（排除当前字典数据）
        if (dictDataRepository.existsLabelInType(request.getDictTypeId(), request.getDictLabel(), id)) {
            throw new BusinessException("字典数据标签已存在");
        }
        if (dictDataRepository.existsValueInType(request.getDictTypeId(), request.getDictValue(), id)) {
            throw new BusinessException("字典数据值已存在");
        }

        // 更新字典数据实体
        exist.setDictTypeId(request.getDictTypeId());
        exist.setDictLabel(request.getDictLabel());
        exist.setDictValue(request.getDictValue());
        exist.setDictSort(request.getDictSort());
        exist.setCssClass(request.getCssClass());
        exist.setListClass(request.getListClass());
        exist.setIsDefault(request.getIsDefault());
        exist.setRemark(request.getRemark());
        exist.setStatus(request.getStatus());
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
     */
    @Override
    public void deleteDictData(Long id, Long operatorId) {
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
        }

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
     * 根据ID查询字典数据（带缓存）
     *
     * @param id 字典数据ID
     * @return 字典数据实体
     */
    @Override
    public SysDictData getDictDataById(Long id) {
        if (id == null) {
            return null;
        }

        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictDataById(id);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                // 检查是否为空值标记（防穿透）
                if (NULL_VALUE_MARKER.equals(cacheJson)) {
                    return null;
                }
                return JsonUtils.fromJson(cacheJson, SysDictData.class);
            }
        } catch (Exception e) {
            log.warn("读取字典数据缓存失败, id={}", id, e);
        }

        // 缓存未命中，从数据库查询
        SysDictData data = dictDataRepository.selectById(id);

        // 写入缓存
        if (data != null) {
            writeDictDataByIdCache(data);
        } else {
            // 缓存空值，防止缓存穿透
            writeNullCache(cacheKey, SystemRedisKeyConstants.Dictionary.DICT_NULL_CACHE_TTL);
        }

        return data;
    }

    /**
     * 根据字典编码查询字典数据列表（带分布式锁防击穿）
     *
     * @param dictCode 字典编码
     * @return 字典数据实体列表
     */
    @Override
    public List<SysDictData> listDictDataByTypeCode(String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            return Collections.emptyList();
        }

        // 校验字典类型是否存在
        SysDictType type = getDictTypeByCode(dictCode);
        if (type == null) {
            throw new BusinessException("字典类型不存在");
        }
        // 校验字典类型是否停用
        if (type.getStatus() != null && Objects.equals(type.getStatus(), SystemStatusConstants.DISABLE_VALUE)) {
            throw new BusinessException("字典类型已停用");
        }

        // 先从缓存中读取
        String cacheKey = SystemRedisKeyConstants.Dictionary.dictDataListByCode(dictCode);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.toList(cacheJson, SysDictData.class);
            }
        } catch (Exception e) {
            log.warn("读取字典数据列表缓存失败, code={}", dictCode, e);
        }

        // 缓存未命中，使用分布式锁防止缓存击穿
        String lockKey = SystemRedisKeyConstants.Dictionary.dictDataListLock(dictCode);
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 尝试获取锁，最多等待3秒，锁自动释放时间10秒
            boolean locked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (locked) {
                try {
                    // 再次检查缓存（双重检查）
                    String cacheJson = redisService.string().get(cacheKey, String.class);
                    if (StringUtils.isNotBlank(cacheJson)) {
                        return JsonUtils.toList(cacheJson, SysDictData.class);
                    }

                    // 从数据库查询
                    List<SysDictData> list = dictDataRepository.selectListByTypeCode(dictCode);

                    // 写入缓存（带随机偏移防雪崩）
                    writeDictDataListCache(dictCode, list);

                    return list;
                } finally {
                    lock.unlock();
                }
            } else {
                // 获取锁失败，直接查询数据库（降级策略）
                log.warn("获取字典数据列表锁失败，降级查询数据库, code={}", dictCode);
                return dictDataRepository.selectListByTypeCode(dictCode);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取字典数据列表锁被中断, code={}", dictCode, e);
            return dictDataRepository.selectListByTypeCode(dictCode);
        } catch (Exception e) {
            log.error("查询字典数据列表异常, code={}", dictCode, e);
            return dictDataRepository.selectListByTypeCode(dictCode);
        }
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

    // ===================== 缓存操作（私有方法） =====================

    /**
     * 计算带随机偏移的缓存过期时间（防雪崩）
     *
     * @param baseTtl 基础过期时间（秒）
     * @return 带随机偏移的过期时间
     */
    private Duration calculateTtlWithOffset(long baseTtl) {
        long offset = RANDOM.nextInt((int) SystemRedisKeyConstants.Dictionary.DICT_CACHE_TTL_OFFSET);
        return Duration.ofSeconds(baseTtl + offset);
    }

    /**
     * 写入空值缓存（防穿透）
     *
     * @param cacheKey 缓存键
     * @param ttl      过期时间（秒）
     */
    private void writeNullCache(String cacheKey, long ttl) {
        try {
            redisService.string().set(cacheKey, NULL_VALUE_MARKER, Duration.ofSeconds(ttl));
            log.debug("写入空值缓存, key={}", cacheKey);
        } catch (Exception e) {
            log.warn("写入空值缓存失败, key={}", cacheKey, e);
        }
    }

    /**
     * 写入字典类型缓存（按编码）
     *
     * @param type 字典类型实体
     */
    private void writeDictTypeByCodeCache(SysDictType type) {
        if (type == null) {
            return;
        }

        try {
            String key = SystemRedisKeyConstants.Dictionary.dictTypeByCode(type.getDictCode());
            String json = JsonUtils.toJson(type);
            Duration ttl = calculateTtlWithOffset(SystemRedisKeyConstants.Dictionary.DICT_TYPE_CACHE_TTL);
            redisService.string().set(key, json, ttl);
            log.debug("写入字典类型缓存（按编码）, code={}, ttl={}秒", type.getDictCode(), ttl.getSeconds());
        } catch (Exception e) {
            log.warn("写入字典类型缓存失败, code={}", type.getDictCode(), e);
        }
    }

    /**
     * 写入字典类型缓存（按ID）
     *
     * @param type 字典类型实体
     */
    private void writeDictTypeByIdCache(SysDictType type) {
        if (type == null) {
            return;
        }

        try {
            String key = SystemRedisKeyConstants.Dictionary.dictTypeById(type.getId());
            String json = JsonUtils.toJson(type);
            Duration ttl = calculateTtlWithOffset(SystemRedisKeyConstants.Dictionary.DICT_TYPE_CACHE_TTL);
            redisService.string().set(key, json, ttl);
            log.debug("写入字典类型缓存（按ID）, id={}, ttl={}秒", type.getId(), ttl.getSeconds());
        } catch (Exception e) {
            log.warn("写入字典类型缓存失败, id={}", type.getId(), e);
        }
    }

    /**
     * 写入启用的字典类型列表缓存
     *
     * @param list 字典类型列表
     */
    private void writeDictTypeEnabledListCache(List<SysDictType> list) {
        try {
            String key = SystemRedisKeyConstants.Dictionary.dictTypeEnabledList();
            String json = JsonUtils.toJson(list == null ? Collections.emptyList() : list);
            Duration ttl = calculateTtlWithOffset(SystemRedisKeyConstants.Dictionary.DICT_ENABLED_TYPES_CACHE_TTL);
            redisService.string().set(key, json, ttl);
            log.debug("写入启用字典类型列表缓存, size={}, ttl={}秒", list == null ? 0 : list.size(), ttl.getSeconds());
        } catch (Exception e) {
            log.warn("写入启用字典类型列表缓存失败", e);
        }
    }

    /**
     * 写入字典数据缓存（按ID）
     *
     * @param data 字典数据实体
     */
    private void writeDictDataByIdCache(SysDictData data) {
        if (data == null) {
            return;
        }

        try {
            String key = SystemRedisKeyConstants.Dictionary.dictDataById(data.getId());
            String json = JsonUtils.toJson(data);
            Duration ttl = calculateTtlWithOffset(SystemRedisKeyConstants.Dictionary.DICT_DATA_DETAIL_CACHE_TTL);
            redisService.string().set(key, json, ttl);
            log.debug("写入字典数据缓存（按ID）, id={}, ttl={}秒", data.getId(), ttl.getSeconds());
        } catch (Exception e) {
            log.warn("写入字典数据缓存失败, id={}", data.getId(), e);
        }
    }

    /**
     * 写入字典数据列表缓存
     *
     * @param dictCode 字典编码
     * @param list     字典数据实体列表
     */
    private void writeDictDataListCache(String dictCode, List<SysDictData> list) {
        try {
            String key = SystemRedisKeyConstants.Dictionary.dictDataListByCode(dictCode);
            String json = JsonUtils.toJson(list == null ? Collections.emptyList() : list);
            Duration ttl = calculateTtlWithOffset(SystemRedisKeyConstants.Dictionary.DICT_DATA_LIST_CACHE_TTL);
            redisService.string().set(key, json, ttl);
            log.debug("写入字典数据列表缓存, code={}, size={}, ttl={}秒", dictCode, list == null ? 0 : list.size(), ttl.getSeconds());
        } catch (Exception e) {
            log.warn("写入字典数据列表缓存失败, code={}", dictCode, e);
        }
    }

    /**
     * 失效指定字典编码的所有缓存
     *
     * @param dictCode 字典编码
     */
    private void invalidateCachesByDictCode(String dictCode) {
        if (StringUtils.isBlank(dictCode)) {
            return;
        }

        try {
            // 删除字典类型缓存（按编码）
            String typeCodeKey = SystemRedisKeyConstants.Dictionary.dictTypeByCode(dictCode);
            redisService.delete(typeCodeKey);

            // 删除字典数据列表缓存
            String dataListKey = SystemRedisKeyConstants.Dictionary.dictDataListByCode(dictCode);
            redisService.delete(dataListKey);

            // 删除启用的字典类型列表缓存（因为可能包含该类型）
            String enabledListKey = SystemRedisKeyConstants.Dictionary.dictTypeEnabledList();
            redisService.delete(enabledListKey);

            log.info("失效字典缓存成功, code={}", dictCode);
        } catch (Exception e) {
            log.warn("失效字典缓存失败, code={}", dictCode, e);
        }
    }

    /**
     * 获取字典类型的最大排序值
     *
     * @return 最大排序值
     */
    @Override
    public Integer getMaxDictTypeSort() {
        return dictTypeRepository.getMaxDictSort();
    }

    /**
     * 获取指定字典类型下的最大排序值
     *
     * @param dictTypeId 字典类型ID
     * @return 最大排序值
     */
    @Override
    public Integer getMaxDictDataSort(Long dictTypeId) {
        return dictDataRepository.getMaxDictSort(dictTypeId);
    }

    // ===================== 缓存管理（公共方法） =====================

    /**
     * 清理字典类型分页查询缓存
     * <p>
     * 在字典类型增删改操作后调用，清理所有分页查询缓存
     * </p>
     */
    private void invalidatePageQueryCache() {
        try {
            String pattern = SystemRedisKeyConstants.Dictionary.dictTypePageQueryPattern();
            Set<String> keys = redisService.keys(pattern);
            if (CollectionUtils.isNotEmpty(keys)) {
                redisService.delete(keys);
                log.debug("清理字典类型分页查询缓存成功，共清理 {} 个缓存", keys.size());
            }
        } catch (Exception e) {
            log.warn("清理字典类型分页查询缓存失败", e);
        }
    }

    /**
     * 预热字典缓存
     * <p>
     * 系统启动时或需要时，预加载常用字典数据到缓存中，提升首次访问性能
     * </p>
     */
    @Override
    public void warmUpCache() {
        log.info("开始预热字典缓存...");
        long startTime = System.currentTimeMillis();

        try {
            List<SysDictType> enabledTypes = dictTypeRepository.selectEnabledList();
            if (CollectionUtils.isNotEmpty(enabledTypes)) {
                writeDictTypeEnabledListCache(enabledTypes);
                warmUpEachDictType(enabledTypes);
            }

            long endTime = System.currentTimeMillis();
            log.info("字典缓存预热完成，耗时: {}ms，预热字典类型数: {}",
                    endTime - startTime, enabledTypes == null ? 0 : enabledTypes.size());
        } catch (Exception e) {
            log.error("字典缓存预热失败", e);
        }
    }

    /**
     * 预热每个字典类型及其数据
     *
     * @param enabledTypes 启用的字典类型列表
     */
    private void warmUpEachDictType(List<SysDictType> enabledTypes) {
        for (SysDictType type : enabledTypes) {
            try {
                warmUpSingleDictType(type);
            } catch (Exception e) {
                log.warn("预热字典类型失败: code={}", type.getDictCode(), e);
            }
        }
    }

    /**
     * 预热单个字典类型
     *
     * @param type 字典类型实体
     */
    private void warmUpSingleDictType(SysDictType type) {
        // 缓存字典类型（按编码和ID）
        writeDictTypeByCodeCache(type);
        writeDictTypeByIdCache(type);

        // 缓存字典数据列表
        List<SysDictData> dataList = dictDataRepository.selectListByTypeCode(type.getDictCode());
        writeDictDataListCache(type.getDictCode(), dataList);

        // 缓存每个字典数据（按ID）
        if (CollectionUtils.isNotEmpty(dataList)) {
            dataList.forEach(this::writeDictDataByIdCache);
        }

        log.debug("预热字典类型成功: code={}, dataSize={}", type.getDictCode(),
                dataList == null ? 0 : dataList.size());
    }
}