import { createStore } from 'vuex'

export default createStore({
  state: {
    // 初始化：优先从本地存储读取，没有则为null
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
    isLogin: !!localStorage.getItem('token'), // 布尔值：是否登录（有token则为true）
    // 话题点赞状态管理
    topicLikes: {}
  },

  mutations: {
    // 登录成功：更新Vuex + 本地存储
    SET_LOGIN_STATE(state, { token, userInfo }) {
      // 更新Vuex状态（响应式）
      state.token = token
      state.userInfo = userInfo
      state.isLogin = true
      // 持久化到本地存储（localStorage永久存储，sessionStorage仅会话有效）
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(userInfo)) // 对象要转字符串
    },

    // 更新用户头像
    UPDATE_USER_AVATAR(state, avatarUrl) {
      if (state.userInfo) {
        state.userInfo.avatar = avatarUrl
        // 持久化到本地存储
        localStorage.setItem('userInfo', JSON.stringify(state.userInfo))
      }
    },

    // 登出：清空Vuex + 本地存储
    CLEAR_LOGIN_STATE(state) {
      state.token = ''
      state.userInfo = null
      state.isLogin = false
      state.topicLikes = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    },

    // 更新话题点赞状态
    UPDATE_TOPIC_LIKE(state, { topicId, isLiked, likeCount }) {
      if (!state.topicLikes[topicId]) {
        state.topicLikes[topicId] = {}
      }
      state.topicLikes[topicId].isLiked = isLiked
      state.topicLikes[topicId].likeCount = likeCount
    },

    // 批量更新话题点赞状态
    BATCH_UPDATE_TOPIC_LIKES(state, topicLikes) {
      state.topicLikes = { ...state.topicLikes, ...topicLikes }
    }
  },

  actions: {
    // 登录请求（毕设中替换为真实接口）  组件里通过 store.dispatch('login', 传参) 调用
    async login({ commit }, loginForm) {
      try {
        // 登录接口请求
        const res = await fetch('http://localhost:8088/api/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(loginForm)
        })
        const data = await res.json()

        // 登录成功：调用mutation更新状态
        if (res.ok && data.code === 200) {
          commit('SET_LOGIN_STATE', {
            token: data.token, // 接口返回的令牌
            userInfo: data.userInfo, // 接口返回的用户信息（用户名/ID等）
          })
          console.log(data.userInfo)
          return true // 告诉组件登录成功
        } else {
          alert(data.message || '登录失败')
          return false
        }
      } catch (error) {
        console.error('登录请求失败：', error)
        alert('网络异常，请稍后重试')
        return false
      }
    },

    //  登出操作
    logout({ commit }) {
      commit('CLEAR_LOGIN_STATE') // 只调用mutation清空状态
    },

    // 更新话题点赞状态
    updateTopicLike({ commit }, { topicId, isLiked, likeCount }) {
      commit('UPDATE_TOPIC_LIKE', { topicId, isLiked, likeCount })
    },

    // 批量更新话题点赞状态
    batchUpdateTopicLikes({ commit }, topicLikes) {
      commit('BATCH_UPDATE_TOPIC_LIKES', topicLikes)
    }
  },
  getters: {
    // 便捷获取用户信息
    userName: (state) => state.userInfo?.username || '匿名用户',
    nickName: (state) => state.userInfo?.nickname || state.userInfo?.username || '匿名用户',
    userId: (state) => state.userInfo?.id || '',
    // 获取话题点赞状态
    getTopicLikeStatus: (state) => (topicId) => {
      return state.topicLikes[topicId] || { isLiked: false, likeCount: 0 }
    }
  }
})