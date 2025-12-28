<template>
  <div class="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
    <div class="flex flex-col md:flex-row gap-8">
      <!-- 左侧导航栏 -->
      <div class="w-full md:w-64 space-y-2">
        <div class="bg-white rounded-3xl p-6 shadow-sm border border-gray-50 mb-6">
          <div class="flex items-center space-x-4 mb-6">
            <div class="w-12 h-12 bg-blue-600 rounded-2xl flex items-center justify-center text-white font-bold text-xl">
              {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'U' }}
            </div>
            <div>
              <h2 class="font-bold text-gray-800">{{ userStore.userInfo?.username || '未登录' }}</h2>
              <p class="text-xs text-gray-400">{{ userStore.userInfo?.email || '普通用户' }}</p>
              <div class="flex items-center mt-1 space-x-1">
                <span :class="['px-2 py-0.5 rounded text-[10px] font-medium', 
                  userStore.isAdmin ? 'bg-red-100 text-red-600' : 'bg-blue-100 text-blue-600']">
                  {{ userStore.isAdmin ? '管理员' : '普通用户' }}
                </span>
                <!-- 
                <button 
                  @click="toggleRole"
                  class="px-2 py-0.5 bg-gray-100 hover:bg-gray-200 rounded text-[10px] font-medium text-gray-700 transition-colors"
                  title="切换用户角色测试权限控制"
                >
                  切换
                </button>
                -->
              </div>
            </div>
          </div>
          <button 
            @click="activeTab = 'orders'"
            :class="activeTab === 'orders' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'"
            class="w-full flex items-center space-x-3 px-4 py-3 rounded-xl transition-all font-medium text-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span>我的订单</span>
          </button>
          <button 
            @click="activeTab = 'address'"
            :class="activeTab === 'address' ? 'bg-blue-50 text-blue-600' : 'text-gray-500 hover:bg-gray-50'"
            class="w-full flex items-center space-x-3 px-4 py-3 rounded-xl transition-all font-medium text-sm mt-1"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <span>收货地址</span>
          </button>
          <hr class="my-4 border-gray-100" />
          <button 
            @click="handleLogout"
            class="w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-red-500 hover:bg-red-50 transition-all font-medium text-sm"
          >
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
            <span>退出登录</span>
          </button>
        </div>
      </div>

      <!-- 右侧内容区 -->
      <div class="flex-1 bg-white rounded-[40px] p-8 shadow-sm border border-gray-50 min-h-[600px]">
        <!-- 订单列表 -->
        <div v-if="activeTab === 'orders'">
          <h3 class="text-2xl font-black text-gray-800 mb-8">我的订单</h3>
          <div v-if="orders.length === 0" class="text-center py-20">
            <p class="text-gray-400">暂无订单数据</p>
          </div>
          <div v-else class="space-y-6">
            <div 
              v-for="order in orders" 
              :key="order.id" 
              @click="viewOrderDetail(order)"
              class="border border-gray-100 rounded-3xl p-6 hover:border-blue-200 hover:shadow-md transition-all cursor-pointer group"
            >
              <div class="flex justify-between items-start mb-4">
                <div>
                  <p class="text-xs text-gray-400 font-medium uppercase tracking-wider">订单号: {{ order.id }}</p>
                  <p class="text-sm text-gray-500 mt-1">{{ order.createTime }}</p>
                </div>
                <div class="flex items-center gap-3">
                  <span 
                    :class="{
                      'bg-yellow-100 text-yellow-600': order.status === 'PENDING',
                      'bg-green-100 text-green-600': order.status === 'PAID',
                      'bg-blue-100 text-blue-600': order.status === 'SHIPPED',
                      'bg-gray-100 text-gray-600': order.status === 'DONE'
                    }"
                    class="px-3 py-1 rounded-lg text-xs font-bold"
                  >
                    {{ statusMap[order.status] }}
                  </span>
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400 group-hover:text-blue-600 transition-colors" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                  </svg>
                </div>
              </div>
              <div class="flex justify-between items-end">
                <div class="flex -space-x-2">
                  <div v-for="(item, i) in order.items.slice(0, 3)" :key="i" class="w-10 h-10 rounded-full border-2 border-white bg-gray-100 overflow-hidden">
                    <img v-if="item.productImage || item.image" :src="item.productImage || item.image" class="w-full h-full object-cover" />
                  </div>
                  <div v-if="order.items.length > 3" class="w-10 h-10 rounded-full border-2 border-white bg-gray-200 flex items-center justify-center">
                    <span class="text-xs font-bold text-gray-600">+{{ order.items.length - 3 }}</span>
                  </div>
                </div>
                <div class="text-right">
                  <p class="text-xs text-gray-400">实付金额</p>
                  <p class="text-xl font-black text-gray-900">￥{{ order.totalAmount.toLocaleString() }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 地址管理 -->
        <div v-if="activeTab === 'address'">
          <div class="flex justify-between items-center mb-8">
            <h3 class="text-2xl font-black text-gray-800">收货地址</h3>
            <button 
              @click="openAddressModal()"
              class="bg-blue-600 text-white px-6 py-2 rounded-xl text-sm font-bold shadow-lg shadow-blue-100 hover:bg-blue-700 transition-colors"
            >
              + 新增地址
            </button>
          </div>
          <div v-if="addresses.length === 0" class="text-center py-20">
            <p class="text-gray-400">暂无收货地址，请添加</p>
          </div>
          <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div v-for="addr in addresses" :key="addr.id" class="p-6 border border-gray-100 rounded-3xl relative hover:border-blue-100 transition-all group">
              <div v-if="addr.isDefault" class="absolute top-4 right-4 bg-blue-100 text-blue-600 text-[10px] px-2 py-0.5 rounded-md font-bold uppercase">默认</div>
              <h4 class="font-bold text-gray-800 mb-1">{{ addr.receiverName }}</h4>
              <p class="text-sm text-gray-500 mb-3">{{ addr.phone }}</p>
              <p class="text-sm text-gray-400 leading-relaxed">
                {{ addr.province }} {{ addr.city }} {{ addr.district }}
                <br/>
                {{ addr.detailAddress }}
              </p>
              <div class="mt-6 flex space-x-4 opacity-0 group-hover:opacity-100 transition-opacity">
                <button @click="openAddressModal(addr)" class="text-xs font-bold text-blue-600 hover:underline">编辑</button>
                <button @click="deleteAddress(addr.id)" class="text-xs font-bold text-red-500 hover:underline">删除</button>
                <button v-if="!addr.isDefault" @click="setDefaultAddress(addr.id)" class="text-xs font-bold text-green-600 hover:underline">设为默认</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址编辑弹窗 -->
    <AddressModal 
      v-if="showAddressModal"
      :address="editingAddress"
      :isNew="isNewAddress"
      @close="showAddressModal = false"
      @save="handleSaveAddress"
    />

    <!-- 订单详情弹窗 -->
    <OrderDetailModal
      v-if="showOrderDetail"
      :order="selectedOrder"
      @close="showOrderDetail = false"
    />
  </div>
</template>

<script setup>
import AddressModal from '../components/AddressModal.vue'
import OrderDetailModal from '../components/OrderDetailModal.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { orderService } from '../api/order'
import { userService } from '../api/user'

const userStore = useUserStore()
const router = useRouter()
const activeTab = ref('orders')
const showAddressModal = ref(false)
const showOrderDetail = ref(false)
const editingAddress = ref(null)
const isNewAddress = ref(false)
const selectedOrder = ref(null)

// 订单数据
const orders = ref([])
const loadingOrders = ref(false)

// 获取订单列表
const fetchOrders = async () => {
  loadingOrders.value = true
  try {
    const response = await orderService.getOrders()
    // Spring Page returns 'content' inside 'data'
    const content = response.data?.content || []
    
    // Map backend fields to frontend expected format
    orders.value = content.map(order => ({
      ...order,
      createTime: order.createTime, // Backend returns createTime
      status: mapBackendStatus(order.status), // Map int status -> string status
      items: order.items || [] // Ensure items is an array
    }))
    
    console.log('获取订单列表成功:', orders.value)
  } catch (error) {
    console.error('获取订单列表失败:', error)
    orders.value = []
  } finally {
    loadingOrders.value = false
  }
}

// Map backend integer status to frontend string status
const mapBackendStatus = (statusInt) => {
  const map = {
    0: 'PENDING',
    1: 'PAID',
    2: 'SHIPPED',
    3: 'DONE',
    4: 'CANCELLED'
  }
  return map[statusInt] || 'PENDING'
}

// 地址数据（从 API 加载）
const addresses = ref([])
const loadingAddresses = ref(false)

// Mock 地址数据（API 失败时降级使用）
const mockAddresses = [
  {
    id: 1,
    receiverName: '张三',
    phone: '138****8888',
    detailAddress: '广东省 广州市 天河区 示例路 123 号极简创意园区 A 栋 404',
    isDefault: true
  },
  {
    id: 2,
    receiverName: '李四',
    phone: '139****9999',
    detailAddress: '北京市 朝阳区 三里屯 SOHO 2 号楼 1208',
    isDefault: false
  }
]

const statusMap = {
  'PENDING': '待支付',
  'PAID': '已支付',
  'SHIPPED': '已发货',
  'DONE': '已完成'
}

// 获取地址列表
const fetchAddresses = async () => {
  loadingAddresses.value = true
  try {
    const response = await userService.getAddresses()
    addresses.value = response.data || response || []
    console.log('获取地址列表成功:', addresses.value)
  } catch (error) {
    console.error('获取地址列表失败，使用 Mock 数据:', error)
    addresses.value = mockAddresses
  } finally {
    loadingAddresses.value = false
  }
}

// 打开地址弹窗（新增或编辑）
const openAddressModal = (address = null) => {
  if (address) {
    editingAddress.value = { ...address }
    isNewAddress.value = false
  } else {
    editingAddress.value = {
      id: null,
      receiverName: '',
      phone: '',
      detailAddress: '',
      isDefault: false
    }
    isNewAddress.value = true
  }
  showAddressModal.value = true
}

// 保存地址
const handleSaveAddress = async (addressData) => {
  try {
    if (isNewAddress.value) {
      // 新增地址
      const response = await userService.addAddress(addressData)
      console.log('新增地址成功:', response)
      await fetchAddresses() // 重新加载地址列表
    } else {
      // 编辑地址
      const response = await userService.updateAddress(addressData.id, addressData)
      console.log('更新地址成功:', response)
      await fetchAddresses() // 重新加载地址列表
    }
    showAddressModal.value = false
  } catch (error) {
    console.error('保存地址失败:', error)
    alert('保存地址失败，请重试')
  }
}

// 删除地址
const deleteAddress = async (addressId) => {
  if (!confirm('确定要删除这个地址吗？')) {
    return
  }
  
  try {
    await userService.deleteAddress(addressId)
    console.log('删除地址成功:', addressId)
    await fetchAddresses() // 重新加载地址列表
  } catch (error) {
    console.error('删除地址失败:', error)
    alert('删除地址失败，请重试')
  }
}

// 设为默认地址
const setDefaultAddress = async (addressId) => {
  try {
    await userService.setDefaultAddress(addressId)
    console.log('设置默认地址成功:', addressId)
    await fetchAddresses() // 重新加载地址列表
  } catch (error) {
    console.error('设置默认地址失败:', error)
    alert('设置默认地址失败，请重试')
  }
}

// 查看订单详情
const viewOrderDetail = async (order) => {
  try {
    // 调用订单详情接口获取完整数据
    const response = await orderService.getOrderDetail(order.id)
    const detailData = response.data || {}
    
    selectedOrder.value = {
      ...detailData,
      status: mapBackendStatus(detailData.status),
      shippingAddress: detailData.receiverAddress, // 映射地址字段
      items: detailData.items || []
    }
    showOrderDetail.value = true
  } catch (error) {
    console.error('获取订单详情失败，使用列表数据:', error)
    // 失败时使用列表中的数据（也需要适配字段）
    selectedOrder.value = {
      ...order,
      shippingAddress: order.receiverAddress || order.shippingAddress // 尝试获取地址
    }
    showOrderDetail.value = true
  }
}

onMounted(async () => {
  // 为了测试方便，暂时不检查登录状态
  // if (!userStore.isLoggedIn) {
  //   router.push('/login')
  //   return
  // }
  
  console.log('用户个人中心页面加载完成')
  
  // 加载地址列表
  await fetchAddresses()
  // 加载订单列表
  await fetchOrders()
})

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}

// 切换角色（用于测试）
const toggleRole = () => {
  userStore.toggleAdminMode()
  alert(`已切换为 ${userStore.isAdmin ? '管理员' : '普通用户'} 角色`)
}
</script>