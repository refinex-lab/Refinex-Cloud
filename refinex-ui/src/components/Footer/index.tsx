import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      copyright="Powered by Refinex Cloud"
      links={[
        {
          key: 'Refinex Cloud',
          title: 'Refinex Cloud',
          href: 'https://refinex.cn',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/refinex-lab/Refinex-Cloud',
          blankTarget: true,
        },
        {
          key: 'Refinex',
          title: 'Refinex',
          href: 'https://refinex.cn',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
