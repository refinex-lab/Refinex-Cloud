<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouterPush } from '@/hooks/common/router';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchGenerateCaptcha, fetchRegister } from '@/service/api/auth';

defineOptions({
  name: 'Register'
});

const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useNaiveForm();

interface FormModel {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
  captchaUuid: string;
  captchaCode: string;
}

const model: FormModel = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  captchaUuid: '',
  captchaCode: ''
});

const captchaImage = ref('');
const loading = ref(false);

const rules = computed<Record<keyof FormModel, App.Global.FormRule[]>>(() => {
  const { formRules, createConfirmPwdRule } = useFormRules();

  return {
    username: formRules.userName,
    email: [
      { required: true, message: '邮箱不能为空', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: formRules.pwd,
    confirmPassword: createConfirmPwdRule(model.password),
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

  try {
    const registerData = {
      username: model.username,
      email: model.email,
      password: model.password,
      captchaUuid: model.captchaUuid,
      captchaCode: model.captchaCode
    };

    const { data, error } = await fetchRegister(registerData);

    if (!error && data) {
      window.$message?.success('注册成功，请登录');
      toggleLoginModule('pwd-login');
    } else {
      window.$message?.error('注册失败，请重试');
      getCaptcha(); // 重新获取验证码
    }
  } catch (error) {
    window.$message?.error('注册失败，请重试');
    getCaptcha();
  }
}

onMounted(() => {
  getCaptcha();
});
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <NFormItem path="username">
      <NInput v-model:value="model.username" placeholder="请输入用户名">
        <template #prefix>
          <SvgIcon icon="ph:user" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
    <NFormItem path="email">
      <NInput v-model:value="model.email" placeholder="请输入邮箱">
        <template #prefix>
          <SvgIcon icon="ph:envelope" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
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
    <NFormItem path="confirmPassword">
      <NInput
        v-model:value="model.confirmPassword"
        type="password"
        show-password-on="click"
        placeholder="请确认密码"
      >
        <template #prefix>
          <SvgIcon icon="ph:lock" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
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
    <NSpace vertical :size="18" class="w-full">
      <NButton type="primary" size="large" block @click="handleSubmit" class="login-button">
        {{ $t('common.confirm') }}
      </NButton>
      <NButton size="large" block @click="toggleLoginModule('pwd-login')" class="login-button">
        {{ $t('page.login.common.back') }}
      </NButton>
    </NSpace>
  </NForm>
</template>

<style scoped>
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

.captcha-container:hover {
  border-color: #1890ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
}

.captcha-container:active {
  transform: translateY(0);
}

:global(.dark) .captcha-container {
  border-color: #303030;
  background: #1f1f1f;
}

:global(.dark) .captcha-container:hover {
  border-color: #1890ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);
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
