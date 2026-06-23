<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>数据看板</h1>
      <p>欢迎回来，{{ getRoleText() }}</p>
    </div>
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-icon pet-icon">🐾</div>
        <div class="stat-info">
          <p class="stat-value">{{ petCount }}</p>
          <p class="stat-label">宠物总数</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon foster-icon">🏠</div>
        <div class="stat-info">
          <p class="stat-value">{{ fosterCount }}</p>
          <p class="stat-label">寄养中数量</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon app-icon">📝</div>
        <div class="stat-info">
          <p class="stat-value">{{ applicationCount }}</p>
          <p class="stat-label">本月申请</p>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-icon pending-icon">⏳</div>
        <div class="stat-info">
          <p class="stat-value">{{ pendingCount }}</p>
          <p class="stat-label">待处理</p>
        </div>
      </el-card>
    </div>
    <div class="charts-row">
      <el-card class="chart-card">
        <template #header>
          <span>寄养趋势</span>
        </template>
        <div class="chart-placeholder">
          <el-progress :percentage="65" :stroke-width="20" />
        </div>
      </el-card>
      <el-card class="chart-card">
        <template #header>
          <span>宠物种类分布</span>
        </template>
        <div class="chart-placeholder">
          <el-progress type="circle" :percentage="45" />
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const petCount = ref(128)
const fosterCount = ref(24)
const applicationCount = ref(56)
const pendingCount = ref(8)

const getRoleText = () => {
  const role = localStorage.getItem('role')
  const roleMap: Record<string, string> = {
    owner: '寄养者',
    caregiver: '被寄养者',
    admin: '管理员'
  }
  return roleMap[role || ''] || '用户'
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 20px;
}

.dashboard-header h1 {
  font-size: 24px;
  color: #303133;
  margin: 0 0 8px 0;
}

.dashboard-header p {
  font-size: 14px;
  color: #909399;
  margin: 0;
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
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin-right: 16px;
}

.pet-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.foster-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.app-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.pending-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

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

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.chart-card {
  height: 300px;
}

.chart-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100% - 48px);
}
</style>
