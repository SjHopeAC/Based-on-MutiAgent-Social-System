<template>
  <div class="message-detail-container">
    <div class="message-header">
      <button class="back-button" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 11H7.83L13.42 5.41L12 4L4 12L12 20L13.41 18.59L7.83 13H20V11Z" fill="currentColor"/>
        </svg>
        返回
      </button>
      <div class="chat-info">
        <h2 class="chat-title">{{ otherUser?.nickname || otherUser?.username || '未知用户' }}</h2>
      </div>
    </div>
    
    <div class="message-content">
      <!-- 消息列表 -->
      <div class="message-list">
        <!-- 消息列表 -->
        <div v-if="messages && messages.length > 0">
          <template v-for="msg in messages" :key="msg?.id || Math.random()">
            <div 
              v-if="msg"
              class="message-wrapper"
              :class="{ 'other-message': msg.senderId !== currentUserId, 'own-message': msg.senderId === currentUserId }"
            >
              <!-- 对方头像 -->
              <div v-if="msg.senderId !== currentUserId" class="message-avatar">
                <img :src="getAvatarUrl(msg.sender?.avatar || otherUser?.avatar)" alt="对方头像" />
              </div>
              
              <!-- 消息内容 -->
              <div class="message-content-wrapper">
                <!-- 分享消息 -->
                <div v-if="msg.type === 2 || msg.type === 3" class="share-message" :class="{ 'own': msg.senderId === currentUserId }">
                  <div class="share-header" :class="{ 'own': msg.senderId === currentUserId }">
                    <span class="share-type">{{ msg.type === 2 ? '分享话题' : '分享帖子' }}</span>
                  </div>
                  <div class="share-content">
                    <!-- 分享话题 -->
                    <template v-if="msg.type === 2">
                      <h4 class="share-title" :class="{ 'own': msg.senderId === currentUserId }"> {{ msg.topic?.topicTitle || msg.content || '话题' }}</h4>
                      <p class="share-desc" :class="{ 'own': msg.senderId === currentUserId }"> {{ msg.topic?.topicDesc?.substring(0, 100) || '无描述' }}</p>
                      <button class="view-button" @click="viewTopic(msg.targetId)">查看话题</button>
                    </template>
                    
                    <!-- 分享帖子 -->
                    <template v-else-if="msg.type === 3">
                      <p class="share-text" :class="{ 'own': msg.senderId === currentUserId }"> {{ msg.post?.postContent || msg.content || '无内容' }}</p>
                      <button class="view-button" @click="viewPost(msg.targetId)">查看帖子</button>
                    </template>
                  </div>
                  <div class="message-time" :class="{ 'own': msg.senderId === currentUserId }"> {{ formatTime(msg.createTime) }}</div>
                </div>
                
                <!-- 直接私信 -->
                <div v-else class="message-bubble" :class="{ 'own': msg.senderId === currentUserId }">
                  <div class="text-message">
                    {{ msg.content || '无内容' }}
                  </div>
                  <div class="message-time"> {{ formatTime(msg.createTime) }}</div>
                </div>
              </div>
              
              <!-- 自己的头像 -->
              <div v-if="msg.senderId === currentUserId" class="message-avatar">
                <img :src="getAvatarUrl(store.state.userInfo?.avatar)" alt="自己的头像" />
              </div>
            </div>
          </template>
        </div>
        <!-- 空消息提示 -->
        <div v-else-if="messages && messages.length === 0" class="empty-messages">
          暂无消息，开始聊天吧！
        </div>
      </div>
      
      <!-- 消息输入框 -->
      <div class="message-input-section">
        <el-input
          v-model="messageContent"
          type="textarea"
          :rows="1"
          placeholder="输入消息..."
          class="message-textarea"
          @keyup.enter.ctrl="sendMessage"
          resize="none"
        ></el-input>
        <el-button type="primary" @click="sendMessage" :disabled="!messageContent.trim()" class="send-button">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const store = useStore()

const otherUserId = ref(Number(route.params.userId))
const messages = ref([])
const messageContent = ref('')
const otherUser = ref(null)
const currentUserId = computed(() => store.getters.userId)

// 返回上一页
const goBack = () => {
  router.back()
}

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
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

// 查看话题
const viewTopic = (topicId) => {
  router.push(`/topic/detail/${topicId}`)
}

// 查看帖子
const viewPost = (postId) => {
  router.push(`/post/detail/${postId}`)
}

// 发送消息
const sendMessage = async () => {
  if (!messageContent.value.trim()) {
    ElMessage.warning('消息内容不能为空')
    return
  }
  
  try {
    const res = await request.post('/message/direct', {
      senderId: currentUserId.value,
      receiverId: otherUserId.value,
      content: messageContent.value.trim()
    })
    
    if (res.code === 200) {
      // 清空输入框
      messageContent.value = ''
      // 重新加载消息列表
      loadMessages()
    } else {
      ElMessage.error(res.msg || '发送失败')
    }
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败')
  }
}

// 获取用户信息
const loadUserInfo = async () => {
  try {
    const res = await request.get(`/user/${otherUserId.value}`)
    if (res.code === 200) {
      otherUser.value = res.data
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 加载消息列表
const loadMessages = async () => {
  try {
    console.log('开始加载消息:', {
      currentUserId: currentUserId.value,
      otherUserId: otherUserId.value
    })
    const res = await request.get(`/message/conversation/${currentUserId.value}/${otherUserId.value}`)
    console.log('消息接口响应:', res)
    if (res.code === 200) {
      messages.value = res.data || []
      console.log('消息列表:', messages.value)
      // 滚动到底部
      setTimeout(() => {
        const messageList = document.querySelector('.message-list')
        if (messageList) {
          messageList.scrollTop = messageList.scrollHeight
        }
      }, 100)
    } else {
      console.error('获取消息失败:', res)
      ElMessage.error(res.msg || '获取消息失败')
    }
  } catch (error) {
    console.error('获取消息失败:', error)
    ElMessage.error('获取消息失败')
  }
}

onMounted(() => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!otherUserId.value) {
    ElMessage.error('用户ID无效')
    router.push('/messages')
    return
  }
  
  loadUserInfo()
  loadMessages()
})
</script>

<style scoped>
.message-detail-container {
  max-width: 800px;
  margin: 0 auto;
  height: 90vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.message-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #007AFF;
  font-size: 14px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
  transform: translateY(-1px);
}

.chat-info {
  flex: 1;
  margin-left: 24px;
}

.chat-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.message-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow: hidden;
}

.message-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
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

.message-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  animation: fadeIn 0.3s ease;
  max-width: 100%;
  margin-bottom: 16px;
}

/* 对方消息 - 左顶格 */
.other-message {
  justify-content: flex-start;
}

/* 自己消息 - 右顶格 */
.own-message {
  justify-content: flex-end;
}

.own-message .message-content-wrapper {
  align-items: flex-end;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.message-content-wrapper {
  max-width: 85%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.message-content-wrapper.own {
  align-items: flex-end;
}

.share-message-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  max-width: 100%;
  animation: fadeIn 0.3s ease;
  margin-bottom: 16px;
}

.share-message-wrapper.own {
  flex-direction: row-reverse;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 18px;
  position: relative;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.2s ease;
  max-width: 100%;
}

.message-bubble:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.message-bubble:not(.own) {
  background: #ffffff;
  border-bottom-left-radius: 4px;
  color: #333;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.message-bubble.own {
  background: #1976d2;
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 1px 3px rgba(25, 118, 210, 0.2);
}

/* 添加消息气泡的小尾巴效果 */
.message-bubble:not(.own)::before {
  content: '';
  position: absolute;
  bottom: 8px;
  left: -8px;
  width: 0;
  height: 0;
  border-right: 8px solid #ffffff;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
}

.message-bubble.own::before {
  content: '';
  position: absolute;
  bottom: 8px;
  right: -8px;
  width: 0;
  height: 0;
  border-left: 8px solid #1976d2;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
}

.share-message {
  background: #ffffff;
  border-radius: 18px;
  overflow: hidden;
  transition: all 0.2s ease;
  max-width: 100%;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.share-message.own {
  background: #1976d2;
  box-shadow: 0 1px 3px rgba(25, 118, 210, 0.2);
}

.share-message:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.share-header {
  background: #f8f9fa;
  padding: 8px 16px;
  font-size: 12px;
  color: #6c757d;
  font-weight: 500;
}

.share-header.own {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.share-content {
  padding: 16px;
}

.share-title {
  font-size: 15px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #333;
  line-height: 1.4;
}

.share-title.own {
  color: white;
}

.share-desc {
  font-size: 14px;
  color: #666;
  margin: 0 0 14px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-desc.own {
  color: rgba(255, 255, 255, 0.8);
}

.share-text {
  font-size: 14px;
  color: #333;
  margin: 0 0 14px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.share-text.own {
  color: white;
}

.view-button {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #e0e0e0;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: inline-block;
}

.view-button:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.share-message.own .view-button {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.share-message.own .view-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.text-message {
  line-height: 1.6;
  font-size: 14px;
}

.message-time {
  font-size: 10px;
  color: #999;
  margin-top: 6px;
  text-align: right;
  opacity: 0.7;
  font-weight: 400;
  padding: 0 4px;
}

.message-bubble.own .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.share-message.own .message-time.own {
  color: rgba(255, 255, 255, 0.7);
}

/* 空消息提示 */
.empty-messages {
  text-align: center;
  padding: 40px 0;
  color: #999;
  font-size: 14px;
  background: #f8f9fa;
  margin: 20px;
  border-radius: 12px;
}

.message-input-section {
  display: flex;
  gap: 10px;
  padding: 10px 20px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  border-radius: 12px;
  transition: all 0.2s ease;
  width: 100%;
  max-width: 100%;
}

/* 调整Element Plus输入框样式 */
:deep(.el-textarea__inner) {
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  padding: 10px 14px;
  font-size: 14px;
  resize: none;
  min-height: 36px;
  max-height: 100px;
  line-height: 1.5;
  background: #fafafa;
  transition: all 0.2s ease;
}

:deep(.el-textarea__inner:focus) {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.2);
  background: #fff;
}

/* 调整Element Plus按钮样式 */
:deep(.el-button--primary) {
  background: #1976d2;
  border: none;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  padding: 0 20px;
  height: 36px;
  transition: all 0.2s ease;
}

:deep(.el-button--primary:hover:not(:disabled)) {
  background: #1565c0;
  transform: translateY(-1px);
}

:deep(.el-button--primary:active:not(:disabled)) {
  transform: translateY(0);
}

:deep(.el-button--primary:disabled) {
  background: #e0e0e0;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .message-detail-container {
    max-width: 100%;
    height: 100vh;
  }
  
  .message-content {
    padding: 16px;
  }
  
  .message-bubble {
    max-width: 85%;
    padding: 12px 16px;
  }
  
  .message-input-section {
    padding: 12px;
    border-radius: 12px;
  }
  
  .message-textarea {
    padding: 12px 14px;
    min-height: 40px;
  }
  
  .send-button {
    padding: 0 20px;
    height: 40px;
  }
}
</style>