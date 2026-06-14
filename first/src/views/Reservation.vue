<script setup lang="ts">
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  ClockCircleOutlined,
  FieldTimeOutlined,
  MobileOutlined,
  NotificationOutlined,
  WalletOutlined,
} from '@ant-design/icons-vue'
import { api } from '../api'
import type { Announcement, ReservationRule, ReservationSlot, Venue, VenueAvailability } from '../types'

const route = useRoute()
const router = useRouter()
const venues = ref<Venue[]>([])
const slots = ref<ReservationSlot[]>([])
const announcements = ref<Announcement[]>([])
const rule = ref<ReservationRule>()
const availability = ref<VenueAvailability>()
const loading = ref(false)
const submitting = ref(false)
let refreshTimer: number | undefined
const form = reactive({
  venueId: undefined as number | undefined,
  date: dayjs().add(1, 'day').format('YYYY-MM-DD'),
  slotKey: '',
})

const selectedVenue = computed(() => venues.value.find((venue) => venue.id === form.venueId))
const selectedSlot = computed(() => slots.value.find((slot) => `${slot.startTime}_${slot.endTime}` === form.slotKey))
const availableSlots = computed(() => slots.value.filter((slot) => slot.status === 'AVAILABLE').length)
const availabilityReason = computed(() => (availability.value?.available === false ? availability.value.reason : '运维状态正常，可预约开放时段'))
const estimatedCost = computed(() => {
  if (!selectedVenue.value || !selectedSlot.value) return 0
  const hours = dayjs(selectedSlot.value.endTime).diff(dayjs(selectedSlot.value.startTime), 'hour', true)
  return Math.round(hours * Number(selectedVenue.value.price || 0))
})

const statusColor: Record<string, string> = {
  AVAILABLE: 'green',
  BOOKED: 'red',
  MAINTENANCE: 'orange',
  CLOSED: 'default',
  CLEANING_EXPIRED: 'red',
  NEED_CLEANING: 'red',
  PENDING_RECHECK: 'orange',
  FAULT: 'red',
  MISSING: 'red',
  DAMAGED: 'red',
}

const slotKey = (slot: ReservationSlot) => `${slot.startTime}_${slot.endTime}`

const loadSlots = async () => {
  if (!form.venueId || !form.date) return
  loading.value = true
  try {
    const [slotRes, ruleRes, availabilityRes] = await Promise.all([
      api.getReservationSlots(form.venueId, form.date),
      api.getEffectiveRule(form.venueId),
      api.getVenueAvailability(form.venueId),
    ])
    slots.value = slotRes.data
    rule.value = ruleRes.data
    availability.value = availabilityRes.data
    if (!slots.value.some((slot) => slotKey(slot) === form.slotKey && slot.status === 'AVAILABLE')) {
      form.slotKey = slots.value.find((slot) => slot.status === 'AVAILABLE')
        ? slotKey(slots.value.find((slot) => slot.status === 'AVAILABLE') as ReservationSlot)
        : ''
    }
  } finally {
    loading.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const [venueRes, announcementRes] = await Promise.all([api.getVenues(), api.getAnnouncements()])
    venues.value = venueRes.data
    announcements.value = announcementRes.data
    const queryVenueId = Number(route.query.venueId)
    form.venueId = queryVenueId && venues.value.some((venue) => venue.id === queryVenueId) ? queryVenueId : venues.value[0]?.id
    await loadSlots()
  } finally {
    loading.value = false
  }
}

const reserve = async () => {
  const requestedSlotKey = form.slotKey
  await loadSlots()
  const latestSlot = slots.value.find((slot) => slotKey(slot) === requestedSlotKey && slot.status === 'AVAILABLE')
  if (!form.venueId || !latestSlot) {
    message.warning('请选择可预约时段')
    return
  }
  submitting.value = true
  try {
    await api.createReservation({
      venueId: form.venueId,
      startTime: dayjs(latestSlot.startTime).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(latestSlot.endTime).format('YYYY-MM-DDTHH:mm:ss'),
    })
    message.success('预约提交成功，核销码已生成')
    router.push('/my-reservations')
  } finally {
    submitting.value = false
  }
}

const slotLabel = (slot: ReservationSlot) => `${dayjs(slot.startTime).format('HH:mm')} - ${dayjs(slot.endTime).format('HH:mm')}`

watch(() => [form.venueId, form.date], loadSlots)
onMounted(async () => {
  await loadData()
  refreshTimer = window.setInterval(() => {
    loadSlots()
  }, 15000)
})

onUnmounted(() => {
  if (refreshTimer) {
    window.clearInterval(refreshTimer)
  }
})
</script>

<template>
  <section class="page-hero compact reservation-hero">
    <div>
      <a-tag color="gold">Mobile First Booking</a-tag>
      <h1>在线预约</h1>
      <p>按场馆、日期和时段快速预约。系统会校验开放窗口、限额、维护状态和场地冲突。</p>
    </div>
    <a-button size="large" @click="router.push('/my-reservations')">查看我的预约</a-button>
  </section>

  <section class="booking-alerts" v-if="announcements.length">
    <article v-for="item in announcements.slice(0, 2)" :key="item.id">
      <NotificationOutlined />
      <div>
        <strong>{{ item.title }}</strong>
        <span>{{ item.content }}</span>
      </div>
    </article>
  </section>

  <div class="booking-mobile-flow">
    <a-card class="booking-picker-card" :bordered="false">
      <div class="section-heading">
        <span><MobileOutlined /> 预约入口</span>
        <strong>{{ availableSlots }} 个可约时段</strong>
      </div>
      <a-alert
        class="booking-status-alert"
        :type="availability?.available === false ? 'warning' : 'success'"
        show-icon
        :message="availabilityReason"
      />
      <a-form layout="vertical">
        <a-form-item label="选择场馆">
          <a-select v-model:value="form.venueId" show-search option-filter-prop="label">
            <a-select-option v-for="venue in venues" :key="venue.id" :value="venue.id" :label="venue.name">
              {{ venue.name }}（¥{{ venue.price }}/小时）
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="选择日期">
          <a-date-picker v-model:value="form.date" value-format="YYYY-MM-DD" style="width: 100%" />
        </a-form-item>
      </a-form>

      <a-spin :spinning="loading">
        <div class="slot-grid">
          <button
            v-for="slot in slots"
            :key="slotKey(slot)"
            type="button"
            class="slot-card"
            :class="{ active: form.slotKey === slotKey(slot), disabled: slot.status !== 'AVAILABLE' }"
            :disabled="slot.status !== 'AVAILABLE'"
            @click="form.slotKey = slotKey(slot)"
          >
            <strong>{{ slotLabel(slot) }}</strong>
            <a-tag :color="statusColor[slot.status] || 'default'">{{ slot.label }}</a-tag>
          </button>
        </div>
      </a-spin>
    </a-card>

    <aside class="booking-confirm-card">
      <a-card class="info-card highlight" :bordered="false">
        <h3>{{ selectedVenue?.name || '等待选择场馆' }}</h3>
        <p>{{ selectedVenue?.description || '选择场馆后，会显示费用、开放规则与核销说明。' }}</p>
        <div class="cost-line">
          <WalletOutlined />
          <span>预估费用</span>
          <strong>¥{{ estimatedCost }}</strong>
        </div>
      </a-card>

      <a-card class="info-card" :bordered="false">
        <div class="rule-list">
          <p><ClockCircleOutlined /> 开放窗口：未来 {{ rule?.advanceDays || 3 }} 天</p>
          <p><FieldTimeOutlined /> 取消截止：开场前 {{ rule?.cancelBeforeHours || 4 }} 小时</p>
          <p><CheckCircleOutlined /> 单次最长：{{ rule?.maxHoursPerBooking || 2 }} 小时</p>
          <p><CalendarOutlined /> 每日 {{ rule?.dailyLimit || 2 }} 次 / 每周 {{ rule?.weeklyLimit || 6 }} 次</p>
        </div>
      </a-card>

      <a-button type="primary" size="large" block :loading="submitting" :disabled="!selectedSlot" @click="reserve">
        <CalendarOutlined />
        确认预约并生成核销码
      </a-button>
    </aside>
  </div>
</template>
