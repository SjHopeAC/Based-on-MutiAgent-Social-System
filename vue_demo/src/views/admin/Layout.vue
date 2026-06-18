<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="250px" class="admin-sidebar">
        <div class="sidebar-header">
          <h2>管理后台</h2>
          <p class="admin-info">管理员: {{ adminName }}</p>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/posts">
            <el-icon><Document /></el-icon>
            <span>帖子管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/topics">
            <el-icon><ChatDotRound /></el-icon>
            <span>话题管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/comments">
            <el-icon><ChatLineSquare /></el-icon>
            <span>评论管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/categories">
            <el-icon><Folder /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/reports">
            <el-icon><Warning /></el-icon>
            <span>举报管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="admin-header">
          <div class="header-left">
            <el-breadcrumb>
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-button @click="goHome" type="primary" plain>返回前台</el-button>
            <el-button @click="handleLogout" type="danger" plain>退出登录</el-button>
          </div>
        </el-header>
        
        <el-main class="admin-main">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'
import {
  DataAnalysis,
  User,
  Document,
  ChatDotRound,
  ChatLineSquare,
  Folder,
  Warning
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const store = useStore()

const adminName = ref('管理员')
const activeMenu = ref('/admin/dashboard')

const currentPageTitle = computed(() => {
  const titles = {
    '/admin/dashboard': '数据统计',
    '/admin/users': '用户管理',
    '/admin/posts': '帖子管理',
    '/admin/topics': '话题管理',
    '/admin/comments': '评论管理',
    '/admin/categories': '分类管理',
    '/admin/reports': '举报管理'
  }
  return titles[route.path] || '管理后台'
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const goHome = () => {
  router.push('/')
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    store.commit('setUserInfo', null)
    router.push('/login')
    ElMessage.success('退出成功')
  }).catch(() => {})
}

onMounted(() => {
  activeMenu.value = route.path
  const userInfo = store.state.userInfo
  if (userInfo?.nickname) {
    adminName.value = userInfo.nickname
  }
})
</script>

<style scoped>
.admin-layout {
  height: 100vh;
  background-color: #f0f2f5;
}

.admin-sidebar {
  background-color: #001529;
  color: #fff;
  height: 100vh;
  overflow-y: auto;
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #1f2937;
}

.sidebar-header h2 {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #fff;
}

.admin-info {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.sidebar-menu {
  border: none;
  background-color: #001529;
}

.sidebar-menu .el-menu-item {
  color: #fff;
  margin: 5px 10px;
  border-radius: 4px;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #1890ff;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #1890ff;
}

.admin-header {
  background-color: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  gap: 10px;
}

.admin-main {
  padding: 20px;
  overflow-y: auto;
}
</style>
