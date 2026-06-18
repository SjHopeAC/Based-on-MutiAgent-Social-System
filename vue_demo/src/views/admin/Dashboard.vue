<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card user-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card post-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.postCount }}</div>
              <div class="stat-label">帖子总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card topic-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.topicCount }}</div>
              <div class="stat-label">话题总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card comment-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><ChatLineSquare /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.commentCount }}</div>
              <div class="stat-label">评论总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card system-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><Monitor /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">
                <el-tag :type="spamSystemStatus.type" effect="dark" size="small">
                  {{ spamSystemStatus.text }}
                </el-tag>
              </div>
              <div class="stat-label">垃圾评论判别系统</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card comment-card">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon :size="40"><ChatLineSquare /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingComments.length }}</div>
              <div class="stat-label">待更改评论</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="recent-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新用户</span>
            </div>
          </template>
          <el-table :data="recentUsers" style="width: 100%">
            <el-table-column prop="username" label="用户名" width="120" />
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="createTime" label="注册时间" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>热门话题</span>
            </div>
          </template>
          <el-table :data="hotTopics" style="width: 100%">
            <el-table-column prop="topicTitle" label="话题标题" />
            <el-table-column prop="viewCount" label="浏览量" width="100" />
            <el-table-column prop="likeCount" label="点赞数" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑评论对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑评论" width="50%">
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="评论类型" prop="commentType">
          <el-select v-model="editForm.commentType" placeholder="请选择评论类型">
            <el-option label="正向评论" value="1" />
            <el-option label="待评评论" value="0" />
            <el-option label="负向评论" value="-1" />
            <el-option label="垃圾评论" value="-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="正常" value="1" />
            <el-option label="封禁" value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitEdit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评论详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="评论详情" width="60%">
      <div v-if="currentComment">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="评论ID">{{ currentComment.id }}</el-descriptions-item>
          <el-descriptions-item label="用户ID">{{ currentComment.userId }}</el-descriptions-item>
          <el-descriptions-item label="目标ID">
            帖子 #{{ currentComment.targetId }}
          </el-descriptions-item>
          <el-descriptions-item label="评论类型">
            <el-tag :type="getCommentTypeColor(currentComment.commentType)">
              {{ getCommentTypeText(currentComment.commentType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ currentComment.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="回复数">{{ currentComment.replyCount }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentComment.status === 1 ? 'success' : 'danger'">
              {{ currentComment.status === 1 ? '正常' : '封禁' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评论时间" :span="2">{{ currentComment.createTime }}</el-descriptions-item>
          <el-descriptions-item label="评论内容" :span="2">
            <div class="comment-content">{{ currentComment.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
        
        <el-divider>回复列表</el-divider>
        <div v-if="replyList.length > 0" class="reply-list">
          <div v-for="reply in replyList" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <div class="reply-user">
                <el-avatar :src="getAvatarUrl(reply.avatar)" :size="28" />
                <span>{{ reply.nickname || reply.username || `用户#${reply.userId}` }}</span>
              </div>
              <span class="reply-time">{{ reply.createTime }}</span>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
          </div>
        </div>
        <el-empty v-else description="暂无回复" />
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, ChatDotRound, ChatLineSquare, Monitor } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const stats = ref({
  userCount: 0,
  postCount: 0,
  topicCount: 0,
  commentCount: 0
})

const recentUsers = ref([])
const hotTopics = ref([])
const pendingComments = ref([])
const spamSystemStatus = ref({
  type: 'info',
  text: '检查中'
})

// 编辑评论相关
const editDialogVisible = ref(false)
const editForm = ref({})
const editFormRef = ref(null)
const editRules = ref({
  commentType: [{ required: true, message: '请选择评论类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 评论详情相关
const detailDialogVisible = ref(false)
const currentComment = ref(null)
const replyList = ref([])

const loadStats = async () => {
  try {
    const res = await request.get('/admin/stats')
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadRecentUsers = async () => {
  try {
    const res = await request.get('/admin/recent-users')
    if (res.code === 200) {
      recentUsers.value = res.data || []
    }
  } catch (error) {
    console.error('加载最新用户失败:', error)
  }
}

const loadHotTopics = async () => {
  try {
    const res = await request.get('/admin/hot-topics')
    if (res.code === 200) {
      hotTopics.value = res.data || []
    }
  } catch (error) {
    console.error('加载热门话题失败:', error)
  }
}

const checkSpamSystemStatus = async () => {
  try {
    // 直接使用 fetch 来请求外部 URL
    const response = await fetch('http://127.0.0.1:8000/health')
    if (response.ok) {
      const data = await response.json()
      if (data.status === 'ok') {
        spamSystemStatus.value = {
          type: 'success',
          text: '正常'
        }
      } else {
        spamSystemStatus.value = {
          type: 'warning',
          text: '异常'
        }
      }
    } else {
      spamSystemStatus.value = {
        type: 'danger',
        text: '连接失败'
      }
    }
  } catch (error) {
    console.error('检查垃圾评论判别系统状态失败:', error)
    spamSystemStatus.value = {
      type: 'danger',
      text: '连接失败'
    }
  }
}

const loadPendingComments = async () => {
  try {
    const res = await request.get('/admin/comments', {
      params: {
        commentType: 0,
        page: 1,
        size: 10
      }
    })
    if (res.code === 200) {
      pendingComments.value = res.data.list || []
    }
  } catch (error) {
    console.error('加载待更改评论失败:', error)
  }
}

const getAvatarUrl = (avatar) => {
  if (!avatar) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1/png.png'
  if (avatar.startsWith('http')) return avatar
  return `http://localhost:8088//api${avatar}`
}

const getCommentTypeColor = (type) => {
  const colors = {
    '1': 'success',
    '0': 'info',
    '-1': 'warning',
    '-2': 'danger'
  }
  return colors[type] || 'info'
}

const getCommentTypeText = (type) => {
  const texts = {
    '1': '正向评论',
    '0': '待评评论',
    '-1': '负向评论',
    '-2': '垃圾评论'
  }
  return texts[type] || '未知类型'
}

const loadReplyList = async (commentId) => {
  try {
    const res = await request.get(`/admin/comments/${commentId}/replies`)
    if (res.code === 200) {
      replyList.value = res.data || []
    }
  } catch (error) {
    console.error('加载回复列表失败:', error)
    replyList.value = []
  }
}

const handleViewComment = (row) => {
  currentComment.value = row
  loadReplyList(row.id)
  detailDialogVisible.value = true
}

const handleEditComment = (comment) => {
  // 在当前页面打开编辑对话框
  editForm.value = {
    id: comment.id,
    commentType: comment.commentType,
    status: comment.status
  }
  editDialogVisible.value = true
}

const handleSubmitEdit = async () => {
  if (editFormRef.value) {
    editFormRef.value.validate(async (valid) => {
      if (valid) {
        try {
          // 准备提交数据，确保类型正确
          const submitData = {
            commentType: parseInt(editForm.value.commentType),
            status: parseInt(editForm.value.status)
          }
          
          // 如果评论类型更改为垃圾评论，自动将状态改为封禁
          if (submitData.commentType === -2) {
            submitData.status = 0
          }
          
          const res = await request.put(`/admin/comments/${editForm.value.id}`, submitData)
          if (res.code === 200) {
            ElMessage.success('编辑成功')
            editDialogVisible.value = false
            // 重新加载待更改评论列表
            loadPendingComments()
          } else {
            ElMessage.error(res.msg || '编辑失败')
          }
        } catch (error) {
          console.error('编辑评论失败:', error)
          ElMessage.error('编辑评论失败')
        }
      }
    })
  }
}

onMounted(() => {
  loadStats()
  loadRecentUsers()
  loadHotTopics()
  loadPendingComments()
  checkSpamSystemStatus()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-card .stat-icon {
  background-color: #e6f7ff;
  color: #1890ff;
}

.post-card .stat-icon {
  background-color: #f6ffed;
  color: #52c41a;
}

.topic-card .stat-icon {
  background-color: #fff7e6;
  color: #fa8c16;
}

.comment-card .stat-icon {
  background-color: #fff1f0;
  color: #f5222d;
}

.system-card .stat-icon {
  background-color: #f0f5ff;
  color: #1890ff;
}

.pending-card .stat-icon {
  background-color: #fff7e6;
  color: #fa8c16;
}

.pending-card {
  background-color: #fff7e6;
  border: 1px solid #ffd591;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.charts-row {
  margin-bottom: 20px;
}

.recent-row {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pending-comments-row {
  margin-bottom: 20px;
}

.empty-placeholder {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
}

.comment-content {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.reply-list {
  max-height: 400px;
  overflow-y: auto;
}

.reply-item {
  padding: 15px;
  border-bottom: 1px solid #e8e8e8;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reply-user {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #1890ff;
}

.reply-time {
  font-size: 12px;
  color: #999;
}

.reply-content {
  color: #333;
  line-height: 1.6;
}
</style>
