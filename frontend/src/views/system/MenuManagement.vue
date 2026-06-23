<template>
  <div class="page-container">
    <div class="page-header">
      <h2>菜单管理</h2>
      <el-button type="primary" @click="handleAdd">新增菜单</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="menuName" label="菜单名称" />
        <el-table-column prop="path" label="路由路径" />
        <el-table-column prop="parentId" label="父菜单" width="80" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
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
  { id: 1, menuName: '数据看板', path: '/dashboard', parentId: 0 },
  { id: 2, menuName: '宠物档案', path: '/pet', parentId: 0 },
  { id: 3, menuName: '系统管理', path: '/system', parentId: 0 },
  { id: 4, menuName: '用户管理', path: '/system/user', parentId: 3 },
  { id: 5, menuName: '角色管理', path: '/system/role', parentId: 3 },
  { id: 6, menuName: '菜单管理', path: '/system/menu', parentId: 3 },
  { id: 7, menuName: '字典管理', path: '/system/dict', parentId: 3 }
])

const handleAdd = () => {}
const handleEdit = (row: unknown) => {}
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
