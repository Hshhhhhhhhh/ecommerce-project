import apiClient from './request'

/**
 * 商品相关 API 服务
 */
export const productService = {
  /**
   * 获取所有商品分类
   * 对应接口：GET /products/categories
   */
  async getCategories() {
    return apiClient.get('/products/categories');
  },

  /**
   * 搜索商品列表
   * @param {Object} params - 搜索参数
   * @param {string} params.keyword - 关键词
   * @param {number} [params.categoryId] - 分类ID
   * @param {string} [params.sort] - 排序方式 (price_asc, price_desc)
   * @param {number} [params.page=1] - 当前页码
   * @param {number} [params.size=20] - 每页数量
   * 对应接口：GET /products/search
   */
  async searchProducts(params) {
    const defaultParams = { page: 1, size: 20, ...params };
    return apiClient.get('/products/search', { params: defaultParams });
  },

  /**
   * 检查商品库存
   * @param {number|string} productId - 商品ID
   * @param {number} quantity - 购买数量
   */
  async checkStock(productId, quantity) {
    return apiClient.post('/products/check-stock', { productId, quantity });
  },

  /**
   * 获取商品详情
   * @param {number|string} id - 商品唯一标识
   * 对应接口：GET /products/{id}
   */
  async getProductDetail(id) {
    return apiClient.get(`/products/${id}`);
  }
};