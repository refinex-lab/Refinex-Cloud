package cn.refinex.platform.controller.role.dto.converter;

import cn.refinex.platform.controller.role.dto.response.RoleResponseDTO;
import cn.refinex.platform.entity.sys.SysRole;
import cn.refinex.platform.enums.DataScopeType;
import cn.refinex.platform.enums.RoleStatus;
import cn.refinex.platform.enums.RoleType;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 角色 DTO 转换器 (MapStruct)
 *
 * @author Refinex
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface RoleConverter {

    /**
     * 实体转响应 DTO
     *
     * @param role 角色实体
     * @return 响应 DTO
     */
    @Mapping(target = "roleTypeLabel", ignore = true)
    @Mapping(target = "dataScopeLabel", ignore = true)
    @Mapping(target = "isBuiltinLabel", ignore = true)
    @Mapping(target = "statusLabel", ignore = true)
    RoleResponseDTO toResponseDTO(SysRole role);

    /**
     * 实体列表转响应 DTO 列表
     *
     * @param roles 角色实体列表
     * @return 响应 DTO 列表
     */
    List<RoleResponseDTO> toResponseDTOList(List<SysRole> roles);

    /**
     * AfterMapping: 在映射完成后填充标签字段
     *
     * @param dto  响应 DTO
     * @param role 角色实体
     */
    @AfterMapping
    default void enrichLabels(@MappingTarget RoleResponseDTO dto, SysRole role) {
        // 填充角色类型标签
        if (role.getRoleType() != null) {
            dto.setRoleTypeLabel(RoleType.getDescription(role.getRoleType()));
        }

        // 填充数据权限范围标签
        if (role.getDataScope() != null) {
            dto.setDataScopeLabel(DataScopeType.getDescription(role.getDataScope()));
        }

        // 填充是否内置标签
        if (role.getIsBuiltin() != null) {
            dto.setIsBuiltinLabel(role.getIsBuiltin() == 1 ? "是" : "否");
        }

        // 填充状态标签
        if (role.getStatus() != null) {
            dto.setStatusLabel(RoleStatus.getDescription(role.getStatus()));
        }
    }
}
