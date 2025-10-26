package cn.refinex.platform.controller.dict.dto.converter;

import cn.refinex.platform.controller.dict.dto.response.DictDataResponseDTO;
import cn.refinex.platform.controller.dict.dto.response.DictTypeResponseDTO;
import cn.refinex.platform.entity.sys.SysDictData;
import cn.refinex.platform.entity.sys.SysDictType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典 DTO 转换器
 *
 * @author Refinex
 * @since 1.0.0
 */
public class DictConverter {

    /**
     * 将字典类型实体转换为响应 DTO
     *
     * @param entity 字典类型实体
     * @return 字典类型响应 DTO
     */
    public static DictTypeResponseDTO toTypeResponseDTO(SysDictType entity) {
        if (entity == null) {
            return null;
        }
        return DictTypeResponseDTO.builder()
                .id(entity.getId())
                .dictCode(entity.getDictCode())
                .dictName(entity.getDictName())
                .dictDesc(entity.getDictDesc())
                .dictSort(entity.getDictSort())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .remark(entity.getRemark())
                .status(entity.getStatus())
                .build();
    }

    /**
     * 批量转换字典类型实体为响应 DTO
     *
     * @param entities 字典类型实体列表
     * @return 字典类型响应 DTO 列表
     */
    public static List<DictTypeResponseDTO> toTypeResponseDTOList(List<SysDictType> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(DictConverter::toTypeResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将字典数据实体转换为响应 DTO
     *
     * @param entity 字典数据实体
     * @return 字典数据响应 DTO
     */
    public static DictDataResponseDTO toDataResponseDTO(SysDictData entity) {
        if (entity == null) {
            return null;
        }
        return DictDataResponseDTO.builder()
                .id(entity.getId())
                .dictTypeId(entity.getDictTypeId())
                .dictLabel(entity.getDictLabel())
                .dictValue(entity.getDictValue())
                .dictSort(entity.getDictSort())
                .cssClass(entity.getCssClass())
                .listClass(entity.getListClass())
                .isDefault(entity.getIsDefault())
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .remark(entity.getRemark())
                .status(entity.getStatus())
                .build();
    }

    /**
     * 批量转换字典数据实体为响应 DTO
     *
     * @param entities 字典数据实体列表
     * @return 字典数据响应 DTO 列表
     */
    public static List<DictDataResponseDTO> toDataResponseDTOList(List<SysDictData> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(DictConverter::toDataResponseDTO)
                .collect(Collectors.toList());
    }

    private DictConverter() {
        // 工具类，禁止实例化
    }
}

