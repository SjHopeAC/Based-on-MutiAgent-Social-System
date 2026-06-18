
<template>
  <div id="app">
    <!-- 只在非登录页面显示Header -->
    <Header v-if="!isAuthPage"/>
    
    <!-- 路由内容 -->
    <router-view />
    
    <!-- 只在非登录页面显示Footer -->
    <Footer v-if="!isAuthPage" />
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Header from './components/Header.vue'
import Footer from './components/Footer.vue'


export default {
  name: "App",
  data: function () {
    return {
    };
  },
  setup() {
    // 1. 必须在setup内调用useRoute，才能获取当前路由
    const route = useRoute()
    console.log(route.path)
    // 2. 修正判断逻辑：排除登录/注册/忘记密码页和管理后台，其他页面显示页眉页脚
    const isAuthPage = computed(() => {
      // 需要隐藏页眉页脚的路径列表
      const authPaths = ['/login','/register','/ForgotPassword']
      // 检查是否是认证页面或管理后台
      return authPaths.includes(route.path) || route.path.startsWith('/admin')
    })

    return {
      isAuthPage // 暴露给模板使用
    }
  },
  components: {
    Header,
    Footer
  },
};
</script>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  background-color: #f5f5f7;
  color: #1d1d1f;
  line-height: 1.5;
}

/* 通用样式 */
a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
  border: none;
  background: none;
  font-family: inherit;
}

ul {
  list-style: none;
}
</style>
