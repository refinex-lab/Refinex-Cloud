import { Image, message, Spin } from 'antd';
import React, { useEffect, useState } from 'react';
import { generateCaptcha } from '@/services/auth';
import styles from './index.less';

interface CaptchaProps {
  /** 验证码UUID变化回调 */
  onUuidChange?: (uuid: string) => void;
  /** 验证码加载状态变化回调 */
  onLoadingChange?: (loading: boolean) => void;
  /** 自定义样式类名 */
  className?: string;
  /** 是否禁用 */
  disabled?: boolean;
}

const Captcha: React.FC<CaptchaProps> = ({
  onUuidChange,
  onLoadingChange,
  className,
  disabled = false,
}) => {
  const [captchaData, setCaptchaData] = useState<{
    uuid: string;
    image: string;
    expireSeconds: number;
  } | null>(null);
  const [loading, setLoading] = useState(false);

  // 获取验证码
  const fetchCaptcha = async () => {
    if (disabled) return;

    setLoading(true);
    onLoadingChange?.(true);

    try {
      const response = await generateCaptcha();
      if (response.success && response.data) {
        setCaptchaData(response.data);
        onUuidChange?.(response.data.uuid);
      } else {
        message.error(response.message || '获取验证码失败');
      }
    } catch (error) {
      console.error('获取验证码失败:', error);
      message.error('获取验证码失败，请重试');
    } finally {
      setLoading(false);
      onLoadingChange?.(false);
    }
  };

  // 组件挂载时自动获取验证码
  useEffect(() => {
    fetchCaptcha();
  }, []);

  return (
    <div className={`${styles.captchaContainer} ${className || ''}`}>
      <div className={styles.captchaImage}>
        {loading ? (
          <div className={styles.loadingContainer}>
            <Spin size="small" />
          </div>
        ) : captchaData ? (
          <Image
            src={captchaData.image}
            alt="验证码"
            preview={false}
            width={120}
            height={40}
            style={{ cursor: disabled ? 'not-allowed' : 'pointer' }}
            onClick={fetchCaptcha}
            title={disabled ? '验证码已禁用' : '点击刷新验证码'}
          />
        ) : (
          <div className={styles.errorContainer}>
            <span>加载失败</span>
          </div>
        )}
      </div>
    </div>
  );
};

export default Captcha;
