<template>
  <div class="share-select-container">
    <div class="share-header">
      <button class="back-button" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 11H7.83L13.42 5.41L12 4L4 12L12 20L13.41 18.59L7.83 13H20V11Z" fill="currentColor"/>
        </svg>
        返回
      </button>
      <h2 class="share-title">选择用户</h2>
    </div>
    
    <div class="share-content">
      <!-- 标签页 -->
      <div class="tab-container">
        <button 
          class="tab-button" 
          :class="{ 'active': activeTab === 'following' }"
          @click="activeTab = 'following'"
        >
          关注
        </button>
        <button 
          class="tab-button" 
          :class="{ 'active': activeTab === 'followers' }"
          @click="activeTab = 'followers'"
        >
          粉丝
        </button>
      </div>
      
      <!-- 用户列表 -->
      <div class="user-list">
        <div 
          v-for="user in activeTab === 'following' ? following : followers" 
          :key="user.id"
          class="user-item"
          :class="{ 'selected': selectedUsers.includes(user.id) }"
          @click="toggleUser(user.id)"
        >
          <div class="user-avatar">
            <img :src="getAvatarUrl(user.avatar)" alt="用户头像" />
          </div>
          <div class="user-info">
            <div class="user-name">{{ user.nickname || user.username }}</div>
            <div class="user-meta">{{ user.bio || '无简介' }}</div>
          </div>
          <div class="user-checkbox">
            <svg v-if="selectedUsers.includes(user.id)" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 6L9 17L4 12" stroke="#007AFF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 6L9 17L4 12" stroke="#ccc" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="(activeTab === 'following' && following.length === 0) || (activeTab === 'followers' && followers.length === 0)" class="empty-state">
          <p>{{ activeTab === 'following' ? '暂无关注用户' : '暂无粉丝' }}</p>
        </div>
      </div>
      
      <!-- 底部操作栏 -->
      <div class="bottom-action">
        <div class="selected-count">已选择 {{ selectedUsers.length }} 人</div>
        <button 
          class="confirm-button" 
          :disabled="selectedUsers.length === 0"
          @click="confirmSelection"
        >
          确定分享
        </button>
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

const currentUserId = computed(() => store.getters.userId)
const activeTab = ref('following')
const following = ref([])
const followers = ref([])
const selectedUsers = ref([])

// 获取分享参数
const shareType = ref(route.query.type || 'topic') // topic 或 post
const targetId = ref(Number(route.query.id))
const shareContent = ref(route.query.content || '')

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

// 返回上一页
const goBack = () => {
  router.back()
}

// 切换用户选择
const toggleUser = (userId) => {
  const index = selectedUsers.value.indexOf(userId)
  if (index > -1) {
    selectedUsers.value.splice(index, 1)
  } else {
    selectedUsers.value.push(userId)
  }
}

// 确认选择
const confirmSelection = async () => {
  if (selectedUsers.value.length === 0) {
    ElMessage.warning('请选择至少一个用户')
    return
  }
  
  try {
    // 批量发送分享
    const promises = selectedUsers.value.map(userId => {
      if (shareType.value === 'topic') {
        return request.post('/message/share/topic', {
          senderId: currentUserId.value,
          receiverId: userId,
          topicId: targetId.value,
          content: shareContent.value
        })
      } else {
        return request.post('/message/share/post', {
          senderId: currentUserId.value,
          receiverId: userId,
          postId: targetId.value,
          content: shareContent.value
        })
      }
    })
    
    const results = await Promise.all(promises)
    const successCount = results.filter(res => res.code === 200).length
    
    if (successCount > 0) {
      ElMessage.success(`成功分享给 ${successCount} 人`)
      router.back()
    } else {
      ElMessage.error('分享失败')
    }
  } catch (error) {
    console.error('分享失败:', error)
    ElMessage.error('分享失败')
  }
}

// 加载关注列表
const loadFollowing = async () => {
  try {
    // 假设后端有获取关注列表的接口
    const res = await request.get(`/user/${currentUserId.value}/following`)
    if (res.code === 200) {
      following.value = res.data || []
    }
  } catch (error) {
    console.error('获取关注列表失败:', error)
    // 模拟数据
    following.value = [
      { id: 2, nickname: '用户2', username: 'user2', avatar: '', bio: '这是用户2的简介' },
      { id: 3, nickname: '用户3', username: 'user3', avatar: '', bio: '这是用户3的简介' }
    ]
  }
}

// 加载粉丝列表
const loadFollowers = async () => {
  try {
    // 假设后端有获取粉丝列表的接口
    const res = await request.get(`/user/${currentUserId.value}/followers`)
    if (res.code === 200) {
      followers.value = res.data || []
    }
  } catch (error) {
    console.error('获取粉丝列表失败:', error)
    // 模拟数据
    followers.value = [
      { id: 4, nickname: '用户4', username: 'user4', avatar: '', bio: '这是用户4的简介' },
      { id: 5, nickname: '用户5', username: 'user5', avatar: '', bio: '这是用户5的简介' }
    ]
  }
}

onMounted(() => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (!targetId.value) {
    ElMessage.error('缺少分享目标ID')
    router.back()
    return
  }
  
  loadFollowing()
  loadFollowers()
})
</script>

<style scoped>
.share-select-container {
  max-width: 600px;
  margin: 0 auto;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.share-header {
  display: flex;
  align-items: center;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
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
  transition: background-color 0.2s ease;
}

.back-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
}

.share-title {
  flex: 1;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  text-align: center;
  margin-right: 60px; /* 平衡返回按钮的宽度 */
}

.share-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  overflow: hidden;
}

.tab-container {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.tab-button {
  flex: 1;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: #fff;
  font-size: 14px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tab-button:hover {
  border-color: #007AFF;
  color: #007AFF;
}

.tab-button.active {
  background: #007AFF;
  border-color: #007AFF;
  color: #fff;
}

.user-list {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.user-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.user-item:hover {
  background-color: #f9f9f9;
}

.user-item.selected {
  background-color: #f0f7ff;
}

.user-avatar {
  margin-right: 16px;
}

.user-avatar img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.user-meta {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.user-item.selected .user-checkbox {
  background-color: rgba(0, 122, 255, 0.1);
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

.bottom-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 -1px 3px rgba(0, 0, 0, 0.05);
}

.selected-count {
  font-size: 14px;
  color: #666;
}

.confirm-button {
  background: #007AFF;
  color: #fff;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.confirm-button:hover:not(:disabled) {
  background: #0056b3;
}

.confirm-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 滚动条样式 */
.user-list::-webkit-scrollbar {
  width: 6px;
}

.user-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.user-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.user-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .share-select-container {
    max-width: 100%;
    height: 100vh;
  }
  
  .share-content {
    padding: 12px;
  }
}
</style>