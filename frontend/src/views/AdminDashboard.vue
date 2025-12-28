<template>
  <div class="min-h-screen bg-gray-50 flex">
    <!-- 侧边栏导航 -->
    <aside class="w-64 bg-white border-r border-gray-100 flex flex-col sticky top-0 h-screen">
      <div class="p-8">
        <div class="flex items-center space-x-3 mb-10">
          <div class="bg-gray-900 p-2 rounded-xl">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
          </div>
          <span class="font-black text-xl tracking-tighter italic">ADMIN</span>
        </div>

        <nav class="space-y-2">
          <button @click="activeTab = 'stats'" :class="activeTab === 'stats' ? 'bg-blue-600 text-white shadow-lg shadow-blue-100' : 'text-gray-500 hover:bg-gray-50'" class="w-full flex items-center space-x-3 px-4 py-3 rounded-2xl transition-all font-bold text-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h7" /></svg>
            <span>概览工作台</span>
          </button>
          <button @click="activeTab = 'products'" :class="activeTab === 'products' ? 'bg-blue-600 text-white shadow-lg shadow-blue-100' : 'text-gray-500 hover:bg-gray-50'" class="w-full flex items-center space-x-3 px-4 py-3 rounded-2xl transition-all font-bold text-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" /></svg>
            <span>商品库管理</span>
          </button>
          <button @click="activeTab = 'orders'" :class="activeTab === 'orders' ? 'bg-blue-600 text-white shadow-lg shadow-blue-100' : 'text-gray-500 hover:bg-gray-50'" class="w-full flex items-center space-x-3 px-4 py-3 rounded-2xl transition-all font-bold text-sm">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" /></svg>
            <span>全网订单</span>
          </button>
        </nav>
      </div>
      
      <div class="mt-auto p-8">
        <button @click="router.push('/')" class="w-full flex items-center space-x-3 px-4 py-3 rounded-2xl text-gray-400 hover:bg-gray-50 transition-all font-bold text-sm">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" /></svg>
          <span>返回商城</span>
        </button>
      </div>
    </aside>

    <!-- 主体内容 -->
    <main class="flex-1 p-12 overflow-y-auto">
      <!-- 概览工作台 -->
      <div v-if="activeTab === 'stats'" class="space-y-10">
        <header>
          <h1 class="text-4xl font-black text-gray-900 tracking-tight">今日业务概览</h1>
          <p class="text-gray-400 mt-2">实时监控商城运行状态</p>
        </header>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div class="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
            <div class="flex justify-between items-start mb-2">
              <p class="text-xs font-black text-gray-400 uppercase tracking-widest">成交额 (GMV)</p>
              <div class="flex space-x-1 bg-gray-100 p-1 rounded-lg">
                <button @click="changeRange('day')" :class="timeRange === 'day' ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'" class="px-2 py-1 text-xs font-bold rounded-md transition-all">今日</button>
                <button @click="changeRange('month')" :class="timeRange === 'month' ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'" class="px-2 py-1 text-xs font-bold rounded-md transition-all">本月</button>
                <button @click="changeRange('year')" :class="timeRange === 'year' ? 'bg-white shadow text-gray-900' : 'text-gray-500 hover:text-gray-700'" class="px-2 py-1 text-xs font-bold rounded-md transition-all">本年</button>
              </div>
            </div>
            <h2 class="text-3xl font-black text-gray-900">￥{{ stats.gmv }}</h2>
          </div>
          <div class="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
            <p class="text-xs font-black text-gray-400 uppercase tracking-widest mb-2">订单总量</p>
            <h2 class="text-3xl font-black text-gray-900">{{ stats.orderCount }}</h2>
          </div>
          <div class="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
            <p class="text-xs font-black text-gray-400 uppercase tracking-widest mb-2">活跃用户</p>
            <h2 class="text-3xl font-black text-gray-900">{{ stats.userCount }}</h2>
          </div>
        </div>

        <!-- 销售趋势图表 -->
        <div class="bg-white p-8 rounded-[32px] border border-gray-100 shadow-sm">
          <h3 class="text-lg font-black text-gray-900 mb-6">销售趋势</h3>
          <div class="h-80">
            <Line v-if="chartData.labels.length > 0" :data="chartData" :options="chartOptions" />
            <div v-else class="h-full flex items-center justify-center text-gray-400">暂无数据</div>
          </div>
        </div>
      </div>

      <!-- 商品库管理 -->
      <div v-if="activeTab === 'products'" class="space-y-8">
        <div class="flex justify-between items-end">
          <header>
            <h1 class="text-4xl font-black text-gray-900 tracking-tight">商品库管理</h1>
            <p class="text-gray-400 mt-2">维护商城核心商品资产</p>
          </header>
          <button @click="openAddModal" class="bg-gray-900 text-white px-8 py-4 rounded-2xl font-black shadow-xl hover:bg-black transition-all active:scale-95">
            发布新商品
          </button>
        </div>

        <div class="bg-white rounded-[40px] border border-gray-100 shadow-sm overflow-hidden">
          <table class="w-full text-left">
            <thead>
              <tr class="bg-gray-50/50 border-b border-gray-50">
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">商品详情</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">价格</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">库存</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest text-right">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr v-for="product in products" :key="product.id" class="hover:bg-gray-50/30 transition-colors">
                <td class="px-8 py-6">
                  <div class="flex items-center space-x-4">
                    <img :src="getImageUrl(product)" class="w-12 h-12 rounded-xl object-cover bg-gray-100" />
                    <div>
                      <p class="font-bold text-gray-800">{{ product.name }}</p>
                      <p class="text-xs text-gray-400">{{ product.categoryName }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-8 py-6 font-black text-gray-900">￥{{ product.price }}</td>
                <td class="px-8 py-6">
                  <span :class="product.stock < 10 ? 'text-red-500 font-bold' : 'text-gray-500'">{{ product.stock }}</span>
                </td>
                <td class="px-8 py-6 text-right space-x-4">
                  <button @click="openEditModal(product)" class="text-xs font-black text-blue-600 hover:underline">编辑</button>
                  <button @click="deleteProduct(product.id)" class="text-xs font-black text-red-500 hover:underline">下架</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 订单管理 -->
      <div v-if="activeTab === 'orders'" class="space-y-8">
        <header>
          <h1 class="text-4xl font-black text-gray-900 tracking-tight">全网订单</h1>
          <p class="text-gray-400 mt-2">处理客户订单与物流发货</p>
        </header>
        
        <div class="bg-white rounded-[40px] border border-gray-100 shadow-sm overflow-hidden">
          <table class="w-full text-left">
            <thead>
              <tr class="bg-gray-50/50 border-b border-gray-50">
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">订单编号</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">用户</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">金额</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">状态</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest">下单时间</th>
                <th class="px-8 py-6 text-xs font-black text-gray-400 uppercase tracking-widest text-right">操作</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-50">
              <tr 
                v-for="order in orders" 
                :key="order.id" 
                class="hover:bg-gray-50/30 transition-colors cursor-pointer"
                @click="openOrderModal(order)"
              >
                <td class="px-8 py-6">
                  <p class="font-mono text-sm font-bold text-gray-800">#{{ order.id }}</p>
                </td>
                <td class="px-8 py-6">
                  <p class="font-bold text-gray-800">用户ID: {{ order.userId }}</p>
                </td>
                <td class="px-8 py-6 font-black text-gray-900">￥{{ order.totalAmount }}</td>
                <td class="px-8 py-6">
                  <span :class="{
                    'bg-yellow-100 text-yellow-800': order.status === 'pending',
                    'bg-blue-100 text-blue-800': order.status === 'paid',
                    'bg-green-100 text-green-800': order.status === 'shipped' || order.status === 'completed',
                    'bg-red-100 text-red-800': order.status === 'cancelled'
                  }" class="px-3 py-1 rounded-full text-xs font-bold">
                    {{ getStatusText(order.status) }}
                  </span>
                </td>
                <td class="px-8 py-6 text-sm text-gray-500">{{ formatDate(order.createdAt) }}</td>
                <td class="px-8 py-6 text-right space-x-4" @click.stop>
                  <button 
                    v-if="order.status === 'paid'"
                    @click="updateOrderStatus(order.id, 'shipped')" 
                    class="text-xs font-black text-blue-600 hover:underline"
                  >
                    发货
                  </button>
                  <button 
                    v-if="order.status === 'shipped'"
                    @click="sendConfirmationEmail(order.id)" 
                    class="text-xs font-black text-purple-600 hover:underline"
                  >
                    发送确认邮件
                  </button>
                  <button 
                    v-if="order.status === 'pending'"
                    @click="updateOrderStatus(order.id, 'cancelled')" 
                    class="text-xs font-black text-red-500 hover:underline"
                  >
                    取消
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
          
          <!-- 空状态 -->
          <div v-if="orders.length === 0" class="p-12 text-center">
            <p class="text-gray-400 font-medium">暂无订单数据</p>
          </div>

          <!-- 分页控件 -->
          <div v-if="totalPages > 1" class="flex justify-center items-center space-x-4 p-6 border-t border-gray-50">
            <button 
              @click="fetchOrders(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="px-4 py-2 rounded-xl text-sm font-bold transition-colors"
              :class="currentPage === 0 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
            >
              上一页
            </button>
            <span class="text-sm font-medium text-gray-500">
              第 {{ currentPage + 1 }} 页 / 共 {{ totalPages }} 页
            </span>
            <button 
              @click="fetchOrders(currentPage + 1)" 
              :disabled="currentPage >= totalPages - 1"
              class="px-4 py-2 rounded-xl text-sm font-bold transition-colors"
              :class="currentPage >= totalPages - 1 ? 'text-gray-300 cursor-not-allowed' : 'text-gray-600 hover:bg-gray-100'"
            >
              下一页
            </button>
          </div>
        </div>
      </div>
    </main>

    <!-- 商品编辑模态框 -->
    <ProductModal
      v-if="showProductModal"
      :product="editingProduct"
      :isEdit="!!editingProduct"
      @close="showProductModal = false"
      @save="handleSaveProduct"
    />

    <!-- 订单详情模态框 -->
    <OrderDetailModal
      v-if="showOrderModal"
      :order="selectedOrder"
      @close="showOrderModal = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { adminService } from '../api/admin'
import ProductModal from '../components/ProductModal.vue'
import OrderDetailModal from '../components/OrderDetailModal.vue'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import { Line } from 'vue-chartjs'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

const router = useRouter()
const activeTab = ref('stats')
const showProductModal = ref(false)
const editingProduct = ref(null)
const showOrderModal = ref(false)
const selectedOrder = ref(null)
const loading = ref(false)

// Pagination state
const currentPage = ref(0)
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)

const getImageUrl = (p) => {
  const url = p.imageUrl || p.image || p.img
  if (!url) return ''
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}

// 统计数据
const stats = ref({ 
  gmv: '0.00', 
  orderCount: 0, 
  userCount: 0 
})

const timeRange = ref('day')
const chartData = ref({
  labels: [],
  datasets: []
})
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: false
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: {
        display: true,
        drawBorder: false
      }
    },
    x: {
      grid: {
        display: false
      }
    }
  }
}

// Mock 统计数据（API 失败时降级使用）
const mockStats = {
  gmv: '128,490.00',
  orderCount: 412,
  userCount: 1205
}

// 商品数据（从 API 加载）
const products = ref([])

// Mock 商品数据（API 失败时降级使用）
const mockProducts = [
  {
    id: 1,
    name: '旗舰智能手机',
    categoryName: '数码产品',
    price: 5999,
    stock: 45,
    imageUrl: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400',
    description: '最新款5G智能手机，性能强劲'
  },
  {
    id: 2,
    name: '超轻薄笔记本',
    categoryName: '数码产品',
    price: 8299,
    stock: 8,
    imageUrl: 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400',
    description: '轻至1.2kg，续航12小时'
  },
  {
    id: 3,
    name: '无线降噪耳机',
    categoryName: '音频配件',
    price: 1999,
    stock: 120,
    imageUrl: 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400',
    description: '主动降噪，沉浸式音质体验'
  },
  {
    id: 4,
    name: '人体工学键盘',
    categoryName: '电脑配件',
    price: 599,
    stock: 3,
    imageUrl: 'https://images.unsplash.com/photo-1587829741301-dc798b83aca2?w=400',
    description: '机械轴体，RGB背光'
  },
  {
    id: 5,
    name: '4K显示器',
    categoryName: '电脑配件',
    price: 3299,
    stock: 25,
    imageUrl: 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400',
    description: '27英寸4K分辨率，HDR支持'
  }
]

// 订单数据（从 API 加载）
const orders = ref([])

// Mock 订单数据（API 失败时降级使用）
const mockOrders = [
  {
    id: 'ORD202312250001',
    userId: 1001,
    totalAmount: 5999,
    status: 'paid',
    createdAt: '2023-12-25T10:30:00Z'
  },
  {
    id: 'ORD202312250002',
    userId: 1002,
    totalAmount: 1999,
    status: 'shipped',
    createdAt: '2023-12-25T09:15:00Z'
  },
  {
    id: 'ORD202312250003',
    userId: 1003,
    totalAmount: 8299,
    status: 'pending',
    createdAt: '2023-12-25T08:45:00Z'
  },
  {
    id: 'ORD202312240001',
    userId: 1004,
    totalAmount: 599,
    status: 'completed',
    createdAt: '2023-12-24T16:20:00Z'
  },
  {
    id: 'ORD202312240002',
    userId: 1005,
    totalAmount: 3299,
    status: 'cancelled',
    createdAt: '2023-12-24T14:10:00Z'
  }
]

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await adminService.getStats(timeRange.value)
    const data = response.data || response
    stats.value = data
    
    // Process chart data
    if (data.chartData) {
      const labels = data.chartData.map(item => item.date)
      const values = data.chartData.map(item => item.amount)
      
      chartData.value = {
        labels,
        datasets: [
          {
            label: '销售额',
            backgroundColor: '#3B82F6',
            borderColor: '#3B82F6',
            data: values,
            tension: 0.4
          }
        ]
      }
    }
    console.log('获取统计数据成功:', stats.value)
  } catch (error) {
    console.error('获取统计数据失败，使用 Mock 数据:', error)
    stats.value = mockStats
  }
}

const changeRange = (range) => {
  timeRange.value = range
  fetchStats()
}

// 获取商品列表
const fetchProducts = async () => {
  loading.value = true
  try {
    const response = await adminService.getProducts()
    const data = response.items || response.data?.items || response || []
    products.value = data
    console.log('获取商品列表成功:', products.value)
  } catch (error) {
    console.error('获取商品列表失败:', error)
    // products.value = mockProducts // Disable mock data
  } finally {
    loading.value = false
  }
}

// 获取订单列表
const fetchOrders = async (page = 0) => {
  try {
    currentPage.value = page
    const response = await adminService.getOrders({
      page: currentPage.value,
      size: pageSize.value
    })
    // Spring Page returns 'content' inside 'data'
    // response structure: { code: 200, data: { content: [...], totalElements: ... }, ... }
    const data = response.data || {}
    const content = data.content || []
    
    totalPages.value = data.totalPages || 0
    totalElements.value = data.totalElements || 0
    
    // Map backend fields to frontend expected format
    orders.value = content.map(order => ({
      ...order,
      createdAt: order.createTime, // Map createTime -> createdAt
      status: mapBackendStatus(order.status), // Map int status -> string status
      shippingAddress: order.receiverAddress // Map receiverAddress -> shippingAddress for Modal
    }))
    
    console.log('获取订单列表成功:', orders.value)
  } catch (error) {
    console.error('获取订单列表失败:', error)
  }
}

// Map backend integer status to frontend string status
const mapBackendStatus = (statusInt) => {
  const map = {
    0: 'pending',
    1: 'paid',
    2: 'shipped',
    3: 'completed',
    4: 'cancelled'
  }
  return map[statusInt] || 'pending'
}

// 更新订单状态
const updateOrderStatus = async (orderId, status) => {
  try {
    await adminService.updateOrderStatus(orderId, status)
    console.log('更新订单状态成功:', orderId, status)
    await fetchOrders() // 重新加载订单列表
  } catch (error) {
    console.error('更新订单状态失败:', error)
    alert('更新订单状态失败，请重试')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待支付',
    'paid': '已支付',
    'shipped': '已发货',
    'completed': '已完成',
    'cancelled': '已取消'
  }
  return statusMap[status] || status
}

// 打开订单详情
const openOrderModal = (order) => {
  selectedOrder.value = order
  showOrderModal.value = true
}

// 打开新增商品模态框
const openAddModal = () => {
  editingProduct.value = null
  showProductModal.value = true
}

// 打开编辑商品模态框
const openEditModal = (product) => {
  editingProduct.value = { ...product }
  showProductModal.value = true
}

// 保存商品（新增或编辑）
const handleSaveProduct = async (productData) => {
  try {
    if (editingProduct.value) {
      // 编辑商品
      await adminService.updateProduct(productData.id, productData)
      console.log('更新商品成功:', productData)
    } else {
      // 新增商品
      await adminService.createProduct(productData)
      console.log('创建商品成功:', productData)
    }
    showProductModal.value = false
    await fetchProducts() // 重新加载商品列表
  } catch (error) {
    console.error('保存商品失败:', error)
    alert('保存商品失败，请重试')
  }
}

// 下架商品
const deleteProduct = async (productId) => {
  if (!confirm('确定要下架这个商品吗？')) {
    return
  }
  
  try {
    await adminService.deleteProduct(productId)
    console.log('下架商品成功:', productId)
    await fetchProducts() // 重新加载商品列表
  } catch (error) {
    console.error('下架商品失败:', error)
    alert('下架商品失败，请重试')
  }
}

// 发送确认邮件
const sendConfirmationEmail = async (orderId) => {
  try {
    await adminService.sendConfirmationEmail(orderId)
    alert('确认邮件已发送')
  } catch (error) {
    console.error('发送邮件失败:', error)
    alert('发送邮件失败')
  }
}

onMounted(() => {
  console.log('管理后台加载完成')
  // 实际项目中应检查管理员权限
  
  // 加载初始数据（不使用 await，避免 setup 返回 Promise）
  fetchStats()
  fetchProducts()
  fetchOrders()
})
</script>