<template>
  <div class="post-detail-container">
    <!-- 返回按钮 -->
    <div class="back-button-container">
      <button class="back-button" @click="goBack">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M20 11H7.83L13.42 5.41L12 4L4 12L12 20L13.41 18.59L7.83 13H20V11Z" fill="currentColor"/>
        </svg>
        <span>返回</span>
      </button>
    </div>
    
    <!-- 帖子详情 -->
    <div class="post-detail-card">
      <div class="post-header">
        <div class="post-author">
          <img :src="getAvatarUrl(post.creator?.avatar)" class="author-avatar" @click.stop="navigateToUserProfile(post.creatorId)" />
          <div class="author-info">
            <h4 class="author-name">{{ post.creator?.nickName || post.creator?.username || '未知用户' }}</h4>
            <div class="post-meta">
              <span class="post-time">{{ formatTime(post.createTime) }}</span>
              <span class="post-topic-inline" v-if="post.topicId">
                {{ getTopicName(post.topicId) }}
              </span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="post-content">
        {{ post.postContent }}
      </div>
      
      <!-- 帖子照片 -->
      <div class="post-media" v-if="post.postImgUrl">
        <div class="media-grid">
          <div class="media-item">
            <img 
              :src="getImageUrl(post.postImgUrl)" 
              alt="帖子图片"
              class="post-photo"
            />
          </div>
        </div>
      </div>
      
      <div class="post-footer">
        <div class="post-stats">
          <button :class="['stat-button', { 'liked': post.isLiked }]" @click="handleLike">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>{{ post.likeCount || 0 }}</span>
          </button>
          <div class="stat-button">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 11.5C21 16.75 16.75 21 11.5 21C6.25 21 2 16.75 2 11.5C2 6.25 6.25 2 11.5 2C16.75 2 21 6.25 21 11.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M8 14V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M16 14V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 10V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>{{ post.viewCount || 0 }}</span>
          </div>
          <div class="stat-button">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>{{ post.commentCount || 0 }}</span>
          </div>
          <button class="stat-button" @click="handleShare">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M8 10L12 14L16 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>分享</span>
          </button>
        </div>
      </div>
    </div>
    
    <!-- 评论区域 -->
    <div class="comments-section">
      <h3 class="comments-title">评论 ({{ commentList.length }})</h3>
      
      <!-- 评论输入框 -->
      <div class="comment-input-section">
        <textarea 
          v-model="newComment" 
          placeholder="写下你的评论..."
          class="comment-textarea"
        ></textarea>
        <button @click="submitComment" class="submit-comment-button">提交评论</button>
      </div>
      
      <!-- 一级评论列表 -->
      <div class="comment-list">
        <div v-for="comment in commentList" :key="comment.commentId" class="comment-item">
          <!-- 评论用户信息 -->
          <div class="comment-header">
            <img :src="getAvatarUrl(comment.creator?.avatar)" class="avatar" />
            <span class="username">{{ comment.creator?.nickName || comment.creator?.username || '未知用户' }}</span>
            <span class="time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <!-- 回复对象 -->
          <div v-if="comment.replyToUser" class="reply-to">
            回复 @{{ comment.replyToUser?.nickName || comment.replyToUser?.username || '未知用户' }}
          </div>
          <!-- 评论内容 -->
          <div class="comment-content">{{ comment.commentContent }}</div>
          <!-- 评论操作 -->
          <div class="comment-actions">
            <button @click="likeComment(comment.commentId)">点赞 {{ comment.likeCount }}</button>
            <button @click="showReplyBox(comment)">回复</button>
            <button @click="reportComment(comment)" class="report-button">举报</button>
          </div>
          <!-- 回复框（点击回复显示） -->
          <div v-if="replyCommentId === comment.commentId" class="reply-box">
            <div class="reply-info">
              回复 @{{ comment.creator?.nickName || comment.creator?.username || '未知用户' }}
            </div>
            <textarea v-model="replyContent" :placeholder="`回复 ${comment.creator?.nickName || comment.creator?.username || '未知用户'}...`"></textarea>
            <div class="reply-actions">
              <button @click="cancelReply" class="cancel-reply-button">取消</button>
              <button @click="submitReply(comment.commentId, comment.creator)" class="submit-reply-button">提交回复</button>
            </div>
          </div>
          <!-- 子评论列表（递归渲染） -->
          <div class="child-comment-list" v-if="comment.children && comment.children.length">
            <CommentItem 
              v-for="child in comment.children" 
              :key="child.commentId" 
              :comment="child"
              :post-id="postId"
              @like="likeComment"
              @submit-reply="getCommentList"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import CommentItem from './CommentItem.vue' // 递归组件

const route = useRoute()
const router = useRouter()
const store = useStore()

const postId = ref(Number(route.params.postId))
const post = ref({})
const commentList = ref([])
const newComment = ref('')
const replyCommentId = ref(null)
const replyContent = ref('')
const topics = ref([])

const currentUserId = computed(() => store.getters.userId)

// 返回上一页
const goBack = () => {
  const from = route.query.from
  if (from && from.startsWith('topic/')) {
    // 如果是从话题详情页来的，返回对应的话题详情页
    router.push({ path: `/${from}` })
  } else {
    // 否则返回上一页
    router.back()
  }
}

const navigateToUserProfile = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`)
  }
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

// 获取图片URL
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

// 加载话题列表
const loadTopics = async () => {
  try {
    const res = await request('/topic')
    topics.value = res || []
  } catch (error) {
    console.error('加载话题失败:', error)
  }
}

// 根据话题ID获取话题名称
const getTopicName = (topicId) => {
  if (!topicId) return ''
  const topic = topics.value.find(t => t.topicId === topicId)
  return topic ? topic.topicTitle : '未知话题'
}

// 获取帖子详情
const getPostDetail = async () => {
  try {
    const url = currentUserId.value 
      ? `/post/${postId.value}?currentUserId=${currentUserId.value}`
      : `/post/${postId.value}`
    const res = await request(url)
    post.value = res.data || {}
  } catch (error) {
    console.error('获取帖子详情失败:', error)
    ElMessage.error('获取帖子详情失败')
  }
}

// 获取评论列表
const getCommentList = async () => {
  try {
    const res = await request(`/comment/post/${postId.value}`)
    commentList.value = res.data || []
  } catch (error) {
    console.error('获取评论列表失败:', error)
    ElMessage.error('获取评论列表失败')
  }
}

// 帖子点赞
const handleLike = async () => {
  try {
    const userId = currentUserId.value
    if (!userId || userId === '') {
      ElMessage.warning('请先登录后再点赞')
      return
    }
    
    const isLiked = post.value.isLiked || false
    const count = isLiked ? -1 : 1
    
    await request.put(`/post/like/${postId.value}?count=${count}&userId=${userId}`)
    
    post.value.isLiked = !isLiked
    post.value.likeCount += count
    
    ElMessage.success(isLiked ? '取消点赞成功' : '点赞成功')
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败')
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  
  try {
    const userInfo = store.state.userInfo
    if (!userInfo) {
      ElMessage.warning('请先登录')
      return
    }
    
    // 调用垃圾评论检测接口
    ElMessage.info('正在检测评论...')
    
    const detectRes = await fetch(`http://127.0.0.1:8000/predict?text=${encodeURIComponent(newComment.value.trim())}&user_id=user_${userInfo.id}&post_id=${postId.value}`)
    const detectData = await detectRes.json()
    
    // 根据检测结果设置评论类型和状态
    let commentType = 0 // 待评评论
    let status = 1 // 正常状态
    
    if (detectData.result === '是') {
      // 垃圾评论
      commentType = -2
      status = 0 // 封禁状态
    } else if (detectData.result === '否') {
      // 正常评论
      commentType = 1
      status = 1
    }
    
    // 提交评论到后端，包含检测结果
    await request.post('/comment/create', {
      postId: postId.value,
      parentId: null,
      creatorId: userInfo.id,
      commentContent: newComment.value.trim(),
      commentType: commentType,
      status: status,
      confidence: detectData.confidence,
      reasoning: detectData.reasoning,
      result: detectData.result,
      source: detectData.source
    })
    
    newComment.value = ''
    await getCommentList()
    
    if (commentType === -2) {
      ElMessage.warning('您的评论经过判定认为是垃圾评论，已被封禁')
    } else {
      ElMessage.success('您的评论已被成功发布')
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败')
  }
}

// 点赞评论
const likeComment = async (commentId) => {
  try {
    await request.put(`/comment/like/${commentId}?count=1`)
    
    // 本地更新点赞数
    const updateLikeCount = (comments) => {
      for (const comment of comments) {
        if (comment.commentId === commentId) {
          comment.likeCount++
          return true
        }
        if (comment.children && comment.children.length) {
          if (updateLikeCount(comment.children)) {
            return true
          }
        }
      }
      return false
    }
    
    updateLikeCount(commentList.value)
    ElMessage.success('点赞成功')
  } catch (error) {
    console.error('点赞评论失败:', error)
    ElMessage.error('点赞失败')
  }
}

// 显示回复框
const showReplyBox = (comment) => {
  replyCommentId.value = comment.commentId
}

// 取消回复
const cancelReply = () => {
  replyCommentId.value = null
  replyContent.value = ''
}

// 提交回复
const submitReply = async (parentId, replyToUser) => {
  if (!replyContent.value.trim()) {
    ElMessage.warning('请输入回复内容')
    return
  }
  
  try {
    const userInfo = store.state.userInfo
    if (!userInfo) {
      ElMessage.warning('请先登录')
      return
    }
    
    // 调用垃圾评论检测接口
    ElMessage.info('正在检测评论...')
    
    const detectRes = await fetch(`http://127.0.0.1:8000/predict?text=${encodeURIComponent(replyContent.value.trim())}&user_id=user_${userInfo.id}&post_id=${postId.value}`)
    const detectData = await detectRes.json()
    
    // 根据检测结果设置评论类型和状态
    let commentType = 0 // 待评评论
    let status = 1 // 正常状态
    
    if (detectData.result === '是') {
      // 垃圾评论
      commentType = -2
      status = 0 // 封禁状态
    } else if (detectData.result === '否') {
      // 正常评论
      commentType = 1
      status = 1
    }
    
    // 提交回复到后端，包含检测结果
    await request.post('/comment/create', {
      postId: postId.value,
      parentId: parentId,
      creatorId: userInfo.id,
      commentContent: replyContent.value.trim(),
      commentType: commentType,
      status: status,
      confidence: detectData.confidence,
      reasoning: detectData.reasoning,
      result: detectData.result,
      source: detectData.source
    })
    
    replyContent.value = ''
    replyCommentId.value = null
    await getCommentList()
    
    if (commentType === -2) {
      ElMessage.warning('回复已被判定为垃圾评论，已自动封禁')
    } else {
      ElMessage.success(`回复 ${replyToUser?.nickName || replyToUser?.username || '未知用户'} 成功`)
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

// 格式化时间
const formatTime = (timeString) => {
  if (!timeString) return ''
  
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  
  // 计算时间差
  const seconds = Math.floor(diff / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  const days = Math.floor(hours / 24)
  
  // 根据时间差返回不同格式
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

// 举报评论
const reportComment = async (comment) => {
  try {
    // 检查是否是自己的评论
    if (comment.creatorId === currentUserId.value) {
      ElMessage.warning('不能举报自己的评论')
      return
    }
    
    // 检查是否已经举报过
    if (comment.appealed) {
      ElMessage.warning('该评论已被举报')
      return
    }
    
    await ElMessageBox.confirm('确定要举报这条评论吗？举报后将提交给管理员审核。', '举报确认', {
      confirmButtonText: '确定举报',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.post(`/comment/${comment.commentId}/report`)
    if (res.code === 200) {
      ElMessage.success('举报成功，请等待管理员审核')
      comment.appealed = true
    } else {
      ElMessage.error(res.msg || '举报失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('举报评论失败:', error)
      ElMessage.error('举报评论失败')
    }
  }
}

// 分享帖子
const handleShare = () => {
  router.push({
    path: '/share/select-users',
    query: {
      type: 'post',
      id: postId.value,
      content: post.value.postContent || '分享了一篇帖子给你'
    }
  })
}

// 增加帖子浏览量
const increaseViewCount = async () => {
  try {
    await request.post(`/post/view/${postId.value}`)
    console.log('浏览量增加成功')
  } catch (error) {
    console.error('增加浏览量失败:', error)
  }
}

// 初始化数据
onMounted(async () => {
  const userInfo = store.state.userInfo
  if (!userInfo || !userInfo.id) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  await loadTopics()
  getPostDetail()
  getCommentList()
  increaseViewCount()
})
</script>

<style scoped>
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}

.back-button-container {
  margin-bottom: 24px;
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
  padding: 8px 16px;
  border-radius: 8px;
  transition: background-color 0.2s ease;
}

.back-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
}

.back-button svg {
  width: 20px;
  height: 20px;
}

.post-detail-card {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.post-header {
  margin-bottom: 20px;
}

.post-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.author-info {
  flex: 1;
}

.author-name {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
}

.post-time {
  font-size: 12px;
  color: #6e6e73;
}

.post-meta .post-topic-inline {
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 8px;
  display: inline-block;
  box-shadow: 0 1px 4px rgba(58, 123, 213, 0.2);
}

.post-content {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.post-media {
  margin-bottom: 20px;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.media-item {
  border-radius: 8px;
  overflow: hidden;
  aspect-ratio: 1;
}

.post-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.post-footer {
  border-top: 1px solid #e5e5ea;
  padding-top: 16px;
}

.post-stats {
  display: flex;
  gap: 24px;
}

.stat-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  color: #6e6e73;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.2s ease;
}

.stat-button:hover {
  color: #FF3B30;
}

.stat-button.liked {
  color: #FF3B30;
  font-weight: 600;
}

.comments-section {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.comments-title {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
}

.comment-input-section {
  margin-bottom: 24px;
}

.comment-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 1px solid #e5e5ea;
  border-radius: 8px;
  resize: vertical;
  font-size: 14px;
  margin-bottom: 12px;
}

.submit-comment-button {
  background-color: #007AFF;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-comment-button:hover {
  background-color: #0066cc;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f7;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  font-size: 14px;
  font-weight: 600;
}

.time {
  font-size: 12px;
  color: #6e6e73;
}

.reply-to {
  font-size: 13px;
  color: #007AFF;
  margin-bottom: 8px;
  background-color: rgba(0, 122, 255, 0.05);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.comment-content {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  gap: 20px;
  margin-bottom: 12px;
}

.comment-actions button {
  background: none;
  border: none;
  color: #6e6e73;
  cursor: pointer;
  font-size: 12px;
  transition: color 0.2s ease;
}

.comment-actions button:hover {
  color: #007AFF;
}

.reply-box {
  margin-top: 12px;
  margin-left: 48px;
  padding: 12px;
  background-color: #f5f5f7;
  border-radius: 8px;
}

.reply-info {
  font-size: 13px;
  color: #007AFF;
  margin-bottom: 8px;
  background-color: rgba(0, 122, 255, 0.05);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
}

.reply-box textarea {
  width: 100%;
  min-height: 80px;
  padding: 8px;
  border:1px solid #e5e5ea;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  margin-bottom: 8px;
}

.reply-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.cancel-reply-button {
  background-color: #f5f5f7;
  color: #6e6e73;
  border: 1px solid #e5e5ea;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-reply-button:hover {
  background-color: #e5e5ea;
  color: #333;
}

.submit-reply-button {
  background-color: #007AFF;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 6px 12px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.submit-reply-button:hover {
  background-color: #0066cc;
}

.child-comment-list {
  margin-top: 16px;
  margin-left: 48px;
}

@media (max-width: 768px) {
  .post-detail-container {
    padding: 16px;
  }
  
  .post-detail-card,
  .comments-section {
    padding: 16px;
  }
  
  .media-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>