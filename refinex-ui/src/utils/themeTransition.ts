/**
 * 主题切换过渡动画工具
 *
 * 参考业界优秀实践：
 * - GitHub: 淡入淡出 + 轻微缩放
 * - VS Code: 平滑颜色过渡
 * - Ant Design: 优雅的渐变效果
 * - macOS: 系统级主题切换动画
 *
 * 支持：
 * 1. View Transitions API（现代浏览器）
 * 2. 传统 CSS 过渡（兼容性方案）
 * 3. 可配置的动画效果
 */

import {flushSync} from 'react-dom';

/**
 * 动画效果类型
 */
export type TransitionEffect = 'fade' | 'scale' | 'slide' | 'none';

/**
 * 过渡配置
 */
export interface TransitionConfig {
  /** 动画效果类型 */
  effect?: TransitionEffect;
  /** 动画持续时间（毫秒） */
  duration?: number;
  /** 缓动函数 */
  easing?: string;
  /** 是否启用过渡效果 */
  enabled?: boolean;
}

/**
 * 默认配置
 */
const DEFAULT_CONFIG: Required<TransitionConfig> = {
  effect: 'fade',
  duration: 400,
  easing: 'cubic-bezier(0.4, 0, 0.2, 1)', // Material Design 标准缓动
  enabled: true,
};

/**
 * 检测浏览器是否支持 View Transitions API
 */
export const supportsViewTransitions = (): boolean => {
  return typeof document !== 'undefined' && 'startViewTransition' in document;
};

/**
 * 检测是否启用了减少动画的系统设置
 */
export const prefersReducedMotion = (): boolean => {
  if (typeof window === 'undefined') return false;
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

/**
 * 应用 CSS 变量到根元素
 */
const applyCSSVariables = (config: Required<TransitionConfig>): void => {
  const root = document.documentElement;
  root.style.setProperty('--theme-transition-duration', `${config.duration}ms`);
  root.style.setProperty('--theme-transition-easing', config.easing);
};

/**
 * 使用 View Transitions API 执行过渡
 */
const executeViewTransition = (callback: () => void, config: Required<TransitionConfig>): void => {
  const root = document.documentElement;

  // 设置过渡效果类型（用于 CSS 选择）
  root.setAttribute('data-transition-effect', config.effect);

  // @ts-ignore - View Transitions API 还在实验阶段
  const transition = document.startViewTransition(() => {
    flushSync(() => {
      callback();
    });
  });

  // 过渡完成后清理
  transition.finished.then(() => {
    root.removeAttribute('data-transition-effect');
  }).catch(() => {
    root.removeAttribute('data-transition-effect');
  });
};

/**
 * 使用传统方式执行过渡（降级方案）
 */
const executeFallbackTransition = (
  callback: () => void,
  config: Required<TransitionConfig>,
): void => {
  const root = document.documentElement;

  // 添加过渡类名
  root.classList.add('theme-transitioning');
  root.setAttribute('data-transition-effect', config.effect);

  // 执行主题切换
  flushSync(() => {
    callback();
  });

  // 动画结束后移除类名
  setTimeout(() => {
    root.classList.remove('theme-transitioning');
    root.removeAttribute('data-transition-effect');
  }, config.duration);
};

/**
 * 执行主题切换过渡动画
 *
 * @param callback 主题切换的回调函数
 * @param userConfig 用户自定义配置
 *
 * @example
 * ```tsx
 * executeThemeTransition(() => {
 *   setTheme('dark');
 * }, {
 *   effect: 'fade',
 *   duration: 300,
 * });
 * ```
 */
export const executeThemeTransition = (
  callback: () => void,
  userConfig?: TransitionConfig,
): void => {
  const config = { ...DEFAULT_CONFIG, ...userConfig };

  // 如果禁用动画或用户偏好减少动画，直接执行回调
  if (!config.enabled || prefersReducedMotion()) {
    callback();
    return;
  }

  // 应用 CSS 变量
  applyCSSVariables(config);

  // 如果支持 View Transitions API，优先使用
  if (supportsViewTransitions()) {
    executeViewTransition(callback, config);
  } else {
    // 降级方案
    executeFallbackTransition(callback, config);
  }
};

/**
 * 获取推荐的过渡配置
 * 根据不同场景返回最佳配置
 */
export const getRecommendedConfig = (
  scenario: 'quick' | 'smooth' | 'elegant',
): TransitionConfig => {
  switch (scenario) {
    case 'quick':
      return {
        effect: 'fade',
        duration: 200,
        easing: 'ease-out',
      };
    case 'smooth':
      return {
        effect: 'scale',
        duration: 400,
        easing: 'cubic-bezier(0.4, 0, 0.2, 1)',
      };
    case 'elegant':
      return {
        effect: 'fade',
        duration: 600,
        easing: 'cubic-bezier(0.16, 1, 0.3, 1)', // ease-out-expo
      };
    default:
      return DEFAULT_CONFIG;
  }
};

/**
 * 预加载过渡效果
 * 在应用启动时调用，确保首次切换流畅
 */
export const preloadTransitionEffects = (): void => {
  if (typeof document === 'undefined') return;

  const root = document.documentElement;
  applyCSSVariables(DEFAULT_CONFIG);

  // 预先添加并移除类名，触发浏览器解析 CSS
  root.classList.add('theme-transitioning');
  requestAnimationFrame(() => {
    root.classList.remove('theme-transitioning');
  });
};

