<template>
  <div class="group bg-white rounded-2xl shadow-card hover:shadow-card-hover transition-all duration-300 overflow-hidden border border-transparent hover:border-blue-100 flex flex-col h-full">
    <!-- 商品图片 -->
    <div class="relative aspect-square overflow-hidden bg-gray-100">
      <img 
        :src="getImageUrl(product)" 
        :alt="product.name"
        class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
        :class="{ 'grayscale opacity-70': product.stock <= 0 }"
      />
      <!-- 已售完遮罩 -->
      <div v-if="product.stock <= 0" class="absolute inset-0 flex items-center justify-center bg-black/30 z-10">
        <span class="bg-black/70 text-white px-3 py-1 rounded-lg text-sm font-bold">已售完</span>
      </div>
      <!-- 悬浮快捷加入按钮 -->
      <button 
        v-if="product.stock === undefined || product.stock > 0"
        @click.stop="$emit('add-to-cart', product)"
        class="absolute bottom-3 right-3 bg-white/90 backdrop-blur-sm p-2.5 rounded-xl shadow-lg text-blue-600 transform translate-y-12 opacity-0 group-hover:translate-y-0 group-hover:opacity-100 transition-all duration-300 hover:bg-blue-600 hover:text-white"
      >
        <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
      </button>
    </div>

    <!-- 商品信息 -->
    <div class="p-4 flex flex-col flex-1">
      <div class="flex items-center justify-between mb-2">
        <span class="text-[10px] font-bold uppercase tracking-wider text-blue-500 bg-blue-50 px-2 py-0.5 rounded-md">
          {{ product.categoryName || product.category }}
        </span>
        <div class="flex items-center text-yellow-400">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3 fill-current" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
          <span class="text-xs text-gray-400 ml-1">4.8</span>
        </div>
      </div>
      
      <h3 class="font-bold text-gray-800 text-sm mb-1 line-clamp-2 hover:text-blue-600 cursor-pointer">
        {{ product.name }}
      </h3>
      
      <p class="text-xs text-gray-400 mb-4 line-clamp-1 italic">
        {{ product.description }}
      </p>

      <div class="mt-auto flex items-end justify-between">
        <div class="flex flex-col">
          <span class="text-[10px] text-gray-400 line-through">￥{{ (product.price * 1.2).toFixed(0) }}</span>
          <span class="text-xl font-black text-red-500">
            <span class="text-sm font-bold">￥</span>{{ product.price }}
          </span>
          <span v-if="product.stock !== undefined" class="text-[10px] text-gray-400 mt-1">剩余: {{ product.stock }}</span>
        </div>
        <button 
          @click="$emit('view-detail', product)"
          class="text-xs font-bold text-gray-400 hover:text-blue-600 transition-colors flex items-center gap-1"
        >
          查看详情
          <svg xmlns="http://www.w3.org/2000/svg" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  product: Object
})
defineEmits(['add-to-cart', 'view-detail'])

const getImageUrl = (p) => {
  const url = p.imageUrl || p.image || p.img
  if (!url) return ''
  if (url.includes('localhost:8080')) {
    return url.replace(/https?:\/\/localhost:8080/, '')
  }
  return url
}
</script>