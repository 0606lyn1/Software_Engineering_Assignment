<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { api } from '../api'

const router = useRouter()
const loading = ref(false)
const formState = reactive({ username: '', password: '', email: '' })

const onSubmit = async () => {
  loading.value = true
  try {
    await api.register(formState)
    message.success('\u6ce8\u518c\u6210\u529f\uff0c\u8bf7\u767b\u5f55')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <a-row justify="center" style="margin-top: 28px">
    <a-col :xs="24" :sm="18" :md="11" :lg="8">
      <a-card class="academic-card" :title="'\u5b66\u751f\u6ce8\u518c'">
        <a-form layout="vertical" @finish="onSubmit">
          <a-form-item :label="'\u7528\u6237\u540d'" name="username" :rules="[{ required: true, message: '\u8bf7\u8f93\u5165\u7528\u6237\u540d' }]"><a-input v-model:value="formState.username" /></a-form-item>
          <a-form-item :label="'\u90ae\u7bb1'" name="email" :rules="[{ required: true, type: 'email', message: '\u8bf7\u8f93\u5165\u6b63\u786e\u90ae\u7bb1' }]"><a-input v-model:value="formState.email" /></a-form-item>
          <a-form-item :label="'\u5bc6\u7801'" name="password" :rules="[{ required: true, message: '\u8bf7\u8f93\u5165\u5bc6\u7801' }]"><a-input-password v-model:value="formState.password" /></a-form-item>
          <a-button type="primary" html-type="submit" block :loading="loading">&#27880;&#20876;&#36134;&#21495;</a-button>
        </a-form>
      </a-card>
    </a-col>
  </a-row>
</template>