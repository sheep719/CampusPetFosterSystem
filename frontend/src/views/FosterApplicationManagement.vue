<template>
  <div class="page-container">
    <div class="page-header">
      <h2>寄养申请管理</h2>
      <el-button type="primary" @click="handleAdd">发起申请</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="petName" label="宠物名称" />
        <el-table-column prop="caregiverName" label="被寄养者" />
        <el-table-column prop="startTime" label="开始时间" />
        <el-table-column prop="expectedEndTime" label="预计结束" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="success" @click="handleAccept(row)">接受</el-button>
            <el-button v-if="row.status === 'pending'" size="small" type="danger" @click="handleReject(row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const tableData = ref([
  { id: 1, petName: '咪咪', caregiverName: '李阿姨', startTime: '2026-06-25 09:00', expectedEndTime: '2026-06-30 18:00', status: 'pending' }
])

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    accepted: 'info',
    in_progress: 'success',
    completed: '',
    rejected: 'danger',
    cancelled: 'info'
  }
  return map[status] || ''
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待确认',
    accepted: '已接受',
    in_progress: '进行中',
    completed: '已完成',
    rejected: '已拒绝',
    cancelled: '已取消'
  }
  return map[status] || status
}

const handleAdd = () => {}
const handleView = (row: unknown) => {}
const handleAccept = (row: unknown) => {}
const handleReject = (row: unknown) => {}
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #303133;
  margin: 0;
}
</style>
