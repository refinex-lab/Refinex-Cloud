下<script setup lang="ts">
import { computed, watch } from 'vue';
import type { Component } from 'vue';
import { mixColor } from '@sa/color';
import { loginModuleRecord } from '@/constants/app';
import { useAppStore } from '@/store/modules/app';
import { useThemeStore } from '@/store/modules/theme';
import { $t } from '@/locales';
import PwdLogin from './modules/pwd-login.vue';
import CodeLogin from './modules/code-login.vue';
import Register from './modules/register.vue';
import ResetPwd from './modules/reset-pwd.vue';
import BindWechat from './modules/bind-wechat.vue';

interface Props {
  /** The login module */
  module?: UnionKey.LoginModule;
}

const props = defineProps<Props>();

const appStore = useAppStore();
const themeStore = useThemeStore();

watch(
  () => themeStore.darkMode,
  (val) => {
    console.log('[login] themeStore.darkMode:', val);
  },
  { immediate: true }
);

interface LoginModule {
  label: string;
  component: Component;
}

const moduleMap: Record<UnionKey.LoginModule, LoginModule> = {
  'pwd-login': { label: loginModuleRecord['pwd-login'], component: PwdLogin },
  'code-login': { label: loginModuleRecord['code-login'], component: CodeLogin },
  register: { label: loginModuleRecord.register, component: Register },
  'reset-pwd': { label: loginModuleRecord['reset-pwd'], component: ResetPwd },
  'bind-wechat': { label: loginModuleRecord['bind-wechat'], component: BindWechat }
};

const activeModule = computed(() => moduleMap[props.module || 'pwd-login']);

const bgColor = computed(() => {
  // 背景采用纯色：亮色为白，暗色为近黑，避免受主题色影响
  return themeStore.darkMode ? '#0f1117' : '#ffffff';
});
</script>

<template>
  <div
    class="login-split relative size-full overflow-hidden"
    :style="{ backgroundColor: bgColor }"
  >
    <!-- 左侧：信息区 + 表单 -->
    <section class="left-panel h-full flex flex-col w-40% lt-md:w-100%">
      <!-- 顶部栏：左-系统信息；右-主题/语言切换 -->
      <div class="topbar flex-y-center justify-between px-32px py-24px lt-sm:px-16px lt-sm:py-16px">
        <div class="flex-y-center gap-12px">
          <SystemLogo class="text-32px lt-sm:text-28px" />
          <h3
            class="login-title text-20px font-600 lt-sm:text-18px"
            :style="{ color: themeStore.darkMode ? '#fff' : '#111', fontStyle: 'italic' }"
          >
            {{ $t('system.title') }}
          </h3>
        </div>
        <div class="flex-y-center gap-12px">
          <ThemeSchemaSwitch
            :theme-schema="themeStore.themeScheme"
            :show-tooltip="false"
            class="text-18px"
            @switch="themeStore.toggleThemeScheme"
          />
          <LangSwitch
            v-if="themeStore.header.multilingual.visible"
            :lang="appStore.locale"
            :lang-options="appStore.localeOptions"
            :show-tooltip="false"
            @change-lang="appStore.changeLocale"
          />
        </div>
      </div>

      <!-- 表单区 -->
      <div class="flex-1 flex-center px-32px lt-sm:px-16px">
        <NCard :bordered="false" class="form-card w-420px lt-sm:w-300px rd-12px">
          <div>
            <h3
              class="section-title text-18px font-600"
              :style="{ color: themeStore.darkMode ? '#fff' : '#111', fontStyle: 'italic' }"
            >
              {{ $t(activeModule.label) }}
            </h3>
            <div class="pt-20px">
              <Transition :name="themeStore.page.animateMode" mode="out-in" appear>
                <component :is="activeModule.component" />
              </Transition>
            </div>
          </div>
        </NCard>
      </div>
    </section>

    <!-- 右侧：背景图 -->
    <section class="right-panel login-hero h-full w-60% lt-md:hidden"></section>
  </div>
</template>

<style scoped>
.login-split {
  display: flex;
}

/* 左侧布局 */
.left-panel {
  min-width: 0;
}

/* 右侧背景图 + 弧形分隔 */
.login-hero {
  background-image: url('@/assets/imgs/login-bg.jpg');
  background-size: cover;
  background-position: center;
  position: relative;
  border-top-left-radius: 20px;
  border-bottom-left-radius: 20px;
  overflow: hidden;
}
/* 取消伪元素弧形，使用内阴影做柔和分隔，避免中间白条 */
.login-hero::before {
  display: none;
}
.login-hero {
  box-shadow: inset 10px 0 24px rgba(0,0,0,0.06);
}
:global(.dark) .login-hero {
  box-shadow: inset 10px 0 28px rgba(0,0,0,0.5);
}

/* 标题仅保留斜体，颜色交由内联 :style 控制 */
.login-title,
.section-title {
  font-style: italic;
}

/* 主操作按钮圆角收紧（便方形风格） */
.form-card :deep(.n-button.n-button--primary) {
  border-radius: 8px !important;
}
.form-card :deep(.n-button) {
  border-radius: 8px;
}

/* 表单卡片风格微调，贴近现代后台审美 */
.form-card :deep(.n-card__content) {
  padding: 24px 24px 28px;
}

/* 卡片去毛玻璃与上浮，改为细边框 */
.form-card {
  background: transparent !important;
  box-shadow: none !important;
  border: 1px solid rgba(0, 0, 0, 0.08);
}
:global(.dark) .form-card {
  border-color: rgba(255, 255, 255, 0.28);
}

/* 响应式：小屏隐藏右侧图，表单占满宽度 */
@media (max-width: 960px) {
  .right-panel { display: none; }
  .left-panel { width: 100%; }
}

/* 进一步缩小时的内边距优化 */
@media (max-width: 480px) {
  .topbar { padding: 12px 12px; }
  .form-card :deep(.n-card__content) { padding: 18px; }
}
</style>
