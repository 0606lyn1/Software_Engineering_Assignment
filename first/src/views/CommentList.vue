<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { CommentOutlined, SendOutlined, UserOutlined } from '@ant-design/icons-vue'
import { api } from '../api'
import { useAuthStore } from '../stores/auth'
import type { CommentItem } from '../types'

const props = defineProps<{ venueId: number }>()
const auth = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const comments = ref<CommentItem[]>([])
const form = reactive({ content: '' })

const loadData = async () => {
  if (!props.venueId) return
  loading.value = true
  try {
    const res = await api.getComments(props.venueId)
    comments.value = res.data
  } finally {
    loading.value = false
  }
}

const formatTime = (value: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '')

const submit = async () => {
  if (!auth.token) {
    message.warning('请先登录后再发表评论')
    return
  }
  if (!form.content.trim()) {
    message.warning('请输入评论内容')
    return
  }
  submitLoading.value = true
  try {
    await api.createComment({ venueId: props.venueId, content: form.content.trim() })
    message.success('评论发布成功')
    form.content = ''
    await loadData()
  } finally {
    submitLoading.value = false
  }
}

watch(() => props.venueId, loadData, { immediate: true })
onMounted(loadData)
</script>

<template>
  <a-space direction="vertical" class="comment-panel">
    <div class="comment-head">
      <div>
        <h3><CommentOutlined /> 使用体验</h3>
        <p>{{ comments.length }} 条评论，围绕场地、灯光、设备和预约流程。</p>
      </div>
      <a-button size="small" @click="loadData">刷新</a-button>
    </div>

    <a-alert v-if="!auth.token" type="info" show-icon message="登录后才能发表评论，浏览无需登录。" />

    <a-form :model="form" @finish="submit" layout="vertical" class="comment-form">
      <a-form-item label="写下你的使用体验">
        <a-textarea
          v-model:value="form.content"
          :rows="3"
          :maxlength="200"
          show-count
          :disabled="!auth.token"
          :placeholder="auth.token ? '例如：地面清洁、灯光、预约流程等' : '请先登录'"
        />
      </a-form-item>
      <a-button type="primary" html-type="submit" :loading="submitLoading" :disabled="!auth.token">
        <SendOutlined />
        发表评论
      </a-button>
    </a-form>

    <a-list :loading="loading" :data-source="comments" class="comment-list">
      <template #emptyText>
        <a-empty description="暂无评论，成为第一个反馈的人" />
      </template>
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta :description="formatTime(item.createdAt)">
            <template #avatar>
              <a-avatar><UserOutlined /></a-avatar>
            </template>
            <template #title>用户 {{ item.userId }}</template>
          </a-list-item-meta>
          <p class="comment-content">{{ item.content }}</p>
        </a-list-item>
      </template>
    </a-list>
  </a-space>
</template>
