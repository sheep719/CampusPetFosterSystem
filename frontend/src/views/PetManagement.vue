<template>
  <div class="pet-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.petName" placeholder="请输入宠物名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="宠物种类">
          <el-select v-model="searchForm.species" placeholder="全部" clearable style="width: 120px">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="仓鼠" value="hamster" />
            <el-option label="兔子" value="rabbit" />
            <el-option label="鸟" value="bird" />
            <el-option label="乌龟" value="turtle" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="searchForm.gender" placeholder="全部" clearable style="width: 100px">
            <el-option label="公" value="1" />
            <el-option label="母" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康状态">
          <el-select v-model="searchForm.healthStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="健康" value="healthy" />
            <el-option label="亚健康" value="sub_healthy" />
            <el-option label="生病" value="sick" />
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
        <h3 class="table-title">宠物档案列表</h3>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增宠物档案</el-button>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="petName" label="宠物名称" min-width="120" />
        <el-table-column prop="species" label="宠物种类" width="100">
          <template #default="{ row }">
            <el-tag :type="getSpeciesTagType(row.species)" effect="light">{{ getSpeciesLabel(row.species) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="breed" label="品种" min-width="120" />
        <el-table-column prop="gender" label="性别" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.gender === '1' ? 'info' : 'warning'" effect="plain">
              {{ row.gender === '1' ? '公' : '母' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="age" label="年龄" width="80" align="center">
          <template #default="{ row }">{{ row.age }}岁</template>
        </el-table-column>
        <el-table-column prop="weight" label="体重" width="80" align="center">
          <template #default="{ row }">{{ row.weight }}kg</template>
        </el-table-column>
        <el-table-column prop="healthStatus" label="健康状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getHealthTagType(row.healthStatus)" effect="light">
              {{ getHealthLabel(row.healthStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="主人" width="100" />
        <el-table-column prop="ownerPhone" label="联系方式" width="130" />
        <el-table-column prop="createTime" label="建档时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
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
        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="form.petName" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="宠物种类" prop="species">
          <el-select v-model="form.species" placeholder="请选择宠物种类" style="width: 100%">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="仓鼠" value="hamster" />
            <el-option label="兔子" value="rabbit" />
            <el-option label="鸟" value="bird" />
            <el-option label="乌龟" value="turtle" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio value="1">公</el-radio>
            <el-radio value="2">母</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="0" :max="30" style="width: 100%" />
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="0" :max="200" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="健康状态">
          <el-select v-model="form.healthStatus" placeholder="请选择健康状态" style="width: 100%">
            <el-option label="健康" value="healthy" />
            <el-option label="亚健康" value="sub_healthy" />
            <el-option label="生病" value="sick" />
          </el-select>
        </el-form-item>
        <el-form-item label="免疫情况">
          <el-checkbox-group v-model="form.vaccinations">
            <el-checkbox label="狂犬疫苗" value="rabies" />
            <el-checkbox label="猫三联/犬六联" value="combined" />
            <el-checkbox label="驱虫" value="deworming" />
            <el-checkbox label="绝育" value="neutering" />
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="特殊说明">
          <el-input
            v-model="form.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入宠物的特殊习性、饮食禁忌等说明"
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
      title="宠物档案详情"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="宠物名称">{{ detailData.petName }}</el-descriptions-item>
        <el-descriptions-item label="宠物种类">
          <el-tag :type="getSpeciesTagType(detailData.species)" effect="light">
            {{ getSpeciesLabel(detailData.species) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="品种">{{ detailData.breed }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <el-tag :type="detailData.gender === '1' ? 'info' : 'warning'" effect="plain">
            {{ detailData.gender === '1' ? '公' : '母' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detailData.age }}岁</el-descriptions-item>
        <el-descriptions-item label="体重">{{ detailData.weight }}kg</el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag :type="getHealthTagType(detailData.healthStatus)" effect="light">
            {{ getHealthLabel(detailData.healthStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="建档时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="主人" :span="2">{{ detailData.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式" :span="2">{{ detailData.ownerPhone }}</el-descriptions-item>
        <el-descriptions-item label="特殊说明" :span="2">{{ detailData.notes || '-' }}</el-descriptions-item>
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
import { getPetList, createPet, updatePet, deletePet } from '@/api/pet'
import type { Pet } from '@/api/pet'

const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const isEdit = ref(false)

const searchForm = reactive({
  petName: '',
  species: '',
  gender: '',
  healthStatus: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const form = reactive({
  id: null as number | null,
  petName: '',
  species: '',
  breed: '',
  gender: '1',
  age: 0,
  weight: 0,
  healthStatus: 'healthy',
  vaccinations: [] as string[],
  notes: ''
})

const detailData = reactive({
  petName: '',
  species: '',
  breed: '',
  gender: '',
  age: 0,
  weight: 0,
  healthStatus: '',
  ownerName: '',
  ownerPhone: '',
  createTime: '',
  notes: ''
})

const rules: FormRules = {
  petName: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  species: [{ required: true, message: '请选择宠物种类', trigger: 'change' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }]
}

const speciesMap: Record<string, string> = {
  cat: '猫',
  dog: '狗',
  hamster: '仓鼠',
  rabbit: '兔子',
  bird: '鸟',
  turtle: '乌龟'
}

const healthMap: Record<string, string> = {
  healthy: '健康',
  sub_healthy: '亚健康',
  sick: '生病'
}

const getSpeciesLabel = (species: string) => speciesMap[species] || species
const getHealthLabel = (status: string) => healthMap[status] || status

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

const getHealthTagType = (status: string) => {
  const map: Record<string, string> = {
    healthy: 'success',
    sub_healthy: 'warning',
    sick: 'danger'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getPetList({
      page: pagination.page,
      size: pagination.pageSize,
      petName: searchForm.petName || undefined,
      species: searchForm.species || undefined
    })
    pagination.total = res.total
    tableData.value = res.list.map((item: Pet) => ({
      ...item,
      weight: 0,
      healthStatus: item.vaccineStatus === '已完成疫苗接种' ? 'healthy' : 'sub_healthy',
      ownerName: '主人' + item.ownerId,
      ownerPhone: '-',
      createTime: '-',
      notes: item.healthNote || item.dietNote || '-'
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
  searchForm.petName = ''
  searchForm.species = ''
  searchForm.gender = ''
  searchForm.healthStatus = ''
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
  dialogTitle.value = '新增宠物档案'
  form.id = null
  form.petName = ''
  form.species = ''
  form.breed = ''
  form.gender = '1'
  form.age = 0
  form.weight = 0
  form.healthStatus = 'healthy'
  form.vaccinations = []
  form.notes = ''
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑宠物档案'
  form.id = row.id
  form.petName = row.petName
  form.species = row.species
  form.breed = row.breed
  form.gender = row.gender
  form.age = row.age
  form.weight = row.weight
  form.healthStatus = row.healthStatus
  form.vaccinations = []
  form.notes = row.notes
  dialogVisible.value = true
}

const handleView = (row: any) => {
  detailData.petName = row.petName
  detailData.species = row.species
  detailData.breed = row.breed
  detailData.gender = row.gender
  detailData.age = row.age
  detailData.weight = row.weight
  detailData.healthStatus = row.healthStatus
  detailData.ownerName = row.ownerName
  detailData.ownerPhone = row.ownerPhone
  detailData.createTime = row.createTime
  detailData.notes = row.notes
  detailVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  try {
    const data = {
      petName: form.petName,
      species: form.species,
      breed: form.breed,
      gender: form.gender === '1' ? 'male' : 'female',
      age: form.age,
      healthNote: form.notes
    }
    if (isEdit.value && form.id) {
      await updatePet(form.id, data)
      ElMessage.success('修改成功')
    } else {
      await createPet(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除宠物 "${row.petName}" 的档案吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePet(row.id)
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
.pet-management {
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