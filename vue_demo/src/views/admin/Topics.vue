<template>
  <div class="topic-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>话题管理</span>
          <div class="header-actions">
            <el-select v-model="statusFilter" placeholder="状态筛选" style="width: 120px; margin-right: 10px" @change="handleFilterChange">
              <el-option label="全部" value="" />
              <el-option label="正常" value="1" />
              <el-option label="审核中" value="2" />
              <el-option label="已下架" value="3" />
            </el-select>
            <el-select v-model="hotFilter" placeholder="热门筛选" style="width: 120px; margin-right: 10px" @change="handleFilterChange">
              <el-option label="全部" value="" />
              <el-option label="热门" value="1" />
              <el-option label="普通" value="0" />
            </el-select>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索话题标题"
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

      <el-table :data="topicList" style="width: 100%" v-loading="loading">
        <el-table-column prop="topicId" label="ID" width="80" />
        <el-table-column prop="topicTitle" label="话题标题" width="200" show-overflow-tooltip />
        <el-table-column prop="topicDesc" label="话题描述" width="250" show-overflow-tooltip />
        <el-table-column prop="creatorId" label="创建者ID" width="100" />
        <el-table-column prop="categoryId" label="分类ID" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="90" />
        <el-table-column prop="likeCount" label="点赞数" width="90" />
        <el-table-column prop="participantCount" label="参与人数" width="100" />
        <el-table-column prop="shareCount" label="分享数" width="90" />
        <el-table-column prop="isHot" label="热门" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isHot === 1 ? 'danger' : 'info'" size="small">
              {{ row.isHot === 1 ? '热门' : '普通' }}
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
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
              v-if="row.status === 1"
              type="warning"
              size="small"
              @click="handleSetHot(row)"
            >
              {{ row.isHot === 1 ? '取消热门' : '设为热门' }}
            </el-button>
            <el-button
              v-if="row.status === 1 || row.status === 2"
              type="danger"
              size="small"
              @click="handleDelete(row)"
            >
              下架
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

    <el-dialog v-model="detailDialogVisible" title="话题详情" width="60%">
      <div v-if="currentTopic">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="话题ID">{{ currentTopic.topicId }}</el-descriptions-item>
          <el-descriptions-item label="创建者ID">{{ currentTopic.creatorId }}</el-descriptions-item>
          <el-descriptions-item label="分类ID">{{ currentTopic.categoryId }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentTopic.status)">
              {{ getStatusText(currentTopic.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="热门">
            <el-tag :type="currentTopic.isHot === 1 ? 'danger' : 'info'">
              {{ currentTopic.isHot === 1 ? '热门' : '普通' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ currentTopic.viewCount }}</el-descriptions-item>
          <el-descriptions-item label="点赞数">{{ currentTopic.likeCount }}</el-descriptions-item>
          <el-descriptions-item label="参与人数">{{ currentTopic.participantCount }}</el-descriptions-item>
          <el-descriptions-item label="分享数">{{ currentTopic.shareCount }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ currentTopic.createTime }}</el-descriptions-item>
          <el-descriptions-item label="话题标题" :span="2">{{ currentTopic.topicTitle }}</el-descriptions-item>
          <el-descriptions-item label="话题描述" :span="2">{{ currentTopic.topicDesc }}</el-descriptions-item>
          <el-descriptions-item label="标签" :span="2">{{ currentTopic.tags || '无' }}</el-descriptions-item>
        </el-descriptions>
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

const topicList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const hotFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailDialogVisible = ref(false)
const currentTopic = ref(null)

const getStatusType = (status) => {
  const types = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    1: '正常',
    2: '审核中',
    3: '已下架'
  }
  return texts[status] || '未知'
}

const loadTopicList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/topics', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        keyword: searchKeyword.value,
        status: statusFilter.value,
        isHot: hotFilter.value
      }
    })
    if (res.code === 200) {
      topicList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('加载话题列表失败:', error)
    ElMessage.error('加载话题列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadTopicList()
}

const handleFilterChange = () => {
  currentPage.value = 1
  loadTopicList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadTopicList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadTopicList()
}

const handleView = (row) => {
  currentTopic.value = row
  detailDialogVisible.value = true
}

const handleApprove = (row) => {
  ElMessageBox.confirm('确定要通过该话题吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/topics/${row.topicId}/approve`)
      if (res.code === 200) {
        ElMessage.success('审核通过成功')
        loadTopicList()
      } else {
        ElMessage.error(res.msg || '审核通过失败')
      }
    } catch (error) {
      console.error('审核通过失败:', error)
      ElMessage.error('审核通过失败')
    }
  }).catch(() => {})
}

const handleSetHot = (row) => {
  const action = row.isHot === 1 ? '取消热门' : '设为热门'
  ElMessageBox.confirm(`确定要${action}该话题吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/topics/${row.topicId}/hot`, {
        isHot: row.isHot === 1 ? 0 : 1
      })
      if (res.code === 200) {
        ElMessage.success(`${action}成功`)
        loadTopicList()
      } else {
        ElMessage.error(res.msg || `${action}失败`)
      }
    } catch (error) {
      console.error(`${action}失败:`, error)
      ElMessage.error(`${action}失败`)
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要下架该话题吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.delete(`/admin/topics/${row.topicId}`)
      if (res.code === 200) {
        ElMessage.success('下架成功')
        loadTopicList()
      } else {
        ElMessage.error(res.msg || '下架失败')
      }
    } catch (error) {
      console.error('下架失败:', error)
      ElMessage.error('下架失败')
    }
  }).catch(() => {})
}

const handleRestore = (row) => {
  ElMessageBox.confirm('确定要恢复该话题吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.put(`/admin/topics/${row.topicId}/restore`)
      if (res.code === 200) {
        ElMessage.success('恢复成功')
        loadTopicList()
      } else {
        ElMessage.error(res.msg || '恢复失败')
      }
    } catch (error) {
      console.error('恢复失败:', error)
      ElMessage.error('恢复失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadTopicList()
})
</script>

<style scoped>
.topic-management {
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
</style>
