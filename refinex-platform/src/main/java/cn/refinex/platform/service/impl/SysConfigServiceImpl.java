package cn.refinex.platform.service.impl;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.controller.config.dto.request.SysConfigCreateRequestDTO;
import cn.refinex.platform.controller.config.dto.request.SysConfigQueryRequestDTO;
import cn.refinex.platform.controller.config.dto.request.SysConfigUpdateRequestDTO;
import cn.refinex.platform.entity.sys.SysConfig;
import cn.refinex.platform.repository.sys.SysConfigRepository;
import cn.refinex.platform.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static cn.refinex.common.constants.SystemRedisKeyConstants.SysConfig.buildFrontendConfigCacheKey;

/**
 * 系统配置服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements SysConfigService {

    private final JdbcTemplateManager jdbcManager;
    private final SysConfigRepository sysConfigRepository;
    private final RedisService redisService;

    /**
     * 创建系统配置
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 配置ID
     */
    @Override
    public Long create(SysConfigCreateRequestDTO request, Long operatorId) {
        // 唯一键校验
        if (sysConfigRepository.existsKey(request.getConfigKey(), null)) {
            throw new BusinessException("配置键已存在");
        }

        SysConfig entity = BeanConverter.toBean(request, SysConfig.class);
        entity.setCreateBy(operatorId);
        entity.setUpdateBy(operatorId);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        long configId = jdbcManager.executeInTransaction(tx -> sysConfigRepository.insert(tx, entity));

        // 缓存前端可见配置
        if (entity.getIsFrontend() != null && entity.getIsFrontend() == 1) {
            entity.setId(configId);
            writeFrontendCache(entity);
        }

        return configId;
    }

    /**
     * 更新系统配置
     *
     * @param id         配置ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean update(Long id, SysConfigUpdateRequestDTO request, Long operatorId) {
        // 校验配置是否存在
        SysConfig exist = sysConfigRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("配置不存在");
        }

        exist.setConfigValue(request.getConfigValue());
        exist.setConfigType(request.getConfigType());
        exist.setConfigGroup(request.getConfigGroup());
        exist.setConfigLabel(request.getConfigLabel());
        exist.setConfigDesc(request.getConfigDesc());
        exist.setIsSensitive(request.getIsSensitive());
        exist.setIsFrontend(request.getIsFrontend());
        exist.setRemark(request.getRemark());
        exist.setSort(request.getSort());
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = jdbcManager.executeInTransaction(tx -> sysConfigRepository.updateById(tx, exist));

        // 刷新缓存，前端可见配置则刷新前端缓存，否则清理前端缓存
        if (rows > 0) {
            if (exist.getIsFrontend() != null && exist.getIsFrontend() == 1) {
                writeFrontendCache(exist);
            } else {
                invalidateFrontendCache(exist.getConfigKey());
            }
        }
        return rows > 0;
    }

    /**
     * 删除系统配置
     *
     * @param id         配置ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id, Long operatorId) {
        // 校验配置是否存在
        SysConfig exist = sysConfigRepository.selectById(id);
        if (exist == null) {
            return false;
        }

        int rows = jdbcManager.executeInTransaction(tx -> sysConfigRepository.softDeleteById(tx, id, operatorId));
        if (rows > 0) {
            // 清理前端缓存
            invalidateFrontendCache(exist.getConfigKey());
        }

        return rows > 0;
    }

    /**
     * 根据ID查询系统配置
     *
     * @param id 配置ID
     * @return 系统配置
     */
    @Override
    public SysConfig getById(Long id) {
        return sysConfigRepository.selectById(id);
    }

    /**
     * 根据配置键查询系统配置
     *
     * @param configKey 配置键
     * @return 系统配置
     */
    @Override
    public SysConfig getByKey(String configKey) {
        // 对前端可见配置尝试读取缓存
        String cacheKey = buildFrontendConfigCacheKey(configKey);
        try {
            String cacheJson = redisService.string().get(cacheKey, String.class);
            if (StringUtils.isNotBlank(cacheJson)) {
                return JsonUtils.fromJson(cacheJson, SysConfig.class);
            }
        } catch (Exception e) {
            log.warn("读取配置缓存失败, key={}", configKey, e);
        }

        // 缓存未命中，查询数据库后写入缓存
        SysConfig cfg = sysConfigRepository.selectByKey(configKey);
        if (cfg != null && cfg.getIsFrontend() != null && cfg.getIsFrontend() == 1) {
            writeFrontendCache(cfg);
        }

        return cfg;
    }

    /**
     * 根据配置组查询系统配置
     *
     * @param group 配置组
     * @return 系统配置列表
     */
    @Override
    public List<SysConfig> listByGroup(String group) {
        return sysConfigRepository.listByGroup(group);
    }

    /**
     * 分页查询系统配置
     *
     * @param query     查询请求
     * @param pageRequest 分页请求
     * @return 系统配置分页结果
     */
    @Override
    public PageResult<SysConfig> page(SysConfigQueryRequestDTO query, PageRequest pageRequest) {
        return sysConfigRepository.pageQuery(query, pageRequest);
    }

    /**
     * 更新系统配置前端可见状态
     *
     * @param id         配置ID
     * @param isFrontend 是否前端可见
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean updateFrontendVisible(Long id, Integer isFrontend, Long operatorId) {
        int rows = jdbcManager.executeInTransaction(tx -> sysConfigRepository.updateStatus(tx, id, isFrontend, operatorId));
        if (rows > 0) {
            // 刷新缓存，前端可见配置则刷新前端缓存，否则清理前端缓存
            SysConfig cfg = sysConfigRepository.selectById(id);
            if (cfg != null) {
                if (isFrontend != null && isFrontend == 1) {
                    writeFrontendCache(cfg);
                } else {
                    invalidateFrontendCache(cfg.getConfigKey());
                }
            }
        }

        return rows > 0;
    }

    /**
     * 写入前端可见配置缓存, 缓存时间为30分钟
     *
     * @param cfg 系统配置
     */
    private void writeFrontendCache(SysConfig cfg) {
        try {
            String key = buildFrontendConfigCacheKey(cfg.getConfigKey());
            String json = JsonUtils.toJson(cfg);
            redisService.string().set(key, json, Duration.ofMinutes(30));
        } catch (Exception e) {
            log.warn("写入配置缓存失败, key={}", cfg.getConfigKey(), e);
        }
    }

    /**
     * 删除前端可见配置缓存
     *
     * @param configKey 配置键
     */
    private void invalidateFrontendCache(String configKey) {
        try {
            redisService.delete(buildFrontendConfigCacheKey(configKey));
        } catch (Exception e) {
            log.warn("删除配置缓存失败, key={}", configKey, e);
        }
    }
}


