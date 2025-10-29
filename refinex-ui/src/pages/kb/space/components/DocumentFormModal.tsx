/**
 * 新建文档弹窗组件
 */
import React, { useState } from 'react';
import { Form, Input, message, Modal, Radio } from 'antd';
import { createDocument, getDocumentById } from '@/services/kb/document';
import type { ContentDocumentCreateRequest } from '@/services/kb/typings.d';

interface DocumentFormModalProps {
  visible: boolean;
  spaceId: number;
  directoryId?: number;
  onSuccess: (docGuid: string) => void;
  onCancel: () => void;
}

const DocumentFormModal: React.FC<DocumentFormModalProps> = ({
  visible,
  spaceId,
  directoryId,
  onSuccess,
  onCancel,
}) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const requestData: ContentDocumentCreateRequest = {
        spaceId,
        directoryId: directoryId!,
        docTitle: values.docTitle,
        docSummary: values.docSummary,
        accessType: values.accessType || 0,
      };

      const response = await createDocument(requestData);

      if (response.success && response.data) {
        message.success('文档创建成功');
        form.resetFields();

        // 获取新建文档的 GUID
        const docDetail = await getDocumentById(response.data);
        if (docDetail.success && docDetail.data) {
          onSuccess(docDetail.data.docGuid);
        }
      }
    } catch (error) {
      console.error('创建文档失败:', error);
      message.error('创建文档失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="新建文档"
      open={visible}
      onOk={handleSubmit}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="docTitle"
          label="文档标题"
          rules={[{ required: true, message: '请输入文档标题' }]}
        >
          <Input placeholder="请输入文档标题" maxLength={100} />
        </Form.Item>
        <Form.Item name="docSummary" label="文档简介">
          <Input.TextArea rows={3} placeholder="请输入文档简介（可选）" maxLength={500} />
        </Form.Item>
        <Form.Item name="accessType" label="访问权限" initialValue={0}>
          <Radio.Group>
            <Radio value={0}>继承空间权限</Radio>
            <Radio value={1}>私有</Radio>
            <Radio value={2}>公开</Radio>
          </Radio.Group>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default DocumentFormModal;

