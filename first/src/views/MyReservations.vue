<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  CheckCircleOutlined,
  CloseCircleOutlined,
  ExclamationCircleOutlined,
  MailOutlined,
  QrcodeOutlined,
  ReloadOutlined,
} from '@ant-design/icons-vue'
import { api } from '../api'
import type { Reservation } from '../types'

const router = useRouter()
const data = ref<Reservation[]>([])
const loading = ref(false)
const workingId = ref<number | null>(null)
const appealOpen = ref(false)
const appealReason = ref('')
const appealTarget = ref<Reservation | null>(null)

const activeCount = computed(() => data.value.filter((item) => ['BOOKED', 'CHECKED_IN'].includes(item.status)).length)
const violationCount = computed(() => data.value.filter((item) => item.status === 'NO_SHOW').length)
const nextReservation = computed(() =>
  [...data.value]
    .filter((item) => item.status === 'BOOKED' && dayjs(item.startTime).isAfter(dayjs()))
    .sort((a, b) => dayjs(a.startTime).valueOf() - dayjs(b.startTime).valueOf())[0],
)

const statusMeta: Record<string, { color: string; text: string }> = {
  BOOKED: { color: 'green', text: '待使用' },
  CHECKED_IN: { color: 'blue', text: '已核销' },
  CANCELED: { color: 'red', text: '已取消' },
  CANCELLED: { color: 'red', text: '已取消' },
  EXPIRED: { color: 'default', text: '已过期' },
  NO_SHOW: { color: 'volcano', text: '爽约' },
}

const formatTime = (value?: string) => (value ? dayjs(value).format('YYYY-MM-DD HH:mm') : '-')
const canCancel = (item: Reservation) => item.status === 'BOOKED' && (!item.cancelDeadline || dayjs().isBefore(dayjs(item.cancelDeadline)))
const canAppeal = (item: Reservation) => item.status === 'NO_SHOW' && item.appealStatus !== 'PENDING'

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getMyReservations()
    data.value = res.data
  } finally {
    loading.value = false
  }
}

const cancel = async (item: Reservation) => {
  workingId.value = item.id
  try {
    await api.cancelReservation(item.id, '用户自助取消')
    message.success('预约已取消')
    await loadData()
  } finally {
    workingId.value = null
  }
}

const openAppeal = (item: Reservation) => {
  appealTarget.value = item
  appealReason.value = ''
  appealOpen.value = true
}

const submitAppeal = async () => {
  if (!appealTarget.value || !appealReason.value.trim()) {
    message.warning('请填写申诉原因')
    return
  }
  workingId.value = appealTarget.value.id
  try {
    await api.appealReservation(appealTarget.value.id, appealReason.value)
    message.success('申诉已提交')
    appealOpen.value = false
    await loadData()
  } finally {
    workingId.value = null
  }
}

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact">
    <div>
      <a-tag color="green">My Schedule</a-tag>
      <h1>我的预约</h1>
      <p>查看预约状态、核销码、取消截止时间和违约申诉记录。</p>
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
      <span>爽约记录</span>
      <strong>{{ violationCount }}</strong>
    </div>
    <div>
      <span>下一场</span>
      <strong>{{ nextReservation ? formatTime(nextReservation.startTime) : '暂无' }}</strong>
    </div>
  </section>

  <a-card class="table-card" :bordered="false">
    <a-table :data-source="data" :loading="loading" row-key="id" :pagination="{ pageSize: 6 }" :scroll="{ x: 1160 }">
      <template #emptyText>
        <a-empty description="暂无预约记录">
          <a-button type="primary" @click="router.push('/venues')">去选择场馆</a-button>
        </a-empty>
      </template>
      <a-table-column title="预约编号" data-index="id" width="100" />
      <a-table-column title="场馆 ID" data-index="venueId" width="90" />
      <a-table-column title="使用时间" width="230">
        <template #default="{ record }">
          {{ formatTime(record.startTime) }} - {{ dayjs(record.endTime).format('HH:mm') }}
        </template>
      </a-table-column>
      <a-table-column title="状态" data-index="status" width="120">
        <template #default="{ text }">
          <a-tag :color="statusMeta[text]?.color || 'default'">{{ statusMeta[text]?.text || text }}</a-tag>
        </template>
      </a-table-column>
      <a-table-column title="核销码" data-index="checkinCode" width="130">
        <template #default="{ text, record }">
          <span class="checkin-code" v-if="record.status === 'BOOKED'"><QrcodeOutlined /> {{ text }}</span>
          <span v-else>-</span>
        </template>
      </a-table-column>
      <a-table-column title="取消截止" data-index="cancelDeadline" width="170">
        <template #default="{ text }">{{ formatTime(text) }}</template>
      </a-table-column>
      <a-table-column title="邮件提醒" width="190">
        <template #default="{ record }">
          <a-space wrap v-if="record.reminderStatuses?.length">
            <a-tag
              v-for="status in record.reminderStatuses"
              :key="status"
              :color="status.includes('失败') ? 'red' : 'cyan'"
            >
              <MailOutlined /> {{ status }}
            </a-tag>
          </a-space>
          <span v-else>-</span>
        </template>
      </a-table-column>
      <a-table-column title="申诉" width="120">
        <template #default="{ record }">
          <a-tag v-if="record.appealStatus === 'PENDING'" color="gold">审核中</a-tag>
          <span v-else>{{ record.appealStatus === 'APPROVED' ? '已通过' : '-' }}</span>
        </template>
      </a-table-column>
      <a-table-column title="操作" width="180" fixed="right">
        <template #default="{ record }">
          <a-space>
            <a-popconfirm v-if="canCancel(record)" title="确认取消该预约吗？" @confirm="cancel(record)">
              <a-button danger size="small" :loading="workingId === record.id">
                <CloseCircleOutlined />
                取消
              </a-button>
            </a-popconfirm>
            <a-button v-if="canAppeal(record)" size="small" :loading="workingId === record.id" @click="openAppeal(record)">
              <ExclamationCircleOutlined />
              申诉
            </a-button>
            <a-tag v-if="record.status === 'CHECKED_IN'" color="blue"><CheckCircleOutlined /> 已入场</a-tag>
          </a-space>
        </template>
      </a-table-column>
    </a-table>
  </a-card>

  <a-modal v-model:open="appealOpen" title="异常申诉" ok-text="提交申诉" @ok="submitAppeal">
    <p>如因天气、设备故障或场馆临时关闭导致无法使用，请填写原因，管理员可据此处理违约记录。</p>
    <a-textarea v-model:value="appealReason" :rows="4" placeholder="请说明异常情况" />
  </a-modal>
</template>
