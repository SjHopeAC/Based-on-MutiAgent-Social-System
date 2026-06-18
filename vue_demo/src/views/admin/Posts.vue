<template>
  <div class="post-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>帖子管理</span>
          <div class="header-actions">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px" @change="handleFilterChange">
              <el-option label="全部" value="" />
              <el-option label="正常" value="1" />
              <el-option label="审核中" value="2" />
              <el-option label="已删除" value="3" />
              <el-option label="违规" value="4" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索帖子内容"
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

      <el-table :data="postList" style="width: 100%" v-loading="loading">
        <el-table-column prop="postId" label="ID" width="80" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.postImgUrl"
              :src="getPostImgUrl(row.postImgUrl)"
              :preview-src-list="[getPostImgUrl(row.postImgUrl)]"
              fit="cover"
              style="width: 60px; height: 60px; border-radius: 4px"
            />
            <div v-else class="no-image">无图片</div>
          </template>
        </el-table-column>
        <el-table-column prop="postContent" label="内容" width="250" show-overflow-tooltip />
        <el-table-column prop="creatorId" label="发布者ID" width="100" />
        <el-table-column prop="topicId" label="话题ID" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="90" />
        <el-table-column prop="likeCount" label="点赞数" width="90" />
        <el-table-column prop="commentCount" label="评论数" width="90" />
        <el-table-column prop="shareCount" label="分享数" width="90" />
        <el-table-column prop="isHot" label="热门" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isHot === 1 ? 'danger' : 'info'" size="small">
              {{ row.isHot === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button
              v-if="row.status === 2"
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button
              :type="row.isHot === 1 ? 'info' : 'danger'"
              size="small"
              @click="handleToggleHot(row)"
            >
              {{ row.isHot === 1 ? '取消热门' : '设为热门' }}
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
            <el-button
              v-if="row.status === 3"
              type="success"
              size="small"
              @click="handleRestore(row)"
            >
              恢复
            </el-button>
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

    <el-dialog v-model="detailDialogVisible" title="帖子详情" width="60%">
      <div v-if="currentPost">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="帖子ID">{{ currentPost.postId }}</el-descriptions-item>
          <el-descriptions-item label="发布者ID">{{ currentPost.creatorId }}</el-descriptions-item>
          <el-descriptions-item label="话题ID">{{ currentPost.topicId }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentPost.status)">
              {{ getStatusText(currentPost.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ currentPost.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ currentPost.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="评论数">{{ currentPost.commentCount }}</el-descriptions-item>
          <el-descriptions-item label="分享数">{{ currentPost.shareCount }}</el-descriptions-item>
          <el-descriptions-item label="发布时间" :span="2">{{ currentPost.createTime }}</el-descriptions-item>
          <el-descriptions-item label="内容" :span="2">
            {{ currentPost.postContent }}
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="currentPost.postImgUrl" style="margin-top: 20px">
          <el-divider>图片</el-divider>
          <el-image
            :src="getPostImgUrl(currentPost.postImgUrl)"
            :preview-src-list="[getPostImgUrl(currentPost.postImgUrl)]"
            fit="contain"
            style="max-width: 100%; max-height: 400px"
          />
        </div>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '../../utils/request'

const postList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentPost = ref(null)

const getPostImgUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `http://localhost:8088${url}`
}

const getStatusType = (status) => {
  const types = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    1: '正常',
    2: '审核中',
    3: '已删除',
    4: '违规'
  }
  return texts[status] || '未知'
}

const loadPostList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/posts', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: statusFilter.value
      }
    })
    if (res.code === 200) {
      postList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载帖子列表失败:', error)
    ElMessage.error('加载帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadPostList()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadPostList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadPostList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadPostList()
}

const handleView = (row) => {
  currentPost.value = row
  detailDialogVisible.value = true
}

const handleApprove = (row) => {
  ElMessageBox.confirm('确定要通过该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/posts/${row.postId}/approve`)
      if (res.code === 200) {
        ElMessage.success('审核通过成功')
        loadPostList()
      } else {
        ElMessage.error(res.msg || '审核通过失败')
      }
    } catch (error) {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败')
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/admin/posts/${row.postId}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadPostList()
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
  ElMessageBox.confirm('确定要恢复该帖子吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/posts/${row.postId}/restore`)
      if (res.code === 200) {
        ElMessage.success('恢复成功')
        loadPostList()
      } else {
        ElMessage.error(res.msg || '恢复失败')
      }
    } catch (error) {
      console.error('恢复失败:', error)
      ElMessage.error('恢复失败')
    }
  }).catch(() => {})
}

const handleToggleHot = (row) => {
  const action = row.isHot === 1 ? '取消热门' : '设为热门'
  ElMessageBox.confirm(`确定要${action}该帖子吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 转换isHot值：前端Integer → 后端Integer
      const isHotValue = row.isHot === 1 ? 0 : 1
      const res = await request.put(`/admin/posts/${row.postId}/hot`, {
        isHot: isHotValue
      })
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        loadPostList()
      } else {
        ElMessage.error(res.msg || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadPostList()
})
</script>

<style scoped>
.post-management {
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

.no-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #999;
}
</style>
