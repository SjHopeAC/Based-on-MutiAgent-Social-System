<template>
  <div class="comment-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评论管理</span>
          <div class="header-actions">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px" @change="handleFilterChange">
              <el-option label="全部" value="" />
              <el-option label="正常" value="1" />
              <el-option label="封禁" value="0" />
            </el-select>
            <el-select v-model="commentTypeFilter" placeholder="类型筛选" style="width: 120px; margin-right: 10px" @change="handleFilterChange">
              <el-option label="全部" value="" />
              <el-option label="正向评论" value="1" />
              <el-option label="待评评论" value="0" />
              <el-option label="负向评论" value="-1" />
              <el-option label="垃圾评论" value="-2" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索评论内容"
              style="width: 300px"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <el-table :data="commentList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="commentType" label="评论类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getCommentTypeColor(row.commentType)" size="small">
              {{ getCommentTypeText(row.commentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="150">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :src="getAvatarUrl(row.avatar)" :size="32" />
              <span class="user-name">{{ row.nickname || row.username || `用户#${row.userId}` }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="targetId" label="目标ID" width="100">
          <template #default="{ row }">
            帖子 #{{ row.targetId }}
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" width="250" show-overflow-tooltip />
        <el-table-column prop="result" label="检测结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === '是' ? 'danger' : 'success'" size="small">
              {{ row.result || '未检测' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="confidence" label="置信度" width="100" />
        <el-table-column prop="source" label="检测来源" width="100" />
        <el-table-column prop="likeCount" label="点赞数" width="90" />
        <el-table-column prop="replyCount" label="回复数" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '封禁' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="评论时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

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
          <el-descriptions-item label="检测结果">
            <el-tag :type="currentComment.result === '是' ? 'danger' : 'success'">
              {{ currentComment.result || '未检测' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="置信度">{{ currentComment.confidence || '-' }}</el-descriptions-item>
          <el-descriptions-item label="检测来源">{{ currentComment.source || '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <div class="reasoning-section">
          <h4 class="reasoning-title">分析推理</h4>
          <div class="reasoning-content" v-if="currentComment.reasoning">{{ currentComment.reasoning }}</div>
          <span v-else class="no-reasoning">-</span>
        </div>
        
        <el-descriptions :column="2" border style="margin-top: 20px;">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import request from '../../utils/request'

const route = useRoute()

const commentList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const commentTypeFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const editDialogVisible = ref(false)
const currentComment = ref(null)
const replyList = ref([])
const editForm = ref({})
const editFormRef = ref(null)
const editRules = ref({
  commentType: [{ required: true, message: '请选择评论类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

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

const handleEdit = (row) => {
  editForm.value = {
    id: row.id,
    commentType: row.commentType,
    status: row.status
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
            loadCommentList()
          }
        } catch (error) {
          console.error('编辑评论失败:', error)
          ElMessage.error('编辑评论失败')
        }
      }
    })
  }
}

const getAvatarUrl = (avatar) => {
  if (!avatar) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1/png.png'
  if (avatar.startsWith('http')) return avatar
  return `http://localhost:8088//api${avatar}`
}

const loadCommentList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/comments', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: statusFilter.value,
        commentType: commentTypeFilter.value
      }
    })
    if (res.code === 200) {
      commentList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载评论列表失败:', error)
    ElMessage.error('加载评论列表失败')
  } finally {
    loading.value = false
  }
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

const handleSearch = () => {
  currentPage.value = 1
  loadCommentList()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadCommentList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadCommentList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadCommentList()
}

const handleView = (row) => {
  currentComment.value = row
  loadReplyList(row.id)
  detailDialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/admin/comments/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadCommentList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleRestore = (row) => {
  ElMessageBox.confirm('确定要恢复该评论吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/comments/${row.id}/restore`)
      if (res.code === 200) {
        ElMessage.success('恢复成功')
        loadCommentList()
      } else {
        ElMessage.error(res.msg || '恢复失败')
      }
    } catch (error) {
      console.error('恢复失败:', error)
      ElMessage.error('恢复失败')
    }
  }).catch(() => {})
}

onMounted(async () => {
  await loadCommentList()
  
  // 检查URL参数是否有editCommentId
  const editCommentId = route.query.editCommentId
  if (editCommentId) {
    // 查找对应的评论
    const comment = commentList.value.find(c => c.id === Number(editCommentId))
    if (comment) {
      handleEdit(comment)
    } else {
      // 如果评论不在当前列表中，刷新列表后再查找
      await loadCommentList()
      const updatedComment = commentList.value.find(c => c.id === Number(editCommentId))
      if (updatedComment) {
        handleEdit(updatedComment)
      }
    }
  }
})
</script>

<style scoped>
.comment-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.comment-content {
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  background-color: #f5f5f5;
  border-radius: 4px;
}

.reasoning-section {
  margin: 20px 0;
}

.reasoning-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.reasoning-content {
  height: 63px; /* 固定三行，每行21px */
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-word;
  padding: 10px;
  background-color: #f9fafc;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 14px;
  line-height: 1.5;
}

.no-reasoning {
  padding: 10px;
  color: #999;
  font-size: 14px;
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

.user-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  font-weight: 500;
  color: #333;
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
