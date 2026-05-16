<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import { api } from '../api'
import type { Reservation } from '../types'

const data = ref<Reservation[]>([])
const loading = ref(false)

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
  await api.deleteReservation(id)
  message.success('\u9884\u7ea6\u5df2\u5220\u9664')
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <a-card class="academic-card" :title="'\u6211\u7684\u9884\u7ea6\u8bb0\u5f55'">
    <a-table :data-source="data" :loading="loading" row-key="id">
      <a-table-column :title="'\u9884\u7ea6\u7f16\u53f7'" data-index="id" width="100" />
      <a-table-column :title="'\u573a\u9986ID'" data-index="venueId" width="90" />
      <a-table-column :title="'\u5f00\u59cb\u65f6\u95f4'" data-index="startTime" />
      <a-table-column :title="'\u7ed3\u675f\u65f6\u95f4'" data-index="endTime" />
      <a-table-column :title="'\u72b6\u6001'" data-index="status" width="100" />
      <a-table-column :title="'\u64cd\u4f5c'" width="100">
        <template #default="{ record }">
          <a-popconfirm :title="'\u786e\u8ba4\u5220\u9664\u8be5\u9884\u7ea6\u5417\uff1f'" @confirm="remove(record.id)">
            <a-button danger size="small">&#21024;&#38500;</a-button>
          </a-popconfirm>
        </template>
      </a-table-column>
    </a-table>
  </a-card>
</template>