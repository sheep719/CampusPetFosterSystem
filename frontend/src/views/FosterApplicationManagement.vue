<template>
  <div class="foster-application-management">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="申请编号">
          <el-input v-model="searchForm.orderNo" placeholder="请输入申请编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="宠物名称">
          <el-input v-model="searchForm.petName" placeholder="请输入宠物名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="寄养者">
          <el-input v-model="searchForm.ownerName" placeholder="请输入寄养者姓名" clearable style="width: 150px" />
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
        <h3 class="table-title">寄养申请列表</h3>
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
        <el-table-column prop="ownerName" label="寄养者" width="100" />
        <el-table-column prop="ownerPhone" label="联系方式" width="130" />
        <el-table-column prop="caregiverName" label="被寄养者" width="100" />
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
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="success" link :icon="Check" @click="handleApprove(row)" v-if="row.status === 'pending'">通过</el-button>
            <el-button type="danger" link :icon="Close" @click="handleReject(row)" v-if="row.status === 'pending'">拒绝</el-button>
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
        <el-descriptions-item label="寄养者">{{ detailData.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.ownerPhone }}</el-descriptions-item>
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
      v-model="auditVisible"
      :title="auditDialogTitle"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="auditForm" ref="auditFormRef" label-width="80px">
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAuditSubmit">{{ auditDialogTitle }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { Search, Refresh, View, Check, Close } from '@element-plus/icons-vue'
import { getApplicationList, reviewApplication } from '@/api/fosterApplication'
import type { FosterApplication } from '@/api/fosterApplication'

const loading = ref(false)
const detailVisible = ref(false)
const auditVisible = ref(false)
const auditDialogTitle = ref('')
const auditFormRef = ref<FormInstance>()
const currentAuditRow = ref<any>(null)
const isApprove = ref(false)

const searchForm = reactive({
  orderNo: '',
  petName: '',
  ownerName: '',
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
  ownerName: '',
  ownerPhone: '',
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

const auditForm = reactive({
  remark: ''
})

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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getApplicationList({
      page: pagination.page,
      size: pagination.pageSize,
      petName: searchForm.petName || undefined,
      status: searchForm.status || undefined
    })
    pagination.total = res.total
    tableData.value = res.list.map((item: FosterApplication) => {
      const start = new Date(item.startTime)
      const end = new Date(item.expectedEndTime)
      const days = Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24))
      return {
        ...item,
        orderNo: 'FOSTER' + item.id.toString().padStart(10, '0'),
        petName: '宠物' + item.petId,
        species: 'cat',
        breed: '-',
        age: 1,
        ownerName: '寄养者' + item.ownerId,
        ownerPhone: '-',
        caregiverName: '被寄养者' + item.caregiverId,
        locationName: '寄养点' + item.locationId,
        startDate: item.startTime.substring(0, 10),
        endDate: item.expectedEndTime.substring(0, 10),
        days: days > 0 ? days : 1,
        totalPrice: days * 50,
        createTime: item.createTime || '-',
        auditTime: '-',
        auditRemark: item.rejectReason || '-',
        petNotes: item.careNote || '-'
      }
    })
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
  searchForm.orderNo = ''
  searchForm.petName = ''
  searchForm.ownerName = ''
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
  detailData.ownerName = row.ownerName
  detailData.ownerPhone = row.ownerPhone
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

const handleApprove = (row: any) => {
  isApprove.value = true
  auditDialogTitle.value = '通过申请'
  currentAuditRow.value = row
  auditForm.remark = ''
  auditVisible.value = true
}

const handleReject = (row: any) => {
  isApprove.value = false
  auditDialogTitle.value = '拒绝申请'
  currentAuditRow.value = row
  auditForm.remark = ''
  auditVisible.value = true
}

const handleAuditSubmit = async () => {
  try {
    if (currentAuditRow.value) {
      await reviewApplication(currentAuditRow.value.id, isApprove.value, auditForm.remark)
      ElMessage.success(isApprove.value ? '申请已通过' : '申请已拒绝')
    }
    auditVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.foster-application-management {
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