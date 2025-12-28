<template>
  <div class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
    <div class="bg-white rounded-[32px] w-full max-w-md p-8 shadow-2xl animate-in fade-in zoom-in duration-300">
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-xl font-black text-gray-800">{{ isNew ? '新增收货地址' : '编辑收货地址' }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <form @submit.prevent="onSave" class="space-y-4">
        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 ml-1 uppercase">收货人 *</label>
          <input 
            v-model="formData.receiverName" 
            type="text"
            placeholder="请输入收货人姓名" 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
            required
          />
        </div>
        
        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 ml-1 uppercase">联系电话 *</label>
          <input 
            v-model="formData.phone" 
            type="tel"
            placeholder="请输入手机号码" 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
            required
          />
        </div>

        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 ml-1 uppercase">所在地区 *</label>
          <div class="flex gap-2">
            <select 
              v-model="formData.province" 
              @change="handleProvinceChange"
              class="flex-1 bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none appearance-none"
              required
            >
              <option value="">选择省份</option>
              <option v-for="p in regions" :key="p.name" :value="p.name">{{ p.name }}</option>
            </select>
            
            <select 
              v-model="formData.city" 
              @change="handleCityChange"
              class="flex-1 bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none appearance-none"
              required
              :disabled="!formData.province"
            >
              <option value="">选择城市</option>
              <option v-for="c in availableCities" :key="c.name" :value="c.name">{{ c.name }}</option>
            </select>
            
            <select 
              v-model="formData.district" 
              class="flex-1 bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none appearance-none"
              required
              :disabled="!formData.city"
            >
              <option value="">选择区县</option>
              <option v-for="d in availableDistricts" :key="d.name" :value="d.name">{{ d.name }}</option>
            </select>
          </div>
        </div>

        <div class="space-y-1">
          <label class="text-[10px] font-bold text-gray-400 ml-1 uppercase">详细地址 *</label>
          <textarea 
            v-model="formData.detailAddress" 
            placeholder="街道、楼牌号等" 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none h-24 resize-none"
            required
          ></textarea>
        </div>

        <div class="flex items-center gap-2 py-2">
          <input 
            v-model="formData.isDefault" 
            type="checkbox" 
            id="isDefault"
            class="w-4 h-4 text-blue-600 rounded focus:ring-2 focus:ring-blue-500"
          />
          <label for="isDefault" class="text-sm font-medium text-gray-700 cursor-pointer">
            设为默认地址
          </label>
        </div>

        <div class="flex gap-3 mt-8">
          <button 
            type="button"
            @click="$emit('close')" 
            class="flex-1 py-3 font-bold text-gray-400 hover:bg-gray-50 rounded-xl transition-colors"
          >
            取消
          </button>
          <button 
            type="submit"
            class="flex-1 py-3 font-bold bg-blue-600 text-white rounded-xl shadow-lg shadow-blue-100 hover:bg-blue-700 transition-all"
          >
            {{ isNew ? '添加地址' : '保存修改' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { regions } from '../assets/regions'

const props = defineProps({
  address: {
    type: Object,
    default: () => ({
      id: null,
      receiverName: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      isDefault: false
    })
  },
  isNew: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

// 使用 props 初始值初始化表单
const formData = reactive({ 
  ...props.address,
  province: props.address.province || '',
  city: props.address.city || '',
  district: props.address.district || ''
})

// 计算可选城市
const availableCities = computed(() => {
  if (!formData.province) return []
  const provinceData = regions.find(p => p.name === formData.province)
  return provinceData ? provinceData.children : []
})

// 计算可选区县
const availableDistricts = computed(() => {
  if (!formData.city) return []
  const cityData = availableCities.value.find(c => c.name === formData.city)
  return cityData ? cityData.children : []
})

// 省份变化时重置城市和区县
const handleProvinceChange = () => {
  formData.city = ''
  formData.district = ''
}

// 城市变化时重置区县
const handleCityChange = () => {
  formData.district = ''
}

const onSave = () => {
  // 表单验证
  if (!formData.receiverName || !formData.phone || !formData.detailAddress || !formData.province || !formData.city || !formData.district) {
    alert('请填写完整的地址信息')
    return 
  }

  // 简单的手机号验证
  if (!/^1[3-9]\d{9}$/.test(formData.phone)) {
    alert('请输入正确的手机号码')
    return
  }

  emit('save', { ...formData })
}
</script>