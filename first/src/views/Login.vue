<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const formState = reactive({ username: '', password: '' })

const onSubmit = async () => {
  loading.value = true
  try {
    const res = await api.login(formState)
    authStore.setAuth(res.data.token, res.data.user)
    message.success('\u767b\u5f55\u6210\u529f')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <a-row justify="center" style="margin-top: 28px">
    <a-col :xs="24" :sm="18" :md="11" :lg="8">
      <a-card class="academic-card" :title="'\u8d26\u53f7\u767b\u5f55'">
        <a-form layout="vertical" @finish="onSubmit">
          <a-form-item :label="'\u7528\u6237\u540d'" name="username" :rules="[{ required: true, message: '\u8bf7\u8f93\u5165\u7528\u6237\u540d' }]">
            <a-input v-model:value="formState.username" autocomplete="username" />
          </a-form-item>
          <a-form-item :label="'\u5bc6\u7801'" name="password" :rules="[{ required: true, message: '\u8bf7\u8f93\u5165\u5bc6\u7801' }]">
            <a-input-password v-model:value="formState.password" autocomplete="current-password" />
          </a-form-item>
          <a-button type="primary" html-type="submit" block :loading="loading">&#30331;&#24405;</a-button>
        </a-form>
      </a-card>
    </a-col>
  </a-row>
</template>