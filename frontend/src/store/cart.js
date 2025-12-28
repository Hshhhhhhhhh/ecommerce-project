import { defineStore } from 'pinia'
import { cartApiService } from '../api/cartApi'
import { useUserStore } from './user'

// 持久化相关常量
const CART_STORAGE_KEY = 'cart_items'

// 后端同步防抖定时器
let syncTimer = null

export const useCartStore = defineStore('cart', {
  state: () => ({
    // 从 localStorage 恢复购物车数据
    items: JSON.parse(localStorage.getItem(CART_STORAGE_KEY) || '[]'),
  }),
  
  getters: {
    // 购物车商品总数量
    totalCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    
    // 购物车总金额
    totalPrice: (state) => state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
    
    // 获取购物车是否为空
    isEmpty: (state) => state.items.length === 0
  },
  
  actions: {
    /**
     * 添加商品到购物车
     */
    addToCart(product) {
      const existing = this.items.find(i => i.id === product.id)
      if (existing) {
        existing.quantity++
      } else {
        this.items.push({ ...product, quantity: 1 })
      }
      this._persistAndSync()
    },
    
    /**
     * 从购物车移除商品
     */
    async removeFromCart(id) {
      this.items = this.items.filter(i => i.id !== id)
      
      // 如果已登录，调用后端删除接口
      const userStore = useUserStore()
      if (userStore.isLoggedIn) {
        try {
          await cartApiService.removeItem(id)
        } catch (error) {
          console.error('删除购物车商品失败:', error)
        }
      }

      this._persistAndSync()
    },
    
    /**
     * 更新商品数量
     */
    updateQuantity(id, newQty) {
      const item = this.items.find(i => i.id === id)
      if (item && newQty > 0) {
        item.quantity = newQty
        this._persistAndSync()
      } else if (newQty <= 0) {
        // 数量为0时移除商品
        this.removeFromCart(id)
      }
    },
    
    /**
     * 清空购物车
     */
    clearCart() {
      this.items = []
      this._persistToStorage()
    },
    
    /**
     * 持久化到 localStorage（私有方法）
     */
    _persistToStorage() {
      try {
        localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(this.items))
      } catch (error) {
        console.error('保存购物车到本地存储失败:', error)
      }
    },
    
    /**
     * 从 localStorage 恢复数据
     */
    restoreFromStorage() {
      try {
        const stored = localStorage.getItem(CART_STORAGE_KEY)
        if (stored) {
          this.items = JSON.parse(stored)
        }
      } catch (error) {
        console.error('从本地存储恢复购物车失败:', error)
        this.items = []
      }
    },

    /**
     * 持久化并尝试同步到后端
     */
    async _persistAndSync() {
      this._persistToStorage()
      
      const userStore = useUserStore()
      if (userStore.isLoggedIn) {
        if (syncTimer) clearTimeout(syncTimer)
        syncTimer = setTimeout(async () => {
          try {
            const syncItems = this.items.map(item => ({ 
              productId: item.id, 
              quantity: item.quantity 
            }))
            await cartApiService.syncCart(syncItems)
          } catch (error) {
            console.error('同步购物车失败:', error)
          }
        }, 500)
      }
    },

    /**
     * 从后端获取购物车并合并
     */
    async mergeCart() {
      const userStore = useUserStore()
      if (!userStore.isLoggedIn) return

      // 1. 先同步本地数据到后端
      if (this.items.length > 0) {
        try {
          const syncItems = this.items.map(item => ({ 
            productId: item.id, 
            quantity: item.quantity 
          }))
          await cartApiService.syncCart(syncItems)
        } catch (error) {
          console.error('合并购物车时同步失败:', error)
        }
      }

      // 2. 获取最新购物车数据
      try {
        const response = await cartApiService.getCart()
        if (response.data) {
          this.items = response.data.map(item => ({
            id: item.productId,
            name: item.name,
            imageUrl: item.imageUrl,
            price: item.price,
            quantity: item.quantity,
            stock: item.stock // 尝试从后端获取库存信息
          }))
          this._persistToStorage()
        }
      } catch (error) {
        console.error('获取购物车失败:', error)
      }
    }
  }
})