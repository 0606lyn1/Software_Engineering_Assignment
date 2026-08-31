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
    const status = error?.response?.status
    const serverMsg = error?.response?.data?.msg
    let text = serverMsg || error.message || '网络异常'
    if (!serverMsg && (status === 401 || status === 403)) {
      // 后端对未认证和越权都直接返回空 body，只能靠状态码区分提示。
      text = localStorage.getItem('token') ? '登录已过期或没有操作权限，请重新登录' : '请先登录后再操作'
    }
    message.error(text)
    return Promise.reject(error)
  },
)

export default http
