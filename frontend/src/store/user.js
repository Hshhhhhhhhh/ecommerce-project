import { defineStore } from 'pinia'

// 持久化相关常量
const TOKEN_KEY = 'auth_token'
const USER_INFO_KEY = 'user_info'
const TOKEN_EXPIRES_KEY = 'token_expires'

export const useUserStore = defineStore('user', {
  state: () => ({
    // JWT Token
    token: localStorage.getItem(TOKEN_KEY) || null,
    
    // 用户信息
    userInfo: (() => {
      const stored = localStorage.getItem(USER_INFO_KEY)
      if (stored) {
        try {
          return JSON.parse(stored)
        } catch (e) {
          console.error('解析用户信息失败:', e)
          return null
        }
      }
      // 测试默认用户
      return {
        username: '演示用户',
        email: 'demo@example.com',
        avatar: '',
        role: 'ROLE_USER'
      }
    })(),
    
    // Token 过期时间（时间戳）
    tokenExpires: localStorage.getItem(TOKEN_EXPIRES_KEY) 
      ? parseInt(localStorage.getItem(TOKEN_EXPIRES_KEY)) 
      : null,
    
    // 登录状态
    isLoggedIn: !!localStorage.getItem(TOKEN_KEY)
  }),
  
  getters: {
    // 用户名
    username: (state) => state.userInfo?.username || '游客',
    
    // 用户邮箱
    email: (state) => state.userInfo?.email || '',
    
    // 用户头像
    avatar: (state) => state.userInfo?.avatar || '',
    
    // 是否是管理员
    isAdmin: (state) => state.userInfo?.role === 'ADMIN' || state.userInfo?.role === 'ROLE_ADMIN',
    
    // 用户角色
    userRole: (state) => state.userInfo?.role || null,
    
    // Token 是否有效（未过期）
    isTokenValid: (state) => {
      if (!state.token || !state.tokenExpires) return false
      return Date.now() < state.tokenExpires
    },
    
    // 获取 Authorization Header
    authHeader: (state) => {
      return state.token ? `Bearer ${state.token}` : ''
    }
  },
  
  actions: {
    /**
     * 用户登录
     * @param {Object} userInfo - 用户信息
     * @param {string} token - JWT Token
     * @param {number} expiresIn - Token有效期（秒），默认7天
     */
    login(userInfo, token, expiresIn = 7 * 24 * 60 * 60) {
      this.userInfo = userInfo
      this.token = token
      this.isLoggedIn = true
      
      // 计算过期时间
      const expires = Date.now() + expiresIn * 1000
      this.tokenExpires = expires
      
      // 持久化到 localStorage
      this._persistToStorage()
    },
    
    /**
     * 用户登出
     */
    logout() {
      this.userInfo = null
      this.token = null
      this.tokenExpires = null
      this.isLoggedIn = false
      
      // 清除 localStorage
      this._clearStorage()

      // 清空购物车 (动态导入避免循环依赖)
      import('./cart').then(({ useCartStore }) => {
        const cartStore = useCartStore()
        cartStore.clearCart()
      })
    },
    
    /**
     * 更新用户信息
     */
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      this._persistToStorage()
    },
    
    /**
     * 刷新 Token
     */
    refreshToken(newToken, expiresIn = 7 * 24 * 60 * 60) {
      this.token = newToken
      this.tokenExpires = Date.now() + expiresIn * 1000
      this._persistToStorage()
    },
    
    /**
     * 从本地存储恢复用户信息
     */
    restoreFromStorage() {
      try {
        const token = localStorage.getItem(TOKEN_KEY)
        const userInfo = localStorage.getItem(USER_INFO_KEY)
        const expires = localStorage.getItem(TOKEN_EXPIRES_KEY)
        
        if (token && userInfo) {
          this.token = token
          this.userInfo = JSON.parse(userInfo)
          this.tokenExpires = expires ? parseInt(expires) : null
          
          // 检查 Token 是否过期
          if (this.isTokenValid) {
            this.isLoggedIn = true
          } else {
            console.warn('Token 已过期，请重新登录')
            this.logout()
          }
        }
      } catch (error) {
        console.error('从本地存储恢复用户信息失败:', error)
        this.logout()
      }
    },
    
    /**
     * 切换管理员模式（仅用于测试）
     */
    toggleAdminMode() {
      if (!this.userInfo) return
      
      this.userInfo.role = this.userInfo.role === 'ROLE_ADMIN' 
        ? 'ROLE_USER' 
        : 'ROLE_ADMIN'
      
      this._persistToStorage()
    },
    
    /**
     * 持久化到 localStorage（私有方法）
     */
    _persistToStorage() {
      try {
        if (this.token) {
          localStorage.setItem(TOKEN_KEY, this.token)
        }
        if (this.userInfo) {
          localStorage.setItem(USER_INFO_KEY, JSON.stringify(this.userInfo))
        }
        if (this.tokenExpires) {
          localStorage.setItem(TOKEN_EXPIRES_KEY, this.tokenExpires.toString())
        }
      } catch (error) {
        console.error('保存用户信息到本地存储失败:', error)
      }
    },
    
    /**
     * 清除 localStorage（私有方法）
     */
    _clearStorage() {
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_INFO_KEY)
      localStorage.removeItem(TOKEN_EXPIRES_KEY)
    }
  }
})
