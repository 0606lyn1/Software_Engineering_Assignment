<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useVenueStore } from '../stores/venue'
import { api } from '../api'
import CommentList from './CommentList.vue'

const venueStore = useVenueStore()
const router = useRouter()
const types = ref<{ id: number; name: string }[]>([])
const filter = reactive({ typeId: undefined as number | undefined })
const open = ref(false)
const selectedVenueId = ref(0)

const loadData = async () => {
  await venueStore.fetchVenues(filter.typeId)
  const typeRes = await api.getVenueTypes()
  types.value = typeRes.data
}

const openComments = (venueId: number) => {
  selectedVenueId.value = venueId
  open.value = true
}

onMounted(loadData)
</script>

<template>
  <a-card class="academic-card" :title="'\u573a\u9986\u76ee\u5f55'">
    <a-space style="margin-bottom: 16px; width: 100%; justify-content: space-between; display:flex;">
      <a-space>
        <a-select v-model:value="filter.typeId" allow-clear style="width: 190px" :placeholder="'\u6309\u573a\u9986\u7c7b\u578b\u7b5b\u9009'" @change="loadData">
          <a-select-option v-for="item in types" :key="item.id" :value="item.id">{{ item.name }}</a-select-option>
        </a-select>
        <a-button @click="loadData">&#21047;&#26032;</a-button>
      </a-space>
      <a-button type="primary" @click="router.push('/reservation')">&#21069;&#24448;&#39044;&#32422;</a-button>
    </a-space>

    <a-table :data-source="venueStore.venues" row-key="id" :pagination="{ pageSize: 6 }">
      <a-table-column :title="'\u573a\u9986\u540d\u79f0'" data-index="name" />
      <a-table-column :title="'\u7c7b\u578bID'" data-index="typeId" width="90" />
      <a-table-column :title="'\u4ef7\u683c\uff08\u5143/\u5c0f\u65f6\uff09'" data-index="price" width="140" />
      <a-table-column :title="'\u573a\u9986\u8bf4\u660e'" data-index="description" />
      <a-table-column :title="'\u5907\u6ce8'" data-index="notes" />
      <a-table-column :title="'\u64cd\u4f5c'" width="110">
        <template #default="{ record }"><a-button size="small" @click="openComments(record.id)">&#26597;&#30475;&#35780;&#35770;</a-button></template>
      </a-table-column>
    </a-table>
  </a-card>

  <a-drawer v-model:open="open" :title="'\u573a\u9986\u8bc4\u8bba'" width="520">
    <CommentList :venue-id="selectedVenueId" />
  </a-drawer>
</template>