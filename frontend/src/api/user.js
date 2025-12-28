import apiClient from './request'

/**
 * 用户相关 API 服务
 */
export const userService = {
  /**
   * 获取用户收货地址列表
   */
  async getAddresses() {
    return apiClient.get('/user/address')
  },

  /**
   * 添加收货地址
   */
  async addAddress(addressData) {
    return apiClient.post('/user/address', addressData)
  },

  /**
   * 更新收货地址
   */
  async updateAddress(addressId, addressData) {
    return apiClient.put(`/user/address/${addressId}`, addressData)
  },

  /**
   * 删除收货地址
   */
  async deleteAddress(addressId) {
    return apiClient.delete(`/user/address/${addressId}`)
  },

  /**
   * 设置默认地址
   */
  async setDefaultAddress(addressId) {
    return apiClient.put(`/user/address/${addressId}/default`)
  },

  /**
   * 获取用户信息
   */
  async getUserProfile() {
    return apiClient.get('/user/profile')
  },

  /**
   * 更新用户信息
   */
  async updateUserProfile(userData) {
    return apiClient.put('/user/profile', userData)
  }
}
