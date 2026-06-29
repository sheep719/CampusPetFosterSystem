<template>
  <div class="owner-dashboard">
    <div class="welcome-banner">
      <div class="banner-left">
        <h2>你好，{{ username }} 👋</h2>
        <p>欢迎回到校园宠物临时寄养管理系统</p>
        <p class="banner-tip">今天也要好好照顾你的毛孩子哦~ 🐾</p>
      </div>
      <div class="banner-right">
        <div class="banner-pet">🐱</div>
        <div class="banner-pet">🐶</div>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon pet-icon">🐾</div>
        <div class="stat-info">
          <p class="stat-value">{{ petCount }}</p>
          <p class="stat-label">我的宠物</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon foster-icon">🏠</div>
        <div class="stat-info">
          <p class="stat-value">{{ fosteringCount }}</p>
          <p class="stat-label">寄养中</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon app-icon">📝</div>
        <div class="stat-info">
          <p class="stat-value">{{ applicationCount }}</p>
          <p class="stat-label">我的申请</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon pending-icon">⏳</div>
        <div class="stat-info">
          <p class="stat-value">{{ pendingCount }}</p>
          <p class="stat-label">待审核</p>
        </div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="content-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">🐱 我的宠物</span>
            <el-button type="primary" link @click="goToMyPets">查看全部</el-button>
          </div>
        </template>
        <div class="pet-list">
          <div class="pet-item" v-for="pet in myPets" :key="pet.id">
            <div class="pet-avatar">{{ pet.avatar }}</div>
            <div class="pet-info">
              <p class="pet-name">{{ pet.name }}</p>
              <p class="pet-desc">{{ pet.breed }} · {{ pet.age }}岁</p>
            </div>
            <el-tag :type="pet.status === 'fostering' ? 'success' : 'info'" size="small">
              {{ pet.status === 'fostering' ? '寄养中' : '在家' }}
            </el-tag>
          </div>
          <el-empty v-if="myPets.length === 0" description="暂无宠物，快去添加吧~" />
        </div>
      </el-card>

      <el-card class="content-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">📋 最近申请</span>
            <el-button type="primary" link @click="goToApplications">查看全部</el-button>
          </div>
        </template>
        <div class="application-list">
          <div class="application-item" v-for="app in recentApplications" :key="app.id">
            <div class="app-icon">📦</div>
            <div class="app-info">
              <p class="app-title">{{ app.petName }} - {{ app.caregiverName }}</p>
              <p class="app-time">{{ app.time }}</p>
            </div>
            <el-tag :type="getStatusType(app.status)" size="small">
              {{ getStatusText(app.status) }}
            </el-tag>
          </div>
          <el-empty v-if="recentApplications.length === 0" description="暂无申请记录" />
        </div>
      </el-card>
    </div>

    <el-card class="caregiver-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">🏆 推荐被寄养者</span>
          <el-button type="primary" link @click="goToCaregivers">查看更多</el-button>
        </div>
      </template>
      <div class="caregiver-list">
        <div class="caregiver-item" v-for="cg in recommendedCaregivers" :key="cg.id">
          <el-avatar :size="60" class="cg-avatar">{{ cg.avatar }}</el-avatar>
          <div class="cg-info">
            <div class="cg-name-row">
              <span class="cg-name">{{ cg.name }}</span>
              <el-rate v-model="cg.rating" disabled :size="14" />
            </div>
            <p class="cg-desc">{{ cg.distance }} · 已寄养 {{ cg.count }} 次</p>
            <p class="cg-skill">擅长：{{ cg.skills }}</p>
          </div>
          <el-button type="primary" size="small" round>查看详情</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref(userStore.username || localStorage.getItem('username') || '用户')

const petCount = ref(3)
const fosteringCount = ref(1)
const applicationCount = ref(5)
const pendingCount = ref(2)

const myPets = ref([
  { id: 1, name: '小白', breed: '橘猫', age: 2, avatar: '🐱', status: 'fostering' },
  { id: 2, name: '旺财', breed: '金毛', age: 3, avatar: '🐕', status: 'home' },
  { id: 3, name: '咪咪', breed: '布偶猫', age: 1, avatar: '😺', status: 'home' }
])

const recentApplications = ref([
  { id: 1, petName: '小白', caregiverName: '李阿姨', time: '2024-01-15', status: 'approved' },
  { id: 2, petName: '旺财', caregiverName: '张奶奶', time: '2024-01-10', status: 'pending' },
  { id: 3, petName: '咪咪', caregiverName: '王叔叔', time: '2024-01-05', status: 'rejected' }
])

const recommendedCaregivers = ref([
  { id: 1, name: '李阿姨', avatar: '👩', rating: 4.8, distance: '距离学校1.5km', count: 12, skills: '猫咪、小型犬' },
  { id: 2, name: '张奶奶', avatar: '👵', rating: 4.9, distance: '距离学校2.0km', count: 25, skills: '各种狗狗' },
  { id: 3, name: '王叔叔', avatar: '👨', rating: 4.7, distance: '距离学校1.0km', count: 8, skills: '猫咪、兔子' }
])

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    completed: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    completed: '已完成'
  }
  return map[status] || status
}

const goToMyPets = () => {
  router.push('/owner-pets')
}

const goToApplications = () => {
  router.push('/owner-applications')
}

const goToCaregivers = () => {
  router.push('/owner-caregivers')
}
</script>

<style scoped>
.owner-dashboard {
  padding: 0;
}

.welcome-banner {
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 50%, #ff9a9e 100%);
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.banner-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.banner-left p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}

.banner-tip {
  margin-top: 12px !important;
  font-style: italic;
}

.banner-right {
  display: flex;
  gap: 20px;
}

.banner-pet {
  font-size: 60px;
  animation: bounce 2s ease-in-out infinite;
}

.banner-pet:last-child {
  animation-delay: 0.5s;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-10px) rotate(-5deg); }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 16px;
}

.pet-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.foster-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.app-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.pending-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-info { flex: 1; }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin: 4px 0 0 0;
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.content-card {
  border-radius: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
}

.pet-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pet-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: background 0.3s;
}

.pet-item:hover {
  background: #ecf5ff;
}

.pet-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.pet-info {
  flex: 1;
}

.pet-name {
  margin: 0;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.pet-desc {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #909399;
}

.application-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.application-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: background 0.3s;
}

.application-item:hover {
  background: #ecf5ff;
}

.app-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-right: 12px;
}

.app-info {
  flex: 1;
}

.app-title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.app-time {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #909399;
}

.caregiver-card {
  border-radius: 12px;
}

.caregiver-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.caregiver-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 12px;
  background: linear-gradient(180deg, #f5f7fa 0%, #fff 100%);
  text-align: center;
  transition: transform 0.3s ease;
}

.caregiver-item:hover {
  transform: translateY(-4px);
}

.cg-avatar {
  margin-bottom: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 24px;
}

.cg-info {
  width: 100%;
  margin-bottom: 12px;
}

.cg-name-row {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}

.cg-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.cg-desc {
  margin: 4px 0;
  font-size: 13px;
  color: #909399;
}

.cg-skill {
  margin: 4px 0;
  font-size: 12px;
  color: #606266;
}
</style>