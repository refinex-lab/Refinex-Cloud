/**
 * 目录表单弹窗
 * 用于创建和编辑目录
 */

import { Form, Input, InputNumber, message, Modal } from 'antd';
import React, { useEffect } from 'react';
import type { ContentDirectoryTreeNode } from '@/services/kb/typings';
import { createDirectory, updateDirectory } from '@/services/kb/directory';

interface DirectoryFormModalProps {
  visible: boolean;
  mode: 'create' | 'edit';
  spaceId: number;
  directory?: ContentDirectoryTreeNode | null;
  parentDirectory?: ContentDirectoryTreeNode | null;
  onSuccess: () => void;
  onCancel: () => void;
}

const DirectoryFormModal: React.FC<DirectoryFormModalProps> = ({
  visible,
  mode,
  spaceId,
  directory,
  parentDirectory,
  onSuccess,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = React.useState(false);

  useEffect(() => {
    if (visible) {
      if (mode === 'edit' && directory) {
        // 编辑模式：填充表单
        form.setFieldsValue({
          directoryName: directory.directoryName,
          sort: directory.sort,
          remark: directory.remark || '',
        });
      } else {
        // 创建模式：重置表单
        form.resetFields();
      }
    }
  }, [visible, mode, directory, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      if (mode === 'create') {
        // 创建目录
        await createDirectory({
          spaceId,
          parentId: parentDirectory?.id || 0,
          directoryName: values.directoryName,
          sort: values.sort || 0,
          remark: values.remark,
        });
        message.success('创建成功');
      } else {
        // 更新目录
        if (!directory) {
          message.error('目录信息不存在');
          return;
        }
        await updateDirectory({
          id: directory.id,
          directoryName: values.directoryName,
          sort: values.sort,
          remark: values.remark,
        });
        message.success('更新成功');
      }

      form.resetFields();
      onSuccess();
    } catch (error) {
      console.error('操作失败:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  const getTitle = () => {
    if (mode === 'create') {
      return parentDirectory
        ? `在"${parentDirectory.directoryName}"下新建子目录`
        : '新建根目录';
    }
    return `编辑目录"${directory?.directoryName}"`;
  };

  return (
    <Modal
      title={getTitle()}
      open={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      confirmLoading={loading}
      destroyOnClose
      width={520}
      okText="确定"
      cancelText="取消"
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
        initialValues={{
          sort: 0,
        }}
      >
        {parentDirectory && (
          <Form.Item label="父目录">
            <Input value={parentDirectory.directoryName} disabled />
          </Form.Item>
        )}

        <Form.Item
          label="目录名称"
          name="directoryName"
          rules={[
            { required: true, message: '请输入目录名称' },
            { max: 100, message: '目录名称不能超过100个字符' },
            {
              pattern: /^[^\\/]+$/,
              message: '目录名称不能包含 / 或 \\ 字符',
            },
          ]}
        >
          <Input placeholder="请输入目录名称" maxLength={100} showCount />
        </Form.Item>

        <Form.Item
          label="排序"
          name="sort"
          tooltip="数值越小越靠前"
          rules={[{ required: true, message: '请输入排序值' }]}
        >
          <InputNumber
            placeholder="请输入排序值"
            min={0}
            max={99999}
            style={{ width: '100%' }}
          />
        </Form.Item>

        <Form.Item
          label="备注"
          name="remark"
          rules={[{ max: 500, message: '备注不能超过500个字符' }]}
        >
          <Input.TextArea
            placeholder="请输入备注（选填）"
            rows={3}
            maxLength={500}
            showCount
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DirectoryFormModal;
