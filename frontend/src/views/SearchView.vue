<template>
  <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
    <!-- 搜索头部状态 -->
    <div class="mb-10 flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <h2 class="text-3xl font-black text-gray-800">
          搜索结果：<span class="text-blue-600">“{{ searchQuery }}”</span>
        </h2>
        <p class="text-gray-400 mt-2 font-medium">
          为您找到 {{ filteredProducts.length }} 件相关商品
        </p>
      </div>
      
      <!-- 简单的排序过滤 -->
      <div class="flex gap-4">
        <select 
          v-model="sortOrder"
          @change="handleSortChange"
          class="bg-white border border-gray-100 rounded-xl px-4 py-2 text-sm font-bold text-gray-600 outline-none focus:ring-2 focus:ring-blue-500 shadow-sm cursor-pointer"
        >
          <option value="default">综合排序</option>
          <option value="price_asc">价格从低到高</option>
          <option value="price_desc">价格从高到低</option>
        </select>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-32">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-500">搜索中...</p>
    </div>

    <!-- 结果网格 -->
    <div v-else-if="filteredProducts.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
      <div 
        v-for="product in filteredProducts" 
        :key="product.id"
        class="group bg-white rounded-[32px] overflow-hidden border border-gray-50 shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300"
      >
        <div class="relative aspect-square overflow-hidden cursor-pointer" @click="goToDetail(product.id)">
          <img :src="getImageUrl(product)" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110" />
          <div class="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
        </div>
        <div class="p-6">
          <div class="text-[10px] font-black text-blue-500 uppercase tracking-widest mb-1">{{ product.categoryName || product.category }}</div>
          <h3 class="font-bold text-gray-800 mb-2 truncate group-hover:text-blue-600 transition-colors">{{ product.name }}</h3>
          <div class="flex justify-between items-center mt-4">
            <span class="text-xl font-black text-gray-900">￥{{ product.price }}</span>
            <button 
              @click="handleAddToCart(product)"
              class="w-10 h-10 bg-gray-900 text-white rounded-xl flex items-center justify-center hover:bg-blue-600 transition-colors shadow-lg shadow-gray-100"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="text-center py-32 bg-white rounded-[40px] border border-dashed border-gray-200">
      <div class="w-20 h-20 bg-gray-50 text-gray-300 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
      </div>
      <h3 class="text-xl font-bold text-gray-800 mb-2">未找到匹配商品</h3>
      <p class="text-gray-400">尝试更换关键词，或者去首页看看推荐商品</p>
      <router-link to="/" class="inline-block mt-8 px-8 py-3 bg-blue-600 text-white rounded-2xl font-bold shadow-lg shadow-blue-100">回到首页</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../store/cart'
import { productService } from '../api/product'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const getImageUrl = (p) => {
  const url = p.imageUrl || p.image || p.img
  if (!url) return ''
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}

const products = ref([])
const loading = ref(false)
const sortOrder = ref('default')

// 获取路由参数中的搜索关键词
const searchQuery = computed(() => route.query.q || '')

// 搜索商品
const searchProducts = async () => {
  if (!searchQuery.value) return
  
  loading.value = true
  try {
    const params = {
      keyword: searchQuery.value
    }
    
    // 添加排序参数
    if (sortOrder.value === 'price_asc') {
      params.sort = 'price_asc'
    } else if (sortOrder.value === 'price_desc') {
      params.sort = 'price_desc'
    }
    
    const response = await productService.searchProducts(params)
    // 适配后端 PageResult 结构 (response.data.items)
    products.value = response.data.items || response.data || []
    console.log('搜索成功:', products.value.length, '件商品')
  } catch (err) {
    console.error('搜索失败:', err)
    
    // 失败时使用 Mock 数据
    const mockProducts = [
      { id: 1, name: '极简智能手表 Series 7', price: 2599, category: '电子产品', img: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500', stock: 50 },
      { id: 2, name: '手工陶瓷咖啡杯', price: 128, category: '居家生活', img: 'https://images.unsplash.com/photo-1514228742587-6b1558fbed20?w=500', stock: 100 },
      { id: 3, name: '无线主动降噪耳机', price: 1899, category: '电子产品', img: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500', stock: 30 },
      { id: 4, name: '纯棉透气白色 T 恤', price: 199, category: '服装服饰', img: 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500', stock: 200 },
    ]
    
    // 前端过滤
    products.value = mockProducts.filter(p => 
      p.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
      p.category.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  } finally {
    loading.value = false
  }
}

const filteredProducts = computed(() => products.value)

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const handleSortChange = () => {
  searchProducts()
}

const handleAddToCart = (product) => {
  if (product.stock !== undefined && product.stock <= 0) {
    alert('该商品已售完')
    return
  }
  // 检查购物车中已有的数量
  const currentCartItem = cartStore.items.find(item => item.id === product.id)
  const currentQty = currentCartItem ? currentCartItem.quantity : 0
  
  if (product.stock !== undefined && currentQty + 1 > product.stock) {
    alert(`库存不足，仅剩 ${product.stock} 件`)
    return
  }

  cartStore.addToCart(product)
  alert('已加入购物车')
}

// 监听搜索关键词变化
watch(searchQuery, () => {
  searchProducts()
}, { immediate: true })

onMounted(() => {
  searchProducts()
})
</script>