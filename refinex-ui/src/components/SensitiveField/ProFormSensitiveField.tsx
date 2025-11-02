/**
 * ProForm 敏感字段组件
 * 用于在 ProForm 中使用的敏感字段表单项
 */

import type { ProFormFieldProps } from '@ant-design/pro-components';
import { ProFormField } from '@ant-design/pro-components';
import React from 'react';
import SensitiveFormField from './FormField';
import type { SensitiveFormFieldProps } from './FormField';

export interface ProFormSensitiveFieldProps
  extends Omit<ProFormFieldProps, 'valueType' | 'placeholder'>,
    Omit<SensitiveFormFieldProps, 'value' | 'onChange'> {}

const ProFormSensitiveField: React.FC<ProFormSensitiveFieldProps> = ({
  isEdit,
  servicePath,
  tableName,
  rowGuid,
  fieldCode,
  placeholder,
  ...restProps
}) => {
  return (
    <ProFormField
      {...restProps}
      renderFormItem={() => (
        <SensitiveFormField
          isEdit={isEdit}
          servicePath={servicePath}
          tableName={tableName}
          rowGuid={rowGuid}
          fieldCode={fieldCode}
          placeholder={placeholder}
        />
      )}
    />
  );
};

export default ProFormSensitiveField;

