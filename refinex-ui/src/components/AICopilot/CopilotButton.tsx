import { RobotOutlined } from '@ant-design/icons';
import { Tooltip } from 'antd';
import React from 'react';
import { useIntl } from '@umijs/max';

interface CopilotButtonProps {
  onClick: () => void;
}

const CopilotButton: React.FC<CopilotButtonProps> = ({ onClick }) => {
  const intl = useIntl();

  return (
    <Tooltip
      title={
        <span>
          {intl.formatMessage({ id: 'component.aiCopilot.trigger' })}{' '}
          <kbd style={{ marginLeft: 4 }}>⌘K</kbd>
        </span>
      }
    >
      <div
        style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          cursor: 'pointer',
          fontSize: 18,
        }}
        onClick={onClick}
      >
        <RobotOutlined />
      </div>
    </Tooltip>
  );
};

export default CopilotButton;

