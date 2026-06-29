<template>
  <div class="user-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="searchForm.role" placeholder="全部" clearable style="width: 150px">
            <el-option label="管理员" value="admin" />
            <el-option label="寄养者" value="owner" />
            <el-option label="被寄养者" value="caregiver" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <h3 class="table-title">用户列表</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增用户</el-button>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column prop="roleName" label="角色" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.roleCode)" effect="light">
              {{ row.roleName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="enabled" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link :icon="Key" @click="handleResetPassword(row)">重置密码</el-button>
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
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="form.roleCode" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="admin" />
            <el-option label="寄养者" value="owner" />
            <el-option label="被寄养者" value="caregiver" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="enabled">
          <el-radio-group v-model="form.enabled">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const searchForm = reactive({
  username: '',
  role: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  username: '',
  roleCode: '',
  phone: '',
  enabled: '1'
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const roleMap: Record<string, string> = {
  admin: '管理员',
  owner: '寄养者',
  caregiver: '被寄养者'
}

const getRoleTagType = (roleCode: string) => {
  const map: Record<string, string> = {
    admin: 'danger',
    owner: 'success',
    caregiver: 'warning'
  }
  return map[roleCode] || 'info'
}

const mockData = [
  { id: 1, username: 'admin', roleCode: 'admin', roleName: '管理员', phone: '13800138000', enabled: '1', createTime: '2026-06-23 10:00:00' },
  { id: 2, username: 'student001', roleCode: 'owner', roleName: '寄养者', phone: '13900139001', enabled: '1', createTime: '2026-06-23 11:00:00' },
  { id: 3, username: 'caregiver001', roleCode: 'caregiver', roleName: '被寄养者', phone: '13900139002', enabled: '1', createTime: '2026-06-23 12:00:00' },
  { id: 4, username: 'student002', roleCode: 'owner', roleName: '寄养者', phone: '13900139003', enabled: '1', createTime: '2026-06-24 09:00:00' },
  { id: 5, username: 'student003', roleCode: 'owner', roleName: '寄养者', phone: '13900139004', enabled: '0', createTime: '2026-06-24 10:00:00' },
  { id: 6, username: 'caregiver002', roleCode: 'caregiver', roleName: '被寄养者', phone: '13900139005', enabled: '1', createTime: '2026-06-24 11:00:00' },
  { id: 7, username: 'caregiver003', roleCode: 'caregiver', roleName: '被寄养者', phone: '13900139006', enabled: '1', createTime: '2026-06-24 14:00:00' },
  { id: 8, username: 'student004', roleCode: 'owner', roleName: '寄养者', phone: '13900139007', enabled: '1', createTime: '2026-06-24 15:00:00' },
  { id: 9, username: 'student005', roleCode: 'owner', roleName: '寄养者', phone: '13900139008', enabled: '1', createTime: '2026-06-25 08:00:00' },
  { id: 10, username: 'caregiver004', roleCode: 'caregiver', roleName: '被寄养者', phone: '13900139009', enabled: '0', createTime: '2026-06-25 09:00:00' },
  { id: 11, username: 'student006', roleCode: 'owner', roleName: '寄养者', phone: '13900139010', enabled: '1', createTime: '2026-06-25 10:00:00' },
  { id: 12, username: 'caregiver005', roleCode: 'caregiver', roleName: '被寄养者', phone: '13900139011', enabled: '1', createTime: '2026-06-25 11:00:00' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.username) {
      filtered = filtered.filter(item => item.username.includes(searchForm.username))
    }
    if (searchForm.role) {
      filtered = filtered.filter(item => item.roleCode === searchForm.role)
    }
    if (searchForm.status) {
      filtered = filtered.filter(item => item.enabled === searchForm.status)
    }
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = filtered.slice(start, end).map(item => ({
      ...item,
      roleName: roleMap[item.roleCode]
    }))
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.role = ''
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

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  form.id = null
  form.username = ''
  form.roleCode = ''
  form.phone = ''
  form.enabled = '1'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  form.id = row.id
  form.username = row.username
  form.roleCode = row.roleCode
  form.phone = row.phone
  form.enabled = row.enabled
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

const handleResetPassword = (row: any) => {
  ElMessageBox.confirm(`确定要重置用户 "${row.username}" 的密码吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('密码已重置为 123456')
  }).catch(() => {})
}

const handleStatusChange = (row: any) => {
  ElMessage.success(row.enabled === '1' ? '已启用' : '已禁用')
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '提示', {
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
.user-management {
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