<template>
  <div class="page-container">
    <div class="page-header">
      <h2>宠物档案管理</h2>
      <el-button type="primary" @click="handleAdd">新增宠物</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="petName" label="宠物名称" />
        <el-table-column prop="species" label="种类" />
        <el-table-column prop="breed" label="品种" />
        <el-table-column prop="gender" label="性别" />
        <el-table-column prop="age" label="年龄" />
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
  { id: 1, petName: '咪咪', species: '猫', breed: '英短', gender: '母', age: 2 },
  { id: 2, petName: '旺财', species: '狗', breed: '金毛', gender: '公', age: 3 }
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
