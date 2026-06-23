<template>
  <div class="page-container">
    <div class="page-header">
      <h2>字典管理</h2>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="dictName" label="字典类型" />
        <el-table-column prop="dictCode" label="字典编码" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleViewData(row)">查看数据</el-button>
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
  { id: 1, dictName: '宠物种类', dictCode: 'pet_species' },
  { id: 2, dictName: '寄养状态', dictCode: 'foster_status' },
  { id: 3, dictName: '健康状态', dictCode: 'health_status' },
  { id: 4, dictName: '评价等级', dictCode: 'rating_level' }
])

const handleViewData = (row: unknown) => {}
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
