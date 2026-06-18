<template>
  <div class="comment-item">
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
      <button @click="$emit('like', comment.commentId)">点赞 {{ comment.likeCount }}</button>
      <button @click="showReplyBox">回复</button>
      <button @click="reportComment" class="report-button">举报</button>
    </div>
    <!-- 回复框（点击回复显示） -->
    <div v-if="isReplyBoxVisible" class="reply-box">
      <div class="reply-info">
        回复 @{{ comment.creator?.nickName || comment.creator?.username || '未知用户' }}
      </div>
      <textarea v-model="replyContent" :placeholder="`回复 ${comment.creator?.nickName || comment.creator?.username || '未知用户'}...`"></textarea>
      <div class="reply-actions">
        <button @click="cancelReply" class="cancel-reply-button">取消</button>
        <button @click="submitReply" class="submit-reply-button">提交回复</button>
      </div>
    </div>
    <!-- 子评论列表（递归渲染） -->
    <div class="child-comment-list" v-if="comment.children && comment.children.length">
      <CommentItem 
        v-for="child in comment.children" 
        :key="child.commentId" 
        :comment="child"
        :post-id="props.postId"
        @like="$emit('like', $event)"
        @reply="$emit('reply', $event)"
        @submit-reply="$emit('submit-reply', $event)"
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  postId: {
    type: Number,
    required: false
  }
})

const emit = defineEmits(['like', 'reply', 'submit-reply'])
const store = useStore()

const isReplyBoxVisible = ref(false)
const replyContent = ref('')

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

// 显示回复框
const showReplyBox = () => {
  isReplyBoxVisible.value = true
}

// 取消回复
const cancelReply = () => {
  isReplyBoxVisible.value = false
  replyContent.value = ''
}

// 提交回复
const submitReply = async () => {
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
    
    const detectRes = await fetch(`http://127.0.0.1:8000/predict?text=${encodeURIComponent(replyContent.value.trim())}&user_id=user_${userInfo.id}&post_id=${props.postId}`)
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
      postId: props.postId,
      parentId: props.comment.commentId,
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
    isReplyBoxVisible.value = false
    emit('submit-reply')
    
    if (commentType === -2) {
      ElMessage.warning('您的评论经过判定认为是垃圾评论，已被封禁')
    } else {
      ElMessage.success('您的评论已被成功发布')
    }
  } catch (error) {
    console.error('回复失败:', error)
    ElMessage.error('回复失败')
  }
}

// 举报评论
const reportComment = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo) {
      ElMessage.warning('请先登录')
      return
    }
    
    // 检查是否是自己的评论
    if (props.comment.creatorId === userInfo.id) {
      ElMessage.warning('不能举报自己的评论')
      return
    }
    
    // 检查是否已经举报过
    if (props.comment.appealed) {
      ElMessage.warning('该评论已被举报')
      return
    }
    
    await ElMessageBox.confirm('确定要举报这条评论吗？举报后将提交给管理员审核。', '举报确认', {
      confirmButtonText: '确定举报',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.post(`/comment/${props.comment.commentId}/report`)
    if (res.code === 200) {
      ElMessage.success('举报成功，请等待管理员审核')
      props.comment.appealed = true
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
</script>

<style scoped>
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
  .child-comment-list {
    margin-left: 32px;
  }
  
  .reply-box {
    margin-left: 32px;
  }
}
</style>