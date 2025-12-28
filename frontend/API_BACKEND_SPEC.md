# 前端 API 接口文档

> **Base URL**: `http://localhost:8080/api/v1`  
> **认证方式**: JWT Token (Bearer Authentication)  
> **请求头**: `Authorization: Bearer <token>`

---

## 📋 目录
1. [认证模块 (Auth)](#1-认证模块-auth)
2. [商品模块 (Product)](#2-商品模块-product)
3. [订单模块 (Order)](#3-订单模块-order)
4. [购物车模块 (Cart)](#4-购物车模块-cart)
5. [用户模块 (User)](#5-用户模块-user)
6. [管理员模块 (Admin)](#6-管理员模块-admin)

---

## 1. 认证模块 (Auth)

### 1.1 用户注册
- **路径**: `POST /auth/register`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": "number",
    "username": "string"
  }
}
```

### 1.2 用户登录
- **路径**: `POST /auth/login`
- **权限**: 公开
- **请求体**:
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "string (JWT)",
    "expiresIn": "number (秒)",
    "user": {
      "id": "number",
      "username": "string",
      "role": "string (USER|ADMIN)"
    }
  }
}
```

### 1.3 获取个人信息
- **路径**: `GET /user/profile`
- **权限**: 需要登录
- **请求头**: `Authorization: Bearer <token>`
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "number",
    "username": "string",
    "role": "string",
    "email": "string (可选)",
    "phone": "string (可选)"
  }
}
```

---

## 2. 商品模块 (Product)

### 2.1 获取商品分类
- **路径**: `GET /products/categories`
- **权限**: 公开
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "number",
      "name": "string",
      "productCount": "number (可选)"
    }
  ]
}
```

### 2.2 搜索商品
- **路径**: `GET /products/search`
- **权限**: 公开
- **查询参数**:
  - `keyword` (string, 可选): 搜索关键词
  - `categoryId` (number, 可选): 分类ID
  - `sort` (string, 可选): 排序方式 (`price_asc` | `price_desc`)
  - `page` (number, 默认1): 当前页码
  - `size` (number, 默认20): 每页数量
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "number",
        "name": "string",
        "price": "number (BigDecimal, 精确到分)",
        "stock": "number",
        "categoryName": "string",
        "imageUrl": "string",
        "description": "string (可选)"
      }
    ],
    "total": "number",
    "page": "number",
    "size": "number"
  }
}
```

### 2.3 获取商品详情
- **路径**: `GET /products/{id}`
- **权限**: 公开
- **路径参数**: `id` (number) - 商品ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "number",
    "name": "string",
    "price": "number (BigDecimal, 精确到分)",
    "stock": "number",
    "categoryName": "string",
    "imageUrl": "string",
    "description": "string",
    "createdAt": "string (ISO 8601)",
    "updatedAt": "string (ISO 8601)"
  }
}
```

### 2.4 检查商品库存
- **路径**: `POST /products/check-stock`
- **权限**: 公开
- **请求体**:
```json
{
  "productId": "number",
  "quantity": "number"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "available": "boolean",
    "currentStock": "number"
  }
}
```

---

## 3. 订单模块 (Order)

### 3.1 创建订单
- **路径**: `POST /orders`
- **权限**: 需要登录
- **请求体**:
```json
{
  "addressId": "number",
  "items": [
    {
      "productId": "number",
      "quantity": "number"
    }
  ]
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "orderId": "string",
    "totalAmount": "number (BigDecimal, 精确到分)",
    "status": "string (PENDING)"
  }
}
```

### 3.2 支付订单
- **路径**: `POST /orders/{orderId}/pay`
- **权限**: 需要登录
- **路径参数**: `orderId` (string) - 订单ID
- **响应**:
```json
{
  "code": 200,
  "message": "支付成功",
  "data": {
    "orderId": "string",
    "status": "string (PAID)"
  }
}
```

### 3.3 获取订单列表
- **路径**: `GET /orders`
- **权限**: 需要登录
- **查询参数**:
  - `page` (number, 默认1): 当前页码
  - `size` (number, 默认10): 每页数量
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "string",
        "createdAt": "string (ISO 8601)",
        "status": "string (PENDING|PAID|SHIPPED|DONE)",
        "totalAmount": "number (BigDecimal, 精确到分)",
        "shippingAddress": "string",
        "receiverName": "string",
        "receiverPhone": "string"
      }
    ],
    "total": "number",
    "page": "number",
    "size": "number"
  }
}
```

### 3.4 获取订单详情
- **路径**: `GET /orders/{orderId}`
- **权限**: 需要登录
- **路径参数**: `orderId` (string) - 订单ID
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "string",
    "createdAt": "string (ISO 8601)",
    "status": "string",
    "totalAmount": "number (BigDecimal, 精确到分)",
    "shippingAddress": "string",
    "receiverName": "string",
    "receiverPhone": "string",
    "items": [
      {
        "id": "number",
        "name": "string",
        "price": "number (BigDecimal, 精确到分)",
        "quantity": "number",
        "image": "string"
      }
    ]
  }
}
```

---

## 4. 购物车模块 (Cart)

### 4.1 同步购物车
- **路径**: `POST /cart/sync`
- **权限**: 需要登录
- **请求体**:
```json
{
  "items": [
    {
      "id": "number",
      "name": "string",
      "price": "number (BigDecimal, 精确到分)",
      "quantity": "number",
      "imageUrl": "string"
    }
  ]
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "同步成功",
  "data": null
}
```

### 4.2 删除购物车商品
- **路径**: `DELETE /cart/items/{productId}`
- **权限**: 需要登录
- **路径参数**: `productId` (number) - 商品ID
- **响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 4.3 更新购物车商品数量
- **路径**: `PATCH /cart/items/{productId}`
- **权限**: 需要登录
- **路径参数**: `productId` (number) - 商品ID
- **请求体**:
```json
{
  "quantity": "number"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 5. 用户模块 (User)

### 5.1 获取收货地址列表
- **路径**: `GET /user/address`
- **权限**: 需要登录
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "number",
      "receiverName": "string",
      "phone": "string",
      "detailAddress": "string",
      "isDefault": "boolean"
    }
  ]
}
```

### 5.2 添加收货地址
- **路径**: `POST /user/address`
- **权限**: 需要登录
- **请求体**:
```json
{
  "receiverName": "string",
  "phone": "string",
  "detailAddress": "string",
  "isDefault": "boolean"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "id": "number",
    "receiverName": "string",
    "phone": "string",
    "detailAddress": "string",
    "isDefault": "boolean"
  }
}
```

### 5.3 更新收货地址
- **路径**: `PUT /user/address/{addressId}`
- **权限**: 需要登录
- **路径参数**: `addressId` (number) - 地址ID
- **请求体**:
```json
{
  "receiverName": "string",
  "phone": "string",
  "detailAddress": "string",
  "isDefault": "boolean"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 5.4 删除收货地址
- **路径**: `DELETE /user/address/{addressId}`
- **权限**: 需要登录
- **路径参数**: `addressId` (number) - 地址ID
- **响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 5.5 设置默认地址
- **路径**: `PUT /user/address/{addressId}/default`
- **权限**: 需要登录
- **路径参数**: `addressId` (number) - 地址ID
- **响应**:
```json
{
  "code": 200,
  "message": "设置成功",
  "data": null
}
```

### 5.6 更新用户信息
- **路径**: `PUT /user/profile`
- **权限**: 需要登录
- **请求体**:
```json
{
  "email": "string (可选)",
  "phone": "string (可选)"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 6. 管理员模块 (Admin)

> ⚠️ **所有管理员接口都需要 `ROLE_ADMIN` 权限**

### 6.1 上传图片
- **路径**: `POST /admin/upload`
- **权限**: 管理员
- **请求体**: `multipart/form-data`
  - `file`: 图片文件 (jpg, png, gif 等)
- **说明**: 上传商品图片到服务器或 OSS，返回可访问的 URL
- **响应**:
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "string (图片完整URL)",
    "filename": "string",
    "size": "number (字节)"
  }
}
```

### 6.2 获取统计数据
- **路径**: `GET /admin/stats`
- **权限**: 管理员
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "gmv": "string (格式化金额，如 '128,490.00')",
    "orderCount": "number",
    "userCount": "number"
  }
}
```

### 6.3 获取商品列表（管理员）
- **路径**: `GET /admin/products`
- **权限**: 管理员
- **查询参数**:
  - `page` (number, 可选): 当前页码
  - `size` (number, 可选): 每页数量
  - `keyword` (string, 可选): 搜索关键词
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "number",
        "name": "string",
        "categoryName": "string",
        "price": "number",
        "stock": "number",
        "imageUrl": "string",
        "description": "string"
      }
    ],
    "total": "number"
  }
}
```

### 6.4 创建商品
- **路径**: `POST /admin/products`
- **权限**: 管理员
- **请求体**:
```json
{
  "name": "string",
  "categoryName": "string",
  "price": "number",
  "stock": "number",
  "imageUrl": "string",
  "description": "string (可选)"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": "number",
    "name": "string",
    "categoryName": "string",
    "price": "number",
    "stock": "number",
    "imageUrl": "string",
    "description": "string"
  }
}
```

### 6.5 更新商品
- **路径**: `PUT /admin/products/{productId}`
- **权限**: 管理员
- **路径参数**: `productId` (number) - 商品ID
- **请求体**:
```json
{
  "name": "string",
  "categoryName": "string",
  "price": "number",
  "stock": "number",
  "imageUrl": "string",
  "description": "string (可选)"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 6.6 删除商品（下架）
- **路径**: `DELETE /admin/products/{productId}`
- **权限**: 管理员
- **路径参数**: `productId` (number) - 商品ID
- **响应**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

### 6.7 获取订单列表（管理员）
- **路径**: `GET /admin/orders`
- **权限**: 管理员
- **查询参数**:
  - `page` (number, 可选): 当前页码
  - `size` (number, 可选): 每页数量
  - `status` (string, 可选): 订单状态筛选
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "string",
        "userId": "number",
        "createdAt": "string (ISO 8601)",
        "status": "string",
        "totalAmount": "number (BigDecimal, 精确到分)"
      }
    ],
    "total": "number"
  }
}
```

### 6.8 更新订单状态
- **路径**: `PUT /admin/orders/{orderId}/status`
- **权限**: 管理员
- **路径参数**: `orderId` (string) - 订单ID
- **请求体**:
```json
{
  "status": "string (PENDING|PAID|SHIPPED|DONE)"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

### 6.9 获取用户列表
- **路径**: `GET /admin/users`
- **权限**: 管理员
- **查询参数**:
  - `page` (number, 可选): 当前页码
  - `size` (number, 可选): 每页数量
  - `keyword` (string, 可选): 搜索关键词（用户名）
- **响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [
      {
        "id": "number",
        "username": "string",
        "role": "string",
        "createdAt": "string"
      }
    ],
    "total": "number"
  }
}
```

---

## 📌 统一响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { /* 业务数据 */ }
}
```

### 错误响应
```json
{
  "code": 400 | 401 | 403 | 404 | 500,
  "message": "错误描述",
  "data": null
}
```

### HTTP 状态码说明
- **200**: 成功
- **400**: 请求参数错误
- **401**: 未登录或 Token 过期
- **403**: 无权限访问
- **404**: 资源不存在
- **500**: 服务器内部错误

---

## 🔐 认证机制

1. **登录获取 Token**: 调用 `POST /auth/login` 获取 JWT Token
2. **存储 Token**: 前端存储在 `localStorage` (`auth_token`)
3. **携带 Token**: 所有需要认证的接口在请求头中添加:
   ```
   Authorization: Bearer <token>
   ```
4. **Token 过期**: 前端会自动跳转到登录页，用户需要重新登录

---

## 📊 接口统计

| 模块 | 接口数量 | 需要登录 | 需要管理员权限 |
|-----|---------|---------|--------------|
| 认证模块 | 3 | 1 | 0 |
| 商品模块 | 4 | 0 | 0 |
| 订单模块 | 6 | 6 | 0 |
| 购物车模块 | 3 | 3 | 0 |
| 用户模块 | 6 | 6 | 0 |
| 管理员模块 | 9 | 9 | 9 |
| **总计** | **31** | **25** | **9** |

---

## 🔄 前端降级策略

所有 API 调用都包含 Mock 数据降级机制：
- API 请求失败时，自动使用本地 Mock 数据
- 确保开发环境下前端可独立运行
- 生产环境下应禁用 Mock 数据

---

## 📝 备注

1. **分页参数**: 默认 `page=1, size=20`
2. **日期格式**: ISO 8601 标准 (例: `2025-12-25T10:30:00Z`)，统一使用 `createdAt` 和 `updatedAt` 字段
3. **金额类型**: 后端使用 `BigDecimal` 类型，精确到分（两位小数），前端显示时除以 100
4. **图片 URL**: 支持完整 HTTP/HTTPS URL
5. **订单状态枚举**: `PENDING` (待支付) | `PAID` (已支付) | `SHIPPED` (已发货) | `DONE` (已完成) | `CANCELLED` (已取消)
6. **用户角色枚举**: `USER` | `ADMIN`
7. **响应格式**: 所有接口响应必须包含 `code`、`message` 和 `data` 三个字段
