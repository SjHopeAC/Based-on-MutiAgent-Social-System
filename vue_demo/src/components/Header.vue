<template>
  <header class="forum-header">
    <div class="header-content">
      <!-- 左上角：Logo和品牌 -->
      <div class="brand-section">
        <div class="logo">
          <svg width="32" height="32" viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="32" height="32" rx="8" fill="url(#gradient)"/>
            <path d="M10 13H22V15H10V13ZM10 17H18V19H10V17ZM20 17H22V19H20V17Z" fill="white"/>
            <defs>
              <linearGradient id="gradient" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse">
                <stop stop-color="#007AFF"/>
                <stop offset="1" stop-color="#0056CC"/>
              </linearGradient>
            </defs>
          </svg>
          <div class="brand">
            <h1 class="brand-name">阔论</h1>
            <p class="brand-tagline">深度对话·思想碰撞</p>
          </div>
        </div>
      </div>

      <!-- 中间 搜索框 -->
      <div class="search-container">
        <div class="search-box" :class="{ 'search-focused': isSearchFocused }">
          <svg class="search-icon" width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 21L16.65 16.65M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <input 
            type="text" 
            placeholder="搜索话题、用户或内容" 
            class="search-input"
            v-model="searchQuery"
            @focus="isSearchFocused = true"
            @blur="isSearchFocused = false"
            @keyup.enter="handleSearch"
          />
          <div class="search-actions">
            <button 
              class="search-clear" 
              @click="clearSearch"
              v-show="searchQuery.length > 0"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="search-button" @click="handleSearch">
              搜索
            </button>
          </div>
        </div>
      </div>

      <!-- 右上角：用户区域 -->
      <div class="user-area">
        <!-- 已登录状态 -->
        <template v-if="isLoggedIn">
          <div class="user-profile">
            <router-link to="/user-profile" class="avatar-container" :class="{ 'vip-avatar-container': isVip }">
            <div class="avatar" :class="{ 'vip-avatar': isVip }">
              <img 
                :src="getAvatarUrl(userInfo.value?.avatar)" 
                :alt="userName"
                class="avatar-image"
              />
            </div>
            </router-link>
            <div class="user-info">
              <div class="user-name">欢迎，{{ userName }}</div>
              <div class="user-vip" v-if="isVip">
                VIP
              </div>
            </div>
          </div>
          <div class="header-actions">
            <router-link to="/Notifications">
            <button class="action-button notification-button" @click="handleNotification">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 8C18 6.4087 17.3679 4.88258 16.2426 3.75736C15.1174 2.63214 13.5913 2 12 2C10.4087 2 8.88258 2.63214 7.75736 3.75736C6.63214 4.88258 6 6.4087 6 8C6 15 3 17 3 17H21C21 17 18 15 18 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M13.73 21C13.5542 21.3031 13.3018 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span class="notification-badge" v-if="notificationCount > 0">
                {{ notificationCount > 99 ? '99+' : notificationCount }}
              </span>
            </button>
            </router-link>
            <router-link to="/admin/dashboard">
            <button class="action-button admin-button" v-if="isAdmin">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2C13.1 2 14 2.9 14 4C14 5.1 13.1 6 12 6C10.9 6 10 5.1 10 4C10 2.9 10.9 2 12 2ZM21 9H15V22H13V16H11V22H9V9H3V7H21V9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              管理后台
            </button>
            </router-link>
            <router-link to="/post/create">
            <button class="action-button create-button">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              发布帖子
            </button>
            </router-link>
          </div>
        </template>
        
        <!-- 未登录状态 -->
        <template v-else>
          <div class="header-actions">
            <router-link to="/login">
            <button class="action-button login-button">
              登录
            </button>
            </router-link>
            <router-link to="/register">
            <button class="action-button register-button">
              注册
            </button>
            </router-link>
          </div>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import router from '@/router'
import { ref, computed, onMounted, watch } from 'vue'
import { useStore } from 'vuex'
import request from '@/utils/request'

// 定义emit事件
const emit = defineEmits(['search'])

// 获取store
const store = useStore()

// 搜索相关
const searchQuery = ref('')
const isSearchFocused = ref(false)

// 从store中获取用户信息
const userInfo = computed(() => store.state.userInfo || null)
const userName = computed(() => store.getters.nickName || '访客')

// 检查是否登录
const isLoggedIn = computed(() => store.state.isLogin || false)

// 检查是否是管理员
const isAdmin = computed(() => {
  const user = userInfo.value
  return user && ( user.username === 'admin')
})

//通知相关
const notificationCount = ref(0)

// VIP状态判断
const isVip = computed(() => userInfo.value?.isVip || userInfo.value?.vipLevel > 0)

// 获取头像URL
const getAvatarUrl = (avatar) => {
  // 直接从store获取用户头像，确保能拿到最新值
  const userAvatar = userInfo.value?.avatar || avatar
  
  if (!userAvatar) {
    // 使用Element Plus的默认头像样式
    return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
  
  // 检查是否已经是完整URL
  if (userAvatar.startsWith('http://') || userAvatar.startsWith('https://')) {
    return userAvatar
  }
  
  // 处理相对路径
  let avatarPath = userAvatar
  if (!avatarPath.startsWith('/')) {
    avatarPath = '/' + avatarPath
  }
  
  // 构建完整URL
  return `http://localhost:8088/api/upload${avatarPath}`
}

// 获取未读通知数量
const loadUnreadCount = async () => {
  try {
    if (isLoggedIn.value && userInfo.value?.id) {
      // 获取系统通知数量
      const notificationRes = await request(`/notifications/unreadCount?userId=${userInfo.value.id}`)
      const notificationNum = notificationRes.code === 200 ? (notificationRes.data || 0) : 0
      
      // 获取未读私信数量
      const messageRes = await request(`/message/unread/count/${userInfo.value.id}`)
      const messageNum = messageRes.code === 200 ? (messageRes.data || 0) : 0
      
      // 总的通知数量
      notificationCount.value = notificationNum + messageNum
    }
  } catch (error) {
    console.error('获取未读通知数量失败:', error)
  }
}

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    // 跳转到搜索页面，并传递搜索关键词
    router.push({
      path: '/search',
      query: { keyword: searchQuery.value.trim() }
    })
  } else {
    router.push('/search')
  }
}

const clearSearch = () => {
  searchQuery.value = ''
}

const handleNotification = () => {
  // 点击通知后重新加载未读数量
  loadUnreadCount()
}

// 监听登录状态变化
watch(isLoggedIn, () => {
  if (isLoggedIn.value) {
    loadUnreadCount()
  } else {
    notificationCount.value = 0
  }
})

// 监听用户信息变化
watch(userInfo, () => {
  if (isLoggedIn.value) {
    loadUnreadCount()
  }
}, { deep: true })

// 页面加载时获取未读通知数量
onMounted(() => {
  if (isLoggedIn.value) {
    loadUnreadCount()
  }
})
</script>

<style scoped>
.forum-header {
  background: white;
  border-bottom: 1px solid #e5e5ea;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
}

/* 品牌区域 */
.brand-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  height: 48px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 100%;
}

.brand {
  display: flex;
  flex-direction: column;
}

.brand-name {
  font-size: 22px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

.brand-tagline {
  font-size: 12px;
  color: #6e6e73;
  font-weight: 400;
  letter-spacing: 0.3px;
}

/* 搜索框 */
.search-container {
  flex: 1;
  max-width: 600px;
}

.search-box {
  display: flex;
  align-items: center;
  background-color: #f5f5f7;
  border-radius: 12px;
  border: 1px solid #e5e5ea;
  padding: 8px 16px;
  transition: all 0.2s ease;
  height: 48px;
}

.search-box.search-focused {
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.search-icon {
  color: #86868b;
  margin-right: 8px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #1d1d1f;
  outline: none;
  padding: 4px 0;
}

.search-input::placeholder {
  color: #86868b;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.search-clear {
  background: none;
  border: none;
  color: #86868b;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.search-clear:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.search-button {
  background-color: #007AFF;
  color: white;
  border: none;
  border-radius: 10px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.search-button:hover {
  background-color: #0056CC;
  transform: translateY(-1px);
}

/* 用户区域 */
.user-area {
  display: flex;
  align-items: center;
  gap: 24px;
  height: 48px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(0, 122, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #007AFF;
  border: 1.5px solid white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* VIP头像样式 */
.avatar.vip-avatar {
  border: 2px solid #FFD700;
  box-shadow: 0 0 0 2px rgba(255, 215, 0, 0.2);
}

/* 头像容器核心样式：消除router-link默认样式 + 手型 + hover蓝色阴影 */
.avatar-container {
  text-decoration: none; /* 消除链接下划线 */
  color: inherit; /* 继承父元素颜色，不改变原有样式 */
  cursor: pointer; /* 鼠标移上去显示手型 */
  display: inline-block; /* 保证阴影效果正常显示 */
  border-radius: 50%; /* 圆形阴影，和头像匹配 */
  transition: all 0.2s ease; /* 过渡动画，效果更丝滑 */
}

/* 普通用户hover效果：蓝色阴影 + 轻微缩放 */
.avatar-container:hover:not(.vip-avatar-container) {
  box-shadow: 0 0 0 4px rgba(0, 122, 255, 0.2); /* 淡蓝色外阴影，和主题色匹配 */
  transform: scale(1.05); /* 轻微放大，提升交互体验 */
}

/* VIP用户头像容器样式 */
.avatar-container.vip-avatar-container {
  transition: all 0.2s ease;
}

/* VIP用户hover效果：金色阴影 + 轻微缩放 */
.avatar-container.vip-avatar-container:hover {
  box-shadow: 0 0 0 4px rgba(255, 215, 0, 0.3); /* 金色外阴影 */
  transform: scale(1.05); /* 轻微放大，提升交互体验 */
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.user-vip {
  font-size: 12px;
  font-weight: 500;
  color: #FF9500;
  background-color: rgba(255, 149, 0, 0.1);
  padding: 2px 4px;
  border-radius: 6px;
  display: inline-block;
  margin-top: 2px;
  white-space: nowrap;
  width: auto;
  min-width: unset;
  text-align: center;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 10px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

.notification-button {
  background: none;
  color: #6e6e73;
}

.notification-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.notification-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: #FF3B30;
  color: white;
  font-size: 11px;
  font-weight: 600;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  border: 2px solid white;
}

.create-button {
  background-color: #007AFF;
  color: white;
}

.create-button:hover {
  background-color: #0056CC;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
}

.admin-button {
  background-color: #f56c6c;
  color: white;
}

.admin-button:hover {
  background-color: #f78989;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(245, 108, 108, 0.3);
}

/* 登录和注册按钮 */
.login-button {
  background-color: transparent;
  color: #007AFF;
  border: 1px solid #007AFF;
}

.login-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
}

.register-button {
  background-color: #007AFF;
  color: white;
}

.register-button:hover {
  background-color: #0056CC;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .search-container {
    width: 100%;
  }
  
  .user-area {
    width: 100%;
    justify-content: space-between;
    height: auto;
  }
  
  .brand-section {
    height: auto;
  }
  
  .logo {
    height: auto;
  }
  
  .search-box {
    height: auto;
  }
}
</style>