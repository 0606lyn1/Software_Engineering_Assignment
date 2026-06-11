<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  CalendarOutlined,
  CommentOutlined,
  FieldTimeOutlined,
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons-vue'
import { useVenueStore } from '../stores/venue'
import { api } from '../api'
import CommentList from './CommentList.vue'
import type { VenueType } from '../types'

const venueStore = useVenueStore()
const router = useRouter()
const types = ref<VenueType[]>([])
const loading = ref(false)
const filter = reactive({ typeId: undefined as number | undefined, keyword: '' })
const open = ref(false)
const selectedVenueId = ref(0)
const selectedVenueName = ref('')

const typeMap = computed(() => new Map(types.value.map((item) => [item.id, item.name])))
const filteredVenues = computed(() => {
  const keyword = filter.keyword.trim().toLowerCase()
  if (!keyword) return venueStore.venues
  return venueStore.venues.filter((venue) =>
    [venue.name, venue.description, venue.notes].some((text) => text?.toLowerCase().includes(keyword)),
  )
})
const averagePrice = computed(() => {
  if (!filteredVenues.value.length) return 0
  const total = filteredVenues.value.reduce((sum, venue) => sum + Number(venue.price || 0), 0)
  return Math.round(total / filteredVenues.value.length)
})

const loadData = async () => {
  loading.value = true
  try {
    await venueStore.fetchVenues(filter.typeId)
    const typeRes = await api.getVenueTypes()
    types.value = typeRes.data
  } finally {
    loading.value = false
  }
}

const openComments = (venueId: number, name: string) => {
  selectedVenueId.value = venueId
  selectedVenueName.value = name
  open.value = true
}

const reserveVenue = (venueId: number) => {
  router.push({ path: '/reservation', query: { venueId } })
}

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact">
    <div>
      <a-tag color="red">Venue Directory</a-tag>
      <h1>场馆目录</h1>
      <p>按项目筛选、查看价格与说明，并在同一页面打开真实用户评论。</p>
    </div>
    <a-button type="primary" size="large" @click="router.push('/reservation')">
      <CalendarOutlined />
      前往预约
    </a-button>
  </section>

  <section class="summary-strip">
    <div>
      <span>当前场馆</span>
      <strong>{{ filteredVenues.length }}</strong>
    </div>
    <div>
      <span>场馆类型</span>
      <strong>{{ types.length }}</strong>
    </div>
    <div>
      <span>均价</span>
      <strong>¥{{ averagePrice }}/小时</strong>
    </div>
  </section>

  <a-card class="tool-card" :bordered="false">
    <a-space wrap class="toolbar">
      <a-input
        v-model:value="filter.keyword"
        allow-clear
        class="search-input"
        placeholder="搜索场馆名称、说明或备注"
      >
        <template #prefix><SearchOutlined /></template>
      </a-input>
      <a-select
        v-model:value="filter.typeId"
        allow-clear
        class="type-select"
        placeholder="按场馆类型筛选"
        @change="loadData"
      >
        <a-select-option v-for="item in types" :key="item.id" :value="item.id">
          {{ item.name }}
        </a-select-option>
      </a-select>
      <a-button @click="loadData"><ReloadOutlined /> 刷新</a-button>
    </a-space>
  </a-card>

  <a-spin :spinning="loading">
    <a-empty v-if="!filteredVenues.length" description="暂无匹配场馆" class="empty-state" />
    <div v-else class="venue-grid">
      <a-card v-for="venue in filteredVenues" :key="venue.id" class="venue-card" :bordered="false">
        <div class="venue-card-head">
          <div>
            <a-tag color="volcano">{{ typeMap.get(venue.typeId) || `类型 ${venue.typeId}` }}</a-tag>
            <h3>{{ venue.name }}</h3>
          </div>
          <div class="venue-price">¥{{ venue.price }}<span>/小时</span></div>
        </div>
        <p class="venue-desc">{{ venue.description || '暂无场馆说明' }}</p>
        <div class="venue-note">
          <FieldTimeOutlined />
          <span>{{ venue.notes || '开放时段以预约页可选时间为准' }}</span>
        </div>
        <div class="venue-actions">
          <a-button type="primary" @click="reserveVenue(venue.id)">
            <CalendarOutlined />
            预约此场馆
          </a-button>
          <a-button @click="openComments(venue.id, venue.name)">
            <CommentOutlined />
            评论
          </a-button>
        </div>
      </a-card>
    </div>
  </a-spin>

  <a-drawer v-model:open="open" :title="`${selectedVenueName} 的场馆评论`" width="560">
    <CommentList :venue-id="selectedVenueId" />
  </a-drawer>
</template>
