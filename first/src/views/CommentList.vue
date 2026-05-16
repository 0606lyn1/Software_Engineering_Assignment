<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { message } from 'ant-design-vue'
import { api } from '../api'
import type { CommentItem } from '../types'

const props = defineProps<{ venueId: number }>()
const loading = ref(false)
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

const submit = async () => {
  if (!form.content.trim()) {
    message.warning('\u8bf7\u8f93\u5165\u8bc4\u8bba\u5185\u5bb9')
    return
  }
  await api.createComment({ venueId: props.venueId, content: form.content })
  message.success('\u8bc4\u8bba\u53d1\u5e03\u6210\u529f')
  form.content = ''
  await loadData()
}

watch(() => props.venueId, loadData, { immediate: true })
onMounted(loadData)
</script>

<template>
  <a-space direction="vertical" style="width: 100%">
    <a-form @finish="submit" layout="vertical">
      <a-form-item :label="'\u5199\u4e0b\u4f60\u7684\u4f7f\u7528\u4f53\u9a8c'">
        <a-textarea v-model:value="form.content" :rows="3" :placeholder="'\u4f8b\u5982\uff1a\u5730\u9762\u6e05\u6d01\u3001\u706f\u5149\u3001\u9884\u7ea6\u6d41\u7a0b\u7b49'" />
      </a-form-item>
      <a-button type="primary" html-type="submit">&#21457;&#34920;&#35780;&#35770;</a-button>
    </a-form>

    <a-list :loading="loading" :data-source="comments" bordered>
      <template #renderItem="{ item }">
        <a-list-item>
          <a-list-item-meta :description="item.createdAt">
            <template #title>&#29992;&#25143; {{ item.userId }}</template>
          </a-list-item-meta>
          <div>{{ item.content }}</div>
        </a-list-item>
      </template>
    </a-list>
  </a-space>
</template>