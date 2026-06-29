<template>
  <div class="owner-pets">
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
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="pet-list">
      <el-card
        v-for="item in tableData"
        :key="item.id"
        class="pet-card"
        shadow="hover"
      >
        <div class="pet-header">
          <div class="pet-avatar">
            <el-icon :size="56" class="avatar-icon">
              <component :is="getSpeciesIcon(item.species)" />
            </el-icon>
          </div>
          <div class="pet-info">
            <h3 class="pet-name">{{ item.petName }}</h3>
            <div class="pet-meta">
              <el-tag :type="getSpeciesTagType(item.species)" effect="light">{{ getSpeciesLabel(item.species) }}</el-tag>
              <span>{{ item.breed }}</span>
            </div>
          </div>
          <div class="pet-stats">
            <div class="stat-item">
              <span class="stat-value">{{ item.age }}</span>
              <span class="stat-label">岁</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ item.weight }}</span>
              <span class="stat-label">kg</span>
            </div>
          </div>
        </div>
        <div class="pet-details">
          <div class="detail-row">
            <span class="detail-label">性别</span>
            <span class="detail-value">{{ item.gender === '1' ? '公' : '母' }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">健康状态</span>
            <el-tag :type="getHealthTagType(item.healthStatus)" effect="light">
              {{ getHealthLabel(item.healthStatus) }}
            </el-tag>
          </div>
          <div class="detail-row">
            <span class="detail-label">免疫情况</span>
            <div class="vaccinations">
              <el-tag
                v-for="v in item.vaccinations"
                :key="v"
                type="success"
                effect="plain"
                size="small"
              >
                {{ getVaccinationLabel(v) }}
              </el-tag>
            </div>
          </div>
          <div class="detail-row" v-if="item.notes">
            <span class="detail-label">特殊说明</span>
            <span class="detail-value">{{ item.notes }}</span>
          </div>
        </div>
        <div class="pet-actions">
          <el-button type="primary" :icon="Edit" @click="handleEdit(item)">编辑</el-button>
          <el-button type="success" :icon="Calendar" @click="handleApply(item)">发起寄养</el-button>
          <el-button type="danger" link :icon="Delete" @click="handleDelete(item)">删除</el-button>
        </div>
      </el-card>
    </div>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
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
      v-model="applyVisible"
      title="发起寄养申请"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="90px">
        <el-form-item label="选择被寄养者" prop="caregiverId">
          <el-select v-model="applyForm.caregiverId" placeholder="请选择被寄养者" style="width: 100%">
            <el-option v-for="c in caregiverList" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="寄养开始日期" prop="startDate">
          <el-date-picker v-model="applyForm.startDate" type="date" placeholder="请选择开始日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="寄养结束日期" prop="endDate">
          <el-date-picker v-model="applyForm.endDate" type="date" placeholder="请选择结束日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="寄养天数">
          <el-input-number :model-value="applyDays" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="预估费用">
          <span style="color: #E6A23C; font-weight: 600; font-size: 16px">¥{{ estimatedPrice }}</span>
        </el-form-item>
        <el-form-item label="宠物说明">
          <el-input
            v-model="applyForm.notes"
            type="textarea"
            :rows="3"
            placeholder="请输入宠物的特殊习性、饮食禁忌等说明"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Edit, Delete, Calendar, User, HomeFilled, MapLocation, Document } from '@element-plus/icons-vue'
import type { Component } from 'vue'

const loading = ref(false)
const dialogVisible = ref(false)
const applyVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const applyFormRef = ref<FormInstance>()
const isEdit = ref(false)
const currentPet = ref<any>(null)

const searchForm = reactive({
  petName: '',
  species: ''
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

const applyForm = reactive({
  caregiverId: null as number | null,
  startDate: '',
  endDate: '',
  notes: ''
})

const applyRules: FormRules = {
  caregiverId: [{ required: true, message: '请选择被寄养者', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const rules: FormRules = {
  petName: [{ required: true, message: '请输入宠物名称', trigger: 'blur' }],
  species: [{ required: true, message: '请选择宠物种类', trigger: 'change' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }]
}

const caregiverList = ref([
  { id: 1, name: '李阿姨', price: 60 },
  { id: 2, name: '张叔叔', price: 50 },
  { id: 3, name: '王奶奶', price: 30 },
  { id: 4, name: '陈阿姨', price: 55 },
  { id: 5, name: '刘大哥', price: 70 },
  { id: 6, name: '孙阿姨', price: 45 }
])

const applyDays = computed(() => {
  if (!applyForm.startDate || !applyForm.endDate) return 0
  const start = new Date(applyForm.startDate)
  const end = new Date(applyForm.endDate)
  return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1
})

const estimatedPrice = computed(() => {
  const caregiver = caregiverList.value.find(c => c.id === applyForm.caregiverId)
  if (!caregiver || applyDays.value <= 0) return 0
  return applyDays.value * caregiver.price
})

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

const vaccinationMap: Record<string, string> = {
  rabies: '狂犬疫苗',
  combined: '猫三联/犬六联',
  deworming: '驱虫',
  neutering: '绝育'
}

const speciesIconMap: Record<string, Component> = {
  cat: User,
  dog: HomeFilled,
  rabbit: MapLocation,
  bird: Document,
  hamster: MapLocation,
  turtle: MapLocation
}

const getSpeciesLabel = (species: string) => speciesMap[species] || species
const getHealthLabel = (status: string) => healthMap[status] || status
const getVaccinationLabel = (v: string) => vaccinationMap[v] || v
const getSpeciesIcon = (species: string) => speciesIconMap[species] || User

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

const mockData = [
  { id: 1, petName: '咪咪', species: 'cat', breed: '英短', gender: '2', age: 2, weight: 3.5, healthStatus: 'healthy', vaccinations: ['rabies', 'combined'], notes: '性格温顺，喜欢吃猫粮' },
  { id: 2, petName: '旺财', species: 'dog', breed: '金毛', gender: '1', age: 3, weight: 25, healthStatus: 'healthy', vaccinations: ['rabies', 'combined', 'deworming'], notes: '活泼好动，每天需要遛两次' },
  { id: 3, petName: '雪球', species: 'rabbit', breed: '垂耳兔', gender: '2', age: 1, weight: 1.8, healthStatus: 'healthy', vaccinations: [], notes: '胆小，需要安静环境' },
  { id: 4, petName: '小仓鼠', species: 'hamster', breed: '金丝熊', gender: '1', age: 0.5, weight: 0.15, healthStatus: 'sub_healthy', vaccinations: [], notes: '最近有点食欲不振' },
  { id: 5, petName: '橘子', species: 'cat', breed: '橘猫', gender: '1', age: 4, weight: 5.2, healthStatus: 'healthy', vaccinations: ['rabies', 'combined', 'neutering'], notes: '爱吃，容易长胖' },
  { id: 6, petName: '可乐', species: 'dog', breed: '柯基', gender: '2', age: 2, weight: 12, healthStatus: 'healthy', vaccinations: ['rabies', 'combined'], notes: '短腿，跑不快' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.petName) {
      filtered = filtered.filter(item => item.petName.includes(searchForm.petName))
    }
    if (searchForm.species) {
      filtered = filtered.filter(item => item.species === searchForm.species)
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
  searchForm.petName = ''
  searchForm.species = ''
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

const handleEdit = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑宠物'
  form.id = row.id
  form.petName = row.petName
  form.species = row.species
  form.breed = row.breed
  form.gender = row.gender
  form.age = row.age
  form.weight = row.weight
  form.healthStatus = row.healthStatus
  form.vaccinations = [...row.vaccinations]
  form.notes = row.notes
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

const handleApply = (row: any) => {
  currentPet.value = row
  applyForm.caregiverId = null
  applyForm.startDate = ''
  applyForm.endDate = ''
  applyForm.notes = row.notes
  applyVisible.value = true
}

const handleSubmitApply = async () => {
  await applyFormRef.value?.validate()
  if (applyDays.value <= 0) {
    ElMessage.warning('结束日期必须晚于开始日期')
    return
  }
  ElMessage.success('申请已提交，请等待审核')
  applyVisible.value = false
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要删除宠物 "${row.petName}" 吗？`, '提示', {
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
.owner-pets {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.pet-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.pet-card {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.pet-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.pet-header {
  display: flex;
  align-items: center;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.pet-avatar {
  width: 72px;
  height: 72px;
  border-radius: 12px;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  color: white;
}

.pet-info {
  flex: 1;
}

.pet-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.pet-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-meta span {
  font-size: 14px;
  color: #909399;
}

.pet-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.pet-details {
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.detail-label {
  font-size: 14px;
  color: #909399;
}

.detail-value {
  font-size: 14px;
  color: #606266;
}

.vaccinations {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 4px;
}

.pet-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

:deep(.el-card__body) {
  padding: 20px;
}
</style>