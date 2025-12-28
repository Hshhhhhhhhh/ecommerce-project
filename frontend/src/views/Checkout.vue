<template>
  <div class="max-w-4xl mx-auto py-12 px-4">
    <div v-if="!isPaid">
      <h2 class="text-3xl font-black text-gray-800 mb-8 flex items-center gap-3">
        确认订单
      </h2>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 space-y-6">
          <!-- 地址显示区域 -->
          <div class="bg-white p-6 rounded-3xl shadow-card border border-gray-50">
            <div class="flex justify-between items-center mb-4">
              <h3 class="font-bold text-gray-800">收货地址</h3>
              <button @click="openAddressModal(null)" class="text-blue-600 text-sm font-bold hover:underline">
                + 新增地址
              </button>
            </div>
            
            <!-- 地址列表 (可滑动选择) -->
            <div v-if="addresses.length > 0" class="space-y-3 max-h-60 overflow-y-auto pr-2">
              <div 
                v-for="addr in addresses" 
                :key="addr.id"
                @click="selectAddress(addr)"
                :class="[
                  'p-4 rounded-2xl border-2 cursor-pointer transition-all relative',
                  activeAddress.id === addr.id 
                    ? 'border-blue-500 bg-blue-50/30' 
                    : 'border-gray-100 hover:border-gray-200'
                ]"
              >
                <div class="flex justify-between items-start">
                  <div>
                    <p class="font-bold text-gray-800 text-lg">
                      {{ addr.receiverName }} 
                      <span class="text-gray-400 font-medium ml-2 text-sm">{{ addr.phone }}</span>
                      <span v-if="addr.isDefault" class="ml-2 px-2 py-0.5 bg-gray-100 text-gray-500 text-xs rounded-full">默认</span>
                    </p>
                    <p class="text-gray-500 text-sm mt-1">
                      {{ addr.province }} {{ addr.city }} {{ addr.district }} {{ addr.detailAddress }}
                    </p>
                  </div>
                  <div v-if="activeAddress.id === addr.id" class="text-blue-500">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" viewBox="0 0 20 20" fill="currentColor">
                      <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                    </svg>
                  </div>
                </div>
              </div>
            </div>

            <!-- 无地址状态 -->
            <div v-else class="text-center py-8 bg-gray-50 rounded-2xl border border-dashed border-gray-200">
              <p class="text-gray-400 mb-4">暂无收货地址</p>
              <button @click="openAddressModal(null)" class="px-6 py-2 bg-gray-900 text-white rounded-xl font-bold text-sm hover:bg-black transition-colors">
                添加新地址
              </button>
            </div>
          </div>

          <!-- 商品清单 -->
          <div class="bg-white p-6 rounded-3xl shadow-card border border-gray-50">
            <h3 class="font-bold text-gray-800 mb-4">商品清单</h3>
            <div class="space-y-4">
              <div v-for="item in cartStore.items" :key="item.id" class="flex items-center gap-4">
                <img :src="item.image || item.img || item.imageUrl" class="w-16 h-16 rounded-xl object-cover" />
                <div class="flex-1">
                  <p class="font-bold text-gray-800 text-sm">{{ item.name }}</p>
                  <p class="text-xs text-gray-400">数量：{{ item.quantity }}</p>
                </div>
                <p class="font-bold text-gray-800">￥{{ (item.price * item.quantity).toLocaleString() }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 结算卡片 -->
        <div class="lg:col-span-1">
          <div class="bg-white p-8 rounded-3xl shadow-card border border-gray-50 sticky top-24">
            <h3 class="font-bold text-xl mb-6 text-gray-800">支付详情</h3>
            <div class="space-y-4 mb-8">
              <div class="flex justify-between text-gray-500 text-sm">
                <span>商品总额</span>
                <span class="text-gray-800">￥{{ cartStore.totalPrice.toLocaleString() }}</span>
              </div>
              <div class="flex justify-between text-gray-500 text-sm">
                <span>运费</span>
                <span class="text-green-500 font-bold">免费</span>
              </div>
              <div class="h-px bg-gray-100 my-4"></div>
              <div class="flex justify-between items-center">
                <span class="font-bold text-gray-800">应付合计</span>
                <span class="text-2xl font-black text-red-500">￥{{ cartStore.totalPrice.toLocaleString() }}</span>
              </div>
            </div>
            <button 
              @click="handlePay" 
              :disabled="loading" 
              class="w-full bg-green-500 text-white py-4 rounded-2xl font-bold hover:bg-green-600 transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ loading ? '处理中...' : '模拟支付' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付成功 -->
    <div v-else class="text-center py-20 bg-white rounded-[40px] shadow-card border border-gray-50 max-w-2xl mx-auto">
      <div class="w-20 h-20 bg-green-50 text-green-500 rounded-full flex items-center justify-center mx-auto mb-6">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-10 w-10" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <h2 class="text-4xl font-black text-gray-800 mb-4">支付成功！</h2>
      <button @click="router.push('/')" class="bg-blue-600 text-white px-8 py-4 rounded-2xl font-bold hover:bg-blue-700 transition-all">
        返回首页
      </button>
    </div>

    <!-- 独立的地址管理模拟组件 -->
    <AddressModal 
      v-if="showAddressModal" 
      :address="editingAddress"
      :isNew="!editingAddress.id"
      @close="showAddressModal = false"
      @save="handleSaveAddress"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cart'
import { useUserStore } from '../store/user'
import { userService } from '../api/user'
import { orderService } from '../api/order'
import AddressModal from '../components/AddressModal.vue'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const loading = ref(false)
const isPaid = ref(false)
const showAddressModal = ref(false)
const addresses = ref([])
const loadingAddresses = ref(false)
const editingAddress = ref({})

// 当前选中的地址
const activeAddress = reactive({})

// 选择地址
const selectAddress = (addr) => {
  Object.assign(activeAddress, addr)
}

// 打开地址模态框
const openAddressModal = (addr) => {
  if (addr) {
    editingAddress.value = { ...addr }
  } else {
    editingAddress.value = {
      receiverName: '',
      phone: '',
      detailAddress: '',
      isDefault: false
    }
  }
  showAddressModal.value = true
}

// 保存地址（新增或修改）
const handleSaveAddress = async (addressData) => {
  try {
    if (addressData.id) {
      await userService.updateAddress(addressData)
    } else {
      await userService.addAddress(addressData)
    }
    showAddressModal.value = false
    await fetchAddresses() // 刷新列表
  } catch (error) {
    console.error('保存地址失败:', error)
    alert('保存地址失败，请重试')
  }
}

// 获取收货地址列表
const fetchAddresses = async () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: '/checkout' } })
    return
  }
  
  loadingAddresses.value = true
  try {
    const response = await userService.getAddresses()
    addresses.value = response.data || []
    
    // 逻辑：优先选中默认地址，如果没有默认地址，选中第一个
    if (addresses.value.length > 0) {
      const defaultAddr = addresses.value.find(addr => addr.isDefault)
      if (defaultAddr) {
        Object.assign(activeAddress, defaultAddr)
      } else {
        Object.assign(activeAddress, addresses.value[0])
      }
    } else {
      // 清空选中状态
      for (const key in activeAddress) delete activeAddress[key]
    }
    
    console.log('收货地址加载成功:', addresses.value.length)
  } catch (err) {
    console.error('获取收货地址失败:', err)
    addresses.value = []
  } finally {
    loadingAddresses.value = false
  }
}

// 提交订单并支付
const handlePay = async () => {
  if (!activeAddress.id) {
    alert('请先选择或添加收货地址')
    return
  }
  
  if (cartStore.isEmpty) {
    alert('购物车为空')
    return
  }

  // 检查库存
  for (const item of cartStore.items) {
    if (item.stock !== undefined && item.quantity > item.stock) {
      alert(`商品 "${item.name}" 库存不足，仅剩 ${item.stock} 件，请调整数量`)
      return
    }
  }
  
  loading.value = true
  
  try {
    // 提交订单
    const orderData = {
      addressId: activeAddress.id,
      items: cartStore.items.map(item => ({
        productId: item.id,
        quantity: item.quantity,
        price: item.price
      }))
    }
    
    const orderResponse = await orderService.createOrder(orderData)
    // 兼容不同的返回结构
    const orderId = orderResponse.data?.orderId || orderResponse.data?.id || orderResponse.data
    
    if (!orderId) {
      console.error('Invalid order response:', orderResponse)
      throw new Error('订单创建失败：未返回订单ID')
    }
    
    console.log('订单创建成功:', orderId)
    
    // 模拟支付
    await orderService.payOrder(orderId)
    console.log('支付成功')
    
    // 清空购物车
    cartStore.clearCart()
    
    // 显示支付成功
    isPaid.value = true
  } catch (err) {
    console.error('订单提交失败:', err)
    const msg = err.response?.data?.message || err.message || '订单提交失败，请重试'
    alert(msg)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  // 检查购物车是否为空
  if (cartStore.isEmpty) {
    alert('购物车为空')
    router.push('/cart')
    return
  }
  
  fetchAddresses()
})
</script>