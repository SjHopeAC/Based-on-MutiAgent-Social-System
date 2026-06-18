<template>
  <div class="login-container">
    <!-- 复用登录页的背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 品牌标识区域（保留Logo的原始SVG） -->
      <div class="brand-section">
        <div class="brand-logo">
          <!-- 保留品牌Logo的原始SVG，不替换 -->
          <svg width="52" height="52" viewBox="0 0 52 52" fill="none" xmlns="http://www.w3.org/2000/svg">
            <rect width="52" height="52" rx="12" fill="url(#paint0_linear)"/>
            <path d="M20 16H32V20H20V16ZM20 24H30V28H20V24ZM34 24H32V28H34V24Z" fill="white"/>
            <defs>
              <linearGradient id="paint0_linear" x1="0" y1="0" x2="52" y2="52" gradientUnits="userSpaceOnUse">
                <stop stop-color="#007AFF"/>
                <stop offset="1" stop-color="#0056CC"/>
              </linearGradient>
            </defs>
          </svg>
        </div>
        <h1 class="brand-title">阔论</h1>
        <p class="brand-tagline">发现 · 分享 · 连接</p>
      </div>

      <!-- 注册卡片（匹配登录卡片风格） -->
      <div class="login-card">
        <div class="card-header">
          <h2>创建新账户</h2>
          <p>注册以开始使用阔论</p>
        </div>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          class="login-form"
          @submit.prevent="handleRegister"
        >
          <!-- 用户名输入（替换为Element Plus User图标） -->
          <div class="form-group">
            <label class="form-label">用户名</label>
            <div class="input-wrapper" :class="{ 'focused': isUsernameFocused, 'error': usernameError }">
              <el-icon class="input-icon">
                <User color="#8E8E93" />
              </el-icon>
              <input
                v-model="registerForm.username"
                type="text"
                placeholder="请设置用户名"
                class="clean-input"
                @focus="isUsernameFocused = true"
                @blur="isUsernameFocused = false"
                @keyup.enter="handleRegister"
              />
            </div>
            <div v-if="usernameError" class="error-message">{{ usernameError }}</div>
          </div>

          <!-- 邮箱输入（替换为Element Plus Message图标） -->
          <div class="form-group">
            <label class="form-label">邮箱</label>
            <div class="input-wrapper" :class="{ 'focused': isEmailFocused, 'error': emailError }">
              <el-icon class="input-icon">
                <Message color="#8E8E93" />
              </el-icon>
              <input
                v-model="registerForm.email"
                type="email"
                placeholder="请输入您的邮箱"
                class="clean-input"
                @focus="isEmailFocused = true"
                @blur="isEmailFocused = false"
                @keyup.enter="handleRegister"
              />
            </div>
            <div v-if="emailError" class="error-message">{{ emailError }}</div>
          </div>

          <!-- 密码输入（替换为Element Plus Lock图标 + View/Hide图标） -->
          <div class="form-group">
            <label class="form-label">密码</label>
            <div class="input-wrapper" :class="{ 'focused': isPasswordFocused, 'error': passwordError }">
              <el-icon class="input-icon">
                <Lock color="#8E8E93" />
              </el-icon>
              <input
                v-model="registerForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请设置密码（至少6位）"
                class="clean-input"
                @focus="isPasswordFocused = true"
                @blur="isPasswordFocused = false"
                @keyup.enter="handleRegister"
              />
              <button
                type="button"
                class="password-toggle"
                @click="showPassword = !showPassword"
                :aria-label="showPassword ? '隐藏密码' : '显示密码'"
              >
                <el-icon v-if="!showPassword">
                  <View color="#8E8E93" />
                </el-icon>
                <el-icon v-else>
                  <Hide color="#8E8E93" />
                </el-icon>
              </button>
            </div>
            <div v-if="passwordError" class="error-message">{{ passwordError }}</div>
          </div>

          <!-- 确认密码输入（替换为Element Plus Lock图标） -->
          <div class="form-group">
            <label class="form-label">确认密码</label>
            <div class="input-wrapper" :class="{ 'focused': isConfirmPwdFocused, 'error': confirmPwdError }">
              <el-icon class="input-icon">
                <Lock color="#8E8E93" />
              </el-icon>
              <input
                v-model="registerForm.confirmPassword"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请再次输入密码"
                class="clean-input"
                @focus="isConfirmPwdFocused = true"
                @blur="isConfirmPwdFocused = false"
                @keyup.enter="handleRegister"
              />
            </div>
            <div v-if="confirmPwdError" class="error-message">{{ confirmPwdError }}</div>
          </div>

          <!-- 注册按钮 -->
          <button
            type="submit"
            :class="['login-button', { 'loading': loading }]"
            @click="handleRegister"
            :disabled="loading"
          >
            <span v-if="!loading">注册</span>
            <div v-else class="spinner"></div>
          </button>

          <!-- 登录链接 -->
          <div class="register-link">
            <span>已有账户？</span>
            <a href="#" class="register-button" @click.prevent="handleLogin">
              立即登录
            </a>
          </div>
        </el-form>
      </div>

      <!-- 底部信息（复用登录页） -->
      <div class="footer">
        <div class="footer-links">
          <a href="#" class="footer-link">隐私政策</a>
          <span class="separator">·</span>
          <a href="#" class="footer-link">使用条款</a>
          <span class="separator">·</span>
          <a href="#" class="footer-link">联系我们</a>
        </div>
        <p class="copyright">© 2026 阔论话题广场</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
// 引入需要的Element Plus图标
import { User, Lock, View, Hide, Message } from '@element-plus/icons-vue'
import request from "@/utils/request";

export default {
  name: "RegisterView",
  // 注册Element Plus图标组件
  components: {
    User,
    Lock,
    View,
    Hide,
    Message
  },
  setup() {
    const router = useRouter();
    const registerFormRef = ref();
    const showPassword = ref(false);
    const isUsernameFocused = ref(false);
    const isEmailFocused = ref(false);
    const isPasswordFocused = ref(false);
    const isConfirmPwdFocused = ref(false);
    const usernameError = ref("");
    const emailError = ref("");
    const passwordError = ref("");
    const confirmPwdError = ref("");
    const loading = ref(false);

    // 注册表单数据
    const registerForm = reactive({
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
    });

    // 表单验证
    const validateForm = () => {
      let isValid = true;
      // 清空之前的错误提示
      usernameError.value = "";
      emailError.value = "";
      passwordError.value = "";
      confirmPwdError.value = "";

      // 验证用户名
      if (!registerForm.username.trim()) {
        usernameError.value = "请输入用户名";
        isValid = false;
      } else if (registerForm.username.length < 3) {
        usernameError.value = "用户名长度不能少于3位";
        isValid = false;
      }

      // 验证邮箱
      if (!registerForm.email.trim()) {
        emailError.value = "请输入邮箱";
        isValid = false;
      } else if (!/\S+@\S+\.\S+/.test(registerForm.email)) {
        emailError.value = "请输入有效的邮箱地址";
        isValid = false;
      }

      // 验证密码
      if (!registerForm.password) {
        passwordError.value = "请输入密码";
        isValid = false;
      } else if (registerForm.password.length < 6) {
        passwordError.value = "密码长度不能少于6位";
        isValid = false;
      }

      // 验证确认密码
      if (!registerForm.confirmPassword) {
        confirmPwdError.value = "请确认密码";
        isValid = false;
      } else if (registerForm.confirmPassword !== registerForm.password) {
        confirmPwdError.value = "两次输入的密码不一致";
        isValid = false;
      }

      return isValid;
    };

    // 处理注册
    const handleRegister = async () => {
      if (!validateForm()) {
        return;
      }

      try {
        loading.value = true;
        const response = await request.post('/register', registerForm);
      

        if (response.code === 200) {
          ElMessage.success(response.msg || "注册成功！即将返回登录页面");
          
          // 注册成功2秒后跳转到登录页
          setTimeout(() => {
            router.push('/login');
          }, 2000);
        } else {
          ElMessage.error(response.msg || "注册失败，请稍后重试");
        }
      } catch (error) {
        ElMessage.error("请求异常，请稍后重试");
        console.error("注册请求错误：", error);
      } finally {
        loading.value = false;
      }
    };

    // 跳转到登录页
    const handleLogin = () => {
      router.push('/login');
    };

    return {
      registerFormRef,
      registerForm,
      loading,
      showPassword,
      isUsernameFocused,
      isEmailFocused,
      isPasswordFocused,
      isConfirmPwdFocused,
      usernameError,
      emailError,
      passwordError,
      confirmPwdError,
      handleRegister,
      handleLogin,
    };
  },
};

</script>


<style>
/* Element Plus图标样式 */
.input-icon {
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

/* 确保Element Plus图标颜色生效 */
.el-icon {
  color: #8E8E93;
  font-size: 20px;
}

/* 全局重置样式（保留原有） */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

:root {
  --primary-blue: #007AFF;
  --primary-blue-light: rgba(0, 122, 255, 0.1);
  --background-color: #F5F5F7;
  --surface-color: #FFFFFF;
  --text-primary: #1D1D1F;
  --text-secondary: #8E8E93;
  --border-color: #E5E5EA;
  --border-color-focused: #007AFF;
  --shadow-color: rgba(0, 0, 0, 0.08);
  --error-color: #FF3B30;
  --success-color: #34C759;
}
</style>

<!-- 局部样式：仅作用于当前组件 -->
<style scoped>
.login-container {
  min-height: 100vh;
  background-color: var(--background-color);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(0, 122, 255, 0.05) 0%, rgba(0, 122, 255, 0.1) 100%);
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
}

.circle-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
}

.circle-3 {
  width: 150px;
  height: 150px;
  top: 50%;
  left: 10%;
}

.main-content {
  max-width: 400px;
  width: 100%;
  z-index: 1;
}

/* 品牌区域 */
.brand-section {
  text-align: center;
  margin-bottom: 48px;
}

.brand-logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.brand-title {
  font-size: 38px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.5px;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-tagline {
  font-size: 16px;
  color: var(--text-secondary);
  font-weight: 400;
  letter-spacing: 0.5px;
}

/* 注册卡片 */
.login-card {
  background: var(--surface-color);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px var(--shadow-color);
  margin-bottom: 32px;
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.card-header h2 {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.card-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 表单样式 */
.login-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.form-group {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1.5px solid var(--border-color);
  border-radius: 12px;
  padding: 14px 16px;
  transition: all 0.2s ease;
  background: var(--surface-color);
  height: 52px;
}

.input-wrapper.focused {
  border-color: var(--primary-blue);
  box-shadow: 0 0 0 3px var(--primary-blue-light);
}

.input-wrapper.error {
  border-color: var(--error-color);
}

.clean-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  color: var(--text-primary);
  background: transparent;
  line-height: 1.5;
  height: 100%;
  padding: 0;
}

.clean-input::placeholder {
  color: var(--text-secondary);
  font-size: 16px;
  opacity: 0.7;
}

.password-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  transition: color 0.2s ease;
  margin-left: 8px;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.password-toggle:hover {
  background-color: var(--primary-blue-light);
  color: var(--primary-blue);
}

.error-message {
  font-size: 12px;
  color: var(--error-color);
  margin-top: 4px;
  min-height: 16px;
}

/* 注册按钮 */
.login-button {
  background: linear-gradient(135deg, var(--primary-blue) 0%, #0056CC 100%);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 16px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 8px;
  width: 100%;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 122, 255, 0.3);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-button.loading {
  background: linear-gradient(135deg, var(--primary-blue) 0%, #0056CC 100%);
  opacity: 0.9;
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 登录链接 */
.register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--text-secondary);
  font-size: 15px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
  gap: 8px;
}

.register-button {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 600;
  transition: opacity 0.2s ease;
}

.register-button:hover {
  opacity: 0.8;
}

/* 页脚 */
.footer {
  text-align: center;
}

.footer-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.footer-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 13px;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: var(--primary-blue);
}

.separator {
  color: var(--text-secondary);
  font-size: 13px;
}

.copyright {
  color: var(--text-secondary);
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 16px;
  }
  
  .login-card {
    padding: 32px 24px;
  }
  
  .brand-title {
    font-size: 32px;
  }
  
  .input-wrapper {
    padding: 12px 14px;
    height: 48px;
  }
}

@media (max-width: 375px) {
  .brand-section {
    margin-bottom: 32px;
  }
  
  .login-card {
    padding: 24px 20px;
  }
}
</style>