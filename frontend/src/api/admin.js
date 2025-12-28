import apiClient from './request'

/**
 * 管理员 API 服务
 */
export const adminService = {
  /**
   * 上传图片
   * @param {File} file - 图片文件
   */
  async uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post('/admin/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  /**
   * 获取统计数据
   * @param {string} range - 时间范围 (today, month, year)
   */
  async getStats(range) {
    return apiClient.get('/admin/stats', { params: { range } })
  },

  /**
   * 发送确认收货邮件
   * @param {number} orderId - 订单ID
   */
  async sendConfirmationEmail(orderId) {
    return apiClient.post(`/orders/${orderId}/send-confirmation-email`)
  },

  /**
   * 获取商品列表（管理员）
   * @param {Object} params - { page, size, keyword }
   */
  async getProducts(params = {}) {
    return apiClient.get('/admin/products', { params })
  },

  /**
   * 创建商品
   * @param {Object} productData - 商品信息
   */
  async createProduct(productData) {
    return apiClient.post('/admin/products', productData)
  },

  /**
   * 更新商品
   * @param {number|string} productId - 商品ID
   * @param {Object} productData - 商品信息
   */
  async updateProduct(productId, productData) {
    return apiClient.put(`/admin/products/${productId}`, productData)
  },

  /**
   * 删除商品（下架）
   * @param {number|string} productId - 商品ID
   */
  async deleteProduct(productId) {
    return apiClient.delete(`/admin/products/${productId}`)
  },

  /**
   * 获取订单列表（管理员）
   * @param {Object} params - { page, size, status }
   */
  async getOrders(params = {}) {
    return apiClient.get('/admin/orders', { params })
  },

  /**
   * 更新订单状态
   * @param {number|string} orderId - 订单ID
   * @param {string} status - 订单状态
   */
  async updateOrderStatus(orderId, status) {
    // Backend only supports 'ship' action via specific endpoint
    if (status === 'shipped') {
      return apiClient.put(`/admin/orders/${orderId}/ship`)
    }
    // For other statuses (like cancelled), we might need another endpoint or logic
    // Assuming current backend only has shipOrder
    return Promise.reject(new Error('暂不支持此状态更新操作'))
  },

  /**
   * 获取用户列表
   * @param {Object} params - { page, size, keyword }
   */
  async getUsers(params = {}) {
    return apiClient.get('/admin/users', { params })
  }
}
