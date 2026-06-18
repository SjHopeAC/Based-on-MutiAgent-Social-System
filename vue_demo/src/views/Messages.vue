<template>
  <div class="messages-container">
    <div class="messages-header">
      <h1 class="messages-title">私信</h1>
    </div>
    
    <div class="messages-content">
      <!-- 私信列表 -->
      <div class="message-list">
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="message-item"
          :class="{ 'unread': message.status === 0 }"
          @click="navigateToMessage(message.senderId)"
        >
          <div class="message-avatar">
            <img :src="getAvatarUrl(message.sender?.avatar)" alt="用户头像" />
          </div>
          <div class="message-info">
            <div class="message-header">
              <span class="message-sender">{{ message.sender?.nickname || message.sender?.username || '未知用户' }}</span>
              <span class="message-time">{{ formatTime(message.createTime) }}</span>
            </div>
            <div class="message-content">
              <template v-if="message.type === 1">
                {{ message.content || '无内容' }}
              </template>
              <template v-else-if="message.type === 2">
                <span class="share-tag">分享话题：</span>{{ message.topic?.topicTitle || '话题' }}
              </template>
              <template v-else-if="message.type === 3">
                <span class="share-tag">分享帖子：</span>{{ message.post?.postContent?.substring(0, 50) || '帖子' }}
              </template>
            </div>
          </div>
          <div v-if="message.status === 0" class="unread-badge"></div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="empty-messages">
          <div class="empty-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 11.5C21 16.75 16.75 21 11.5 21C6.25 21 2 16.75 2 11.5C2 6.25 6.25 2 11.5 2C16.75 2 21 6.25 21 11.5Z" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M8 14V17" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 14V17" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 10V17" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <p class="empty-text">暂无私信</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const store = useStore()

const messages = ref([])
const currentUserId = computed(() => {
  const userId = store.getters.userId
  console.log('计算得到的userId:', userId)
  return userId
})

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return 'https://picsum.photos/120/120'
  }
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  let avatarPath = avatar
  if (!avatarPath.startsWith('/')) {
    avatarPath = '/' + avatarPath
  }
  return `http://localhost:8088/api/upload${avatarPath}`
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const minutes = Math.floor(diff / (1000 * 60))
  
  if (days > 0) {
    return `${days}天前`
  } else if (hours > 0) {
    return `${hours}小时前`
  } else if (minutes > 0) {
    return `${minutes}分钟前`
  } else {
    return '刚刚'
  }
}

// 导航到私信详情
const navigateToMessage = (userId) => {
  router.push(`/messages/${userId}`)
}

// 加载私信列表
const loadMessages = async () => {
  try {
    console.log('开始加载私信，用户ID:', currentUserId.value)
    if (!currentUserId.value || currentUserId.value === '') {
      ElMessage.warning('请先登录')
      router.push('/login')
      return
    }
    const res = await request.get(`/message/list/${currentUserId.value}`)
    console.log('私信列表请求响应:', res)
    if (res.code === 200) {
      messages.value = res.data || []
      console.log('加载到的私信数据:', messages.value)
    } else {
      ElMessage.error(res.msg || '获取私信列表失败')
    }
  } catch (error) {
    console.error('获取私信列表失败:', error)
    ElMessage.error('获取私信列表失败')
  }
}

onMounted(() => {
  console.log('进入私信页面，用户信息:', store.state.userInfo)
  if (!currentUserId.value || currentUserId.value === '') {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  loadMessages()
})
</script>

<style scoped>
.messages-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.messages-header {
  margin-bottom: 32px;
}

.messages-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.messages-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  max-height: 70vh;
}

.message-list {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  max-height: 70vh;
  padding-right: 8px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条样式 */
.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.message-list::-webkit-scrollbar-thumb {
  background: rgba(0, 122, 255, 0.3);
  border-radius: 10px;
}

.message-list::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 122, 255, 0.5);
}

.message-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.message-item:hover {
  background-color: #f9f9f9;
}

.message-item.unread {
  background-color: #f5f9ff;
}

.message-avatar {
  flex-shrink: 0;
  margin-right: 16px;
}

.message-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.message-info {
  flex: 1;
  min-width: 0;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-sender {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-content {
  font-size: 14px;
  color: #666;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.share-tag {
  color: #007AFF;
  font-weight: 500;
}

.unread-badge {
  width: 8px;
  height: 8px;
  background-color: #ff3b30;
  border-radius: 50%;
  margin-left: 12px;
}

.empty-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  color: #999;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-container {
    padding: 16px;
  }
  
  .message-item {
    padding: 12px 16px;
  }
  
  .message-avatar img {
    width: 40px;
    height: 40px;
  }
}
</style>