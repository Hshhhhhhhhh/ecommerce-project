<template>
  <div class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
    <div class="bg-white rounded-[32px] w-full max-w-2xl p-8 shadow-2xl animate-in fade-in zoom-in duration-300 max-h-[90vh] overflow-y-auto">
      <!-- 头部 -->
      <div class="flex justify-between items-start mb-6">
        <div>
          <h3 class="text-2xl font-black text-gray-800">订单详情</h3>
          <p class="text-sm text-gray-400 mt-1">订单号: {{ order.id }}</p>
        </div>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <!-- 订单状态 -->
      <div class="bg-gradient-to-r from-blue-50 to-indigo-50 rounded-2xl p-6 mb-6">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500 mb-1">订单状态</p>
            <p 
              :class="{
                'text-yellow-600': order.status === 'PENDING',
                'text-green-600': order.status === 'PAID',
                'text-blue-600': order.status === 'SHIPPED',
                'text-gray-600': order.status === 'DONE'
              }"
              class="text-2xl font-black"
            >
              {{ statusMap[order.status] }}
            </p>
          </div>
          <div class="text-right">
            <p class="text-sm text-gray-500 mb-1">下单时间</p>
            <p class="text-base font-bold text-gray-800">{{ order.createTime }}</p>
          </div>
        </div>
      </div>

      <!-- 收货信息 -->
      <div class="border border-gray-100 rounded-2xl p-6 mb-6">
        <h4 class="font-bold text-gray-800 mb-4 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
          </svg>
          收货信息
        </h4>
        <div class="space-y-2 text-sm">
          <div class="flex">
            <span class="text-gray-500 w-20">收货人:</span>
            <span class="text-gray-800 font-medium">{{ order.receiverName }}</span>
          </div>
          <div class="flex">
            <span class="text-gray-500 w-20">联系电话:</span>
            <span class="text-gray-800 font-medium">{{ order.receiverPhone }}</span>
          </div>
          <div class="flex">
            <span class="text-gray-500 w-20">收货地址:</span>
            <span class="text-gray-800 font-medium">{{ order.shippingAddress }}</span>
          </div>
        </div>
      </div>

      <!-- 商品清单 -->
      <div class="border border-gray-100 rounded-2xl p-6 mb-6">
        <h4 class="font-bold text-gray-800 mb-4 flex items-center gap-2">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-blue-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
          </svg>
          商品清单
        </h4>
        <div class="space-y-4">
          <div 
            v-for="item in order.items" 
            :key="item.id"
            class="flex items-center gap-4 p-3 bg-gray-50 rounded-xl"
          >
            <img 
              :src="item.image || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200'" 
              class="w-16 h-16 rounded-lg object-cover"
              alt="商品图片"
            />
            <div class="flex-1">
              <p class="font-bold text-gray-800 text-sm">{{ item.name }}</p>
              <p class="text-xs text-gray-400 mt-1">数量: {{ item.quantity }}</p>
            </div>
            <div class="text-right">
              <p class="font-black text-gray-900">￥{{ item.price.toLocaleString() }}</p>
              <p class="text-xs text-gray-400 mt-1">小计: ￥{{ (item.price * item.quantity).toLocaleString() }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 费用明细 -->
      <div class="bg-gray-50 rounded-2xl p-6">
        <h4 class="font-bold text-gray-800 mb-4">费用明细</h4>
        <div class="space-y-3">
          <div class="flex justify-between text-sm">
            <span class="text-gray-600">商品总额</span>
            <span class="text-gray-800 font-medium">￥{{ order.totalAmount.toLocaleString() }}</span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-600">运费</span>
            <span class="text-green-600 font-bold">免运费</span>
          </div>
          <div class="h-px bg-gray-200 my-2"></div>
          <div class="flex justify-between items-center">
            <span class="text-gray-800 font-bold">实付金额</span>
            <span class="text-2xl font-black text-red-500">￥{{ order.totalAmount.toLocaleString() }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="mt-6 flex gap-3">
        <button 
          @click="$emit('close')"
          class="flex-1 py-3 font-bold text-gray-600 hover:bg-gray-50 rounded-xl transition-colors"
        >
          关闭
        </button>
        <button 
          v-if="order.status === 'PENDING'"
          @click="handleCancelOrder"
          class="flex-1 py-3 font-bold bg-red-600 text-white rounded-xl shadow-lg shadow-red-100 hover:bg-red-700 transition-all"
        >
          取消订单
        </button>
        <button 
          v-if="order.status === 'SHIPPED'"
          @click="handleConfirmOrder"
          class="flex-1 py-3 font-bold bg-green-600 text-white rounded-xl shadow-lg shadow-green-100 hover:bg-green-700 transition-all"
        >
          确认收货
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { orderService } from '../api/order'

const props = defineProps({
  order: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['close', 'refresh'])

const statusMap = {
  'PENDING': '待支付',
  'PAID': '已支付',
  'SHIPPED': '已发货',
  'DONE': '已完成',
  'CANCELLED': '已取消'
}

// 取消订单
const handleCancelOrder = async () => {
  if (!confirm('确定要取消这个订单吗？')) {
    return
  }
  
  try {
    await orderService.cancelOrder(props.order.id)
    alert('订单已取消')
    emit('refresh') // 通知父组件刷新订单列表
    emit('close')
  } catch (error) {
    console.error('取消订单失败:', error)
    alert('取消订单失败，请重试')
  }
}

// 确认收货
const handleConfirmOrder = async () => {
  if (!confirm('确认已收到货物？')) {
    return
  }
  
  try {
    await orderService.confirmOrder(props.order.id)
    alert('确认收货成功')
    emit('refresh') // 通知父组件刷新订单列表
    emit('close')
  } catch (error) {
    console.error('确认收货失败:', error)
    alert('确认收货失败，请重试')
  }
}
</script>

<style scoped>
/* 自定义滚动条 */
.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #cbd5e0;
  border-radius: 10px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}
</style>
