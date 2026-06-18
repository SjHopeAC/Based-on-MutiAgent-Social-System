// src/utils/request.js 基础封装（适配Mock和真实后端）
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8088/api', // 后端API地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 响应拦截器：统一处理返回结果
request.interceptors.response.use(
  (res) => {
    // 直接返回后端/Mock的原始数据，方便前端解析
    return res.data
  },
  (error) => {
    ElMessage.error('请求失败：' + error.message)
    return Promise.reject(error)
  }
)

export default request