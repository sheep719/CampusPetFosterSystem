<template>
  <div class="caregiver-management">
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
        <el-form-item label="认证状态">
          <el-select v-model="searchForm.certified" placeholder="全部" clearable style="width: 120px">
            <el-option label="已认证" value="1" />
            <el-option label="未认证" value="0" />
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
        <h3 class="table-title">被寄养者列表</h3>
      </div>
      <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="address" label="住址" min-width="200" />
        <el-table-column prop="distance" label="距离(km)" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #409EFF; font-weight: 600">{{ row.distance }}km</span>
          </template>
        </el-table-column>
        <el-table-column prop="careTypes" label="可照顾类型" width="150">
          <template #default="{ row }">
            <el-tag
              v-for="type in row.careTypesList"
              :key="type.value"
              :type="getSpeciesTagType(type.value)"
              effect="plain"
              size="small"
              style="margin-right: 4px"
            >
              {{ type.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="experience" label="照顾经验(年)" width="120" align="center">
          <template #default="{ row }">{{ row.experience }}年</template>
        </el-table-column>
        <el-table-column prop="avgRating" label="评分" width="100" align="center">
          <template #default="{ row }">
            <el-rate :model-value="row.avgRating" disabled :show-score="true" text-color="#FF9900" />
          </template>
        </el-table-column>
        <el-table-column prop="serviceCount" label="服务次数" width="100" align="center">
          <template #default="{ row }">
            <span style="color: #E6A23C; font-weight: 600">{{ row.serviceCount }}次</span>
          </template>
        </el-table-column>
        <el-table-column prop="certified" label="认证状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.certified === '1' ? 'success' : 'warning'" effect="light">
              {{ row.certified === '1' ? '已认证' : '未认证' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="success" link :icon="Check" @click="handleCertify(row)" v-if="row.certified === '0'">认证</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">禁用</el-button>
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
      title="被寄养者详情"
      width="550px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="住址">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="距离学校">{{ detailData.distance }}km</el-descriptions-item>
        <el-descriptions-item label="照顾经验">{{ detailData.experience }}年</el-descriptions-item>
        <el-descriptions-item label="服务次数">{{ detailData.serviceCount }}次</el-descriptions-item>
        <el-descriptions-item label="平均评分">
          <el-rate :model-value="detailData.avgRating" disabled text-color="#FF9900" />
        </el-descriptions-item>
        <el-descriptions-item label="认证状态">
          <el-tag :type="detailData.certified === '1' ? 'success' : 'warning'">
            {{ detailData.certified === '1' ? '已认证' : '未认证' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="可照顾类型" :span="2">
          <el-tag
            v-for="type in detailData.careTypesList"
            :key="type.value"
            :type="getSpeciesTagType(type.value)"
            effect="plain"
            size="small"
            style="margin-right: 4px"
          >
            {{ type.label }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="自我介绍" :span="2">{{ detailData.introduction || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, Check, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const detailVisible = ref(false)

const searchForm = reactive({
  name: '',
  careTypes: '',
  certified: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref<any[]>([])

const detailData = reactive({
  name: '',
  phone: '',
  address: '',
  distance: 0,
  experience: 0,
  serviceCount: 0,
  avgRating: 0,
  certified: '',
  careTypesList: [] as any[],
  introduction: ''
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
  { id: 1, name: '李阿姨', phone: '13800138011', address: '阳光小区3号楼201室', distance: 1.2, careTypes: ['cat', 'dog'], experience: 5, avgRating: 4.8, serviceCount: 42, certified: '1', enabled: '1', introduction: '退休后开始照顾邻居家的宠物，有丰富的养猫养狗经验' },
  { id: 2, name: '张叔叔', phone: '13800138012', address: '幸福路88号', distance: 2.5, careTypes: ['cat', 'dog', 'rabbit'], experience: 3, avgRating: 4.5, serviceCount: 28, certified: '1', enabled: '1', introduction: '专业宠物护理员，持有宠物护理证书' },
  { id: 3, name: '王奶奶', phone: '13800138013', address: '和谐花园5号楼102室', distance: 3.0, careTypes: ['hamster', 'rabbit'], experience: 2, avgRating: 4.9, serviceCount: 15, certified: '1', enabled: '1', introduction: '喜欢小动物，专门照顾小宠物' },
  { id: 4, name: '陈阿姨', phone: '13800138014', address: '锦绣苑12号楼302室', distance: 1.8, careTypes: ['cat'], experience: 4, avgRating: 4.7, serviceCount: 35, certified: '1', enabled: '1', introduction: '猫咪爱好者，家里有猫爬架和各种猫玩具' },
  { id: 5, name: '刘大哥', phone: '13800138015', address: '学府路100号', distance: 0.8, careTypes: ['cat', 'dog', 'bird'], experience: 6, avgRating: 4.6, serviceCount: 56, certified: '1', enabled: '1', introduction: '离学校最近，方便学生探望，提供接送服务' },
  { id: 6, name: '赵姐', phone: '13800138016', address: '碧水湾小区', distance: 4.2, careTypes: ['turtle'], experience: 8, avgRating: 4.4, serviceCount: 12, certified: '1', enabled: '1', introduction: '专业养龟多年，有大型水池' },
  { id: 7, name: '孙阿姨', phone: '13800138017', address: '绿园小区2号楼', distance: 2.0, careTypes: ['cat', 'dog', 'rabbit', 'hamster'], experience: 3, avgRating: 4.8, serviceCount: 31, certified: '1', enabled: '1', introduction: '家庭式寄养，环境温馨' },
  { id: 8, name: '周叔叔', phone: '13800138018', address: '郊外农场', distance: 5.5, careTypes: ['dog'], experience: 10, avgRating: 4.3, serviceCount: 23, certified: '1', enabled: '0', introduction: '大型户外场地，适合运动量较大的狗狗' },
  { id: 9, name: '吴阿姨', phone: '13800138019', address: '蓝天小区', distance: 3.5, careTypes: ['bird'], experience: 5, avgRating: 4.5, serviceCount: 18, certified: '0', enabled: '1', introduction: '专业鸟类照顾' },
  { id: 10, name: '郑奶奶', phone: '13800138020', address: '温馨家园', distance: 2.8, careTypes: ['cat', 'dog'], experience: 4, avgRating: 4.7, serviceCount: 29, certified: '1', enabled: '1', introduction: '爱心人士，对待宠物如同家人' }
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
    if (searchForm.certified) {
      filtered = filtered.filter(item => item.certified === searchForm.certified)
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
  searchForm.certified = ''
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
  detailData.name = row.name
  detailData.phone = row.phone
  detailData.address = row.address
  detailData.distance = row.distance
  detailData.experience = row.experience
  detailData.serviceCount = row.serviceCount
  detailData.avgRating = row.avgRating
  detailData.certified = row.certified
  detailData.careTypesList = row.careTypesList
  detailData.introduction = row.introduction
  detailVisible.value = true
}

const handleCertify = (row: any) => {
  ElMessageBox.confirm(`确定要认证被寄养者 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('认证成功')
    loadData()
  }).catch(() => {})
}

const handleStatusChange = (row: any) => {
  ElMessage.success(row.enabled === '1' ? '已启用' : '已禁用')
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确定要禁用被寄养者 "${row.name}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('禁用成功')
    loadData()
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.caregiver-management {
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