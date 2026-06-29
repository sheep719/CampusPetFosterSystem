<template>
  <div class="menu-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="searchForm.menuName" placeholder="请输入菜单名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <h3 class="table-title">菜单列表</h3>
        <div>
          <el-button :icon="Expand" @click="expandAll">展开全部</el-button>
          <el-button :icon="Fold" @click="collapseAll">收起全部</el-button>
          <el-button type="primary" :icon="Plus" @click="handleAdd()">新增菜单</el-button>
        </div>
      </div>
      <el-table
        :data="tableData"
        border
        stripe
        row-key="id"
        :tree-props="{ children: 'children' }"
        ref="tableRef"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="200" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon" :size="18" class="menu-icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="180" />
        <el-table-column prop="component" label="组件路径" min-width="200">
          <template #default="{ row }">
            <el-tag type="info" effect="plain" size="small">{{ row.component || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="visible" label="显示" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === '1' ? 'success' : 'info'" size="small">
              {{ row.visible === '1' ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Plus" size="small" @click="handleAdd(row)">新增子菜单</el-button>
            <el-button type="warning" link :icon="Edit" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTree"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            check-strictly
            placeholder="请选择上级菜单"
            style="width: 100%"
            :render-after-expand="false"
          />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="form.menuType">
            <el-radio value="M">目录</el-radio>
            <el-radio value="C">菜单</el-radio>
            <el-radio value="F">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" v-if="form.menuType !== 'F'">
          <el-select v-model="form.icon" placeholder="请选择图标" style="width: 100%" filterable>
            <el-option v-for="item in iconOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="路由路径" v-if="form.menuType !== 'F'" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="form.menuType === 'C'">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" v-if="form.menuType === 'F'">
          <el-input v-model="form.perms" placeholder="如：system:user:add" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="是否显示" v-if="form.menuType !== 'F'">
          <el-radio-group v-model="form.visible">
            <el-radio value="1">显示</el-radio>
            <el-radio value="0">隐藏</el-radio>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import type { TableInstance } from 'element-plus'
import {
  Search, Refresh, Plus, Edit, Delete, Expand, Fold,
  DataBoard, Goods, User, Setting, Menu, Location, Document
} from '@element-plus/icons-vue'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const tableRef = ref<TableInstance>()
const isEdit = ref(false)

const searchForm = reactive({
  menuName: ''
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  parentId: 0,
  menuType: 'C',
  menuName: '',
  icon: '',
  path: '',
  component: '',
  perms: '',
  sort: 0,
  visible: '1'
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }]
}

const iconOptions = [
  'DataBoard', 'Goods', 'User', 'Setting', 'Menu', 'Location', 'Document',
  'Edit', 'Delete', 'Search', 'Plus', 'Refresh', 'Fold', 'Expand', 'Key'
]

const menuTree = computed(() => {
  return [
    { id: 0, menuName: '顶级菜单', children: tableData.value }
  ]
})

const mockData = [
  {
    id: 1,
    menuName: '数据看板',
    icon: 'DataBoard',
    path: '/dashboard',
    component: 'Dashboard',
    sort: 1,
    visible: '1',
    menuType: 'C',
    children: []
  },
  {
    id: 2,
    menuName: '宠物管理',
    icon: 'Goods',
    path: '/pet',
    component: '',
    sort: 2,
    visible: '1',
    menuType: 'M',
    children: [
      { id: 21, menuName: '宠物列表', icon: '', path: '/pet/list', component: 'PetManagement', sort: 1, visible: '1', menuType: 'C', children: [] },
      { id: 22, menuName: '寄养点管理', icon: '', path: '/pet/location', component: 'FosterLocationManagement', sort: 2, visible: '1', menuType: 'C', children: [] }
    ]
  },
  {
    id: 3,
    menuName: '寄养管理',
    icon: 'Location',
    path: '/foster',
    component: '',
    sort: 3,
    visible: '1',
    menuType: 'M',
    children: [
      { id: 31, menuName: '寄养申请', icon: '', path: '/foster/application', component: 'FosterApplicationManagement', sort: 1, visible: '1', menuType: 'C', children: [] },
      { id: 32, menuName: '寄养记录', icon: '', path: '/foster/record', component: 'FosterRecord', sort: 2, visible: '1', menuType: 'C', children: [] }
    ]
  },
  {
    id: 4,
    menuName: '被寄养者管理',
    icon: 'User',
    path: '/caregiver',
    component: '',
    sort: 4,
    visible: '1',
    menuType: 'M',
    children: [
      { id: 41, menuName: '被寄养者列表', icon: '', path: '/caregiver/list', component: 'CaregiverManagement', sort: 1, visible: '1', menuType: 'C', children: [] },
      { id: 42, menuName: '认证审核', icon: '', path: '/caregiver/audit', component: 'CaregiverAudit', sort: 2, visible: '1', menuType: 'C', children: [] }
    ]
  },
  {
    id: 5,
    menuName: '系统管理',
    icon: 'Setting',
    path: '/system',
    component: '',
    sort: 5,
    visible: '1',
    menuType: 'M',
    children: [
      { id: 51, menuName: '用户管理', icon: '', path: '/system/user', component: 'system/UserManagement', sort: 1, visible: '1', menuType: 'C', children: [] },
      { id: 52, menuName: '角色管理', icon: '', path: '/system/role', component: 'system/RoleManagement', sort: 2, visible: '1', menuType: 'C', children: [] },
      { id: 53, menuName: '菜单管理', icon: '', path: '/system/menu', component: 'system/MenuManagement', sort: 3, visible: '1', menuType: 'C', children: [] },
      { id: 54, menuName: '字典管理', icon: '', path: '/system/dict', component: 'system/DictManagement', sort: 4, visible: '1', menuType: 'C', children: [] }
    ]
  }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let data = JSON.parse(JSON.stringify(mockData))
    if (searchForm.menuName) {
      data = data.filter((item: any) => {
        const matchName = item.menuName.includes(searchForm.menuName)
        const matchChild = item.children?.some((c: any) => c.menuName.includes(searchForm.menuName))
        return matchName || matchChild
      })
    }
    tableData.value = data
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  searchForm.menuName = ''
  loadData()
}

const expandAll = () => {
  tableRef.value?.toggleAllExpansion(true)
}

const collapseAll = () => {
  tableRef.value?.toggleAllExpansion(false)
}

const handleAdd = (row?: any) => {
  isEdit.value = false
  dialogTitle.value = '新增菜单'
  form.id = null
  form.parentId = row?.id || 0
  form.menuType = row ? 'C' : 'M'
  form.menuName = ''
  form.icon = ''
  form.path = ''
  form.component = ''
  form.perms = ''
  form.sort = 0
  form.visible = '1'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑菜单'
  form.id = row.id
  form.parentId = row.parentId || 0
  form.menuType = row.menuType || 'C'
  form.menuName = row.menuName
  form.icon = row.icon || ''
  form.path = row.path || ''
  form.component = row.component || ''
  form.perms = row.perms || ''
  form.sort = row.sort || 0
  form.visible = row.visible || '1'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

const handleDelete = (row: any) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('存在子菜单，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除菜单 "${row.menuName}" 吗？`, '提示', {
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
.menu-management {
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

.menu-icon {
  color: #606266;
}
</style>