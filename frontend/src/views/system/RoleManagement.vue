<template>
  <div class="role-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <h3 class="table-title">角色列表</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="150" />
        <el-table-column prop="roleCode" label="角色编码" width="150">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.roleCode }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="角色描述" min-width="200" />
        <el-table-column prop="userCount" label="用户数" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #409EFF; font-weight: 600">{{ row.userCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link :icon="Grid" @click="handleAssignPermission(row)">分配权限</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入角色描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="permissionVisible"
      title="分配权限"
      width="400px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-tree
        :data="permissionTree"
        show-checkbox
        node-key="id"
        ref="treeRef"
        :default-expand-all="true"
        :default-checked-keys="checkedKeys"
      >
        <template #default="{ data }">
          <span>{{ data.label }}</span>
        </template>
      </el-tree>
      <template #footer>
        <el-button @click="permissionVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermission">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import type { TreeInstance } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Grid } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const permissionVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const treeRef = ref<TreeInstance>()
const isEdit = ref(false)
const checkedKeys = ref<number[]>([])

const searchForm = reactive({
  roleName: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  roleName: '',
  roleCode: '',
  description: ''
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  description: [{ required: true, message: '请输入角色描述', trigger: 'blur' }]
}

const permissionTree = [
  {
    id: 1,
    label: '数据看板',
    children: []
  },
  {
    id: 2,
    label: '宠物管理',
    children: [
      { id: 21, label: '宠物列表' },
      { id: 22, label: '新增宠物' },
      { id: 23, label: '编辑宠物' },
      { id: 24, label: '删除宠物' }
    ]
  },
  {
    id: 3,
    label: '寄养管理',
    children: [
      { id: 31, label: '寄养申请' },
      { id: 32, label: '审核申请' },
      { id: 33, label: '寄养记录' }
    ]
  },
  {
    id: 4,
    label: '系统管理',
    children: [
      { id: 41, label: '用户管理' },
      { id: 42, label: '角色管理' },
      { id: 43, label: '菜单管理' },
      { id: 44, label: '字典管理' }
    ]
  }
]

const mockData = [
  { id: 1, roleName: '管理员', roleCode: 'admin', description: '系统管理员，拥有全部权限', userCount: 1, createTime: '2026-06-23 10:00:00' },
  { id: 2, roleName: '寄养者', roleCode: 'owner', description: '学生用户，可管理宠物和发起寄养申请', userCount: 6, createTime: '2026-06-23 11:00:00' },
  { id: 3, roleName: '被寄养者', roleCode: 'caregiver', description: '附近居民，可接收寄养申请并照顾宠物', userCount: 5, createTime: '2026-06-23 12:00:00' },
  { id: 4, roleName: '运营人员', roleCode: 'operator', description: '平台运营人员，负责内容审核和用户管理', userCount: 2, createTime: '2026-06-24 09:00:00' },
  { id: 5, roleName: '客服人员', roleCode: 'customer_service', description: '客服人员，处理用户咨询和投诉', userCount: 3, createTime: '2026-06-24 10:00:00' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.roleName) {
      filtered = filtered.filter(item => item.roleName.includes(searchForm.roleName))
    }
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
  searchForm.roleName = ''
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

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  form.id = null
  form.roleName = ''
  form.roleCode = ''
  form.description = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  form.id = row.id
  form.roleName = row.roleName
  form.roleCode = row.roleCode
  form.description = row.description
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

const handleAssignPermission = (row: any) => {
  checkedKeys.value = [1, 21, 31, 41]
  permissionVisible.value = true
}

const handleSavePermission = () => {
  ElMessage.success('权限分配成功')
  permissionVisible.value = false
}

const handleDelete = (row: any) => {
  if (row.userCount > 0) {
    ElMessage.warning('该角色下存在用户，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除角色 "${row.roleName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.role-management {
  padding: 0;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.table-card {
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>