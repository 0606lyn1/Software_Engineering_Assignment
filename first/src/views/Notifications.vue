<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import dayjs from 'dayjs'
import { BellOutlined, CheckCircleOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { api } from '../api'
import type { UserNotification } from '../types'

const loading = ref(false)
const notifications = ref<UserNotification[]>([])
const unreadCount = computed(() => notifications.value.filter((item) => !item.readFlag).length)

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getNotifications()
    notifications.value = res.data
  } finally {
    loading.value = false
  }
}

const read = async (item: UserNotification) => {
  if (item.readFlag) return
  await api.readNotification(item.id)
  item.readFlag = true
}

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact notification-hero">
    <div>
      <a-tag color="blue">Message Center</a-tag>
      <h1>消息中心</h1>
      <p>接收预约成功、取消、核销、申诉和违约提醒。</p>
    </div>
    <a-button size="large" :loading="loading" @click="loadData"><ReloadOutlined /> 刷新</a-button>
  </section>

  <section class="summary-strip">
    <div>
      <span>全部消息</span>
      <strong>{{ notifications.length }}</strong>
    </div>
    <div>
      <span>未读</span>
      <strong>{{ unreadCount }}</strong>
    </div>
  </section>

  <a-card class="notification-list" :bordered="false">
    <a-empty v-if="!notifications.length" description="暂无消息" />
    <article v-for="item in notifications" :key="item.id" :class="{ unread: !item.readFlag }" @click="read(item)">
      <BellOutlined />
      <div>
        <strong>{{ item.title }}</strong>
        <p>{{ item.content }}</p>
        <span>{{ item.type }} · {{ dayjs(item.createdAt).format('YYYY-MM-DD HH:mm') }}</span>
      </div>
      <a-tag :color="item.readFlag ? 'default' : 'blue'">
        <CheckCircleOutlined v-if="item.readFlag" />
        {{ item.readFlag ? '已读' : '未读' }}
      </a-tag>
    </article>
  </a-card>
</template>
