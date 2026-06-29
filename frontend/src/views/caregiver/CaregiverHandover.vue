<template>
  <div class="caregiver-handover">
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="交接编号">
          <el-input v-model="searchForm.handoverNo" placeholder="请输入交接编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="交接类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable style="width: 120px">
            <el-option label="送交" value="delivery" />
            <el-option label="接回" value="pickup" />
          </el-select>
        </el-form-item>
        <el-form-item label="交接状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待交接" value="pending" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="handoverNo" label="交接编号" width="140" align="center" />
        <el-table-column prop="applicationNo" label="申请编号" width="140" align="center" />
        <el-table-column label="宠物信息" width="160">
          <template #default="{ row }">
            <div class="pet-info-cell">
              <el-tag :type="getSpeciesTagType(row.petSpecies)" effect="light">{{ row.petSpeciesLabel }}</el-tag>
              <span class="pet-name">{{ row.petName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="寄养者" width="100" align="center" />
        <el-table-column prop="ownerPhone" label="联系电话" width="110" align="center" />
        <el-table-column label="交接类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 'delivery' ? 'warning' : 'success'" effect="plain">
              {{ row.type === 'delivery' ? '送交' : '接回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handoverDate" label="交接日期" width="110" align="center" />
        <el-table-column prop="handoverTime" label="交接时间" width="100" align="center" />
        <el-table-column prop="location" label="交接地点" width="120" show-overflow-tooltip />
        <el-table-column label="交接状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status === 'completed' ? '已完成' : '待交接' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="我的确认" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.caregiverConfirmed ? 'success' : 'info'" size="small">
              {{ row.caregiverConfirmed ? '已确认' : '未确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="success" link :icon="Check" @click="handleConfirm(row)" v-if="row.status === 'pending' && !row.caregiverConfirmed">确认交接</el-button>
          </template>
        </el-table-column>
      </el-table>

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
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="交接详情"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border v-if="currentHandover">
        <el-descriptions-item label="交接编号">{{ currentHandover.handoverNo }}</el-descriptions-item>
        <el-descriptions-item label="申请编号">{{ currentHandover.applicationNo }}</el-descriptions-item>
        <el-descriptions-item label="宠物名称">{{ currentHandover.petName }}</el-descriptions-item>
        <el-descriptions-item label="宠物种类">{{ currentHandover.petSpeciesLabel }}</el-descriptions-item>
        <el-descriptions-item label="寄养者">{{ currentHandover.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ currentHandover.ownerPhone }}</el-descriptions-item>
        <el-descriptions-item label="交接类型">
          <el-tag :type="currentHandover.type === 'delivery' ? 'warning' : 'success'" effect="plain">
            {{ currentHandover.type === 'delivery' ? '送交' : '接回' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交接状态">
          <el-tag :type="currentHandover.status === 'completed' ? 'success' : 'warning'">
            {{ currentHandover.status === 'completed' ? '已完成' : '待交接' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交接日期">{{ currentHandover.handoverDate }}</el-descriptions-item>
        <el-descriptions-item label="交接时间">{{ currentHandover.handoverTime }}</el-descriptions-item>
        <el-descriptions-item label="交接地点" :span="2">{{ currentHandover.location }}</el-descriptions-item>
        <el-descriptions-item label="宠物特殊说明" :span="2">{{ currentHandover.petNotes || '无' }}</el-descriptions-item>
        <el-descriptions-item label="交接备注" :span="2">{{ currentHandover.notes || '无' }}</el-descriptions-item>
        <el-descriptions-item label="确认状态" :span="2">
          <div class="confirm-info">
            <div class="confirm-item">
              <span class="confirm-label">寄养者确认：</span>
              <el-tag :type="currentHandover.ownerConfirmed ? 'success' : 'info'" size="small">
                {{ currentHandover.ownerConfirmed ? '已确认' : '未确认' }}
              </el-tag>
              <span class="confirm-time" v-if="currentHandover.ownerConfirmTime">{{ currentHandover.ownerConfirmTime }}</span>
            </div>
            <div class="confirm-item">
              <span class="confirm-label">我已确认：</span>
              <el-tag :type="currentHandover.caregiverConfirmed ? 'success' : 'info'" size="small">
                {{ currentHandover.caregiverConfirmed ? '已确认' : '未确认' }}
              </el-tag>
              <span class="confirm-time" v-if="currentHandover.caregiverConfirmTime">{{ currentHandover.caregiverConfirmTime }}</span>
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="交接照片" :span="2">
          <div class="photo-list" v-if="currentHandover.photos && currentHandover.photos.length">
            <el-image
              v-for="(photo, index) in currentHandover.photos"
              :key="index"
              :src="photo"
              :preview-src-list="currentHandover.photos"
              fit="cover"
              class="handover-photo"
            />
          </div>
          <span v-else style="color: #909399">暂无照片</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog
      v-model="confirmVisible"
      title="确认交接"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-alert type="info" show-icon :closable="false" style="margin-bottom: 20px">
        <template #title>
          <span v-if="currentHandover?.type === 'delivery'">请确认宠物送交已完成，确认后表示您已接收宠物并开始寄养。</span>
          <span v-else>请确认宠物接回已完成，确认后表示您已完成寄养并将宠物交还给寄养者。</span>
        </template>
      </el-alert>
      <el-form :model="confirmForm" ref="confirmFormRef" label-width="90px">
        <el-form-item label="交接编号">
          <el-input :model-value="currentHandover?.handoverNo" disabled />
        </el-form-item>
        <el-form-item label="交接类型">
          <el-tag :type="currentHandover?.type === 'delivery' ? 'warning' : 'success'" effect="plain">
            {{ currentHandover?.type === 'delivery' ? '送交' : '接回' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="宠物名称">
          <el-input :model-value="currentHandover?.petName" disabled />
        </el-form-item>
        <el-form-item label="交接地点">
          <el-input :model-value="currentHandover?.location" disabled />
        </el-form-item>
        <el-form-item label="交接备注">
          <el-input v-model="confirmForm.notes" type="textarea" :rows="3" placeholder="请输入交接备注（如宠物状态、交接情况等）" />
        </el-form-item>
        <el-form-item label="上传照片">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handlePhotoChange"
            :file-list="confirmForm.photoList"
            accept="image/*"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div style="color: #909399; font-size: 12px; margin-top: 4px">建议上传交接现场照片作为凭证</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="confirmVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitConfirm">确认交接</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Search, Refresh, View, Check, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const detailVisible = ref(false)
const confirmVisible = ref(false)
const confirmFormRef = ref<FormInstance>()
const currentHandover = ref<any>(null)

const searchForm = reactive({
  handoverNo: '',
  type: '',
  status: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const confirmForm = reactive({
  notes: '',
  photoList: [] as any[]
})

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

const mockPhotos = [
  'https://via.placeholder.com/300x200?text=Photo1',
  'https://via.placeholder.com/300x200?text=Photo2',
  'https://via.placeholder.com/300x200?text=Photo3'
]

const mockData = [
  { id: 1, handoverNo: 'HO202401001', applicationNo: 'FA202401001', petName: '咪咪', petSpecies: 'cat', petSpeciesLabel: '猫', ownerName: '张三', ownerPhone: '13912345678', type: 'delivery', handoverDate: '2024-01-15', handoverTime: '09:00', location: '学校东门', status: 'completed', notes: '宠物状态良好', petNotes: '性格温顺，喜欢吃猫粮', photos: mockPhotos, ownerConfirmed: true, ownerConfirmTime: '2024-01-15 09:05', caregiverConfirmed: true, caregiverConfirmTime: '2024-01-15 09:10' },
  { id: 2, handoverNo: 'HO202401002', applicationNo: 'FA202401001', petName: '咪咪', petSpecies: 'cat', petSpeciesLabel: '猫', ownerName: '张三', ownerPhone: '13912345678', type: 'pickup', handoverDate: '2024-01-20', handoverTime: '18:00', location: '学校东门', status: 'completed', notes: '寄养期间猫咪表现良好', petNotes: '性格温顺，喜欢吃猫粮', photos: mockPhotos.slice(0, 2), ownerConfirmed: true, ownerConfirmTime: '2024-01-20 18:05', caregiverConfirmed: true, caregiverConfirmTime: '2024-01-20 18:10' },
  { id: 3, handoverNo: 'HO202401003', applicationNo: 'FA202401002', petName: '旺财', petSpecies: 'dog', petSpeciesLabel: '狗', ownerName: '李四', ownerPhone: '13898765432', type: 'delivery', handoverDate: '2024-01-18', handoverTime: '10:00', location: '学校西门', status: 'completed', notes: '狗狗很活泼，已带好狗粮', petNotes: '活泼好动，每天需要遛两次', photos: mockPhotos, ownerConfirmed: true, ownerConfirmTime: '2024-01-18 10:05', caregiverConfirmed: true, caregiverConfirmTime: '2024-01-18 10:15' },
  { id: 4, handoverNo: 'HO202401004', applicationNo: 'FA202401002', petName: '旺财', petSpecies: 'dog', petSpeciesLabel: '狗', ownerName: '李四', ownerPhone: '13898765432', type: 'pickup', handoverDate: '2024-01-25', handoverTime: '17:00', location: '学校西门', status: 'pending', notes: '', petNotes: '活泼好动，每天需要遛两次', photos: [], ownerConfirmed: false, ownerConfirmTime: '', caregiverConfirmed: false, caregiverConfirmTime: '' },
  { id: 5, handoverNo: 'HO202401005', applicationNo: 'FA202401003', petName: '雪球', petSpecies: 'rabbit', petSpeciesLabel: '兔子', ownerName: '王五', ownerPhone: '13711223344', type: 'delivery', handoverDate: '2024-01-22', handoverTime: '14:00', location: '学校北门', status: 'pending', notes: '', petNotes: '胆小，需要安静环境', photos: [], ownerConfirmed: false, ownerConfirmTime: '', caregiverConfirmed: false, caregiverConfirmTime: '' }
]

const loadData = () => {
  loading.value = true
  setTimeout(() => {
    let filtered = [...mockData]
    if (searchForm.handoverNo) {
      filtered = filtered.filter(item => item.handoverNo.includes(searchForm.handoverNo))
    }
    if (searchForm.type) {
      filtered = filtered.filter(item => item.type === searchForm.type)
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
  searchForm.handoverNo = ''
  searchForm.type = ''
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
  currentHandover.value = row
  detailVisible.value = true
}

const handleConfirm = (row: any) => {
  currentHandover.value = row
  confirmForm.notes = ''
  confirmForm.photoList = []
  confirmVisible.value = true
}

const handlePhotoChange = (file: any, fileList: any[]) => {
  confirmForm.photoList = fileList
}

const handleSubmitConfirm = async () => {
  ElMessageBox.confirm('确认完成交接？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('交接确认成功')
    confirmVisible.value = false
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.caregiver-handover {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.search-form {
  margin: 0;
}

.table-card {
  border-radius: 8px;
}

.pet-info-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pet-name {
  font-weight: 500;
  color: #303133;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.confirm-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.confirm-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.confirm-label {
  font-size: 14px;
  color: #606266;
}

.confirm-time {
  font-size: 12px;
  color: #909399;
}

.photo-list {
  display: flex;
  gap: 8px;
}

.handover-photo {
  width: 100px;
  height: 80px;
  border-radius: 4px;
}

:deep(.el-descriptions__label) {
  width: 100px;
}

:deep(.el-upload--picture-card) {
  width: 80px;
  height: 80px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 80px;
  height: 80px;
}
</style>