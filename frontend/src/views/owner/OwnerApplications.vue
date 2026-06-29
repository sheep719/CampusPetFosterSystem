<template>
  <div class="owner-applications">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申请编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入申请编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.petName" placeholder="请输入宠物名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="申请状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 150px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="寄养中" value="fostering" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
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
        <h3 class="table-title">我的寄养申请</h3>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="申请编号" width="160">
          <template #default="{ row }">
            <span style="color: #409EFF">{{ row.orderNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="petName" label="宠物名称" width="120" />
        <el-table-column prop="species" label="宠物种类" width="100">
          <template #default="{ row }">
            <el-tag :type="getSpeciesTagType(row.species)" effect="light">{{ getSpeciesLabel(row.species) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="caregiverName" label="被寄养者" width="100" />
        <el-table-column prop="locationName" label="寄养点" width="150" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="days" label="寄养天数" width="90" align="center">
          <template #default="{ row }">
            <span style="color: #E6A23C; font-weight: 600">{{ row.days }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="费用(元)" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #E6A23C; font-weight: 600">¥{{ row.totalPrice }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="申请状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" effect="light">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="danger" link :icon="Close" @click="handleCancel(row)" v-if="row.status === 'pending' || row.status === 'approved'">取消申请</el-button>
            <el-button type="success" link :icon="Star" @click="handleRate(row)" v-if="row.status === 'completed' && !row.rated">评价</el-button>
            <el-tag type="success" size="small" v-if="row.status === 'completed' && row.rated">已评价</el-tag>
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
      v-model="detailVisible"
      title="寄养申请详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号"><span style="color: #409EFF">{{ detailData.orderNo }}</span></el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ detailData.petName }}</el-descriptions-item>
        <el-descriptions-item label="宠物种类">
          <el-tag :type="getSpeciesTagType(detailData.species)" effect="light">
            {{ getSpeciesLabel(detailData.species) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="品种">{{ detailData.breed }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detailData.age }}岁</el-descriptions-item>
        <el-descriptions-item label="被寄养者">{{ detailData.caregiverName }}</el-descriptions-item>
        <el-descriptions-item label="寄养点">{{ detailData.locationName }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="寄养天数"><span style="color: #E6A23C; font-weight: 600">{{ detailData.days }}天</span></el-descriptions-item>
        <el-descriptions-item label="费用"><span style="color: #E6A23C; font-weight: 600">¥{{ detailData.totalPrice }}</span></el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusTagType(detailData.status)" effect="light">
            {{ getStatusLabel(detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detailData.auditTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detailData.auditRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="宠物说明" :span="2">{{ detailData.petNotes || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="rateVisible"
      title="评价服务"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="rateForm" :rules="rateRules" ref="rateFormRef" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" :show-text="true" text-color="#FF9900" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="rateForm.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您对这次寄养服务的评价"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitRate">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, View, Close, Star } from '@element-plus/icons-vue'

const loading = ref(false)
const detailVisible = ref(false)
const rateVisible = ref(false)
const rateFormRef = ref<FormInstance>()
const currentRateRow = ref<any>(null)

const searchForm = reactive({
  orderNo: '',
  petName: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const detailData = reactive({
  orderNo: '',
  createTime: '',
  petName: '',
  species: '',
  breed: '',
  age: 0,
  caregiverName: '',
  locationName: '',
  startDate: '',
  endDate: '',
  days: 0,
  totalPrice: 0,
  status: '',
  auditTime: '',
  auditRemark: '',
  petNotes: ''
})

const rateForm = reactive({
  rating: 5,
  content: ''
})

const rateRules: FormRules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }]
}

const speciesMap: Record<string, string> = {
  cat: '猫',
  dog: '狗',
  hamster: '仓鼠',
  rabbit: '兔子',
  bird: '鸟',
  turtle: '乌龟'
}

const statusMap: Record<string, string> = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已拒绝',
  fostering: '寄养中',
  completed: '已完成',
  cancelled: '已取消'
}

const getSpeciesLabel = (species: string) => speciesMap[species] || species
const getStatusLabel = (status: string) => statusMap[status] || status

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

const getStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    approved: 'primary',
    rejected: 'danger',
    fostering: 'success',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

const mockData = [
  { id: 1, orderNo: 'FOSTER20260625001', petName: '咪咪', species: 'cat', breed: '英短', age: 2, caregiverName: '李阿姨', locationName: '李阿姨温馨小屋', startDate: '2026-06-26', endDate: '2026-07-02', days: 7, totalPrice: 420, status: 'pending', rated: false, createTime: '2026-06-25 09:30:00', petNotes: '性格温顺，喜欢吃猫粮' },
  { id: 2, orderNo: 'FOSTER20260625003', petName: '雪球', species: 'rabbit', breed: '垂耳兔', age: 1, caregiverName: '王奶奶', locationName: '王奶奶仓鼠乐园', startDate: '2026-06-28', endDate: '2026-07-01', days: 4, totalPrice: 120, status: 'approved', rated: false, createTime: '2026-06-25 11:00:00', auditTime: '2026-06-25 11:30:00', petNotes: '胆小，需要安静环境' },
  { id: 3, orderNo: 'FOSTER20260625004', petName: '小仓鼠', species: 'hamster', breed: '金丝熊', age: 0.5, caregiverName: '孙阿姨', locationName: '孙阿姨萌宠乐园', startDate: '2026-06-25', endDate: '2026-06-30', days: 6, totalPrice: 270, status: 'fostering', rated: false, createTime: '2026-06-24 14:00:00', auditTime: '2026-06-24 14:30:00', petNotes: '最近有点食欲不振' },
  { id: 4, orderNo: 'FOSTER20260625005', petName: '橘子', species: 'cat', breed: '橘猫', age: 4, caregiverName: '陈阿姨', locationName: '陈阿姨猫咪公寓', startDate: '2026-06-20', endDate: '2026-06-25', days: 6, totalPrice: 330, status: 'completed', rated: true, createTime: '2026-06-19 10:00:00', auditTime: '2026-06-19 10:30:00', petNotes: '爱吃，容易长胖' },
  { id: 5, orderNo: 'FOSTER20260625006', petName: '可乐', species: 'dog', breed: '柯基', age: 2, caregiverName: '刘大哥', locationName: '刘大哥宠物旅馆', startDate: '2026-06-25', endDate: '2026-07-01', days: 7, totalPrice: 490, status: 'rejected', rated: false, createTime: '2026-06-24 16:00:00', auditTime: '2026-06-24 16:30:00', auditRemark: '寄养点已满', petNotes: '短腿，跑不快' },
  { id: 6, orderNo: 'FOSTER20260625010', petName: '大白', species: 'dog', breed: '萨摩耶', age: 2, caregiverName: '周叔叔', locationName: '周叔叔犬舍', startDate: '2026-06-22', endDate: '2026-06-28', days: 7, totalPrice: 560, status: 'cancelled', rated: false, createTime: '2026-06-21 10:00:00', petNotes: '毛发雪白，需要经常打理' },
  { id: 7, orderNo: 'FOSTER20260625012', petName: '皮皮', species: 'bird', breed: '玄凤鹦鹉', age: 1, caregiverName: '吴阿姨', locationName: '吴阿姨小鸟屋', startDate: '2026-07-01', endDate: '2026-07-05', days: 5, totalPrice: 125, status: 'completed', rated: false, createTime: '2026-06-25 10:00:00', auditTime: '2026-06-25 10:30:00', petNotes: '会说话' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.orderNo) {
      filtered = filtered.filter(item => item.orderNo.includes(searchForm.orderNo))
    }
    if (searchForm.petName) {
      filtered = filtered.filter(item => item.petName.includes(searchForm.petName))
    }
    if (searchForm.status) {
      filtered = filtered.filter(item => item.status === searchForm.status)
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
  searchForm.orderNo = ''
  searchForm.petName = ''
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

const handleView = (row: any) => {
  detailData.orderNo = row.orderNo
  detailData.createTime = row.createTime
  detailData.petName = row.petName
  detailData.species = row.species
  detailData.breed = row.breed
  detailData.age = row.age
  detailData.caregiverName = row.caregiverName
  detailData.locationName = row.locationName
  detailData.startDate = row.startDate
  detailData.endDate = row.endDate
  detailData.days = row.days
  detailData.totalPrice = row.totalPrice
  detailData.status = row.status
  detailData.auditTime = row.auditTime
  detailData.auditRemark = row.auditRemark
  detailData.petNotes = row.petNotes
  detailVisible.value = true
}

const handleCancel = (row: any) => {
  ElMessageBox.confirm(`确定要取消申请 "${row.orderNo}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('申请已取消')
    loadData()
  }).catch(() => {})
}

const handleRate = (row: any) => {
  currentRateRow.value = row
  rateForm.rating = 5
  rateForm.content = ''
  rateVisible.value = true
}

const handleSubmitRate = async () => {
  await rateFormRef.value?.validate()
  ElMessage.success('评价提交成功')
  rateVisible.value = false
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.owner-applications {
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