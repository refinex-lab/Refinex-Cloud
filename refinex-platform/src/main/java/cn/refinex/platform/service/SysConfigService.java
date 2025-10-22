package cn.refinex.platform.service;

import cn.refinex.common.jdbc.page.PageRequest;
import cn.refinex.common.jdbc.page.PageResult;
import cn.refinex.platform.domain.dto.request.SysConfigCreateRequest;
import cn.refinex.platform.domain.dto.request.SysConfigQueryRequest;
import cn.refinex.platform.domain.dto.request.SysConfigUpdateRequest;
import cn.refinex.platform.domain.entity.sys.SysConfig;

import java.util.List;

/**
 * 系统配置服务接口
 *
 * @author Refinex
 * @since 1.0.0
 */
public interface SysConfigService {

    /**
     * 创建系统配置
     *
     * @param request    创建请求
     * @param operatorId 操作人ID
     * @return 配置ID
     */
    Long create(SysConfigCreateRequest request, Long operatorId);

    /**
     * 更新系统配置
     *
     * @param request    更新请求
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean update(Long id, SysConfigUpdateRequest request, Long operatorId);

    /**
     * 删除系统配置
     *
     * @param id         配置ID
     * @param operatorId 操作人ID
     * @return 是否删除成功
     */
    boolean delete(Long id, Long operatorId);

    /**
     * 根据ID查询系统配置
     *
     * @param id 配置ID
     * @return 系统配置
     */
    SysConfig getById(Long id);

    /**
     * 根据键查询系统配置
     *
     * @param configKey 配置键
     * @return 系统配置
     */
    SysConfig getByKey(String configKey);

    /**
     * 根据分组查询系统配置
     *
     * @param group 配置分组
     * @return 系统配置列表
     */
    List<SysConfig> listByGroup(String group);

    /**
     * 分页查询系统配置
     *
     * @param query       查询请求
     * @param pageRequest 分页请求
     * @return 系统配置分页结果
     */
    PageResult<SysConfig> page(SysConfigQueryRequest query, PageRequest pageRequest);

    /**
     * 更新系统配置前端可见性
     *
     * @param id         配置ID
     * @param isFrontend 是否前端可见（0：否，1：是）
     * @param operatorId 操作人ID
     * @return 是否更新成功
     */
    boolean updateFrontendVisible(Long id, Integer isFrontend, Long operatorId);
}


