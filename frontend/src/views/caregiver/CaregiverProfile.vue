<template>
  <div class="caregiver-profile">
    <el-card class="profile-card" shadow="hover">
      <div class="profile-header">
        <div class="avatar-section">
          <div class="avatar">
            <el-icon :size="64" class="avatar-icon">
              <UserFilled />
            </el-icon>
          </div>
          <button class="avatar-edit-btn" @click="handleAvatarEdit">
            <el-icon :size="16"><Camera /></el-icon>
          </button>
        </div>
        <div class="basic-info">
          <h2 class="name">{{ form.name }}</h2>
          <div class="meta">
            <el-tag :type="form.certified === '1' ? 'success' : 'warning'" effect="plain">
              {{ form.certified === '1' ? '已认证' : '未认证' }}
            </el-tag>
            <span class="experience">{{ form.experience }}年照顾经验</span>
            <span class="rating">
              <el-rate :model-value="form.avgRating" disabled :show-score="true" text-color="#FF9900" />
            </span>
          </div>
        </div>
        <div class="stats">
          <div class="stat-item">
            <div class="stat-value">{{ stats.serviceCount }}</div>
            <div class="stat-label">服务次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.avgRating }}</div>
            <div class="stat-label">平均评分</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.income }}</div>
            <div class="stat-label">累计收入(元)</div>
          </div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="info-card" shadow="never">
          <h3 class="card-title">基本信息</h3>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="姓名" prop="name">
                  <el-input v-model="form.name" :disabled="!isEditing" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="form.phone" :disabled="!isEditing" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别">
                  <el-radio-group v-model="form.gender" :disabled="!isEditing">
                    <el-radio value="1">男</el-radio>
                    <el-radio value="2">女</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄">
                  <el-input-number v-model="form.age" :min="18" :max="99" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="住址" prop="address">
                  <el-input v-model="form.address" :disabled="!isEditing" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="距离学校(km)">
                  <el-input-number v-model="form.distance" :min="0" :max="50" :step="0.1" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="照顾经验(年)">
                  <el-input-number v-model="form.experience" :min="0" :max="50" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="可照顾类型">
                  <el-checkbox-group v-model="form.careTypes" :disabled="!isEditing">
                    <el-checkbox label="猫" value="cat" />
                    <el-checkbox label="狗" value="dog" />
                    <el-checkbox label="仓鼠" value="hamster" />
                    <el-checkbox label="兔子" value="rabbit" />
                    <el-checkbox label="鸟" value="bird" />
                    <el-checkbox label="乌龟" value="turtle" />
                  </el-checkbox-group>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="自我介绍">
                  <el-input
                    v-model="form.introduction"
                    type="textarea"
                    :rows="4"
                    :disabled="!isEditing"
                    placeholder="请介绍一下自己的照顾经验和优势"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
          <div class="card-actions" v-if="!isEditing">
            <el-button type="primary" :icon="Edit" @click="handleEdit">编辑资料</el-button>
            <el-button type="warning" :icon="Key" @click="handleChangePassword">修改密码</el-button>
          </div>
          <div class="card-actions" v-else>
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </div>
        </el-card>

        <el-card class="info-card" shadow="never">
          <h3 class="card-title">服务价格</h3>
          <el-form :model="priceForm" label-width="120px">
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="猫(元/天)">
                  <el-input-number v-model="priceForm.cat" :min="0" :max="500" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="狗(元/天)">
                  <el-input-number v-model="priceForm.dog" :min="0" :max="500" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="仓鼠(元/天)">
                  <el-input-number v-model="priceForm.hamster" :min="0" :max="200" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="兔子(元/天)">
                  <el-input-number v-model="priceForm.rabbit" :min="0" :max="200" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="鸟(元/天)">
                  <el-input-number v-model="priceForm.bird" :min="0" :max="200" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="乌龟(元/天)">
                  <el-input-number v-model="priceForm.turtle" :min="0" :max="100" :disabled="!isEditing" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="info-card" shadow="never">
          <h3 class="card-title">服务统计</h3>
          <div class="chart-container">
            <div class="chart-bar">
              <div class="bar-label">待确认</div>
              <div class="bar-track">
                <div class="bar-fill pending" :style="{ width: '30%' }"></div>
              </div>
              <div class="bar-value">3</div>
            </div>
            <div class="chart-bar">
              <div class="bar-label">寄养中</div>
              <div class="bar-track">
                <div class="bar-fill fostering" :style="{ width: '50%' }"></div>
              </div>
              <div class="bar-value">5</div>
            </div>
            <div class="chart-bar">
              <div class="bar-label">已完成</div>
              <div class="bar-track">
                <div class="bar-fill completed" :style="{ width: '80%' }"></div>
              </div>
              <div class="bar-value">42</div>
            </div>
          </div>
        </el-card>

        <el-card class="info-card" shadow="never">
          <h3 class="card-title">近期评价</h3>
          <div class="review-list">
            <div class="review-item" v-for="review in reviewList" :key="review.id">
              <div class="review-header">
                <span class="reviewer-name">{{ review.reviewerName }}</span>
                <el-rate :model-value="review.rating" disabled text-color="#FF9900" :show-score="false" />
              </div>
              <p class="review-content">{{ review.content }}</p>
              <span class="review-date">{{ review.date }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="450px"
      :close-on-click-modal="false"
    >
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="80px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, type FormInstance, type FormRules } from 'vue'
import { ElMessage } from 'element-plus'
import { UserFilled, Camera, Edit, Key } from '@element-plus/icons-vue'

const isEditing = ref(false)
const passwordDialogVisible = ref(false)
const formRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const form = reactive({
  id: 1,
  name: '李阿姨',
  phone: '13800138011',
  gender: '2',
  age: 55,
  address: '阳光小区3号楼201室',
  distance: 1.2,
  experience: 5,
  avgRating: 4.8,
  certified: '1',
  careTypes: ['cat', 'dog'] as string[],
  introduction: '退休后开始照顾邻居家的宠物，有丰富的养猫养狗经验。家里有独立小院，环境优美，每天定时遛狗，提供优质猫粮狗粮。'
})

const priceForm = reactive({
  cat: 60,
  dog: 60,
  hamster: 30,
  rabbit: 35,
  bird: 25,
  turtle: 20
})

const stats = reactive({
  serviceCount: 42,
  avgRating: 4.8,
  income: 18600
})

const reviewList = ref([
  { id: 1, reviewerName: '张三', rating: 5, content: '李阿姨照顾得很好，猫咪回来很开心！', date: '2026-06-24' },
  { id: 2, reviewerName: '李四', rating: 5, content: '环境干净，狗狗玩得很开心，下次还来！', date: '2026-06-22' },
  { id: 3, reviewerName: '王五', rating: 4, content: '整体不错，就是价格稍微有点贵', date: '2026-06-20' }
])

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  address: [{ required: true, message: '请输入住址', trigger: 'blur' }]
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const handleAvatarEdit = () => {
  ElMessage.info('头像上传功能开发中')
}

const handleEdit = () => {
  isEditing.value = true
}

const handleSave = async () => {
  await formRef.value?.validate()
  ElMessage.success('保存成功')
  isEditing.value = false
}

const handleCancel = () => {
  isEditing.value = false
}

const handleChangePassword = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordDialogVisible.value = true
}

const handleSubmitPassword = async () => {
  await passwordFormRef.value?.validate()
  ElMessage.success('密码修改成功')
  passwordDialogVisible.value = false
}
</script>

<style scoped>
.caregiver-profile {
  padding: 0;
}

.profile-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 30px;
}

.avatar-section {
  position: relative;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #67C23A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  color: white;
}

.avatar-edit-btn {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #409EFF;
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 14px;
}

.basic-info {
  flex: 1;
}

.name {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.experience {
  font-size: 14px;
  color: #909399;
}

.rating {
  min-width: 100px;
}

.stats {
  display: flex;
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409EFF;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.info-card {
  border-radius: 12px;
  margin-bottom: 20px;
}

.card-title {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.chart-container {
  padding: 10px 0;
}

.chart-bar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.bar-label {
  width: 60px;
  font-size: 14px;
  color: #606266;
}

.bar-track {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.bar-fill.pending {
  background: #E6A23C;
}

.bar-fill.fostering {
  background: #409EFF;
}

.bar-fill.completed {
  background: #67C23A;
}

.bar-value {
  width: 40px;
  text-align: right;
  font-size: 14px;
  color: #606266;
  margin-left: 12px;
}

.review-list {
  padding: 10px 0;
}

.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.review-content {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.review-date {
  font-size: 12px;
  color: #909399;
}

:deep(.el-card__body) {
  padding: 24px;
}
</style>