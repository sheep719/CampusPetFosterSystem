<template>
  <div class="handover-management">
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
        <el-form-item label="交接日期">
          <el-date-picker
            v-model="searchForm.date"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
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
        <el-table-column prop="caregiverName" label="被寄养者" width="100" align="center" />
        <el-table-column label="交接类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === 'delivery' ? 'warning' : 'success'" effect="plain">
              {{ row.type === 'delivery' ? '送交' : '接回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handoverDate" label="交接日期" width="110" align="center" />
        <el-table-column prop="handoverTime" label="交接时间" width="100" align="center" />
        <el-table-column label="交接状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'completed' ? 'success' : 'warning'">
              {{ row.status === 'completed' ? '已完成' : '待交接' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="交接照片" width="80" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Picture" @click="handleViewPhotos(row)" v-if="row.photos && row.photos.length">查看</el-button>
            <span v-else style="color: #909399">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="notes" label="交接备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="success" link :icon="Check" @click="handleConfirm(row)" v-if="row.status === 'pending'">确认交接</el-button>
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
        <el-descriptions-item label="被寄养者">{{ currentHandover.caregiverName }}</el-descriptions-item>
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
        <el-descriptions-item label="交接备注" :span="2">{{ currentHandover.notes || '无' }}</el-descriptions-item>
        <el-descriptions-item label="交接人确认" :span="2">
          <div class="confirm-info">
            <div class="confirm-item">
              <span class="confirm-label">寄养者确认：</span>
              <el-tag :type="currentHandover.ownerConfirmed ? 'success' : 'info'" size="small">
                {{ currentHandover.ownerConfirmed ? '已确认' : '未确认' }}
              </el-tag>
              <span class="confirm-time" v-if="currentHandover.ownerConfirmTime">{{ currentHandover.ownerConfirmTime }}</span>
            </div>
            <div class="confirm-item">
              <span class="confirm-label">被寄养者确认：</span>
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
      v-model="photoVisible"
      title="交接照片"
      width="600px"
    >
      <div class="photo-grid" v-if="currentPhotos.length">
        <el-image
          v-for="(photo, index) in currentPhotos"
          :key="index"
          :src="photo"
          :preview-src-list="currentPhotos"
          fit="cover"
          class="grid-photo"
        />
      </div>
      <el-empty description="暂无照片" v-else />
    </el-dialog>

    <el-dialog
      v-model="confirmVisible"
      title="确认交接"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-form :model="confirmForm" ref="confirmFormRef" label-width="90px">
        <el-form-item label="交接编号">
          <el-input :model-value="currentHandover?.handoverNo" disabled />
        </el-form-item>
        <el-form-item label="交接类型">
          <el-tag :type="currentHandover?.type === 'delivery' ? 'warning' : 'success'" effect="plain">
            {{ currentHandover?.type === 'delivery' ? '送交' : '接回' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="交接日期">
          <el-date-picker v-model="confirmForm.handoverDate" type="date" placeholder="请选择实际交接日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="交接时间">
          <el-time-picker v-model="confirmForm.handoverTime" placeholder="请选择实际交接时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="交接地点">
          <el-input v-model="confirmForm.location" placeholder="请输入交接地点" />
        </el-form-item>
        <el-form-item label="交接照片">
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
        </el-form-item>
        <el-form-item label="交接备注">
          <el-input v-model="confirmForm.notes" type="textarea" :rows="3" placeholder="请输入交接备注" />
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
import { Search, Refresh, View, Check, Picture, Plus } from '@element-plus/icons-vue'
import { getHandoverList, confirmHandover } from '@/api/handoverRecord'
import type { HandoverRecord } from '@/api/handoverRecord'

const loading = ref(false)
const detailVisible = ref(false)
const photoVisible = ref(false)
const confirmVisible = ref(false)
const confirmFormRef = ref<FormInstance>()
const currentHandover = ref<any>(null)
const currentPhotos = ref<string[]>([])

const searchForm = reactive({
  handoverNo: '',
  type: '',
  status: '',
  date: null as any
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const confirmForm = reactive({
  handoverDate: '',
  handoverTime: '',
  location: '',
  notes: '',
  photoList: [] as any[]
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHandoverList({
      page: pagination.page,
      size: pagination.pageSize,
      type: searchForm.type || undefined,
      status: searchForm.status || undefined
    })
    pagination.total = res.total
    tableData.value = res.list.map((item: HandoverRecord) => ({
      ...item,
      handoverNo: 'HO' + item.id.toString().padStart(8, '0'),
      applicationNo: 'FA' + item.applicationId.toString().padStart(8, '0'),
      petName: '宠物' + item.petId,
      petSpecies: 'cat',
      petSpeciesLabel: '猫',
      ownerName: '寄养者' + item.ownerId,
      caregiverName: '被寄养者' + item.caregiverId,
      type: item.handoverType === 'delivery' ? 'delivery' : 'pickup',
      handoverDate: item.handoverTime ? item.handoverTime.substring(0, 10) : '-',
      handoverTime: item.handoverTime ? item.handoverTime.substring(11, 16) : '-',
      location: item.location || '-',
      status: item.status === 'completed' ? 'completed' : 'pending',
      notes: item.notes || '',
      photos: [],
      ownerConfirmed: item.ownerConfirmed === 1,
      ownerConfirmTime: '-',
      caregiverConfirmed: item.caregiverConfirmed === 1,
      caregiverConfirmTime: '-'
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
  searchForm.handoverNo = ''
  searchForm.type = ''
  searchForm.status = ''
  searchForm.date = null
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

const handleViewPhotos = (row: any) => {
  currentPhotos.value = row.photos || []
  photoVisible.value = true
}

const handleConfirm = (row: any) => {
  currentHandover.value = row
  confirmForm.handoverDate = row.handoverDate
  confirmForm.handoverTime = row.handoverTime
  confirmForm.location = row.location
  confirmForm.notes = ''
  confirmForm.photoList = []
  confirmVisible.value = true
}

const handlePhotoChange = (file: any, fileList: any[]) => {
  confirmForm.photoList = fileList
}

const handleSubmitConfirm = async () => {
  if (!confirmForm.handoverDate) {
    ElMessage.warning('请选择交接日期')
    return
  }
  if (!confirmForm.handoverTime) {
    ElMessage.warning('请选择交接时间')
    return
  }
  if (!confirmForm.location) {
    ElMessage.warning('请输入交接地点')
    return
  }
  
  ElMessageBox.confirm('确认完成该交接记录？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      if (currentHandover.value) {
        await confirmHandover(currentHandover.value.id, {
          location: confirmForm.location,
          notes: confirmForm.notes
        })
        ElMessage.success('交接已确认')
      }
    } catch (error) {
      ElMessage.error('确认失败')
    }
    confirmVisible.value = false
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.handover-management {
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

.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.grid-photo {
  width: 100%;
  height: 150px;
  border-radius: 8px;
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