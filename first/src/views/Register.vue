<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  LockOutlined,
  MailOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { api } from '../api'

const router = useRouter()
const loading = ref(false)
const formState = reactive({ username: '', password: '', confirmPassword: '', email: '' })

const validateConfirm = async (_rule: unknown, value: string) => {
  if (!value) {
    return Promise.reject('请再次输入密码')
  }
  if (value !== formState.password) {
    return Promise.reject('两次输入的密码不一致')
  }
  return Promise.resolve()
}

const onSubmit = async () => {
  loading.value = true
  try {
    await api.register({
      username: formState.username,
      password: formState.password,
      email: formState.email,
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page register-page">
    <div class="auth-backdrop" />
    <div class="campus-flow" aria-hidden="true" />
    <div class="campus-sparkles" aria-hidden="true" />
    <div class="campus-orb orb-one" aria-hidden="true" />
    <div class="campus-orb orb-two" aria-hidden="true" />

    <section class="auth-hero compact">
      <div class="auth-copy reveal-up">
        <a-tag color="green" class="auth-kicker">Student Access</a-tag>
        <h1>创建学生账号，开启场馆预约服务。</h1>
        <p>
          注册后即可进入体育场预约，完成场馆查询、预约提交、
          记录追踪和体验反馈。
        </p>

        <div class="auth-feature-list">
          <span><TeamOutlined /> 统一身份</span>
          <span><SafetyCertificateOutlined /> 预约保护</span>
          <span><CalendarOutlined /> 预约记录</span>
          <span><CheckCircleOutlined /> 体验反馈</span>
        </div>
      </div>

      <a-card class="auth-card reveal-up" :bordered="false">
        <div class="auth-card-head">
          <div>
            <p>新用户入口</p>
            <h2>学生注册</h2>
          </div>
          <div class="auth-card-icon"><TeamOutlined /></div>
        </div>

        <div class="auth-slider" aria-label="注册后服务提示">
          <div class="auth-slider-track">
            <span>创建校园账号</span>
            <span>进入预约平台</span>
            <span>管理个人记录</span>
            <span>分享场馆体验</span>
            <span>创建校园账号</span>
            <span>进入预约平台</span>
          </div>
        </div>

        <a-form :model="formState" layout="vertical" @finish="onSubmit">
          <a-form-item
            label="用户名"
            name="username"
            :rules="[{ required: true, message: '请输入用户名' }]"
          >
            <a-input v-model:value="formState.username" size="large" placeholder="设置用户名">
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="邮箱"
            name="email"
            :rules="[{ required: true, type: 'email', message: '请输入正确邮箱' }]"
          >
            <a-input v-model:value="formState.email" size="large" placeholder="name@example.com">
              <template #prefix><MailOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="密码"
            name="password"
            :rules="[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少 6 位' },
            ]"
          >
            <a-input-password v-model:value="formState.password" size="large" placeholder="至少 6 位">
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <a-form-item label="确认密码" name="confirmPassword" :rules="[{ validator: validateConfirm }]">
            <a-input-password
              v-model:value="formState.confirmPassword"
              size="large"
              placeholder="再次输入密码"
            >
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <a-button type="primary" html-type="submit" block size="large" :loading="loading">
            创建学生账号
          </a-button>
        </a-form>

        <div class="auth-footer">
          <span>已有账号？</span>
          <RouterLink to="/login">返回登录</RouterLink>
        </div>
      </a-card>
    </section>
  </main>
</template>
