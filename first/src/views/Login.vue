<script setup lang="ts">
import { reactive, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  LockOutlined,
  ReadOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
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
    message.success('登录成功，正在进入预约中心')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page login-page">
    <div class="auth-backdrop" />
    <div class="campus-flow" aria-hidden="true" />
    <div class="campus-sparkles" aria-hidden="true" />
    <div class="campus-orb orb-one" aria-hidden="true" />
    <div class="campus-orb orb-two" aria-hidden="true" />

    <section class="auth-hero desktop-login-view">
      <div class="auth-copy reveal-up">
        <a-tag color="cyan" class="auth-kicker">Sports Booking Service</a-tag>
        <h1>体育场预约</h1>
        <p>
          面向学生提供场馆查询、在线预约、记录管理与体验反馈，
          让校园体育资源使用更透明、更高效。
        </p>

        <div class="auth-service-grid">
          <div>
            <ReadOutlined />
            <strong>场馆查询</strong>
            <span>查看价格、说明与可用项目</span>
          </div>
          <div>
            <CalendarOutlined />
            <strong>在线预约</strong>
            <span>选择场馆和完整时间段</span>
          </div>
          <div>
            <TeamOutlined />
            <strong>记录管理</strong>
            <span>集中跟踪个人预约状态</span>
          </div>
          <div>
            <CheckCircleOutlined />
            <strong>体验反馈</strong>
            <span>发布评论，沉淀真实使用体验</span>
          </div>
        </div>

        <div class="auth-hours">
          <span>开放时段</span>
          <strong>06:00 - 22:00</strong>
        </div>

        <div class="after-login-preview" aria-label="登录后页面动效预览">
          <div class="preview-browser-bar">
            <span />
            <span />
            <span />
            <strong>登录后平台预览</strong>
          </div>
          <div class="preview-viewport">
            <div class="preview-scroll">
              <section class="preview-page dashboard">
                <div class="preview-page-head">
                  <b>首页总览</b>
                  <small>今日开放 12 个场馆</small>
                </div>
                <div class="preview-stat-row">
                  <span>可预约</span>
                  <strong>8</strong>
                  <span>待使用</span>
                  <strong>2</strong>
                </div>
              </section>
              <section class="preview-page venues">
                <div class="preview-page-head">
                  <b>场馆目录</b>
                  <small>篮球馆 / 羽毛球馆 / 游泳馆</small>
                </div>
                <div class="preview-card-list">
                  <i />
                  <i />
                  <i />
                </div>
              </section>
              <section class="preview-page booking">
                <div class="preview-page-head">
                  <b>在线预约</b>
                  <small>选择时间段并提交</small>
                </div>
                <div class="preview-timeline">
                  <span />
                  <span />
                  <span />
                </div>
              </section>
              <section class="preview-page records">
                <div class="preview-page-head">
                  <b>我的预约</b>
                  <small>查看状态和历史记录</small>
                </div>
                <div class="preview-records">
                  <span>已确认</span>
                  <span>待开始</span>
                </div>
              </section>
            </div>
          </div>
        </div>
      </div>

      <a-card class="auth-card reveal-up" :bordered="false">
        <div class="auth-card-head">
          <div>
            <p>学生服务入口</p>
            <h2>账号登录</h2>
          </div>
          <div class="auth-card-icon"><SafetyCertificateOutlined /></div>
        </div>

        <div class="auth-slider" aria-label="预约流程提示">
          <div class="auth-slider-track">
            <span>查询可用场馆</span>
            <span>选择预约时间</span>
            <span>提交后查看记录</span>
            <span>发布使用反馈</span>
            <span>查询可用场馆</span>
            <span>选择预约时间</span>
          </div>
        </div>

        <a-form :model="formState" layout="vertical" @finish="onSubmit">
          <a-form-item
            label="用户名"
            name="username"
            :rules="[{ required: true, message: '请输入用户名' }]"
          >
            <a-input
              v-model:value="formState.username"
              autocomplete="username"
              size="large"
              placeholder="输入校园账号"
            >
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="密码"
            name="password"
            :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password
              v-model:value="formState.password"
              autocomplete="current-password"
              size="large"
              placeholder="输入登录密码"
            >
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <a-button type="primary" html-type="submit" block size="large" :loading="loading">
            进入预约平台
          </a-button>
        </a-form>

        <div class="auth-footer">
          <span><SafetyCertificateOutlined /> 登录后可提交预约、评论和修改资料</span>
          <RouterLink to="/register">创建新账号</RouterLink>
        </div>
      </a-card>
    </section>

    <section class="mobile-login-view">
      <div class="mobile-login-brand">
        <div class="brand-mark">TY</div>
        <div>
          <a-tag color="gold">Sports Booking</a-tag>
          <h1>体育场预约</h1>
          <p>登录后即可查询场馆、预约时段、查看核销码。</p>
        </div>
      </div>

      <a-card class="mobile-login-card" :bordered="false">
        <div class="mobile-login-head">
          <div>
            <span>学生服务入口</span>
            <h2>账号登录</h2>
          </div>
          <SafetyCertificateOutlined />
        </div>

        <a-form :model="formState" layout="vertical" @finish="onSubmit">
          <a-form-item
            label="用户名"
            name="username"
            :rules="[{ required: true, message: '请输入用户名' }]"
          >
            <a-input
              v-model:value="formState.username"
              autocomplete="username"
              size="large"
              placeholder="输入校园账号"
            >
              <template #prefix><UserOutlined /></template>
            </a-input>
          </a-form-item>

          <a-form-item
            label="密码"
            name="password"
            :rules="[{ required: true, message: '请输入密码' }]"
          >
            <a-input-password
              v-model:value="formState.password"
              autocomplete="current-password"
              size="large"
              placeholder="输入登录密码"
            >
              <template #prefix><LockOutlined /></template>
            </a-input-password>
          </a-form-item>

          <a-button type="primary" html-type="submit" block size="large" :loading="loading">
            进入预约平台
          </a-button>
        </a-form>

        <div class="mobile-login-footer">
          <span><SafetyCertificateOutlined /> 可提交预约、评论和修改资料</span>
          <RouterLink to="/register">创建新账号</RouterLink>
        </div>
      </a-card>
    </section>
  </main>
</template>
