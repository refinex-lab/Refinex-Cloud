package cn.refinex.platform.service.impl;

import cn.refinex.common.exception.BusinessException;
import cn.refinex.common.jdbc.core.JdbcTemplateManager;
import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.common.json.utils.JsonUtils;
import cn.refinex.common.redis.RedisService;
import cn.refinex.common.utils.object.BeanConverter;
import cn.refinex.platform.domain.dto.request.SysPermissionCreateRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionQueryRequest;
import cn.refinex.platform.domain.dto.request.SysPermissionUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysPermission;
import cn.refinex.platform.repository.sys.SysPermissionRepository;
import cn.refinex.platform.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static cn.refinex.common.constants.SystemRedisKeyConstants.Permission.*;

/**
 * 权限服务实现
 *
 * @author Refinex
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {

    private final JdbcTemplateManager jdbcManager;
    private final SysPermissionRepository sysPermissionRepository;
    private final RedisService redisService;

    /**
     * 创建系统权限
     *
     * @param request 创建请求
     * @param operatorId 操作人ID
     * @return 权限ID
     */
    @Override
    public Long create(SysPermissionCreateRequest request, Long operatorId) {
        // 校验权限编码是否存在
        if (sysPermissionRepository.existsByCode(request.getPermissionCode(), null)) {
            throw new BusinessException("权限编码已存在");
        }

        SysPermission p = BeanConverter.toBean(request, SysPermission.class);
        p.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        p.setCreateBy(operatorId);
        p.setCreateTime(LocalDateTime.now());
        p.setUpdateBy(operatorId);
        p.setUpdateTime(LocalDateTime.now());

        long id = jdbcManager.executeInTransaction(tx -> sysPermissionRepository.insert(tx, p));

        // 失效权限缓存
        invalidatePermissionCaches(p);

        return id;
    }

    /**
     * 更新系统权限
     *
     * @param id         权限ID
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean update(Long id, SysPermissionUpdateRequest request, Long operatorId) {
        // 校验权限是否存在
        SysPermission exist = sysPermissionRepository.selectById(id);
        if (exist == null) {
            throw new BusinessException("权限不存在");
        }
        // 校验权限编码是否存在
        if (sysPermissionRepository.existsByCode(request.getPermissionCode(), request.getId())) {
            throw new BusinessException("权限编码已存在");
        }

        exist.setPermissionCode(request.getPermissionCode());
        exist.setPermissionName(request.getPermissionName());
        exist.setPermissionType(request.getPermissionType());
        exist.setParentId(request.getParentId());
        exist.setModuleName(request.getModuleName());
        exist.setResourcePath(request.getResourcePath());
        exist.setHttpMethod(request.getHttpMethod());
        exist.setSort(request.getSort());
        exist.setStatus(request.getStatus() == null ? 0 : request.getStatus());
        exist.setRemark(request.getRemark());
        exist.setExtraData(request.getExtraData());
        exist.setUpdateBy(operatorId);
        exist.setUpdateTime(LocalDateTime.now());
        exist.setVersion(exist.getVersion() == null ? 0 : exist.getVersion() + 1);

        int rows = jdbcManager.executeInTransaction(tx -> sysPermissionRepository.updateById(tx, exist));
        if (rows > 0) {
            // 失效权限缓存
            invalidatePermissionCaches(exist);
        }
        return rows > 0;
    }

    /**
     * 删除系统权限
     *
     * @param id 权限ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id, Long operatorId) {
        int rows = jdbcManager.executeInTransaction(tx -> sysPermissionRepository.softDeleteById(tx, id, operatorId));
        if (rows > 0) {
            // 失效权限缓存
            redisService.delete(permissionTree());
        }
        return rows > 0;
    }

    /**
     * 更新系统权限状态
     *
     * @param id 权限ID
     * @param status 状态
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    @Override
    public boolean updateStatus(Long id, Integer status, Long operatorId) {
        int rows = jdbcManager.executeInTransaction(tx -> sysPermissionRepository.updateStatus(tx, id, status, operatorId));
        if (rows > 0) {
            // 失效权限缓存
            redisService.delete(permissionTree());
        }
        return rows > 0;
    }

    /**
     * 根据权限ID查询系统权限
     *
     * @param id 权限ID
     * @return 系统权限
     */
    @Override
    public SysPermission getById(Long id) {
        return sysPermissionRepository.selectById(id);
    }

    /**
     * 根据权限编码查询系统权限
     *
     * @param code 权限编码
     * @return 系统权限
     */
    @Override
    public SysPermission getByCode(String code) {
        // 读缓存 id 映射
        String mapKey = permissionCodeToId(code);
        try {
            Long id = redisService.string().get(mapKey, Long.class);
            if (id != null) {
                String detailKey = permissionDetail(id);
                String json = redisService.string().get(detailKey, String.class);
                if (StringUtils.isNotBlank(json)) {
                    return JsonUtils.fromJson(json, SysPermission.class);
                }
            }
        } catch (Exception e) {
            log.warn("读取权限缓存失败 code={} ", code, e);
        }

        // 未命中缓存，查询数据库，写入缓存
        SysPermission p = sysPermissionRepository.selectByCode(code);
        if (p != null) {
            writePermissionCaches(p);
        }
        return p;
    }

    /**
     * 根据角色ID查询系统权限
     *
     * @param roleId 角色ID
     * @return 系统权限列表
     */
    @Override
    public List<SysPermission> listByRoleId(Long roleId) {
        return sysPermissionRepository.listByRoleId(roleId);
    }

    /**
     * 分页查询系统权限
     *
     * @param query 查询条件
     * @param pageRequest 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<SysPermission> page(SysPermissionQueryRequest query, PageRequest pageRequest) {
        return sysPermissionRepository.pageQuery(query, pageRequest);
    }

    /**
     * 失效权限缓存
     *
     * @param p 权限
     */
    private void invalidatePermissionCaches(SysPermission p) {
        try {
            redisService.delete(permissionTree());
            redisService.delete(permissionDetail(p.getId()));
            redisService.delete(permissionCodeToId(p.getPermissionCode()));

            if (StringUtils.isNotBlank(p.getModuleName())) {
                redisService.delete(modulePermissions(p.getModuleName()));
            }
        } catch (Exception e) {
            log.warn("删除权限缓存失败 id={}", p.getId(), e);
        }
    }

    /**
     * 写入权限缓存
     *
     * @param p 权限
     */
    private void writePermissionCaches(SysPermission p) {
        try {
            String detailKey = permissionDetail(p.getId());
            redisService.string().set(detailKey, JsonUtils.toJson(p), Duration.ofHours(24));

            String mapKey = permissionCodeToId(p.getPermissionCode());
            redisService.string().set(mapKey, p.getId(), Duration.ofHours(24));

            if (StringUtils.isNotBlank(p.getModuleName())) {
                // 模块列表缓存留给模块批量接口维护；此处不写入，避免不一致
            }
        } catch (Exception e) {
            log.warn("写入权限缓存失败 id={}", p.getId(), e);
        }
    }
}


