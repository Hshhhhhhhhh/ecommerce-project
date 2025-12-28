<template>
  <!-- 顶层容器：确保背景色和阴影，sticky 保持吸顶 -->
  <nav class="bg-white shadow-sm sticky top-0 z-50 border-b border-gray-100 w-full">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Flex 布局：确保 items-center 和 flex-row 强制横向 -->
      <div class="flex flex-row justify-between h-16 items-center">
        
        <!-- 左侧 Logo -->
        <router-link to="/" class="flex flex-row items-center cursor-pointer flex-shrink-0">
          <div class="bg-blue-600 p-1.5 rounded-lg mr-2 flex items-center justify-center w-9 h-9">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <span class="text-xl font-black tracking-tight text-gray-800 hidden sm:block">极简商城</span>
        </router-link>

        <!-- 中间搜索框 -->
        <div class="hidden md:flex flex-1 max-w-md mx-8">
          <div class="relative w-full">
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="搜索感兴趣的商品..." 
              @keyup.enter="handleSearch"
              class="w-full bg-gray-50 border border-gray-200 rounded-full py-1.5 px-10 focus:ring-2 focus:ring-blue-500 focus:bg-white outline-none transition-all text-sm"
            />
            <button 
              @click="handleSearch"
              class="absolute left-3 top-2.5 text-gray-400 hover:text-blue-600 transition-colors cursor-pointer"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 右侧菜单 -->
        <div class="flex flex-row items-center space-x-4 sm:space-x-8">
          <!-- 用户个人中心 -->
          <router-link 
            to="/profile"
            class="group text-gray-500 hover:text-blue-600 cursor-pointer flex flex-col items-center justify-center transition-colors w-12"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
            </svg>
            <span class="text-[10px] font-medium mt-0.5">我的</span>
            <!-- 管理员标识 -->
            <span v-if="userStore.isAdmin" class="absolute -top-1 -right-1 bg-red-500 text-white text-[8px] px-1 rounded">管理员</span>
          </router-link>
          
          <!-- 管理后台入口（仅管理员可见） -->
          <router-link 
            v-if="userStore.isAdmin"
            to="/admin"
            class="group text-gray-500 hover:text-red-600 cursor-pointer flex flex-col items-center justify-center transition-colors w-12"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <span class="text-[10px] font-medium mt-0.5 text-red-600">管理</span>
          </router-link>
          
          <!-- 购物车入口 -->
          <router-link to="/cart" class="group text-gray-500 hover:text-blue-600 cursor-pointer flex flex-col items-center justify-center relative transition-colors w-12">
            <svg xmlns="http://www.w3.org/2000/svg" class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <span class="text-[10px] font-medium mt-0.5">购物车</span>
            <span v-if="cartStore.totalCount > 0" class="absolute top-0 -right-1 bg-red-500 text-white text-[10px] font-bold rounded-full w-4 h-4 flex items-center justify-center shadow-sm">
              {{ cartStore.totalCount }}
            </span>
          </router-link>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cart'
import { useUserStore } from '../store/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()
const searchQuery = ref('')

// 处理搜索
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ 
      name: 'search', 
      query: { q: searchQuery.value.trim() } 
    })
  }
}
</script>