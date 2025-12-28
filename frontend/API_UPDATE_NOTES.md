# API 规范更新说明

## 📝 更新日期
2025-12-25

## 🔄 主要改进

### 1. **新增核心接口** (5个)

#### 订单模块 (2个新接口)
- ✅ `POST /orders/{orderId}/cancel` - 取消订单（PENDING 状态）
- ✅ `POST /orders/{orderId}/confirm` - 确认收货（SHIPPED → DONE）

#### 购物车模块 (2个新接口)
- ✅ `DELETE /cart/items/{productId}` - 删除单个商品
- ✅ `PATCH /cart/items/{productId}` - 更新商品数量

#### 管理员模块 (1个新接口)
- ✅ `POST /admin/upload` - 图片上传（multipart/form-data）

---

### 2. **命名规范统一**

#### 用户 ID 字段
- **修改前**: 注册返回 `userId`，登录返回 `id`
- **修改后**: 统一使用 `id`

#### 时间字段
- **修改前**: 混用 `createTime` 和 `createdAt`
- **修改后**: 统一使用 `createdAt` 和 `updatedAt`

#### 响应格式
- **修改前**: 部分接口缺少 `message` 字段
- **修改后**: 所有接口必须包含 `code`、`message`、`data` 三个字段

---

### 3. **数据类型规范**

#### 金额字段
- **类型**: `BigDecimal`（后端）
- **精度**: 精确到分（两位小数）
- **前端处理**: 接收时除以 100 显示为元

#### 订单状态枚举
- 新增: `CANCELLED` (已取消)
- 完整枚举: `PENDING` | `PAID` | `SHIPPED` | `DONE` | `CANCELLED`

---

## 📊 接口统计更新

| 模块 | 原接口数 | 新增 | 更新后总数 |
|-----|---------|-----|----------|
| 认证模块 | 3 | 0 | 3 |
| 商品模块 | 4 | 0 | 4 |
| 订单模块 | 4 | +2 | **6** |
| 购物车模块 | 1 | +2 | **3** |
| 用户模块 | 6 | 0 | 6 |
| 管理员模块 | 8 | +1 | **9** |
| **总计** | 26 | +5 | **31** |

---

## 🔧 前端代码更新

### 新增文件
1. `src/api/cartApi.js` - 购物车 API 服务（独立于 store）

### 更新文件
1. **src/api/order.js**
   - 新增 `cancelOrder(orderId)` 方法
   - 新增 `confirmOrder(orderId)` 方法

2. **src/api/admin.js**
   - 新增 `uploadImage(file)` 方法

3. **src/components/OrderDetailModal.vue**
   - 新增"取消订单"按钮（PENDING 状态）
   - 新增"确认收货"按钮（SHIPPED 状态）
   - 集成 API 调用和错误处理

4. **src/store/cart.js**
   - 修改导入：`orderService` → `cartApiService`
   - 保持现有防抖同步逻辑

### API 使用示例

#### 取消订单
```javascript
import { orderService } from '@/api/order'

await orderService.cancelOrder('ORD202512250001')
```

#### 确认收货
```javascript
import { orderService } from '@/api/order'

await orderService.confirmOrder('ORD202512250001')
```

#### 上传图片
```javascript
import { adminService } from '@/api/admin'

const file = event.target.files[0]
const response = await adminService.uploadImage(file)
console.log('图片 URL:', response.data.url)
```

#### 购物车单项操作
```javascript
import { cartApiService } from '@/api/cartApi'

// 删除商品
await cartApiService.removeItem(productId)

// 更新数量
await cartApiService.updateQuantity(productId, 5)
```

---

## ⚠️ 后端开发注意事项

### 1. 金额处理
```java
// 正确做法：使用 BigDecimal
private BigDecimal price; // 单位：分

// 前端发送：59.99 元
// 后端存储：5999 分（BigDecimal）
// 返回给前端：5999（前端除以 100 显示）
```

### 2. 订单状态流转
```
PENDING → PAID → SHIPPED → DONE
   ↓
CANCELLED (仅 PENDING 状态可取消)
```

### 3. 图片上传
- 支持文件类型: `jpg`, `jpeg`, `png`, `gif`
- 最大文件大小: 建议 5MB
- 返回完整 URL（含协议和域名）

### 4. 响应格式示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 业务数据
  }
}
```

---

## 📋 待办事项

### 后端实现
- [ ] 实现取消订单接口
- [ ] 实现确认收货接口
- [ ] 实现购物车单项操作接口
- [ ] 实现图片上传接口（建议使用 OSS）
- [ ] 统一所有响应格式（添加 message 字段）
- [ ] 金额字段改用 BigDecimal 类型

### 前端优化
- [x] 订单详情模态框集成新功能
- [x] 购物车 API 服务独立化
- [x] 更新 API 文档
- [ ] 商品编辑表单集成图片上传
- [ ] 购物车使用单项操作接口（可选优化）

---

## 🔗 相关文档
- [API 后端规范文档](./API_BACKEND_SPEC.md)
- [API 集成检查清单](./API_INTEGRATION_CHECKLIST.md)

---

## 📞 联系方式
如有疑问，请联系前端开发团队。
