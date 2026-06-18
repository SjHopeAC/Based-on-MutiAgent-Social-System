<template>
  <div class="vip-payment-container">
    <div class="vip-header">
      <h1>会员中心</h1>
      <p>开通会员享受更多特权</p>
    </div>
    
    <div class="vip-status-card" v-if="userVipStatus">
      <div class="status-content">
        <div class="status-info">
          <div class="vip-badge">
            <span class="vip-level">VIP {{ userVipStatus.level }}</span>
            <span class="vip-expire">有效期至：{{ formatDate(userVipStatus.expireDate) }}</span>
          </div>
          <el-progress 
            :percentage="vipRemainingDays" 
            :stroke-width="10" 
            :show-text="false"
            class="vip-progress"
          />
          <div class="progress-labels">
            <span>{{ remainingDays }} 天后到期</span>
            <span>{{ formatDate(userVipStatus.expireDate) }}</span>
          </div>
        </div>
        <div class="status-actions">
          <el-button type="warning" @click="renewVip">立即续费</el-button>
        </div>
      </div>
    </div>
    
    <div class="vip-plans">
      <h2 class="section-title">会员套餐</h2>
      <p class="section-subtitle">选择适合您的会员套餐</p>
      
      <div class="plans-grid">
        <div 
          v-for="plan in vipPlans" 
          :key="plan.id"
          class="plan-card"
          :class="{ 'recommended': plan.recommended }"
        >
          <div class="plan-header">
            <h3 class="plan-name">{{ plan.name }}</h3>
            <div class="plan-price">
              <span class="price">¥{{ plan.price }}</span>
              <span class="period">/ {{ plan.period }}</span>
            </div>
            <p class="plan-desc">{{ plan.description }}</p>
          </div>
          
          <div class="plan-features">
            <div 
              v-for="feature in plan.features" 
              :key="feature"
              class="feature-item"
            >
              <el-icon><Check /></el-icon>
              <span>{{ feature }}</span>
            </div>
          </div>
          
          <div class="plan-actions">
            <el-button 
              v-if="plan.recommended" 
              type="primary" 
              size="large"
              @click="selectPlan(plan)"
            >
              立即开通
            </el-button>
            <el-button 
              v-else 
              size="large"
              @click="selectPlan(plan)"
            >
              选择套餐
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="vip-privileges">
      <h2 class="section-title">会员特权</h2>
      
      <div class="privileges-grid">
        <div 
          v-for="privilege in vipPrivileges" 
          :key="privilege.id"
          class="privilege-card"
        >
          <div class="privilege-icon" :class="privilege.className">
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path :d="privilege.icon" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h3>{{ privilege.title }}</h3>
          <p>{{ privilege.description }}</p>
        </div>
      </div>
    </div>
    
    <!-- 支付对话框 -->
    <el-dialog v-model="paymentDialogVisible" title="支付订单" width="500px">
      <div class="payment-dialog">
        <div class="order-info">
          <div class="order-item">
            <span>套餐名称</span>
            <span class="order-value">{{ selectedPlan?.name }}</span>
          </div>
          <div class="order-item">
            <span>套餐价格</span>
            <span class="order-value">¥{{ selectedPlan?.price }}</span>
          </div>
          <div class="order-item">
            <span>有效期</span>
            <span class="order-value">{{ selectedPlan?.period }}</span>
          </div>
          <div class="order-total">
            <span>应付金额</span>
            <span class="total-price">¥{{ selectedPlan?.price }}</span>
          </div>
        </div>
        
        <div class="payment-methods">
          <h4>选择支付方式</h4>
          <div class="methods-grid">
            <div 
              class="method-item selected"
            >
              <div class="method-icon">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 9V7C17 5.89543 16.1046 5 15 5H9C7.89543 5 7 5.89543 7 7V9M5 9H19C20.1046 9 21 9.89543 21 11V17C21 18.1046 20.1046 19 19 19H5C3.89543 19 3 18.1046 3 17V11C3 9.89543 3.89543 9 5 9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <span>余额支付</span>
            </div>
          </div>
        </div>
        
        <div class="payment-actions">
          <el-button @click="paymentDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment">确认支付</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useStore } from 'vuex'

const store = useStore()

// VIP状态
const userVipStatus = ref({
  level: 2,
  expireDate: '2024-12-31',
  joinDate: '2024-01-01'
})

// VIP套餐
const vipPlans = ref([
  {
    id: 1,
    name: '月度会员',
    price: '29.9',
    period: '30天',
    description: '适合短期体验',
    recommended: false,
    features: [
      '专属标识',
      '基础上传限制',
      '普通客服支持',
      '基础内容加速'
    ]
  },
  {
    id: 2,
    name: '季度会员',
    price: '79.9',
    period: '90天',
    description: '最具性价比',
    recommended: true,
    features: [
      '专属标识',
      '较大上传限制',
      '优先客服支持',
      '内容加速',
      '广告过滤',
      '会员专属内容'
    ]
  },
  {
    id: 3,
    name: '年度会员',
    price: '299.9',
    period: '365天',
    description: '长期使用更优惠',
    recommended: false,
    features: [
      '尊贵标识',
      '超大上传限制',
      '专属客服经理',
      '极速内容加速',
      '完全无广告',
      '会员专属内容',
      '专属活动特权',
      '生日特别礼物'
    ]
  }
])

// 会员特权
const vipPrivileges = ref([
  {
    id: 1,
    title: '专属标识',
    description: '在个人主页和评论中展示专属会员标识',
    icon: 'M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z',
    className: 'privilege-1'
  },
  {
    id: 2,
    title: '更大上传限制',
    description: '图片上传限制提升至50MB，视频上传限制提升至1GB',
    icon: 'M4 16L4 17C4 18.6569 5.34315 20 7 20L17 20C18.6569 20 20 18.6569 20 17L20 16M12 4L12 16M12 16L15 13M12 16L9 13',
    className: 'privilege-2'
  },
  {
    id: 3,
    title: '无广告体验',
    description: '享受纯净的浏览体验，无任何广告干扰',
    icon: 'M6 18L18 6M6 6L18 18',
    className: 'privilege-3'
  },
  {
    id: 4,
    title: '专属客服',
    description: '尊享VIP专属客服通道，快速响应解决问题',
    icon: 'M8 12H16M16 8H8M16 16H8M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z',
    className: 'privilege-4'
  }
])

// 对话框状态
const paymentDialogVisible = ref(false)
const selectedPlan = ref(null)

// 计算属性
const vipRemainingDays = computed(() => {
  // 计算剩余天数百分比
  const totalDays = 365 // 假设年度会员
  const usedDays = 100 // 假设已使用天数
  return Math.round((1 - usedDays / totalDays) * 100)
})

const remainingDays = computed(() => {
  // 计算剩余天数
  return 265 // 简化计算
})

// 方法
const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const renewVip = () => {
  selectedPlan.value = vipPlans.value[1] // 默认选择季度会员
  paymentDialogVisible.value = true
}

const selectPlan = (plan) => {
  selectedPlan.value = plan
  paymentDialogVisible.value = true
}

const confirmPayment = async () => {
  try {
    const userInfo = store.state.userInfo
    if (!userInfo || !userInfo.id) {
      ElMessage.error('请先登录')
      return
    }
    
    const price = parseFloat(selectedPlan.value.price)
    const period = selectedPlan.value.period
    
    // 调用后端API扣除余额
    const response = await request.post('/user/deduct', {
      userId: userInfo.id,
      amount: price
    })
    
    if (response.code === 200) {
      // 扣除余额成功后，更新VIP状态
      const vipResponse = await request.post('/user/updateVipStatus', {
        userId: userInfo.id,
        period: period
      })
      
      if (vipResponse.code === 200) {
        // 支付成功
        paymentDialogVisible.value = false
        ElMessage.success('支付成功！会员已开通')
        
        // 计算过期时间
        const expireDate = calculateExpireDate(selectedPlan.value.period)
        
        // 更新VIP状态
        userVipStatus.value.level = 1 // 简化为1，表示VIP
        userVipStatus.value.expireDate = expireDate
        
        // 更新用户余额
        if (userInfo.balance) {
          userInfo.balance -= price
        }
        
        // 更新用户信息中的VIP状态
        if (userInfo) {
          userInfo.isVip = true
          userInfo.vipExpireDate = expireDate
        }
      } else {
        ElMessage.error('更新VIP状态失败：' + (vipResponse.msg || '未知错误'))
      }
    } else {
      ElMessage.error(response.msg || '支付失败')
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error('支付失败，请稍后重试')
  }
}

// 根据套餐有效期计算过期时间
const calculateExpireDate = (period) => {
  const now = new Date()
  
  // 解析有效期
  if (period.includes('30天')) {
    now.setDate(now.getDate() + 30)
  } else if (period.includes('90天')) {
    now.setDate(now.getDate() + 90)
  } else if (period.includes('365天')) {
    now.setFullYear(now.getFullYear() + 1)
  }
  
  // 格式化日期为YYYY-MM-DD
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  
  return `${year}-${month}-${day}`
}
</script>

<style scoped>
.vip-payment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background-color: white;
  border-radius: 16px;
  min-height: calc(100vh - 200px);
}

.vip-header {
  text-align: center;
  margin-bottom: 32px;
}

.vip-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.vip-header p {
  font-size: 16px;
  color: #6e6e73;
}

.vip-status-card {
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 40px;
  color: white;
}

.status-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  flex: 1;
}

.vip-badge {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.vip-level {
  font-size: 28px;
  font-weight: 700;
}

.vip-expire {
  font-size: 14px;
  opacity: 0.9;
}

.vip-progress {
  margin-bottom: 8px;
}

.progress-labels {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  opacity: 0.9;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.section-subtitle {
  font-size: 16px;
  color: #6e6e73;
  margin-bottom: 32px;
}

.plans-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.plan-card {
  background-color: white;
  border: 2px solid #e5e5ea;
  border-radius: 16px;
  padding: 24px;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.plan-card.recommended {
  border-color: #007AFF;
  transform: translateY(-10px);
  box-shadow: 0 10px 30px rgba(0, 122, 255, 0.2);
  position: relative;
}

.plan-card.recommended::before {
  content: '推荐';
  position: absolute;
  top: -12px;
  right: 24px;
  background-color: #007AFF;
  color: white;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.plan-header {
  text-align: center;
  margin-bottom: 24px;
}

.plan-name {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin: 0 0 16px 0;
}

.plan-price {
  margin-bottom: 12px;
}

.price {
  font-size: 32px;
  font-weight: 700;
  color: #007AFF;
}

.period {
  font-size: 16px;
  color: #6e6e73;
}

.plan-desc {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

.plan-features {
  flex: 1;
  margin-bottom: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #1d1d1f;
}

.feature-item .el-icon {
  color: #34C759;
}

.plan-actions {
  text-align: center;
}

.plan-actions .el-button {
  width: 100%;
}

.privileges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.privilege-card {
  background-color: #f5f5f7;
  border-radius: 16px;
  padding: 24px;
  text-align: center;
  transition: all 0.2s ease;
}

.privilege-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
}

.privilege-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.privilege-icon.privilege-1 {
  background: linear-gradient(135deg, #007AFF 0%, #00B4FF 100%);
  color: white;
}

.privilege-icon.privilege-2 {
  background: linear-gradient(135deg, #34C759 0%, #4CD964 100%);
  color: white;
}

.privilege-icon.privilege-3 {
  background: linear-gradient(135deg, #AF52DE 0%, #D858E8 100%);
  color: white;
}

.privilege-icon.privilege-4 {
  background: linear-gradient(135deg, #FF9500 0%, #FFCC00 100%);
  color: white;
}

.privilege-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 8px 0;
}

.privilege-card p {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
  line-height: 1.5;
}

.payment-dialog {
  padding: 8px;
}

.order-info {
  background-color: #f5f5f7;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 24px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.order-value {
  font-weight: 600;
  color: #1d1d1f;
}

.order-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #e5e5ea;
  margin-top: 12px;
  font-size: 16px;
  font-weight: 600;
}

.total-price {
  font-size: 24px;
  color: #FF3B30;
}

.payment-methods {
  margin-bottom: 24px;
}

.payment-methods h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 16px 0;
}

.methods-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.method-item {
  border: 2px solid #e5e5ea;
  border-radius: 12px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.method-item:hover {
  border-color: #007AFF;
}

.method-item.selected {
  border-color: #007AFF;
  background-color: rgba(0, 122, 255, 0.05);
}

.method-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6e6e73;
}

.method-item.selected .method-icon {
  color: #007AFF;
}

.payment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.history-filters {
  display: flex;
  gap: 12px;
}

.amount-positive {
  color: #34C759;
  font-weight: 600;
}

.amount-negative {
  color: #FF3B30;
  font-weight: 600;
}
</style>