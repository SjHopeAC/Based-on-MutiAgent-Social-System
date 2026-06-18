<template>
  <footer class="forum-footer">
    <div class="footer-content">
      <div class="footer-section">
        <h3 class="footer-title">阔论</h3>
        <p class="footer-description">深度对话·思想碰撞的社区平台</p>
        <div class="social-links">
          <a href="#" class="social-link">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 2H15C13.6739 2 12.4021 2.52678 11.4645 3.46447C10.5268 4.40215 10 5.67392 10 7V10H7V14H10V22H14V14H17L18 10H14V7C14 6.73478 14.1054 6.48043 14.2929 6.29289C14.4804 6.10536 14.7348 6 15 6H18V2Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </a>
          <a href="#" class="social-link">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M23 3C22.0424 3.67548 20.9821 4.19211 19.86 4.53C19.2577 3.83751 18.4573 3.34669 17.567 3.12393C16.6767 2.90116 15.7395 2.9572 14.8821 3.28445C14.0247 3.61171 13.2884 4.1944 12.773 4.95372C12.2575 5.71303 11.9877 6.61234 12 7.53V8.53C10.2426 8.57557 8.50127 8.18581 6.93101 7.39545C5.36074 6.60508 4.01032 5.43864 3 4C3 4 -1 13 8 17C5.94053 18.398 3.48716 19.0989 1 19C10 24 21 19 21 7.5C20.9991 7.22145 20.9723 6.94359 20.92 6.67C21.9406 5.66349 22.6608 4.39271 23 3Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </a>
          <a href="#" class="social-link">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 8C17.5913 8 19.1174 8.63214 20.2426 9.75736C21.3679 10.8826 22 12.4087 22 14V21H18V14C18 13.4696 17.7893 12.9609 17.4142 12.5858C17.0391 12.2107 16.5304 12 16 12C15.4696 12 14.9609 12.2107 14.5858 12.5858C14.2107 12.9609 14 13.4696 14 14V21H10V14C10 12.4087 10.6321 10.8826 11.7574 9.75736C12.8826 8.63214 14.4087 8 16 8Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M6 9H2V21H6V9Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M4 6C5.10457 6 6 5.10457 6 4C6 2.89543 5.10457 2 4 2C2.89543 2 2 2.89543 2 4C2 5.10457 2.89543 6 4 6Z" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </a>
        </div>
      </div>
      
      <div class="footer-section">
        <h4 class="footer-subtitle">产品</h4>
        <ul class="footer-links">
          <li><a href="#" class="footer-link">功能特性</a></li>
          <li><a href="#" class="footer-link">更新日志</a></li>
          <li><a href="#" class="footer-link">API文档</a></li>
        </ul>
      </div>
      
      <div class="footer-section">
        <h4 class="footer-subtitle">帮助</h4>
        <ul class="footer-links">
          <li><a href="#" class="footer-link">帮助中心</a></li>
          <li><a href="#" class="footer-link">社区指南</a></li>
          <li><a href="#" class="footer-link">服务条款</a></li>
        </ul>
      </div>
      
      <div class="footer-section">
        <h4 class="footer-subtitle">关于</h4>
        <ul class="footer-links">
          <li><a href="#" class="footer-link">关于我们</a></li>
          <li><a href="#" class="footer-link">加入我们</a></li>
          <li><a href="#" class="footer-link">联系我们</a></li>
        </ul>
      </div>
    </div>
    
    <div class="footer-bottom">
      <p class="copyright">© 2026 阔论社区. 保留所有权利.</p>
      <div class="footer-links-bottom">
        <a href="#" class="footer-link">隐私政策</a>
        <span class="separator">|</span>
        <a href="#" class="footer-link">服务条款</a>
        <span class="separator">|</span>
        <a href="#" class="footer-link">Cookie政策</a>
      </div>
    </div>
  </footer>
</template>

<script setup>
// 这里可以添加组件逻辑
</script>

<style scoped>
.forum-footer {
  background-color: white;
  border-top: 1px solid #e5e5ea;
  margin-top: 60px;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 48px 32px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

.footer-title {
  font-size: 20px;
  font-weight: 700;
  color: #1d1d1f;
  margin-bottom: 16px;
}

.footer-description {
  font-size: 14px;
  color: #6e6e73;
  margin-bottom: 24px;
  line-height: 1.5;
}

.social-links {
  display: flex;
  gap: 16px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f5f5f7;
  color: #6e6e73;
  transition: all 0.2s ease;
}

.social-link:hover {
  background-color: #007AFF;
  color: white;
}

.footer-subtitle {
  font-size: 16px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 20px;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-link {
  font-size: 14px;
  color: #6e6e73;
  text-decoration: none;
  transition: all 0.2s ease;
}

.footer-link:hover {
  color: #007AFF;
}

.footer-bottom {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px 32px;
  border-top: 1px solid #e5e5ea;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.copyright {
  font-size: 14px;
  color: #6e6e73;
}

.footer-links-bottom {
  display: flex;
  align-items: center;
  gap: 16px;
}

.separator {
  color: #e5e5ea;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    padding: 32px 16px;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
}
</style>