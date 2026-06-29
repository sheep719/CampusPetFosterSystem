<template>
  <div class="main-layout">
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="sidebar-header">
        <h2>🐾 宠物寄养系统</h2>
      </div>
      <el-menu :default-active="activeMenu" mode="vertical" class="sidebar-menu" :collapse="isCollapsed">
        <template v-if="role === 'admin'">
          <el-menu-item index="/dashboard">
            <el-icon><component :is="DataAnalysis" /></el-icon>
            <template #title>数据看板</template>
          </el-menu-item>
          <el-menu-item index="/pet">
            <el-icon><component :is="House" /></el-icon>
            <template #title>宠物档案</template>
          </el-menu-item>
          <el-menu-item index="/caregiver">
            <el-icon><component :is="HomeFilled" /></el-icon>
            <template #title>被寄养者管理</template>
          </el-menu-item>
          <el-menu-item index="/foster-location">
            <el-icon><component :is="MapLocation" /></el-icon>
            <template #title>寄养点管理</template>
          </el-menu-item>
          <el-menu-item index="/foster-application">
            <el-icon><component :is="Document" /></el-icon>
            <template #title>寄养申请管理</template>
          </el-menu-item>
          <el-menu-item index="/handover">
            <el-icon><component :is="Connection" /></el-icon>
            <template #title>交接记录管理</template>
          </el-menu-item>
          <el-sub-menu index="/system">
            <template #title>
              <el-icon><component :is="Setting" /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/system/user">用户管理</el-menu-item>
            <el-menu-item index="/system/role">角色管理</el-menu-item>
            <el-menu-item index="/system/menu">菜单管理</el-menu-item>
            <el-menu-item index="/system/dict">字典管理</el-menu-item>
          </el-sub-menu>
        </template>
        <template v-else-if="role === 'owner'">
          <el-menu-item index="/owner-dashboard">
            <el-icon><component :is="DataAnalysis" /></el-icon>
            <template #title>我的首页</template>
          </el-menu-item>
          <el-menu-item index="/owner-pets">
            <el-icon><component :is="House" /></el-icon>
            <template #title>我的宠物</template>
          </el-menu-item>
          <el-menu-item index="/owner-applications">
            <el-icon><component :is="Document" /></el-icon>
            <template #title>寄养申请</template>
          </el-menu-item>
          <el-menu-item index="/owner-caregivers">
            <el-icon><component :is="HomeFilled" /></el-icon>
            <template #title>被寄养者列表</template>
          </el-menu-item>
          <el-menu-item index="/owner-handover">
            <el-icon><component :is="Connection" /></el-icon>
            <template #title>交接记录</template>
          </el-menu-item>
        </template>
        <template v-else-if="role === 'caregiver'">
          <el-menu-item index="/caregiver-dashboard">
            <el-icon><component :is="DataAnalysis" /></el-icon>
            <template #title>我的首页</template>
          </el-menu-item>
          <el-menu-item index="/caregiver-applications">
            <el-icon><component :is="Document" /></el-icon>
            <template #title>寄养申请</template>
          </el-menu-item>
          <el-menu-item index="/caregiver-pets">
            <el-icon><component :is="House" /></el-icon>
            <template #title>寄养中宠物</template>
          </el-menu-item>
          <el-menu-item index="/caregiver-profile">
            <el-icon><component :is="User" /></el-icon>
            <template #title>我的资料</template>
          </el-menu-item>
          <el-menu-item index="/caregiver-handover">
            <el-icon><component :is="Connection" /></el-icon>
            <template #title>交接记录</template>
          </el-menu-item>
        </template>
      </el-menu>
    </aside>
    <main class="main-content">
      <header class="header">
        <div class="header-left">
          <el-button :icon="isCollapsed ? Expand : Fold" @click="toggleSidebar" class="toggle-btn" text />
          <span class="breadcrumb">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <span class="welcome-text">欢迎回来，{{ getRoleText() }} 👋</span>
          <NotificationCenter />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" class="user-avatar">
                {{ avatarText }}
              </el-avatar>
              <span class="username">{{ username }}</span>
              <el-icon><component :is="ArrowDown" /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><component :is="User" /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="logout" divided>
                  <el-icon><component :is="SwitchButton" /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>
      <div class="content-wrapper">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DataAnalysis, House, HomeFilled, MapLocation, Document, Setting, User, ArrowDown, Fold, Expand, SwitchButton, Connection } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import NotificationCenter from '@/components/NotificationCenter.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const isCollapsed = ref(false)
const role = computed(() => userStore.role || localStorage.getItem('role') || '')
const username = computed(() => localStorage.getItem('username') || '用户')
const avatarText = computed(() => username.value.charAt(0).toUpperCase())

const activeMenu = computed(() => route.path)

const pageTitleMap: Record<string, string> = {
  '/dashboard': '数据看板',
  '/pet': '宠物档案',
  '/caregiver': '被寄养者管理',
  '/foster-location': '寄养点管理',
  '/foster-application': '寄养申请管理',
  '/handover': '交接记录管理',
  '/system/user': '用户管理',
  '/system/role': '角色管理',
  '/system/menu': '菜单管理',
  '/system/dict': '字典管理',
  '/owner-dashboard': '我的首页',
  '/owner-pets': '我的宠物',
  '/owner-applications': '寄养申请',
  '/owner-caregivers': '被寄养者列表',
  '/owner-handover': '交接记录',
  '/caregiver-dashboard': '我的首页',
  '/caregiver-applications': '寄养申请',
  '/caregiver-pets': '寄养中宠物',
  '/caregiver-profile': '我的资料',
  '/caregiver-handover': '交接记录',
  '/notification': '通知中心'
}

const currentPageTitle = computed(() => pageTitleMap[route.path] || '首页')

const getRoleText = () => {
  const roleMap: Record<string, string> = {
    owner: '寄养者',
    caregiver: '被寄养者',
    admin: '管理员'
  }
  return roleMap[role.value] || '用户'
}

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.clearUser()
      localStorage.removeItem('username')
      ElMessage.success('退出成功')
      router.push('/')
    }).catch(() => {})
  } else if (command === 'profile') {
    ElMessage.info('个人中心功能开发中')
  }
}

onMounted(() => {
  if (role.value === 'owner' && route.path === '/dashboard') {
    router.push('/owner-dashboard')
  } else if (role.value === 'caregiver' && route.path === '/dashboard') {
    router.push('/caregiver-dashboard')
  }
})
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #2d3a4b 0%, #1e2836 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  flex-shrink: 0;
}

.sidebar.collapsed {
  width: 64px;
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
}

.sidebar-header h2 {
  font-size: 16px;
  margin: 0;
  white-space: nowrap;
}

.sidebar.collapsed .sidebar-header h2 {
  display: none;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

:deep(.el-menu) {
  border-right: none;
  background: transparent;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.7);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #fff;
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  overflow: hidden;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toggle-btn {
  font-size: 18px;
}

.breadcrumb {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.welcome-text {
  font-size: 14px;
  color: #606266;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #303133;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}

.username {
  font-size: 14px;
  font-weight: 500;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>