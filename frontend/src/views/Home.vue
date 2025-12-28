<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <!-- 营销 Banner -->
    <div class="bg-gradient-to-r from-blue-500 to-indigo-600 rounded-2xl h-48 mb-10 flex items-center px-12 text-white">
      <div>
        <h1 class="text-3xl font-bold mb-2">全场数码产品 8 折起</h1>
        <p class="opacity-90">开启你的智能生活新体验</p>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold text-gray-800">热门推荐</h2>
      <div class="flex space-x-4 text-sm">
        <button 
          @click="selectCategory(null)"
          :class="[selectedCategory === null ? 'text-blue-600 font-bold' : 'text-gray-500 hover:text-blue-600']"
        >
          全部
        </button>
        <button
          v-for="category in categories.slice(0, 3)"
          :key="category.id"
          @click="selectCategory(category.id)"
          :class="[selectedCategory === category.id ? 'text-blue-600 font-bold' : 'text-gray-500 hover:text-blue-600']"
        >
          {{ category.name }}
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      <p class="mt-4 text-gray-500">加载中...</p>
    </div>

    <!-- 错误提示 -->
    <div v-else-if="error" class="text-center py-12">
      <p class="text-red-600">{{ error }}</p>
      <button @click="fetchProducts()" class="mt-4 px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
        重新加载
      </button>
    </div>

    <!-- 商品网格 -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <ProductCard
        v-for="product in products"
        :key="product.id"
        :product="product"
        @add-to-cart="handleAddToCart"
        @view-detail="handleViewDetail"
      />
    </div>

    <!-- 管理后台入口 -->
    <div class="mt-16 text-center">
      <router-link 
        to="/admin" 
        class="inline-flex items-center space-x-2 text-gray-400 hover:text-gray-600 text-sm font-medium transition-colors"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
        </svg>
        <span>管理后台</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cart'
import { productService } from '../api/product'
import ProductCard from '../components/ProductCard.vue'

const router = useRouter()
const cartStore = useCartStore()

const products = ref([])
const categories = ref([])
const loading = ref(false)
const error = ref('')
const selectedCategory = ref(null)

// Mock 商品数据（API 失败时降级使用）
const mockProducts = [
  { id: 1, name: '旗舰智能手机 Pro Max', price: 5999, category: '数码产品', description: '搭载最新A17芯片，120Hz屏幕', imageUrl: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400', stock: 100 },
  { id: 2, name: '超轻薄笔记本 15寸', price: 8299, category: '数码产品', description: '轻至1.2kg，16GB内存', imageUrl: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400', stock: 50 },
  { id: 3, name: '无线降噪耳机', price: 1999, category: '音频配件', description: '主动降噪，40小时续航', imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', stock: 200 },
  { id: 4, name: '人体工学键盘', price: 599, category: '电脑配件', description: '机械轴体，RGB背光', imageUrl: 'https://images.unsplash.com/photo-1587829741301-dc798b83aca2?w=400', stock: 30 },
  { id: 5, name: '4K显示器 27寸', price: 3299, category: '电脑配件', description: '4K分辨率，HDR400', imageUrl: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400', stock: 15 },
  { id: 6, name: '无线充电器', price: 299, category: '数码配件', description: '15W快充，支持多设备', imageUrl: 'https://images.unsplash.com/photo-1591290619762-c588f8ed1906?w=400', stock: 500 },
  { id: 7, name: '智能手表', price: 2499, category: '数码产品', description: '健康监测，GPS定位', imageUrl: 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400', stock: 80 },
  { id: 8, name: '蓝牙音箱', price: 899, category: '音频配件', description: '360度环绕音效', imageUrl: 'https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?w=400', stock: 60 },
  { id: 9, name: '游戏鼠标', price: 399, category: '电脑配件', description: '16000DPI，可编程按键', imageUrl: 'https://images.unsplash.com/photo-1527814050087-3793815479db?w=400', stock: 120 },
  { id: 10, name: '移动电源 20000mAh', price: 199, category: '数码配件', description: '双向快充，轻薄便携', imageUrl: 'https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400', stock: 300 },
  { id: 11, name: '机械键盘', price: 799, category: '电脑配件', description: '青轴，全键无冲', imageUrl: 'https://images.unsplash.com/photo-1595225476474-87563907a212?w=400', stock: 40 },
  { id: 12, name: 'Type-C数据线', price: 59, category: '数码配件', description: '100W快充，编织线材', imageUrl: 'https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?w=400', stock: 1000 },
  { id: 13, name: '平板电脑 11寸', price: 4299, category: '数码产品', description: 'M2芯片，120Hz屏幕', imageUrl: 'https://images.unsplash.com/photo-1561154464-82e9adf32764?w=400', stock: 25 },
  { id: 14, name: '网络摄像头 1080P', price: 399, category: '电脑配件', description: '自动对焦，降噪麦克风', imageUrl: 'https://images.unsplash.com/photo-1633113216876-6c3d8b4e3e2e?w=400', stock: 70 },
  { id: 15, name: '无线路由器', price: 599, category: '数码配件', description: 'WiFi 6，全千兆端口', imageUrl: 'https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=400', stock: 90 },
  { id: 16, name: 'USB扩展坞', price: 299, category: '电脑配件', description: '7合1接口，4K输出', imageUrl: 'https://images.unsplash.com/photo-1625948515291-69613efd103f?w=400', stock: 150 }
]

// Mock 分类数据
const mockCategories = [
  { id: 1, name: '数码产品' },
  { id: 2, name: '音频配件' },
  { id: 3, name: '电脑配件' },
  { id: 4, name: '数码配件' }
]

// 处理查看商品详情
const handleViewDetail = (product) => {
  router.push({ name: 'product-detail', params: { id: product.id } })
}

// 获取商品列表
const fetchProducts = async (params = {}) => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await productService.searchProducts(params)
    // 适配后端 PageResult 结构 (response.data.items)
    const data = response.data?.items || response.data || response.products || response || []
    products.value = data.length > 0 ? data : mockProducts
    console.log('商品列表加载成功:', products.value.length)
  } catch (err) {
    console.error('获取商品列表失败，使用 Mock 数据:', err)
    error.value = ''
    products.value = mockProducts
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await productService.getCategories()
    const data = response.data || response || []
    categories.value = data.length > 0 ? data : mockCategories
    console.log('分类列表加载成功:', categories.value.length)
  } catch (err) {
    console.error('获取分类列表失败，使用 Mock 数据:', err)
    categories.value = mockCategories
  }
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  fetchProducts({ categoryId })
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

// 页面加载时获取数据
onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>