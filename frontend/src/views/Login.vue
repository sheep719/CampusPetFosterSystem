<template>
  <div class="login-container">
    <div class="paw-bg">
      <div class="paw paw1">🐾</div>
      <div class="paw paw2">🐾</div>
      <div class="paw paw3">🐾</div>
      <div class="paw paw4">🐾</div>
      <div class="paw paw5">🐾</div>
      <div class="paw paw6">🐾</div>
      <div class="paw paw7">🐾</div>
      <div class="paw paw8">🐾</div>
      <div class="pet pet-cat1">🐱</div>
      <div class="pet pet-dog1">🐶</div>
      <div class="pet pet-cat2">😺</div>
      <div class="pet pet-dog2">🐕</div>
      <div class="pet pet-cat3">😸</div>
      <div class="pet pet-dog3">🦮</div>
    </div>
    <div class="login-wrapper">
      <div class="login-header">
        <div class="logo-icon">🐾</div>
        <h1>校园宠物临时寄养管理系统</h1>
        <p>Student Pet Foster Management System</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn">登 录</el-button>
        </el-form-item>
        <el-form-item class="register-link">
          <span>还没有账号？</span>
          <el-link @click="goToRegister">立即注册</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref()
const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  loginFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      const success = await userStore.handleLogin(loginForm.username, loginForm.password)
      if (success) {
        ElMessage.success('登录成功')
        const role = userStore.role || localStorage.getItem('role') || ''
        if (role === 'admin') {
          router.push('/dashboard')
        } else if (role === 'owner') {
          router.push('/owner-dashboard')
        } else if (role === 'caregiver') {
          router.push('/caregiver-dashboard')
        } else {
          router.push('/dashboard')
        }
      } else {
        ElMessage.error('用户名或密码错误')
      }
    }
  })
}

const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 50%, #ff9a9e 100%);
  position: relative;
  overflow: hidden;
}

.paw-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.paw {
  position: absolute;
  font-size: 40px;
  opacity: 0.15;
  animation: float 6s ease-in-out infinite;
}

.paw1 { top: 10%; left: 5%; animation-delay: 0s; font-size: 50px; }
.paw2 { top: 20%; right: 8%; animation-delay: 1s; font-size: 35px; }
.paw3 { top: 40%; left: 3%; animation-delay: 2s; font-size: 45px; }
.paw4 { top: 60%; right: 5%; animation-delay: 3s; font-size: 55px; }
.paw5 { top: 75%; left: 8%; animation-delay: 4s; font-size: 38px; }
.paw6 { top: 85%; right: 10%; animation-delay: 5s; font-size: 42px; }
.paw7 { top: 30%; left: 15%; animation-delay: 2.5s; font-size: 30px; }
.paw8 { top: 55%; right: 15%; animation-delay: 1.5s; font-size: 48px; }

.pet {
  position: absolute;
  font-size: 80px;
  opacity: 0.25;
  animation: float 8s ease-in-out infinite;
}

.pet-cat1 { top: 8%; left: 12%; animation-delay: 0.5s; font-size: 100px; }
.pet-dog1 { top: 12%; right: 12%; animation-delay: 1.5s; font-size: 90px; }
.pet-cat2 { top: 35%; left: 5%; animation-delay: 2.5s; font-size: 70px; }
.pet-dog2 { top: 45%; right: 6%; animation-delay: 3.5s; font-size: 85px; }
.pet-cat3 { top: 68%; left: 10%; animation-delay: 4.5s; font-size: 75px; }
.pet-dog3 { top: 72%; right: 8%; animation-delay: 5.5s; font-size: 95px; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.login-wrapper {
  width: 420px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
  border: 2px solid rgba(255, 255, 255, 0.5);
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-icon {
  font-size: 60px;
  margin-bottom: 15px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.login-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
  background: linear-gradient(135deg, #ff9a9e, #fecfef);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-header p {
  font-size: 14px;
  color: #909399;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border: none;
  border-radius: 22px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 154, 158, 0.4);
}

.register-link {
  text-align: center;
  margin-top: 10px;
}

.register-link span {
  color: #909399;
  font-size: 14px;
}
</style>