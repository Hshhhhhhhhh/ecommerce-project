<template>
  <div class="min-h-screen bg-gray-50 flex flex-col font-sans text-gray-900">
    <!-- 全局导航栏 -->
    <Navbar />
    
    <!-- 页面切换区域 -->
    <main class="flex-grow">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部版权 -->
    <footer class="bg-white border-t border-gray-100 py-12 mt-12">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <div class="flex justify-center space-x-6 mb-4">
          <a href="#" class="text-xs font-bold text-gray-400 hover:text-blue-600 uppercase tracking-widest">关于我们</a>
          <a href="#" class="text-xs font-bold text-gray-400 hover:text-blue-600 uppercase tracking-widest">售后政策</a>
          <a href="#" class="text-xs font-bold text-gray-400 hover:text-blue-600 uppercase tracking-widest">联系客服</a>
        </div>
        <p class="text-gray-300 text-[10px] font-medium uppercase tracking-[0.2em]">© 2025 Minimalist Shop. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>

<style>
/* 页面切换动画 */
.page-fade-enter-active, .page-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.page-fade-enter-from { 
  opacity: 0; 
  transform: translateY(10px); 
}
.page-fade-leave-to { 
  opacity: 0; 
  transform: translateY(-10px); 
}

::selection {
  background-color: rgba(37, 99, 235, 0.1);
  color: #2563eb;
}

.scrollbar-hide::-webkit-scrollbar {
  display: none;
}
.scrollbar-hide {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>