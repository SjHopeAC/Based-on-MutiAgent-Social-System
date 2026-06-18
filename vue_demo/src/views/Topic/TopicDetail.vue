<template>
  <div class="topic-detail-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <button class="back-button" @click="goBack">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          返回
        </button>
        <h1 class="page-title">话题详情</h1>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 话题信息卡片 -->
      <div class="topic-info-card">
        <div class="topic-header">
          <div class="topic-meta">
            <span class="topic-category">{{ getCategoryName(topicInfo.categoryId) }}</span>
            <span class="topic-time">{{ formatDate(topicInfo.createTime) }}</span>
            <span v-if="topicInfo.isHot" class="hot-tag">热门</span>
          </div>
          <h2 class="topic-title">{{ topicInfo.topicTitle }}</h2>
          <p class="topic-description">{{ topicInfo.topicDesc }}</p>
          <div class="topic-tags">
            <span 
              v-for="tag in (topicInfo.tags ? topicInfo.tags.split(',') : [])" 
              :key="tag"
              class="topic-tag"
            >
              {{ tag }}
            </span>
          </div>
          <div class="topic-stats">
            <div class="stat-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>{{ topicInfo.viewCount || 0 }} 浏览</span>
            </div>
            <div class="stat-item">
              <button 
                class="stat-button" 
                @click="handleTopicLike"
                :class="{ 'liked': topicInfo.isLiked }"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>{{ topicInfo.likeCount || 0 }} 点赞</span>
              </button>
            </div>
            <div class="stat-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>{{ topicInfo.participantCount || 0 }} 参与</span>
            </div>
            <div class="stat-item">
              <button class="stat-button" @click="handleTopicShare">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8 10L12 14L16 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <span>分享</span>
              </button>
            </div>
          </div>
        </div>
        <div class="topic-creator">
          <div class="creator-avatar" @click.stop="navigateToUserProfile(topicInfo.creatorId)">
            <img 
              :src="getAvatarUrl(topicInfo.creatorAvatar)" 
              alt="创建者头像"
              class="avatar-image"
            />
          </div>
          <div class="creator-info">
            <div class="creator-name">{{ topicInfo.creatorName || '未知用户' }}</div>
            <div class="creator-role">话题创建者</div>
          </div>
          <button 
            :class="['follow-button', { 'following': isFollowingCreator }]"
            @click="toggleFollow(topicInfo.creatorId)"
          >
            {{ isFollowingCreator ? '已关注' : '关注' }}
          </button>
        </div>
      </div>

      <!-- 参与话题区域 -->
      <div class="post-create-card">
        <div class="create-header">
          <div class="user-avatar">
                <img 
                  :src="getAvatarUrl(store.state.userInfo?.avatar)" 
                  alt="用户头像"
                  class="avatar-image"
                />
              </div>
          <h3 class="create-title">参与该话题</h3>
        </div>
        <div class="create-form">
          <p class="participate-desc">分享你的想法，参与到这个话题的讨论中来</p>
          <button 
            class="submit-button participate-button"
            @click="navigateToCreatePost"
          >
            发布帖子
          </button>
        </div>
      </div>

      <!-- 热门帖子列表 -->
      <div class="popular-posts-section">
        <div class="section-header">
          <h3 class="section-title">热门帖子</h3>
          <span class="post-count">{{ posts.length }} 篇帖子</span>
        </div>
        <div class="posts-list">
          <div 
            v-for="post in posts" 
            :key="post.postId"
            class="post-item"
            @click="navigateToPost(post.postId)"
          >
            <div class="post-header">
              <div class="post-author">
                <div class="author-avatar">
                  <img 
                    :src="getAvatarUrl(post.creator?.avatar)" 
                    alt="作者头像"
                    class="avatar-image"
                  />
                </div>
                <div class="author-info">
                  <div class="author-name">{{ post.creator?.nickname || post.creator?.username || '未知用户' }}</div>
                  <div class="post-time">{{ formatDate(post.createTime) }}</div>
                </div>
                <button 
                  v-if="post.creator && post.creator.id !== store.state.userInfo?.id"
                  :class="['post-follow-button', { 'following': post.creator?.following }]"
                  @click.stop="togglePostCreatorFollow(post.creator.id)"
                >
                  {{ post.creator?.following ? '已关注' : '关注' }}
                </button>
              </div>
            </div>
            <div class="post-content">
              <p class="post-text">{{ post.postContent }}</p>
              <div v-if="post.postImgUrl" class="post-images">
                <div class="post-image">
                  <img :src="getImageUrl(post.postImgUrl)" alt="帖子图片" />
                </div>
              </div>
            </div>
            <div class="post-footer">
              <div class="post-stats">
                <button 
                  class="stat-button" 
                  @click.stop="handleLike(post.postId)"
                  :class="{ 'liked': post.isLiked }"
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ post.likeCount || 0 }}</span>
                </button>
                <button class="stat-button" @click.stop="handleComment(post.postId)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>{{ post.commentCount || 0 }}</span>
                </button>
                <button class="stat-button" @click.stop="handleShare(post.postId, post.postContent)">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M8 10L12 14L16 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  <span>分享</span>
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 页码显示 -->
        <div class="pagination">
          <button 
            class="page-button"
            @click="changePage(1)"
            :disabled="currentPage === 1"
          >
            首页
          </button>
          <button 
            class="page-button"
            @click="changePage(currentPage - 1)"
            :disabled="currentPage === 1"
          >
            上一页
          </button>
          
          <button 
            v-for="pageNum in pageNumbers" 
            :key="pageNum"
            :class="['page-button', 'page-number', { 'active': currentPage === pageNum }]"
            @click="changePage(pageNum)"
          >
            {{ pageNum }}
          </button>
          
          <button 
            class="page-button"
            @click="changePage(currentPage + 1)"
            :disabled="currentPage === totalPages"
          >
            下一页
          </button>
          <button 
            class="page-button"
            @click="changePage(totalPages)"
            :disabled="currentPage === totalPages"
          >
            末页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const router = useRouter()
const route = useRoute()
const store = useStore()

// 响应式数据
const topicInfo = ref({})
const posts = ref([])
const newPost = reactive({
  content: '',
  tags: []
})
const selectedTags = ref([])
const tagInput = ref('')
const isFollowingCreator = ref(false)
const loadingMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const totalPosts = ref(0)
const totalPages = computed(() => Math.ceil(totalPosts.value / pageSize.value))
const hasMore = ref(true)

// 计算显示的页码范围
const pageNumbers = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  // 显示当前页附近的页码，最多显示5个
  let start = Math.max(1, current - 2)
  let end = Math.min(total, start + 4)
  
  // 如果后面的页码不够，往前补
  if (end - start < 4) {
    start = Math.max(1, end - 4)
  }
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

// 生命周期钩子
onMounted(() => {
  const userInfo = store.state.userInfo
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  const topicId = route.params.id
  if (topicId) {
    loadTopicInfo(topicId)
    loadPosts(topicId, 1)
  }
})

// 方法定义
const goBack = () => {
  router.back()
}

const navigateToCreatePost = () => {
  router.push({ 
    path: '/post/create',
    query: { topicId: topicInfo.value.topicId, topicTitle: topicInfo.value.topicTitle }
  })
}

const loadTopicInfo = async (topicId) => {
  try {
    // 获取当前用户ID
    const currentUserId = store.getters.userId
    
    // 调用后端API获取话题信息，包含点赞状态
    const res = await request(`/topic/detail/${topicId}?currentUserId=${currentUserId}`)
    const topicData = res.data || {}
    
    topicInfo.value = topicData
    
    // 检查是否关注了创建者
    if (topicInfo.value.creatorId) {
      checkFollowStatus(topicInfo.value.creatorId)
      // 获取创建者信息
      await loadCreatorInfo(topicInfo.value.creatorId)
    }
  } catch (error) {
    console.error('加载话题信息失败:', error)
    ElMessage.error('加载话题信息失败')
  }
}

const loadCreatorInfo = async (userId) => {
  try {
    const res = await request(`/user/${userId}`)
    const userInfo = res.data || {}
    
    // 更新话题创建者信息
    if (userInfo) {
      topicInfo.value.creatorName = userInfo.nickname || '未知用户'
      topicInfo.value.creatorAvatar = userInfo.avatar
    }
  } catch (error) {
    console.error('加载创建者信息失败:', error)
    // 失败时不影响页面显示
  }
}

const loadPosts = async (topicId, pageNum = 1) => {
  try {
    loadingMore.value = true
    
    // 获取当前登录用户ID
    const userInfo = store.state.userInfo
    const currentUserId = userInfo?.id || null
    
    // 使用新的API端点获取话题相关的帖子，包含关注状态
    const res = await request(`/post/selectWithFollow?page=${pageNum}&pagesize=${pageSize.value}&topic=${topicId}&ishot=true&currentUserId=${currentUserId}`)
    
    // 处理API返回的数据格式
    const postList = res.data?.list || []
    totalPosts.value = res.data?.total || 0
    
    // 后端已经补充了用户信息和关注状态，不需要重新请求
    posts.value = postList
    currentPage.value = pageNum
    
    hasMore.value = postList.length === pageSize.value
  } catch (error) {
    console.error('加载帖子失败:', error)
    ElMessage.error('加载帖子失败')
  } finally {
    loadingMore.value = false
  }
}

const loadMorePosts = () => {
  if (hasMore.value && !loadingMore.value) {
    const topicId = route.params.id
    loadPosts(topicId, currentPage.value + 1)
  }
}

// 导航到帖子详情页
const navigateToPost = (postId) => {
  router.push({
    path: `/post/detail/${postId}`,
    query: {
      from: `topic/${route.params.id}` // 记录来源，方便返回
    }
  })
}

const navigateToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
}

const changePage = (pageNum) => {
  if (pageNum >= 1 && pageNum <= totalPages.value && pageNum !== currentPage.value) {
    const topicId = route.params.id
    loadPosts(topicId, pageNum)
  }
}

const publishPost = async () => {
  if (!newPost.content.trim()) {
    ElMessage.warning('请输入帖子内容')
    return
  }
  
  try {
    const topicId = route.params.id
    const userInfo = store.state.userInfo
    const requestData = {
      topicId: parseInt(topicId),
      content: newPost.content.trim(),
      tags: selectedTags.value.join(','),
      authorId: userInfo?.id || null
    }
    
    const res = await request.post('/post/create', requestData)
    ElMessage.success('帖子发布成功！')
    
    // 清空表单
    newPost.content = ''
    selectedTags.value = []
    tagInput.value = ''
    
    // 刷新帖子列表
    loadPosts(topicId)
  } catch (error) {
    console.error('发布帖子失败:', error)
    ElMessage.error('发布帖子失败')
  }
}

const addTag = () => {
  if (tagInput.value.trim() && !selectedTags.value.includes(tagInput.value.trim())) {
    if (selectedTags.value.length < 5) {
      selectedTags.value.push(tagInput.value.trim())
      tagInput.value = ''
    } else {
      ElMessage.warning('最多添加5个标签')
    }
  }
}

const removeTag = (tag) => {
  selectedTags.value = selectedTags.value.filter(t => t !== tag)
}

const toggleFollow = async (userId) => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.error('请先登录')
      return
    }
    
    const res = await request(`/users/${userId}/follow`, 'POST', {
      currentUserId: userInfo.id
    })
    
    if (res.code === 200 || res.success) {
      isFollowingCreator.value = !isFollowingCreator.value
      ElMessage.success(isFollowingCreator.value ? '关注成功' : '取消关注成功')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('关注操作失败')
  }
}

const checkFollowStatus = async (userId) => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      isFollowingCreator.value = false
      return
    }
    
    // 使用正确的接口检查关注状态
    const activeUsersRes = await request(`/users/active?currentUserId=${userInfo.id}`)
    const activeUsers = activeUsersRes.data || []
    
    // 查找目标用户的关注状态
    const targetUser = activeUsers.find(user => user.id === userId)
    isFollowingCreator.value = targetUser?.following || false
  } catch (error) {
    console.error('检查关注状态失败:', error)
    isFollowingCreator.value = false
  }
}

// 关注/取消关注帖子创建者
const togglePostCreatorFollow = async (creatorId) => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo?.id) {
      ElMessage.error('请先登录')
      return
    }
    
    const res = await request(`/users/${creatorId}/follow`, 'POST', {
      currentUserId: userInfo.id
    })
    
    if (res.code === 200 || res.success) {
      // 更新对应帖子的关注状态
      const post = posts.value.find(p => p.creator?.id === creatorId)
      if (post && post.creator) {
        post.creator.following = !post.creator.following
      }
      ElMessage.success(post?.creator?.following ? '关注成功' : '取消关注成功')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('关注操作失败')
  }
}

// 话题点赞
const handleTopicLike = async () => {
  try {
    // 检查话题ID是否有效
    if (!topicInfo.value.topicId) {
      console.error('话题ID无效:', topicInfo.value.topicId)
      ElMessage.error('话题信息未加载完成')
      return
    }
    
    // 确保isLiked有默认值
    if (topicInfo.value.isLiked === undefined) {
      topicInfo.value.isLiked = false
    }
    
    // 获取当前用户ID
    const currentUserId = store.getters.userId
    if (!currentUserId) {
      ElMessage.warning('请先登录后再点赞')
      return
    }
    
    console.log('话题ID:', topicInfo.value.topicId)
    console.log('当前点赞状态:', topicInfo.value.isLiked)
    
    // 计算点赞变化量
    const count = topicInfo.value.isLiked ? -1 : 1
    console.log('点赞变化量:', count)
    console.log('请求路径:', `/topic/like?topicId=${topicInfo.value.topicId}&count=${count}&currentUserId=${currentUserId}`)
    
    // 调用话题点赞API，使用正确的参数传递方式（baseURL已包含/api前缀）
    const res = await request.post(`/topic/like?topicId=${topicInfo.value.topicId}&count=${count}&currentUserId=${currentUserId}`)
    
    console.log('点赞API响应:', res)
    
    // 更新话题的点赞状态
    if (res && (res.code === 200 || res.success)) {
      topicInfo.value.isLiked = !topicInfo.value.isLiked
      topicInfo.value.likeCount = topicInfo.value.isLiked 
        ? (topicInfo.value.likeCount || 0) + 1 
        : Math.max(0, (topicInfo.value.likeCount || 1) - 1)
      
      ElMessage.success('操作成功')
    } else {
      ElMessage.error(res?.msg || '点赞操作失败')
    }
  } catch (error) {
    console.error('话题点赞操作失败:', error)
    ElMessage.error('点赞操作失败')
  }
}

// 帖子点赞
const handleLike = async (postId) => {
  try {
    const userId = store.getters.userId
    if (!userId || userId === '') {
      ElMessage.warning('请先登录后再点赞')
      return
    }
    
    const post = posts.value.find(p => p.postId === postId)
    if (!post) {
      ElMessage.error('帖子信息未找到')
      return
    }
    
    const isLiked = post.isLiked || false
    const count = isLiked ? -1 : 1
    
    await request.put(`/post/like/${postId}?count=${count}&userId=${userId}`)
    
    post.isLiked = !isLiked
    post.likeCount += count
    
    ElMessage.success(isLiked ? '取消点赞成功' : '点赞成功')
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const handleComment = (postId) => {
  // 跳转到评论页面或展开评论区
  ElMessage.info('评论功能开发中')
}

const handleShare = (postId, postContent) => {
  // 分享功能
  router.push({
    path: '/share/select-users',
    query: {
      type: 'post',
      id: postId,
      content: postContent || '分享了一篇帖子给你'
    }
  })
}

const handleTopicShare = () => {
  // 分享话题功能
  router.push({
    path: '/share/select-users',
    query: {
      type: 'topic',
      id: topicInfo.value.topicId,
      content: topicInfo.value.topicTitle || '分享了一个话题给你'
    }
  })
}

// 辅助函数
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

const getImageUrl = (image) => {
  if (!image) {
    return 'https://picsum.photos/400/300'
  }
  
  if (image.startsWith('http://') || image.startsWith('https://')) {
    return image
  }
  
  let imagePath = image
  if (!imagePath.startsWith('/')) {
    imagePath = '/' + imagePath
  }
  
  return `http://localhost:8088/api/upload/post${imagePath}`
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diff = now - date
  
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
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

const getCategoryName = (categoryId) => {
  const categoryMap = {
    1: '技术',
    2: '生活',
    3: '职业',
    4: '学习'
  }
  
  return categoryMap[categoryId] || '未知分类'
}
</script>

<style scoped>
.topic-detail-page {
  min-height: 100vh;
  background-color: #f5f5f7;
}

.page-header {
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  padding: 16px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
}

.back-button {
  background: none;
  border: none;
  color: #333;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  margin-left: 24px;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 话题信息卡片 */
.topic-info-card {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.topic-category {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.topic-time {
  font-size: 12px;
  color: #6e6e73;
}

.hot-tag {
  background-color: rgba(255, 59, 48, 0.1);
  color: #FF3B30;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.topic-title {
  margin: 0 0 16px 0;
  font-size: 24px;
  font-weight: 600;
  line-height: 1.3;
}

.topic-description {
  margin: 0 0 20px 0;
  font-size: 16px;
  line-height: 1.5;
  color: #333;
}

.topic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.topic-tag {
  background-color: #f0f0f0;
  color: #666;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
}

.topic-stats {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #e0e0e0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.stat-item svg {
  color: #888;
}

.topic-creator {
  display: flex;
  align-items: center;
  gap: 16px;
}

.creator-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.creator-info {
  flex: 1;
}

.creator-name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 4px;
}

.creator-role {
  font-size: 12px;
  color: #6e6e73;
}

.follow-button {
  background-color: #007AFF;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.follow-button:hover {
  background-color: #0056b3;
}

.follow-button.following {
  background-color: #f0f0f0;
  color: #666;
}

.follow-button.following:hover {
  background-color: #e0e0e0;
}

.post-follow-button {
  background-color: #007AFF;
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-left: auto;
}

.post-follow-button:hover {
  background-color: #0056b3;
}

.post-follow-button.following {
  background-color: #f0f0f0;
  color: #666;
}

.post-follow-button.following:hover {
  background-color: #e0e0e0;
}

/* 发布帖子区域 */
.post-create-card {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.create-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
}

.create-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-content {
  width: 100%;
  padding: 16px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  font-family: inherit;
}

.post-content:focus {
  outline: none;
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.form-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.post-tags {
  flex: 1;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.selected-tag {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.remove-tag {
  background: none;
  border: none;
  color: #007AFF;
  font-size: 16px;
  cursor: pointer;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.remove-tag:hover {
  background-color: rgba(0, 122, 255, 0.2);
}

.tag-input {
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 6px 12px;
  font-size: 12px;
  width: 120px;
  transition: all 0.2s ease;
}

.tag-input:focus {
  outline: none;
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
  width: 150px;
}

.submit-button {
  background-color: #007AFF;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.submit-button:hover:not(:disabled) {
  background-color: #0056b3;
}

.submit-button:disabled {
  background-color: #e0e0e0;
  color: #999;
  cursor: not-allowed;
}

/* 热门帖子列表 */
.popular-posts-section {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.post-count {
  font-size: 14px;
  color: #6e6e73;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 20px;
}

.post-item {
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.post-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.post-header {
  margin-bottom: 16px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.author-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.author-name {
  font-size: 14px;
  font-weight: 500;
}

.post-time {
  font-size: 12px;
  color: #6e6e73;
}

.post-text {
  margin: 0 0 16px 0;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}

.post-images {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.post-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-footer {
  display: flex;
  justify-content: flex-end;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-button {
  background: none;
  border: none;
  color: #666;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.stat-button:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #333;
}

.stat-button.liked {
  color: #FF3B30;
}

.stat-button.liked svg {
  color: #FF3B30;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}

.load-more-button {
  background-color: #f5f5f7;
  color: #666;
  border: 1px solid #e0e0e0;
  padding: 10px 20px;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.load-more-button:hover:not(:disabled) {
  background-color: #e0e0e0;
}

.load-more-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
}

.page-button {
  background-color: #f5f5f7;
  color: #666;
  border: 1px solid #e0e0e0;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.page-button:hover:not(:disabled) {
  background-color: #e0e0e0;
  color: #333;
}

.page-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.page-number {
  min-width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.page-number.active {
  background-color: #007AFF;
  color: white;
  border-color: #007AFF;
}

.page-number.active:hover {
  background-color: #0056b3;
  border-color: #0056b3;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 16px;
  }
  
  .topic-info-card,
  .post-create-card,
  .popular-posts-section {
    padding: 16px;
  }
  
  .topic-title {
    font-size: 20px;
  }
  
  .form-footer {
    flex-direction: column;
    align-items: stretch;
  }
  
  .post-tags {
    justify-content: flex-start;
  }
  
  .submit-button {
    align-self: stretch;
  }
}
</style>