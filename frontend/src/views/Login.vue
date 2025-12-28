<template>
  <div class="min-h-[80vh] flex items-center justify-center px-4 py-12">
    <!-- 引入独立的 AuthForm 组件 -->
    <AuthForm @success="handleAuthSuccess" />
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import AuthForm from '../components/AuthForm.vue'

const router = useRouter()
const route = useRoute()

const handleAuthSuccess = (userData) => {
  console.log('登录成功，用户信息:', userData)
  
  // 获取登录前的目标路径，如果没有则跳转到首页
  const redirect = route.query.redirect || '/'
  router.push(redirect)
}
</script>