<template>
  <div class="caregiver-dashboard">
    <div class="welcome-banner">
      <div class="banner-left">
        <h2>你好，{{ username }} 👋</h2>
        <p>欢迎回到校园宠物临时寄养管理系统</p>
        <div class="rating-row">
          <el-rate v-model="profileRating" disabled :size="16" />
          <span class="rating-text">评分 {{ profileRating }} / 5.0</span>
        </div>
      </div>
      <div class="banner-right">
        <div class="banner-pet">🐶</div>
        <div class="banner-pet">🐱</div>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon pet-icon">🐾</div>
        <div class="stat-info">
          <p class="stat-value">{{ fosteringCount }}</p>
          <p class="stat-label">寄养中</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon app-icon">📝</div>
        <div class="stat-info">
          <p class="stat-value">{{ pendingCount }}</p>
          <p class="stat-label">待处理申请</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon total-icon">🏆</div>
        <div class="stat-info">
          <p class="stat-value">{{ totalCount }}</p>
          <p class="stat-label">累计寄养</p>
        </div>
      </el-card>
      <el-card class="stat-card" shadow="hover">
        <div class="stat-icon review-icon">⭐</div>
        <div class="stat-info">
          <p class="stat-value">{{ reviewCount }}</p>
          <p class="stat-label">收到评价</p>
        </div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="content-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">⏳ 待处理申请</span>
            <el-button type="primary" link @click="goToApplications">查看全部</el-button>
          </div>
        </template>
        <div class="application-list">
          <div class="application-item" v-for="app in pendingApplications" :key="app.id">
            <div class="app-avatar">{{ app.petAvatar }}</div>
            <div class="app-info">
              <p class="app-title">{{ app.petName }} <span class="app-breed">· {{ app.petBreed }}</span></p>
              <p class="app-desc">寄养者：{{ app.ownerName }}</p>
              <p class="app-time">时间：{{ app.startDate }} ~ {{ app.endDate }}</p>
            </div>
            <div class="app-actions">
              <el-button type="success" size="small" @click="approveApp(app)">同意</el-button>
              <el-button type="danger" size="small" @click="rejectApp(app)">拒绝</el-button>
            </div>
          </div>
          <el-empty v-if="pendingApplications.length === 0" description="暂无待处理申请" />
        </div>
      </el-card>

      <el-card class="content-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">🐾 寄养中宠物</span>
            <el-button type="primary" link @click="goToPets">查看全部</el-button>
          </div>
        </template>
        <div class="fostering-list">
          <div class="fostering-item" v-for="pet in fosteringPets" :key="pet.id">
            <div class="foster-avatar">{{ pet.avatar }}</div>
            <div class="foster-info">
              <p class="foster-name">{{ pet.name }} <span class="foster-breed">· {{ pet.breed }}</span></p>
              <p class="foster-owner">寄养者：{{ pet.ownerName }}</p>
              <div class="foster-progress">
                <el-progress :percentage="pet.progress" :stroke-width="6" :show-text="false" />
                <span class="foster-days">剩余 {{ pet.remainingDays }} 天</span>
              </div>
            </div>
            <el-button type="primary" size="small" round>详情</el-button>
          </div>
          <el-empty v-if="fosteringPets.length === 0" description="暂无寄养中宠物" />
        </div>
      </el-card>
    </div>

    <el-card class="review-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span class="card-title">💬 最新评价</span>
          <el-button type="primary" link>查看更多</el-button>
        </div>
      </template>
      <div class="review-list">
        <div class="review-item" v-for="review in latestReviews" :key="review.id">
          <el-avatar :size="44" class="review-avatar">{{ review.avatar }}</el-avatar>
          <div class="review-content">
            <div class="review-header">
              <span class="review-name">{{ review.name }}</span>
              <el-rate v-model="review.rating" disabled :size="14" />
              <span class="review-time">{{ review.time }}</span>
            </div>
            <p class="review-text">{{ review.content }}</p>
            <p class="review-pet">宠物：{{ review.petName }}</p>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const username = ref(userStore.username || localStorage.getItem('username') || '用户')

const profileRating = ref(4.8)
const fosteringCount = ref(2)
const pendingCount = ref(3)
const totalCount = ref(15)
const reviewCount = ref(28)

const pendingApplications = ref([
  { id: 1, petName: '小白', petBreed: '橘猫', petAvatar: '🐱', ownerName: '张三', startDate: '01-20', endDate: '01-25' },
  { id: 2, petName: '旺财', petBreed: '金毛', petAvatar: '🐕', ownerName: '李四', startDate: '01-22', endDate: '01-28' },
  { id: 3, petName: '咪咪', petBreed: '布偶猫', petAvatar: '😺', ownerName: '王五', startDate: '01-25', endDate: '02-01' }
])

const fosteringPets = ref([
  { id: 1, name: '豆豆', breed: '柯基', avatar: '🐶', ownerName: '赵同学', progress: 60, remainingDays: 4 },
  { id: 2, name: '花花', breed: '英短', avatar: '🐱', ownerName: '钱同学', progress: 30, remainingDays: 7 }
])

const latestReviews = ref([
  { id: 1, name: '张同学', avatar: '👨', rating: 5, time: '3天前', content: '李阿姨非常有爱心，每天都给我发视频汇报狗狗的情况，狗狗回来长胖了一圈！非常感谢~', petName: '旺财（金毛）' },
  { id: 2, name: '王同学', avatar: '👩', rating: 4.5, time: '1周前', content: '阿姨很耐心，猫咪适应得很好，下次还会选择这里寄养。', petName: '咪咪（布偶猫）' },
  { id: 3, name: '李同学', avatar: '👧', rating: 5, time: '2周前', content: '超级好的体验！家里很干净，狗狗玩得很开心，接回来的时候状态特别好。', petName: '豆豆（柯基）' }
])

const approveApp = (app: any) => {
  ElMessage.success(`已同意 ${app.petName} 的寄养申请`)
}

const rejectApp = (app: any) => {
  ElMessage.info(`已拒绝 ${app.petName} 的寄养申请`)
}

const goToApplications = () => {
  router.push('/caregiver-applications')
}

const goToPets = () => {
  router.push('/caregiver-pets')
}
</script>

<style scoped>
.caregiver-dashboard {
  padding: 0;
}

.welcome-banner {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 50%, #fbc2eb 100%);
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

.rating-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 12px;
}

.rating-text {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
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
  50% { transform: translateY(-10px) rotate(5deg); }
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
.app-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.total-icon { background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); }
.review-icon { background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%); }

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

.application-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.application-item {
  display: flex;
  align-items: center;
  padding: 14px;
  border-radius: 10px;
  background: #f5f7fa;
  transition: all 0.3s;
}

.application-item:hover {
  background: #ecf5ff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.app-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin-right: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.app-info {
  flex: 1;
}

.app-title {
  margin: 0 0 4px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.app-breed {
  font-size: 13px;
  font-weight: normal;
  color: #909399;
}

.app-desc {
  margin: 2px 0;
  font-size: 13px;
  color: #606266;
}

.app-time {
  margin: 2px 0 0 0;
  font-size: 12px;
  color: #909399;
}

.app-actions {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.fostering-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.fostering-item {
  display: flex;
  align-items: center;
  padding: 14px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8f4f8 100%);
  transition: all 0.3s;
}

.fostering-item:hover {
  transform: translateX(4px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.foster-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  margin-right: 14px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.foster-info {
  flex: 1;
}

.foster-name {
  margin: 0 0 4px 0;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.foster-breed {
  font-size: 13px;
  font-weight: normal;
  color: #909399;
}

.foster-owner {
  margin: 2px 0 6px 0;
  font-size: 13px;
  color: #606266;
}

.foster-progress {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.foster-progress :deep(.el-progress) {
  flex: 1;
}

.foster-days {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.review-card {
  border-radius: 12px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  display: flex;
  gap: 14px;
  padding: 16px;
  border-radius: 10px;
  background: #f5f7fa;
}

.review-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.review-content {
  flex: 1;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.review-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.review-time {
  font-size: 12px;
  color: #909399;
  margin-left: auto;
}

.review-text {
  margin: 0 0 6px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.6;
}

.review-pet {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
</style>