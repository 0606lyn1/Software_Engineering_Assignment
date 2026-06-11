<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { MailOutlined, SafetyCertificateOutlined, SaveOutlined, UserOutlined } from '@ant-design/icons-vue'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const saving = ref(false)
const form = reactive({
  username: authStore.user?.username || '',
  email: authStore.user?.email || '',
})

const save = async () => {
  if (!authStore.user) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  saving.value = true
  try {
    const res = await api.updateUser(authStore.user.id, {
      username: form.username,
      email: form.email,
      role: authStore.user.role,
    })
    authStore.setAuth(authStore.token, res.data)
    message.success('个人资料已更新')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <section class="page-hero compact">
    <div>
      <a-tag color="blue">Profile</a-tag>
      <h1>个人信息</h1>
      <p>维护账号基础资料，保证预约记录和评论反馈归属清晰。</p>
    </div>
  </section>

  <div class="profile-layout">
    <a-card class="profile-card" :bordered="false">
      <a-avatar :size="72" class="profile-avatar">
        {{ authStore.user?.username?.slice(0, 1)?.toUpperCase() || 'U' }}
      </a-avatar>
      <h2>{{ authStore.user?.username || '未登录用户' }}</h2>
      <p>{{ authStore.user?.email || '登录后查看邮箱' }}</p>
      <a-tag color="gold"><SafetyCertificateOutlined /> {{ authStore.user?.role || 'GUEST' }}</a-tag>
    </a-card>

    <a-card class="form-card" :bordered="false">
      <a-form layout="vertical" @finish="save">
        <a-form-item
          label="用户名"
          name="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input v-model:value="form.username" size="large">
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item
          label="邮箱"
          name="email"
          :rules="[{ required: true, type: 'email', message: '请输入正确邮箱' }]"
        >
          <a-input v-model:value="form.email" size="large">
            <template #prefix><MailOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="角色">
          <a-input :value="authStore.user?.role" disabled size="large" />
        </a-form-item>
        <a-button type="primary" html-type="submit" size="large" :loading="saving">
          <SaveOutlined />
          保存修改
        </a-button>
      </a-form>
    </a-card>
  </div>
</template>
