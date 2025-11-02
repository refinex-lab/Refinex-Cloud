/**
 * 敏感字段显示组件
 * 支持点击眼睛图标解密查看明文
 */

import { EyeOutlined, EyeInvisibleOutlined, LoadingOutlined } from '@ant-design/icons';
import { message } from 'antd';
import React, { useState } from 'react';
import { decryptSensitiveData } from '@/services/common';
import type { SensitiveDecryptRequest } from '@/services/common';
import './SensitiveField.less';

export interface SensitiveFieldProps {
  /** 脱敏后的值 */
  maskedValue: string;
  /** 服务路径，如 '/refinex-platform' 或 '/refinex-ai' */
  servicePath: string;
  /** 表名 */
  tableName: string;
  /** 行 GUID */
  rowGuid: string;
  /** 字段编码 */
  fieldCode: string;
  /** 是否可以复制 */
  copyable?: boolean;
}

const SensitiveField: React.FC<SensitiveFieldProps> = ({
  maskedValue,
  servicePath,
  tableName,
  rowGuid,
  fieldCode,
  copyable = true,
}) => {
  const [visible, setVisible] = useState(false);
  const [plainValue, setPlainValue] = useState<string>('');
  const [loading, setLoading] = useState(false);

  const handleToggleVisible = async () => {
    if (visible) {
      // 隐藏明文
      setVisible(false);
      return;
    }

    // 如果已经获取过明文，直接显示
    if (plainValue) {
      setVisible(true);
      return;
    }

    // 调用接口解密
    setLoading(true);
    try {
      const params: SensitiveDecryptRequest = {
        tableName,
        rowGuid,
        fieldCode,
      };
      const response = await decryptSensitiveData(servicePath, params);
      if (response.code === 200 && response.data) {
        setPlainValue(response.data.plainValue);
        setVisible(true);
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

  const handleCopy = () => {
    if (copyable && plainValue) {
      navigator.clipboard.writeText(plainValue);
      message.success('已复制到剪贴板');
    }
  };

  return (
    <span className="sensitive-field">
      <span
        className={`sensitive-field-value ${visible && copyable ? 'copyable' : ''}`}
        onClick={visible && copyable ? handleCopy : undefined}
        title={visible && copyable ? '点击复制' : undefined}
      >
        {visible ? plainValue : maskedValue}
      </span>
      <span className="sensitive-field-icon" onClick={handleToggleVisible}>
        {loading ? (
          <LoadingOutlined />
        ) : visible ? (
          <EyeInvisibleOutlined />
        ) : (
          <EyeOutlined />
        )}
      </span>
    </span>
  );
};

export default SensitiveField;

