import { defineStore } from 'pinia'
import type { UserInfo } from '../types'

interface AuthState {
  token: string
  user: UserInfo | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token') || '',
    user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') as string) : null,
  }),
  getters: {
    normalizedRole: (state) => {
      const role = state.user?.role?.toUpperCase()
      return role === 'USER' ? 'STUDENT' : role || 'GUEST'
    },
    isAdmin(): boolean {
      return this.normalizedRole === 'ADMIN'
    },
    isStaff(): boolean {
      return this.normalizedRole === 'STAFF'
    },
    canManageMaintenance(): boolean {
      return this.isAdmin || this.isStaff
    },
    canReserve(): boolean {
      return ['ADMIN', 'TEACHER', 'STUDENT'].includes(this.normalizedRole)
    },
    roleLabel(): string {
      const labels: Record<string, string> = {
        ADMIN: '系统管理员',
        STAFF: '场地负责人',
        TEACHER: '教师',
        STUDENT: '学生',
        USER: '学生',
        GUEST: '访客',
      }
      return labels[this.normalizedRole] || this.normalizedRole
    },
  },
  actions: {
    setAuth(token: string, user: UserInfo) {
      this.token = token
      this.user = user
      localStorage.setItem('token', token)
      localStorage.setItem('user', JSON.stringify(user))
    },
    clearAuth() {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },
  },
})
