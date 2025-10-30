/**
 * 清理旧版本弹窗
 * 批量删除旧版本，保留指定数量的最新版本
 */

import React, { useState } from 'react';
import { App, Form, InputNumber, Modal } from 'antd';
import { DeleteOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { cleanOldVersions } from '@/services/kb/version';

interface VersionCleanModalProps {
  visible: boolean;
  documentId: number;
  totalVersions: number;
  onSuccess: () => void;
  onCancel: () => void;
}

const VersionCleanModal: React.FC<VersionCleanModalProps> = ({
  visible,
  documentId,
  totalVersions,
  onSuccess,
  onCancel,
}) => {
  const { message } = App.useApp();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [keepCount, setKeepCount] = useState(10);

  // 计算将要删除的数量
  const getDeleteCount = () => {
    const count = totalVersions - keepCount;
    return count > 0 ? count : 0;
  };

  // 提交清理
  const handleSubmit = async () => {
    try {
      await form.validateFields();
      const values = form.getFieldsValue();

      setLoading(true);
      const response = await cleanOldVersions(documentId, values.keepCount);

      if (response.success && response.data) {
        message.success(
          response.data.message || `已清理 ${response.data.deletedCount} 个旧版本`,
        );
        form.resetFields();
        onSuccess();
      }
    } catch (error) {
      message.error('清理失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={
        <span>
          <DeleteOutlined style={{ marginRight: 8 }} />
          清理旧版本
        </span>
      }
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      okText="确认清理"
      okType="danger"
      cancelText="取消"
      width={480}
    >
      <div style={{ marginBottom: 16 }}>
        <ExclamationCircleOutlined style={{ color: '#faad14', marginRight: 8 }} />
        <span style={{ color: '#595959' }}>
          此操作将永久删除旧版本，仅保留最近的版本，请谨慎操作！
        </span>
      </div>

      <Form
        form={form}
        layout="vertical"
        initialValues={{ keepCount: 10 }}
        onValuesChange={(_, allValues) => {
          setKeepCount(allValues.keepCount || 0);
        }}
      >
        <Form.Item
          name="keepCount"
          label="保留版本数量"
          rules={[
            { required: true, message: '请输入保留版本数量' },
            {
              type: 'number',
              min: 1,
              max: 100,
              message: '保留数量必须在 1-100 之间',
            },
          ]}
          tooltip="保留最近的 N 个版本，其余旧版本将被删除"
        >
          <InputNumber
            min={1}
            max={100}
            style={{ width: '100%' }}
            placeholder="请输入保留数量"
            addonAfter="个版本"
          />
        </Form.Item>
      </Form>

      {/* 预览信息 */}
      <div
        style={{
          padding: 16,
          background: '#fff7e6',
          border: '1px solid #ffd591',
          borderRadius: 8,
          marginTop: 16,
        }}
      >
        <div style={{ marginBottom: 8 }}>
          <strong>操作预览：</strong>
        </div>
        <div style={{ color: '#595959', fontSize: 13, lineHeight: 1.8 }}>
          <div>• 当前总版本数: {totalVersions} 个</div>
          <div>• 将保留最近: {keepCount} 个版本</div>
          <div>
            • 将删除:{' '}
            <span style={{ color: '#ff4d4f', fontWeight: 600 }}>{getDeleteCount()} 个版本</span>
          </div>
        </div>
      </div>

      {getDeleteCount() === 0 && (
        <div
          style={{
            marginTop: 16,
            padding: 12,
            background: '#e6f7ff',
            border: '1px solid #91d5ff',
            borderRadius: 6,
            color: '#0050b3',
            fontSize: 13,
          }}
        >
          提示: 保留数量大于或等于当前版本数，无需清理
        </div>
      )}
    </Modal>
  );
};

export default VersionCleanModal;

