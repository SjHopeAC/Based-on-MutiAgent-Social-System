<template>
  <div class="user-profile-container">
    <div class="profile-header">
      <button class="back-button" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 11H7.83L13.42 5.41L12 4L4 12L12 20L13.41 18.59L7.83 13H20V11Z" fill="currentColor"/>
        </svg>
        返回
      </button>
      <h2 class="profile-title">用户信息</h2>
    </div>

    <div class="profile-content" v-if="user">
      <div class="profile-card">
        <div class="avatar-section">
          <img :src="getAvatarUrl(user.avatar)" alt="用户头像" class="user-avatar" />
        </div>

        <div class="user-info-section">
          <div class="user-name">
            {{ user.nickname || user.username }}
            <span v-if="user.isVip" class="vip-badge">VIP</span>
          </div>
          <div class="user-username">@{{ user.username }}</div>
          <div class="user-bio">{{ user.bio || '这个人很懒，还没有填写简介' }}</div>
        </div>

        <div class="user-stats">
          <div class="stat-item">
            <div class="stat-value">{{ user.followCount || 0 }}</div>
            <div class="stat-label">关注</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ user.fansCount || 0 }}</div>
            <div class="stat-label">粉丝</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ user.postCount || 0 }}</div>
            <div class="stat-label">帖子</div>
          </div>
        </div>

        <div class="action-buttons" v-if="currentUserId && currentUserId !== user.id">
          <button 
            v-if="!isFollowing"
            class="follow-button" 
            @click="handleFollow"
          >
            关注
          </button>
          <button 
            v-else
            class="unfollow-button" 
            @click="handleUnfollow"
          >
            已关注
          </button>
          <button class="message-button" @click="handleMessage">
            私信
          </button>
        </div>
      </div>

      <div class="user-details">
        <div class="detail-item" v-if="user.email">
          <span class="detail-label">邮箱：</span>
          <span class="detail-value">{{ user.email }}</span>
        </div>
        <div class="detail-item" v-if="user.phone">
          <span class="detail-label">手机：</span>
          <span class="detail-value">{{ user.phone }}</span>
        </div>
        <div class="detail-item" v-if="user.createTime">
          <span class="detail-label">注册时间：</span>
          <span class="detail-value">{{ formatDate(user.createTime) }}</span>
        </div>
      </div>
    </div>

    <div class="loading-state" v-else-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div class="empty-state" v-else>
      <p>用户不存在</p>
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

const user = ref(null)
const loading = ref(true)
const isFollowing = ref(false)

const currentUserId = computed(() => store.getters.userId)

const userId = computed(() => Number(route.params.userId))

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

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

const goBack = () => {
  router.back()
}

const loadUserProfile = async () => {
  try {
    loading.value = true
    const res = await request.get(`/user/${userId.value}`)
    if (res.code === 200) {
      user.value = res.data
      await checkFollowStatus()
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  } finally {
    loading.value = false
  }
}

const checkFollowStatus = async () => {
  if (!currentUserId.value || currentUserId.value === userId.value) {
    return
  }
  try {
    const res = await request.get(`/follow/check?userId=${currentUserId.value}&targetUserId=${userId.value}`)
    if (res.code === 200) {
      isFollowing.value = res.data
    }
  } catch (error) {
    console.error('检查关注状态失败:', error)
  }
}

const handleFollow = async () => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res = await request.post('/follow/follow', {
      userId: currentUserId.value,
      targetUserId: userId.value
    })
    if (res.code === 200) {
      isFollowing.value = true
      if (user.value.followerCount !== undefined) {
        user.value.followerCount++
      }
      ElMessage.success('关注成功')
    } else {
      ElMessage.error(res.msg || '关注失败')
    }
  } catch (error) {
    console.error('关注失败:', error)
    ElMessage.error('关注失败')
  }
}

const handleUnfollow = async () => {
  try {
    const res = await request.post('/follow/unfollow', {
      userId: currentUserId.value,
      targetUserId: userId.value
    })
    if (res.code === 200) {
      isFollowing.value = false
      if (user.value.followerCount !== undefined) {
        user.value.followerCount--
      }
      ElMessage.success('已取消关注')
    } else {
      ElMessage.error(res.msg || '取消关注失败')
    }
  } catch (error) {
    console.error('取消关注失败:', error)
    ElMessage.error('取消关注失败')
  }
}

const handleMessage = () => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/messages/${userId.value}`)
}

onMounted(() => {
  if (!userId.value) {
    ElMessage.error('缺少用户ID')
    router.back()
    return
  }
  loadUserProfile()
})
</script>

<style scoped>
.user-profile-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.profile-header {
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

.profile-title {
  flex: 1;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  text-align: center;
  margin-right: 60px;
}

.profile-content {
  flex: 1;
  padding: 20px;
}

.profile-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
}

.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.user-info-section {
  text-align: center;
  margin-bottom: 24px;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.vip-badge {
  display: inline-block;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(255, 215, 0, 0.3);
}

.user-username {
  font-size: 14px;
  color: #999;
  margin-bottom: 12px;
}

.user-bio {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  max-width: 400px;
  margin: 0 auto;
}

.user-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 24px;
  padding: 20px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.follow-button,
.unfollow-button,
.message-button {
  flex: 1;
  max-width: 140px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.follow-button {
  background: #007AFF;
  color: #fff;
}

.follow-button:hover {
  background: #0056b3;
}

.unfollow-button {
  background: #f0f0f0;
  color: #666;
}

.unfollow-button:hover {
  background: #e0e0e0;
}

.message-button {
  background: #fff;
  color: #007AFF;
  border: 1px solid #007AFF;
}

.message-button:hover {
  background: rgba(0, 122, 255, 0.1);
}

.user-details {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.detail-item {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  width: 100px;
  color: #999;
  font-size: 14px;
}

.detail-value {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #999;
  font-size: 14px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #007AFF;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .user-profile-container {
    max-width: 100%;
  }

  .profile-content {
    padding: 12px;
  }

  .profile-card {
    padding: 24px 16px;
  }

  .user-stats {
    gap: 24px;
  }

  .action-buttons {
    flex-direction: column;
  }

  .follow-button,
  .unfollow-button,
  .message-button {
    max-width: 100%;
  }
}
</style>
