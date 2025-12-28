<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50">
    <div class="bg-white p-8 rounded-3xl shadow-lg max-w-md w-full text-center">
      <div v-if="loading" class="py-8">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
        <p class="text-gray-500">正在确认收货...</p>
      </div>
      
      <div v-else-if="success" class="py-8">
        <div class="w-16 h-16 bg-green-100 text-green-500 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-800 mb-2">确认收货成功</h2>
        <p class="text-gray-500 mb-6">感谢您的购买！</p>
        <router-link to="/" class="inline-block bg-blue-600 text-white px-6 py-2 rounded-xl font-bold hover:bg-blue-700 transition-colors">
          返回首页
        </router-link>
      </div>
      
      <div v-else class="py-8">
        <div class="w-16 h-16 bg-red-100 text-red-500 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </div>
        <h2 class="text-2xl font-bold text-gray-800 mb-2">确认失败</h2>
        <p class="text-gray-500 mb-6">{{ errorMsg }}</p>
        <router-link to="/" class="inline-block bg-gray-200 text-gray-700 px-6 py-2 rounded-xl font-bold hover:bg-gray-300 transition-colors">
          返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '../api/request'

const route = useRoute()
const loading = ref(true)
const success = ref(false)
const errorMsg = ref('')

onMounted(async () => {
  const token = route.query.token
  if (!token) {
    loading.value = false
    errorMsg.value = '无效的确认链接'
    return
  }

  try {
    await request.post('/orders/confirm-by-email', null, {
      params: { token }
    })
    success.value = true
  } catch (error) {
    success.value = false
    errorMsg.value = error.response?.data?.message || '确认收货失败，链接可能已过期'
  } finally {
    loading.value = false
  }
})
</script>
