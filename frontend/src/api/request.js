import axios from 'axios'
import { useUserStore } from '../store/user'

// 创建 axios 实例，使用环境变量配置
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 15000, // 延长超时时间到 15 秒，避免非本地网络延迟导致请求失败
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器：统一注入 Token
apiClient.interceptors.request.use(
  (config) => {
    // 从 Pinia store 获取 token
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers.Authorization = userStore.authHeader
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理响应状态
apiClient.interceptors.response.use(
  (response) => {
    const res = response.data
    // 根据后端约定的响应格式处理
    if (res.code !== 200) {
      console.error('业务逻辑错误:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res
  },
  (error) => {
    // 处理 401 未授权错误
    if (error.response?.status === 401) {
      const userStore = useUserStore()
      userStore.logout()
      console.warn('登录已过期，请重新登录')
      
      // 可以在这里跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else if (error.response?.status === 404) {
      console.error('请求的资源不存在')
    } else if (error.response?.status === 403) {
      console.error('没有访问权限')
    } else if (error.response?.status >= 500) {
      console.error('服务器错误，请稍后重试')
    } else if (error.message === 'Network Error') {
      console.error('网络连接失败，请检查网络')
    } else {
      const message = error.response?.data?.message || '请求失败'
      console.error(message)
    }
    
    return Promise.reject(error)
  }
)

export default apiClient
