<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { useRouterPush } from '@/hooks/common/router';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchGenerateCaptcha, type LoginRequest } from '@/service/api/auth';

defineOptions({
  name: 'PwdLogin'
});

const authStore = useAuthStore();
const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useNaiveForm();

interface FormModel {
  loginType: 1 | 2; // 1=密码登录, 2=邮箱登录
  username: string;
  email: string;
  password: string;
  captchaUuid: string;
  captchaCode: string;
  rememberMe: boolean;
}

const model: FormModel = reactive({
  loginType: 1,
  username: '',
  email: '',
  password: '',
  captchaUuid: '',
  captchaCode: '',
  rememberMe: false
});

const captchaImage = ref('');
const loading = ref(false);

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  const { formRules } = useFormRules();

  return {
    loginType: [{ required: true, message: '请选择登录方式', trigger: 'change' }],
    username: [
      {
        required: model.loginType === 1,
        message: '用户名不能为空',
        trigger: 'blur'
      }
    ],
    email: [
      {
        required: model.loginType === 2,
        message: '邮箱不能为空',
        trigger: 'blur'
      },
      {
        type: 'email',
        message: '请输入正确的邮箱格式',
        trigger: 'blur',
        trigger: model.loginType === 2 ? 'blur' : []
      }
    ],
    password: formRules.pwd,
    captchaCode: [
      { required: true, message: '验证码不能为空', trigger: 'blur' }
    ]
  };
});

/** 获取验证码 */
async function getCaptcha() {
  try {
    loading.value = true;
    const { data } = await fetchGenerateCaptcha();
    if (data) {
      captchaImage.value = data.image;
      model.captchaUuid = data.uuid;
      model.captchaCode = '';
    }
  } catch (error) {
    window.$message?.error('获取验证码失败');
  } finally {
    loading.value = false;
  }
}

async function handleSubmit() {
  await validate();

  const loginData: LoginRequest = {
    loginType: model.loginType,
    password: model.password,
    captchaUuid: model.captchaUuid,
    captchaCode: model.captchaCode,
    rememberMe: model.rememberMe
  };

  if (model.loginType === 1) {
    loginData.username = model.username;
  } else {
    loginData.email = model.email;
  }

  await authStore.login(loginData);
}

onMounted(() => {
  getCaptcha();
});
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <!-- 登录方式选择 -->
    <NFormItem path="loginType">
      <NRadioGroup v-model:value="model.loginType" class="w-full">
        <NSpace>
          <NRadio :value="1">用户名登录</NRadio>
          <NRadio :value="2">邮箱登录</NRadio>
        </NSpace>
      </NRadioGroup>
    </NFormItem>

    <!-- 用户名/邮箱输入 -->
    <NFormItem v-if="model.loginType === 1" path="username">
      <NInput v-model:value="model.username" placeholder="请输入用户名">
        <template #prefix>
          <SvgIcon icon="ph:user" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
    <NFormItem v-if="model.loginType === 2" path="email">
      <NInput v-model:value="model.email" placeholder="请输入邮箱">
        <template #prefix>
          <SvgIcon icon="ph:envelope" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>

    <!-- 密码输入 -->
    <NFormItem path="password">
      <NInput
        v-model:value="model.password"
        type="password"
        show-password-on="click"
        placeholder="请输入密码"
      >
        <template #prefix>
          <SvgIcon icon="ph:lock" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>

    <!-- 验证码输入 -->
    <NFormItem path="captchaCode">
      <div class="w-full flex gap-12px">
        <NInput
          v-model:value="model.captchaCode"
          placeholder="请输入验证码"
          class="flex-1"
        >
          <template #prefix>
            <SvgIcon icon="ph:key" class="text-16px text-#999 dark:text-#bbb" />
          </template>
        </NInput>
        <div class="captcha-container" @click="getCaptcha">
          <img
            v-if="captchaImage"
            :src="captchaImage"
            alt="验证码"
            class="captcha-image"
            :title="'点击刷新验证码'"
          />
          <div v-else class="captcha-placeholder">
            {{ loading ? '加载中...' : '点击获取验证码' }}
          </div>
        </div>
      </div>
    </NFormItem>

    <NSpace vertical :size="24">
      <div class="flex-y-center justify-between">
        <NCheckbox v-model:checked="model.rememberMe">记住我</NCheckbox>
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

.captcha-container {
  width: 120px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e0e0e6;
  border-radius: 6px;
  cursor: pointer;
  background: #f5f5f5;
  transition: all 0.3s;
}

:global(.dark) .captcha-container {
  border-color: #303030;
  background: #1f1f1f;
}

:global(.dark) .captcha-container:hover {
  border-color: #1890ff;
}

.captcha-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.captcha-image:hover {
  opacity: 0.9;
}

.captcha-placeholder {
  font-size: 12px;
  color: #999;
  text-align: center;
  user-select: none;
  line-height: 40px;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
