<template>
  <div class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
    <div class="bg-white rounded-[32px] w-full max-w-2xl p-8 shadow-2xl animate-in fade-in zoom-in duration-300 max-h-[90vh] overflow-y-auto">
      <!-- 头部 -->
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-black text-gray-800">{{ isEdit ? '编辑商品' : '发布新商品' }}</h3>
        <button @click="$emit('close')" class="text-gray-400 hover:text-gray-600">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-6">
        <!-- 商品名称 -->
        <div class="space-y-2">
          <label class="text-sm font-bold text-gray-700">商品名称 *</label>
          <input 
            v-model="formData.name" 
            type="text"
            placeholder="请输入商品名称" 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
            required
          />
        </div>

        <!-- 商品分类 -->
        <div class="space-y-2">
          <label class="text-sm font-bold text-gray-700">商品分类 *</label>
          <input 
            v-model="formData.categoryName" 
            type="text"
            placeholder="例如：数码产品、音频配件" 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
            required
          />
        </div>

        <!-- 价格和库存 -->
        <div class="grid grid-cols-2 gap-4">
          <div class="space-y-2">
            <label class="text-sm font-bold text-gray-700">价格 (元) *</label>
            <input 
              v-model.number="formData.price" 
              type="number"
              min="0"
              step="0.01"
              placeholder="0.00" 
              class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
              required
            />
          </div>
          <div class="space-y-2">
            <label class="text-sm font-bold text-gray-700">库存 *</label>
            <input 
              v-model.number="formData.stock" 
              type="number"
              min="0"
              placeholder="0" 
              class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
              required
            />
          </div>
        </div>

        <!-- 商品描述 -->
        <div class="space-y-2">
          <label class="text-sm font-bold text-gray-700">商品描述</label>
          <textarea 
            v-model="formData.description" 
            placeholder="请输入商品详细描述..." 
            class="w-full bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none h-32 resize-none"
          ></textarea>
        </div>

        <!-- 商品图片上传 -->
        <div class="space-y-2">
          <label class="text-sm font-bold text-gray-700">商品图片 *</label>
          
          <!-- 上传按钮区域 -->
          <div class="flex gap-3">
            <input 
              v-model="formData.imageUrl" 
              type="text"
              placeholder="输入图片URL或上传图片" 
              class="flex-1 bg-gray-50 border-none rounded-xl py-3 px-4 focus:ring-2 focus:ring-blue-500 outline-none" 
              required
            />
            <label class="relative cursor-pointer">
              <input 
                type="file" 
                accept="image/*" 
                @change="handleFileSelect"
                class="hidden"
              />
              <span class="inline-flex items-center gap-2 px-6 py-3 bg-gray-900 text-white font-bold rounded-xl hover:bg-black transition-all active:scale-95 whitespace-nowrap">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                {{ uploading ? '上传中...' : '本地上传' }}
              </span>
            </label>
          </div>
          
          <p class="text-xs text-gray-400 ml-1">可直接输入图片URL，或点击按钮上传本地图片</p>
          
          <!-- 上传进度 -->
          <div v-if="uploading" class="bg-blue-50 rounded-xl p-3">
            <div class="flex items-center gap-2 text-blue-600 text-sm font-bold">
              <svg class="animate-spin h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              正在上传图片，请稍候...
            </div>
          </div>
          
          <!-- 上传错误提示 -->
          <div v-if="uploadError" class="bg-red-50 rounded-xl p-3">
            <p class="text-red-600 text-sm font-bold">{{ uploadError }}</p>
          </div>
        </div>

        <!-- 图片预览 -->
        <div v-if="formData.imageUrl" class="space-y-2">
          <label class="text-sm font-bold text-gray-700">图片预览</label>
          <img 
            :src="getImageUrl(formData.imageUrl)" 
            class="w-full h-48 object-cover rounded-2xl bg-gray-100"
            @error="imageError = true"
            alt="商品预览"
          />
          <p v-if="imageError" class="text-xs text-red-500 ml-1">图片加载失败，请检查 URL</p>
        </div>

        <!-- 操作按钮 -->
        <div class="flex gap-4 mt-8">
          <button 
            type="button"
            @click="$emit('close')" 
            class="flex-1 py-3 font-bold text-gray-600 hover:bg-gray-50 rounded-xl transition-colors"
          >
            取消
          </button>
          <button 
            type="submit"
            class="flex-1 py-3 font-bold bg-blue-600 text-white rounded-xl shadow-lg shadow-blue-100 hover:bg-blue-700 transition-all"
            :disabled="loading"
          >
            {{ loading ? '保存中...' : (isEdit ? '保存修改' : '发布商品') }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { adminService } from '../api/admin'

const props = defineProps({
  product: {
    type: Object,
    default: () => null
  },
  isEdit: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'save'])

const loading = ref(false)
const imageError = ref(false)
const uploading = ref(false)
const uploadError = ref('')

const getImageUrl = (url) => {
  if (!url) return ''
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}

// 初始化表单数据
const formData = reactive({
  id: null,
  name: '',
  categoryName: '',
  price: 0,
  stock: 0,
  description: '',
  imageUrl: ''
})

// 如果是编辑模式，填充现有数据
if (props.product) {
  Object.assign(formData, props.product)
}

// 监听图片 URL 变化，重置错误状态
watch(() => formData.imageUrl, () => {
  imageError.value = false
})

/**
 * 处理文件选择
 */
const handleFileSelect = async (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    uploadError.value = '请选择图片文件'
    return
  }

  // 验证文件大小（限制5MB）
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    uploadError.value = '图片大小不能超过 5MB'
    return
  }

  uploadError.value = ''
  uploading.value = true

  try {
    const response = await adminService.uploadImage(file)
    
    // 上传成功，填充URL
    // 后端返回格式: { code: 200, data: "http://...", message: "..." }
    // request.js 拦截器返回的是 response.data (即整个 JSON body)
    // 所以这里的 response 就是 { code, data, message }
    if (response.data) {
      formData.imageUrl = response.data
      uploadError.value = ''
    } else {
      uploadError.value = '上传失败：未返回图片URL'
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    uploadError.value = error.response?.data?.message || '上传失败，请重试'
  } finally {
    uploading.value = false
    // 清空 input，允许重复上传同一文件
    event.target.value = ''
  }
}

const handleSubmit = () => {
  // 基本验证
  if (!formData.name || formData.price <= 0 || formData.stock < 0 || !formData.imageUrl) {
    alert('请填写所有必填项')
    return
  }

  if (imageError.value) {
    alert('图片加载失败，请检查图片 URL')
    return
  }

  loading.value = true
  
  // 发送数据给父组件
  setTimeout(() => {
    // 确保 categoryId 存在，如果只有 categoryName，需要后端处理或前端映射
    // 这里假设后端需要 categoryId，但前端目前只有 categoryName
    // 临时解决方案：如果 categoryId 为空，尝试从 props.product 中获取，或者设为默认值 1
    if (!formData.categoryId) {
        formData.categoryId = props.product?.categoryId || props.product?.category?.id || 1;
    }
    emit('save', { ...formData })
    loading.value = false
  }, 300)
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
