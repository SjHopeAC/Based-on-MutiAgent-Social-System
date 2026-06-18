// src/mock/index.js
import Mock from 'mockjs'

// 模拟登录接口：POST /api/login
// Mock.mock('/login', 'post', (req) => {
//   // 解析前端传的用户名/密码
//   const { username, password } = JSON.parse(req.body)
  
//   // 模拟场景1：登录成功（admin/123456）
//   if (username === 'admin' && password === '123456') {
//     return {
//       code: 200,
//       msg: '登录成功',
//       data: {
//         token: "success123", // 生成随机token
//         userInfo: {
//           id: 1,
//           username: 'admin',
//           nickname: '管理员'
//         }
//       }
//     }
//   }
//   // 模拟场景2：登录失败
//   else {
//     return {
//       code: 400,
//       msg: '用户名或密码错误',
//       data: null
//     }
//   }
// })

// // 模拟注册接口：POST /api/register
// Mock.mock('/register', 'post', () => {
//   return {
//     code: 200,
//     msg: '注册成功',
//     data: null
//   }
// })

export default Mock