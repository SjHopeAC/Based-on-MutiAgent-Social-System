<template>
  <div class="login-container">
    <!-- 简洁的背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 品牌标识区域（保留原始Logo SVG） -->
      <div class="brand-section">
        <div class="brand-logo">
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

      <!-- 登录卡片 -->
      <div class="login-card">
        <div class="card-header">
          <h2>欢迎回来</h2>
          <p>登录以继续使用阔论</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <!-- 用户名输入-->
          <div class="form-group">
            <label class="form-label">用户名</label>
            <div class="input-wrapper" :class="{ 'focused': isUsernameFocused }">
              <el-icon class="input-icon">
                <User color="#8E8E93" />
              </el-icon>
              <input
                v-model="loginForm.username"
                type="text"
                placeholder="请输入用户名"
                class="clean-input"
                @focus="isUsernameFocused = true"
                @blur="isUsernameFocused = false"
                @keyup.enter="handleLogin"
              />
            </div>
            <div v-if="usernameError" class="error-message">{{ usernameError }}</div>
          </div>

          <!-- 密码输入（替换为Element Plus Lock + View/Hide图标） -->
          <div class="form-group">
            <div class="form-label-row">
              <label class="form-label">密码</label>
            </div>
            <div class="input-wrapper" :class="{ 'focused': isPasswordFocused, 'error': passwordError }">
              <el-icon class="input-icon">
                <Lock color="#8E8E93" />
              </el-icon>
              <input
                v-model="loginForm.password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                class="clean-input"
                @focus="isPasswordFocused = true"
                @blur="isPasswordFocused = false"
                @keyup.enter="handleLogin"
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

          <!-- 记住我选项 -->
          <div class="remember-me">
            <label class="checkbox-container">
              <input 
                type="checkbox" 
                v-model="loginForm.remember"
                class="checkbox-input"
              >
              <span class="checkbox-custom"></span>
              <span class="checkbox-label">记住登录状态</span>
            </label>
          </div>

          <!-- 登录按钮 -->
          <button
            type="submit"
            :class="['login-button', { 'loading': loading }]"
            @click="handleLogin"
            :disabled="loading"
          >
            <span v-if="!loading">登录</span>
            <div v-else class="spinner"></div>
          </button>

          <!-- 注册链接 -->
          <div class="register-link">
            <span>还没有账户？</span>
            <a href="#" class="register-button" @click.prevent="handleRegister">
              创建账户
            </a>
            <a href="#" class="forgot-link" @click.prevent="handleForgotPassword">
              忘记密码？
            </a>
          </div>
        </el-form>
      </div>

      <!-- 底部信息 -->
      <div class="footer">
        <div class="footer-links">
          <a href="#" class="footer-link">隐私政策</a>
          <span class="separator">·</span>
          <a href="#" class="footer-link">使用条款</a>
          <span class="separator">·</span>
          <a href="#" class="footer-link">联系我们</a>
        </div>
        <p class="copyright">© 2024 阔论话题广场</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import { ElMessage } from "element-plus";
// 引入Element Plus图标
import { User, Lock, View, Hide } from '@element-plus/icons-vue'
import request from "@/utils/request"; // 测试时取消注释，发布时根据实际情况处理

export default {
  name: "LoginView",
  // 注册Element Plus图标组件
  components: {
    User,
    Lock,
    View,
    Hide
  },
  setup() {
    const router = useRouter();
    const store = useStore();
    const loginFormRef = ref();
    const showPassword = ref(false);
    const isUsernameFocused = ref(false);
    const isPasswordFocused = ref(false);
    const usernameError = ref("");
    const passwordError = ref("");

    // 登录表单数据
    const loginForm = reactive({
      username: "",
      password: "",
      remember: false,
    });

    // 加载状态
    const loading = ref(false);

    // 表单验证
    const validateForm = () => {
      let isValid = true;
      usernameError.value = "";
      passwordError.value = "";

      // 验证用户名/邮箱
      if (!loginForm.username.trim()) {
        usernameError.value = "请输入邮箱或用户名";
        isValid = false;
      } else if (!/\S+@\S+\.\S+/.test(loginForm.username) && loginForm.username.length < 3) {
        usernameError.value = "请输入有效的邮箱或用户名";
        isValid = false;
      }

      // 验证密码
      if (!loginForm.password) {
        passwordError.value = "请输入密码";
        isValid = false;
      } else if (loginForm.password.length < 6) {
        passwordError.value = "密码长度不能少于6位";
        isValid = false;
      }

      return isValid;
    };

    // 处理登录
    const handleLogin = async () => {
      if (!validateForm()) {
        return;
      }
      try {
        loading.value = true;
        const response = await request.post('/login', {
          username: loginForm.username,
          password: loginForm.password
        });
        
    
        console.log("登录返回的完整数据：", response);

        if (response.code === 200) {
          store.commit('SET_LOGIN_STATE', {
            token: response.data.token,
            userInfo: response.data.userInfo
          });
          ElMessage.success("登录成功！欢迎来到阔论");
          router.push('/home');
        } else {
          ElMessage.error(response.msg || "登录失败，请检查您的账号和密码");
        }
      } catch (error) {
        ElMessage.error("请求异常，请稍后重试");
        console.error("登录请求错误：", error);
      } finally {
        loading.value = false;
      }
    };

    // 其他操作
    const handleForgotPassword = () => {
      router.push('/ForgotPassword');
    };

    const handleRegister = () => {
      router.push('/register');
    };

    return {
      loginFormRef,
      loginForm,
      loading,
      showPassword,
      isUsernameFocused,
      isPasswordFocused,
      usernameError,
      passwordError,
      handleLogin,
      handleForgotPassword,
      handleRegister,
    };
  },
};
</script>

<!-- 全局样式：处理Element Plus图标和通用样式 -->
<style>
/* 全局样式重置 */
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

/* Element Plus图标样式适配 */
.input-icon {
  font-size: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}

.el-icon {
  color: #8E8E93;
  font-size: 20px;
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

/* 登录卡片 */
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

.form-label-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* 记住我选项 */
.remember-me {
  margin-top: 8px;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-secondary);
  user-select: none;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-custom {
  position: relative;
  height: 20px;
  width: 20px;
  background-color: var(--surface-color);
  border: 1.5px solid var(--border-color);
  border-radius: 6px;
  margin-right: 10px;
  transition: all 0.2s ease;
}

.checkbox-input:checked ~ .checkbox-custom {
  background-color: var(--primary-blue);
  border-color: var(--primary-blue);
}

.checkbox-input:checked ~ .checkbox-custom:after {
  content: '';
  position: absolute;
  left: 6px;
  top: 2px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

.checkbox-label {
  font-size: 14px;
  color: var(--text-secondary);
}

/* 登录按钮 */
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

.forgot-link {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: opacity 0.2s ease;
}

.forgot-link:hover {
  opacity: 0.8;
}

/* 注册链接 */
.register-link {
  display: flex;
  justify-content: center;
  align-items: center;
  color: var(--text-secondary);
  font-size: 15px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--border-color);
  gap: 16px;
}

.register-button {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 600;
  transition: opacity 0.2s ease;
}

.forgot-link {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 500;
  font-size: 14px;
  transition: opacity 0.2s ease;
}

.forgot-link:hover {
  opacity: 0.8;
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
  
  .form-label-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>