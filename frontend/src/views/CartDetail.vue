<template>
  <div class="max-w-4xl mx-auto py-12 px-4">
    <h2 class="text-3xl font-black text-gray-800 mb-8 flex items-center gap-3">
      <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
      </svg>
      我的购物车
    </h2>

    <div v-if="cartStore.items.length === 0" class="text-center py-20 bg-white rounded-[40px] shadow-sm border border-gray-50">
      <div class="bg-gray-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
        </svg>
      </div>
      <p class="text-gray-400 text-lg font-medium">购物车竟然是空的</p>
      <button 
        @click="router.push('/')"
        class="mt-6 text-blue-600 font-bold hover:underline underline-offset-4"
      >
        去商场逛逛 →
      </button>
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <!-- 商品列表 -->
      <div class="lg:col-span-2 space-y-4">
        <div 
          v-for="item in cartStore.items" 
          :key="item.id" 
          class="bg-white p-5 rounded-3xl shadow-sm border border-gray-50 flex items-center gap-6 group hover:shadow-md transition-shadow"
        >
          <img :src="getImageUrl(item)" class="w-24 h-24 rounded-2xl object-cover shadow-sm" />
          <div class="flex-1">
            <h4 class="font-bold text-gray-800 text-lg group-hover:text-blue-600 transition-colors">{{ item.name }}</h4>
            <p class="text-gray-400 text-sm mt-1">单价：￥{{ item.price }}</p>
            <div class="flex items-center mt-3 space-x-3">
              <button @click="item.quantity > 1 ? item.quantity-- : null" class="w-8 h-8 rounded-full border border-gray-100 flex items-center justify-center hover:bg-gray-50 active:scale-90">-</button>
              <span class="font-bold w-4 text-center">{{ item.quantity }}</span>
              <button 
                @click="increaseQuantity(item)" 
                class="w-8 h-8 rounded-full border border-gray-100 flex items-center justify-center hover:bg-gray-50 active:scale-90 disabled:opacity-50 disabled:cursor-not-allowed"
                :disabled="item.stock !== undefined && item.quantity >= item.stock"
              >+</button>
            </div>
            <p v-if="item.stock !== undefined && item.quantity >= item.stock" class="text-xs text-red-500 mt-1">库存不足</p>
          </div>
          <div class="text-right">
            <p class="text-xl font-black text-gray-800">￥{{ (item.price * item.quantity).toLocaleString() }}</p>
            <button 
              @click="cartStore.removeFromCart(item.id)" 
              class="mt-2 text-red-400 hover:text-red-500 p-2"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        </div>
      </div>

      <!-- 结算汇总 -->
      <div class="lg:col-span-1">
        <div class="bg-white p-8 rounded-[40px] shadow-xl shadow-gray-100 border border-gray-50 sticky top-24">
          <h3 class="font-bold text-xl mb-6 text-gray-800">订单摘要</h3>
          <div class="space-y-4 mb-8">
            <div class="flex justify-between text-gray-500">
              <span>商品数量</span>
              <span class="font-bold text-gray-800">{{ cartStore.totalCount }} 件</span>
            </div>
            <div class="flex justify-between text-gray-500">
              <span>运费</span>
              <span class="text-green-500 font-bold">免运费</span>
            </div>
            <div class="h-px bg-gray-50 my-4"></div>
            <div class="flex justify-between items-center">
              <span class="font-bold text-gray-800">总计金额</span>
              <span class="text-3xl font-black text-red-500">￥{{ cartStore.totalPrice.toLocaleString() }}</span>
            </div>
          </div>
          <!-- 关键点：添加了 @click="router.push('/checkout')" 触发跳转 -->
          <button 
            @click="router.push('/checkout')"
            class="w-full bg-blue-600 text-white py-5 rounded-2xl font-black text-lg shadow-xl shadow-blue-100 hover:bg-blue-700 active:scale-[0.98] transition-all"
          >
            去下单
          </button>
          <p class="text-center text-gray-400 text-xs mt-4">收货地址等信息将在下一页确认</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cart'

const router = useRouter()
const cartStore = useCartStore()

const increaseQuantity = (item) => {
  if (item.stock !== undefined && item.quantity >= item.stock) {
    alert(`库存不足，仅剩 ${item.stock} 件`)
    return
  }
  item.quantity++
}

const getImageUrl = (item) => {
  const url = item.imageUrl || item.image || item.img
  if (!url) return ''
  // 如果是 localhost:8080 的绝对路径，转换为相对路径以利用代理
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}
</script>