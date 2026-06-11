<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import { CalendarOutlined, DeleteOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { api } from '../api'
import type { Reservation } from '../types'

const router = useRouter()
const data = ref<Reservation[]>([])
const loading = ref(false)
const deletingId = ref<number | null>(null)

const activeCount = computed(() => data.value.filter((item) => item.status !== 'CANCELLED').length)
const nextReservation = computed(() =>
  [...data.value]
    .filter((item) => dayjs(item.startTime).isAfter(dayjs()))
    .sort((a, b) => dayjs(a.startTime).valueOf() - dayjs(b.startTime).valueOf())[0],
)

const statusColor = (status: string) => {
  const normalized = status?.toUpperCase()
  if (normalized === 'CANCELLED') return 'red'
  if (normalized === 'FINISHED') return 'blue'
  if (normalized === 'PENDING') return 'gold'
  return 'green'
}

const formatTime = (value: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getMyReservations()
    data.value = res.data
  } finally {
    loading.value = false
  }
}

const remove = async (id: number) => {
  deletingId.value = id
  try {
    await api.deleteReservation(id)
    message.success('预约已删除')
    await loadData()
  } finally {
    deletingId.value = null
  }
}

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact">
    <div>
      <a-tag color="green">My Schedule</a-tag>
      <h1>我的预约</h1>
      <p>集中查看预约状态，必要时取消不再使用的时间段。</p>
    </div>
    <a-space wrap>
      <a-button @click="loadData"><ReloadOutlined /> 刷新</a-button>
      <a-button type="primary" @click="router.push('/reservation')">
        <CalendarOutlined />
        新建预约
      </a-button>
    </a-space>
  </section>

  <section class="summary-strip">
    <div>
      <span>全部记录</span>
      <strong>{{ data.length }}</strong>
    </div>
    <div>
      <span>有效预约</span>
      <strong>{{ activeCount }}</strong>
    </div>
    <div>
      <span>下一场</span>
      <strong>{{ nextReservation ? formatTime(nextReservation.startTime) : '暂无' }}</strong>
    </div>
  </section>

  <a-card class="table-card" :bordered="false">
    <a-table
      :data-source="data"
      :loading="loading"
      row-key="id"
      :pagination="{ pageSize: 6 }"
      :scroll="{ x: 760 }"
    >
      <template #emptyText>
        <a-empty description="暂无预约记录">
          <a-button type="primary" @click="router.push('/venues')">去选择场馆</a-button>
        </a-empty>
      </template>
      <a-table-column title="预约编号" data-index="id" width="110" />
      <a-table-column title="场馆 ID" data-index="venueId" width="100" />
      <a-table-column title="开始时间" data-index="startTime" width="180">
        <template #default="{ text }">{{ formatTime(text) }}</template>
      </a-table-column>
      <a-table-column title="结束时间" data-index="endTime" width="180">
        <template #default="{ text }">{{ formatTime(text) }}</template>
      </a-table-column>
      <a-table-column title="状态" data-index="status" width="120">
        <template #default="{ text }">
          <a-tag :color="statusColor(text)">{{ text || 'CONFIRMED' }}</a-tag>
        </template>
      </a-table-column>
      <a-table-column title="操作" width="120" fixed="right">
        <template #default="{ record }">
          <a-popconfirm title="确认删除该预约吗？" @confirm="remove(record.id)">
            <a-button danger size="small" :loading="deletingId === record.id">
              <DeleteOutlined />
              删除
            </a-button>
          </a-popconfirm>
        </template>
      </a-table-column>
    </a-table>
  </a-card>
</template>
