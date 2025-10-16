<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouterPush } from '@/hooks/common/router';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchGenerateCaptcha } from '@/service/api/auth';

defineOptions({
  name: 'ResetPwd'
});

const { toggleLoginModule } = useRouterPush();
const { formRef, validate } = useNaiveForm();

interface FormModel {
  email: string;
  newPassword: string;
  confirmPassword: string;
  captchaUuid: string;
  captchaCode: string;
}

const model: FormModel = reactive({
  email: '',
  newPassword: '',
  confirmPassword: '',
  captchaUuid: '',
  captchaCode: ''
});

const captchaImage = ref('');
const loading = ref(false);

type RuleRecord = Partial<Record<keyof FormModel, App.Global.FormRule[]>>;

const rules = computed<RuleRecord>(() => {
  const { formRules, createConfirmPwdRule } = useFormRules();

  return {
    email: [
      { required: true, message: '邮箱不能为空', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    newPassword: formRules.pwd,
    confirmPassword: createConfirmPwdRule(model.newPassword),
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
      captchaImage.value = data.captchaImage;
      model.captchaUuid = data.captchaUuid;
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
    // 由于后端暂时没有提供重置密码的API，这里仅做演示
    // 实际项目中应该调用相应的重置密码接口
    window.$message?.success('重置密码请求已提交，请查看邮箱');
    toggleLoginModule('pwd-login');
  } catch (error) {
    window.$message?.error('重置密码失败，请重试');
    getCaptcha();
  }
}

onMounted(() => {
  getCaptcha();
});
</script>

<template>
  <NForm ref="formRef" :model="model" :rules="rules" size="large" :show-label="false" @keyup.enter="handleSubmit">
    <NFormItem path="email">
      <NInput v-model:value="model.email" placeholder="请输入注册邮箱">
        <template #prefix>
          <SvgIcon icon="ph:envelope" class="text-16px text-#999 dark:text-#bbb" />
        </template>
      </NInput>
    </NFormItem>
    <NFormItem path="newPassword">
      <NInput
        v-model:value="model.newPassword"
        type="password"
        show-password-on="click"
        placeholder="请输入新密码"
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
        placeholder="请确认新密码"
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
            :src="`data:image/png;base64,${captchaImage}`"
            alt="验证码"
            class="captcha-image"
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
}

.captcha-placeholder {
  font-size: 12px;
  color: #999;
  text-align: center;
  user-select: none;
}
</style>
