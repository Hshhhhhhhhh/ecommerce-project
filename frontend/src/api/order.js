import apiClient from './request'

/**
 * 订单与购物车服务
 */
export const orderService = {
  /**
   * 同步购物车到后端
   */
  async syncCart(items) {
    return apiClient.post('/cart/sync', { items });
  },

  /**
   * 提交订单
   * @param {Object} data - { addressId, items }
   */
  async createOrder(data) {
    return apiClient.post('/orders', data);
  },

  /**
   * 模拟支付
   */
  async payOrder(orderId) {
    return apiClient.post(`/orders/${orderId}/pay`);
  },

  /**
   * 获取订单列表
   * @param {Object} params - { page, size }
   */
  async getOrders(params = {}) {
    return apiClient.get('/orders', { params });
  },

  /**
   * 获取订单详情
   * @param {number|string} orderId - 订单ID
   */
  async getOrderDetail(orderId) {
    return apiClient.get(`/orders/${orderId}`);
  },

  /**
   * 取消订单
   * @param {number|string} orderId - 订单ID
   * @说明 只能取消 PENDING 状态的订单
   */
  async cancelOrder(orderId) {
    return apiClient.post(`/orders/${orderId}/cancel`);
  },

  /**
   * 确认收货
   * @param {number|string} orderId - 订单ID
   * @说明 将订单状态从 SHIPPED 改为 DONE
   */
  async confirmOrder(orderId) {
    return apiClient.post(`/orders/${orderId}/confirm`);
  }
};