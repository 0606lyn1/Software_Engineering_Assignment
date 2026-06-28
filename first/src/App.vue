<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  CalendarOutlined,
  HomeOutlined,
  LoginOutlined,
  LogoutOutlined,
  ReadOutlined,
  SettingOutlined,
  TeamOutlined,
  ToolOutlined,
  UserOutlined,
  BellOutlined,
  ControlOutlined,
  MenuOutlined,
} from '@ant-design/icons-vue'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const selectedKeys = computed(() => [route.path])
const isAuthPage = computed(() => ['/login', '/register'].includes(route.path))
const canReserve = computed(() => authStore.canReserve)
const canManageMaintenance = computed(() => authStore.canManageMaintenance)
const canManageUsers = computed(() => authStore.isAdmin)
const mobileMenuOpen = ref(false)

const logout = () => {
  authStore.clearAuth()
  mobileMenuOpen.value = false
  router.push('/login')
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}
</script>

<template>
  <router-view v-if="isAuthPage" />

  <a-layout v-else class="shell">
    <a-layout-header class="shell-header">
      <router-link class="brand" to="/">
        <div class="brand-mark">TY</div>
        <div>
          <div class="brand-title">体育场预约</div>
          <div class="brand-subtitle">Sports Booking</div>
        </div>
      </router-link>

      <a-menu mode="horizontal" :selected-keys="selectedKeys" class="top-menu">
        <a-menu-item key="/">
          <router-link to="/"><HomeOutlined /> 首页</router-link>
        </a-menu-item>
        <a-menu-item key="/venues">
          <router-link to="/venues"><ReadOutlined /> 场馆目录</router-link>
        </a-menu-item>
        <a-menu-item v-if="canReserve" key="/reservation">
          <router-link to="/reservation"><CalendarOutlined /> 在线预约</router-link>
        </a-menu-item>
        <a-menu-item v-if="canReserve" key="/my-reservations">
          <router-link to="/my-reservations"><TeamOutlined /> 我的预约</router-link>
        </a-menu-item>
        <a-menu-item v-if="canManageMaintenance" key="/maintenance">
          <router-link to="/maintenance"><ToolOutlined /> 场地维护</router-link>
        </a-menu-item>
        <a-menu-item v-if="canManageUsers" key="/users">
          <router-link to="/users"><SettingOutlined /> 人员管理</router-link>
        </a-menu-item>
        <a-menu-item v-if="canManageUsers" key="/operations-admin">
          <router-link to="/operations-admin"><ControlOutlined /> 运营配置</router-link>
        </a-menu-item>
        <a-menu-item key="/notifications">
          <router-link to="/notifications"><BellOutlined /> 消息</router-link>
        </a-menu-item>
        <a-menu-item key="/user-center">
          <router-link to="/user-center"><UserOutlined /> 个人信息</router-link>
        </a-menu-item>
      </a-menu>

      <div class="user-area">
        <a-space v-if="authStore.user">
          <span class="user-name">你好，{{ authStore.user.username }}</span>
          <a-tag color="cyan">{{ authStore.roleLabel }}</a-tag>
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

      <a-button class="mobile-menu-button" ghost @click="mobileMenuOpen = true">
        <MenuOutlined />
      </a-button>
    </a-layout-header>

    <a-drawer
      v-model:open="mobileMenuOpen"
      class="mobile-nav-drawer"
      placement="right"
      width="320"
      title="体育场预约"
    >
      <nav class="mobile-nav-list">
        <router-link to="/" @click="closeMobileMenu"><HomeOutlined /> 首页</router-link>
        <router-link to="/venues" @click="closeMobileMenu"><ReadOutlined /> 场馆目录</router-link>
        <router-link v-if="canReserve" to="/reservation" @click="closeMobileMenu"><CalendarOutlined /> 在线预约</router-link>
        <router-link v-if="canReserve" to="/my-reservations" @click="closeMobileMenu"><TeamOutlined /> 我的预约</router-link>
        <router-link v-if="canManageMaintenance" to="/maintenance" @click="closeMobileMenu"><ToolOutlined /> 场地维护</router-link>
        <router-link v-if="canManageUsers" to="/users" @click="closeMobileMenu"><SettingOutlined /> 人员管理</router-link>
        <router-link v-if="canManageUsers" to="/operations-admin" @click="closeMobileMenu"><ControlOutlined /> 运营配置</router-link>
        <router-link to="/notifications" @click="closeMobileMenu"><BellOutlined /> 消息中心</router-link>
        <router-link to="/user-center" @click="closeMobileMenu"><UserOutlined /> 个人信息</router-link>
      </nav>

      <div class="mobile-nav-footer">
        <template v-if="authStore.user">
          <span>{{ authStore.user.username }}</span>
          <a-tag color="cyan">{{ authStore.roleLabel }}</a-tag>
          <a-button danger block @click="logout"><LogoutOutlined /> 退出登录</a-button>
        </template>
        <template v-else>
          <router-link to="/login" @click="closeMobileMenu">
            <a-button block><LoginOutlined /> 登录</a-button>
          </router-link>
          <router-link to="/register" @click="closeMobileMenu">
            <a-button type="primary" block>注册</a-button>
          </router-link>
        </template>
      </div>
    </a-drawer>

    <a-layout-content>
      <div class="page-wrap">
        <router-view />
      </div>
    </a-layout-content>
  </a-layout>
</template>
