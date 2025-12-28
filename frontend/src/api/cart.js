import { defineStore } from 'pinia'
import { orderService } from '../api/order'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: JSON.parse(localStorage.getItem('cart_items')) || [],
  }),
  getters: {
    totalCount: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
  },
  actions: {
    addToCart(product) {
      const existing = this.items.find(i => i.id === product.id)
      if (existing) {
        existing.quantity++
      } else {
        this.items.push({ ...product, quantity: 1 })
      }
      this.saveAndSync();
    },
    removeFromCart(id) {
      this.items = this.items.filter(i => i.id !== id)
      this.saveAndSync();
    },
    // 保存本地并尝试同步到后端（如果已登录）
    async saveAndSync() {
      localStorage.setItem('cart_items', JSON.stringify(this.items));
      const token = localStorage.getItem('token');
      if (token) {
        try {
          await orderService.syncCart(this.items);
        } catch (e) {
          console.warn('购物车同步失败');
        }
      }
    },
    clearCart() {
      this.items = [];
      localStorage.removeItem('cart_items');
    }
  }
})