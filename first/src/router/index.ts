import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import VenueList from '../views/VenueList.vue'
import Reservation from '../views/Reservation.vue'
import MyReservations from '../views/MyReservations.vue'
import UserCenter from '../views/UserCenter.vue'
import VenueMaintenance from '../views/VenueMaintenance.vue'
import UserManagement from '../views/UserManagement.vue'
import OperationsAdmin from '../views/OperationsAdmin.vue'
import Notifications from '../views/Notifications.vue'

const normalizeRole = (role?: string) => {
  const normalized = role?.toUpperCase()
  return normalized === 'USER' ? 'STUDENT' : normalized || 'GUEST'
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/venues', component: VenueList },
    { path: '/reservation', component: Reservation, meta: { roles: ['ADMIN', 'TEACHER', 'STUDENT'] } },
    { path: '/my-reservations', component: MyReservations, meta: { roles: ['ADMIN', 'TEACHER', 'STUDENT'] } },
    { path: '/maintenance', component: VenueMaintenance, meta: { roles: ['ADMIN', 'STAFF'] } },
    { path: '/users', component: UserManagement, meta: { roles: ['ADMIN'] } },
    { path: '/operations-admin', component: OperationsAdmin, meta: { roles: ['ADMIN'] } },
    { path: '/notifications', component: Notifications },
    { path: '/user-center', component: UserCenter },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const publicPages = ['/login', '/register']

  if (token && to.path === '/login') {
    next('/')
    return
  }

  if (!publicPages.includes(to.path) && !token) {
    next('/login')
    return
  }

  const roles = to.meta.roles as string[] | undefined
  if (roles?.length) {
    const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user') as string) : null
    if (!roles.includes(normalizeRole(user?.role))) {
      next('/')
      return
    }
  }

  next()
})

export default router
