<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  DeleteOutlined,
  PlusOutlined,
  ReloadOutlined,
  SafetyCertificateOutlined,
  TeamOutlined,
  UserAddOutlined,
} from '@ant-design/icons-vue'
import { api } from '../api'
import type { UserInfo } from '../types'

const users = ref<UserInfo[]>([])
const loading = ref(false)
const creating = ref(false)
const activeRole = ref<string>('ALL')

const form = reactive({
  username: '',
  password: '',
  email: '',
  role: 'STUDENT',
})

const roleOptions = [
  { value: 'STUDENT', label: '学生', desc: '可预约场馆、查看个人预约' },
  { value: 'TEACHER', label: '教师', desc: '可预约场馆、查看个人预约' },
  { value: 'STAFF', label: '场地负责人', desc: '可维护场地状态与运维台账' },
  { value: 'ADMIN', label: '系统管理员', desc: '可管理人员、场地维护与平台数据' },
]

const roleLabel = (role?: string) => {
  const normalized = role === 'USER' ? 'STUDENT' : role || 'STUDENT'
  return roleOptions.find((item) => item.value === normalized)?.label || normalized
}

const roleColor = (role?: string) => {
  const normalized = role === 'USER' ? 'STUDENT' : role
  if (normalized === 'ADMIN') return 'red'
  if (normalized === 'STAFF') return 'cyan'
  if (normalized === 'TEACHER') return 'blue'
  return 'green'
}

const roleCounts = computed(() =>
  roleOptions.map((role) => ({
    ...role,
    count: users.value.filter((user) => (user.role === 'USER' ? 'STUDENT' : user.role) === role.value).length,
  })),
)

const filteredUsers = computed(() => {
  if (activeRole.value === 'ALL') return users.value
  return users.value.filter((user) => (user.role === 'USER' ? 'STUDENT' : user.role) === activeRole.value)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await api.getUsers()
    users.value = res.data
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.username = ''
  form.password = ''
  form.email = ''
  form.role = 'STUDENT'
}

const createUser = async () => {
  creating.value = true
  try {
    await api.createUser({ ...form })
    message.success('人员已添加')
    resetForm()
    await loadData()
  } finally {
    creating.value = false
  }
}

const removeUser = async (id: number) => {
  await api.deleteUser(id)
  message.success('人员已删除')
  await loadData()
}

onMounted(loadData)
</script>

<template>
  <section class="page-hero compact user-admin-hero">
    <div>
      <a-tag color="red">Admin Console</a-tag>
      <h1>人员管理</h1>
      <p>管理员可以添加学生、教师、场地负责人或系统管理员。普通注册入口默认创建学生账号。</p>
    </div>
    <a-button size="large" type="primary" :loading="loading" @click="loadData">
      <ReloadOutlined />
      刷新人员
    </a-button>
  </section>

  <section class="user-role-strip">
    <button class="role-stat" :class="{ active: activeRole === 'ALL' }" type="button" @click="activeRole = 'ALL'">
      <TeamOutlined />
      <span>全部人员</span>
      <strong>{{ users.length }}</strong>
    </button>
    <button
      v-for="role in roleCounts"
      :key="role.value"
      class="role-stat"
      :class="{ active: activeRole === role.value }"
      type="button"
      @click="activeRole = role.value"
    >
      <SafetyCertificateOutlined />
      <span>{{ role.label }}</span>
      <strong>{{ role.count }}</strong>
    </button>
  </section>

  <div class="user-admin-layout">
    <a-card class="user-create-card" :bordered="false">
      <div class="section-heading">
        <span><UserAddOutlined /> 添加人员</span>
        <strong>身份必填</strong>
      </div>
      <a-form layout="vertical" :model="form" @finish="createUser">
        <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" placeholder="例如：teacher01" />
        </a-form-item>
        <a-form-item label="邮箱" name="email" :rules="[{ required: true, type: 'email', message: '请输入正确邮箱' }]">
          <a-input v-model:value="form.email" placeholder="例如：teacher01@example.com" />
        </a-form-item>
        <a-form-item label="初始密码" name="password" :rules="[{ required: true, min: 6, message: '密码至少 6 位' }]">
          <a-input-password v-model:value="form.password" placeholder="请输入初始密码" />
        </a-form-item>
        <a-form-item label="人员身份" name="role" :rules="[{ required: true, message: '请选择身份' }]">
          <a-radio-group v-model:value="form.role" class="role-radio-list">
            <a-radio-button v-for="role in roleOptions" :key="role.value" :value="role.value">
              {{ role.label }}
            </a-radio-button>
          </a-radio-group>
        </a-form-item>
        <p class="role-helper">
          {{ roleOptions.find((item) => item.value === form.role)?.desc }}
        </p>
        <a-button type="primary" html-type="submit" size="large" block :loading="creating">
          <PlusOutlined />
          添加人员
        </a-button>
      </a-form>
    </a-card>

    <a-card class="user-table-card desktop-table-card" :bordered="false">
      <div class="section-heading">
        <span>人员列表</span>
        <strong>{{ filteredUsers.length }} 条记录</strong>
      </div>
      <a-table
        :data-source="filteredUsers"
        :loading="loading"
        row-key="id"
        :pagination="{ pageSize: 8 }"
        :scroll="{ x: 760 }"
      >
        <a-table-column title="ID" data-index="id" width="80" />
        <a-table-column title="用户名" data-index="username" width="160" />
        <a-table-column title="邮箱" data-index="email" width="220" />
        <a-table-column title="身份" data-index="role" width="150">
          <template #default="{ text }">
            <a-tag :color="roleColor(text)">{{ roleLabel(text) }}</a-tag>
          </template>
        </a-table-column>
        <a-table-column title="创建时间" data-index="createdAt" width="190" />
        <a-table-column title="操作" width="120" fixed="right">
          <template #default="{ record }">
            <a-popconfirm title="确认删除该人员吗？" @confirm="removeUser(record.id)">
              <a-button danger size="small" :disabled="record.username === 'admin'">
                <DeleteOutlined />
                删除
              </a-button>
            </a-popconfirm>
          </template>
        </a-table-column>
      </a-table>
    </a-card>

    <section class="mobile-card-list user-mobile-list">
      <a-spin :spinning="loading">
        <a-empty v-if="!filteredUsers.length" description="暂无人员" />
        <article v-for="user in filteredUsers" :key="user.id" class="mobile-record-card">
          <div class="mobile-record-head">
            <div>
              <span>ID {{ user.id }}</span>
              <strong>{{ user.username }}</strong>
            </div>
            <a-tag :color="roleColor(user.role)">{{ roleLabel(user.role) }}</a-tag>
          </div>
          <div class="mobile-record-grid">
            <p><span>邮箱</span><strong>{{ user.email }}</strong></p>
            <p><span>创建时间</span><strong>{{ user.createdAt }}</strong></p>
          </div>
          <div class="mobile-record-actions">
            <a-popconfirm title="确认删除该人员吗？" @confirm="removeUser(user.id)">
              <a-button danger block :disabled="user.username === 'admin'">
                <DeleteOutlined />
                删除
              </a-button>
            </a-popconfirm>
          </div>
        </article>
      </a-spin>
    </section>
  </div>
</template>
