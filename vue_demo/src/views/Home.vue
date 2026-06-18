<template>
  <div class="forum-container">
    <!-- 主内容区 -->
    <main class="forum-main">
      <div class="main-container">
        <!-- 左侧导航 -->
        <aside class="forum-sidebar">
          <nav class="sidebar-nav">
            <div class="nav-section">
              <h3 class="nav-title">导航</h3>
              <ul class="nav-list">
                <li 
                  v-for="item in navItems" 
                  :key="item.id"
                  :class="['nav-item', { active: activeNav === item.id }]"
                  @click="setActiveNav(item.id)"
                >
                  <a href="#" class="nav-link" @click.prevent>
                    <svg class="nav-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path :d="item.icon" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.name }}</span>
                  </a>
                </li>
              </ul>
            </div>

            <div class="nav-section">
              <h3 class="nav-title">热门分类</h3>
              <ul class="category-list">
                <li 
                  v-for="category in categories" 
                  :key="category.categoryId"
                  class="category-item"
                >
                  <a href="#" class="category-link" @click.prevent="selectCategory(category.categoryId)">
                    <div :class="['category-icon', category.isHot === 1 ? 'hot' : 'normal']"></div>
                    <div class="category-info">
                      <span class="category-name">{{ category.categoryName }}</span>
                      <span class="category-count">{{ category.topicCount }}</span>
                    </div>
                  </a>
                </li>
              </ul>
            </div>


          </nav>
        </aside>

        <!-- 主内容区 -->
        <div class="forum-content">
          <!-- 创建话题区域 -->
          <div class="create-topic-card" @click="openCreateModal">
            <div class="create-avatar">
              <img 
                :src="getAvatarUrl(store.state.userInfo?.avatar)" 
                alt="个人头像"
                class="avatar-image"
              />
            </div>
            <div class="create-prompt">
              <span class="prompt-text">分享你的想法或提出问题...</span>
            </div>
            <button class="create-action">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M19 11H13V5C13 4.44772 12.5523 4 12 4C11.4477 4 11 4.44772 11 5V11H5C4.44772 11 4 11.4477 4 12C4 12.5523 4.44772 13 5 13H11V19C11 19.5523 11.4477 20 12 20C12.5523 20 13 19.5523 13 19V13H19C19.5523 13 20 12.5523 20 12C20 11.4477 19.5523 11 19 11Z" fill="currentColor"/>
              </svg>
            </button>
          </div>

          <!-- 话题筛选 -->
          <div class="topic-filters">
            <div class="filter-tabs">
              <button 
                v-for="tab in filterTabs" 
                :key="tab.id"
                :class="['filter-tab', { active: activeFilter === tab.id }]"
                @click="setActiveFilter(tab.id)"
              >
                {{ tab.name }}
              </button>
            </div>
            <div class="filter-controls">
              <el-select 
                v-model="selectedTag" 
                placeholder="选择标签"
                clearable
                @change="handleTagChange"
              >
                <el-option
                  v-for="tag in popularTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
                />
              </el-select>
            </div>
          </div>

          <!-- 话题/帖子列表 -->
          <div class="topics-container">
            <div 
              v-for="item in (activeFilter === 'topic' ? topics.filter(t => t && t.topicId) : posts.filter(p => p && p.postId))" 
              :key="activeFilter === 'topic' ? item.topicId : item.postId"
              class="topic-card"
              @mouseenter="hoveredTopic = activeFilter === 'topic' ? item.topicId : item.postId"
              @mouseleave="hoveredTopic = null"
              @click="activeFilter === 'topic' ? navigateToTopic(item.topicId) : navigateToPost(item.postId)"
            >
              <div class="topic-header">
                <div class="topic-author">
                  <div class="author-avatar" @click.stop="navigateToUserProfile(activeFilter === 'topic' ? item.creatorId : item.creatorId)">
                    <img 
                      :src="getAvatarUrl(userCache[activeFilter === 'topic' ? item.creatorId : item.creatorId]?.avatar)" 
                      alt="作者头像"
                      class="avatar-image"
                    />
                  </div>
                  <div class="author-info">
                    <div class="author-name">{{ userCache[activeFilter === 'topic' ? item.creatorId : item.creatorId]?.nickname || userCache[activeFilter === 'topic' ? item.creatorId : item.creatorId]?.username || item.creatorName || '未知用户' }}</div>
                    <div class="author-meta">
                      <span class="topic-time">{{ formatDate(item.createTime) }}</span>
                      <span class="topic-category">{{ getCategoryName(item.categoryId) }}</span>
                      <!-- 帖子所属话题 -->
                      <span v-if="activeFilter === 'post' && item.topicId" class="post-topic-inline">
                        {{ getTopicName(item.topicId) }}
                      </span>
                    </div>
                  </div>
                  <button 
                    v-if="activeFilter === 'topic' ? userCache[item.creatorId] : userCache[item.creatorId]" 
                    :class="['follow-button', { 'following': userCache[activeFilter === 'topic' ? item.creatorId : item.creatorId]?.following }]"
                    @click.stop="toggleFollow(activeFilter === 'topic' ? item.creatorId : item.creatorId)"
                  >
                    {{ userCache[activeFilter === 'topic' ? item.creatorId : item.creatorId]?.following ? '已关注' : '关注' }}
                  </button>
                </div>
                <button class="topic-menu" @click.stop="showTopicMenu(item.topicId)">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="6" r="1" fill="currentColor"/>
                    <circle cx="12" cy="12" r="1" fill="currentColor"/>
                    <circle cx="12" cy="18" r="1" fill="currentColor"/>
                  </svg>
                </button>
              </div>
              
              <div class="topic-content">
                <!-- 话题显示 -->
                <template v-if="activeFilter === 'topic'">
                  <div class="topic-title-container">
                    <h3 class="topic-title">{{ item.topicTitle }}</h3>
                    <span v-if="isTrendingTopic(item.topicId)" class="hot-tag">热门</span>
                  </div>
                  <p class="topic-excerpt">{{ item.topicDesc }}</p>
                  
                  <div class="topic-tags">
                    <span 
                      v-for="tag in (item.tags ? item.tags.split(',') : [])" 
                      :key="tag"
                      class="topic-tag"
                    >
                      {{ tag }}
                    </span>
                  </div>
                  
                  <div class="topic-media" v-if="item.hasMedia">
                    <div class="media-preview image">
                      <div class="media-content"></div>
                    </div>
                  </div>
                </template>
                
                <!-- 帖子显示 -->
                <template v-else-if="activeFilter === 'post'">
                  <p class="post-content">{{ item.postContent }}</p>
                  
                  <!-- 帖子照片展示 -->
                  <div class="post-media" v-if="item.postImgUrl">
                    <div class="media-grid">
                      <div class="media-item">
                        <img 
                          :src="getImageUrl(item.postImgUrl)" 
                          :alt="'帖子照片'"
                          class="post-photo"
                        />
                      </div>
                    </div>
                  </div>
                </template>
              </div>
              
              <div class="topic-footer">
                <div class="topic-stats">
                  <button :class="['stat-button', { 'liked': item.isLiked }]" @click.stop="activeFilter === 'topic' ? handleLike(item.topicId) : handleLikePost(item.postId)" title="点赞">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.likeCount || 0 }}</span>
                  </button>
                  <button class="stat-button" @click.stop="activeFilter === 'topic' ? handleShare(item.topicId) : handleSharePost(item.postId)" title="转发">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H5C4.46957 17 3.96086 16.7893 3.58579 16.4142C3.21071 16.0391 3 15.5304 3 15V9C3 8.46957 3.21071 7.96086 3.58579 7.58579C3.96086 7.21071 4.46957 7 5 7H19C19.5304 7 20.0391 7.21071 20.4142 7.58579C20.7893 7.96086 21 8.46957 21 9V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M8 10L12 14L16 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.shareCount || 0 }}</span>
                  </button>

                  <div v-if="activeFilter === 'post'" class="stat-button" title="评论数">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.commentCount || 0 }}</span>
                  </div>

                  <div class="stat-button" title="浏览量">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M21 11.5C21 16.75 16.75 21 11.5 21C6.25 21 2 16.75 2 11.5C2 6.25 6.25 2 11.5 2C16.75 2 21 6.25 21 11.5Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M8 14V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M16 14V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M12 10V17" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    <span>{{ item.viewCount || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div v-if="(activeFilter === 'topic' ? topics.filter(t => t && t.topicId) : posts.filter(p => p && p.postId)).length === 0" class="empty-data">
              此选项暂无讨论
            </div>
          </div>

          <!-- 加载更多 -->
          <div class="load-more">
            <button class="load-more-button" @click="activeFilter === 'topic' ? loadMoreTopics() : loadMorePosts()">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 5V19M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
              </svg>
              {{ activeFilter === 'topic' ? '加载更多话题' : '加载更多帖子' }}
            </button>
          </div>
        </div>

        <!-- 右侧边栏 -->
        <aside class="forum-rightbar">
          <!-- 热门话题 -->
          <div class="rightbar-section">
            <div class="section-header">
              <h3 class="section-title">热门话题</h3>
              <button class="refresh-button" @click="refreshTrending">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M23 4V10H17M1 20V14H7" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M3.51 9.00001C4.01717 7.5668 4.87913 6.2854 6.01547 5.27542C7.1518 4.26543 8.52547 3.55978 10.0083 3.22426C11.4911 2.88874 13.0348 2.93436 14.4952 3.35677C15.9556 3.77918 17.2853 4.56471 18.36 5.64001L23 10M1 14L5.64 18.36C6.71475 19.4353 8.04437 20.2208 9.50481 20.6432C10.9652 21.0656 12.5089 21.1113 13.9917 20.7757C15.4745 20.4402 16.8482 19.7346 17.9845 18.7246C19.1209 17.7146 19.9828 16.4332 20.49 15" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
            <div class="trending-topics">
              <div 
                v-for="(trending, index) in trendingTopics" 
                :key="trending.topicId"
                class="trending-item"
                @click="navigateToTopic(trending.topicId)"
              >
                <div class="trending-rank" :class="`rank-${index + 1}`">
                  <span class="rank-number">{{ index + 1 }}</span>
                </div>
                <div class="trending-content">
                  <h4 class="trending-title">{{ trending.topicTitle }}</h4>
                  <p class="trending-desc">{{ truncateText(trending.topicDesc, 20) }}</p>
                  <div class="trending-meta">
                    <span class="trending-count">{{ trending.participantCount }}人讨论</span>
                    <span class="trending-time">{{ formatDate(trending.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 活跃用户 -->
          <div class="rightbar-section">
            <div class="section-header">
              <h3 class="section-title">活跃用户</h3>
            </div>
            <div class="active-users">
              <div 
                v-for="activeUser in activeUsers" 
                :key="activeUser.id"
                class="user-item"
              >
                <div class="user-avatar" :class="{ 'vip-avatar': activeUser.isVip }" @click.stop="navigateToUserProfile(activeUser.id)">
                  <img 
                    :src="getAvatarUrl(activeUser.avatar)" 
                    :alt="activeUser.nickname"
                    class="avatar-image"
                  />
                </div>
                <div class="user-info">
                  <div class="user-name">
                    {{ activeUser.nickname }}
                    <span v-if="activeUser.isVip" class="vip-badge">VIP</span>
                  </div>
                  <div class="user-activity">共发布{{ activeUser.PostCount }}篇帖子</div>
                </div>
                <button 
                  :class="['follow-button', { 'following': activeUser.following }]"
                  @click="toggleFollow(activeUser.id)"
                >
                  {{ activeUser.following ? '已关注' : '关注' }}
                </button>
              </div>
            </div>
          </div>

          <!-- 热门标签 -->
          <div class="rightbar-section">
            <div class="section-header">
              <h3 class="section-title">热门标签</h3>
            </div>
            <div class="popular-tags">
              <span 
                v-for="tag in popularTags" 
                :key="tag"
                class="tag"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </aside>
      </div>
    </main>

    <!-- 创建话题模态框 -->
    <div :class="['modal-overlay', { active: showCreateModal }]" @click="closeCreateModal">
      <div class="create-modal" @click.stop>
        <div class="modal-header">
          <h3 class="modal-title">创建新话题</h3>
          <button class="modal-close" @click="closeCreateModal">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
        <div class="modal-content">
          <div class="form-group">
      <label class="form-label">话题分类 <span class="required">*</span></label>
      <div class="category-selector">
        <div class="category-options">
          <button 
            v-for="category in categories" 
            :key="category.categoryId || category.id"
            type="button"
            :class="['category-option', { active: newTopic.categoryId && newTopic.categoryId === String(category.categoryId || category.id) }]"
            @click.stop="selectTopicCategory(category.categoryId || category.id)"
          >
            <div :class="['category-icon', category.isHot === 1 ? 'hot' : 'normal']"></div>
            <span>{{ category.categoryName }}</span>
          </button>
          </div>
            </div>
              </div>
          <div class="form-group">
            <label class="form-label">话题标题 <span class="required">*</span></label>
            <input 
              type="text" 
              class="form-input" 
              placeholder="请输入话题标题（2-50字）" 
              v-model="newTopic.topicTitle"
              maxlength="50"
            >
            <div class="input-hint">{{ newTopic.topicTitle.length }}/50</div>
          </div>
          <div class="form-group">
            <label class="form-label">话题内容 <span class="required">*</span></label>
            <textarea 
              class="form-textarea" 
              placeholder="详细描述你的话题，让更多人了解..." 
              v-model="newTopic.topicDesc"
              maxlength="500"
              rows="4"
            ></textarea>
            <div class="input-hint">{{ newTopic.topicDesc.length }}/500</div>
          </div>
          <div class="form-group">
            <label class="form-label">添加标签</label>
            <div class="tag-input">
              <div class="selected-tags">
                <span 
                  v-for="tag in newTopic.tags" 
                  :key="tag"
                  class="selected-tag"
                >
                  <span class="tag-text">{{ tag }}</span>
                  <button class="remove-tag" @click="removeTag(tag)">×</button>
                </span>
              </div>
              <input 
                type="text" 
                class="tag-input-field" 
                placeholder="输入标签后按回车，最多添加5个标签"
                v-model="tagInput"
                @keyup.enter="addTag"
                :disabled="newTopic.tags.length >= 5"
              >
            </div>
            <div class="input-hint">{{ newTopic.tags.length }}/5</div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="modal-button secondary" @click="closeCreateModal">取消</button>
          <button class="modal-button primary" @click="publishTopic">发布话题</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import request from '../utils/request'


// 路由实例
const router = useRouter()

// Vuex存储实例
const store = useStore()
const userInfo = computed(() => store.state.userInfo || null)

// 导航（不变）
const activeNav = ref('home')
const navItems = [
  { id: 'home', name: '首页', icon: 'M3 9L12 2L21 9V20C21 20.5304 20.7893 21.0391 20.4142 21.4142C20.0391 21.7893 19.5304 22 19 22H5C4.46957 22 3.96086 21.7893 3.58579 21.4142C3.21071 21.0391 3 20.5304 3 20V9Z' },
  { id: 'hot', name: '热门', icon: 'M17 20H22V4H17V20ZM2 20H7V4H2V20ZM9 20H15V4H9V20Z' },
  { id: 'new', name: '最新', icon: 'M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22ZM12 8V12L15 15' },
  { id: 'follow', name: '关注', icon: 'M22 12C22 10.6868 21.7413 9.38642 21.2388 8.17317C20.7362 6.95991 19.9997 5.85752 19.0711 4.92893C18.1425 4.00035 17.0401 3.26375 15.8268 2.7612C14.6136 2.25866 13.3132 2 12 2V12H22ZM22 12C22 13.3132 21.7413 14.6136 21.2388 15.8268C20.7362 17.0401 19.9997 18.1425 19.0711 19.0711C18.1425 19.9997 17.0401 20.7362 15.8268 21.2388C14.6136 21.7413 13.3132 22 12 22C10.6868 22 9.38642 21.7413 8.17317 21.2388C6.95991 20.7362 5.85752 19.9997 4.92893 19.0711C4.00035 18.1425 3.26375 17.0401 2.7612 15.8268C2.25866 14.6136 2 13.3132 2 12L12 12H22Z' }
]

// 分类（从后端获取）
const categories = ref([])
// 统计（从后端获取）
const stats = ref([])
// 筛选标签（话题和帖子）
const activeFilter = ref('topic')
const filterTabs = [
  { id: 'topic', name: '话题' },
  { id: 'post', name: '帖子' }
]

// 当前选中的热门标签
const selectedTag = ref('')

// 话题列表（从后端获取）
const hoveredTopic = ref(null)
const topics = ref([])
// 帖子列表（从后端获取）
const posts = ref([])
// 独立的话题列表，用于显示帖子所属话题（不受筛选影响）
const allTopics = ref([])

// 右侧边栏数据（从后端获取）
const trendingTopics = ref([])
const activeUsers = ref([])
const popularTags = ref([])

// 用户信息缓存
const userCache = ref({})

// 创建话题模态框
const showCreateModal = ref(false)
const tagInput = ref('')
const newTopic = reactive({
  categoryId: null, // 新增：关联分类ID
  topicTitle: '', // 对应数据库topic_title
  topicDesc: '',  // 对应数据库topic_desc
  tags: []        // 标签（后端可存储为字符串，用逗号分隔）
})

// 已从 utils/request.js 导入 request 对象

// 获取用户信息（带缓存）
const getUserInfo = async (userId) => {
  if (!userId) return null
  
  if (userCache.value[userId]) {
    return userCache.value[userId]
  }
  
  try {
    // 从Vuex中获取登录状态和用户信息
    const isLogin = store.state.isLogin
    const currentUserId = store.getters.userId
    
    // 传递当前登录用户ID给接口，以便获取关注状态
    const res = await request.get(`/user/${userId}?currentUserId=${currentUserId}`)
    const userInfoData = res.data
    userCache.value[userId] = userInfoData
    return userInfoData
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return null
  }
}

// 批量获取话题创建者信息
const loadTopicCreators = async () => {
  // 获取话题创建者ID
  const topicCreatorIds = topics.value && topics.value.length > 0 
    ? [...new Set(topics.value.map(t => t.creatorId).filter(id => id))] 
    : []
  
  // 获取帖子创建者ID
  const postCreatorIds = posts.value && posts.value.length > 0 
    ? [...new Set(posts.value.map(p => p.creatorId).filter(id => id))] 
    : []
  
  // 合并所有创建者ID，去重
  const creatorIds = [...new Set([...topicCreatorIds, ...postCreatorIds])]
  
  if (creatorIds.length === 0) {
    console.log('没有需要加载的用户信息')
    return
  }
  
  console.log('需要加载的用户ID:', creatorIds)
  
  // 尝试获取用户信息，但失败时不影响页面显示
  for (const userId of creatorIds) {
    try {
      const userInfo = await getUserInfo(userId)
    } catch (error) {
      console.error(`获取用户 ${userId} 信息失败:`, error)
    }
  }
  console.log('用户信息加载完成，缓存内容:', userCache.value)
}

// 重新加载用户缓存，包括关注状态
const loadUserCache = async () => {
  // 1. 从Vuex获取登录状态和用户ID
  const isLogin = store.state.isLogin
  const currentUserId = store.getters.userId
  
  // 2. 保存当前的关注状态，避免被覆盖
  const currentFollowingStatus = {}
  for (const userId in userCache.value) {
    if (userCache.value[userId].following) {
      currentFollowingStatus[userId] = true
    }
  }
  
  // 3. 清空用户缓存
  userCache.value = {}
  
  // 4. 先从活跃用户列表更新关注状态（优先级最高）
  if (activeUsers.value && activeUsers.value.length > 0) {
    for (const user of activeUsers.value) {
      userCache.value[user.id] = user
    }
    console.log('从活跃用户列表更新关注状态:', userCache.value)
  }
  
  // 5. 重新加载话题创建者信息
  await loadTopicCreators()
  
  // 6. 加载帖子创建者信息
  if (posts.value && posts.value.length > 0) {
    const creatorIds = [...new Set(posts.value.map(p => p.creatorId).filter(id => id))]
    for (const userId of creatorIds) {
      if (!userCache.value[userId]) {
        try {
          const userInfo = await getUserInfo(userId)
        } catch (error) {
          console.error(`获取用户 ${userId} 信息失败:`, error)
        }
      }
    }
  }
  
  // 7. 恢复之前的关注状态，确保关注状态不丢失
  for (const userId in currentFollowingStatus) {
    if (userCache.value[userId]) {
      userCache.value[userId].following = true
    }
  }
  
  console.log('用户缓存重新加载完成:', userCache.value)
}

// 判断话题是否为热门话题
const isTrendingTopic = (topicId) => {
  return trendingTopics.value.some(topic => topic.topicId === topicId)
}

// 初始化：加载所有后端数据
const initData = async () => {
  try {
    // 1. 从Vuex获取登录状态和用户ID
    const isLogin = store.state.isLogin
    const currentUserId = store.getters.userId
    
    // 2. 清空用户缓存，确保关注状态正确
    userCache.value = {}
    
    // 3. 获取分类列表
    const categoriesRes = await request.get('/topic/categories')
    categories.value = categoriesRes.data || []

    // 4. 获取统计数据
    try {
      const statsRes = await request.get('/admin/stats')
      stats.value = statsRes.data || [
        { label: '注册用户', value: '0' },
        { label: '在线用户', value: '0' },
        { label: '总话题数', value: '0' }
      ]
      console.log('获取统计数据成功:', stats.value)
    } catch (error) {
      console.error('获取统计数据失败:', error)
      stats.value = [
        { label: '注册用户', value: '0' },
        { label: '在线用户', value: '0' },
        { label: '总话题数', value: '0' }
      ]
    }

    // 4.5 获取所有话题（用于显示帖子所属话题）
    const allTopicsRes = await request.get('/topics')
    allTopics.value = allTopicsRes.data || []

    // 5. 获取话题/帖子列表（根据当前筛选类型和导航栏选择）
    await loadDataByNav(activeNav.value)

    // 6. 获取热门话题
    const trendingRes = await request.get('/topics/trending')
    trendingTopics.value = trendingRes.data || []

    // 7. 获取活跃用户（带关注状态）
    // 传递当前登录用户ID给接口，未登录时使用0
    const userId = currentUserId || 0
    const usersRes = await request.get(`/users/active?currentUserId=${userId}`)
    const users = usersRes.data || []
    
    activeUsers.value = users

    // 8. 加载用户缓存，包括关注状态
    await loadUserCache()

    // 9. 获取热门标签
    const tagsRes = await request.get('/tags/popular')
    popularTags.value = tagsRes.data || ['技术', '设计', '产品']
  } catch (error) {
    console.error('初始化数据失败:', error)
  }
}

// 🔴 方法定义（对接后端）
const setActiveNav = async (navId) => {
  activeNav.value = navId
  await loadDataByNav(navId)
}

const loadDataByNav = async (navId) => {
  try {
    const currentUserId = store.getters.userId
    let url = ''
    
    if (activeFilter.value === 'topic') {
      switch (navId) {
        case 'home':
          url = currentUserId && currentUserId !== '' 
            ? `/topics?currentUserId=${currentUserId}`
            : '/topics'
          break
        case 'hot':
          url = currentUserId && currentUserId !== '' 
            ? `/topics?isHot=1&currentUserId=${currentUserId}`
            : '/topics?isHot=1'
          break
        case 'new':
          url = currentUserId && currentUserId !== '' 
            ? `/topics?orderBy=createTime&currentUserId=${currentUserId}`
            : '/topics?orderBy=createTime'
          break
        case 'follow':
          if (!currentUserId || currentUserId === '') {
            ElMessage.warning('请先登录后查看关注内容')
            return
          }
          url = `/topics?followed=1&currentUserId=${currentUserId}`
          break
      }
      
      const topicsRes = await request.get(url)
      topics.value = topicsRes.data || []
      posts.value = []
    } else if (activeFilter.value === 'post') {
      switch (navId) {
        case 'home':
          url = currentUserId && currentUserId !== '' 
            ? `/post/list?currentUserId=${currentUserId}`
            : '/post/list'
          break
        case 'hot':
          url = currentUserId && currentUserId !== '' 
            ? `/post/list?isHot=1&currentUserId=${currentUserId}`
            : '/post/list?isHot=1'
          break
        case 'new':
          url = currentUserId && currentUserId !== '' 
            ? `/post/list?orderBy=createTime&currentUserId=${currentUserId}`
            : '/post/list?orderBy=createTime'
          break
        case 'follow':
          if (!currentUserId || currentUserId === '') {
            ElMessage.warning('请先登录后查看关注内容')
            return
          }
          url = `/post/list?followed=1&currentUserId=${currentUserId}`
          break
      }
      
      const postsRes = await request.get(url)
      posts.value = postsRes.data || []
      topics.value = []
    }
    
    await loadUserCache()
  } catch (error) {
    console.error('加载数据失败:', error)
    ElMessage.error('加载数据失败')
  }
}

// 选择分类（导航栏使用）
const selectCategory = (categoryId) => {
  // 导航栏分类点击逻辑
  console.log('导航栏选择分类:', categoryId)
}

// 选择话题分类（模态框使用）
const selectTopicCategory = (categoryId) => {
  console.log('点击分类:', categoryId, '类型:', typeof categoryId)
  console.log('当前选中分类:', newTopic.categoryId, '类型:', typeof newTopic.categoryId)
  
  // 确保categoryId不为null或undefined
  if (!categoryId) {
    console.log('categoryId为null或undefined')
    return
  }
  
  // 确保使用字符串类型进行比较，避免类型不一致导致的问题
  const idStr = String(categoryId)
  console.log('转换后的categoryId:', idStr, '类型:', typeof idStr)
  
  // 只有当点击的分类与当前选中的分类相同时才取消选中
  if (newTopic.categoryId === idStr) {
    console.log('取消选中分类:', idStr)
    newTopic.categoryId = null
  } else {
    // 否则选中该分类
    console.log('选中分类:', idStr)
    newTopic.categoryId = idStr
  }
  console.log('更新后选中分类:', newTopic.categoryId, '类型:', typeof newTopic.categoryId)
}



const handleTagChange = (tag) => {
  if (tag) {
    ElMessage.info(`已选择标签: ${tag}`)
    // 根据选中的标签和当前筛选类型（话题/帖子）获取数据
    fetchDataByTagAndType(tag, activeFilter.value)
  } else {
    // 清除标签选择，恢复默认数据
    fetchDataByTagAndType('', activeFilter.value)
  }
}

const fetchDataByTagAndType = async (tag, type) => {
  try {
    let url = ''
    const currentUserId = store.getters.userId
    if (type === 'topic') {
      if (tag) {
        url = currentUserId && currentUserId !== '' 
          ? `/topics?tag=${tag}&currentUserId=${currentUserId}`
          : `/topics?tag=${tag}`
      } else {
        url = currentUserId && currentUserId !== '' 
          ? `/topics?currentUserId=${currentUserId}`
          : '/topics'
      }
    } else if (type === 'post') {
      if (tag) {
        url = currentUserId && currentUserId !== '' 
          ? `/post/list?tag=${tag}&currentUserId=${currentUserId}`
          : `/post/list?tag=${tag}`
      } else {
        url = currentUserId && currentUserId !== '' 
          ? `/post/list?currentUserId=${currentUserId}`
          : '/post/list'
      }
    }
    
    const res = await request.get(url)
    if (type === 'topic') {
      const topicsData = res.data || []
      topics.value = topicsData
    } else if (type === 'post') {
      posts.value = res.data || []
    }
    
    // 加载用户缓存，包括关注状态
    await loadUserCache()
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const setActiveFilter = async (filterId) => {
  activeFilter.value = filterId
  await loadDataByNav(activeNav.value)
}

// 🔴 按筛选条件获取话题
const fetchTopicsByFilter = async (filterId) => {
  try {
    const res = await request.get(`/topics?filter=${filterId}`)
    topics.value = res.data || []
  } catch (error) {
    ElMessage.error('获取话题列表失败')
  }
}

const openCreateModal = () => {
  // 未登录跳转登录页
  if (!localStorage.getItem('token')) {
    ElMessage.warning('请先登录后创建话题')
    router.push('/login')
    return
  }
  showCreateModal.value = true
}

// 文本截断函数
const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 获取头像URL
const getAvatarUrl = (avatar) => {
  if (!avatar) {
    return 'https://picsum.photos/120/120'
  }
  
  // 检查是否已经是完整URL
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }
  
  // 处理相对路径
  let avatarPath = avatar
  if (!avatarPath.startsWith('/')) {
    avatarPath = '/' + avatarPath
  }
  
  // 构建完整URL
  return `http://localhost:8088/api/upload${avatarPath}`
}

// 获取帖子图片URL
const getImageUrl = (imageUrl) => {
  if (!imageUrl) {
    return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  }
  
  // 检查是否已经是完整URL
  if (imageUrl.startsWith('http://') || imageUrl.startsWith('https://')) {
    return imageUrl
  }
  
  // 处理相对路径
  let imgPath = imageUrl
  if (!imgPath.startsWith('/')) {
    imgPath = '/' + imgPath
  }
  
  // 构建完整URL
  return `http://localhost:8088${imgPath}`
}

// 获取话题名称
const getTopicName = (topicId) => {
  if (!topicId) return ''
  const topic = allTopics.value.find(t => t.topicId === topicId)
  return topic ? topic.topicTitle : '未知话题'
}

// 点击话题导航到话题详细页
const navigateToTopic = (topicId) => {
  router.push(`/topic/detail/${topicId}`)
}

// 点击头像导航到用户信息页面
const navigateToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
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

// 根据分类ID获取分类名称
const getCategoryName = (categoryId) => {
  const categoryMap = {
    1: '技术',
    2: '生活',
    3: '职业',
    4: '学习'
  }
  
  return categoryMap[categoryId] || '未知分类'
}

const closeCreateModal = () => {
  showCreateModal.value = false
  // 重置表单
  newTopic.categoryId = null
  newTopic.topicTitle = ''
  newTopic.topicDesc = ''
  newTopic.tags = []
  tagInput.value = ''
}

const showTopicMenu = (topicId) => {
  ElMessage.info(`操作话题 ID: ${topicId}`)
}

const handleLike = async (topicId) => {
  try {
    const currentUserId = store.getters.userId
    if (!currentUserId) {
      ElMessage.warning('请先登录后再点赞')
      return
    }
    
    const topic = topics.value.find(t => t.topicId === topicId)
    if (!topic) return
    
    // 确定点赞状态和count值
    const isLiked = topic.isLiked || false
    const count = isLiked ? -1 : 1
    
    // 调用点赞/取消点赞接口，使用URL查询字符串传递参数
    await request.post(`/topic/like?topicId=${topicId}&count=${count}&currentUserId=${currentUserId}`)
    
    // 更新本地状态
    topic.isLiked = !isLiked
    topic.likeCount += count
    
    ElMessage.success(isLiked ? '取消点赞成功' : '点赞成功')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleShare = (topicId) => {
  // 分享话题功能
  const topic = topics.value.find(t => t.topicId === topicId)
  const content = topic ? topic.topicTitle : '分享了一个话题给你'
  router.push({
    path: '/share/select-users',
    query: {
      type: 'topic',
      id: topicId,
      content: content
    }
  })
}

const handleSharePost = (postId) => {
  // 分享帖子功能
  const post = posts.value.find(p => p.postId === postId)
  const content = post ? post.postContent : '分享了一篇帖子给你'
  router.push({
    path: '/share/select-users',
    query: {
      type: 'post',
      id: postId,
      content: content
    }
  })
}

// 浏览量接口改为只读，移除点击事件
const handleView = (topicId) => {
  // 仅显示浏览量，不调用接口
  ElMessage.info('浏览量已记录')
}

const handleReply = (topicId) => {
  ElMessage.info(`回复话题 ID: ${topicId}`)
  // 可跳转回复页面，此处简化
}

// 帖子相关函数
const handleLikePost = async (postId) => {
  try {
    const currentUserId = store.getters.userId
    if (!currentUserId || currentUserId === '') {
      ElMessage.warning('请先登录后再点赞')
      return
    }
    
    const post = posts.value.find(p => p.postId === postId)
    if (!post) return
    
    const isLiked = post.isLiked || false
    const count = isLiked ? -1 : 1
    
    await request.put(`/post/like/${postId}?count=${count}&userId=${currentUserId}`)
    
    post.isLiked = !isLiked
    post.likeCount += count
    
    ElMessage.success(isLiked ? '取消点赞成功' : '点赞成功')
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('操作失败')
  }
}

const navigateToPost = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const loadMoreTopics = async () => {
  try {
    const currentPage = Math.ceil(topics.value.length / 10) + 1 // 假设每页10条
    const res = await request.get(`/topics?page=${currentPage}&size=10`)
    const newTopics = res.data || []
    
    // 从Vuex中获取点赞状态并更新话题数据
    newTopics.forEach(topic => {
      const likeStatus = store.getters.getTopicLikeStatus(topic.topicId)
      if (likeStatus) {
        topic.isLiked = likeStatus.isLiked
        topic.likeCount = likeStatus.likeCount
      }
    })
    
    topics.value = [...topics.value, ...newTopics]
    ElMessage.success('加载更多成功')
  } catch (error) {
    ElMessage.error('加载更多失败')
  }
}

const loadMorePosts = async () => {
  try {
    const currentPage = Math.ceil(posts.value.length / 10) + 1
    const currentUserId = store.getters.userId
    const url = currentUserId && currentUserId !== '' 
      ? `/post/list?pageNum=${currentPage}&pageSize=10&currentUserId=${currentUserId}`
      : `/post/list?pageNum=${currentPage}&pageSize=10`
    const res = await request.get(url)
    posts.value = [...posts.value, ...res.data]
    ElMessage.success('加载更多成功')
  } catch (error) {
    ElMessage.error('加载更多失败')
  }
}

const refreshTrending = async () => {
  try {
    const res = await request.get('/topics/trending')
    trendingTopics.value = res.data || []
    ElMessage.success('刷新热门话题成功')
  } catch (error) {
    ElMessage.error('刷新热门话题失败')
  }
}

// 关注/取消关注用户
const toggleFollow = async (userId) => {
  try {
    // 1. 从Vuex获取登录状态和用户ID
    const isLogin = store.state.isLogin
    const currentUserId = store.getters.userId
    
    // 2. 未登录判断：直接弹提示并返回
    if (!isLogin || !currentUserId) {
      ElMessage.warning('请先登录后再关注用户')
      return
    }

    // 3. 乐观更新：先更新本地状态
    const currentUser = userCache.value[userId]
    const oldFollowing = currentUser?.following || false
    const newFollowing = !oldFollowing
    
    // 更新用户缓存中的关注状态
    if (currentUser) {
      currentUser.following = newFollowing
    }
    
    // 更新活跃用户列表中的关注状态
    if (activeUsers.value) {
      const activeUser = activeUsers.value.find(user => user.id === userId)
      if (activeUser) {
        activeUser.following = newFollowing
      }
    }

    // 4. 调用关注/取消关注接口
    const followRes = await request.post(`/users/${userId}/follow?currentUserId=${currentUserId}`)
    
    // 4.1 后端返回失败（比如不能关注自己），显示具体提示并回滚状态
    if (followRes && !followRes.success) {
      // 回滚状态
      if (currentUser) {
        currentUser.following = oldFollowing
      }
      if (activeUsers.value) {
        const activeUser = activeUsers.value.find(user => user.id === userId)
        if (activeUser) {
          activeUser.following = oldFollowing
        }
      }
      ElMessage.warning(followRes.message || '操作失败')
      return
    }

    // 6. 重新获取活跃用户列表（更新关注状态）
    const usersRes = await request.get(`/users/active?currentUserId=${currentUserId}`)
    activeUsers.value = usersRes.data || []
    
    // 6. 重新加载用户缓存，确保所有用户的关注状态正确
    await loadUserCache()
    
    ElMessage.success('操作成功')
  } catch (error) {
    // 捕获网络异常/接口报错，提取具体错误信息
    let errMsg = '操作失败'
    if (error.response) {
      errMsg = error.response.data?.message || errMsg
    } else if (error.data) {
      errMsg = error.data?.message || errMsg
    } else if (error.message) {
      errMsg = error.message
    }
    ElMessage.error(errMsg)
  }
}

const selectTag = (tag) => {
  ElMessage.info(`筛选标签: ${tag}`)
  // 可调用标签筛选接口，此处简化
}

const addTag = () => {
  const tag = tagInput.value.trim()
  if (tag && !newTopic.tags.includes(tag) && newTopic.tags.length < 5) {
    newTopic.tags.push(tag)
    tagInput.value = ''
  } else if (newTopic.tags.length >= 5) {
    ElMessage.warning('最多只能添加5个标签')
  }
}

const removeTag = (tag) => {
  const index = newTopic.tags.indexOf(tag)
  if (index !== -1) {
    newTopic.tags.splice(index, 1)
  }
}

// 发布话题
const publishTopic = async () => {
  // 表单校验
  if (!newTopic.categoryId) {
    ElMessage.warning('请选择话题分类')
    return
  }
  if (!newTopic.topicTitle.trim()) {
    ElMessage.warning('请输入话题标题')
    return
  }
  if (!newTopic.topicDesc.trim()) {
    ElMessage.warning('请输入话题描述')
    return
  }

  try {
    // 构造请求参数（符合指定格式）
    const requestData = {
      categoryId: Number(newTopic.categoryId),
      topicTitle: newTopic.topicTitle.trim(),
      topicDesc: newTopic.topicDesc.trim(),
      creatorId: userInfo.value?.id || null, // 登录用户ID
      tags: newTopic.tags.join(',') // 前端标签数组转的字符串
      // status可选传，不传后端默认1
    }

    // 调用创建话题接口
    const res = await request.post('/topic/create', requestData)
    
    // 创建成功处理
    ElMessage.success('话题发布成功！')
    closeCreateModal()
    
    // 刷新话题列表（新增的话题放在最前面）
    topics.value.unshift(res.data)
  } catch (error) {
    ElMessage.error(`发布失败: ${error.message}`)
  }
}

// 初始化加载数据
onMounted(() => {
  initData()
})
</script>


<style scoped>
.forum-container {
  min-height: 100vh;
  background-color: #f5f5f7;
}

/* 主内容区 */
.forum-main {
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
}

.main-container {
  display: flex;
  gap: 32px;
}

/* 左侧导航栏 */
.forum-sidebar {
  width: 240px;
  flex-shrink: 0;
  position: sticky;
  top: 100px;
  height: calc(100vh - 180px);
  overflow-y: auto;
}

.sidebar-nav {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.nav-section {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.nav-title {
  font-size: 13px;
  font-weight: 600;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 16px;
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 4px;
}

.nav-item.active .nav-link {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  color: #1d1d1f;
  text-decoration: none;
  transition: all 0.2s ease;
  font-weight: 500;
}

.nav-link:hover {
  background-color: #f5f5f7;
}

.nav-icon {
  color: currentColor;
}

/* 分类列表 */
.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  margin-bottom: 8px;
}

.category-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  text-decoration: none;
  color: #1d1d1f;
  transition: all 0.2s ease;
}

.category-link:hover {
  background-color: #f5f5f7;
}

.category-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.category-icon:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.category-icon.tech {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
}

.category-icon.life {
  background: linear-gradient(135deg, #34C759 0%, #4CD964 100%);
}

.category-icon.career {
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
}

.category-icon.learning {
  background: linear-gradient(135deg, #AF52DE 0%, #D858E8 100%);
}

.category-icon.hot {
  background: linear-gradient(135deg, #FF3B30 0%, #FF6B6B 100%);
  position: relative;
}

.category-icon.hot::after {
  content: 'HOT';
  position: absolute;
  top: -4px;
  right: -4px;
  background-color: #FF3B30;
  color: white;
  font-size: 8px;
  font-weight: 700;
  padding: 2px 4px;
  border-radius: 6px;
  transform: rotate(15deg);
}

.category-icon.normal {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
}

.category-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
}

.category-count {
  font-size: 12px;
  color: #6e6e73;
}

/* 在线统计 */
.online-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding: 8px;
  background-color: #f5f5f7;
  border-radius: 10px;
}

.stat-value {
  font-size: 18px;
  font-weight: 700;
  color: #007AFF;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 11px;
  color: #6e6e73;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 主内容区 */
.forum-content {
  flex: 1;
  min-width: 0;
}

/* 创建话题卡片 */
.create-topic-card {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.create-topic-card:hover {
  border-color: #e5e5ea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.create-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: rgba(0, 122, 255, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #007AFF;
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.author-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  flex-shrink: 0;
  overflow: hidden;
}

.user-item .user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
}

.create-prompt {
  flex: 1;
}

.prompt-text {
  color: #6e6e73;
  font-size: 15px;
}

.create-action {
  background: none;
  border: none;
  color: #86868b;
  cursor: pointer;
  padding: 8px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.create-action:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

/* 话题筛选 */
.topic-filters {
  background-color: white;
  border-radius: 16px;
  padding: 16px 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tab {
  background: none;
  border: none;
  padding: 8px 16px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #6e6e73;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-tab.active {
  background-color: #007AFF;
  color: white;
}

.filter-tab:hover:not(.active) {
  background-color: #f5f5f7;
}

.filter-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: 1px solid #e5e5ea;
  padding: 8px 12px;
  border-radius: 10px;
  font-size: 13px;
  color: #6e6e73;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-button:hover {
  border-color: #007AFF;
  color: #007AFF;
}

/* 话题卡片 */
.topics-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.topic-card {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.topic-card:hover {
  border-color: #e5e5ea;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.topic-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.topic-author {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.author-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  flex-shrink: 0;
}

.author-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.author-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.follow-button {
  background-color: #007AFF;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.follow-button:hover {
  background-color: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 122, 255, 0.2);
}

.follow-button.following {
  background-color: #f5f5f7;
  color: #666;
  border: 1px solid #e5e5ea;
}

.follow-button.following:hover {
  background-color: #e5e5ea;
  color: #333;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.author-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #6e6e73;
}

.topic-time {
  position: relative;
}

.topic-time::after {
  content: "•";
  margin-left: 16px;
  color: #e5e5ea;
}

.topic-category {
  color: #007AFF;
  font-weight: 500;
}

.topic-menu {
  background: none;
  border: none;
  color: #86868b;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.topic-menu:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.topic-content {
  margin-bottom: 24px;
}

.topic-title-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.topic-title {
  font-size: 18px;
  font-weight: 700;
  color: #1d1d1f;
  line-height: 1.4;
  flex: 1;
  margin-right: 10px;
}

.hot-tag {
  background-color: #ff4d4f;
  color: white;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.topic-excerpt {
  font-size: 15px;
  color: #6e6e73;
  line-height: 1.6;
  margin-bottom: 16px;
}

.topic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.topic-tag {
  font-size: 12px;
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 500;
}

.topic-media {
  margin-top: 16px;
}

.media-preview {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  background-color: #f5f5f7;
  overflow: hidden;
}

.media-preview.image .media-content {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #efeef4 0%, #f5f5f7 100%);
}

/* 帖子照片样式 */
.post-media {
  margin-top: 16px;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.media-item {
  border-radius: 8px;
  overflow: hidden;
  background-color: #f5f5f7;
  aspect-ratio: 1;
}

.post-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.topic-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 16px;
  border-top: 1px solid #e5e5ea;
}

.topic-stats {
  display: flex;
  align-items: center;
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
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
  font-size: 13px;
  font-weight: 500;
}

.stat-button:hover {
  color: #FF3B30;
  background-color: rgba(255, 59, 48, 0.1);
}

.stat-button.liked {
  color: #FF3B30;
}

.stat-button.liked:hover {
  color: #FF3B30;
  background-color: rgba(255, 59, 48, 0.1);
}

.reply-button {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: 1px solid #e5e5ea;
  padding: 8px 16px;
  border-radius: 10px;
  color: #6e6e73;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.reply-button:hover {
  border-color: #007AFF;
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
}

/* 加载更多 */
.load-more {
  text-align: center;
  margin-top: 32px;
}

.load-more-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: 1px solid #e5e5ea;
  padding: 16px 32px;
  border-radius: 16px;
  color: #6e6e73;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  margin: 0 auto;
  transition: all 0.2s ease;
}

.load-more-button:hover {
  border-color: #007AFF;
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  transform: translateY(-1px);
}

/* 右侧边栏 */
.forum-rightbar {
  width: 300px;
  flex-shrink: 0;
  position: sticky;
  top: 100px;
  height: calc(100vh - 180px);
  overflow-y: auto;
}

.rightbar-section {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px;
  font-weight: 700;
  color: #1d1d1f;
}

.refresh-button {
  background: none;
  border: none;
  color: #86868b;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.refresh-button:hover {
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

/* 热门话题 */
.trending-topics {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.trending-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 12px;
  border-radius: 10px;
}

.trending-item:hover {
  background-color: #f5f5f7;
  transform: translateY(-2px);
}

.trending-rank {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 12px;
  font-weight: 700;
  color: #6e6e73;
}

.rank-1 {
  background-color: #FF3B30 !important;
  color: white !important;
}

.rank-2 {
  background-color: #FF9500 !important;
  color: white !important;
}

.rank-3 {
  background-color: #FFCC00 !important;
  color: white !important;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.modal-overlay.active {
  opacity: 1;
  visibility: visible;
}

.create-modal {
  background-color: white;
  border-radius: 20px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  transform: translateY(-20px);
}

.modal-overlay.active .create-modal {
  transform: translateY(0);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
  border-bottom: 1px solid #e5e5ea;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.modal-close {
  background: none;
  border: none;
  color: #86868b;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-close:hover {
  background-color: rgba(0, 0, 0, 0.05);
  color: #1d1d1f;
}

.modal-content {
  padding: 32px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.required {
  color: #FF3B30;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e5e5ea;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e5e5ea;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  resize: vertical;
  transition: all 0.2s ease;
  box-sizing: border-box;
  min-height: 120px;
}

.form-textarea:focus {
  outline: none;
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.input-hint {
  font-size: 12px;
  color: #6e6e73;
  margin-top: 4px;
  text-align: right;
}

.category-selector {
  margin-top: 8px;
}

.category-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.category-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #e5e5ea;
  border-radius: 12px;
  background-color: #f5f5f7;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #1d1d1f;
}

.category-option:hover {
  border-color: #007AFF;
  background-color: rgba(0, 122, 255, 0.05);
}

.category-option.active {
  border-color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
}

.category-icon {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  flex-shrink: 0;
}

.category-icon.hot {
  background-color: #FF3B30;
}

.category-icon.normal {
  background-color: #007AFF;
}

.tag-input {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.selected-tag {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  background-color: rgba(0, 122, 255, 0.1);
  border-radius: 16px;
  font-size: 13px;
  color: #007AFF;
}

.remove-tag {
  background: none;
  border: none;
  color: #007AFF;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
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

.tag-input-field {
  padding: 12px 16px;
  border: 1px solid #e5e5ea;
  border-radius: 12px;
  font-size: 14px;
  color: #1d1d1f;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.tag-input-field:focus {
  outline: none;
  border-color: #007AFF;
  box-shadow: 0 0 0 3px rgba(0, 122, 255, 0.1);
}

.tag-input-field:disabled {
  background-color: #f5f5f7;
  cursor: not-allowed;
}

.modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px 32px;
  border-top: 1px solid #e5e5ea;
}

.modal-button {
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;
}

.modal-button.secondary {
  background-color: #f5f5f7;
  color: #1d1d1f;
}

.modal-button.secondary:hover {
  background-color: #e5e5ea;
}

.modal-button.primary {
  background-color: #007AFF;
  color: white;
}

.modal-button.primary:hover {
  background-color: #0056b3;
  transform: translateY(-1px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .create-modal {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-content,
  .modal-footer {
    padding: 20px;
  }
  
  .category-options {
    justify-content: center;
  }
}

.trending-content {
  flex: 1;
}

.trending-title {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 4px;
  line-height: 1.4;
}

.trending-desc {
  font-size: 12px;
  color: #6e6e73;
  margin-bottom: 4px;
  line-height: 1.4;
}

.trending-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #6e6e73;
}

.trending-count {
  position: relative;
}

.trending-count::after {
  content: "•";
  margin-left: 16px;
  color: #e5e5ea;
}

/* 活跃用户 */
.active-users {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.user-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.user-item .user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
  flex-shrink: 0;
  position: relative;
}

.user-item .user-info {
  flex: 1;
}

.user-item .user-name {
  font-size: 13px;
  font-weight: 600;
  color: #1d1d1f;
}

.user-item .user-activity {
  font-size: 12px;
  color: #6e6e73;
}

/* VIP用户样式 */
.vip-avatar {
  color: #FFD700;
  background-color: rgba(255, 215, 0, 0.1);
  border: 1px solid #FFD700;
  position: relative;
}

.vip-avatar::after {
  content: '';
  position: absolute;
  bottom: -1px;
  right: -1px;
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  border-radius: 50%;
  border: 1px solid #fff;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

/* VIP标识样式 */
.vip-badge {
  display: inline-block;
  font-size: 9px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #FFD700 0%, #FFA500 100%);
  padding: 1px 4px;
  border-radius: 6px;
  margin-left: 4px;
  vertical-align: middle;
  text-transform: uppercase;
  letter-spacing: 0;
}

/* 关注按钮样式 */
.follow-button {
  background-color: #007AFF;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.follow-button:hover {
  background-color: #0056b3;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 122, 255, 0.2);
}

.follow-button.following {
  background-color: #f5f5f7;
  color: #666;
  border: 1px solid #e5e5ea;
}

.follow-button.following:hover {
  background-color: #e5e5ea;
  color: #333;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 热门标签 */
.popular-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  font-size: 12px;
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.tag:hover {
  background-color: rgba(0, 122, 255, 0.2);
}
/* 帖子照片展示样式 */
.post-media {
  margin-top: 12px;
}

.media-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.media-item {
  border-radius: 6px;
  overflow: hidden;
  aspect-ratio: 1;
  background-color: #f5f5f7;
}

.post-photo {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-photo:hover {
  transform: scale(1.05);
}

.author-meta .post-topic-inline {
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 8px;
  display: inline-block;
  box-shadow: 0 1px 4px rgba(58, 123, 213, 0.2);
}

/* 帖子内容样式 */
.post-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  margin-bottom: 12px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 帖子话题样式 */
.post-topic {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%);
  border-radius: 20px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(58, 123, 213, 0.2);
  transition: all 0.3s ease;
}

.post-topic:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(58, 123, 213, 0.3);
}

.post-topic .topic-label {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
}

.post-topic .topic-name {
  font-size: 13px;
  color: #ffffff;
  font-weight: 600;
}

/* 统计按钮样式调整 */
.stat-button {
  cursor: pointer;
}

.stat-button:not(button) {
  cursor: default;
}

.stat-button:not(button):hover {
  color: #6e6e73;
  background-color: transparent;
}

/* 空数据提示 */
.empty-data {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 15px;
  background-color: #fafafa;
  border-radius: 12px;
  margin-top: 20px;
}
</style>