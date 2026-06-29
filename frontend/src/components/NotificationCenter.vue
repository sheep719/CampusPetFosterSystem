<template>
  <el-dropdown ref="dropdownRef" @command="handleCommand" trigger="click">
    <span class="notification-icon-wrapper">
      <el-icon :size="20" :class="{ 'notification-dot': unreadCount > 0 }">
        <Bell />
      </el-icon>
      <el-badge v-if="unreadCount > 0" :value="unreadCount" class="notification-badge" />
    </span>
    <template #dropdown>
      <div class="notification-dropdown">
        <div class="dropdown-header">
          <span>通知中心</span>
          <span class="mark-all" @click.stop="handleMarkAllRead">全部已读</span>
        </div>
        <div class="notification-list" v-if="notifications.length">
          <div
            v-for="item in notifications"
            :key="item.id"
            class="notification-item"
            :class="{ 'unread': !item.readStatus }"
            @click.stop="handleClick(item)"
          >
            <div class="notification-icon">
              <Clock v-if="item.type === 'expire'" />
              <Star v-else-if="item.type === 'rating'" />
              <InfoFilled v-else />
            </div>
            <div class="notification-content">
              <div class="notification-title">{{ item.title }}</div>
              <div class="notification-desc">{{ item.content }}</div>
              <div class="notification-time">{{ formatTime(item.createTime) }}</div>
            </div>
          </div>
        </div>
        <div class="empty-notification" v-else>
          <el-icon :size="48" class="empty-icon"><Bell /></el-icon>
          <p>暂无通知</p>
        </div>
        <div class="dropdown-footer">
          <span @click.stop="handleViewAll">查看全部</span>
        </div>
      </div>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell, Clock, Star, InfoFilled } from '@element-plus/icons-vue'

const router = useRouter()
const dropdownRef = ref()
const unreadCount = ref(0)
const notifications = ref<any[]>([])
let refreshTimer: any = null
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL

const loadNotifications = () => {
  const userId = localStorage.getItem('userId') || '1'
  const token = localStorage.getItem('token') || ''
  fetch(`${apiBaseUrl}/notification/list?userId=${userId}&limit=10`, {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        notifications.value = data.data || []
      }
    })
    .catch(() => {})
}

const loadUnreadCount = () => {
  const userId = localStorage.getItem('userId') || '1'
  const token = localStorage.getItem('token') || ''
  fetch(`${apiBaseUrl}/notification/unread-count?userId=${userId}`, {
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        unreadCount.value = data.data?.count || 0
      }
    })
    .catch(() => {})
}

const handleMarkAllRead = () => {
  const userId = localStorage.getItem('userId') || '1'
  const token = localStorage.getItem('token') || ''
  fetch(`${apiBaseUrl}/notification/read-all?userId=${userId}`, { 
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        ElMessage.success('已全部标记为已读')
        unreadCount.value = 0
        notifications.value.forEach(n => n.readStatus = true)
      }
    })
    .catch(() => {})
}

const handleClick = (item: any) => {
  const token = localStorage.getItem('token') || ''
  fetch(`${apiBaseUrl}/notification/read/${item.id}`, { 
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` }
  })
    .then(res => res.json())
    .then(data => {
      if (data.code === 200) {
        item.readStatus = true
        unreadCount.value = Math.max(0, unreadCount.value - 1)
        if (item.linkUrl) {
          router.push(item.linkUrl)
        }
      }
    })
    .catch(() => {})
}

const handleViewAll = () => {
  router.push('/notification')
}

const handleCommand = () => {}

const formatTime = (timeStr: string) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return date.toLocaleDateString()
}

onMounted(() => {
  loadNotifications()
  loadUnreadCount()
  refreshTimer = setInterval(() => {
    loadUnreadCount()
  }, 30000)
})

onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.notification-icon-wrapper {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background 0.3s;
}

.notification-icon-wrapper:hover {
  background: #f5f7fa;
}

.notification-dot {
  color: #409EFF;
}

.notification-badge {
  position: absolute;
  top: 2px;
  right: 2px;
}

.notification-dropdown {
  width: 400px;
  padding: 0;
}

.dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  font-weight: 600;
  font-size: 16px;
}

.mark-all {
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
}

.mark-all:hover {
  text-decoration: underline;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.notification-item:hover {
  background: #f5f7fa;
}

.notification-item.unread {
  background: #f0f5ff;
}

.notification-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
}

.notification-icon :deep(.el-icon) {
  color: #409EFF;
  font-size: 18px;
}

.notification-content {
  flex: 1;
  min-width: 0;
}

.notification-title {
  font-weight: 500;
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.notification-desc {
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 4px;
}

.notification-time {
  font-size: 12px;
  color: #909399;
}

.empty-notification {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  color: #909399;
}

.empty-icon {
  color: #c0c4cc;
  margin-bottom: 12px;
}

.empty-notification p {
  margin: 0;
  font-size: 14px;
}

.dropdown-footer {
  padding: 12px;
  text-align: center;
  border-top: 1px solid #f0f0f0;
}

.dropdown-footer span {
  font-size: 14px;
  color: #409EFF;
  cursor: pointer;
}

.dropdown-footer span:hover {
  text-decoration: underline;
}
</style>