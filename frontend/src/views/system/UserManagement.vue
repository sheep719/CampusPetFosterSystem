<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="roleName" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.roleName)">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="enabled" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" @click="handleResetPassword(row)">重置密码</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const tableData = ref([
  { id: 1, username: 'admin', roleName: '管理员', phone: '13800138000', enabled: true, createTime: '2026-06-23 10:00' },
  { id: 2, username: 'student001', roleName: '寄养者', phone: '13900139001', enabled: true, createTime: '2026-06-23 11:00' },
  { id: 3, username: 'caregiver001', roleName: '被寄养者', phone: '13900139002', enabled: true, createTime: '2026-06-23 12:00' }
])

const getRoleTagType = (roleName: string) => {
  const map: Record<string, string> = {
    '管理员': 'danger',
    '寄养者': 'success',
    '被寄养者': 'info'
  }
  return map[roleName] || ''
}

const handleAdd = () => {}
const handleEdit = (row: unknown) => {}
const handleResetPassword = (row: unknown) => {}
const handleDelete = (row: unknown) => {}
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
