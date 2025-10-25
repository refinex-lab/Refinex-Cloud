import { PageContainer } from '@ant-design/pro-components';
import { Card, Typography } from 'antd';

const { Title, Paragraph } = Typography;

/**
 * 系统配置页面
 */
const SystemConfig: React.FC = () => {
  return (
    <PageContainer
      header={{
        title: '系统配置',
        breadcrumb: {},
      }}
    >
      <Card>
        <Typography>
          <Title level={2}>系统配置管理</Title>
          <Paragraph>
            这里是系统配置页面，您可以在这里管理系统的各项配置。
          </Paragraph>
          <Paragraph>
            功能开发中，敬请期待...
          </Paragraph>
        </Typography>
      </Card>
    </PageContainer>
  );
};

export default SystemConfig;

