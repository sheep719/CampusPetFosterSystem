<template>
  <div class="owner-caregivers">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="可照顾类型">
          <el-select v-model="searchForm.careTypes" placeholder="全部" clearable style="width: 150px">
            <el-option label="猫" value="cat" />
            <el-option label="狗" value="dog" />
            <el-option label="仓鼠" value="hamster" />
            <el-option label="兔子" value="rabbit" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序方式">
          <el-select v-model="sortType" placeholder="默认" clearable style="width: 120px">
            <el-option label="距离最近" value="distance" />
            <el-option label="评分最高" value="rating" />
            <el-option label="服务次数" value="service" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="caregiver-list">
      <el-card
        v-for="item in tableData"
        :key="item.id"
        class="caregiver-card"
        shadow="hover"
      >
        <div class="caregiver-header">
          <div class="caregiver-avatar">
            <el-icon :size="40" class="avatar-icon">
              <UserFilled />
            </el-icon>
          </div>
          <div class="caregiver-info">
            <h3 class="caregiver-name">{{ item.name }}</h3>
            <div class="caregiver-meta">
              <el-tag :type="item.certified === '1' ? 'success' : 'warning'" effect="plain" size="small">
                {{ item.certified === '1' ? '已认证' : '未认证' }}
              </el-tag>
              <el-tag type="info" effect="plain" size="small">
                {{ item.experience }}年经验
              </el-tag>
            </div>
          </div>
          <div class="caregiver-rating">
            <el-rate :model-value="item.avgRating" disabled :show-score="true" text-color="#FF9900" />
          </div>
        </div>
        <div class="caregiver-details">
          <div class="detail-row">
            <span class="detail-label">📍 住址</span>
            <span class="detail-value">{{ item.address }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">📞 电话</span>
            <span class="detail-value">{{ item.phone }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">🚶 距离</span>
            <span class="detail-value" style="color: #409EFF; font-weight: 600">{{ item.distance }}km</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">🐾 可照顾类型</span>
            <div class="care-types">
              <el-tag
                v-for="type in item.careTypesList"
                :key="type.value"
                :type="getSpeciesTagType(type.value)"
                effect="plain"
                size="small"
                style="margin-right: 4px"
              >
                {{ type.label }}
              </el-tag>
            </div>
          </div>
          <div class="detail-row">
            <span class="detail-label">📊 服务次数</span>
            <span class="detail-value" style="color: #E6A23C; font-weight: 600">{{ item.serviceCount }}次</span>
          </div>
        </div>
        <div v-if="item.introduction" class="caregiver-intro">
          <p>{{ item.introduction }}</p>
        </div>
        <div class="caregiver-actions">
          <el-button type="primary" :icon="Message" @click="handleContact(item)">联系</el-button>
          <el-button type="success" :icon="Calendar" @click="handleApply(item)">发起寄养申请</el-button>
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
      v-model="applyVisible"
      title="发起寄养申请"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form :model="applyForm" :rules="applyRules" ref="applyFormRef" label-width="90px">
        <el-form-item label="选择宠物" prop="petId">
          <el-select v-model="applyForm.petId" placeholder="请选择宠物" style="width: 100%">
            <el-option v-for="pet in petList" :key="pet.id" :label="pet.petName" :value="pet.id" />
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
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, UserFilled, Message, Calendar } from '@element-plus/icons-vue'

const loading = ref(false)
const applyVisible = ref(false)
const applyFormRef = ref<FormInstance>()
const currentCaregiver = ref<any>(null)
const sortType = ref('')

const searchForm = reactive({
  name: '',
  careTypes: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const petList = ref([
  { id: 1, petName: '咪咪', species: 'cat', breed: '英短', age: 2 },
  { id: 2, petName: '旺财', species: 'dog', breed: '金毛', age: 3 },
  { id: 3, petName: '雪球', species: 'rabbit', breed: '垂耳兔', age: 1 }
])

const applyForm = reactive({
  petId: null as number | null,
  startDate: '',
  endDate: '',
  notes: ''
})

const applyRules: FormRules = {
  petId: [{ required: true, message: '请选择宠物', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const applyDays = computed(() => {
  if (!applyForm.startDate || !applyForm.endDate) return 0
  const start = new Date(applyForm.startDate)
  const end = new Date(applyForm.endDate)
  return Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24)) + 1
})

const estimatedPrice = computed(() => {
  if (!currentCaregiver.value || applyDays.value <= 0) return 0
  return applyDays.value * currentCaregiver.value.price
})

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

const mockData = [
  { id: 1, name: '李阿姨', phone: '13800138011', address: '阳光小区3号楼201室', distance: 1.2, careTypes: ['cat', 'dog'], experience: 5, avgRating: 4.8, serviceCount: 42, certified: '1', price: 60, introduction: '退休后开始照顾邻居家的宠物，有丰富的养猫养狗经验' },
  { id: 2, name: '张叔叔', phone: '13800138012', address: '幸福路88号', distance: 2.5, careTypes: ['cat', 'dog', 'rabbit'], experience: 3, avgRating: 4.5, serviceCount: 28, certified: '1', price: 50, introduction: '专业宠物护理员，持有宠物护理证书' },
  { id: 3, name: '王奶奶', phone: '13800138013', address: '和谐花园5号楼102室', distance: 3.0, careTypes: ['hamster', 'rabbit'], experience: 2, avgRating: 4.9, serviceCount: 15, certified: '1', price: 30, introduction: '喜欢小动物，专门照顾小宠物' },
  { id: 4, name: '陈阿姨', phone: '13800138014', address: '锦绣苑12号楼302室', distance: 1.8, careTypes: ['cat'], experience: 4, avgRating: 4.7, serviceCount: 35, certified: '1', price: 55, introduction: '猫咪爱好者，家里有猫爬架和各种猫玩具' },
  { id: 5, name: '刘大哥', phone: '13800138015', address: '学府路100号', distance: 0.8, careTypes: ['cat', 'dog', 'bird'], experience: 6, avgRating: 4.6, serviceCount: 56, certified: '1', price: 70, introduction: '离学校最近，方便学生探望，提供接送服务' },
  { id: 6, name: '孙阿姨', phone: '13800138017', address: '绿园小区2号楼', distance: 2.0, careTypes: ['cat', 'dog', 'rabbit', 'hamster'], experience: 3, avgRating: 4.8, serviceCount: 31, certified: '1', price: 45, introduction: '家庭式寄养，环境温馨' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.name) {
      filtered = filtered.filter(item => item.name.includes(searchForm.name))
    }
    if (searchForm.careTypes) {
      filtered = filtered.filter(item => item.careTypes.includes(searchForm.careTypes))
    }
    if (sortType.value === 'distance') {
      filtered.sort((a, b) => a.distance - b.distance)
    } else if (sortType.value === 'rating') {
      filtered.sort((a, b) => b.avgRating - a.avgRating)
    } else if (sortType.value === 'service') {
      filtered.sort((a, b) => b.serviceCount - a.serviceCount)
    }
    pagination.total = filtered.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tableData.value = filtered.slice(start, end).map(item => ({
      ...item,
      careTypesList: item.careTypes.map((s: string) => ({
        value: s,
        label: speciesMap[s]
      }))
    }))
    loading.value = false
  }, 300)
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  searchForm.name = ''
  searchForm.careTypes = ''
  sortType.value = ''
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

const handleContact = (item: any) => {
  ElMessage.info(`已拨打 ${item.phone}，请等待接听`)
}

const handleApply = (item: any) => {
  currentCaregiver.value = item
  applyForm.petId = null
  applyForm.startDate = ''
  applyForm.endDate = ''
  applyForm.notes = ''
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.owner-caregivers {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.caregiver-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.caregiver-card {
  border-radius: 12px;
  transition: all 0.3s ease;
}

.caregiver-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.caregiver-header {
  display: flex;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.caregiver-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.avatar-icon {
  color: white;
}

.caregiver-info {
  flex: 1;
}

.caregiver-name {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.caregiver-meta {
  display: flex;
  gap: 8px;
}

.caregiver-rating {
  min-width: 120px;
}

.caregiver-details {
  margin-bottom: 16px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.detail-label {
  font-size: 14px;
  color: #909399;
}

.detail-value {
  font-size: 14px;
  color: #606266;
}

.care-types {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.caregiver-intro {
  padding: 12px;
  background: #fafafa;
  border-radius: 6px;
  margin-bottom: 16px;
}

.caregiver-intro p {
  margin: 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.caregiver-actions {
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