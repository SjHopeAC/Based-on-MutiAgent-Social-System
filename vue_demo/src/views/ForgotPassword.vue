<template>
  <div class="forget-password-container">
    <div class="forget-password-wrapper">
      <!-- 返回按钮 -->
      <div class="back-button">
        <el-button text @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回登录
        </el-button>
      </div>

      <!-- 主要内容 -->
      <div class="forget-content">
        <!-- 步骤指示器 -->
        <div class="step-indicator">
          <el-steps :active="currentStep">
            <el-step title="验证身份" />
            <el-step title="重置密码" />
            <el-step title="完成" />
          </el-steps>
        </div>

        <!-- 步骤1：验证身份 -->
        <div v-if="currentStep === 1" class="step-content">
          <div class="step-header">
            <h2>验证身份</h2>
            <p>请选择验证方式以重置密码</p>
          </div>

          <!-- 验证方式选择 -->
          <div class="verify-methods">
            <div 
              class="method-card" 
              :class="{ 'active': verifyMethod === 'phone' }"
              @click="verifyMethod = 'phone'"
            >
              <div class="method-icon">
                <el-icon><Iphone /></el-icon>
              </div>
              <div class="method-info">
                <h3>手机验证</h3>
                <p>通过绑定的手机号验证</p>
              </div>
            </div>

            <div 
              class="method-card" 
              :class="{ 'active': verifyMethod === 'email' }"
              @click="verifyMethod = 'email'"
            >
              <div class="method-icon">
                <el-icon><Message /></el-icon>
              </div>
              <div class="method-info">
                <h3>邮箱验证</h3>
                <p>通过绑定的邮箱验证</p>
              </div>
            </div>
          </div>

          <!-- 验证表单 -->
          <div class="verify-form">
            <el-form 
              ref="verifyFormRef" 
              :model="verifyForm" 
              :rules="verifyRules"
              label-position="top"
            >
              <!-- 账号输入 -->
              <el-form-item label="账号" prop="username">
                <el-input 
                  v-model="verifyForm.username" 
                  placeholder="请输入您的账号" 
                  size="large"
                  :prefix-icon="User"
                />
              </el-form-item>

              <!-- 手机验证 -->
              <div v-if="verifyMethod === 'phone'">
                <el-form-item label="手机号" prop="phone">
                  <el-input 
                    v-model="verifyForm.phone" 
                    placeholder="请输入绑定的手机号" 
                    size="large"
                    :prefix-icon="Phone"
                    maxlength="11"
                  />
                </el-form-item>
              </div>

              <!-- 邮箱验证 -->
              <div v-else>
                <el-form-item label="邮箱" prop="email">
                  <el-input 
                    v-model="verifyForm.email" 
                    placeholder="请输入绑定的邮箱地址" 
                    size="large"
                    :prefix-icon="Message"
                  />
                </el-form-item>
              </div>
            </el-form>
          </div>

          <div class="step-actions">
            <el-button 
              type="primary" 
              size="large" 
              @click="verifyIdentity"
              class="next-button"
              :loading="loading"
            >
              验证身份
            </el-button>
          </div>
        </div>

        <!-- 步骤2：重置密码 -->
        <div v-else-if="currentStep === 2" class="step-content">
          <div class="step-header">
            <h2>设置新密码</h2>
            <p>请设置您的新密码</p>
          </div>

          <div class="reset-form">
            <el-form 
              ref="passwordFormRef" 
              :model="passwordForm" 
              label-position="top"
              :rules="passwordRules"
            >
              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password"
                  placeholder="请输入新密码（6-20位字符）" 
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                />
                <!-- 密码强度显示条 -->
                <div class="password-strength">
                  <el-progress 
                    :percentage="passwordStrength * 20" 
                    :status="getStrengthStatus(passwordStrength)"
                    :color="getStrengthColor(passwordStrength)"
                    stroke-width="8"
                    class="strength-progress"
                  />
                  <div class="strength-info">
                    <span class="strength-text">{{ passwordStrengthText }}</span>
                    <span class="strength-desc">{{ getStrengthDescription(passwordStrength) }}</span>
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password"
                  placeholder="请再次输入新密码" 
                  size="large"
                  :prefix-icon="Lock"
                  show-password
                />
              </el-form-item>
            </el-form>
          </div>

          <div class="step-actions">
            <el-button 
              size="large" 
              @click="prevStep"
              class="prev-button"
            >
              上一步
            </el-button>
            <el-button 
              type="primary" 
              size="large" 
              @click="resetPassword"
              class="next-button"
              :loading="loading"
            >
              重置密码
            </el-button>
          </div>
        </div>

        <!-- 步骤3：完成 -->
        <div v-else class="step-content">
          <div class="success-content">
            <div class="success-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <h2>密码重置成功</h2>
            <p>您的密码已成功重置，请使用新密码登录</p>
            
            <div class="countdown-info">
              <el-icon><Clock /></el-icon>
              <span>{{ countdownSeconds }}秒后自动跳转到登录页面</span>
            </div>

            <div class="success-actions">
              <el-button 
                type="primary" 
                size="large" 
                @click="goToLogin"
                class="login-button"
              >
                立即登录
              </el-button>
              <el-button 
                size="large" 
                @click="goToHome"
                class="home-button"
              >
                返回首页
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft,
  Iphone,
  Message,
  Phone,
  User,
  Lock,
  CircleCheck,
  Clock
} from '@element-plus/icons-vue'
import request from "@/utils/request";

const router = useRouter()


// 当前步骤
const currentStep = ref(1)

// 验证方式
const verifyMethod = ref('phone')

// 加载状态
const loading = ref(false)

// 倒计时
const countdownSeconds = ref(5)
let redirectTimer = null

// 表单引用
const verifyFormRef = ref()
const passwordFormRef = ref()

// 验证表单
const verifyForm = reactive({
  username: '',
  phone: '',
  email: ''
})

// 存储验证成功返回的userId
const verifyUserId = ref(''); 

// 密码表单
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const verifyRules = reactive({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
})

const passwordRules = reactive({
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6到20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 密码强度计算
const passwordStrength = computed(() => {
  const password = passwordForm.newPassword
  if (!password) return 0
  
  let strength = 0
  if (password.length >= 6) strength++
  if (/[A-Z]/.test(password)) strength++
  if (/[a-z]/.test(password)) strength++
  if (/[0-9]/.test(password)) strength++
  if (/[^A-Za-z0-9]/.test(password)) strength++
  
  return Math.min(strength, 5)
})

const passwordStrengthClass = computed(() => {
  const strength = passwordStrength.value
  if (strength <= 2) return 'strength-weak'
  if (strength <= 3) return 'strength-medium'
  return 'strength-strong'
})

const passwordStrengthText = computed(() => {
  const strength = passwordStrength.value
  if (strength === 0) return '未设置密码'
  if (strength <= 2) return '弱'
  if (strength <= 3) return '中'
  return '强'
})

// 获取强度状态
const getStrengthStatus = (strength) => {
  if (strength <= 2) return 'exception'
  if (strength <= 3) return 'warning'
  return 'success'
}

// 获取强度颜色
const getStrengthColor = (strength) => {
  if (strength <= 2) return '#FF3B30'
  if (strength <= 3) return '#FF9500'
  return '#34C759'
}

// 获取强度描述
const getStrengthDescription = (strength) => {
  if (strength === 0) return '请设置密码'
  if (strength === 1) return '密码长度至少6位'
  if (strength === 2) return '建议包含大小写字母'
  if (strength === 3) return '建议包含数字'
  if (strength === 4) return '建议包含特殊字符'
  return '密码强度良好'
}

// 验证身份
const verifyIdentity = async () => {
  try {
    loading.value = true
    // 验证表单
    await verifyFormRef.value.validate()
    
    // 实际API调用
    const response = await request.post('/forgot-password/verify', {
      username: verifyForm.username,
      phone: verifyMethod.value === 'phone' ? verifyForm.phone : '',
      email: verifyMethod.value === 'email' ? verifyForm.email : ''
    })
    
  
    console.log('验证身份响应：', response)
    
    if (response.code === 200) {
      ElMessage.success('身份验证成功')
      verifyUserId.value = response.data; 
      currentStep.value = 2
    } else {
      ElMessage.error(response.msg || '验证失败，请检查账号和绑定信息')
    }
  } catch (error) {
    ElMessage.error('请求异常，请稍后重试')
    console.error('验证身份错误：', error)
  } finally {
    loading.value = false
  }
}

// 上一步
const prevStep = () => {
  currentStep.value = 1
}

// 重置密码
const resetPassword = async () => {
  try {
    loading.value = true
    // 验证密码表单
    await passwordFormRef.value.validate()
    
    
    const response = await request.post('/forgot-password/reset', {
      userId: verifyUserId.value, // 传验证步骤保存的userId
      newPassword: passwordForm.newPassword
    })
    
    
    console.log('重置密码响应：', response)
    
    if (response.code === 200) {
      ElMessage.success('密码重置成功')
      // 进入完成步骤
      currentStep.value = 3
      // 开始自动跳转倒计时
      startRedirectCountdown()
    } else {
      ElMessage.error(response.msg || '密码重置失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.warning('请正确填写密码信息')
  } finally {
    loading.value = false
  }
}

// 开始自动跳转倒计时
const startRedirectCountdown = () => {
  countdownSeconds.value = 5
  
  redirectTimer = setInterval(() => {
    if (countdownSeconds.value > 0) {
      countdownSeconds.value--
    } else {
      clearInterval(redirectTimer)
      goToLogin()
    }
  }, 1000)
}

// 返回登录页面
const goBack = () => {
  router.push('/login')
}

// 跳转到登录页面
const goToLogin = () => {
  router.push('/login')
}

// 跳转到首页
const goToHome = () => {
  router.push('/')
}

// 清理计时器
onUnmounted(() => {
  if (redirectTimer) {
    clearInterval(redirectTimer)
  }
})
</script>

<style scoped>
.forget-password-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8f0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.forget-password-wrapper {
  width: 100%;
  max-width: 480px;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.back-button {
  padding: 20px 30px;
  border-bottom: 1px solid #f0f0f0;
}

.forget-content {
  padding: 40px 50px;
}

/* 步骤指示器 */
.step-indicator {
  margin-bottom: 40px;
}

/* 步骤头部 */
.step-header {
  text-align: center;
  margin-bottom: 30px;
}

.step-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.step-header p {
  font-size: 14px;
  color: #6e6e73;
  margin: 0;
}

/* 验证方式选择 */
.verify-methods {
  display: flex;
  gap: 16px;
  margin-bottom: 30px;
}

.method-card {
  flex: 1;
  border: 2px solid #e5e5ea;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.method-card:hover {
  border-color: #007AFF;
  background-color: #f8f9ff;
}

.method-card.active {
  border-color: #007AFF;
  background-color: rgba(0, 122, 255, 0.05);
}

.method-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background-color: #f5f5f7;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #86868b;
  transition: all 0.3s ease;
}

.method-card:hover .method-icon {
  color: #007AFF;
}

.method-card.active .method-icon {
  background-color: #007AFF;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
}

.method-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin: 0 0 4px 0;
}

.method-info p {
  font-size: 12px;
  color: #6e6e73;
  margin: 0;
}

/* 验证表单 */
.verify-form {
  margin-bottom: 30px;
}

/* 重置表单 */
.reset-form {
  margin-bottom: 30px;
}

.password-strength {
  margin-top: 12px;
}

.strength-progress {
  margin-bottom: 8px;
}

.strength-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.strength-text {
  font-size: 14px;
  font-weight: 500;
}

.strength-text {
  color: #6e6e73;
}

.strength-desc {
  font-size: 12px;
  color: #86868b;
}

/* 步骤操作按钮 */
.step-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 40px;
}

.prev-button,
.next-button,
.login-button {
  min-width: 140px;
}

/* 成功页面 */
.success-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: rgba(52, 199, 89, 0.1);
  color: #34C759;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.success-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 12px;
}

.success-content p {
  font-size: 14px;
  color: #6e6e73;
  margin-bottom: 30px;
}

.countdown-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 30px;
  padding: 12px;
  background-color: #f5f5f7;
  border-radius: 8px;
  color: #6e6e73;
  font-size: 14px;
}

.countdown-info .el-icon {
  color: #007AFF;
}

.success-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .forget-password-wrapper {
    margin: 20px;
  }
  
  .forget-content {
    padding: 30px;
  }
  
  .verify-methods {
    flex-direction: column;
  }
  
  .success-actions {
    flex-direction: column;
  }
  
  .prev-button,
  .next-button,
  .login-button {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .forget-content {
    padding: 20px;
  }
  
  .step-actions {
    flex-direction: column;
  }
}
</style>

<style>
/* 覆盖element-plus样式 */
.forget-password-container .el-steps {
  background-color: transparent;
}

.forget-password-container .el-step__title {
  font-size: 14px;
  font-weight: 500;
  color: #6e6e73;
}

.forget-password-container .el-step.is-process .el-step__title {
  color: #007AFF;
  font-weight: 600;
}

.forget-password-container .el-step.is-finish .el-step__title {
  color: #34C759;
  font-weight: 600;
}

.forget-password-container .el-step__icon {
  width: 30px;
  height: 30px;
  font-size: 14px;
}

.forget-password-container .el-step__icon-inner {
  font-size: 14px;
  font-weight: 600;
}

/* 完成状态的步骤样式 */
.forget-password-container .el-step.is-finish .el-step__head {
  color: #34C759;
  border-color: #34C759;
}

.forget-password-container .el-step.is-finish .el-step__line {
  border-color: #34C759;
}

/* 进行中状态的步骤样式 */
.forget-password-container .el-step.is-process .el-step__head {
  color: #007AFF;
  border-color: #007AFF;
}

.forget-password-container .el-form-item__label {
  font-weight: 500;
  color: #1d1d1f;
  padding-bottom: 8px;
}

.forget-password-container .el-input__wrapper {
  border-radius: 10px;
  padding: 0 16px;
}

.forget-password-container .el-input__prefix-inner {
  color: #86868b;
}

.forget-password-container .el-button {
  border-radius: 10px;
  font-weight: 500;
}

.forget-password-container .el-button--primary {
  background-color: #007AFF;
  border-color: #007AFF;
}

.forget-password-container .el-button--primary:hover {
  background-color: #0056CC;
  border-color: #0056CC;
}

.forget-password-container .el-button--large {
  padding: 12px 24px;
  font-size: 16px;
}
</style>