<template>
  <div class="main-layout">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2>🐾 宠物寄养系统</h2>
      </div>
      <el-menu :default-active="activeMenu" mode="vertical" class="sidebar-menu">
        <el-menu-item index="/dashboard">
          <el-icon><component :is="Dashboard" /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/pet">
          <el-icon><component :is="Cat" /></el-icon>
          <span>宠物档案</span>
        </el-menu-item>
        <el-menu-item index="/caregiver">
          <el-icon><component :is="Home" /></el-icon>
          <span>被寄养者管理</span>
        </el-menu-item>
        <el-menu-item index="/foster-location">
          <el-icon><component :is="MapLocation" /></el-icon>
          <span>寄养点管理</span>
        </el-menu-item>
        <el-menu-item index="/foster-application">
          <el-icon><component :is="Document" /></el-icon>
          <span>寄养申请管理</span>
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
      </el-menu>
    </aside>
    <main class="main-content">
      <header class="header">
        <div class="header-left">
          <el-button icon="Menu" @click="toggleSidebar" class="toggle-btn" />
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><component :is="User" /></el-icon>
              <span>{{ getRoleText() }}</span>
              <el-icon><component :is="ArrowDown" /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
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
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Dashboard, Cat, Home, MapLocation, Document, Setting, User, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const activeMenu = computed(() => route.path)

const getRoleText = () => {
  const role = localStorage.getItem('role')
  const roleMap: Record<string, string> = {
    owner: '寄养者',
    caregiver: '被寄养者',
    admin: '管理员'
  }
  return roleMap[role || ''] || '用户'
}

const toggleSidebar = () => {}

const handleCommand = (command: string) => {
  if (command === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    ElMessage.success('退出成功')
    router.push('/')
  }
}
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
}

.sidebar-header {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  font-size: 16px;
  margin: 0;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  margin-right: 10px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #303133;
}

.content-wrapper {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
