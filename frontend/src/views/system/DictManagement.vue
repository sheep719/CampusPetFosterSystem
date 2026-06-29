<template>
  <div class="dict-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="字典名称">
          <el-input v-model="searchForm.dictName" placeholder="请输入字典名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="字典类型">
          <el-input v-model="searchForm.dictCode" placeholder="请输入字典类型" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="8">
        <el-card class="table-card" shadow="never">
          <div class="table-header">
            <h3 class="table-title">字典列表</h3>
            <el-button type="primary" :icon="Plus" size="small" @click="handleAddDict">新增</el-button>
          </div>
          <el-table
            :data="dictTableData"
            border
            style="width: 100%"
            v-loading="loading"
            highlight-current-row
            @row-click="handleDictClick"
            :class="['dict-table', { 'dict-detail-open': currentDict }]"
          >
            <el-table-column prop="dictName" label="字典名称" min-width="120" />
            <el-table-column prop="dictCode" label="字典类型" min-width="120">
              <template #default="{ row }">
                <el-tag type="info" effect="plain" size="small">{{ row.dictCode }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="130" align="center">
              <template #default="{ row }">
                <el-button type="primary" link size="small" :icon="Edit" @click.stop="handleEditDict(row)">编辑</el-button>
                <el-button type="danger" link size="small" :icon="Delete" @click.stop="handleDeleteDict(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-wrapper">
            <el-pagination
              v-model:current-page="dictPagination.page"
              v-model:page-size="dictPagination.pageSize"
              :page-sizes="[10, 20, 50]"
              :total="dictPagination.total"
              layout="total, prev, pager, next"
              small
              @size-change="handleDictSizeChange"
              @current-change="handleDictPageChange"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card class="table-card" shadow="never" v-loading="dictDataLoading">
          <template v-if="currentDict">
            <div class="table-header">
              <h3 class="table-title">
                <span style="color: #409EFF; cursor: pointer" @click="currentDict = null">字典列表</span>
                <el-icon style="margin: 0 8px; color: #c0c4cc"><ArrowRight /></el-icon>
                {{ currentDict.dictName }}
                <el-tag type="info" effect="plain" style="margin-left: 10px">{{ currentDict.dictCode }}</el-tag>
              </h3>
              <el-button type="primary" :icon="Plus" size="small" @click="handleAddDictItem">新增字典项</el-button>
            </div>
            <el-table :data="dictItemTableData" border stripe style="width: 100%">
              <el-table-column type="index" label="序号" width="60" align="center" />
              <el-table-column prop="itemLabel" label="字典标签" min-width="150" />
              <el-table-column prop="itemValue" label="字典键值" min-width="120">
                <template #default="{ row }">
                  <el-tag type="success" effect="plain" size="small">{{ row.itemValue }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="80" align="center" />
              <el-table-column prop="status" label="状态" width="80" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === '1' ? 'success' : 'danger'" size="small">
                    {{ row.status === '1' ? '启用' : '停用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="150" />
              <el-table-column label="操作" width="180" fixed="right" align="center">
                <template #default="{ row }">
                  <el-button type="primary" link size="small" :icon="Edit" @click="handleEditDictItem(row)">编辑</el-button>
                  <el-button type="danger" link size="small" :icon="Delete" @click="handleDeleteDictItem(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination-wrapper">
              <el-pagination
                v-model:current-page="dictItemPagination.page"
                v-model:page-size="dictItemPagination.pageSize"
                :page-sizes="[10, 20, 50]"
                :total="dictItemPagination.total"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleDictItemSizeChange"
                @current-change="handleDictItemPageChange"
              />
            </div>
          </template>
          <template v-else>
            <el-empty description="请选择左侧字典查看详情" />
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="dictDialogVisible"
      :title="dictDialogTitle"
      width="450px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="dictForm" :rules="dictRules" ref="dictFormRef" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="dictForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictCode">
          <el-input v-model="dictForm.dictCode" placeholder="请输入字典类型" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="dictForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDict">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dictItemDialogVisible"
      :title="dictItemDialogTitle"
      width="450px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="dictItemForm" :rules="dictItemRules" ref="dictItemFormRef" label-width="80px">
        <el-form-item label="字典标签" prop="itemLabel">
          <el-input v-model="dictItemForm.itemLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="字典键值" prop="itemValue">
          <el-input v-model="dictItemForm.itemValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dictItemForm.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dictItemForm.status">
            <el-radio value="1">启用</el-radio>
            <el-radio value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="dictItemForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictItemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDictItem">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, ArrowRight } from '@element-plus/icons-vue'

const loading = ref(false)
const dictDataLoading = ref(false)
const dictDialogVisible = ref(false)
const dictItemDialogVisible = ref(false)
const dictDialogTitle = ref('')
const dictItemDialogTitle = ref('')
const dictFormRef = ref<FormInstance>()
const dictItemFormRef = ref<FormInstance>()
const isDictEdit = ref(false)
const isDictItemEdit = ref(false)
const currentDict = ref<any>(null)

const searchForm = reactive({
  dictName: '',
  dictCode: ''
})

const dictPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const dictItemPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const dictTableData = ref<any[]>([])
const dictItemTableData = ref<any[]>([])

const dictForm = reactive({
  id: null as number | null,
  dictName: '',
  dictCode: '',
  remark: ''
})

const dictItemForm = reactive({
  id: null as number | null,
  dictId: null as number | null,
  itemLabel: '',
  itemValue: '',
  sort: 0,
  status: '1',
  remark: ''
})

const dictRules: FormRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictCode: [{ required: true, message: '请输入字典类型', trigger: 'blur' }]
}

const dictItemRules: FormRules = {
  itemLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  itemValue: [{ required: true, message: '请输入字典键值', trigger: 'blur' }]
}

const mockDictData = [
  { id: 1, dictName: '宠物种类', dictCode: 'pet_species', remark: '宠物种类字典' },
  { id: 2, dictName: '寄养状态', dictCode: 'foster_status', remark: '寄养申请状态字典' },
  { id: 3, dictName: '健康状态', dictCode: 'health_status', remark: '宠物健康状态字典' },
  { id: 4, dictName: '评价等级', dictCode: 'rating_level', remark: '评价等级字典' },
  { id: 5, dictName: '用户状态', dictCode: 'user_status', remark: '用户状态字典' },
  { id: 6, dictName: '性别', dictCode: 'gender', remark: '性别字典' },
  { id: 7, dictName: '审批状态', dictCode: 'audit_status', remark: '审批状态字典' }
]

const mockDictItemData: Record<number, any[]> = {
  1: [
    { id: 101, itemLabel: '猫', itemValue: 'cat', sort: 1, status: '1', remark: '猫咪' },
    { id: 102, itemLabel: '狗', itemValue: 'dog', sort: 2, status: '1', remark: '狗狗' },
    { id: 103, itemLabel: '仓鼠', itemValue: 'hamster', sort: 3, status: '1', remark: '仓鼠' },
    { id: 104, itemLabel: '兔子', itemValue: 'rabbit', sort: 4, status: '1', remark: '兔子' },
    { id: 105, itemLabel: '鸟', itemValue: 'bird', sort: 5, status: '0', remark: '鸟类' },
    { id: 106, itemLabel: '乌龟', itemValue: 'turtle', sort: 6, status: '1', remark: '乌龟' }
  ],
  2: [
    { id: 201, itemLabel: '待审核', itemValue: 'pending', sort: 1, status: '1', remark: '' },
    { id: 202, itemLabel: '已通过', itemValue: 'approved', sort: 2, status: '1', remark: '' },
    { id: 203, itemLabel: '已拒绝', itemValue: 'rejected', sort: 3, status: '1', remark: '' },
    { id: 204, itemLabel: '寄养中', itemValue: 'fostering', sort: 4, status: '1', remark: '' },
    { id: 205, itemLabel: '已完成', itemValue: 'completed', sort: 5, status: '1', remark: '' },
    { id: 206, itemLabel: '已取消', itemValue: 'cancelled', sort: 6, status: '1', remark: '' }
  ],
  3: [
    { id: 301, itemLabel: '健康', itemValue: 'healthy', sort: 1, status: '1', remark: '' },
    { id: 302, itemLabel: '亚健康', itemValue: 'sub_healthy', sort: 2, status: '1', remark: '' },
    { id: 303, itemLabel: '生病', itemValue: 'sick', sort: 3, status: '1', remark: '' }
  ],
  4: [
    { id: 401, itemLabel: '非常满意', itemValue: '5', sort: 1, status: '1', remark: '五星好评' },
    { id: 402, itemLabel: '满意', itemValue: '4', sort: 2, status: '1', remark: '四星' },
    { id: 403, itemLabel: '一般', itemValue: '3', sort: 3, status: '1', remark: '三星' },
    { id: 404, itemLabel: '不满意', itemValue: '2', sort: 4, status: '1', remark: '二星' },
    { id: 405, itemLabel: '非常不满意', itemValue: '1', sort: 5, status: '1', remark: '一星' }
  ],
  5: [
    { id: 501, itemLabel: '启用', itemValue: '1', sort: 1, status: '1', remark: '' },
    { id: 502, itemLabel: '禁用', itemValue: '0', sort: 2, status: '1', remark: '' }
  ],
  6: [
    { id: 601, itemLabel: '男', itemValue: '1', sort: 1, status: '1', remark: '' },
    { id: 602, itemLabel: '女', itemValue: '2', sort: 2, status: '1', remark: '' },
    { id: 603, itemLabel: '未知', itemValue: '0', sort: 3, status: '1', remark: '' }
  ],
  7: [
    { id: 701, itemLabel: '待审批', itemValue: 'pending', sort: 1, status: '1', remark: '' },
    { id: 702, itemLabel: '已通过', itemValue: 'passed', sort: 2, status: '1', remark: '' },
    { id: 703, itemLabel: '已驳回', itemValue: 'rejected', sort: 3, status: '1', remark: '' }
  ]
}

const loadDictData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockDictData]
    if (searchForm.dictName) {
      filtered = filtered.filter(item => item.dictName.includes(searchForm.dictName))
    }
    if (searchForm.dictCode) {
      filtered = filtered.filter(item => item.dictCode.includes(searchForm.dictCode))
    }
    dictPagination.total = filtered.length
    const start = (dictPagination.page - 1) * dictPagination.pageSize
    const end = start + dictPagination.pageSize
    dictTableData.value = filtered.slice(start, end)
    loading.value = false
  }, 300)
}

const loadDictItemData = () => {
  if (!currentDict.value) return
  dictDataLoading.value = true
  setTimeout(() => {
    const items = mockDictItemData[currentDict.value.id] || []
    dictItemPagination.total = items.length
    const start = (dictItemPagination.page - 1) * dictItemPagination.pageSize
    const end = start + dictItemPagination.pageSize
    dictItemTableData.value = items.slice(start, end)
    dictDataLoading.value = false
  }, 200)
}

const handleSearch = () => {
  dictPagination.page = 1
  loadDictData()
}

const handleReset = () => {
  searchForm.dictName = ''
  searchForm.dictCode = ''
  dictPagination.page = 1
  loadDictData()
}

const handleDictSizeChange = (size: number) => {
  dictPagination.pageSize = size
  loadDictData()
}

const handleDictPageChange = (page: number) => {
  dictPagination.page = page
  loadDictData()
}

const handleDictItemSizeChange = (size: number) => {
  dictItemPagination.pageSize = size
  loadDictItemData()
}

const handleDictItemPageChange = (page: number) => {
  dictItemPagination.page = page
  loadDictItemData()
}

const handleDictClick = (row: any) => {
  currentDict.value = row
  dictItemPagination.page = 1
  loadDictItemData()
}

const handleAddDict = () => {
  isDictEdit.value = false
  dictDialogTitle.value = '新增字典'
  dictForm.id = null
  dictForm.dictName = ''
  dictForm.dictCode = ''
  dictForm.remark = ''
  dictDialogVisible.value = true
}

const handleEditDict = (row: any) => {
  isDictEdit.value = true
  dictDialogTitle.value = '编辑字典'
  dictForm.id = row.id
  dictForm.dictName = row.dictName
  dictForm.dictCode = row.dictCode
  dictForm.remark = row.remark
  dictDialogVisible.value = true
}

const handleSubmitDict = async () => {
  await dictFormRef.value?.validate()
  ElMessage.success(isDictEdit.value ? '修改成功' : '新增成功')
  dictDialogVisible.value = false
  loadDictData()
}

const handleDeleteDict = (row: any) => {
  ElMessageBox.confirm(`确定要删除字典 "${row.dictName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    if (currentDict.value?.id === row.id) {
      currentDict.value = null
    }
    loadDictData()
  }).catch(() => {})
}

const handleAddDictItem = () => {
  isDictItemEdit.value = false
  dictItemDialogTitle.value = '新增字典项'
  dictItemForm.id = null
  dictItemForm.dictId = currentDict.value?.id
  dictItemForm.itemLabel = ''
  dictItemForm.itemValue = ''
  dictItemForm.sort = 0
  dictItemForm.status = '1'
  dictItemForm.remark = ''
  dictItemDialogVisible.value = true
}

const handleEditDictItem = (row: any) => {
  isDictItemEdit.value = true
  dictItemDialogTitle.value = '编辑字典项'
  dictItemForm.id = row.id
  dictItemForm.itemLabel = row.itemLabel
  dictItemForm.itemValue = row.itemValue
  dictItemForm.sort = row.sort
  dictItemForm.status = row.status
  dictItemForm.remark = row.remark
  dictItemDialogVisible.value = true
}

const handleSubmitDictItem = async () => {
  await dictItemFormRef.value?.validate()
  ElMessage.success(isDictItemEdit.value ? '修改成功' : '新增成功')
  dictItemDialogVisible.value = false
  loadDictItemData()
}

const handleDeleteDictItem = (row: any) => {
  ElMessageBox.confirm(`确定要删除字典项 "${row.itemLabel}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('删除成功')
    loadDictItemData()
  }).catch(() => {})
}

onMounted(() => {
  loadDictData()
})
</script>

<style scoped>
.dict-management {
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
  min-height: 500px;
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
  display: flex;
  align-items: center;
}

.dict-table .el-table__row {
  cursor: pointer;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

:deep(.el-card__body) {
  height: 100%;
}
</style>