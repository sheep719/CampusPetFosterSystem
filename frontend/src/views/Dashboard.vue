<template>
  <div class="admin-dashboard">
    <div class="welcome-banner">
      <div class="banner-left">
        <h2>管理员你好 👋</h2>
        <p>欢迎来到校园宠物临时寄养管理系统后台</p>
        <p 
          class="banner-tip cursor-pointer" 
          :class="{ 'has-pending': pendingApplications > 0 }"
          @click="goToPendingApplications"
        >今天有 {{ pendingApplications }} 个待处理申请等待审核 📋</p>
      </div>
      <div class="banner-right">
        <div class="banner-icon">🐾</div>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card cursor-pointer" shadow="hover" @click="goToUserManagement">
        <div class="stat-icon user-icon">👥</div>
        <div class="stat-info">
          <p class="stat-value">{{ userCount }}</p>
          <p class="stat-label">注册用户</p>
        </div>
        <div class="stat-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>12%</span>
        </div>
        <div class="stat-arrow"><el-icon><ArrowRight /></el-icon></div>
      </el-card>
      <el-card class="stat-card cursor-pointer" shadow="hover" @click="goToPetManagement">
        <div class="stat-icon pet-icon">🐾</div>
        <div class="stat-info">
          <p class="stat-value">{{ petCount }}</p>
          <p class="stat-label">宠物总数</p>
        </div>
        <div class="stat-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>8%</span>
        </div>
        <div class="stat-arrow"><el-icon><ArrowRight /></el-icon></div>
      </el-card>
      <el-card class="stat-card cursor-pointer" shadow="hover" @click="goToFosterManagement">
        <div class="stat-icon foster-icon">🏠</div>
        <div class="stat-info">
          <p class="stat-value">{{ fosteringCount }}</p>
          <p class="stat-label">寄养中</p>
        </div>
        <div class="stat-trend down">
          <el-icon><ArrowDown /></el-icon>
          <span>3%</span>
        </div>
        <div class="stat-arrow"><el-icon><ArrowRight /></el-icon></div>
      </el-card>
      <el-card class="stat-card cursor-pointer" shadow="hover" @click="goToPendingApplications">
        <div class="stat-icon app-icon">📝</div>
        <div class="stat-info">
          <p class="stat-value">{{ pendingApplications }}</p>
          <p class="stat-label">待审核申请</p>
        </div>
        <div class="stat-trend up">
          <el-icon><ArrowUp /></el-icon>
          <span>15%</span>
        </div>
        <div class="stat-arrow"><el-icon><ArrowRight /></el-icon></div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">📊 近7天寄养申请趋势</span>
          </div>
        </template>
        <div class="chart-container">
          <div class="bar-chart">
            <div class="bar-item" v-for="(item, index) in weekData" :key="index">
              <div class="bar-wrapper">
                <div class="bar" :style="{ height: item.value + '%' }">
                  <span class="bar-value">{{ item.count }}</span>
                </div>
              </div>
              <span class="bar-label">{{ item.day }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="chart-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">🐾 宠物种类分布</span>
          </div>
        </template>
        <div class="chart-container">
          <div class="pie-chart-wrapper">
          <div class="pie-chart" :style="{ background: pieChartStyle }">
            <div class="pie-inner">
              <p class="pie-total">{{ petCount }}</p>
              <p class="pie-label">总计</p>
            </div>
          </div>
          <div class="pie-legend">
            <div class="legend-item" v-for="item in petTypes" :key="item.name">
              <span class="legend-dot" :style="{ background: item.color }"></span>
              <span class="legend-name">{{ item.name }}</span>
              <span class="legend-value">{{ item.count }}</span>
            </div>
          </div>
        </div>
        </div>
      </el-card>
    </div>

    <div class="bottom-row">
      <el-card class="activity-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">📅 最近活动</span>
          </div>
        </template>
        <div class="activity-timeline">
          <div class="timeline-item" v-for="(item, index) in recentActivities" :key="index">
            <div class="timeline-dot" :class="item.type"></div>
            <div class="timeline-content">
              <p class="timeline-text">{{ item.text }}</p>
              <p class="timeline-time">{{ item.time }}</p>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="top-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">🏆 寄养达人榜</span>
          </div>
        </template>
        <div class="top-list">
          <div class="top-item" v-for="(item, index) in topCaregivers" :key="item.id">
            <div class="top-rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</div>
            <el-avatar :size="40" class="top-avatar">{{ item.avatar }}</el-avatar>
            <div class="top-info">
              <p class="top-name">{{ item.name }}</p>
              <p class="top-desc">已寄养 {{ item.count }} 次</p>
            </div>
            <el-rate v-model="item.rating" disabled :size="14" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ArrowUp, ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { 
  getDashboardStats, 
  getPetSpeciesDistribution,
  getWeeklyTrend,
  getRecentActivitiesList,
  getTopCaregivers
} from '@/api/dashboard'

const router = useRouter()

const userCount = ref(0)
const petCount = ref(0)
const fosteringCount = ref(0)
const pendingApplications = ref(0)
const loading = ref(false)

const weekData = ref<{ day: string; count: number; value: number }[]>([])
const petTypes = ref<{ name: string; count: number; color: string }[]>([])
const recentActivities = ref<{ text: string; type: string; time: string }[]>([])
const topCaregivers = ref<{ id: number; name: string; avatar: string; count: number; rating: number }[]>([])

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const pieChartStyle = computed(() => {
  if (petTypes.value.length === 0) return ''
  const total = petTypes.value.reduce((sum, item) => sum + item.count, 0)
  let currentAngle = 0
  const gradients = petTypes.value.map(item => {
    const percentage = (item.count / total) * 100
    const startAngle = currentAngle
    const endAngle = currentAngle + percentage
    currentAngle = endAngle
    return `${item.color} ${startAngle}% ${endAngle}%`
  })
  return `conic-gradient(${gradients.join(', ')})`
})

const goToPendingApplications = () => {
  router.push('/foster-application')
}

const goToUserManagement = () => {
  router.push('/user-management')
}

const goToPetManagement = () => {
  router.push('/pet-management')
}

const goToFosterManagement = () => {
  router.push('/foster-application')
}

const formatTime = (timeStr: string): string => {
  if (!timeStr) return '未知时间'
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return date.toLocaleDateString('zh-CN')
}

const loadStats = async () => {
  loading.value = true
  try {
    const stats = await getDashboardStats()
    userCount.value = stats.totalUsers || 0
    petCount.value = stats.totalPets || 0
    fosteringCount.value = stats.fosteringPets || 0
    pendingApplications.value = stats.pendingApplications || 0

    const speciesData = await getPetSpeciesDistribution()
    const speciesMap: Record<string, string> = {
      cat: '猫咪',
      dog: '狗狗',
      rabbit: '兔子',
      hamster: '仓鼠',
      bird: '鸟类',
      other: '其他'
    }
    const colorMap: Record<string, string> = {
      cat: '#667eea',
      dog: '#f093fb',
      rabbit: '#4facfe',
      hamster: '#ff9f43',
      bird: '#1dd1a1',
      other: '#43e97b'
    }
    petTypes.value = Object.entries(speciesData).map(([key, count]) => ({
      name: speciesMap[key] || key,
      count: count as number,
      color: colorMap[key] || '#909399'
    }))

    const trend = await getWeeklyTrend()
    const maxCount = Math.max(...trend.counts, 1)
    weekData.value = trend.dates.map((date, index) => ({
      day: weekDays[index % 7],
      count: trend.counts[index],
      value: (trend.counts[index] / maxCount) * 100
    }))

    const activities = await getRecentActivitiesList()
    recentActivities.value = activities.map(item => ({
      text: item.content,
      type: item.type === 'application' ? 'application' : 
            item.type === 'handover' ? 'complete' : 'user',
      time: formatTime(item.time)
    }))

    const caregivers = await getTopCaregivers()
    topCaregivers.value = caregivers.map(item => ({
      id: item.id,
      name: item.name,
      avatar: '👤',
      count: item.totalCareCount,
      rating: item.avgRating
    }))
  } catch (error) {
    console.error('加载统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 0;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
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
  color: #fff;
}

.banner-left p {
  margin: 4px 0;
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.banner-tip {
  margin-top: 12px !important;
  background: rgba(255, 255, 255, 0.2);
  display: inline-block;
  padding: 6px 14px;
  border-radius: 20px;
  transition: all 0.3s ease;
}

.cursor-pointer {
  cursor: pointer;
}

.banner-tip.cursor-pointer:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateX(4px);
}

.banner-tip.has-pending {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { background: rgba(255, 255, 255, 0.2); }
  50% { background: rgba(255, 255, 255, 0.4); }
}

.banner-icon {
  font-size: 80px;
  opacity: 0.3;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-15px) rotate(10deg); }
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
  position: relative;
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

.user-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.pet-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.foster-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.app-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

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

.stat-trend {
  position: absolute;
  top: 16px;
  right: 16px;
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 12px;
}

.stat-trend.up {
  color: #67c23a;
  background: #f0f9eb;
}

.stat-trend.down {
  color: #f56c6c;
  background: #fef0f0;
}

.stat-arrow {
  position: absolute;
  bottom: 16px;
  right: 16px;
  color: #c0c4cc;
  font-size: 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.stat-card:hover .stat-arrow {
  opacity: 1;
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
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

.chart-container {
  height: 280px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding: 20px 0;
}

.bar-chart {
  display: flex;
  align-items: flex-end;
  gap: 24px;
  height: 100%;
  width: 100%;
  justify-content: center;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  flex: 1;
  max-width: 60px;
}

.bar-wrapper {
  height: 200px;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.bar {
  width: 36px;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px 8px 0 0;
  position: relative;
  transition: all 0.3s ease;
  min-height: 20px;
}

.bar:hover {
  background: linear-gradient(180deg, #f093fb 0%, #f5576c 100%);
  transform: scaleX(1.1);
}

.bar-value {
  position: absolute;
  top: -22px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 12px;
  font-weight: 600;
  color: #606266;
}

.bar-label {
  font-size: 12px;
  color: #909399;
}

.pie-chart-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-around;
  height: 100%;
  width: 100%;
}

.pie-chart {
  width: 140px;
  height: 140px;
  border-radius: 50%;
  background: conic-gradient(
    #667eea 0% 45%,
    #f093fb 45% 80%,
    #4facfe 80% 92%,
    #43e97b 92% 100%
  );
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-inner {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.pie-total {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.pie-label {
  font-size: 12px;
  color: #909399;
  margin: 2px 0 0 0;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-name {
  font-size: 13px;
  color: #606266;
  min-width: 40px;
}

.legend-value {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.activity-card,
.top-card {
  border-radius: 12px;
}

.activity-timeline {
  padding: 10px 0;
}

.timeline-item {
  display: flex;
  gap: 14px;
  padding: 12px 0;
  position: relative;
}

.timeline-item:not(:last-child)::after {
  content: '';
  position: absolute;
  left: 5px;
  top: 32px;
  bottom: 0;
  width: 2px;
  background: #ebeef5;
}

.timeline-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 4px;
}

.timeline-dot.user { background: #667eea; }
.timeline-dot.application { background: #f093fb; }
.timeline-dot.complete { background: #43e97b; }
.timeline-dot.pet { background: #4facfe; }

.timeline-content {
  flex: 1;
}

.timeline-text {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #303133;
}

.timeline-time {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.top-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.top-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: background 0.3s;
}

.top-item:hover {
  background: #ecf5ff;
}

.top-rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.rank-1 { background: linear-gradient(135deg, #ffd700, #ffb800); }
.rank-2 { background: linear-gradient(135deg, #c0c0c0, #a8a8a8); }
.rank-3 { background: linear-gradient(135deg, #cd7f32, #b87333); }

.top-rank:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #dcdfe6;
  color: #909399;
}

.top-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-size: 18px;
}

.top-info {
  flex: 1;
}

.top-name {
  margin: 0 0 2px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.top-desc {
  margin: 0;
  font-size: 12px;
  color: #909399;
}
</style>