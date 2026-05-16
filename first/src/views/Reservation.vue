<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { api } from '../api'
import type { Venue } from '../types'

const venues = ref<Venue[]>([])
const loading = ref(false)
const form = reactive({ venueId: undefined as number | undefined, startTime: '', endTime: '' })

onMounted(async () => {
  const response = await api.getVenues()
  venues.value = response.data
})

const onSubmit = async () => {
  if (!form.venueId) {
    message.warning('\u8bf7\u5148\u9009\u62e9\u573a\u9986')
    return
  }
  if (!form.startTime || !form.endTime) {
    message.warning('\u8bf7\u5148\u9009\u62e9\u5b8c\u6574\u65f6\u95f4\u6bb5')
    return
  }
  if (!dayjs(form.startTime).isBefore(dayjs(form.endTime))) {
    message.warning('\u5f00\u59cb\u65f6\u95f4\u5fc5\u987b\u65e9\u4e8e\u7ed3\u675f\u65f6\u95f4')
    return
  }

  loading.value = true
  try {
    await api.createReservation({
      venueId: form.venueId,
      startTime: dayjs(form.startTime).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(form.endTime).format('YYYY-MM-DDTHH:mm:ss'),
    })
    message.success('\u9884\u7ea6\u63d0\u4ea4\u6210\u529f')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <a-card class="academic-card" :title="'\u5728\u7ebf\u9884\u7ea6'">
    <a-form layout="vertical" @finish="onSubmit">
      <a-form-item :label="'\u9009\u62e9\u573a\u9986'" :rules="[{ required: true, message: '\u8bf7\u9009\u62e9\u573a\u9986' }]">
        <a-select v-model:value="form.venueId" :placeholder="'\u8bf7\u9009\u62e9\u573a\u9986'">
          <a-select-option v-for="v in venues" :key="v.id" :value="v.id">
            {{ v.name }}（¥{{ v.price }}/&#23567;&#26102;）
          </a-select-option>
        </a-select>
      </a-form-item>

      <a-row :gutter="12">
        <a-col :xs="24" :md="12">
          <a-form-item :label="'\u5f00\u59cb\u65f6\u95f4'">
            <a-date-picker v-model:value="form.startTime" show-time value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </a-form-item>
        </a-col>
        <a-col :xs="24" :md="12">
          <a-form-item :label="'\u7ed3\u675f\u65f6\u95f4'">
            <a-date-picker v-model:value="form.endTime" show-time value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
          </a-form-item>
        </a-col>
      </a-row>

      <a-button type="primary" html-type="submit" :loading="loading">&#25552;&#20132;&#39044;&#32422;</a-button>
    </a-form>
  </a-card>
</template>