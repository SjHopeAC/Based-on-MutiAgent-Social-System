<template>
  <div class="category-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="categoryList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" width="200" />
        <el-table-column prop="topicCount" label="话题数" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.topicCount > 0 ? '#409EFF' : '#909399' }">{{ row.topicCount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="isHot" label="热门" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isHot ? 'danger' : 'info'" size="small">
              {{ row.isHot ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              :type="row.isHot ? 'info' : 'danger'"
              size="small"
              @click="handleToggleHot(row)"
            >
              {{ row.isHot ? '取消热门' : '设为热门' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="formDialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="50%">
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="formData.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入分类描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../utils/request'

const categoryList = ref([])
const loading = ref(false)
const formDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const formData = reactive({
  id: null,
  categoryName: '',
  description: '',
  sortOrder: 0
})

const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  sortOrder: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

const loadCategoryList = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/categories')
    if (res.code === 200) {
      categoryList.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类列表失败:', error)
    ElMessage.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    id: null,
    categoryName: '',
    description: '',
    sortOrder: 0
  })
  formDialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await request.put(`/admin/categories/${formData.id}`, formData)
        } else {
          res = await request.post('/admin/categories', formData)
        }
        
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
          formDialogVisible.value = false
          loadCategoryList()
        } else {
          ElMessage.error(res.msg || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleToggleHot = async (row) => {
  const action = row.isHot ? '取消热门' : '设为热门'
  const newIsHot = !row.isHot
  try {
    const res = await request.put(`/admin/categories/${row.id}/hot`, {
      isHot: newIsHot
    })
    if (res.code === 200) {
      ElMessage.success(`${action}成功`)
      loadCategoryList()
    } else {
      ElMessage.error(res.msg || `${action}失败`)
    }
  } catch (error) {
    console.error(`${action}失败:`, error)
    ElMessage.error(`${action}失败`)
  }
}

onMounted(() => {
  loadCategoryList()
})
</script>

<style scoped>
.category-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
