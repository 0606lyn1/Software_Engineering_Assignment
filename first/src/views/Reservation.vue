<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FieldTimeOutlined,
  WalletOutlined,
} from '@ant-design/icons-vue'
import { api } from '../api'
import type { Venue } from '../types'

const route = useRoute()
const router = useRouter()
const venues = ref<Venue[]>([])
const loading = ref(false)
const venueLoading = ref(false)
const form = reactive({ venueId: undefined as number | undefined, startTime: '', endTime: '' })

const selectedVenue = computed(() => venues.value.find((venue) => venue.id === form.venueId))
const durationHours = computed(() => {
  if (!form.startTime || !form.endTime) return 0
  const minutes = dayjs(form.endTime).diff(dayjs(form.startTime), 'minute')
  return minutes > 0 ? Number((minutes / 60).toFixed(1)) : 0
})
const estimatedCost = computed(() => Math.round(durationHours.value * Number(selectedVenue.value?.price || 0)))

const setQuickTime = (hoursFromNow: number, duration: number) => {
  const start = dayjs().add(hoursFromNow, 'hour').minute(0).second(0)
  const end = start.add(duration, 'hour')
  form.startTime = start.format('YYYY-MM-DDTHH:mm:ss')
  form.endTime = end.format('YYYY-MM-DDTHH:mm:ss')
}

onMounted(async () => {
  venueLoading.value = true
  try {
    const response = await api.getVenues()
    venues.value = response.data
    const queryVenueId = Number(route.query.venueId)
    if (queryVenueId && venues.value.some((venue) => venue.id === queryVenueId)) {
      form.venueId = queryVenueId
    }
  } finally {
    venueLoading.value = false
  }
})

const onSubmit = async () => {
  if (!form.venueId) {
    message.warning('请先选择场馆')
    return
  }
  if (!form.startTime || !form.endTime) {
    message.warning('请先选择完整时间段')
    return
  }
  if (!dayjs(form.startTime).isBefore(dayjs(form.endTime))) {
    message.warning('开始时间必须早于结束时间')
    return
  }

  loading.value = true
  try {
    await api.createReservation({
      venueId: form.venueId,
      startTime: dayjs(form.startTime).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(form.endTime).format('YYYY-MM-DDTHH:mm:ss'),
    })
    message.success('预约提交成功')
    router.push('/my-reservations')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="page-hero compact">
    <div>
      <a-tag color="gold">Reservation</a-tag>
      <h1>在线预约</h1>
      <p>选择场馆、确认时间段并提交，平台会保留你的预约记录。</p>
    </div>
    <a-button size="large" @click="router.push('/my-reservations')">查看我的预约</a-button>
  </section>

  <div class="reservation-layout">
    <a-card class="form-card" :bordered="false">
      <a-form layout="vertical" @finish="onSubmit">
        <a-form-item label="选择场馆" name="venueId" :rules="[{ required: true, message: '请选择场馆' }]">
          <a-select
            v-model:value="form.venueId"
            show-search
            :loading="venueLoading"
            placeholder="请选择场馆"
            option-filter-prop="label"
          >
            <a-select-option v-for="v in venues" :key="v.id" :value="v.id" :label="v.name">
              {{ v.name }}（¥{{ v.price }}/小时）
            </a-select-option>
          </a-select>
        </a-form-item>

        <div class="quick-times">
          <span>快捷时间</span>
          <a-button size="small" @click="setQuickTime(1, 1)">1 小时后</a-button>
          <a-button size="small" @click="setQuickTime(2, 2)">今晚 2 小时</a-button>
          <a-button size="small" @click="setQuickTime(24, 1)">明天同一时段</a-button>
        </div>

        <a-row :gutter="12">
          <a-col :xs="24" :md="12">
            <a-form-item label="开始时间" name="startTime">
              <a-date-picker
                v-model:value="form.startTime"
                show-time
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="结束时间" name="endTime">
              <a-date-picker
                v-model:value="form.endTime"
                show-time
                value-format="YYYY-MM-DDTHH:mm:ss"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-button type="primary" html-type="submit" size="large" :loading="loading">
          <CalendarOutlined />
          提交预约
        </a-button>
      </a-form>
    </a-card>

    <aside class="reservation-side">
      <a-card class="info-card highlight" :bordered="false">
        <h3>{{ selectedVenue?.name || '等待选择场馆' }}</h3>
        <p>{{ selectedVenue?.description || '选择场馆后，这里会展示价格、说明和本次预约预估。' }}</p>
        <div class="cost-line">
          <WalletOutlined />
          <span>预估费用</span>
          <strong>¥{{ estimatedCost }}</strong>
        </div>
      </a-card>

      <a-card class="info-card" :bordered="false">
        <a-timeline>
          <a-timeline-item color="red">
            <template #dot><FieldTimeOutlined /></template>
            选择场馆与时间段
          </a-timeline-item>
          <a-timeline-item color="gold">
            <template #dot><ClockCircleOutlined /></template>
            系统提交冲突校验
          </a-timeline-item>
          <a-timeline-item color="green">
            <template #dot><CheckCircleOutlined /></template>
            在我的预约中查看状态
          </a-timeline-item>
        </a-timeline>
      </a-card>
    </aside>
  </div>
</template>
