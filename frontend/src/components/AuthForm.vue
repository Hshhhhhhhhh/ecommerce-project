<template>
  <div class="bg-white p-8 rounded-[40px] shadow-xl shadow-blue-50/50 border border-gray-100 w-full max-w-md">
    <!-- 切换标签 -->
    <div class="flex mb-8 bg-gray-50 p-1.5 rounded-2xl">
      <button 
        @click="isLogin = true" 
        :class="['flex-1 py-3 rounded-xl font-bold transition-all', isLogin ? 'bg-white shadow-sm text-blue-600' : 'text-gray-400']"
      >
        登录
      </button>
      <button 
        @click="isLogin = false" 
        :class="['flex-1 py-3 rounded-xl font-bold transition-all', !isLogin ? 'bg-white shadow-sm text-blue-600' : 'text-gray-400']"
      >
        注册
      </button>
    </div>

    <h2 class="text-2xl font-black text-gray-800 mb-2">{{ isLogin ? '欢迎回来' : '创建新账号' }}</h2>
    <p class="text-gray-400 text-sm mb-8">开启您的极简购物之旅</p>

    <form @submit.prevent="handleAuth" class="space-y-5">
      <!-- 错误信息提示 -->
      <div v-if="errorMessage" class="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-2xl text-sm">
        {{ errorMessage }}
      </div>
      
      <div class="space-y-1 animate-in fade-in slide-in-from-top-2">
        <label class="text-xs font-bold text-gray-400 ml-1 uppercase tracking-wider">{{ isLogin ? '账号' : '用户名' }}</label>
        <input v-model="form.username" type="text" :placeholder="isLogin ? '请输入用户名或邮箱' : '设置您的用户名'" class="w-full bg-gray-50 border-none rounded-2xl py-4 px-5 focus:ring-2 focus:ring-blue-500 outline-none" required />
      </div>
      
      <div v-if="!isLogin" class="space-y-1">
        <label class="text-xs font-bold text-gray-400 ml-1 uppercase tracking-wider">电子邮箱</label>
        <input v-model="form.email" type="email" placeholder="example@mail.com" class="w-full bg-gray-50 border-none rounded-2xl py-4 px-5 focus:ring-2 focus:ring-blue-500 outline-none" required />
      </div>

      <div class="space-y-1">
        <label class="text-xs font-bold text-gray-400 ml-1 uppercase tracking-wider">密码</label>
        <input v-model="form.password" type="password" placeholder="输入密码" class="w-full bg-gray-50 border-none rounded-2xl py-4 px-5 focus:ring-2 focus:ring-blue-500 outline-none" required />
      </div>

      <button 
        type="submit"
        :disabled="loading"
        class="w-full bg-blue-600 text-white py-4 rounded-2xl font-black text-lg shadow-lg shadow-blue-100 hover:bg-blue-700 active:scale-[0.98] transition-all mt-4 flex justify-center items-center gap-2"
      >
        <svg v-if="loading" class="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        {{ isLogin ? '立即登录' : '注册账号' }}
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { authService } from '../api/auth'
import { useUserStore } from '../store/user'
import { useCartStore } from '../store/cart'
import SHA256 from 'crypto-js/sha256'

const isLogin = ref(true)
const loading = ref(false)
const errorMessage = ref('')
const emit = defineEmits(['success'])
const userStore = useUserStore()
const cartStore = useCartStore()

const form = reactive({
  username: '',
  email: '',
  password: ''
})

// 使用 SHA-256 对密码进行哈希处理 (使用 crypto-js 以兼容非 HTTPS 环境)
const hashPassword = (password) => {
  return SHA256(password).toString()
}

const handleAuth = async () => {
  errorMessage.value = ''
  loading.value = true
  
  try {
    // 对密码进行哈希处理，避免明文传输
    const hashedPassword = hashPassword(form.password)

    if (isLogin.value) {
      // 登录流程
      const response = await authService.login(form.username, hashedPassword)
      
      // 获取返回的 token 和用户信息
      const { token, user, expiresIn } = response.data
      
      // 存储到 Pinia Store
      userStore.login(user, token, expiresIn)
      
      // 合并购物车
      await cartStore.mergeCart()
      
      console.log('登录成功:', user)
      emit('success', user)
    } else {
      // 注册流程
      const response = await authService.register(form.username, hashedPassword, form.email)
      
      // 注册成功后自动登录
      const { token, user, expiresIn } = response.data
      userStore.login(user, token, expiresIn)
      
      // 合并购物车
      await cartStore.mergeCart()
      
      console.log('注册成功:', user)
      emit('success', user)
    }
  } catch (error) {
    console.error(`${isLogin.value ? '登录' : '注册'}失败:`, error)
    errorMessage.value = error.response?.data?.message || error.message || `${isLogin.value ? '登录' : '注册'}失败，请稍后重试`
  } finally {
    loading.value = false
  }
}
</script>