import apiClient from './request'

/**
 * 购物车 API 服务（用于后端同步）
 */
export const cartApiService = {
  /**
   * 获取购物车列表
   */
  async getCart() {
    return apiClient.get('/cart')
  },

  /**
   * 同步购物车到后端
   * @param {Array} items - 购物车商品列表
   */
  async syncCart(items) {
    return apiClient.post('/cart/sync', { items })
  },

  /**
   * 删除购物车商品
   * @param {number} productId - 商品ID
   */
  async removeItem(productId) {
    return apiClient.delete(`/cart/items/${productId}`)
  },

  /**
   * 更新购物车商品数量
   * @param {number} productId - 商品ID
   * @param {number} quantity - 新数量
   */
  async updateQuantity(productId, quantity) {
    return apiClient.patch(`/cart/items/${productId}`, { quantity })
  }
}
