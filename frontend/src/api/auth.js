import apiClient from './request'

/**
 * 认证服务
 */
export const authService = {
  /**
   * 用户注册
   */
  async register(username, password, email) {
    return apiClient.post('/auth/register', { username, password, email });
  },

  /**
   * 用户登录
   */
  async login(username, password) {
    return apiClient.post('/auth/login', { username, password });
  },

  /**
   * 获取个人信息
   */
  async getProfile() {
    return apiClient.get('/user/profile');
  }
};