<template>
  <div class="create-post-container">
    <div class="create-post-header">
      <div style="display: flex; align-items: center; justify-content: center; position: relative;">
        <button class="back-button" @click="goBack" style="position: absolute; left: 0;">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 11H7.83L13.42 5.41L12 4L4 12L12 20L13.41 18.59L7.83 13H20V11Z" fill="currentColor"/>
          </svg>
          返回
        </button>
        <h1 style="margin: 0;">发布新帖子</h1>
      </div>
      <p style="text-align: center;">分享你的想法和知识</p>
    </div>
    
    <div class="create-post-main">
      <el-form :model="postForm" label-width="80px">
        <!-- 话题选择 -->
        <el-form-item label="话题" required>
          <el-select v-model="postForm.topicId" placeholder="请选择话题" @change="handleTopicChange">
            <el-option 
              v-for="topic in topics" 
              :key="topic.topicId"
              :label="topic.topicTitle" 
              :value="topic.topicId"
            />
          </el-select>
        </el-form-item>
        
        <!-- 帖子内容 -->
        <el-form-item label="内容" required>
          <div class="editor-container">
            <div class="editor-toolbar">
              <button type="button" @click="formatText('bold')" title="加粗">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M6 12H14C15.66 12 17 10.66 17 9C17 7.34 15.66 6 14 6H6V12ZM6 12H15C16.66 12 18 13.34 18 15C18 16.66 16.66 18 15 18H6V12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              <button type="button" @click="formatText('italic')" title="斜体">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 4H18M7 20H15M13 4L11 20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
            <textarea 
              v-model="postForm.postContent" 
              class="editor-textarea"
              placeholder="开始写作..."
              rows="15"
            ></textarea>
            <div class="editor-stats">
              <span>{{ contentLength }}/5000 字</span>
            </div>
          </div>
        </el-form-item>
        
        <!-- 图片上传 -->
        <el-form-item label="图片">
          <el-upload
            v-model:file-list="imageFiles"
            action="#"
            list-type="picture-card"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :before-upload="beforeImageUpload"
            :limit="1"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <el-button type="primary" @click="uploadImage" :disabled="!selectedFile">上传图片</el-button>
        </el-form-item>
        
        <!-- 标签输入 -->
        <el-form-item label="标签">
          <div class="tags-input">
            <el-tag
              v-for="tag in postForm.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="tagInputVisible"
              ref="tagInputRef"
              v-model="tagInputValue"
              size="small"
              @keyup.enter="handleTagInputConfirm"
              @blur="handleTagInputConfirm"
            />
            <el-button v-else size="small" @click="showTagInput">
              + 添加标签
            </el-button>
          </div>
          <div class="popular-tags">
            <span class="tags-label">热门标签：</span>
            <el-tag
              v-for="tag in popularTags"
              :key="tag"
              size="small"
              type="info"
              class="tag-item"
              @click="addPopularTag(tag)"
            >
              {{ tag }}
            </el-tag>
          </div>
        </el-form-item>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <div class="action-buttons">
            <el-button @click="cancel">取消</el-button>
            <el-button type="primary" @click="submitPost">发布帖子</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 图片预览对话框 -->
    <el-dialog v-model="imagePreviewVisible">
      <img :src="previewImageUrl" alt="预览" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const store = useStore()

const postForm = reactive({
  topicId: Number(route.query.topicId) || '',
  categoryId: '',
  postContent: '',
  postImgUrl: '',
  tags: [],
  postImgAttribute: ''
})

const topics = ref([])
const popularTags = ref(['前端', 'Vue', 'React', 'JavaScript', 'TypeScript', 'Node.js', 'Python', 'Java'])
const imageFiles = ref([])
const imagePreviewVisible = ref(false)
const previewImageUrl = ref('')
const tagInputVisible = ref(false)
const tagInputValue = ref('')
const tagInputRef = ref()

const contentLength = computed(() => {
  return postForm.postContent.length
})

const formatText = (type) => {
  const textarea = document.querySelector('.editor-textarea')
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = postForm.postContent.substring(start, end)
  
  let formattedText = selectedText
  switch(type) {
    case 'bold':
      formattedText = `**${selectedText}**`
      break
    case 'italic':
      formattedText = `*${selectedText}*`
      break
  }
  
  postForm.postContent = 
    postForm.postContent.substring(0, start) + 
    formattedText + 
    postForm.postContent.substring(end)
}

const removeTag = (tag) => {
  postForm.tags = postForm.tags.filter(t => t !== tag)
}

const showTagInput = () => {
  tagInputVisible.value = true
  nextTick(() => {
    tagInputRef.value?.focus()
  })
}

const handleTagInputConfirm = () => {
  if (tagInputValue.value) {
    if (!postForm.tags.includes(tagInputValue.value)) {
      postForm.tags.push(tagInputValue.value)
    }
    tagInputValue.value = ''
  }
  tagInputVisible.value = false
}

const addPopularTag = (tag) => {
  if (!postForm.tags.includes(tag)) {
    postForm.tags.push(tag)
  }
}

const handlePictureCardPreview = (file) => {
  previewImageUrl.value = file.url
  imagePreviewVisible.value = true
}

const handleRemove = (file, uploadFiles) => {
  // 使用Element Plus提供的uploadFiles参数，确保与组件内部状态一致
  imageFiles.value = uploadFiles.filter(f => f.uid !== file.uid)
  if (imageFiles.value.length === 0) {
    postForm.postImgUrl = ''
  }
}

// 保存选中的文件
const selectedFile = ref(null)

const beforeImageUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  
  // 根据用户VIP状态设置不同的上传限制
  const userInfo = store.state.userInfo
  const isVip = userInfo?.vipLevel > 0
  const maxSizeMB = isVip ? 50 : 5
  const isLtMaxSize = file.size / 1024 / 1024 < maxSizeMB

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLtMaxSize) {
    ElMessage.error(`图片大小不能超过 ${maxSizeMB}MB!`)
    return false
  }
  
  // 保存文件
  selectedFile.value = file
  
  // 预览图片
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    // 构建新的文件对象，确保与Element Plus的Upload组件兼容
    const newFile = {
      ...file,
      url: reader.result
    }
    
    // 清空之前的图片，只保留当前上传的图片
    imageFiles.value = []
    // 添加到文件列表
    imageFiles.value.push(newFile)
  }
  
  // 返回false，阻止Element Plus自动上传
  return false
}

// 上传图片
const uploadImage = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请选择要上传的图片')
    return
  }
  
  try {
    // 创建FormData对象
    const formData = new FormData()
    formData.append('postImg', selectedFile.value)
    
    // 获取当前登录用户信息
    const currentUser = store.state.userInfo
    let userId = ''
    if (currentUser && currentUser.id) {
      userId = currentUser.id
    } else {
      ElMessage.error('无法获取用户信息')
      return
    }
    
    // 使用时间戳生成唯一文件名
    const timestamp = Date.now()
    const originalFilename = selectedFile.value.name
    const suffix = originalFilename.lastIndexOf('.') > 0 
      ? originalFilename.substring(originalFilename.lastIndexOf('.')) 
      : '.png'
    
    // 使用postImgAttribute作为基础文件名，如果为空则使用userId
    let baseFilename = postForm.postImgAttribute.trim() || String(userId)
    baseFilename = baseFilename.replace(/[^a-zA-Z0-9_-]/g, '_')
    
    const uniqueFilename = `${baseFilename}_${timestamp}${suffix}`
    
    formData.append('userId', userId)
    formData.append('filename', uniqueFilename)
    
    // 上传图片到服务器
    const response = await axios.post('http://localhost:8088/api/upload/post', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    // 处理后端响应
    if (response.data && typeof response.data === 'string') {
      if (response.data.includes('成功')) {
        // 上传成功，构建图片URL
        const postImgUrl = `http://localhost:8088/api/upload/post/${uniqueFilename}`
        postForm.postImgUrl = postImgUrl
        ElMessage.success('图片上传成功')
      } else {
        ElMessage.error(response.data)
      }
    } else {
      ElMessage.error('图片上传失败，请稍后重试')
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    ElMessage.error('上传图片失败，请稍后重试')
  }
}

const cancel = () => {
  router.back()
}

// 返回上一页
const goBack = () => {
  router.back()
}

const submitPost = async () => {
  if (!postForm.topicId || !postForm.postContent) {
    ElMessage.error('请选择话题并填写内容')
    return
  }
  
  try {
    const userInfo = store.state.userInfo
    if (!userInfo) {
      ElMessage.error('请先登录')
      return
    }
    
    const requestData = {
      topicId: postForm.topicId,
      categoryId: postForm.categoryId,
      creatorId: userInfo.id,
      postContent: postForm.postContent,
      postImgUrl: postForm.postImgUrl,
      status: 1
    }
    
    const res = await request.post('/post/create', requestData)
    ElMessage.success('帖子发布成功')
    router.back()
  } catch (error) {
    console.error('发布帖子失败:', error)
    ElMessage.error('发布帖子失败')
  }
}

const handleTopicChange = (topicId) => {
  // 根据选择的话题ID查找对应的分类ID
  const selectedTopic = topics.value.find(topic => topic.topicId === topicId)
  if (selectedTopic && selectedTopic.categoryId) {
    postForm.categoryId = selectedTopic.categoryId
  }
}

const loadTopics = async () => {
  try {
    const res = await request('/topic')
    // 确保返回的数据包含categoryId字段
    topics.value = res || []
    
    // 如果有默认话题ID（从路由参数传入），自动设置分类
    if (postForm.topicId) {
      const defaultTopic = topics.value.find(topic => topic.topicId === postForm.topicId)
      if (defaultTopic && defaultTopic.categoryId) {
        postForm.categoryId = defaultTopic.categoryId
      }
    }
  } catch (error) {
    console.error('加载话题失败:', error)
  }
}

onMounted(() => {
  loadTopics()
})
</script>

<style scoped>
.create-post-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
  background-color: white;
  border-radius: 16px;
  min-height: calc(100vh - 200px);
}

.create-post-header {
  margin-bottom: 32px;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  color: #007AFF;
  font-size: 16px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.back-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
}

.back-button svg {
  width: 18px;
  height: 18px;
}

.create-post-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.create-post-header p {
  font-size: 16px;
  color: #6e6e73;
}

.editor-container {
  border: 1px solid #e5e5ea;
  border-radius: 12px;
  overflow: hidden;
  width: 100%;
  max-width: 800px;
}

.editor-toolbar {
  background-color: #f5f5f7;
  padding: 12px;
  border-bottom: 1px solid #e5e5ea;
  display: flex;
  gap: 12px;
}

.editor-toolbar button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  color: #6e6e73;
  display: flex;
  align-items: center;
  justify-content: center;
}

.editor-toolbar button:hover {
  background-color: #e5e5ea;
  color: #007AFF;
}

.editor-textarea {
  width: 100%;
  padding: 16px;
  border: none;
  font-family: inherit;
  font-size: 16px;
  line-height: 1.6;
  resize: vertical;
  outline: none;
  min-height: 300px;
}

.editor-stats {
  padding: 12px 16px;
  background-color: #f5f5f7;
  border-top: 1px solid #e5e5ea;
  text-align: right;
  font-size: 14px;
  color: #6e6e73;
}

.tags-input {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  margin-bottom: 12px;
}

.popular-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tags-label {
  font-size: 14px;
  color: #6e6e73;
}

.tag-item {
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag-item:hover {
  background-color: #007AFF;
  color: white;
  border-color: #007AFF;
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 32px;
}
</style>