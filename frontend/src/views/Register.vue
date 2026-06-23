<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-header">
        <h1>🐾 账号注册</h1>
        <p>请选择您的角色类型</p>
      </div>
      <div class="role-selection">
        <el-radio-group v-model="registerForm.role" class="role-group">
          <el-radio-button value="owner" class="role-btn">
            <span class="role-icon">🎓</span>
            <span class="role-text">寄养者（学生）</span>
          </el-radio-button>
          <el-radio-button value="caregiver" class="role-btn">
            <span class="role-icon">🏠</span>
            <span class="role-text">被寄养者（居民）</span>
          </el-radio-button>
        </el-radio-group>
      </div>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" class="register-form">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" prefix-icon="Phone" />
        </el-form-item>
        <template v-if="registerForm.role === 'owner'">
          <el-form-item prop="studentNo">
            <el-input v-model="registerForm.studentNo" placeholder="请输入学号" prefix-icon="Document" />
          </el-form-item>
          <el-form-item prop="school">
            <el-input v-model="registerForm.school" placeholder="请输入学校名称" />
          </el-form-item>
          <el-form-item prop="major">
            <el-input v-model="registerForm.major" placeholder="请输入专业" />
          </el-form-item>
          <el-form-item prop="dorm">
            <el-input v-model="registerForm.dorm" placeholder="请输入宿舍地址" />
          </el-form-item>
        </template>
        <template v-if="registerForm.role === 'caregiver'">
          <el-form-item prop="name">
            <el-input v-model="registerForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item prop="address">
            <el-input v-model="registerForm.address" placeholder="请输入家庭住址" />
          </el-form-item>
          <el-form-item prop="distance">
            <el-input-number v-model="registerForm.distance" placeholder="距离学校(公里)" :min="0" :max="50" />
          </el-form-item>
          <el-form-item prop="careTypes">
            <el-select v-model="registerForm.careTypes" placeholder="可照顾的宠物类型" multiple>
              <el-option label="猫" value="cat" />
              <el-option label="狗" value="dog" />
              <el-option label="仓鼠" value="hamster" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" class="register-btn">注 册</el-button>
        </el-form-item>
        <el-form-item class="login-link">
          <span>已有账号？</span>
          <el-link @click="goToLogin">立即登录</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref()
const registerForm = reactive({
  role: '',
  username: '',
  password: '',
  phone: '',
  studentNo: '',
  school: '',
  major: '',
  dorm: '',
  name: '',
  address: '',
  distance: 0,
  careTypes: [] as string[]
})

const rules = {
  role: [{ required: true, message: '请选择角色类型', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  school: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  address: [{ required: true, message: '请输入家庭住址', trigger: 'blur' }],
  careTypes: [{ required: true, message: '请选择可照顾的宠物类型', trigger: 'change' }]
}

const handleRegister = () => {
  registerFormRef.value?.validate((valid: boolean) => {
    if (valid) {
      localStorage.setItem('token', 'mock-token')
      localStorage.setItem('role', registerForm.role)
      ElMessage.success('注册成功')
      router.push('/dashboard')
    }
  })
}

const goToLogin = () => {
  router.push('/')
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-wrapper {
  width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.register-header p {
  font-size: 14px;
  color: #909399;
}

.role-selection {
  margin-bottom: 30px;
}

.role-group {
  display: flex;
  justify-content: center;
}

.role-btn {
  width: 45%;
  height: 60px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.role-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.role-text {
  font-size: 14px;
}

.register-form {
  width: 100%;
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-link {
  text-align: center;
  margin-top: 10px;
}

.login-link span {
  color: #909399;
  font-size: 14px;
}
</style>
