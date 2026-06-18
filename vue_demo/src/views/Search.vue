<template>
  <div class="search-container">
    <div class="search-header">
      <button class="back-button" @click="goBack">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M15 18L9 12L15 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        返回
      </button>
      <h2 class="page-title">搜索</h2>
    </div>

    <!-- 默认搜索选项（热门话题、热门标签） -->
    <div v-if="!hasSearched && hotSearchOptions" class="hot-search-section">
      <div class="hot-topics">
        <h3 class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M13 2L3 14H9L21 22L13 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          热门话题
        </h3>
        <div class="hot-topics-list">
          <div 
            v-for="topic in hotSearchOptions.hotTopics" 
            :key="topic.topicId"
            class="hot-topic-item"
            @click="searchByTopic(topic.topicTitle)"
          >
            <span class="hot-topic-title">{{ topic.topicTitle }}</span>
            <span class="hot-topic-stats">
              <span class="stat-item">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                {{ topic.likeCount }}
              </span>
              <span class="stat-item">
                <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M1 12S5 4 12 4s11 8 11 8 11 8-5 8-11 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                {{ topic.viewCount }}
              </span>
            </span>
          </div>
        </div>
      </div>

      <div class="hot-tags">
        <h3 class="section-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20.59 13.41L13.42 20.58C13.2343 20.766 13.0137 20.89 12.78 20.94L12 21L11.22 20.94C10.9863 20.89 10.7657 20.766 10.58 20.58L3.41 13.41C3.05478 13.0548 2.85693 12.5417 2.85693 12C2.85693 11.4583 3.05478 10.9452 3.41 10.59L10.59 3.41C10.9452 3.05478 11.4583 2.85693 12 2.85693C12.5417 2.85693 13.0548 3.05478 13.41 3.41L20.58 10.59C20.9452 10.9452 21.1431 11.4583 21.1431 12C21.1431 12.5417 20.9452 13.0548 20.59 13.41ZM12 18C12.5523 18 13 17.5523 13 17V7C13 6.44772 12.5523 6 12 6C11.4477 6 11 6.44772 11 7V17C11 17.5523 11.4477 18 12 18Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          热门标签
        </h3>
        <div class="hot-tags-list">
          <span 
            v-for="(tag, index) in hotSearchOptions.hotTags" 
            :key="index"
            class="hot-tag-item"
            @click="searchByTag(tag)"
          >
            #{{ tag }}
          </span>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div v-if="hasSearched" class="search-results">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>搜索中...</p>
      </div>

      <div v-else-if="searchResults" class="results-content">
        <!-- 话题搜索结果 -->
        <div v-if="searchResults.topics && searchResults.topics.length > 0" class="result-section">
          <h3 class="result-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 22C12.5523 22 13 21.5523 13 21V17H11V21C11 21.5523 11.4477 22 12 22Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            话题 ({{ searchResults.topics.length }})
          </h3>
          <div class="result-list">
            <div 
              v-for="topic in searchResults.topics" 
              :key="topic.topicId"
              class="result-item topic-item"
              @click="goToTopicDetail(topic.topicId)"
            >
              <div class="result-content">
                <h4 class="result-title-text">{{ topic.topicTitle }}</h4>
                <p class="result-desc">{{ topic.topicDesc }}</p>
                <div class="result-meta">
                  <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ topic.likeCount }}
                  </span>
                  <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M1 12S5 4 12 4s11 8 11 8 11 8-5 8-11 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ topic.viewCount }}
                  </span>
                  <span class="meta-item">{{ formatTime(topic.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 帖子搜索结果 -->
        <div v-if="searchResults.posts && searchResults.posts.length > 0" class="result-section">
          <h3 class="result-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M14 2H6C5.34315 2 5 2.34315 5 3V21C5 21.5523 5.44772 22 6 22H18C18.5523 22 19 21.5523 19 21V7L14 2Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M14 2V7H19" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 13H8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 17H8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M10 9H8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            帖子 ({{ searchResults.posts.length }})
          </h3>
          <div class="result-list">
            <div 
              v-for="post in searchResults.posts" 
              :key="post.postId"
              class="result-item post-item"
              @click="goToPostDetail(post.postId)"
            >
              <div class="result-content">
                <h4 class="result-title-text">{{ post.postContent.substring(0, 100) }}{{ post.postContent.length > 100 ? '...' : '' }}</h4>
                <div class="result-meta">
                  <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ post.likeCount }}
                  </span>
                  <span class="meta-item">
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ post.commentCount }}
                  </span>
                  <span class="meta-item">{{ formatTime(post.createTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户搜索结果 -->
        <div v-if="searchResults.users && searchResults.users.length > 0" class="result-section">
          <h3 class="result-title">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0781 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 11C14.2091 11 16 9.20914 16 7C16 4.79086 14.2091 3 12 3C9.79086 3 8 4.79086 8 7C8 9.20914 9.79086 11 12 11Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            用户 ({{ searchResults.users.length }})
          </h3>
          <div class="result-list user-list">
            <div 
              v-for="user in searchResults.users" 
              :key="user.id"
              class="result-item user-item"
            >
              <div class="user-main" @click="goToUserProfile(user.id)">
                <div class="user-avatar">
                  <img :src="getAvatarUrl(user.avatar)" :alt="user.nickname || user.username" />
                </div>
                <div class="user-info">
                  <div class="user-name-row">
                    <h4 class="user-name">{{ user.nickname || user.username }}</h4>
                    <span v-if="user.isVip" class="vip-badge">VIP</span>
                    <span v-if="user.isSpamUser" class="spam-badge">劣迹</span>
                  </div>
                  <p class="user-bio">{{ user.bio || '这个人很懒，什么都没写' }}</p>
                  <div class="user-stats">
                    <span class="stat-item">{{ user.followCount || 0 }} 关注</span>
                    <span class="stat-item">{{ user.fansCount || 0 }} 粉丝</span>
                    <span class="stat-item">{{ user.postCount || 0 }} 帖子</span>
                  </div>
                </div>
              </div>
              <button 
                v-if="currentUserId && currentUserId !== user.id"
                :class="['follow-button', { 'following': user.following }]"
                @click.stop="toggleFollow(user)"
              >
                {{ user.following ? '已关注' : '关注' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 无搜索结果 -->
        <div v-if="!searchResults.topics?.length && !searchResults.posts?.length && !searchResults.users?.length" class="empty-state">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 21L16.65 16.65M19 11C19 15.4183 15.4183 19 11 19C6.58172 19 3 15.4183 3 11C3 6.58172 6.58172 3 11 3C15.4183 3 19 6.58172 19 11Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h3>未找到相关内容</h3>
          <p>试试其他关键词或浏览热门话题</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const store = useStore()

const searchQuery = ref('')
const hasSearched = ref(false)
const loading = ref(false)
const searchResults = ref(null)
const hotSearchOptions = ref(null)

const currentUserId = computed(() => store.getters.userId)

const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
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

const formatTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const createTime = new Date(time)
  const diffMs = now - createTime
  const diffMinutes = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  
  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffHours < 24) return `${diffHours}小时前`
  if (diffDays < 7) return `${diffDays}天前`
  return createTime.toLocaleDateString()
}

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    return
  }
  
  loading.value = true
  hasSearched.value = true
  
  try {
    const userId = currentUserId.value
    const url = userId 
      ? `/search?keyword=${encodeURIComponent(searchQuery.value.trim())}&currentUserId=${userId}`
      : `/search?keyword=${encodeURIComponent(searchQuery.value.trim())}`
    const res = await request(url)
    if (res.code === 200 || res.success) {
      searchResults.value = res.data
    }
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleFollow = async (user) => {
  if (!currentUserId.value) {
    ElMessage.warning('请先登录后再关注用户')
    return
  }
  
  try {
    const oldFollowing = user.following || false
    const newFollowing = !oldFollowing
    
    user.following = newFollowing
    
    const res = await request.post('/follow/follow', {
      userId: currentUserId.value,
      targetUserId: user.id
    })
    
    if (res.code === 200 || res.success) {
      ElMessage.success(newFollowing ? '关注成功' : '取消关注成功')
    } else {
      user.following = oldFollowing
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    user.following = oldFollowing
    ElMessage.error('关注操作失败')
  }
}

const searchByTopic = (topicTitle) => {
  searchQuery.value = topicTitle
  handleSearch()
}

const searchByTag = (tag) => {
  searchQuery.value = tag
  handleSearch()
}

const goToTopicDetail = (topicId) => {
  router.push(`/topic/detail/${topicId}`)
}

const goToPostDetail = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
}

const goBack = () => {
  router.back()
}

const loadHotSearchOptions = async () => {
  try {
    const res = await request('/search/hot')
    if (res.code === 200 || res.success) {
      hotSearchOptions.value = res.data
    }
  } catch (error) {
    console.error('加载热门搜索失败:', error)
  }
}

onMounted(() => {
  loadHotSearchOptions()
  
  // 检查是否有从URL传递的搜索关键词
  const keyword = route.query.keyword
  if (keyword) {
    searchQuery.value = keyword
    handleSearch()
  }
})
</script>

<style scoped>
.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  min-height: calc(100vh - 80px);
}

.search-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #f5f5f7;
  color: #1d1d1f;
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: #e5e5ea;
  transform: translateX(-2px);
}

.back-button svg {
  color: #1d1d1f;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.hot-search-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title svg {
  color: #007AFF;
}

.hot-topics-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-topic-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background-color: white;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e5e5ea;
}

.hot-topic-item:hover {
  background-color: #f5f5f7;
  border-color: #007AFF;
  transform: translateX(4px);
}

.hot-topic-title {
  font-size: 15px;
  font-weight: 500;
  color: #1d1d1f;
}

.hot-topic-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6e6e73;
}

.stat-item svg {
  color: #86868b;
}

.hot-tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hot-tag-item {
  display: inline-block;
  padding: 8px 16px;
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hot-tag-item:hover {
  background-color: rgba(0, 122, 255, 0.2);
  transform: translateY(-2px);
}

.search-results {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
  gap: 16px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #e5e5ea;
  border-top-color: #007AFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  color: #6e6e73;
  font-size: 14px;
}

.results-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.result-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e5e5ea;
}

.result-title svg {
  color: #007AFF;
}

.result-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-item {
  padding: 16px;
  background-color: #f5f5f7;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e5e5ea;
}

.result-item:hover {
  background-color: white;
  border-color: #007AFF;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.15);
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.result-title-text {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.result-desc {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.result-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #6e6e73;
}

.meta-item svg {
  color: #86868b;
}

.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: #f5f5f7;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid #e5e5ea;
}

.user-item:hover {
  background-color: white;
  border-color: #007AFF;
  transform: translateX(4px);
}

.user-main {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
  cursor: pointer;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.vip-badge {
  display: inline-block;
  padding: 2px 6px;
  background: linear-gradient(135deg, #ffd700, #ffb347);
  color: white;
  font-size: 10px;
  font-weight: 600;
  border-radius: 4px;
}

.spam-badge {
  display: inline-block;
  padding: 2px 6px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a5a);
  color: white;
  font-size: 10px;
  font-weight: 600;
  border-radius: 4px;
}

.follow-button {
  padding: 8px 16px;
  border-radius: 20px;
  border: none;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: #007AFF;
  color: white;
  flex-shrink: 0;
}

.follow-button:hover {
  background-color: #0056b3;
  transform: scale(1.05);
}

.follow-button.following {
  background-color: #f5f5f7;
  color: #6e6e73;
  border: 1px solid #e5e5ea;
}

.follow-button.following:hover {
  background-color: #ff3b30;
  color: white;
  border-color: #ff3b30;
}

.user-bio {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  line-height: 1.4;
}

.user-stats {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 64px 24px;
  gap: 16px;
  text-align: center;
}

.empty-state svg {
  color: #86868b;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.empty-state p {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

@media (max-width: 768px) {
  .search-container {
    padding: 16px;
  }
  
  .hot-search-section {
    grid-template-columns: 1fr;
  }
  
  .search-input-wrapper {
    padding: 10px 12px;
  }
  
  .search-button {
    padding: 8px 16px;
    font-size: 14px;
  }
  
  .result-item {
    padding: 12px;
  }
  
  .user-item {
    padding: 12px;
  }
  
  .user-avatar {
    width: 48px;
    height: 48px;
  }
}
</style>
