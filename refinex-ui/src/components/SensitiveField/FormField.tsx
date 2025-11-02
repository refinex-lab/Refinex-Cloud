/**
 * 敏感字段表单组件
 * 用于表单编辑时显示和编辑敏感字段
 */

import { EyeOutlined, EyeInvisibleOutlined, LoadingOutlined } from '@ant-design/icons';
import { Input, message } from 'antd';
import React, { useState, useEffect } from 'react';
import { decryptSensitiveData } from '@/services/common';
import type { SensitiveDecryptRequest } from '@/services/common';
import './FormField.less';

export interface SensitiveFormFieldProps {
  /** 当前值（可能是脱敏的，也可能是用户新输入的） */
  value?: string;
  /** 值变化回调 */
  onChange?: (value: string | undefined) => void;
  /** 是否为编辑模式（true=编辑已有记录，false=新建记录） */
  isEdit: boolean;
  /** 服务路径，如 '/refinex-platform' 或 '/refinex-ai' */
  servicePath?: string;
  /** 表名 */
  tableName?: string;
  /** 行 GUID */
  rowGuid?: string;
  /** 字段编码 */
  fieldCode?: string;
  /** 占位符 */
  placeholder?: string;
}

/**
 * 判断是否为脱敏值（包含 *** 字符）
 */
const isMaskedValue = (val: string | undefined): boolean => {
  return !!val && val.includes('***');
};

const SensitiveFormField: React.FC<SensitiveFormFieldProps> = ({
  value,
  onChange,
  isEdit,
  servicePath,
  tableName,
  rowGuid,
  fieldCode,
  placeholder,
}) => {
  const [visible, setVisible] = useState(false);
  const [plainValue, setPlainValue] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [hasLoadedPlainValue, setHasLoadedPlainValue] = useState(false);

  // 用户是否已修改过值
  const [isModified, setIsModified] = useState(false);

  // 当前显示的值逻辑：
  // 1. 如果可见且有明文，显示明文
  // 2. 否则显示传入的 value（可能是脱敏值或用户输入的新值）
  const displayValue = visible && plainValue ? plainValue : (value || '');

  useEffect(() => {
    // 如果用户输入了新值（非脱敏值），标记为已修改
    if (value && !isMaskedValue(value)) {
      setIsModified(true);
      setPlainValue(value);
    }
  }, [value]);

  const handleToggleVisible = async () => {
    // 如果已经显示，则隐藏
    if (visible) {
      setVisible(false);
      return;
    }

    // 如果是新建模式或用户已修改，直接显示当前值
    if (!isEdit || isModified) {
      setVisible(true);
      return;
    }

    // 如果已经加载过明文，直接显示
    if (hasLoadedPlainValue && plainValue) {
      setVisible(true);
      return;
    }

    // 如果当前值不是脱敏值，说明是明文，直接显示
    if (value && !isMaskedValue(value)) {
      setPlainValue(value);
      setVisible(true);
      return;
    }

    // 调用接口解密
    if (!servicePath || !tableName || !rowGuid || !fieldCode) {
      message.warning('缺少解密参数，无法查看明文');
      return;
    }

    setLoading(true);
    try {
      const params: SensitiveDecryptRequest = {
        tableName,
        rowGuid,
        fieldCode,
      };
      const response = await decryptSensitiveData(servicePath, params);
      if (response.code === 200 && response.data) {
        const decryptedValue = response.data.plainValue;
        setPlainValue(decryptedValue);
        setHasLoadedPlainValue(true);
        setVisible(true);
        // 同步更新到表单
        onChange?.(decryptedValue);
      } else {
        message.error(response.message || '解密失败');
      }
    } catch (error) {
      message.error('解密失败，请稍后重试');
      console.error('解密敏感数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    setIsModified(true);
    setPlainValue(newValue);
    onChange?.(newValue || undefined);
  };

  // 判断是否需要使用 password 类型
  // 1. 如果当前值是脱敏值（包含 ***），应该以明文显示，因为它本身就是脱敏的
  // 2. 如果是明文（visible=true 或用户输入的新值），且不可见时，使用 password 类型
  const shouldUsePasswordType = !visible && !isMaskedValue(displayValue);

  return (
    <div className="sensitive-form-field">
      <Input
        type={shouldUsePasswordType ? 'password' : 'text'}
        value={displayValue}
        onChange={handleInputChange}
        placeholder={placeholder || (isEdit ? '留空则不修改' : '请输入')}
        suffix={
          loading ? (
            <LoadingOutlined />
          ) : visible ? (
            <EyeInvisibleOutlined
              onClick={handleToggleVisible}
              style={{ cursor: 'pointer', color: 'rgba(0, 0, 0, 0.45)' }}
            />
          ) : (
            <EyeOutlined
              onClick={handleToggleVisible}
              style={{ cursor: 'pointer', color: 'rgba(0, 0, 0, 0.45)' }}
            />
          )
        }
      />
    </div>
  );
};

export default SensitiveFormField;

