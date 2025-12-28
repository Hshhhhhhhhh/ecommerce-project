import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import './style.css'
import App from './App.vue'
import { useUserStore } from './store/user'
import { useCartStore } from './store/cart'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 应用启动时恢复持久化状态
const userStore = useUserStore()
const cartStore = useCartStore()

// 恢复用户登录状态
userStore.restoreFromStorage()

// 购物车已在 state 初始化时自动恢复，这里可以添加额外的同步逻辑
console.log('应用状态已恢复:', {
  isLoggedIn: userStore.isLoggedIn,
  username: userStore.username,
  cartItems: cartStore.totalCount
})

app.mount('#app')