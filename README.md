# 电商购物平台 (E-commerce Shopping Platform)

**学号**: 202330450621
**姓名**: 黄烁韩
**项目介绍**: 一个基于 Spring Boot + Vue 3 的全栈电商购物平台，支持用户注册登录、商品浏览、购物车管理、订单处理、支付功能等完整的电商流程。

**源码地址**: https://github.com/[username]/ecommerce-project
**技术栈**: Spring Boot 3.2.5 + Vue 3 + MySQL + Redis + Docker

##  技术栈

### 后端
- **框架**: Spring Boot 3.2.5
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **ORM**: Spring Data JPA + MyBatis-Plus
- **安全**: Spring Security + JWT
- **API文档**: Knife4j (Swagger增强)
- **邮件**: Spring Boot Mail
- **容器化**: Docker

### 前端
- **框架**: Vue 3
- **构建工具**: Vite
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **样式**: Tailwind CSS
- **HTTP客户端**: Axios
- **图表**: Chart.js + Vue-ChartJS
- **加密**: CryptoJS

## 功能特性

### 用户模块
- 用户注册与登录
- JWT 身份认证
- 用户信息管理
- 地址管理

### 商品模块
- 商品分类管理
- 商品搜索与分页
- 商品详情展示
- 库存检查

### 购物车模块
- 添加/删除商品
- 购物车同步
- 数量修改

### 订单模块
- 创建订单
- 订单状态管理 (待支付/已支付/已发货/已完成)
- 订单历史查询
- 邮件确认订单

### 支付模块
- 支付请求处理
- 支付状态跟踪

### 管理员模块
- 销售数据统计
- 用户管理

### 文件上传
- 商品图片上传
- 文件存储管理

## 项目结构

```
ecommerce-project/
├── backend/                 # 后端项目
│   ├── src/main/java/com/hshhh/shopping/
│   │   ├── common/          # 公共组件
│   │   ├── config/          # 配置类
│   │   ├── exception/       # 异常处理
│   │   ├── modules/         # 业务模块
│   │   │   ├── admin/       # 管理员模块
│   │   │   ├── auth/        # 认证模块
│   │   │   ├── cart/        # 购物车模块
│   │   │   ├── order/       # 订单模块
│   │   │   ├── payment/     # 支付模块
│   │   │   ├── product/     # 商品模块
│   │   │   ├── user/        # 用户模块
│   │   │   └── file/        # 文件模块
│   │   ├── security/        # 安全配置
│   │   └── ShoppingApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml  # 应用配置
│   │   └── static/          # 静态资源
│   ├── Dockerfile           # Docker构建文件
│   └── pom.xml              # Maven配置
├── frontend/                # 前端项目
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── Dockerfile
│   └── nginx.conf
├── .env.example             # 环境变量示例
└── IMAGE_DEPLOYMENT_GUIDE.md # 镜像部署指南
```

## 环境要求

- Java 17+
- Node.js 16+
- MySQL 8.0+
- Redis
- Docker (可选，用于部署)

## 本地开发

### 后端启动

1. 安装依赖并启动数据库和Redis
2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置
3. 运行后端：
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

### 前端启动

1. 安装依赖：
   ```bash
   cd frontend
   npm install
   ```
2. 启动开发服务器：
   ```bash
   npm run dev
   ```
3. 访问前端：http://localhost:5173

##  部署

支持多种部署方式：

### 方式一：Docker Compose 部署
参考 `IMAGE_DEPLOYMENT_GUIDE.md` 进行镜像构建和部署。

### 方式二：传统部署
1. 后端打包：
   ```bash
   cd backend
   ./mvnw clean package -DskipTests
   ```
2. 前端构建：
   ```bash
   cd frontend
   npm run build
   ```
3. 配置服务器环境变量
4. 启动服务

## 环境变量配置

复制 `.env.example` 到 `.env` 并修改相应配置：

```env
# 数据库配置
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/shopping
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Redis 配置
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# 邮件配置
SPRING_MAIL_HOST=smtp.qq.com
SPRING_MAIL_USERNAME=your_email@qq.com
SPRING_MAIL_PASSWORD=your_password

# JWT 密钥
JWT_SECRET=your_jwt_secret_key

# 前端地址
APP_FRONTEND_URL=http://localhost:5173
```

## API 文档

### 认证接口

#### 用户注册
- **接口地址**: `POST /api/v1/auth/register`
- **请求参数**:
```json
{
  "username": "string",
  "email": "string",
  "password": "string"
}
```
- **响应格式**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "string",
    "user": {
      "id": "number",
      "username": "string",
      "email": "string"
    }
  }
}
```

#### 用户登录
- **接口地址**: `POST /api/v1/auth/login`
- **请求参数**:
```json
{
  "username": "string",
  "password": "string"
}
```

### 商品接口

#### 获取商品分类
- **接口地址**: `GET /api/v1/products/categories`
- **请求参数**: 无

#### 搜索商品
- **接口地址**: `GET /api/v1/products/search`
- **查询参数**:
  - `keyword`: 搜索关键词
  - `categoryId`: 分类ID
  - `page`: 页码 (默认0)
  - `size`: 每页大小 (默认10)

#### 获取商品详情
- **接口地址**: `GET /api/v1/products/{id}`
- **路径参数**: `id` (商品ID)

### 购物车接口

#### 获取购物车
- **接口地址**: `GET /api/v1/cart`
- **请求头**: `Authorization: Bearer {token}`

#### 添加商品到购物车
- **接口地址**: `POST /api/v1/cart`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
```json
{
  "productId": "number",
  "quantity": "number"
}
```

### 订单接口

#### 创建订单
- **接口地址**: `POST /api/v1/orders`
- **请求头**: `Authorization: Bearer {token}`
- **请求参数**:
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

#### 获取用户订单
- **接口地址**: `GET /api/v1/orders`
- **请求头**: `Authorization: Bearer {token}`
- **查询参数**:
  - `page`: 页码 (默认0)
  - `size`: 每页大小 (默认10)

#### 支付订单
- **接口地址**: `POST /api/v1/orders/{id}/pay`
- **请求头**: `Authorization: Bearer {token}`

### 用户接口

#### 获取用户信息
- **接口地址**: `GET /api/v1/users/profile`
- **请求头**: `Authorization: Bearer {token}`

#### 更新用户信息
- **接口地址**: `PUT /api/v1/users/profile`
- **请求头**: `Authorization: Bearer {token}`

### 响应格式统一
所有API响应都遵循统一的格式：
```json
{
  "code": "number",
  "message": "string",
  "data": "object"
}
```

### 错误处理
- **400 Bad Request**: 请求参数错误
- **401 Unauthorized**: 未认证或Token无效
- **403 Forbidden**: 权限不足
- **404 Not Found**: 资源不存在
- **500 Internal Server Error**: 服务器内部错误
