<template>
  <div :class="['user-profile-container', { 'vip-user': userInfo.isVip }]">
    <!-- 顶部信息卡片 -->
    <div class="profile-header">
      <div class="header-content">
        <!-- 用户基本信息 -->
        <div class="user-basic-info">
          <div class="avatar-section">
            <div class="avatar-wrapper">
            <img 
              :src="getAvatarUrl(userInfo.avatar)" 
              alt="用户头像"
              class="user-avatar"
            />
            <button class="avatar-upload" @click="openAvatarUpload">
              <el-icon :size="20"><Upload /></el-icon>
            </button>
          </div>
          </div>
          
          <div class="user-details">
            <div class="user-name-section">
              <h1 class="user-name">{{ userInfo.nickname }}</h1>
              <el-tag v-if="userInfo.isVip" type="warning" class="vip-tag">
                VIP
              </el-tag>
              <el-tag v-if="userInfo.isSpamUser" type="danger" class="spam-tag">
                劣迹用户
              </el-tag>
              <el-tag v-if="isQualityUser()" type="success" class="quality-tag">
                优质用户
              </el-tag>
              <el-button type="primary" size="small" @click="editProfile">编辑资料</el-button>
            </div>
            
            <p class="user-bio">{{ userInfo.bio || '这个人很懒，什么都没有写～' }}</p>
            
            <div class="user-stats">
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.followCount || 0 }}</span>
                <span class="stat-label">关注</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.fansCount || 0 }}</span>
                <span class="stat-label">粉丝</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.postCount || 0 }}</span>
                <span class="stat-label">帖子</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.likeCount || 0 }}</span>
                <span class="stat-label">获赞</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.commentCount || 0 }}</span>
                <span class="stat-label">评论</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userInfo.visitCount || 0 }}</span>
                <span class="stat-label">访问</span>
              </div>
            </div>
            
            <div class="user-meta">
              <span class="meta-item">
                <el-icon :size="16"><Timer /></el-icon>
                加入时间: {{ userInfo.createTime ? formatDateTime(userInfo.createTime) : '未知' }}
              </span>
              <span class="meta-item">
                <el-icon :size="16"><Position /></el-icon>
                上次登录: {{ userInfo.lastLoginTime ? formatDateTime(userInfo.lastLoginTime) : '未知' }}
              </span>
              <span class="meta-item">
                <el-icon :size="16"><User /></el-icon>
                性别: {{ getUserGenderText(userInfo.gender) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 功能导航 -->
    <div class="profile-navigation">
      <el-menu 
        :default-active="activeTab" 
        mode="horizontal" 
        @select="handleTabSelect"
        class="nav-menu"
      >
        <el-menu-item index="overview">概览</el-menu-item>
        <el-menu-item index="posts">我的帖子</el-menu-item>
        <el-menu-item index="topics">我的话题</el-menu-item>
        <el-menu-item index="comments">我的评论</el-menu-item>
        <el-menu-item index="follow">关注</el-menu-item>
        <el-menu-item index="followers">粉丝</el-menu-item>
        <el-menu-item index="recharge">个人充值</el-menu-item>
        <el-menu-item index="account">账户设置</el-menu-item>
      </el-menu>
    </div>
    
    <!-- 主要内容区域 -->
    <div class="profile-main">
      <div class="main-content">
        <!-- 左侧 - 概览信息 -->
        <div class="left-sidebar" v-if="activeTab === 'overview'">
          <!-- 快速入口 -->
          <div class="info-card">
            <h3 class="card-title">
              <el-icon :size="20"><Grid /></el-icon>
              快速入口
            </h3>
            <div class="quick-access-sidebar">
              <div class="access-item" v-for="item in quickAccess" :key="item.id" @click="navigateTo(item.path)">
                <div class="access-icon" :class="item.className">
                  <el-icon :size="24"><Edit /></el-icon>
                </div>
                <span class="access-name">{{ item.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 中间 - 主要内容 -->
        <div class="center-content">
          <!-- 我的帖子 -->
          <div v-if="activeTab === 'posts'">
            <div class="section-header">
              <h2 class="section-title">我的帖子</h2>
              <el-button type="primary" @click="createPost">发布新帖子</el-button>
            </div>
            
            <div class="posts-list">
              <div v-if="userPosts && userPosts.length > 0">
                <div class="post-item" v-for="post in userPosts" :key="post.id" @click="navigateToPost(post.id)">
                  <div class="post-header">
                    <span class="post-category">{{ post.categoryName || '未分类' }}</span>
                    <span class="post-time">{{ post.time }}</span>
                  </div>
                  <h3 class="post-title">{{ post.title }}</h3>
                  <p class="post-excerpt">{{ post.excerpt }}</p>
                  <div class="post-stats">
                    <span class="post-stat">
                      <el-icon :size="16"><Star /></el-icon>
                      {{ post.likes }}
                    </span>
                    <span class="post-stat">
                      <el-icon :size="16"><ChatDotRound /></el-icon>
                      {{ post.comments }}
                    </span>
                    <span class="post-stat">
                      <el-icon :size="16"><DataLine /></el-icon>
                      {{ post.views }}
                    </span>
                  </div>
                  <div class="post-actions" @click.stop>
                    <el-button size="small" @click="editPost(post.id)">编辑</el-button>
                    <el-button size="small" type="danger" @click="deletePost(post.id)">删除</el-button>
                  </div>
                </div>
              </div>
              <div v-else class="empty-data">
                <p>暂无帖子记录</p>
                <el-button type="primary" @click="createPost">发布第一篇帖子</el-button>
              </div>
            </div>
          </div>

          <!-- 我的评论 -->
          <div v-else-if="activeTab === 'comments'">
            <div class="section-header">
              <h2 class="section-title">我的评论</h2>
            </div>
            
            <div class="comments-container">
              <div v-if="userComments && userComments.length > 0">
                <div class="comment-item" v-for="comment in userComments" :key="comment.commentId">
                  <div class="comment-content">
                    <div class="comment-header">
                      <el-tag v-if="comment.commentType === 1" type="success" size="small">正常评论</el-tag>
                      <el-tag v-else-if="comment.commentType === -1" type="warning" size="small">负向评论</el-tag>
                      <el-tag v-else-if="comment.commentType === -2" type="danger" size="small">垃圾评论</el-tag>
                      <el-tag v-else-if="comment.commentType === 0" type="info" size="small">待评评论</el-tag>
                      <el-tag v-else type="info" size="small">未知状态</el-tag>
                      <el-button 
                        v-if="comment.commentType === -2 && !comment.appealed" 
                        type="warning" 
                        size="small" 
                        @click.stop="appealComment(comment)"
                        class="appeal-btn"
                      >
                        申诉
                      </el-button>
                      <el-tag 
                        v-if="comment.commentType === -2 && comment.appealed" 
                        type="warning" 
                        size="small"
                        class="appealed-tag"
                      >
                        已申诉
                      </el-tag>
                    </div>
                    <p class="comment-text">{{ comment.commentContent }}</p>
                    <div class="comment-meta">
                      <span class="comment-time">{{ comment.createTime ? formatDateTime(comment.createTime) : '未知' }}</span>
                      <span class="comment-likes">
                        <el-icon :size="16"><Star /></el-icon>
                        {{ comment.likeCount || 0 }}
                      </span>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-data">
                <p>暂无评论记录</p>
              </div>
            </div>
          </div>

          <!-- 关注页面 -->
          <div v-else-if="activeTab === 'follow'">
            <div class="section-header">
              <h2 class="section-title">我的关注</h2>
            </div>
            
            <div class="follow-container">
              <div v-if="followingList && followingList.length > 0">
                <div class="follow-item" v-for="user in followingList" :key="user.id">
                  <div class="follow-avatar">
                    <img :src="getAvatarUrl(user.avatar)" alt="用户头像" class="user-avatar-small">
                  </div>
                  <div class="follow-info">
                    <h3 class="follow-name">{{ user.nickname || user.username }}</h3>
                    <p class="follow-bio">{{ user.bio || '暂无简介' }}</p>
                  </div>
                  <el-button class="follow-button following" size="small" @click="toggleFollow(user.id)">已关注</el-button>
                </div>
              </div>
              <div v-else class="empty-data">
                <p>暂无关注记录</p>
              </div>
            </div>
          </div>

          <!-- 粉丝页面 -->
          <div v-else-if="activeTab === 'followers'">
            <div class="section-header">
              <h2 class="section-title">我的粉丝</h2>
            </div>
            
            <div class="follow-container">
              <div v-if="followersList && followersList.length > 0">
                <div class="follow-item" v-for="user in followersList" :key="user.id">
                  <div class="follow-avatar">
                    <img :src="getAvatarUrl(user.avatar)" alt="用户头像" class="user-avatar-small">
                  </div>
                  <div class="follow-info">
                    <h3 class="follow-name">{{ user.nickname || user.username }}</h3>
                    <p class="follow-bio">{{ user.bio || '暂无简介' }}</p>
                  </div>
                  <el-button 
                    :class="['follow-button', { 'following': isUserFollowed(user.id) }]" 
                    size="small" 
                    @click="toggleFollow(user.id)"
                  >
                    {{ isUserFollowed(user.id) ? '已关注' : '关注' }}
                  </el-button>
                </div>
              </div>
              <div v-else class="empty-data">
                <p>暂无粉丝记录</p>
              </div>
            </div>
          </div>

          <!-- 我发布的话题 -->
          <div v-else-if="activeTab === 'topics'">
            <div class="section-header">
              <h2 class="section-title">我的话题</h2>
            </div>
            
            <div class="topics-container">
              <div v-if="userTopics && userTopics.length > 0">
                <div 
                  class="topic-item" 
                  v-for="topic in userTopics" 
                  :key="topic.topicId"
                  @click="navigateToTopic(topic.topicId)"
                >
                  <div class="topic-header">
                    <div class="topic-title-container">
                      <span class="topic-title">{{ topic.topicTitle }}</span>
                      <span v-if="topic.isHot === 1" class="hot-tag">热门</span>
                    </div>
                    <span class="topic-time">{{ topic.createTime ? formatDateTime(topic.createTime) : '未知' }}</span>
                  </div>
                  <p class="topic-description">{{ topic.topicDesc }}</p>
                  <div class="topic-stats">
                    <span class="stat">
                      <el-icon :size="16"><ChatDotRound /></el-icon>
                      {{ topic.participantCount || 0 }} 帖子
                    </span>
                    <span class="stat">
                      <el-icon :size="16"><View /></el-icon>
                      {{ topic.viewCount || 0 }} 浏览
                    </span>
                  </div>
                </div>
              </div>
              <div v-else class="empty-data">
                <p>暂无话题记录</p>
              </div>
            </div>
          </div>

          <!-- 个人充值 -->
          <div v-else-if="activeTab === 'recharge'">
            <div class="section-header">
              <h2 class="section-title">个人充值</h2>
            </div>
            
            <div class="recharge-container">
              <!-- 余额显示 -->
              <el-card class="balance-card">
                <div class="balance-info">
                  <div class="balance-label">当前余额</div>
                  <div class="balance-amount">¥{{ userInfo.balance || 0 }}</div>
                </div>
              </el-card>

              <!-- 充值选项 -->
              <el-card class="recharge-options">
                <div class="amount-grid">
                  <div 
                    v-for="amount in rechargeAmounts" 
                    :key="amount"
                    :class="['amount-item', { 'active': selectedAmount === amount }]"
                    @click="selectedAmount = amount"
                  >
                    <span class="amount-value">¥{{ amount }}</span>
                  </div>
                </div>
                <div class="custom-amount">
                  <el-input-number 
                    v-model="customAmount" 
                    :min="1" 
                    :max="10000" 
                    placeholder="自定义金额"
                  />
                </div>
              </el-card>

              <!-- 充值按钮 -->
              <div class="recharge-actions">
                <el-button type="primary" size="large" @click="handleRecharge">
                  立即充值
                </el-button>
              </div>
            </div>
          </div>

          <!-- 账户设置 -->
          <div v-else-if="activeTab === 'account'">
            <div class="account-settings">
              <!-- 安全设置 -->
              <el-card class="setting-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">安全设置</span>
                  </div>
                </template>
                <div class="security-items">
                  <div class="security-item">
                    <div class="security-info">
                      <div class="security-header">
                        <h4>邮箱安全</h4>
                        <el-tag :type="userInfo.email && userInfo.email !== '未知' && userInfo.email !== '未设置' ? 'success' : 'warning'" size="small">
                          {{ userInfo.email && userInfo.email !== '未知' && userInfo.email !== '未设置' ? '已设置' : '未设置' }}
                        </el-tag>
                      </div>
                      <p>{{ userInfo.email && userInfo.email !== '未知' && userInfo.email !== '未设置' ? userInfo.email : '建议设置邮箱以接收重要通知' }}</p>
                    </div>
                  </div>
                  <div class="security-item">
                    <div class="security-info">
                      <div class="security-header">
                        <h4>手机号安全</h4>
                        <el-tag :type="userInfo.phone && userInfo.phone !== '未知' && userInfo.phone !== '未设置' ? 'success' : 'warning'" size="small">
                          {{ userInfo.phone && userInfo.phone !== '未知' && userInfo.phone !== '未设置' ? '已设置' : '未设置' }}
                        </el-tag>
                      </div>
                      <p>{{ userInfo.phone && userInfo.phone !== '未知' && userInfo.phone !== '未设置' ? userInfo.phone : '建议设置手机号以保护账户安全' }}</p>
                    </div>
                  </div>
                  <div class="security-item">
                    <div class="security-info">
                      <div class="security-header">
                        <h4>账户安全状态</h4>
                        <el-tag :type="isAccountSecure() ? 'success' : 'danger'" size="small">
                          {{ isAccountSecure() ? '安全' : '不安全' }}
                        </el-tag>
                      </div>
                      <p>{{ isAccountSecure() ? '您的账户安全设置完善' : '请完善邮箱和手机号设置以提高账户安全性' }}</p>
                    </div>
                  </div>
                </div>
              </el-card>

              <!-- 退出登录 -->
              <el-card class="setting-card">
                <template #header>
                  <div class="card-header">
                    <span class="card-title">退出登录</span>
                  </div>
                </template>
                <div class="logout-section">
                  <p class="logout-desc">退出登录后，您需要重新登录才能访问账户</p>
                  <el-button type="danger" @click="handleLogout">
                    <el-icon class="logout-icon"><SwitchButton /></el-icon>
                    退出登录
                  </el-button>
                </div>
              </el-card>


            </div>
          </div>

          <!-- 概览页面 -->
          <div v-else-if="activeTab === 'overview'">
            <div class="overview-content">
              <!-- 用户相关数据 -->
              <div class="user-related-data">
                <h2 class="section-title">用户数据</h2>
                <div class="user-data-grid">
                  <!-- 基本信息 -->
                  <div class="user-data-card">
                    <h3 class="card-subtitle">基本信息</h3>
                    <div class="data-items">
                      <div class="data-item">
                        <span class="data-label">用户名：</span>
                        <span class="data-value">{{ userInfo.username || '未设置' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">昵称：</span>
                        <span class="data-value">{{ userInfo.nickname || '未设置' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">邮箱：</span>
                        <span class="data-value">{{ userInfo.email || '未设置' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">手机号：</span>
                        <span class="data-value">{{ userInfo.phone || '未设置' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">性别：</span>
                        <span class="data-value">{{ getUserGenderText(userInfo.gender) }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">个人简介：</span>
                        <span class="data-value">{{ userInfo.bio || '未设置' }}</span>
                      </div>
                    </div>
                  </div>
                  
                  <!-- 账号信息 -->
                  <div class="user-data-card">
                    <h3 class="card-subtitle">账号信息</h3>
                    <div class="data-items">
                      <div class="data-item">
                        <span class="data-label">加入时间：</span>
                        <span class="data-value">{{ userInfo.createTime ? formatDateTime(userInfo.createTime) : '未知' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">上次登录：</span>
                        <span class="data-value">{{ userInfo.lastLoginTime ? formatDateTime(userInfo.lastLoginTime) : '未知' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">账号状态：</span>
                        <span class="data-value">{{ userInfo.isSpamUser ? '劣迹用户' : '正常' }}</span>
                      </div>
                      <div class="data-item">
                        <span class="data-label">用户等级：</span>
                        <span class="data-value">{{ isQualityUser() ? '优质用户' : '普通用户' }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧 - 功能卡片 -->
        <div class="right-sidebar">
          <!-- 会员状态 -->
          <div class="info-card">
            <h3 class="card-title">会员状态</h3>
            <div class="vip-status" v-if="userInfo.isVip">
              <div class="vip-badge">
                <span class="vip-level">VIP</span>
                <span class="vip-expire">有效期至: {{ userInfo.vipExpireTime ? formatDate(userInfo.vipExpireTime) : '未知' }}</span>
              </div>
              <el-progress :percentage="vipRemainingDays" :stroke-width="8" />
              <p class="vip-benefits">享受专属标识、更大上传限制、专属客服等特权</p>

              <router-link to="/VipPayment" class="btn-link">
                <el-button type="warning" class="vip-renew-btn">续费会员</el-button>
              </router-link>
            </div>
            <div class="vip-status" v-else>
              <p class="vip-invite">还不是会员</p>
              <p class="vip-desc">开通会员享受更多特权</p>
              <router-link to="/VipPayment" class="btn-link">
              <el-button type="primary" @click="openVip">开通会员</el-button>
              </router-link>
            </div>
          </div>

          <!-- 账户安全 -->
          <div class="info-card">
            <h3 class="card-title">账户安全</h3>
            <div class="security-status">
              <div class="security-item">
                <div :class="['security-icon', isAccountSecure() ? 'secure' : 'insecure']">
                  <el-icon :size="20"><Lock /></el-icon>
                </div>
                <div class="security-info">
                  <span class="security-name">账户状态</span>
                  <span :class="['security-desc', isAccountSecure() ? 'secure' : 'insecure']">
                    {{ isAccountSecure() ? '安全' : '不安全' }}
                  </span>
                </div>
              </div>
            </div>
          </div>


        </div>
      </div>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog v-model="avatarDialogVisible" title="更换头像" width="400px">
      <el-upload
        class="avatar-uploader"
        action="#"
        :show-file-list="false"
        :before-upload="beforeAvatarUpload"
        :on-success="handleAvatarSuccess"
      >
        <img v-if="avatarImageUrl" :src="avatarImageUrl" class="avatar" />
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="avatarDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="uploadAvatar">确认上传</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 编辑资料对话框 -->
    <el-dialog
      v-model="editProfileDialogVisible"
      title="编辑资料"
      width="500px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input 
            v-model="editForm.bio" 
            type="textarea" 
            :rows="3" 
            placeholder="介绍一下自己吧～"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="0">未知</el-radio>
            <el-radio label="1">男</el-radio>
            <el-radio label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editProfileDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEditProfile">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useStore } from 'vuex' // 引入Vuex
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import request from '@/utils/request'
import { 
  Plus, 
  User, 
  Star, 
  ChatDotRound, 
  Folder, 
  Lock, 
  Message, 
  Bell, 
  Setting, 
  Edit, 
  Delete, 
  SwitchButton, 
  Trophy, 
  Timer, 
  DataLine, 
  Upload,
  ArrowRight,
  CircleCheck,
  Warning,
  Position,
  Phone,
  MessageFilled,
  Notification,
  Close,
  Connection,
  Wallet,
  View
} from '@element-plus/icons-vue'

const router = useRouter()
const store = useStore() // 初始化Vuex store

// 用户信息
const userInfo = ref({
  id: 1,
  username: '未加载',
  avatar: '',
  bio: '暂未加载',
  isVip: false,
  vipExpireTime: '2099-12-31',
  followCount: 0,
  fansCount: 0,
  postCount: 0,
  likeCount: 0,
  commentCount: 0,
  visitCount: 0,
  balance: 0,
  spamCommentCount: 0,
  isSpamUser: false,
  lastLoginTime: '未知',
  createTime: '未知',
  nickname: '未设置昵称',
  email: '未知',
  phone: '未知',
  status: 1,
  gender: 0
})

// 活动标签
const activeTab = ref('overview')

// 分类数据
const categories = ref([])

// 用户数据
const userPosts = ref([])
const userComments = ref([])
const followingList = ref([])
const followersList = ref([])
const userTopics = ref([])

// 充值相关
const rechargeAmounts = [10, 50, 100, 200, 500]
const selectedAmount = ref(100)
const customAmount = ref(null)

// 个人成就
const achievements = ref([
  {
    id: 1,
    name: '创作达人',
    description: '发布超过50篇帖子',
    icon: 'M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z',
    className: 'achievement-1'
  },
  {
    id: 2,
    name: '社区活跃',
    description: '连续30天活跃',
    icon: 'M12 8V4L8 6M12 8L16 6M12 8V12M12 12L16 14M12 12L8 14M12 12V16M4 12H2M22 12H20M12 20V22M12 2V4',
    className: 'achievement-2'
  },
  {
    id: 3,
    name: '优质创作者',
    description: '获得10篇精华帖子',
    icon: 'M5 3L19 12L5 21V3Z',
    className: 'achievement-3'
  }
])

// 最近活动
const recentActivities = ref([
  {
    id: 1,
    text: '发布了新帖子「Vue3.4最新特性解析」',
    time: '2小时前',
    icon: 'M12 19V5M5 12H19'
  },
  {
    id: 2,
    text: '收藏了「React性能优化指南」',
    time: '5小时前',
    icon: 'M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z'
  },
  {
    id: 3,
    text: '评论了「TypeScript最佳实践」',
    time: '昨天',
    icon: 'M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z'
  }
])





// 快速入口
const quickAccess = ref([
  {
    id: 1,
    name: '返回主页',
    icon: 'M12 15C13.6569 15 15 13.6569 15 12C15 10.3431 13.6569 9 12 9C10.3431 9 9 10.3431 9 12C9 13.6569 10.3431 15 12 15Z',
    path: '/home',
    className: 'access-1'
  },
  {
    id: 2,
    name: '发布帖子',
    icon: 'M12 5V19M5 12H19',
    path: '/post/create',
    className: 'access-2'
  },
  {
    id: 3,
    name: '消息中心',
    icon: 'M18 8C18 6.4087 17.3679 4.88258 16.2426 3.75736C15.1174 2.63214 13.5913 2 12 2C10.4087 2 8.88258 2.63214 7.75736 3.75736C6.63214 4.88258 6 6.4087 6 8C6 15 3 17 3 17H21C21 17 18 15 18 8Z',
    path: '/Notifications',
    className: 'access-3'
  }
])

// 数据统计
const dataStats = computed(() => {
  const stats = []
  
  // 帖子数
  if (userInfo.value.postCount !== undefined) {
    stats.push({
      id: 1,
      label: '帖子数',
      value: `${userInfo.value.postCount || 0}篇`,
      progress: Math.min(100, (userInfo.value.postCount || 0) * 10),
      className: 'stat-1'
    })
  }
  
  // 获赞数
  if (userInfo.value.likeCount !== undefined) {
    stats.push({
      id: 2,
      label: '获赞数',
      value: `${userInfo.value.likeCount || 0}个`,
      progress: Math.min(100, (userInfo.value.likeCount || 0) / 30),
      className: 'stat-2'
    })
  }
  
  // 评论数
  if (userInfo.value.commentCount !== undefined) {
    stats.push({
      id: 3,
      label: '评论数',
      value: `${userInfo.value.commentCount || 0}条`,
      progress: Math.min(100, (userInfo.value.commentCount || 0) * 5),
      className: 'stat-3'
    })
  }
  
  // 关注数
  if (userInfo.value.followCount !== undefined) {
    stats.push({
      id: 4,
      label: '关注数',
      value: `${userInfo.value.followCount || 0}人`,
      progress: Math.min(100, (userInfo.value.followCount || 0) * 2),
      className: 'stat-4'
    })
  }
  
  // 粉丝数
  if (userInfo.value.fansCount !== undefined) {
    stats.push({
      id: 5,
      label: '粉丝数',
      value: `${userInfo.value.fansCount || 0}人`,
      progress: Math.min(100, (userInfo.value.fansCount || 0) * 2),
      className: 'stat-5'
    })
  }
  
  // 访问数
  if (userInfo.value.visitCount !== undefined) {
    stats.push({
      id: 6,
      label: '访问数',
      value: `${userInfo.value.visitCount || 0}次`,
      progress: Math.min(100, (userInfo.value.visitCount || 0) / 100),
      className: 'stat-6'
    })
  }
  
  return stats
})



// 表单数据
const profileForm = reactive({
  nickname: '张三',
  bio: '前端开发工程师 | 热爱技术分享 | 喜欢探索新事物',
  gender: 'male',
  location: '北京'
})



// 头像上传相关
const avatarDialogVisible = ref(false)
const avatarImageUrl = ref('')

// 编辑资料相关
const editProfileDialogVisible = ref(false)
const editForm = reactive({
  nickname: '',
  bio: '',
  email: '',
  phone: '',
  gender: 0
})

// 加载状态
const loading = ref(false)
const error = ref(null)

// 计算属性
const vipRemainingDays = computed(() => {
  if (!userInfo.value?.vipExpireTime) return 0
  try {
    const expireDate = new Date(userInfo.value.vipExpireTime)
    const today = new Date()
    const totalDays = 365 // 假设会员有效期为1年
    const remainingDays = Math.max(0, Math.ceil((expireDate - today) / (1000 * 60 * 60 * 24)))
    return Math.min(100, Math.round((remainingDays / totalDays) * 100))
  } catch (error) {
    return 0
  }
})

// 方法
const handleTabSelect = (key) => {
  activeTab.value = key
}

const isUserFollowed = (userId) => {
  const currentUserId = store.getters.userId
  if (!currentUserId) return false
  
  const user = followingList.value.find(u => u.id === userId)
  return user !== undefined
}

const toggleFollow = async (userId) => {
  try {
    const isLogin = store.state.isLogin
    const currentUserId = store.getters.userId
    
    if (!isLogin || !currentUserId) {
      ElMessage.warning('请先登录后再关注用户')
      return
    }
    
    const isFollowed = isUserFollowed(userId)
    
    const res = await request.post(`/users/${userId}/follow`, null, {
      params: { currentUserId }
    })
    
    if (res.code === 200 || res.success) {
      if (isFollowed) {
        followingList.value = followingList.value.filter(u => u.id !== userId)
        ElMessage.success('取消关注成功')
      } else {
        const user = followersList.value.find(u => u.id === userId)
        if (user) {
          followingList.value.push({...user, following: true})
        }
        ElMessage.success('关注成功')
      }
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('关注操作失败:', error)
    ElMessage.error('关注操作失败')
  }
}

const appealComment = async (comment) => {
  try {
    await ElMessageBox.confirm('确定要申诉这条评论吗？申诉后将提交给管理员重新审核。', '申诉确认', {
      confirmButtonText: '确定申诉',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.post(`/comment/${comment.commentId}/appeal`)
    if (res.code === 200) {
      ElMessage.success('申诉提交成功，请等待管理员审核')
      comment.appealed = true
    } else {
      ElMessage.error(res.msg || '申诉提交失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申诉评论失败:', error)
      ElMessage.error('申诉评论失败')
    }
  }
}

// 编辑用户资料
const editProfile = () => {
  // 打开编辑资料对话框
  editProfileDialogVisible.value = true
  
  // 初始化表单数据
  editForm.value = {
    nickname: userInfo.value.nickname || '',
    bio: userInfo.value.bio || '',
    email: userInfo.value.email || '',
    phone: userInfo.value.phone || '',
    gender: userInfo.value.gender || 0
  }
}

const openAvatarUpload = () => {
  avatarDialogVisible.value = true
}

const createPost = () => {
  router.push('/post/create')
}

const editPost = (postId) => {
  router.push(`/post/edit/${postId}`)
}

const deletePost = async (postId) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const res = await request.delete(`/post/${postId}`)
    if (res.code === 200) {
      ElMessage.success('帖子删除成功')
      // 从列表中移除已删除的帖子
      userPosts.value = userPosts.value.filter(post => post.id !== postId)
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
      ElMessage.error('删除帖子失败')
    }
  }
}



const saveProfile = () => {
  ElMessage.success('个人资料保存成功')
}

// 保存编辑的用户资料
const saveEditProfile = async () => {
  try {
    // 构建请求数据
    const requestData = {
      id: userInfo.value.id,
      nickname: editForm.nickname,
      bio: editForm.bio,
      email: editForm.email,
      phone: editForm.phone,
      gender: editForm.gender
    }
    
    // 调用后端API更新用户信息
    // 这里需要根据实际的后端API路径进行调整
    const response = await axios.post('http://localhost:8088/api/user/update', requestData)
    
    if (response.data && response.data.code === 200) {
      // 更新成功，更新本地用户信息
      userInfo.value = {
        ...userInfo.value,
        ...requestData
      }
      
      // 更新Vuex store中的用户信息
      store.commit('UPDATE_USER_INFO', userInfo.value)
      
      // 关闭对话框
      editProfileDialogVisible.value = false
      
      ElMessage.success('用户资料更新成功')
    } else {
      ElMessage.error('用户资料更新失败')
    }
  } catch (error) {
    console.error('更新用户资料失败:', error)
    ElMessage.error('网络异常，请稍后重试')
  }
}



const navigateTo = (path) => {
  router.push(path)
}

const navigateToTopic = (topicId) => {
  router.push(`/topic/detail/${topicId}`)
}

const navigateToPost = (postId) => {
  router.push(`/post/detail/${postId}`)
}

const renewVip = () => {
  ElMessage.info('续费会员')
}

const openVip = () => {
  ElMessage.info('开通会员')
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const getVipTagType = (level) => {
  const types = ['', 'success', 'warning', 'danger']
  return types[level] || 'success'
}

const getVipText = (level) => {
  const texts = ['', 'VIP 1', 'VIP 2', 'VIP 3']
  return texts[level] || 'VIP'
}

// 获取性别文本
const getUserGenderText = (gender) => {
  switch (gender) {
    case 1: return '男'
    case 2: return '女'
    default: return '未知'
  }
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

// 判断是否为优质用户
const isQualityUser = () => {
  if (!userInfo.value) return false
  
  const spamCommentCount = userInfo.value.spamCommentCount || 0
  const commentCount = userInfo.value.commentCount || 0
  
  if (commentCount === 0) return false
  
  const spamRatio = spamCommentCount / commentCount
  return spamRatio < 0.25
}

const isAccountSecure = () => {
  if (!userInfo.value) return false
  
  const hasEmail = userInfo.value.email && 
                   userInfo.value.email !== '未知' && 
                   userInfo.value.email !== '未设置'
  const hasPhone = userInfo.value.phone && 
                   userInfo.value.phone !== '未知' && 
                   userInfo.value.phone !== '未设置'
  
  return hasEmail && hasPhone
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '未知'
  
  try {
    const date = new Date(dateTime)
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch (error) {
    return '格式错误'
  }
}

// 保存选中的文件
const selectedFile = ref(null)

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg'
  const isPNG = file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG && !isPNG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  
  // 保存文件
  selectedFile.value = file
  
  // 预览图片
  const reader = new FileReader()
  reader.readAsDataURL(file)
  reader.onload = () => {
    avatarImageUrl.value = reader.result
  }
  
  return false // 阻止自动上传
}

const handleAvatarSuccess = () => {
  // 上传成功处理
}

const uploadAvatar = async () => {
  if (!selectedFile.value) {
    ElMessage.error('请选择要上传的图片')
    return
  }
  
  try {
    // 创建FormData对象
    const formData = new FormData()
    formData.append('avatar', selectedFile.value)
    
    // 获取当前登录用户名
    const currentUser = store.state.userInfo
    let username = ''
    if (currentUser && currentUser.username) {
      username = currentUser.username
    } else if (userInfo.value.username) {
      username = userInfo.value.username
    } else {
      ElMessage.error('无法获取用户名信息')
      return
    }
    formData.append('username', username)
    
  
    const response = await axios.post('http://localhost:8088/api/upload/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    // 处理后端响应
    if (response.data && typeof response.data === 'string') {
      if (response.data.includes('成功')) {
        // 上传成功，构建头像URL
        const originalFilename = selectedFile.value.name
        const suffix = originalFilename.lastIndexOf('.') > 0 
          ? originalFilename.substring(originalFilename.lastIndexOf('.')) 
          : '.png'
        const avatarUrl = `http://localhost:8088/api/upload/${username}${suffix}`
        // 更新本地用户信息
        userInfo.value.avatar = avatarUrl
        // 更新Vuex store中的用户头像
        store.commit('UPDATE_USER_AVATAR', avatarUrl)
        console.log('已更新Vuex中的用户头像:', avatarUrl)
        ElMessage.success(response.data)
        avatarDialogVisible.value = false
      } else {
        ElMessage.error(response.data)
      }
    } else {
      ElMessage.error('头像更新失败，请稍后重试')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    ElMessage.error('上传头像失败，请稍后重试')
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async() => {
    // 清除用户信息
    // 核心：调用Vuex的logout action，清空状态（包括localStorage）
    await store.dispatch('logout')
    ElMessage.success('退出登录成功')
    // 跳转到登录页
    router.push('/home')
  }).catch(() => {
    // 用户取消
  })
}

onMounted(() => {
  // 加载用户数据
  loadUserInfo()
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    // 模拟后端响应
    const mockResponse = {
      code: 200,
      msg: 'success',
      data: {
        id: 1,
        username: '张三',
        avatar: '',
        bio: '前端开发工程师 | 热爱技术分享 | 喜欢探索新事物',
        vipLevel: 2,
        vipExpire: '2024-12-31',
        followingCount: 245,
        followersCount: 1234,
        postsCount: 56,
        likesCount: 2890,
        joinTime: '2023-01-15',
        location: '北京',
        email: 'zhangsan@example.com',
        phone: '138****5678'
      }
    }
    
    // 模拟获取其他数据
    const mockAchievementsResponse = {
      code: 200,
      data: [
        {
          id: 1,
          name: '注册会员',
          description: '完成注册成为会员',
          date: '2023-01-15',
          className: 'achievement-1'
        },
        {
          id: 2,
          name: '发布首帖',
          description: '发布了第一篇帖子',
          date: '2023-01-20',
          className: 'achievement-2'
        },
        {
          id: 3,
          name: '获得100赞',
          description: '帖子获得100个赞',
          date: '2023-02-05',
          className: 'achievement-3'
        }
      ]
    }
    
    const mockActivitiesResponse = {
      code: 200,
      data: [
        {
          id: 1,
          text: '发布了新帖子「Vue3.4最新特性解析」',
          time: '2小时前',
          icon: 'M12 19V5M5 12H19'
        },
        {
          id: 2,
          text: '收藏了「React性能优化指南」',
          time: '5小时前',
          icon: 'M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z'
        },
        {
          id: 3,
          text: '评论了「TypeScript最佳实践」',
          time: '昨天',
          icon: 'M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z'
        }
      ]
    }
    
    const mockPostsResponse = {
      code: 200,
      data: [
        {
          id: 1,
          title: 'Vue 3.4 最新特性深度解析与实战应用',
          excerpt: 'Vue 3.4带来了许多令人兴奋的新特性，包括性能优化、新的组合式API改进等...',
          category: '技术前沿',
          time: '2天前',
          likes: 128,
          comments: 45,
          views: 890
        },
        {
          id: 2,
          title: '前端工程化实战指南',
          excerpt: '从零开始搭建完整的前端工程化体系，包括构建、部署、监控等...',
          category: '学习资源',
          time: '1周前',
          likes: 96,
          comments: 32,
          views: 654
        }
      ]
    }
    
    const mockCollectionsResponse = {
      code: 200,
      data: [
        {
          id: 1,
          name: '前端学习',
          description: '收集优质的前端学习资源',
          count: 24
        },
        {
          id: 2,
          name: '设计灵感',
          description: '优秀的设计案例和灵感',
          count: 18
        }
      ]
    }
    
    const mockStatsResponse = {
      code: 200,
      data: [
        {
          id: 1,
          label: '本月发帖',
          value: '12篇',
          icon: 'M12 19V5M5 12H19',
          progress: 80,
          className: 'stat-1'
        },
        {
          id: 2,
          label: '互动次数',
          value: '456次',
          icon: 'M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H8L4 21V5C4 4.46957 4.21071 3.96086 4.58579 3.58579C4.96086 3.21071 5.46957 3 6 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z',
          progress: 60,
          className: 'stat-2'
        },
        {
          id: 3,
          label: '获赞数',
          value: '2890个',
          icon: 'M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z',
          progress: 90,
          className: 'stat-3'
        }
      ]
    }
    
    const mockNotificationsResponse = {
      code: 200,
      data: [
        {
          id: 1,
          text: '您的帖子「Vue3.4最新特性解析」获得10个新赞',
          time: '刚刚',
          icon: 'M20.84 4.61C20.3292 4.099 19.7228 3.69365 19.0554 3.41706C18.3879 3.14047 17.6725 2.99818 16.95 2.99818C16.2275 2.99818 15.5121 3.14047 14.8446 3.41706C14.1772 3.69365 13.5708 4.099 13.06 4.61L12 5.67L10.94 4.61C9.9083 3.57831 8.50903 2.99871 7.05 2.99871C5.59096 2.99871 4.19169 3.57831 3.16 4.61C2.1283 5.64169 1.54871 7.04097 1.54871 8.5C1.54871 9.95903 2.1283 11.3583 3.16 12.39L12 21.23L20.84 12.39C21.351 11.8792 21.7563 11.2728 22.0329 10.6054C22.3095 9.93789 22.4518 9.22249 22.4518 8.5C22.4518 7.77751 22.3095 7.06211 22.0329 6.39465C21.7563 5.72719 21.351 5.12083 20.84 4.61Z',
          read: false
        },
        {
          id: 2,
          text: '系统维护通知：今晚凌晨2-4点系统升级',
          time: '2小时前',
          icon: 'M13 10V3L4 14H11V21L20 10H13Z',
          read: true
        }
      ]
    }
    
    // 实际API调用（注释掉）
    /*
    const response = await fetch('http://localhost:8088/api/user/profile', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const data = await response.json()
    
    const achievementsResponse = await fetch('http://localhost:8088/api/user/achievements', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const achievementsData = await achievementsResponse.json()
    
    const activitiesResponse = await fetch('http://localhost:8088/api/user/activities', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const activitiesData = await activitiesResponse.json()
    
    const postsResponse = await fetch('http://localhost:8088/api/user/posts', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const postsData = await postsResponse.json()
    
    const collectionsResponse = await fetch('http://localhost:8088/api/user/collections', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const collectionsData = await collectionsResponse.json()
    
    const statsResponse = await fetch('http://localhost:8088/api/user/stats', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const statsData = await statsResponse.json()
    
    const notificationsResponse = await fetch('http://localhost:8088/api/user/notifications', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    const notificationsData = await notificationsResponse.json()
    */
    
    // 从Vuex store获取当前用户ID
    const currentUserId = store.getters.userId
    if (!currentUserId) {
      console.warn('未获取到当前用户ID')
      return
    }
    
    // 调用后端API获取用户信息
    const response = await request.get(`/user/${currentUserId}`)
    
    if (response && response.code === 200) {
      const userData = response.data
      
      // 更新用户信息
      userInfo.value = {
        id: userData.id || 1,
        username: userData.username || '未加载',
        avatar: userData.avatar || '',
        bio: userData.bio || '暂未设置个人简介',
        isVip: userData.isVip || false,
        vipExpireTime: userData.vipExpireTime || '2099-12-31',
        followCount: userData.followCount || 0,
        fansCount: userData.fansCount || 0,
        postCount: userData.postCount || 0,
        likeCount: userData.likeCount || 0,
        commentCount: userData.commentCount || 0,
        visitCount: userData.visitCount || 0,
        balance: userData.balance || 0,
        spamCommentCount: userData.spamCommentCount || 0,
        isSpamUser: userData.isSpamUser || false,
        lastLoginTime: userData.lastLoginTime || '未知',
        createTime: userData.createTime || '未知',
        nickname: userData.nickname || '未设置昵称',
        email: userData.email || '未知',
        phone: userData.phone || '未知',
        status: userData.status || 1,
        gender: userData.gender || 0
      }
      
      // 更新表单数据
      profileForm.nickname = userInfo.value.nickname || userInfo.value.username
      profileForm.bio = userInfo.value.bio || ''
      profileForm.location = userInfo.value.location || ''
      
      console.log('从后端API获取用户信息:', userInfo.value)
    } else {
      console.warn('获取用户信息失败:', response?.msg)
    }
    
    // 清空模拟数据
    achievements.value = []
    recentActivities.value = []
    userPosts.value = []
    
    // 获取用户帖子
    try {
      const postsResponse = await request.get(`/post/user/${currentUserId}`)
      if (postsResponse && postsResponse.code === 200) {
        // 转换帖子数据格式以匹配前端模板
        userPosts.value = postsResponse.data.map(post => {
          const category = categories.value.find(c => c.categoryId === post.categoryId)
          return {
            id: post.postId,
            title: post.postContent.substring(0, 50), // 使用帖子内容作为标题
            excerpt: post.postContent.substring(0, 100) + '...', // 截取内容作为摘要
            categoryName: category ? category.categoryName : '未分类',
            time: post.createTime ? formatDateTime(post.createTime) : '未知',
            likes: post.likeCount || 0,
            comments: post.commentCount || 0,
            views: post.viewCount || 0
          }
        })
      }
    } catch (error) {
      console.error('获取用户帖子错误：', error)
    }

    // 获取用户评论
    try {
      const commentsResponse = await request.get(`/comment/user/${currentUserId}`)
      if (commentsResponse && commentsResponse.code === 200) {
        userComments.value = commentsResponse.data
      }
    } catch (error) {
      console.error('获取用户评论错误：', error)
    }

    // 获取用户关注列表
    try {
      const followingResponse = await request.get(`/user/${currentUserId}/following`)
      if (followingResponse && followingResponse.code === 200) {
        followingList.value = followingResponse.data
      }
    } catch (error) {
      console.error('获取用户关注列表错误：', error)
    }

    // 获取用户粉丝列表
    try {
      const followersResponse = await request.get(`/user/${currentUserId}/followers`)
      if (followersResponse && followersResponse.code === 200) {
        followersList.value = followersResponse.data
      }
    } catch (error) {
      console.error('获取用户粉丝列表错误：', error)
    }

    // 获取用户话题列表
    try {
      const topicsResponse = await request.get(`/topic/user/${currentUserId}`)
      if (topicsResponse && topicsResponse.code === 200) {
        userTopics.value = topicsResponse.data
      }
    } catch (error) {
      console.error('获取用户话题列表错误：', error)
    }
    
    // 获取分类列表
    try {
      const categoriesResponse = await request.get('/topic/categories')
      if (categoriesResponse && categoriesResponse.data) {
        categories.value = categoriesResponse.data
      }
    } catch (error) {
      console.error('获取分类列表错误：', error)
    }
  } catch (error) {
    console.error('获取用户信息错误：', error)
    ElMessage.error('网络异常，请稍后重试')
  }
}

const handleRecharge = async () => {
  const amount = customAmount.value || selectedAmount.value
  if (!amount || amount <= 0) {
    ElMessage.warning('请输入有效的充值金额')
    return
  }
  
  try {
    const currentUserId = store.getters.userId
    if (!currentUserId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const res = await request.post('/user/recharge', {
      userId: currentUserId,
      amount: amount
    })
    
    if (res.code === 200) {
      ElMessage.success('充值成功')
      userInfo.value.balance = (userInfo.value.balance || 0) + amount
    } else {
      ElMessage.error(res.msg || '充值失败')
    }
  } catch (error) {
    console.error('充值失败:', error)
    ElMessage.error('充值失败，请稍后重试')
  }
}
</script>

<style scoped>
/* 个人中心主容器 */
.user-profile-container {
  min-height: 100vh;
  background-color: #f5f5f7;
}

/* VIP用户的金边效果 */
.user-profile-container.vip-user {
  position: relative;
  border: 2px solid transparent;
  border-radius: 8px;
  background-clip: padding-box;
}

.user-profile-container.vip-user::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #ffd700, #ffed4e, #ffd700);
  border-radius: 8px;
  z-index: -1;
  animation: vipGlow 3s ease-in-out infinite alternate;
}

@keyframes vipGlow {
  0% {
    box-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
  }
  100% {
    box-shadow: 0 0 20px rgba(255, 215, 0, 0.8), 0 0 30px rgba(255, 215, 0, 0.5);
  }
}

/* VIP用户的头像效果 */
.user-profile-container.vip-user .avatar-wrapper {
  position: relative;
}

.user-profile-container.vip-user .avatar-wrapper::after {
  content: '';
  position: absolute;
  top: -4px;
  left: -4px;
  right: -4px;
  bottom: -4px;
  border: 2px solid #ffd700;
  border-radius: 50%;
  animation: vipAvatarGlow 2s ease-in-out infinite alternate;
}

@keyframes vipAvatarGlow {
  0% {
    box-shadow: 0 0 5px rgba(255, 215, 0, 0.5);
  }
  100% {
    box-shadow: 0 0 15px rgba(255, 215, 0, 0.8);
  }
}

/* 用户相关数据样式 */
.user-related-data {
  margin: 20px 0;
}

.user-data-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.user-data-card {
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.user-data-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.card-subtitle {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.data-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.data-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.data-item:last-child {
  border-bottom: none;
}

.data-label {
  font-weight: 500;
  color: #666;
  width: 100px;
  flex-shrink: 0;
  font-size: 14px;
}

.data-value {
  flex: 1;
  color: #333;
  font-size: 14px;
  line-height: 1.4;
  font-weight: 400;
}

/* 快速入口侧边栏样式 */
.quick-access-sidebar {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px 0;
}

.quick-access-sidebar .access-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
}

.quick-access-sidebar .access-item:hover {
  background-color: #f5f5f5;
  transform: translateX(4px);
}

.quick-access-sidebar .access-icon {
  width: 32px;
  height: 32px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.quick-access-sidebar .access-name {
  font-weight: 500;
  color: #333;
}

/* 空数据提示 */
.empty-data {
  text-align: center;
  padding: 40px 0;
  color: #999;
  background-color: #ffffff;
  border-radius: 8px;
  margin: 20px 0;
  border: 1px solid #e5e5ea;
}

.empty-data p {
  margin: 0 0 16px 0;
  font-size: 16px;
}

.empty-data .el-button {
  margin-top: 8px;
}

/* 账户操作按钮 */
.account-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e5e5ea;
}

.account-actions .el-button {
  flex: 1;
  max-width: 200px;
}

/* 用户信息显示 */
.user-info-display {
  padding: 10px 0;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.info-label {
  font-weight: 500;
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  color: #333;
  line-height: 1.4;
}

/* 顶部信息卡片 */
.profile-header {
  background-color: white;
  border-bottom: 1px solid #e5e5ea;
  padding: 24px 0;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.user-basic-info {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.avatar-section {
  position: relative;
}

.avatar-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
}

.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-upload {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #007AFF;
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  z-index: 10;
}

.avatar-upload:hover {
  background-color: #0056CC;
  transform: scale(1.1);
}

.user-details {
  flex: 1;
}

.user-name-section {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-name {
  font-size: 28px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.vip-tag {
  font-weight: 600;
  letter-spacing: 0.5px;
}

.user-bio {
  font-size: 16px;
  color: #6e6e73;
  line-height: 1.6;
  margin: 0 0 20px 0;
  max-width: 600px;
}

.user-stats {
  display: flex;
  gap: 32px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
}

/* 关注和粉丝页面样式 */
.follow-container {
  margin-top: 20px;
}

.follow-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background-color: white;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.follow-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.follow-avatar {
  flex-shrink: 0;
}

.user-avatar-small {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.follow-info {
  flex: 1;
}

.follow-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.follow-bio {
  font-size: 14px;
  color: #666;
  margin: 0;
  line-height: 1.4;
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

/* 我的评论页面样式 */
.comments-container {
  margin-top: 20px;
}

.comment-item {
  padding: 16px;
  background-color: white;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.comment-item:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.comment-time {
  flex-shrink: 0;
}

.comment-likes {
  display: flex;
  align-items: center;
  gap: 4px;
}

.stat-number {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
}

.stat-label {
  font-size: 14px;
  color: #6e6e73;
  margin-top: 4px;
}

.user-meta {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #6e6e73;
}

.meta-item svg {
  color: #86868b;
}

/* 导航菜单 */
.profile-navigation {
  background-color: white;
  border-bottom: 1px solid #e5e5ea;
}

.balance-nav {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #f9f9f9;
}

.balance-label {
  font-size: 14px;
  color: #666;
}

.balance-amount {
  font-size: 16px;
  font-weight: 700;
  color: #ff6b6b;
}

.nav-menu {
  max-width: 1200px;
  margin: 0 auto;
  border: none;
}

/* 主要内容区域 */
.profile-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.main-content {
  display: flex;
  gap: 24px;
}

/* 左侧边栏 */
.left-sidebar {
  width: 280px;
  flex-shrink: 0;
}

/* 右侧边栏 */
.right-sidebar {
  width: 320px;
  flex-shrink: 0;
}

/* 中间内容区域 */
.center-content {
  flex: 1;
  min-width: 0;
}

/* 信息卡片 */
.info-card {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* 个人成就 */
.achievements {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background-color: #f5f5f7;
  transition: all 0.2s ease;
}

.achievement-item:hover {
  background-color: #e5e5ea;
  transform: translateY(-2px);
}

.achievement-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.achievement-icon.achievement-1 {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
  color: white;
}

.achievement-icon.achievement-2 {
  background: linear-gradient(135deg, #34C759 0%, #4CD964 100%);
  color: white;
}

.achievement-icon.achievement-3 {
  background: linear-gradient(135deg, #AF52DE 0%, #D858E8 100%);
  color: white;
}

.achievement-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.achievement-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
}

.achievement-desc {
  font-size: 12px;
  color: #6e6e73;
  margin-top: 2px;
}

/* 最近活动 */
.recent-activities {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background-color: #f5f5f7;
  transition: all 0.2s ease;
}

.activity-item:hover {
  background-color: #e5e5ea;
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 14px;
  color: #1d1d1f;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.activity-time {
  font-size: 12px;
  color: #6e6e73;
}

/* 帖子列表 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.post-item {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
}

.post-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

/* 话题列表样式 */
.topics-container {
  margin-top: 20px;
}

.topic-item {
  padding: 20px;
  background-color: white;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.2s ease;
}

.topic-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.topic-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.topic-title-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topic-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.hot-tag {
  padding: 2px 8px;
  background-color: #FF6B6B;
  color: white;
  font-size: 12px;
  font-weight: 500;
  border-radius: 4px;
  white-space: nowrap;
}

.topic-time {
  font-size: 12px;
  color: #999;
}

.topic-description {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.topic-stats {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #999;
}

.topic-stats .stat {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 充值页面样式 */
.recharge-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.balance-info {
  text-align: center;
  padding: 30px;
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 8px;
}

.balance-amount {
  font-size: 48px;
  font-weight: 700;
}

.recharge-options .amount-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.amount-item {
  padding: 16px;
  border: 2px solid #e5e5ea;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.amount-item:hover {
  border-color: #007AFF;
  background-color: #f0f8ff;
}

.amount-item.active {
  border-color: #007AFF;
  background-color: #e6f2ff;
}

.amount-value {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
}

.custom-amount {
  margin-top: 16px;
}

.recharge-actions {
  margin-top: 24px;
  text-align: center;
}

.recharge-actions .el-button {
  width: 200px;
}

.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.post-category {
  font-size: 12px;
  color: #007AFF;
  background-color: rgba(0, 122, 255, 0.1);
  padding: 4px 8px;
  border-radius: 8px;
  font-weight: 500;
}

.post-time {
  font-size: 12px;
  color: #6e6e73;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.post-excerpt {
  font-size: 14px;
  color: #6e6e73;
  line-height: 1.6;
  margin: 0 0 16px 0;
}

.post-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.post-stat {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #6e6e73;
}

.post-stat svg {
  color: #86868b;
}

.post-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 收藏夹 */
.collections-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.collection-folder {
  background-color: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  transition: all 0.2s ease;
  cursor: pointer;
}

.collection-folder:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.folder-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.folder-header svg {
  color: #FF9500;
}

.folder-name {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
  flex: 1;
}

.folder-count {
  font-size: 12px;
  color: #6e6e73;
  background-color: #f5f5f7;
  padding: 2px 8px;
  border-radius: 12px;
}

.folder-desc {
  font-size: 14px;
  color: #6e6e73;
  line-height: 1.5;
  margin: 0;
}

/* 账户设置 */
.account-settings {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.setting-card {
  border-radius: 16px;
  border: 1px solid #e5e5ea;
}

.security-items,
.privacy-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item,
.privacy-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f7;
}

.security-item:last-child,
.privacy-item:last-child {
  border-bottom: none;
}

.security-info {
  flex: 1;
}

.security-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.security-info h4 {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.security-info p {
  font-size: 12px;
  color: #6e6e73;
  margin: 0;
}

.security-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

.security-icon.secure {
  color: #34C759;
  background-color: rgba(52, 199, 89, 0.1);
}

.security-icon.insecure {
  color: #FF3B30;
  background-color: rgba(255, 59, 48, 0.1);
}

/* 退出登录 */
.logout-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  align-items: center;
  padding: 20px 0;
}

.logout-desc {
  font-size: 14px;
  color: #6e6e73;
  text-align: center;
  margin: 0;
}

.logout-icon {
  margin-right: 8px;
}

/* 概览内容 */
.overview-content {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.quick-access {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.access-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.access-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  background-color: #f5f5f7;
  cursor: pointer;
  transition: all 0.2s ease;
}

.access-item:hover {
  background-color: #e5e5ea;
  transform: translateY(-2px);
}

.access-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.access-icon.access-1 {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
  color: white;
}

.access-icon.access-2 {
  background: linear-gradient(135deg, #34C759 0%, #4CD964 100%);
  color: white;
}

.access-icon.access-3 {
  background: linear-gradient(135deg, #AF52DE 0%, #D858E8 100%);
  color: white;
}

.access-icon.access-4 {
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
  color: white;
}

.access-name {
  font-size: 14px;
  font-weight: 500;
  color: #1d1d1f;
}

/* 数据统计 */
.data-statistics {
  background-color: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 20px;
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 20px;
  border-radius: 12px;
  background-color: #f5f5f7;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.stat-1 {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
  color: white;
}

.stat-icon.stat-2 {
  background: linear-gradient(135deg, #34C759 0%, #4CD964 100%);
  color: white;
}

.stat-icon.stat-3 {
  background: linear-gradient(135deg, #AF52DE 0%, #D858E8 100%);
  color: white;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
}

.stat-label {
  font-size: 14px;
  color: #6e6e73;
  margin-top: 4px;
}

/* 会员状态 */
.vip-status {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.vip-badge {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-radius: 12px;
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
  color: white;
}

.vip-level {
  font-size: 16px;
  font-weight: 700;
}

.vip-expire {
  font-size: 12px;
  opacity: 0.9;
}

.vip-benefits {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

.vip-invite {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0;
}

.vip-desc {
  font-size: 14px;
  color: #6e6e73;
  margin: 8px 0 16px 0;
}

.vip-renew-btn {
  margin-top: 8px;
}

/* 安全状态 */
.security-status {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background-color: #f5f5f7;
  transition: all 0.2s ease;
}

.security-item:hover {
  background-color: #e5e5ea;
}

.security-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.security-info {
  flex: 1;
}

.security-name {
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  display: block;
  margin-bottom: 2px;
}

.security-desc {
  font-size: 12px;
  color: #6e6e73;
}

.security-desc.secure {
  color: #34C759;
  font-weight: 600;
}

.security-desc.insecure {
  color: #FF3B30;
  font-weight: 600;
}

/* 通知 */
.notifications {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  border-radius: 10px;
  background-color: #f5f5f7;
  transition: all 0.2s ease;
}

.notification-item:hover {
  background-color: #e5e5ea;
}

.notification-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background-color: rgba(0, 122, 255, 0.1);
  color: #007AFF;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.notification-content {
  flex: 1;
}

.notification-text {
  font-size: 14px;
  color: #1d1d1f;
  margin: 0 0 4px 0;
  line-height: 1.4;
}

.notification-time {
  font-size: 12px;
  color: #6e6e73;
}

/* 头像上传对话框 */
.avatar-uploader {
  display: flex;
  justify-content: center;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.2s ease;
  width: 120px;
  height: 120px;
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
  line-height: 120px;
}

.avatar-uploader :deep(.avatar) {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
  
  .left-sidebar,
  .right-sidebar {
    width: 100%;
  }
  
  .collections-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
  }
  
  .profile-main {
    padding: 16px;
  }
  
  .user-basic-info {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .user-stats {
    justify-content: center;
  }
  
  .user-meta {
    justify-content: center;
  }
  
  .access-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .nav-menu {
    overflow-x: auto;
    white-space: nowrap;
  }
}
</style>

<style scoped>
/* Element Plus 组件样式覆盖 - 使用scoped确保只影响当前组件 */
.user-profile-container :deep(.el-menu) {
  border-bottom: none;
}

.user-profile-container :deep(.el-menu--horizontal) {
  white-space: nowrap;
}

.user-profile-container :deep(.el-menu--horizontal .el-menu-item) {
  height: 48px;
  line-height: 48px;
  padding: 0 20px;
  font-weight: 500;
  color: #6e6e73;
}

.user-profile-container :deep(.el-menu--horizontal .el-menu-item.is-active) {
  color: #007AFF;
  border-bottom: 2px solid #007AFF;
}

.user-profile-container :deep(.el-menu--horizontal .el-menu-item:hover) {
  color: #007AFF;
  background-color: transparent;
}

.user-profile-container :deep(.el-card) {
  border-radius: 16px;
  border: 1px solid #e5e5ea;
}

.user-profile-container :deep(.el-card__header) {
  border-bottom: 1px solid #f5f5f7;
  padding: 20px;
}

.user-profile-container :deep(.el-card__body) {
  padding: 20px;
}

.user-profile-container :deep(.el-form-item) {
  margin-bottom: 20px;
}

.user-profile-container :deep(.el-button) {
  border-radius: 10px;
  font-weight: 500;
}

.user-profile-container :deep(.el-button--primary) {
  background-color: #007AFF;
  border-color: #007AFF;
}

.user-profile-container :deep(.el-button--primary:hover) {
  background-color: #0056CC;
  border-color: #0056CC;
}

.user-profile-container :deep(.el-progress__text) {
  font-size: 12px;
  color: #6e6e73;
}

.user-profile-container :deep(.el-tag) {
  border-radius: 8px;
  font-weight: 500;
}

.user-profile-container :deep(.el-dialog) {
  border-radius: 20px;
}

.user-profile-container :deep(.el-dialog__header) {
  border-bottom: 1px solid #e5e5ea;
  padding: 20px 24px;
}

.user-profile-container :deep(.el-dialog__body) {
  padding: 24px;
}

.user-profile-container :deep(.el-dialog__footer) {
  border-top: 1px solid #e5e5ea;
  padding: 20px 24px;
}
</style>