<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import {
  CheckCircleOutlined,
  ClockCircleOutlined,
  FieldTimeOutlined,
  QrcodeOutlined,
  SafetyCertificateOutlined,
  ToolOutlined,
  WarningOutlined,
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { api } from '../api'
import type { Reservation, Venue, VenueAvailability, VenueOps, VenueOpsPayload } from '../types'

const venues = ref<Venue[]>([])
const opsList = ref<VenueOps[]>([])
const availability = ref<VenueAvailability | null>(null)
const selectedVenueId = ref<number>()
const loading = ref(false)
const saving = ref(false)
const reservations = ref<Reservation[]>([])
const checkinCode = ref('')
const checking = ref(false)

const formState = reactive<VenueOpsPayload>({
  maintenanceStatus: 'NORMAL',
  cleaningStatus: 'CLEAN',
  lightingStatus: 'NORMAL',
  equipmentStatus: 'COMPLETE',
  responsiblePerson: '',
  contactPhone: '',
  remark: '',
})

const statusText: Record<string, string> = {
  NORMAL: '正常',
  MAINTENANCE: '维护中',
  CLOSED: '暂停开放',
  CLEAN: '已清洁',
  NEED_CLEANING: '待清洁',
  PENDING_RECHECK: '待复检',
  FAULT: '故障',
  DIM: '偏暗',
  COMPLETE: '完整',
  MISSING: '缺件',
  DAMAGED: '损坏',
  AVAILABLE: '可预约',
}

const selectedVenue = computed(() => venues.value.find((venue) => venue.id === selectedVenueId.value))
const opsMap = computed(() => new Map(opsList.value.map((item) => [item.venueId, item])))
const selectedOps = computed(() => (selectedVenueId.value ? opsMap.value.get(selectedVenueId.value) : undefined))
const blockedCount = computed(
  () =>
    opsList.value.filter(
      (item) =>
        ['MAINTENANCE', 'CLOSED'].includes(item.maintenanceStatus) ||
        ['NEED_CLEANING', 'PENDING_RECHECK'].includes(item.cleaningStatus) ||
        isCleaningExpired(item) ||
        item.lightingStatus === 'FAULT' ||
        ['MISSING', 'DAMAGED'].includes(item.equipmentStatus),
    ).length,
)
const cleanCount = computed(() => opsList.value.filter((item) => item.cleaningStatus === 'CLEAN').length)
const todayReservations = computed(() =>
  reservations.value.filter((item) => item.startTime?.slice(0, 10) === new Date().toISOString().slice(0, 10)),
)

const getStatusText = (value?: string) => (value ? statusText[value] || value : '未填报')
const isCleaningExpired = (item?: VenueOps) =>
  item?.cleaningStatus === 'CLEAN' && (!item.lastCheckedAt || Date.now() - new Date(item.lastCheckedAt).getTime() > 3 * 24 * 60 * 60 * 1000)
const dutyName = computed(() => selectedOps.value?.responsiblePerson || formState.responsiblePerson || '待分配')
const dutyPhone = computed(() => selectedOps.value?.contactPhone || formState.contactPhone || '暂无联系方式')
const latestCheckTime = computed(() => selectedOps.value?.lastCheckedAt?.replace('T', ' ').slice(0, 16) || '等待首次填报')
const getRiskColor = (item?: VenueOps) => {
  if (!item) return 'default'
  if (['MAINTENANCE', 'CLOSED'].includes(item.maintenanceStatus) || item.lightingStatus === 'FAULT') return 'red'
  if (isCleaningExpired(item)) return 'red'
  if (['NEED_CLEANING', 'PENDING_RECHECK'].includes(item.cleaningStatus) || ['MISSING', 'DAMAGED'].includes(item.equipmentStatus)) {
    return 'orange'
  }
  return 'green'
}

const fillForm = (ops: VenueOps) => {
  formState.maintenanceStatus = ops.maintenanceStatus
  formState.cleaningStatus = ops.cleaningStatus
  formState.lightingStatus = ops.lightingStatus
  formState.equipmentStatus = ops.equipmentStatus
  formState.responsiblePerson = ops.responsiblePerson || ''
  formState.contactPhone = ops.contactPhone || ''
  formState.remark = ops.remark || ''
}

const loadOpsForVenue = async (venueId: number) => {
  const opsRes = await api.getVenueOpsByVenue(venueId)
  const availabilityRes = await api.getVenueAvailability(venueId)
  fillForm(opsRes.data)
  availability.value = availabilityRes.data
}

const loadData = async () => {
  loading.value = true
  try {
    const [venueRes, opsRes, reservationRes] = await Promise.all([api.getVenues(), api.getVenueOps(), api.getAllReservations()])
    venues.value = venueRes.data
    opsList.value = opsRes.data
    reservations.value = reservationRes.data
    if (!selectedVenueId.value && venues.value.length) {
      selectedVenueId.value = venues.value[0].id
    }
    if (selectedVenueId.value) {
      await loadOpsForVenue(selectedVenueId.value)
    }
  } finally {
    loading.value = false
  }
}

const checkIn = async () => {
  if (!checkinCode.value.trim()) {
    message.warning('请输入核销码')
    return
  }
  checking.value = true
  try {
    await api.checkInReservation(checkinCode.value.trim())
    message.success('入场核销成功')
    checkinCode.value = ''
    await loadData()
  } finally {
    checking.value = false
  }
}

const submit = async () => {
  if (!selectedVenueId.value) return
  saving.value = true
  try {
    const res = await api.updateVenueOps(selectedVenueId.value, formState)
    const next = res.data as VenueOps
    const index = opsList.value.findIndex((item) => item.venueId === next.venueId)
    if (index >= 0) {
      opsList.value.splice(index, 1, next)
    } else {
      opsList.value.unshift(next)
    }
    await loadOpsForVenue(selectedVenueId.value)
    message.success('场地维护状态已保存')
  } finally {
    saving.value = false
  }
}

watch(selectedVenueId, async (venueId) => {
  if (venueId) {
    await loadOpsForVenue(venueId)
  }
})

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact ops-hero">
    <div>
      <a-tag color="cyan">Subsystem 2</a-tag>
      <h1>场地维护人员填报</h1>
      <p>维护人员可以录入清洁、灯光、器材和闭馆状态，系统会同步影响场地可约性，形成子系统二的运维闭环。</p>
    </div>
    <a-button type="primary" size="large" :loading="loading" @click="loadData">
      <FieldTimeOutlined />
      刷新台账
    </a-button>
  </section>

  <section class="ops-summary">
    <div>
      <SafetyCertificateOutlined />
      <span>纳入运维</span>
      <strong>{{ opsList.length }}</strong>
    </div>
    <div>
      <CheckCircleOutlined />
      <span>清洁达标</span>
      <strong>{{ cleanCount }}</strong>
    </div>
    <div>
      <WarningOutlined />
      <span>暂不可约</span>
      <strong>{{ blockedCount }}</strong>
    </div>
  </section>

  <section class="checkin-console">
    <a-card class="checkin-card" :bordered="false">
      <div>
        <a-tag color="blue">Check-in Desk</a-tag>
        <h2>入场核销</h2>
        <p>学生或教师到场后出示“我的预约”中的核销码，维护人员输入核销码完成入场确认。</p>
      </div>
      <a-input-search
        v-model:value="checkinCode"
        size="large"
        placeholder="输入 6 位核销码"
        enter-button="核销入场"
        :loading="checking"
        @search="checkIn"
      >
        <template #prefix><QrcodeOutlined /></template>
      </a-input-search>
    </a-card>

    <a-card class="checkin-card compact" :bordered="false">
      <div class="section-heading">
        <span>今日场次</span>
        <strong>{{ todayReservations.length }} 条</strong>
      </div>
      <div class="today-reservation-list">
        <article v-for="item in todayReservations.slice(0, 5)" :key="item.id">
          <strong>#{{ item.id }} 场馆 {{ item.venueId }}</strong>
          <span>{{ item.startTime?.replace('T', ' ').slice(11, 16) }} - {{ item.endTime?.replace('T', ' ').slice(11, 16) }}</span>
          <a-tag :color="item.status === 'CHECKED_IN' ? 'blue' : 'green'">{{ item.status }}</a-tag>
        </article>
        <a-empty v-if="!todayReservations.length" description="今日暂无预约" />
      </div>
    </a-card>
  </section>

  <section class="ops-duty-board">
    <div class="ops-duty-primary">
      <div>
        <a-tag :color="availability?.available ? 'green' : 'red'">当前场地</a-tag>
        <h2>{{ selectedVenue?.name || '请选择场馆' }}</h2>
        <p>{{ availability?.reason || '维护人员选择场馆后，系统会给出开放判定与巡检重点。' }}</p>
      </div>
      <div class="ops-duty-status" :class="{ blocked: availability && !availability.available }">
        <span>{{ availability?.available ? '可开放' : '需处理' }}</span>
        <strong>{{ availability?.available ? 'PASS' : 'HOLD' }}</strong>
      </div>
    </div>

    <div class="ops-duty-card">
      <span>当班负责人</span>
      <strong>{{ dutyName }}</strong>
      <small>{{ dutyPhone }}</small>
    </div>
    <div class="ops-duty-card">
      <span>最近巡检</span>
      <strong>{{ latestCheckTime }}</strong>
      <small>填报人：{{ selectedOps?.lastInspector || '暂无' }}</small>
    </div>
    <div class="ops-duty-checklist">
      <span>巡检清单</span>
      <div>
        <a-tag :color="getRiskColor(selectedOps)">清洁 {{ getStatusText(selectedOps?.cleaningStatus) }}</a-tag>
        <a-tag v-if="isCleaningExpired(selectedOps)" color="red">超过 3 天未清扫</a-tag>
        <a-tag :color="getRiskColor(selectedOps)">灯光 {{ getStatusText(selectedOps?.lightingStatus) }}</a-tag>
        <a-tag :color="getRiskColor(selectedOps)">器材 {{ getStatusText(selectedOps?.equipmentStatus) }}</a-tag>
      </div>
    </div>
  </section>

  <a-spin :spinning="loading">
    <section class="ops-workbench">
      <aside class="ops-venue-panel">
        <div class="section-heading">
          <span>场馆列表</span>
          <strong>{{ venues.length }} 个</strong>
        </div>
        <a-empty v-if="!venues.length" description="等待后端返回场馆数据" class="ops-empty" />
        <button
          v-for="venue in venues"
          :key="venue.id"
          class="ops-venue-item"
          :class="{ active: venue.id === selectedVenueId }"
          type="button"
          @click="selectedVenueId = venue.id"
        >
          <span>
            <strong>{{ venue.name }}</strong>
            <small>{{ venue.notes || '暂无开放说明' }}</small>
          </span>
          <a-tag :color="getRiskColor(opsMap.get(venue.id))">
            {{ getStatusText(opsMap.get(venue.id)?.maintenanceStatus) }}
          </a-tag>
        </button>
      </aside>

      <a-card class="ops-form-card" :bordered="false">
        <div class="ops-form-head">
          <div>
            <a-tag :color="availability?.available ? 'green' : 'red'">
              {{ availability?.available ? '当前可预约' : '当前不可预约' }}
            </a-tag>
            <h2>{{ selectedVenue?.name || '请选择场馆' }}</h2>
            <p>{{ availability?.reason || '选择场馆后查看实时运维判断' }}</p>
          </div>
          <ToolOutlined />
        </div>

        <a-form layout="vertical" :model="formState" @finish="submit">
          <div class="ops-form-grid">
            <a-form-item label="维护状态" name="maintenanceStatus" required>
              <a-select v-model:value="formState.maintenanceStatus">
                <a-select-option value="NORMAL">正常开放</a-select-option>
                <a-select-option value="MAINTENANCE">维护中</a-select-option>
                <a-select-option value="CLOSED">暂停开放</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="清洁状态" name="cleaningStatus" required>
              <a-select v-model:value="formState.cleaningStatus">
                <a-select-option value="CLEAN">已清洁</a-select-option>
                <a-select-option value="NEED_CLEANING">待清洁</a-select-option>
                <a-select-option value="PENDING_RECHECK">待复检</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="灯光状态" name="lightingStatus" required>
              <a-select v-model:value="formState.lightingStatus">
                <a-select-option value="NORMAL">正常</a-select-option>
                <a-select-option value="DIM">偏暗</a-select-option>
                <a-select-option value="FAULT">故障</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="器材状态" name="equipmentStatus" required>
              <a-select v-model:value="formState.equipmentStatus">
                <a-select-option value="COMPLETE">完整</a-select-option>
                <a-select-option value="MISSING">缺件</a-select-option>
                <a-select-option value="DAMAGED">损坏</a-select-option>
              </a-select>
            </a-form-item>

            <a-form-item label="负责人">
              <a-input v-model:value="formState.responsiblePerson" placeholder="例：李老师" />
            </a-form-item>

            <a-form-item label="联系电话">
              <a-input v-model:value="formState.contactPhone" placeholder="例：13800000000" />
            </a-form-item>
          </div>

          <a-form-item label="巡检备注">
            <a-textarea v-model:value="formState.remark" :rows="4" placeholder="记录现场问题、处理建议或复检说明" />
          </a-form-item>

          <div class="ops-actions">
            <span><ClockCircleOutlined /> 最近填报人：{{ opsMap.get(selectedVenueId || 0)?.lastInspector || '暂无' }}</span>
            <a-button type="primary" html-type="submit" size="large" :loading="saving">保存填报</a-button>
          </div>
        </a-form>
      </a-card>
    </section>

    <a-card class="ops-table-card" :bordered="false">
      <div class="section-heading">
        <span>运维状态台账</span>
        <strong>公开状态供预约与场馆目录消费</strong>
      </div>
      <a-empty v-if="!opsList.length" description="暂无运维台账，启动后端并补充 t_venue_ops 后显示记录" class="ops-empty wide" />
      <div v-else class="ops-log-grid">
        <article v-for="item in opsList" :key="item.id" class="ops-log-item">
          <div>
            <a-tag :color="getRiskColor(item)">{{ getStatusText(item.maintenanceStatus) }}</a-tag>
            <h3>{{ venues.find((venue) => venue.id === item.venueId)?.name || `场馆 ${item.venueId}` }}</h3>
          </div>
          <p>{{ item.remark || '暂无巡检备注' }}</p>
          <div class="ops-log-meta">
            <span>清洁：{{ getStatusText(item.cleaningStatus) }}</span>
            <span>灯光：{{ getStatusText(item.lightingStatus) }}</span>
            <span>器材：{{ getStatusText(item.equipmentStatus) }}</span>
          </div>
        </article>
      </div>
    </a-card>
  </a-spin>
</template>
