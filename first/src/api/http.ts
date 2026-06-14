import axios from 'axios'
import { message } from 'ant-design-vue'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload && payload.code !== 0) {
      message.error(payload.msg || '请求失败')
      return Promise.reject(payload)
    }
    return payload
  },
  (error) => {
    message.error(error?.response?.data?.msg || error.message || '网络异常')
    return Promise.reject(error)
  },
)

export default http
