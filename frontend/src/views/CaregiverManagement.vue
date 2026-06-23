<template>
  <div class="page-container">
    <div class="page-header">
      <h2>被寄养者管理</h2>
    </div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="address" label="住址" />
        <el-table-column prop="distance" label="距离(km)" />
        <el-table-column prop="careTypes" label="可照顾类型" />
        <el-table-column prop="avgRating" label="评分" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const tableData = ref([
  { id: 1, name: '李阿姨', address: 'XX小区3号楼201室', distance: 1.5, careTypes: '猫、狗', avgRating: 4.8 }
])

const handleView = (row: unknown) => {}
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
