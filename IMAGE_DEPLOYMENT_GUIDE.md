# 极简商城镜像部署指南 

本文档详细说明了如何采用 **"本地构建镜像 -> 推送至仓库 -> 服务器拉取运行"** 的标准流程进行部署。这种方式服务器无需安装 Maven/Node 环境，启动速度快，且更稳定。

---

## 第一阶段：本地环境准备

### 1. 注册 Docker Hub 账号
如果你还没有账号，请前往 [Docker Hub](https://hub.docker.com/) 注册一个免费账号。
*   **用户名 (Docker ID)**: 假设为 `myshopuser` (下文以此为例，请替换为你自己的用户名)。

### 2. 本地环境确认
确保你的开发电脑（Windows）已经安装了 **Docker Desktop** 并正在运行。

打开 PowerShell，执行以下命令检查：
```powershell
# 1. 检查 Docker 版本
docker --version
# 输出示例: Docker version 24.0.x, build ...

# 2. 登录 Docker Hub (输入你的用户名和密码)
docker login
# 输出示例: Login Succeeded
```

### 3. 确认项目目录结构
确保你的终端位于项目根目录 `d:\code\shop`，结构应如下：
```text
d:\code\shop
├── shopping/               # 后端代码
│   └── Dockerfile          # 后端构建文件
├── my-shop-frontend/       # 前端代码
│   ├── Dockerfile          # 前端构建文件
│   └── nginx.conf          # Nginx 配置
└── docker-compose.yml      # (本地开发用，部署时服务器上用新的)
```

---

## 第二阶段：构建并推送镜像 (在本地操作)

我们需要分别构建后端和前端的镜像，并推送到 Docker Hub。

### 1. 构建并推送后端镜像

在 `d:\code\shop` 目录下执行：

```powershell
# 1. 构建镜像 (注意最后的点 .)
# 格式: docker build -t <你的用户名>/<镜像名>:<标签> <Dockerfile所在目录>
docker build -t myshopuser/shop-backend:latest ./shopping

# 2. 推送镜像到仓库
docker push myshopuser/shop-backend:latest
```

### 2. 构建并推送前端镜像

```powershell
# 1. 构建镜像
docker build -t myshopuser/shop-frontend:latest ./my-shop-frontend

# 2. 推送镜像到仓库
docker push myshopuser/shop-frontend:latest
```

> **提示**: 推送过程取决于你的网速，可能需要几分钟。推送成功后，你可以在 Docker Hub 网页上看到这两个仓库。

---

## 第三阶段：服务器环境配置

### 1. 购买/准备服务器
*   **系统推荐**: Ubuntu 20.04/22.04 LTS 或 CentOS 7+。
*   **配置推荐**: 至少 2核 CPU，4GB 内存 (因为要运行 MySQL, Redis 和 Java 应用)。

### 2. 安装 Docker 环境 (以 Ubuntu 为例)

连接到你的服务器终端，执行：

```bash
# 1. 更新软件源
sudo apt-get update

# 2. 安装 Docker
sudo apt-get install -y docker.io

# 3. 安装 Docker Compose
sudo apt-get install -y docker-compose

# 4. 启动 Docker 并设置开机自启
sudo systemctl start docker
sudo systemctl enable docker
```

---

## 第四阶段：服务器部署与启动

采用镜像部署方式，**你不需要上传任何代码**，只需要在服务器上创建一个 `docker-compose.yml` 文件。

### 1. 创建部署目录
```bash
mkdir -p /opt/shop
cd /opt/shop
```

### 2. 创建 docker-compose.yml
使用 `vim` 或 `nano` 创建文件：
```bash
nano docker-compose.yml
```

**将以下内容粘贴进去 (注意替换 `<你的用户名>` 和 `APP_FRONTEND_URL`)：**

```yaml
version: '3.8'

services:
  # --- 数据库 ---
  mysql:
    image: mysql:8.0
    container_name: shop-mysql
    environment:
      MYSQL_ROOT_PASSWORD: 123  # 建议修改为强密码
      MYSQL_DATABASE: shopping
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - shop-network
    command: --default-authentication-plugin=mysql_native_password
    restart: always

  # --- 缓存 ---
  redis:
    image: redis:alpine
    container_name: shop-redis
    networks:
      - shop-network
    restart: always

  # --- 后端 (从仓库拉取) ---
  backend:
    # 【修改点1】替换为你的 Docker Hub 用户名
    image: myshopuser/shop-backend:latest 
    container_name: shop-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/shopping?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_PASSWORD: 123 # 需与 MySQL 配置一致
      SPRING_REDIS_HOST: redis
      # 【修改点2】修改为服务器公网 IP (用于邮件链接)
      APP_FRONTEND_URL: http://1.2.3.4 
    depends_on:
      - mysql
      - redis
    networks:
      - shop-network
    volumes:
      - ./uploads:/app/uploads # 图片持久化
    restart: always

  # --- 前端 (从仓库拉取) ---
  frontend:
    # 【修改点3】替换为你的 Docker Hub 用户名
    image: myshopuser/shop-frontend:latest
    container_name: shop-frontend
    ports:
      - "80:80"
    depends_on:
      - backend
    networks:
      - shop-network
    restart: always

networks:
  shop-network:
    driver: bridge

volumes:
  mysql_data:
```

### 3. 启动服务
在 `/opt/shop` 目录下执行：

```bash
# 拉取镜像并启动 (-d 表示后台运行)
sudo docker-compose up -d
```

---

## 第五阶段：常见错误与处理 (Troubleshooting)

### 1. 本地构建/推送失败
*   **错误**: `denied: requested access to the resource is denied`
    *   **原因**: 未登录或镜像标签的用户名与当前登录账号不符。
    *   **解决**: 运行 `docker login`，并确保 `docker build -t 用户名/...` 中的用户名正确。
*   **错误**: `error during connect: ...`
    *   **原因**: Docker Desktop 未启动。
    *   **解决**: 启动 Docker Desktop。

### 2. 服务器启动失败
*   **错误**: `Bind for 0.0.0.0:80 failed: port is already allocated`
    *   **原因**: 服务器的 80 端口被占用了 (通常是 Apache 或 Nginx)。
    *   **解决**: 停止占用端口的服务 (`sudo systemctl stop nginx`)，或者修改 `docker-compose.yml` 中的前端端口映射，例如 `"8080:80"`。
*   **错误**: `Connection refused` (数据库连接失败)
    *   **原因**: 后端容器启动比 MySQL 快，导致连接失败。
    *   **解决**: Spring Boot 会自动重试，通常等待 1-2 分钟即可自动恢复。如果一直失败，检查 `docker-compose.yml` 中的密码是否一致。

### 3. 访问问题
*   **现象**: 浏览器访问 IP 无法打开。
    *   **原因**: 云服务器的 **安全组 (Security Group)** 或防火墙未开放端口。
    *   **解决**: 登录阿里云/腾讯云/AWS 控制台，在安全组规则中开放 **80** (前端) 和 **8080** (后端) 端口的入站流量。

### 4. 功能问题
*   **现象**: 图片上传成功但无法显示。
    *   **原因**: 挂载卷权限问题。
    *   **解决**: 在服务器执行 `chmod 777 /opt/shop/uploads` 赋予写入权限。
