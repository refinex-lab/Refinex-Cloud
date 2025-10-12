package cn.refinex.platform.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 平台模块错误信息常量类
 *
 * @author Refinex
 * @since 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlatformErrorMessageConstants {

    // 用户相关错误信息
    public static final String USERNAME_EXIST = "用户名已存在";
    public static final String PHONE_EXIST = "手机号已存在";
    public static final String PASSWORD_STRENGTH = "密码强度不足，必须包含字母、数字和特殊字符";

    // 字典相关错误信息
    public static final String DICT_TYPE_CODE_EXIST = "字典类型编码已存在";
    public static final String DICT_TYPE_NOT_FOUND = "字典类型不存在";
    public static final String DICT_TYPE_DISABLED = "字典类型已停用";
    public static final String DICT_DATA_VALUE_EXIST = "字典数据值已存在";
    public static final String DICT_DATA_LABEL_EXIST = "字典数据标签已存在";
    public static final String DICT_DATA_NOT_FOUND = "字典数据不存在";
    public static final String DICT_DATA_CONFLICT = "字典数据存在冲突";
    public static final String DICT_TYPE_IN_USE = "字典类型已被使用，无法删除";
}
