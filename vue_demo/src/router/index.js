import { createRouter, createWebHistory } from 'vue-router'
import store from '../store'

// 先判断环境：Vite 用 import.meta.env.BASE_URL，Vue CLI 用 process.env.BASE_URL
const baseUrl = import.meta.env?.BASE_URL || process.env.BASE_URL || '/'

const routes = [
  {
    path: "/", // 首页（预览页），作为网站默认入口
    name: "Home",
    component: () => import("../views/Home.vue"),
    meta: {
      title: "首页",
      requiresAuth: false // 首页无需登录（预览页）
    }
  },
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
    meta: {
      title: "登录",
      requiresAuth: false // 登录页不需要登录
    }
  },
  {
    path: "/register",
    name: "Register",
    component: () => import("../views/Register.vue"),
    meta: {
      title: "注册",
      requiresAuth: false // 注册页不需要登录
    }
  },
  {
    path: "/ForgotPassword",
    name: "ForgotPassword",
    component: () => import("../views/ForgotPassword.vue"),
    meta: {
      title: "忘记密码",
      requiresAuth: false // 忘记密码页不需要登录
    }
  },
  {
    path: '/user-profile', // 跳转的路径，需和模板中的 to/ push 路径一致
    name: 'UserProfile', // 路由名称（可选）
    component: () => import("../views/UserProfile.vue"),
    meta: {
      requireAuth: true // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/VipPayment', 
    component: () => import("../views/VipPayment.vue"),
    meta: {
      requireAuth: true // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/Notifications', 
    component: () => import("../views/Notifications.vue"),
    meta: {
      requireAuth: true // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/post/create', 
    component: () => import("../views/Post/CreatePost.vue"),
    meta: {
      requireAuth: true // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/post/edit/:postId',
    name: 'EditPost',
    component: () => import("../views/Post/EditPost.vue"),
    meta: {
      title: '编辑帖子',
      requireAuth: true // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/post/detail/:postId',
    name: 'PostDetail',
    component: () => import('../views/Post/Detail.vue'),
    meta: {
      title: '帖子详情',
      requiresAuth: false // 帖子详情页无需登录即可访问
    }
  },
  {
    path: '/test', // 跳转的路径，需和模板中的 to/ push 路径一致
    name: 'test_vue', 
    component: () => import("../views/Home.vue"),
    meta: {
      requireAuth: false // 可选：标记需要登录才能访问
    }
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/Messages.vue'),
    meta: {
      title: '私信',
      requireAuth: true // 私信页需要登录才能访问
    }
  },
  {
    path: '/messages/:userId',
    name: 'MessageDetail',
    component: () => import('../views/MessageDetail.vue'),
    meta: {
      title: '私信详情',
      requireAuth: true // 私信详情页需要登录才能访问
    }
  },
  {
    path: '/share/select-users',
    name: 'ShareSelectUsers',
    component: () => import('../views/ShareSelectUsers.vue'),
    meta: {
      title: '选择用户',
      requireAuth: true
    }
  },
  {
    path: '/user/:userId',
    name: 'UserInfo',
    component: () => import('../views/UserInfo.vue'),
    meta: {
      title: '用户信息',
      requiresAuth: false
    }
  },
  // 404路由：匹配所有未定义的路径，跳回首页
  // {
  //     path: '/:pathMatch(.*)*',
  //   redirect: '/'
  // },
  {
    path: '/topic/detail/:id',
    name: 'TopicDetail',
    component: () => import('../views/Topic/TopicDetail.vue'),
    meta: {
      title: '话题详情',
      requiresAuth: false // 话题详情页无需登录即可访问
    }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue'),
    meta: {
      title: '搜索',
      requiresAuth: false
    }
  },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: {
      title: '管理后台',
      requireAuth: true,
      isAdmin: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('../views/admin/Dashboard.vue'),
        meta: {
          title: '数据统计',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('../views/admin/Users.vue'),
        meta: {
          title: '用户管理',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('../views/admin/Posts.vue'),
        meta: {
          title: '帖子管理',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'topics',
        name: 'AdminTopics',
        component: () => import('../views/admin/Topics.vue'),
        meta: {
          title: '话题管理',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('../views/admin/Comments.vue'),
        meta: {
          title: '评论管理',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('../views/admin/Categories.vue'),
        meta: {
          title: '分类管理',
          requireAuth: true,
          isAdmin: true
        }
      },
      {
        path: 'reports',
        name: 'AdminReports',
        component: () => import('../views/admin/Reports.vue'),
        meta: {
          title: '举报管理',
          requireAuth: true,
          isAdmin: true
        }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
];

const router = createRouter({
  history: createWebHistory(baseUrl),
  routes
});

// 修复后的路由守卫逻辑（完全匹配你的需求）
router.beforeEach((to, from, next) => {
  // 1. 设置页面标题
  document.title = to.meta.title || 'Vue Demo';

  // 2. 获取核心状态
  const isLogin = store.state.isLogin ?? false; // 兜底默认值，避免undefined
  const needAuth = to.meta.requireAuth ?? false; // 未配置的路由默认无需登录

  // 核心逻辑：
  if (needAuth) {
    // 场景1：访问需要登录的页面
    if (isLogin) {
      // 已登录 → 放行
      next();
    } else {
      // 未登录 → 跳转到登录页（replace: true 避免历史记录堆积）
      next({ path: '/login', replace: true });
    }
  } else {
    // 场景2：访问无需登录的页面（首页/登录/注册/忘记密码）
    // 只有「已登录但访问登录页」时，跳回首页，其他情况全部放行
    if (isLogin && to.path === '/login') {
      next({ path: '/', replace: true });
    } else {
      next(); // 放行：包括未登录访问首页/登录/注册，已登录访问首页/注册等
    }
  }
});

export default router;