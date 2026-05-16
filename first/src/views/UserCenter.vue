<script setup lang="ts">
import { reactive } from 'vue'
import { message } from 'ant-design-vue'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const form = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || '',
})

const save = async () => {
  if (!authStore.user) {
    message.warning('\u8bf7\u5148\u767b\u5f55')
    return
  }
  const res = await api.updateUser(authStore.user.id, {
    username: form.username,
    email: form.email,
    role: authStore.user.role,
  })
  authStore.setAuth(authStore.token, res.data)
  message.success('\u4e2a\u4eba\u8d44\u6599\u5df2\u66f4\u65b0')
}
</script>

<template>
  <a-card class="academic-card" :title="'\u4e2a\u4eba\u4fe1\u606f\u7ef4\u62a4'">
    <a-form layout="vertical" @finish="save">
      <a-form-item :label="'\u7528\u6237\u540d'"><a-input v-model:value="form.username" /></a-form-item>
      <a-form-item :label="'\u90ae\u7bb1'"><a-input v-model:value="form.email" /></a-form-item>
      <a-form-item :label="'\u89d2\u8272'"><a-input :value="authStore.user?.role" disabled /></a-form-item>
      <a-button type="primary" html-type="submit">&#20445;&#23384;&#20462;&#25913;</a-button>
    </a-form>
  </a-card>
</template>