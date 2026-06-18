<template>
  <div class="notifications-container">
    <div class="notifications-header">
      <div class="header-title">
        <h1>消息中心</h1>
        <div v-if="unreadCount > 0" class="unread-badge">{{ unreadCount }}</div>
      </div>
      <div class="header-actions">
        <el-button @click="markAllAsRead">全部标记已读</el-button>
        <el-button type="danger" @click="clearAllNotifications">清空通知</el-button>
      </div>
    </div>
    
    <div class="notifications-tabs">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部" name="all" />
        <el-tab-pane label="未读" name="unread" />
        <el-tab-pane label="系统通知" name="system" />
        <el-tab-pane label="互动消息" name="interaction" />
        <el-tab-pane label="私信" name="private" />
      </el-tabs>
    </div>
    
    <div class="notifications-main">
      <div class="notifications-list">
        <!-- 私信会话 -->
        <div v-if="activeTab === 'private'" class="conversations">
          <div 
            v-for="conversation in conversations" 
            :key="conversation.id"
            class="conversation-item"
            :class="{ 'unread': conversation.unread }"
            @click="openConversation(conversation.id)"
          >
            <div class="conversation-avatar">
              <img :src="getAvatarUrl(conversation.avatar)" alt="" />
            </div>
            <div class="conversation-content">
              <div class="conversation-header">
                <span class="conversation-name">{{ conversation.name }}</span>
                <span class="conversation-time">{{ conversation.time }}</span>
              </div>
              <p class="conversation-preview">{{ conversation.preview }}</p>
              <div class="conversation-badge" v-if="conversation.unreadCount > 0">
                {{ conversation.unreadCount }}
              </div>
            </div>
          </div>
        </div>
        
        <!-- 通知列表 -->
        <div v-else class="notification-items">
          <div 
            v-for="notification in filteredNotifications" 
            :key="notification.id"
            class="notification-item"
            :class="{ 'unread': !notification.read, 'system-notification': notification.noticeType === 5 }"
            @click="handleNotificationClick(notification)"
          >
            <!-- 用户头像（系统通知显示系统图标） -->
            <div class="notification-avatar" v-if="notification.noticeType !== 5">
              <img 
                :src="getAvatarUrl(notification.fromUserAvatar)" 
                :alt="notification.fromUserNickname"
                @error="handleAvatarError"
              />
            </div>
            <div class="notification-icon" v-else :class="getNotificationTypeClass(notification.noticeType)">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path :d="getNotificationIcon(notification.noticeType)" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            
            <div class="notification-content">
              <div class="notification-header">
                <!-- 用户名字（系统通知显示标题） -->
                <span v-if="notification.noticeType !== 5" class="notification-user-name">
                  {{ notification.fromUserNickname || '匿名用户' }}
                </span>
                <span v-else class="notification-title">{{ getNotificationTitle(notification.noticeType) }}</span>
                <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
              </div>
              <p class="notification-text">{{ notification.content }}</p>
              <div v-if="notification.action" class="notification-actions">
                <el-button 
                  v-if="notification.action === 'reply'" 
                  size="small" 
                  @click.stop="handleReply(notification)"
                >
                  回复
                </el-button>
                <el-button 
                  v-if="notification.action === 'view'" 
                  size="small" 
                  type="primary" 
                  @click.stop="handleView(notification)"
                >
                  查看详情
                </el-button>
              </div>
            </div>
            <div class="notification-status">
              <div v-if="!notification.read" class="unread-dot"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="filteredNotifications.length === 0 && activeTab !== 'private'" class="empty-state">
        <div class="empty-icon">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M18 8C18 6.4087 17.3679 4.88258 16.2426 3.75736C15.1174 2.63214 13.5913 2 12 2C10.4087 2 8.88258 2.63214 7.75736 3.75736C6.63214 4.88258 6 6.4087 6 8C6 15 3 17 3 17H21C21 17 18 15 18 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M13.73 21C13.5542 21.3031 13.3018 21.5547 12.9982 21.7295C12.6946 21.9044 12.3504 21.9965 12 21.9965C11.6496 21.9965 11.3054 21.9044 11.0018 21.7295C10.6982 21.5547 10.4458 21.3031 10.27 21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h3>暂无通知</h3>
        <p>还没有收到任何通知</p>
      </div>
      

    </div>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const store = useStore()
const router = useRouter()
const activeTab = ref('all')

const notifications = ref([])
const conversations = ref([])
const unreadCount = ref(0)

const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  if (activeTab.value === 'unread') return notifications.value.filter(n => !n.read)
  if (activeTab.value === 'system') return notifications.value.filter(n => n.noticeType === 5)
  if (activeTab.value === 'interaction') return notifications.value.filter(n => 
    [1, 2, 3, 4].includes(n.noticeType)
  )
  return []
})

const getNotificationTypeClass = (type) => {
  const typeMap = {
    1: 'follow',
    2: 'like',
    3: 'comment',
    4: 'private',
    5: 'system'
  }
  return typeMap[type] || 'system'
}

const getNotificationIcon = (type) => {
  const icons = {
    1: 'M12 4V20M4 12H20', // follow
    2: 'M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z', // like
    3: 'M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z', // comment
    4: 'M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z', // private
    5: 'M13 10V3L4 14H11V21L20 10H13Z' // system
  }
  return icons[type] || icons[5]
}

const getNotificationTitle = (type) => {
  const titles = {
    1: '关注通知',
    2: '点赞通知',
    3: '评论通知',
    4: '私信通知',
    5: '系统通知'
  }
  return titles[type] || '系统通知'
}

const formatTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const notificationTime = new Date(time)
  const diffMs = now - notificationTime
  const diffMinutes = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  
  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 7) return `${diffDays}天前`
  return notificationTime.toLocaleDateString()
}

const loadNotifications = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.warning('请先登录')
      return
    }
    
    const res = await request(`/notifications?userId=${userInfo.id}`)
    if (res.code === 200 || res.success) {
      let noticeList = res.data || []
      // 确保noticeList是一个数组
      if (!Array.isArray(noticeList)) {
        noticeList = []
      }
      // 转换通知格式
      notifications.value = noticeList.filter(notice => notice).map(notice => {
        const noticeType = notice.noticeType || 5
        return {
          id: notice.id || Math.random(),
          noticeType: noticeType,
          title: getNotificationTitle(noticeType),
          content: notice.content || '',
          createTime: notice.createTime || new Date(),
          read: notice.isRead === 1,
          action: [2, 3].includes(noticeType) ? 'view' : null,
          fromUserId: notice.fromUserId || 0,
          targetId: notice.targetId || 0,
          targetType: notice.targetType || 0,
          fromUserNickname: notice.fromUserNickname || '匿名用户',
          fromUserAvatar: notice.fromUserAvatar || ''
        }
      })
      // 计算未读通知数量
      unreadCount.value = notifications.value.filter(n => !n.read).length
    }
  } catch (error) {
    console.error('加载通知失败:', error)
    ElMessage.error('加载通知失败')
  }
}

const handleTabClick = (tab) => {
  activeTab.value = tab.paneName
}

const handleNotificationClick = async (notification) => {
  if (!notification.read) {
    // 标记为已读
      try {
        await request.put(`/notifications/${notification.id}/read`)
        notification.read = true
        // 更新未读通知数量
        unreadCount.value = notifications.value.filter(n => !n.read).length
        // 重新加载通知列表，确保红点自然消失
        await loadNotifications()
      } catch (error) {
        console.error('标记已读失败:', error)
      }
  }
  
  // 根据通知类型跳转到对应页面
  switch(notification.noticeType) {
    case 1: // 关注
      if (notification.targetType === 0) {
        // 跳转到用户个人主页
        window.location.href = `/user/profile/${notification.fromUserId}`
      }
      break
    case 2: // 点赞
    case 3: // 评论
      if (notification.targetType === 1) {
        // 跳转到帖子详情
        window.location.href = `/post/detail/${notification.targetId}`
      }
      break
  }
}

const handleReply = (notification) => {
  ElMessage.info(`回复通知 ${notification.id}`)
}

const handleView = (notification) => {
  if (notification.targetType === 1) {
    window.location.href = `/post/detail/${notification.targetId}`
  }
}

const handleAvatarError = (e) => {
  e.target.src = '/default-avatar.png'
}

const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return '/default-avatar.png'
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

// 获取消息预览
const getMessagePreview = (message) => {
  switch (message.type) {
    case 2: // 分享话题
      const topicTitle = message.topic?.topicTitle || message.content || '话题'
      return `分享了话题：${topicTitle}`
    case 3: // 分享帖子
      const postContent = message.post?.postContent || message.content || '帖子'
      return `分享了帖子：${postContent}`
    default: // 直接私信
      return message.content
  }
}

// 处理分享内容的显示
const renderMessageContent = (message) => {
  switch (message.type) {
    case 2: // 分享话题
      return {
        type: 'topic',
        content: message.content,
        link: `/topic/detail/${message.targetId}`
      }
    case 3: // 分享帖子
      return {
        type: 'post',
        content: message.content,
        link: `/post/detail/${message.targetId}`
      }
    default: // 直接私信
      return {
        type: 'text',
        content: message.content
      }
  }
}

// 跳转到链接
const navigateToLink = (url) => {
  router.push(url)
}

// 加载私信会话列表
const loadConversations = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.warning('请先登录')
      return
    }
    
    const res = await request.get(`/message/list/${userInfo.id}`)
    if (res.code === 200 || res.success) {
      const messages = res.data || []
      console.log('加载到的私信数据:', messages)
      // 按发送者/接收者分组，创建会话列表
      const conversationMap = {}
      
      messages.forEach(message => {
        // 确定会话的对方用户ID
        const otherUserId = message.senderId === userInfo.id ? message.receiverId : message.senderId
        
        if (!conversationMap[otherUserId]) {
          // 创建新会话
          conversationMap[otherUserId] = {
            id: otherUserId,
            name: message.senderId === userInfo.id ? message.receiver?.nickname || '未知用户' : message.sender?.nickname || '未知用户',
            avatar: message.senderId === userInfo.id ? message.receiver?.avatar : message.sender?.avatar,
            preview: getMessagePreview(message),
            time: formatTime(message.createTime),
            unread: message.receiverId === userInfo.id && message.status === 0,
            unreadCount: message.receiverId === userInfo.id && message.status === 0 ? 1 : 0,
            messages: []
          }
        } else {
          // 更新现有会话
          conversationMap[otherUserId].preview = getMessagePreview(message)
          conversationMap[otherUserId].time = formatTime(message.createTime)
          if (message.receiverId === userInfo.id && message.status === 0) {
            conversationMap[otherUserId].unread = true
            conversationMap[otherUserId].unreadCount++
          }
        }
        
        // 添加消息到会话
        conversationMap[otherUserId].messages.push({
          id: message.id,
          content: message.content,
          type: message.type,
          targetId: message.targetId,
          time: formatTime(message.createTime),
          isOwn: message.senderId === userInfo.id,
          topic: message.topic,
          post: message.post
        })
      })
      
      // 转换为数组并按时间排序
      conversations.value = Object.values(conversationMap).sort((a, b) => {
        return new Date(b.time) - new Date(a.time)
      })
      console.log('创建的会话列表:', conversations.value)
    }
  } catch (error) {
    console.error('加载私信失败:', error)
    ElMessage.error('加载私信失败')
  }
}

// 打开会话
const openConversation = async (conversationId) => {
  // 跳转到MessageDetail页面，使用更完善的私信功能
  router.push(`/messages/${conversationId}`)
}

const markAllAsRead = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.warning('请先登录')
      return
    }
    
    if (notifications.value.length === 0) {
      ElMessage.info('没有通知可标记已读')
      return
    }
    
    const res = await request.put(`/notifications/readAll?userId=${userInfo.id}`)
    if (res.code === 200 || res.success) {
      notifications.value.forEach(notification => {
        notification.read = true
      })
      unreadCount.value = 0
      ElMessage.success('已标记所有通知为已读')
    } else {
      ElMessage.error('标记失败')
    }
  } catch (error) {
    console.error('标记全部已读失败:', error)
    ElMessage.error('标记全部已读失败')
  }
}

const clearAllNotifications = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.warning('请先登录')
      return
    }
    
    if (notifications.value.length === 0) {
      ElMessage.info('没有通知可清空')
      return
    }
    
    ElMessageBox.confirm('确定要清空所有通知吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      const res = await request.delete(`/notifications/clearAll?userId=${userInfo.id}`)
      if (res.code === 200 || res.success) {
        notifications.value = []
        unreadCount.value = 0
        ElMessage.success('已清空所有通知')
      } else {
        ElMessage.error('清空通知失败')
      }
    }).catch(() => {
      // 用户点击取消，不做任何操作
    })
  } catch (error) {
    console.error('清空通知失败:', error)
    ElMessage.error('操作失败')
  }
}

// 初始化加载通知和私信
onMounted(() => {
  loadNotifications()
  loadConversations()
})
</script>

<style scoped>
.notifications-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background-color: white;
  border-radius: 16px;
  min-height: calc(100vh - 200px);
}

.notifications-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-title {
  position: relative;
  display: inline-block;
}

.notifications-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
  display: inline-block;
}

.unread-badge {
  position: absolute;
  top: -8px;
  right: -20px;
  background-color: #FF3B30;
  color: white;
  font-size: 12px;
  font-weight: 600;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 6px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #007AFF;
}

.notifications-main {
  min-height: 600px;
}

.notifications-list {
  background-color: #f5f5f7;
  border-radius: 16px;
  overflow: hidden;
  min-height: 600px;
}

.conversations {
  padding: 16px;
  max-height: 600px;
  overflow-y: auto;
  padding-right: 8px;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 自定义滚动条样式 */
.conversations::-webkit-scrollbar {
  width: 6px;
}

.conversations::-webkit-scrollbar-track {
  background: transparent;
  border-radius: 10px;
}

.conversations::-webkit-scrollbar-thumb {
  background: rgba(0, 122, 255, 0.3);
  border-radius: 10px;
}

.conversations::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 122, 255, 0.5);
}

.conversation-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 8px;
  position: relative;
}

.conversation-item:hover {
  background-color: #e5e5ea;
}

.conversation-item.unread {
  background-color: rgba(0, 122, 255, 0.05);
}

.conversation-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.conversation-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.conversation-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.conversation-time {
  font-size: 12px;
  color: #6e6e73;
}

.conversation-preview {
  font-size: 13px;
  color: #6e6e73;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.conversation-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: #FF3B30;
  color: white;
  font-size: 12px;
  font-weight: 600;
  width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-items {
  padding: 16px;
}

.notification-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  background-color: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 8px;
  position: relative;
}

.notification-item:hover {
  background-color: #f5f5f7;
}

.notification-item.unread {
  background-color: rgba(0, 122, 255, 0.05);
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.notification-icon.like {
  background-color: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
}

.notification-icon.comment {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.notification-icon.follow {
  background-color: rgba(52, 199, 89, 0.1);
  color: #34C759;
}

.notification-icon.system {
  background-color: rgba(255, 149, 0, 0.1);
  color: #FF9500;
}

.notification-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.notification-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.notification-item.system-notification {
  background-color: rgba(255, 149, 0, 0.05);
}

.notification-item.system-notification:hover {
  background-color: rgba(255, 149, 0, 0.1);
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notification-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.notification-user-name {
  font-size: 14px;
  font-weight: 600;
  color: #007AFF;
}

.notification-time {
  font-size: 12px;
  color: #6e6e73;
}

.notification-text {
  font-size: 13px;
  color: #6e6e73;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.notification-actions {
  display: flex;
  gap: 8px;
}

.notification-status {
  display: flex;
  align-items: center;
  padding-left: 8px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #007AFF;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  width: 100%;
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: #e5e5ea;
  margin-bottom: 24px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

</style>