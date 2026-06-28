<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { BellOutlined, CalendarOutlined, ControlOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { api } from '../api'
import type { Announcement, Reservation, ReservationRule } from '../types'

const loading = ref(false)
const savingRule = ref(false)
const publishing = ref(false)
const reservations = ref<Reservation[]>([])
const announcements = ref<Announcement[]>([])
const rule = reactive<ReservationRule>({
  venueId: undefined,
  advanceDays: 3,
  cancelBeforeHours: 4,
  maxHoursPerBooking: 2,
  dailyLimit: 2,
  weeklyLimit: 6,
  openTime: '06:00',
  closeTime: '22:00',
  slotMinutes: 60,
})
const announcement = reactive({ title: '', content: '', level: 'INFO' })

const noShowCount = computed(() => reservations.value.filter((item) => item.status === 'NO_SHOW').length)
const appealCount = computed(() => reservations.value.filter((item) => item.appealStatus === 'PENDING').length)

const loadData = async () => {
  loading.value = true
  try {
    const [ruleRes, reservationRes, announcementRes] = await Promise.all([
      api.getEffectiveRule(),
      api.getAllReservations(),
      api.getAnnouncements(),
    ])
    Object.assign(rule, ruleRes.data)
    reservations.value = reservationRes.data
    announcements.value = announcementRes.data
  } finally {
    loading.value = false
  }
}

const saveRule = async () => {
  savingRule.value = true
  try {
    const res = await api.saveReservationRule({ ...rule, venueId: undefined })
    Object.assign(rule, res.data)
    message.success('预约规则已保存')
  } finally {
    savingRule.value = false
  }
}

const publish = async () => {
  if (!announcement.title.trim() || !announcement.content.trim()) {
    message.warning('请填写公告标题和内容')
    return
  }
  publishing.value = true
  try {
    await api.createAnnouncement({ ...announcement })
    announcement.title = ''
    announcement.content = ''
    announcement.level = 'INFO'
    message.success('公告已发布')
    await loadData()
  } finally {
    publishing.value = false
  }
}

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact ops-admin-hero">
    <div>
      <a-tag color="purple">Admin Operations</a-tag>
      <h1>运营配置</h1>
      <p>集中管理预约规则、公告通知、违约与申诉状态，让系统更接近真实高校场馆预约平台。</p>
    </div>
    <a-button size="large" :loading="loading" @click="loadData"><ReloadOutlined /> 刷新</a-button>
  </section>

  <section class="summary-strip">
    <div>
      <span>全量预约</span>
      <strong>{{ reservations.length }}</strong>
    </div>
    <div>
      <span>爽约记录</span>
      <strong>{{ noShowCount }}</strong>
    </div>
    <div>
      <span>待审申诉</span>
      <strong>{{ appealCount }}</strong>
    </div>
    <div>
      <span>公告</span>
      <strong>{{ announcements.length }}</strong>
    </div>
  </section>

  <div class="ops-admin-grid">
    <a-card class="ops-admin-card" :bordered="false">
      <div class="section-heading">
        <span><ControlOutlined /> 全局预约规则</span>
        <strong>管理员配置</strong>
      </div>
      <a-form layout="vertical" :model="rule" @finish="saveRule">
        <div class="ops-form-grid">
          <a-form-item label="提前开放天数"><a-input-number v-model:value="rule.advanceDays" :min="1" :max="30" style="width: 100%" /></a-form-item>
          <a-form-item label="取消截止小时"><a-input-number v-model:value="rule.cancelBeforeHours" :min="0" style="width: 100%" /></a-form-item>
          <a-form-item label="单次最长小时"><a-input-number v-model:value="rule.maxHoursPerBooking" :min="1" style="width: 100%" /></a-form-item>
          <a-form-item label="每日预约上限"><a-input-number v-model:value="rule.dailyLimit" :min="1" style="width: 100%" /></a-form-item>
          <a-form-item label="每周预约上限"><a-input-number v-model:value="rule.weeklyLimit" :min="1" style="width: 100%" /></a-form-item>
          <a-form-item label="时段粒度分钟"><a-input-number v-model:value="rule.slotMinutes" :min="30" :step="30" style="width: 100%" /></a-form-item>
          <a-form-item label="开放时间"><a-input v-model:value="rule.openTime" placeholder="06:00" /></a-form-item>
          <a-form-item label="关闭时间"><a-input v-model:value="rule.closeTime" placeholder="22:00" /></a-form-item>
        </div>
        <a-button type="primary" html-type="submit" :loading="savingRule" block>保存规则</a-button>
      </a-form>
    </a-card>

    <a-card class="ops-admin-card" :bordered="false">
      <div class="section-heading">
        <span><BellOutlined /> 发布公告</span>
        <strong>首页与预约页展示</strong>
      </div>
      <a-form layout="vertical" @finish="publish">
        <a-form-item label="公告级别">
          <a-select v-model:value="announcement.level">
            <a-select-option value="INFO">普通通知</a-select-option>
            <a-select-option value="WARNING">重要提醒</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="标题"><a-input v-model:value="announcement.title" /></a-form-item>
        <a-form-item label="内容"><a-textarea v-model:value="announcement.content" :rows="5" /></a-form-item>
        <a-button type="primary" html-type="submit" :loading="publishing" block>发布公告</a-button>
      </a-form>
    </a-card>
  </div>

  <a-card class="table-card desktop-table-card" :bordered="false">
    <div class="section-heading">
      <span><CalendarOutlined /> 预约与违约监管</span>
      <strong>{{ reservations.length }} 条</strong>
    </div>
    <a-table :data-source="reservations" :loading="loading" row-key="id" :pagination="{ pageSize: 6 }" :scroll="{ x: 980 }">
      <a-table-column title="编号" data-index="id" width="80" />
      <a-table-column title="用户" data-index="userId" width="90" />
      <a-table-column title="场馆" data-index="venueId" width="90" />
      <a-table-column title="时间" width="240">
        <template #default="{ record }">{{ formatTime(record.startTime) }} - {{ dayjs(record.endTime).format('HH:mm') }}</template>
      </a-table-column>
      <a-table-column title="状态" data-index="status" width="120">
        <template #default="{ text }"><a-tag>{{ text }}</a-tag></template>
      </a-table-column>
      <a-table-column title="核销码" data-index="checkinCode" width="120" />
      <a-table-column title="申诉" width="220">
        <template #default="{ record }">
          <a-tag :color="record.appealStatus === 'PENDING' ? 'gold' : 'default'">{{ record.appealStatus || 'NONE' }}</a-tag>
          <span>{{ record.appealReason || '-' }}</span>
        </template>
      </a-table-column>
    </a-table>
  </a-card>

  <section class="mobile-card-list ops-mobile-list">
    <div class="section-heading">
      <span><CalendarOutlined /> 预约与违约监管</span>
      <strong>{{ reservations.length }} 条</strong>
    </div>
    <a-spin :spinning="loading">
      <a-empty v-if="!reservations.length" description="暂无预约记录" />
      <article v-for="item in reservations" :key="item.id" class="mobile-record-card">
        <div class="mobile-record-head">
          <div>
            <span>预约 #{{ item.id }}</span>
            <strong>{{ formatTime(item.startTime) }}</strong>
          </div>
          <a-tag>{{ item.status }}</a-tag>
        </div>
        <div class="mobile-record-grid">
          <p><span>用户</span><strong>ID {{ item.userId }}</strong></p>
          <p><span>场馆</span><strong>ID {{ item.venueId }}</strong></p>
          <p><span>时段</span><strong>{{ dayjs(item.startTime).format('HH:mm') }} - {{ dayjs(item.endTime).format('HH:mm') }}</strong></p>
          <p><span>核销码</span><strong>{{ item.checkinCode || '-' }}</strong></p>
        </div>
        <div class="mobile-tag-row">
          <a-tag :color="item.appealStatus === 'PENDING' ? 'gold' : 'default'">{{ item.appealStatus || 'NONE' }}</a-tag>
          <span>{{ item.appealReason || '暂无申诉原因' }}</span>
        </div>
      </article>
    </a-spin>
  </section>
</template>
