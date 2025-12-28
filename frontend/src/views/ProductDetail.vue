<template>
  <div class="max-w-6xl mx-auto py-12 px-4">
    <!-- 返回按钮 -->
    <button 
      @click="router.push('/')" 
      class="group flex items-center gap-2 text-gray-400 hover:text-blue-600 transition-colors mb-8"
    >
      <div class="w-8 h-8 rounded-full border border-gray-200 flex items-center justify-center group-hover:border-blue-200 group-hover:bg-blue-50">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
        </svg>
      </div>
      <span class="font-bold text-sm">返回商城</span>
    </button>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-start">
      <!-- 商品大图 -->
      <div class="relative group">
        <div class="absolute -inset-4 bg-gradient-to-tr from-blue-50 to-transparent rounded-[40px] -z-10 scale-95 opacity-0 group-hover:opacity-100 group-hover:scale-100 transition-all duration-700"></div>
        <img 
          :src="getImageUrl(product)" 
          class="w-full rounded-[40px] shadow-2xl shadow-gray-200 object-cover aspect-square"
          :class="{ 'grayscale opacity-70': product.stock <= 0 }"
          alt="Product Image"
        />
        <!-- 已售完遮罩 -->
        <div v-if="product.stock <= 0" class="absolute inset-0 flex items-center justify-center bg-black/30 z-10 rounded-[40px]">
          <span class="bg-black/70 text-white px-6 py-3 rounded-2xl text-2xl font-bold">已售完</span>
        </div>
      </div>

      <!-- 商品信息 -->
      <div class="space-y-8">
        <div>
          <span class="inline-block px-4 py-1.5 bg-blue-50 text-blue-600 rounded-full text-xs font-black uppercase tracking-widest mb-4">
            {{ product.categoryName || product.category }}
          </span>
          <h1 class="text-4xl lg:text-5xl font-black text-gray-800 leading-tight">
            {{ product.name }}
          </h1>
          <div class="flex items-end gap-4 mt-6">
            <p class="text-3xl font-black text-red-500">
              ￥{{ product.price?.toLocaleString() }}
            </p>
            <p class="text-gray-500 font-bold mb-1">
              剩余库存: {{ product.stock }}
            </p>
          </div>
        </div>

        <div class="bg-white p-6 rounded-3xl border border-gray-50 shadow-sm">
          <h3 class="text-sm font-bold text-gray-400 uppercase tracking-widest mb-3">商品描述</h3>
          <p class="text-gray-600 leading-relaxed italic">
            "{{ product.description || '这是一款精心挑选的极简设计单品，旨在为您的生活带来质感与便利。每一处细节都经过反复推敲，确保美学与功能的完美统一。' }}"
          </p>
        </div>



        <!-- 操作按钮 -->
        <div class="flex gap-4 pt-4">
          <button 
            @click="handleAddToCart"
            :disabled="product.stock <= 0"
            class="flex-[2] bg-blue-600 text-white py-5 rounded-2xl font-black text-lg shadow-xl shadow-blue-100 hover:bg-blue-700 active:scale-[0.98] transition-all disabled:bg-gray-300 disabled:cursor-not-allowed disabled:shadow-none"
          >
            {{ product.stock <= 0 ? '已售完' : '加入购物车' }}
          </button>
          <button 
            @click="handleQuickBuy"
            :disabled="product.stock <= 0"
            class="flex-1 bg-gray-900 text-white py-5 rounded-2xl font-black text-lg hover:bg-black active:scale-[0.98] transition-all disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            立即购买
          </button>
        </div>

        <!-- 服务保障 -->
        <div class="grid grid-cols-3 gap-4 pt-6">
          <div class="text-center">
            <div class="text-blue-500 mb-1 flex justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" /></svg>
            </div>
            <span class="text-[10px] font-bold text-gray-400 uppercase">正品保障</span>
          </div>
          <div class="text-center border-x border-gray-100">
            <div class="text-blue-500 mb-1 flex justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" /></svg>
            </div>
            <span class="text-[10px] font-bold text-gray-400 uppercase">7天无忧退换</span>
          </div>
          <div class="text-center">
            <div class="text-blue-500 mb-1 flex justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
            </div>
            <span class="text-[10px] font-bold text-gray-400 uppercase">顺丰包邮</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '../store/cart'
import { productService } from '../api/product'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const product = ref({})
const loading = ref(false)
const error = ref('')

const getImageUrl = (p) => {
  const url = p.imageUrl || p.image || p.img
  if (!url) return ''
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}

// 获取商品详情
const fetchProductDetail = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await productService.getProductDetail(route.params.id)
    product.value = response.data || {}
    console.log('商品详情加载成功:', product.value)
  } catch (err) {
    console.error('获取商品详情失败:', err)
    error.value = '加载商品详情失败'
    
    // 失败时使用 Mock 数据
    const mockProducts = [
      { id: 1, name: '旗舰智能手机', price: 5999, category: '数码', description: '搭载最新处理器，性能卓越', img: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400', stock: 100 },
      { id: 2, name: '超轻薄笔记本', price: 8299, category: '数码', description: '轻至1.2kg，续航持久', img: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400', stock: 50 },
      { id: 3, name: '无线降噪耳机', price: 1999, category: '配件', description: '主动降噪，沉浸音质', img: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', stock: 200 },
      { id: 4, name: '人体工学键盘', price: 599, category: '配件', description: '舒适手感，提升效率', img: 'https://images.unsplash.com/photo-1587829741301-dc798b83aca2?w=400', stock: 30 },
    ]
    const id = parseInt(route.params.id)
    product.value = mockProducts.find(p => p.id === id) || {}
  } finally {
    loading.value = false
  }
}

const handleAddToCart = () => {
  if (product.value.stock <= 0) {
    alert('该商品已售完')
    return
  }
  // 检查购物车中已有的数量
  const currentCartItem = cartStore.items.find(item => item.id === product.value.id)
  const currentQty = currentCartItem ? currentCartItem.quantity : 0
  
  if (currentQty + 1 > product.value.stock) {
    alert(`库存不足，仅剩 ${product.value.stock} 件`)
    return
  }

  cartStore.addToCart(product.value)
  alert('已加入购物车')
}

const handleQuickBuy = () => {
  if (product.value.stock <= 0) {
    alert('该商品已售完')
    return
  }
  // 检查购物车中已有的数量
  const currentCartItem = cartStore.items.find(item => item.id === product.value.id)
  const currentQty = currentCartItem ? currentCartItem.quantity : 0
  
  if (currentQty + 1 > product.value.stock) {
    alert(`库存不足，仅剩 ${product.value.stock} 件`)
    return
  }
  
  cartStore.addToCart(product.value)
  router.push('/cart')
}

onMounted(() => {
  fetchProductDetail()
})
</script>