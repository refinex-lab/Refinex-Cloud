<script setup lang="ts">
import { loginModuleRecord } from '@/constants/app';
import { useRouterPush } from '@/hooks/common/router';
import { $t } from '@/locales';
import { useAppStore } from '@/store/modules/app';
import { useThemeStore } from '@/store/modules/theme';
import type { Component } from 'vue';
import { computed } from 'vue';
import BindWechat from './modules/bind-wechat.vue';
import CodeLogin from './modules/code-login.vue';
import PwdLogin from './modules/pwd-login.vue';
import Register from './modules/register.vue';
import ResetPwd from './modules/reset-pwd.vue';
import GlobalFooter from '@/layouts/modules/global-footer/index.vue';

interface Props {
  /** The login module */
  module?: UnionKey.LoginModule;
}

const props = defineProps<Props>();

const appStore = useAppStore();
const themeStore = useThemeStore();
const { toggleLoginModule } = useRouterPush();

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
</script>

<template>
  <div class="login-container relative size-full overflow-hidden">
    <!-- 右上角：主题/语言切换按钮 -->
    <div class="top-right-controls fixed top-24px right-32px z-10 flex-y-center gap-12px lt-sm:top-16px lt-sm:right-16px">
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

    <!-- 左侧：信息区 + 表单 -->
    <section class="left-panel h-full flex flex-col w-40% lt-md:w-100%">
      <!-- 顶部栏：仅显示系统信息 -->
      <div class="topbar flex-y-center px-32px py-24px lt-sm:px-16px lt-sm:py-16px">
        <div class="flex-y-center gap-12px">
          <SystemLogo class="text-32px lt-sm:text-28px" />
          <h3
            class="login-title text-24px font-700 lt-sm:text-20px"
            :style="{ color: themeStore.darkMode ? '#fff' : '#1a1a1a', fontStyle: 'normal' }"
          >
            {{ $t('system.title') }}
          </h3>
        </div>
      </div>

      <!-- 表单区 -->
      <div class="flex-1 flex-center px-24px lt-sm:px-16px" style="align-items: flex-start; padding-top: 100px;">
        <NCard :bordered="false" class="form-card w-480px lt-sm:w-320px rd-12px">
          <div>
            <h3
              class="section-title text-32px font-600 leading-tight"
              :style="{ color: themeStore.darkMode ? '#fff' : '#1a1a1a', fontStyle: 'normal' }"
            >
              {{ $t('page.login.common.loginTo') }}<br>{{ $t('system.title') }}
            </h3>
            <div class="flex-y-center gap-12px mt-20px mb-20px">
              <span class="text-14px text-#666 dark:text-#999">{{ $t('page.login.common.noAccount') }}</span>
              <NButton text class="register-link" @click="toggleLoginModule('register')">
                {{ $t('page.login.common.register') }}
              </NButton>
            </div>
            <div class="pt-20px">
              <Transition :name="themeStore.page.animateMode" mode="out-in" appear>
                <component :is="activeModule.component" />
              </Transition>
            </div>
          </div>
        </NCard>
      </div>

      <!-- 底部页脚 -->
      <div class="footer-container px-32px pb-24px lt-sm:px-16px lt-sm:pb-16px">
        <GlobalFooter />
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 全屏背景容器 */
.login-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
}

/* 右上角控制按钮 */
.top-right-controls {
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

:global(.dark) .top-right-controls {
  background: rgba(40, 40, 40, 0.6);
  border-color: rgba(255, 255, 255, 0.1);
}

/* 左侧面板 */
.left-panel {
  min-width: 0;
  position: relative;
  z-index: 1;
}

/* 标题样式 */
.login-title {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.section-title {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  white-space: pre-line;
  line-height: 1.3;
  margin-bottom: 8px;
}

/* 表单卡片 */
.form-card {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  backdrop-filter: none;
  border-radius: 20px !important;
  max-width: 520px;
}

/* 链接通用样式 */
.register-link {
  color: #1890ff !important;
  text-decoration: none;
  font-size: 14px;
  padding: 0 !important;
  height: auto !important;
  min-height: auto !important;
  background: transparent !important;
}

.register-link:hover {
  color: #40a9ff !important;
  text-decoration: underline;
  background: transparent !important;
}

/* 页脚容器 */
.footer-container {
  margin-top: auto;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 响应式布局 */
@media (max-width: 960px) {
  .left-panel {
    width: 100%;
  }
  .login-container {
    background-attachment: scroll;
  }
}

@media (max-width: 768px) {
  .topbar {
    padding: 16px;
  }
  .form-card {
    width: 100%;
    max-width: 360px;
  }
  .top-right-controls {
    top: 16px;
    right: 16px;
    padding: 6px 10px;
  }
}

@media (max-width: 480px) {
  .form-card {
    width: 100%;
    max-width: 300px;
  }
  .top-right-controls {
    top: 12px;
    right: 12px;
    padding: 4px 8px;
  }
}
</style>
