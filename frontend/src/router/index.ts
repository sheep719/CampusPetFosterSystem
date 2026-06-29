import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/pet',
        name: 'Pet',
        component: () => import('@/views/PetManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/caregiver',
        name: 'Caregiver',
        component: () => import('@/views/CaregiverManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/foster-location',
        name: 'FosterLocation',
        component: () => import('@/views/FosterLocationManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/foster-application',
        name: 'FosterApplication',
        component: () => import('@/views/FosterApplicationManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/system/user',
        name: 'UserManagement',
        component: () => import('@/views/system/UserManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/system/role',
        name: 'RoleManagement',
        component: () => import('@/views/system/RoleManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/system/menu',
        name: 'MenuManagement',
        component: () => import('@/views/system/MenuManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/system/dict',
        name: 'DictManagement',
        component: () => import('@/views/system/DictManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/owner-dashboard',
        name: 'OwnerDashboard',
        component: () => import('@/views/owner/OwnerDashboard.vue'),
        meta: { role: 'owner' }
      },
      {
        path: '/owner-pets',
        name: 'OwnerPets',
        component: () => import('@/views/owner/OwnerPets.vue'),
        meta: { role: 'owner' }
      },
      {
        path: '/owner-applications',
        name: 'OwnerApplications',
        component: () => import('@/views/owner/OwnerApplications.vue'),
        meta: { role: 'owner' }
      },
      {
        path: '/owner-caregivers',
        name: 'OwnerCaregivers',
        component: () => import('@/views/owner/OwnerCaregivers.vue'),
        meta: { role: 'owner' }
      },
      {
        path: '/caregiver-dashboard',
        name: 'CaregiverDashboard',
        component: () => import('@/views/caregiver/CaregiverDashboard.vue'),
        meta: { role: 'caregiver' }
      },
      {
        path: '/caregiver-applications',
        name: 'CaregiverApplications',
        component: () => import('@/views/caregiver/CaregiverApplications.vue'),
        meta: { role: 'caregiver' }
      },
      {
        path: '/caregiver-pets',
        name: 'CaregiverPets',
        component: () => import('@/views/caregiver/CaregiverPets.vue'),
        meta: { role: 'caregiver' }
      },
      {
        path: '/caregiver-profile',
        name: 'CaregiverProfile',
        component: () => import('@/views/caregiver/CaregiverProfile.vue'),
        meta: { role: 'caregiver' }
      },
      {
        path: '/handover',
        name: 'HandoverManagement',
        component: () => import('@/views/HandoverManagement.vue'),
        meta: { role: 'admin' }
      },
      {
        path: '/owner-handover',
        name: 'OwnerHandover',
        component: () => import('@/views/owner/OwnerHandover.vue'),
        meta: { role: 'owner' }
      },
      {
        path: '/caregiver-handover',
        name: 'CaregiverHandover',
        component: () => import('@/views/caregiver/CaregiverHandover.vue'),
        meta: { role: 'caregiver' }
      },
      {
        path: '/notification',
        name: 'Notification',
        component: () => import('@/views/NotificationPage.vue'),
        meta: { role: '' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role') || ''

  if (to.path === '/' || to.path === '/register') {
    if (token) {
      if (role === 'admin') return '/dashboard'
      if (role === 'owner') return '/owner-dashboard'
      if (role === 'caregiver') return '/caregiver-dashboard'
    }
    return
  }

  if (!token) {
    return '/'
  }

  const targetRole = to.meta.role as string
  if (targetRole && targetRole !== role) {
    if (role === 'admin') return '/dashboard'
    if (role === 'owner') return '/owner-dashboard'
    if (role === 'caregiver') return '/caregiver-dashboard'
  }
})

export default router