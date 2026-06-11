<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  CalendarOutlined,
  HomeOutlined,
  LoginOutlined,
  LogoutOutlined,
  ReadOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const selectedKeys = computed(() => [route.path])
const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))

const logout = () => {
  authStore.clearAuth()
  router.push('/login')
}
</script>

<template>
  <router-view v-if="isAuthPage" />

  <a-layout v-else class="shell">
    <a-layout-header class="shell-header">
      <router-link class="brand" to="/">
        <div class="brand-mark">CS</div>
        <div>
          <div class="brand-title">体育场馆预约平台</div>
          <div class="brand-subtitle">Campus Sports Hub</div>
        </div>
      </router-link>

      <a-menu mode="horizontal" :selected-keys="selectedKeys" class="top-menu">
        <a-menu-item key="/">
          <router-link to="/"><HomeOutlined /> 首页</router-link>
        </a-menu-item>
        <a-menu-item key="/venues">
          <router-link to="/venues"><ReadOutlined /> 场馆目录</router-link>
        </a-menu-item>
        <a-menu-item key="/reservation">
          <router-link to="/reservation"><CalendarOutlined /> 在线预约</router-link>
        </a-menu-item>
        <a-menu-item key="/my-reservations">
          <router-link to="/my-reservations"><TeamOutlined /> 我的预约</router-link>
        </a-menu-item>
        <a-menu-item key="/user-center">
          <router-link to="/user-center"><UserOutlined /> 个人信息</router-link>
        </a-menu-item>
      </a-menu>

      <div class="user-area">
        <a-space v-if="authStore.user">
          <span class="user-name">你好，{{ authStore.user.username }}</span>
          <a-button size="small" ghost @click="logout">
            <LogoutOutlined />
            退出
          </a-button>
        </a-space>
        <a-space v-else>
          <router-link to="/login">
            <a-button size="small" ghost><LoginOutlined /> 登录</a-button>
          </router-link>
          <router-link to="/register">
            <a-button size="small" type="primary">注册</a-button>
          </router-link>
        </a-space>
      </div>
    </a-layout-header>

    <a-layout-content>
      <div class="page-wrap">
        <router-view />
      </div>
    </a-layout-content>
  </a-layout>
</template>
