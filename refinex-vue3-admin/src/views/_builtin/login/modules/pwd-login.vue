<script setup lang="ts">
import { computed, reactive } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { useRouterPush } from '@/hooks/common/router';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();
const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useNaiveForm();

interface FormModel {
  userName: string;
  password: string;
}

const model: FormModel = reactive({
  userName: '',
  password: ''
});

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  // inside computed to make locale reactive, if not apply i18n, you can define it without computed
  const { formRules } = useFormRules();

  return {
    userName: formRules.userName,
    password: formRules.pwd
  };
});

async function handleSubmit() {
  await validate();
  await authStore.login(model.userName, model.password);
}
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <NFormItem path="userName">
      <NInput v-model:value="model.userName" :placeholder="$t('page.login.common.userNamePlaceholder')">
        <template #prefix>
          <SvgIcon icon="ph:user" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
    <NFormItem path="password">
      <NInput
        v-model:value="model.password"
        type="password"
        show-password-on="click"
        :placeholder="$t('page.login.common.passwordPlaceholder')"
      >
        <template #prefix>
          <SvgIcon icon="ph:lock" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
    <NSpace vertical :size="24">
      <div class="flex-y-center justify-between">
        <NCheckbox>{{ $t('page.login.pwdLogin.rememberMe') }}</NCheckbox>
        <NButton quaternary class="forget-password-link" @click="toggleLoginModule('reset-pwd')">
          {{ $t('page.login.pwdLogin.forgetPassword') }}
        </NButton>
      </div>
      <NButton type="primary" size="large" block :loading="authStore.loginLoading" @click="handleSubmit" class="login-button">
        {{ $t('common.confirm') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped>
.forget-password-link {
  color: #1890ff !important;
  text-decoration: none;
  font-size: 14px;
  padding: 0 !important;
  height: auto !important;
  min-height: auto !important;
  background: transparent !important;
}

.forget-password-link:hover {
  color: #40a9ff !important;
  text-decoration: underline;
  background: transparent !important;
}

:global(.dark) .forget-password-link {
  color: #69c0ff !important;
}

:global(.dark) .forget-password-link:hover {
  color: #91d5ff !important;
  background: transparent !important;
}

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

:global(.dark) .register-link {
  color: #69c0ff !important;
}

:global(.dark) .register-link:hover {
  color: #91d5ff !important;
  background: transparent !important;
}

.login-button {
  border-radius: 8px !important;
}
</style>
