<template>
  <div class="page-container">
    <div class="page-header">
      <h2>寄养点管理</h2>
      <el-button type="primary" @click="handleAdd">新增寄养点</el-button>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="locationName" label="寄养点名称" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="distance" label="距离(km)" />
        <el-table-column prop="capacity" label="容量" />
        <el-table-column prop="acceptedSpecies" label="接受种类" />
        <el-table-column prop="available" label="是否可用">
          <template #default="{ row }">
            <el-tag :type="row.available ? 'success' : 'danger'">
              {{ row.available ? '可接单' : '不可接单' }}
            </el-tag>
          </template>
        </el-table-column>
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
  { id: 1, locationName: '李阿姨家', address: 'XX小区3号楼201室', distance: 1.5, capacity: 3, acceptedSpecies: '猫、狗', available: true }
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
