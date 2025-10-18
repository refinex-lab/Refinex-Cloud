import { QuestionCircleOutlined } from '@ant-design/icons';
import { SelectLang as UmiSelectLang } from '@umijs/max';
import { ThemeSwitch } from './ThemeSwitch';

export type SiderTheme = 'light' | 'dark';

export const SelectLang: React.FC = () => {
  return (
    <UmiSelectLang
      style={{
        padding: 4,
      }}
    />
  );
};

export const Question: React.FC = () => {
  return (
    <a
      href="https://github.com/refinex-lab/Refinex-Cloud/wiki"
      target="_blank"
      rel="noreferrer"
      style={{
        display: 'inline-flex',
        padding: '4px',
        fontSize: '18px',
        color: 'inherit',
      }}
    >
      <QuestionCircleOutlined />
    </a>
  );
};

export { ThemeSwitch };
