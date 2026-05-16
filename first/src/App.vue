<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from './stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const selectedKeys = computed(() => [route.path])

const logout = () => {
  authStore.clearAuth()
  router.push('/login')
}
</script>

<template>
  <a-layout class="shell">
    <a-layout-header class="shell-header">
      <div class="brand">
        <div class="brand-badge">CAMPUS</div>
        <div>
          <div class="brand-title">&#20307;&#32946;&#22330;&#39302;&#39044;&#32422;&#24179;&#21488;</div>
          <div class="brand-subtitle">&#23398;&#29983;&#26381;&#21153;&#38376;&#25143;</div>
        </div>
      </div>
      <a-menu mode="horizontal" :selected-keys="selectedKeys" class="top-menu">
        <a-menu-item key="/"><router-link to="/">&#39318;&#39029;</router-link></a-menu-item>
        <a-menu-item key="/venues"><router-link to="/venues">&#22330;&#39302;&#30446;&#24405;</router-link></a-menu-item>
        <a-menu-item key="/reservation"><router-link to="/reservation">&#22312;&#32447;&#39044;&#32422;</router-link></a-menu-item>
        <a-menu-item key="/my-reservations"><router-link to="/my-reservations">&#25105;&#30340;&#39044;&#32422;</router-link></a-menu-item>
        <a-menu-item key="/user-center"><router-link to="/user-center">&#20010;&#20154;&#20449;&#24687;</router-link></a-menu-item>
      </a-menu>
      <div class="user-area">
        <a-space v-if="authStore.user">
          <span>&#20320;&#22909;&#65292;{{ authStore.user.username }}</span>
          <a-button size="small" @click="logout">&#36864;&#20986;&#30331;&#24405;</a-button>
        </a-space>
        <a-space v-else>
          <router-link to="/login"><a-button size="small">&#30331;&#24405;</a-button></router-link>
          <router-link to="/register"><a-button size="small" type="primary">&#27880;&#20876;</a-button></router-link>
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