import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store/user'
import Home from '../views/Home.vue'
import CartDetail from '../views/CartDetail.vue'
import Login from '../views/Login.vue'
import Checkout from '../views/Checkout.vue'
import ProductDetail from '../views/ProductDetail.vue'
import SearchView from '../views/SearchView.vue'
import ProfileView from '../views/ProfileView.vue'
import AdminDashboard from '../views/AdminDashboard.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: Home,
    meta: { title: '首页 - 极简商城' }
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartDetail,
    meta: { title: '购物车 - 极简商城' }
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: '登录 - 极简商城' }
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: Checkout,
    meta: { 
      title: '结算 - 极简商城',
      requiresAuth: true // 需要登录
    }
  },
  {
    path: '/product/:id',
    name: 'product-detail',
    component: ProductDetail,
    props: true,
    meta: { title: '商品详情 - 极简商城' }
  },
  {
    path: '/search',
    name: 'search',
    component: SearchView,
    meta: { title: '搜索 - 极简商城' }
  },
  {
    path: '/order/confirm',
    name: 'confirm-order',
    component: () => import('../views/ConfirmOrder.vue'),
    meta: { title: '确认收货 - 极简商城' }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { 
      title: '个人中心 - 极简商城',
      requiresAuth: true // 需要登录
    }
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminDashboard,
    meta: { 
      title: '管理后台 - 极简商城',
      requiresAuth: true, // 需要登录
      requiresAdmin: true // 需要管理员权限
    }
  },
  {
    // 404 页面
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  // 切换路由时滚动到顶部
  scrollBehavior() {
    return { top: 0 }
  }
})

// 全局前置守卫：权限验证
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    // 未登录，重定向到登录页
    next({
      name: 'login',
      query: { redirect: to.fullPath } // 保存目标路径，登录后跳转
    })
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    // 不是管理员，显示提示并重定向到首页
    alert('抱歉，您没有访问管理后台的权限。需要 ROLE_ADMIN 角色。')
    next({ name: 'home' })
    return
  }
  
  // 允许访问
  next()
})

// 全局后置钩子：可用于页面加载完成后的操作
router.afterEach((to, from) => {
  // 可以在这里添加页面访问统计等
  console.log(`路由导航: ${from.path} -> ${to.path}`)
})

export default router