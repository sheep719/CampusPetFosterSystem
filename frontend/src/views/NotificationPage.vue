<template>
  <div class="notification-page">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="通知类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="到期提醒" value="expire" />
            <el-option label="评价提醒" value="rating" />
            <el-option label="系统通知" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="阅读状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="未读" :value="false" />
            <el-option label="已读" :value="true" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Check" @click="handleMarkAllRead">全部已读</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="stats-bar">
        <div class="stat-item">
          <span class="stat-value">{{ totalCount }}</span>
          <span class="stat-label">全部通知</span>
        </div>
        <div class="stat-item warning">
          <span class="stat-value">{{ unreadCount }}</span>
          <span class="stat-label">未读通知</span>
        </div>
        <div class="stat-item success">
          <span class="stat-value">{{ expireCount }}</span>
          <span class="stat-label">到期提醒</span>
        </div>
        <div class="stat-item info">
          <span class="stat-value">{{ ratingCount }}</span>
          <span class="stat-label">评价提醒</span>
        </div>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)" effect="plain">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
        <el-table-column label="阅读状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.readStatus ? 'success' : 'warning'" size="small">
              {{ row.readStatus ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column prop="readTime" label="阅读时间" width="160" align="center" />
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="success" link :icon="Check" @click="handleMarkRead(row)" v-if="!row.readStatus">标为已读</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="通知详情"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="1" border v-if="currentNotification">
        <el-descriptions-item label="通知类型">
          <el-tag :type="getTypeTagType(currentNotification.type)" effect="plain">
            {{ getTypeLabel(currentNotification.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题">{{ currentNotification.title }}</el-descriptions-item>
        <el-descriptions-item label="内容">{{ currentNotification.content }}</el-descriptions-item>
        <el-descriptions-item label="阅读状态">
          <el-tag :type="currentNotification.readStatus ? 'success' : 'warning'" size="small">
            {{ currentNotification.readStatus ? '已读' : '未读' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentNotification.createTime }}</el-descriptions-item>
        <el-descriptions-item label="阅读时间">{{ currentNotification.readTime || '未阅读' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleGoLink" v-if="currentNotification?.linkUrl">
          查看详情
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, View } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const detailVisible = ref(false)
const currentNotification = ref<any>(null)
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL

const searchForm = reactive({
  type: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])
const totalCount = ref(0)
const unreadCount = ref(0)
const expireCount = ref(0)
const ratingCount = ref(0)

const typeMap: Record<string, string> = {
  expire: '到期提醒',
  rating: '评价提醒',
  system: '系统通知'
}

const getTypeLabel = (type: string) => typeMap[type] || type
const getTypeTagType = (type: string) => {
  const map: Record<string, string> = {
    expire: 'warning',
    rating: 'success',
    system: 'info'
  }
  return map[type] || 'info'
}

const mockData = [
  { id: 1, type: 'expire', title: '寄养即将到期提醒', content: '您的宠物 咪咪 的寄养将于 2024-01-20 18:00 到期，请及时安排接回。', linkUrl: '/owner-applications', readStatus: false, createTime: '2024-01-19 10:00:00', readTime: '' },
  { id: 2, type: 'rating', title: '请评价寄养服务', content: '您的宠物 咪咪 已完成寄养，请对本次服务进行评价。', linkUrl: '/owner-applications', readStatus: false, createTime: '2024-01-20 18:30:00', readTime: '' },
  { id: 3, type: 'expire', title: '寄养到期提醒', content: '您的宠物 旺财 的寄养还有 3 天到期，请提前安排接回。', linkUrl: '/owner-applications', readStatus: true, createTime: '2024-01-22 09:00:00', readTime: '2024-01-22 10:30:00' },
  { id: 4, type: 'system', title: '系统通知', content: '您的账号已成功绑定手机号。', linkUrl: '', readStatus: true, createTime: '2024-01-15 14:00:00', readTime: '2024-01-15 14:30:00' },
  { id: 5, type: 'expire', title: '寄养即将到期提醒', content: '您的宠物 雪球 的寄养将于 2024-01-25 14:00 到期，请及时安排接回。', linkUrl: '/owner-applications', readStatus: false, createTime: '2024-01-24 08:00:00', readTime: '' },
  { id: 6, type: 'rating', title: '请评价寄养服务', content: '您的宠物 旺财 已完成寄养，请对本次服务进行评价。', linkUrl: '/owner-applications', readStatus: true, createTime: '2024-01-25 17:30:00', readTime: '2024-01-26 09:00:00' },
  { id: 7, type: 'expire', title: '寄养到期提醒', content: '您的宠物 可乐 的寄养还有 3 天到期，请提前安排接回。', linkUrl: '/owner-applications', readStatus: true, createTime: '2024-01-27 10:00:00', readTime: '2024-01-27 11:00:00' },
  { id: 8, type: 'system', title: '系统通知', content: '新的寄养申请已提交，请及时处理。', linkUrl: '/foster-application', readStatus: false, createTime: '2024-01-28 09:00:00', readTime: '' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.type) {
      filtered = filtered.filter(item => item.type === searchForm.type)
    }
    if (searchForm.status !== '') {
      filtered = filtered.filter(item => item.readStatus === searchForm.status)
    }
    totalCount.value = filtered.length
    unreadCount.value = filtered.filter(item => !item.readStatus).length
    expireCount.value = filtered.filter(item => item.type === 'expire').length
    ratingCount.value = filtered.filter(item => item.type === 'rating').length
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = filtered.slice(start, end)
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.type = ''
  searchForm.status = ''
  pagination.page = 1
  loadData()
}

const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page: number) => {
  pagination.page = page
  loadData()
}

const handleView = (row: any) => {
  currentNotification.value = row
  detailVisible.value = true
}

const handleMarkRead = (row: any) => {
  const token = localStorage.getItem('token') || ''
  fetch(`${apiBaseUrl}/notification/read/${row.id}`, { 
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        row.readStatus = true
        unreadCount.value--
        ElMessage.success('已标记为已读')
      }
    })
    .catch(() => {})
}

const handleMarkAllRead = () => {
  const userId = localStorage.getItem('userId') || '1'
  const token = localStorage.getItem('token') || ''
  ElMessageBox.confirm('确定要将所有通知标记为已读吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    fetch(`${apiBaseUrl}/notification/read-all?userId=${userId}`, { 
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => res.json())
      .then(data => {
        if (data.code === 200) {
          ElMessage.success('已全部标记为已读')
          tableData.value.forEach(item => item.readStatus = true)
          unreadCount.value = 0
        }
      })
      .catch(() => {})
  }).catch(() => {})
}

const handleGoLink = () => {
  if (currentNotification.value?.linkUrl) {
    router.push(currentNotification.value.linkUrl)
    detailVisible.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.notification-page {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.table-card {
  border-radius: 8px;
}

.stats-bar {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 24px;
  background: #fff;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.stat-item.warning {
  border-left-color: #E6A23C;
}

.stat-item.success {
  border-left-color: #67C23A;
}

.stat-item.info {
  border-left-color: #909399;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.el-descriptions__label) {
  width: 80px;
}
</style>