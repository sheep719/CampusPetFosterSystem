<template>
  <div class="foster-location-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="寄养点名称">
          <el-input v-model="searchForm.locationName" placeholder="请输入寄养点名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="searchForm.address" placeholder="请输入地址" clearable style="width: 250px" />
        </el-form-item>
        <el-form-item label="接受种类">
          <el-select v-model="searchForm.acceptedSpecies" placeholder="全部" clearable style="width: 150px">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="仓鼠" value="hamster" />
            <el-option label="兔子" value="rabbit" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.available" placeholder="全部" clearable style="width: 120px">
            <el-option label="可接单" value="1" />
            <el-option label="不可接单" value="0" />
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
        <h3 class="table-title">寄养点列表</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增寄养点</el-button>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="locationName" label="寄养点名称" min-width="150" />
        <el-table-column prop="caregiverName" label="负责人" width="100" />
        <el-table-column prop="caregiverPhone" label="联系电话" width="130" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="distance" label="距离(km)" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #409EFF; font-weight: 600">{{ row.distance }}km</span>
          </template>
        </el-table-column>
        <el-table-column prop="capacity" label="容量" width="80" align="center">
          <template #default="{ row }">
            <el-progress :percentage="(row.currentCount / row.capacity) * 100" :stroke-width="14" :width="80" />
          </template>
        </el-table-column>
        <el-table-column prop="availableCount" label="剩余容量" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.availableCount > 0 ? 'success' : 'danger'" effect="light">
              {{ row.availableCount }}/{{ row.capacity }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="acceptedSpecies" label="接受种类" width="150">
          <template #default="{ row }">
            <el-tag
              v-for="species in row.acceptedSpeciesList"
              :key="species.value"
              :type="getSpeciesTagType(species.value)"
              effect="plain"
              size="small"
              style="margin-right: 4px"
            >
              {{ species.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格(元/天)" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #E6A23C; font-weight: 600">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="100" align="center">
          <template #default="{ row }">
            <el-rate :model-value="row.rating" disabled :show-score="true" text-color="#FF9900" />
          </template>
        </el-table-column>
        <el-table-column prop="available" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.available"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(row)"
              :active-text="row.available === '1' ? '可接单' : '不可接单'"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="warning" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
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
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="寄养点名称" prop="locationName">
          <el-input v-model="form.locationName" placeholder="请输入寄养点名称" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.caregiverName" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.caregiverPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="距离学校(km)">
          <el-input-number v-model="form.distance" :min="0" :max="50" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大容量">
          <el-input-number v-model="form.capacity" :min="1" :max="50" style="width: 100%" />
        </el-form-item>
        <el-form-item label="接受种类">
          <el-checkbox-group v-model="form.acceptedSpecies">
            <el-checkbox label="猫" value="cat" />
            <el-checkbox label="狗" value="dog" />
            <el-checkbox label="仓鼠" value="hamster" />
            <el-checkbox label="兔子" value="rabbit" />
            <el-checkbox label="鸟" value="bird" />
            <el-checkbox label="乌龟" value="turtle" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="价格(元/天)">
          <el-input-number v-model="form.price" :min="0" :max="1000" :step="10" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.available">
            <el-radio value="1">可接单</el-radio>
            <el-radio value="0">不可接单</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="寄养点介绍">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入寄养点环境介绍、设施情况等"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="detailVisible"
      title="寄养点详情"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="寄养点名称">{{ detailData.locationName }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ detailData.caregiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.caregiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="距离学校">{{ detailData.distance }}km</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="容量">{{ detailData.currentCount }}/{{ detailData.capacity }}</el-descriptions-item>
        <el-descriptions-item label="价格">
          <span style="color: #E6A23C; font-weight: 600">¥{{ detailData.price }}/天</span>
        </el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate :model-value="detailData.rating" disabled text-color="#FF9900" />
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.available === '1' ? 'success' : 'danger'">
            {{ detailData.available === '1' ? '可接单' : '不可接单' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="接受种类" :span="2">
          <el-tag
            v-for="species in detailData.acceptedSpeciesList"
            :key="species.value"
            :type="getSpeciesTagType(species.value)"
            effect="plain"
            size="small"
            style="margin-right: 4px"
          >
            {{ species.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="介绍" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import { getLocationList, createLocation, updateLocation, deleteLocation } from '@/api/fosterLocation'
import type { FosterLocation } from '@/api/fosterLocation'

const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const searchForm = reactive({
  locationName: '',
  address: '',
  acceptedSpecies: '',
  available: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  locationName: '',
  caregiverName: '',
  caregiverPhone: '',
  address: '',
  distance: 0,
  capacity: 3,
  acceptedSpecies: [] as string[],
  price: 50,
  available: '1',
  description: ''
})

const detailData = reactive({
  locationName: '',
  caregiverName: '',
  caregiverPhone: '',
  address: '',
  distance: 0,
  capacity: 0,
  currentCount: 0,
  acceptedSpeciesList: [] as any[],
  price: 0,
  rating: 0,
  available: '',
  description: ''
})

const rules: FormRules = {
  locationName: [{ required: true, message: '请输入寄养点名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const speciesMap: Record<string, string> = {
  cat: '猫',
  dog: '狗',
  hamster: '仓鼠',
  rabbit: '兔子',
  bird: '鸟',
  turtle: '乌龟'
}

const getSpeciesTagType = (species: string) => {
  const map: Record<string, string> = {
    cat: 'success',
    dog: 'warning',
    hamster: 'info',
    rabbit: 'primary',
    bird: 'danger',
    turtle: 'success'
  }
  return map[species] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLocationList({
      page: pagination.page,
      size: pagination.pageSize,
      locationName: searchForm.locationName || undefined,
      address: searchForm.address || undefined,
      species: searchForm.acceptedSpecies || undefined,
      available: searchForm.available ? Number(searchForm.available) : undefined
    })
    pagination.total = res.total
    tableData.value = res.list.map((item: FosterLocation) => ({
      ...item,
      distance: item.distanceKm || 0,
      price: 50,
      rating: 4.5,
      currentCount: 0,
      caregiverName: '负责人' + item.caregiverId,
      caregiverPhone: '-',
      availableCount: item.capacity - 0,
      acceptedSpecies: item.acceptedSpecies ? item.acceptedSpecies.split(',') : [],
      acceptedSpeciesList: (item.acceptedSpecies ? item.acceptedSpecies.split(',') : []).map((s: string) => ({
        value: s,
        label: speciesMap[s] || s
      })),
      description: item.costDesc || '-',
      available: item.available ? '1' : '0'
    }))
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.locationName = ''
  searchForm.address = ''
  searchForm.acceptedSpecies = ''
  searchForm.available = ''
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
  dialogTitle.value = '新增寄养点'
  form.id = null
  form.locationName = ''
  form.caregiverName = ''
  form.caregiverPhone = ''
  form.address = ''
  form.distance = 0
  form.capacity = 3
  form.acceptedSpecies = []
  form.price = 50
  form.available = '1'
  form.description = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑寄养点'
  form.id = row.id
  form.locationName = row.locationName
  form.caregiverName = row.caregiverName
  form.caregiverPhone = row.caregiverPhone
  form.address = row.address
  form.distance = row.distance
  form.capacity = row.capacity
  form.acceptedSpecies = row.acceptedSpecies
  form.price = row.price
  form.available = row.available
  form.description = row.description
  dialogVisible.value = true
}

const handleView = (row: any) => {
  detailData.locationName = row.locationName
  detailData.caregiverName = row.caregiverName
  detailData.caregiverPhone = row.caregiverPhone
  detailData.address = row.address
  detailData.distance = row.distance
  detailData.capacity = row.capacity
  detailData.currentCount = row.currentCount
  detailData.acceptedSpeciesList = row.acceptedSpeciesList
  detailData.price = row.price
  detailData.rating = row.rating
  detailData.available = row.available
  detailData.description = row.description
  detailVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    const data = {
      locationName: form.locationName,
      address: form.address,
      distanceKm: form.distance,
      capacity: form.capacity,
      acceptedSpecies: form.acceptedSpecies.join(','),
      carePeriod: '长期',
      costDesc: form.description,
      available: form.available === '1' ? 1 : 0
    }
    if (isEdit.value && form.id) {
      await updateLocation(form.id, data)
      ElMessage.success('修改成功')
    } else {
      await createLocation(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleStatusChange = async (row: any) => {
  try {
    await updateLocation(row.id, { available: row.available === '1' ? 1 : 0 })
    ElMessage.success(row.available === '1' ? '已开启接单' : '已关闭接单')
  } catch (error) {
    row.available = row.available === '1' ? '0' : '1'
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  if (row.currentCount > 0) {
    ElMessage.warning('该寄养点还有宠物在寄养，无法删除')
    return
  }
  ElMessageBox.confirm(`确定要删除寄养点 "${row.locationName}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteLocation(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.foster-location-management {
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