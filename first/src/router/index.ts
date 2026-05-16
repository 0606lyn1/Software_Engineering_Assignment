import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import VenueList from '../views/VenueList.vue'
import Reservation from '../views/Reservation.vue'
import MyReservations from '../views/MyReservations.vue'
import UserCenter from '../views/UserCenter.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/venues', component: VenueList },
    { path: '/reservation', component: Reservation },
    { path: '/my-reservations', component: MyReservations },
    { path: '/user-center', component: UserCenter },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const needAuth = ['/my-reservations', '/user-center', '/reservation']
  if (needAuth.includes(to.path) && !token) {
    next('/login')
    return
  }
  next()
})

export default router
