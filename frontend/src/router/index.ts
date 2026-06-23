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
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: '/pet',
        name: 'Pet',
        component: () => import('@/views/PetManagement.vue')
      },
      {
        path: '/caregiver',
        name: 'Caregiver',
        component: () => import('@/views/CaregiverManagement.vue')
      },
      {
        path: '/foster-location',
        name: 'FosterLocation',
        component: () => import('@/views/FosterLocationManagement.vue')
      },
      {
        path: '/foster-application',
        name: 'FosterApplication',
        component: () => import('@/views/FosterApplicationManagement.vue')
      },
      {
        path: '/system/user',
        name: 'UserManagement',
        component: () => import('@/views/system/UserManagement.vue')
      },
      {
        path: '/system/role',
        name: 'RoleManagement',
        component: () => import('@/views/system/RoleManagement.vue')
      },
      {
        path: '/system/menu',
        name: 'MenuManagement',
        component: () => import('@/views/system/MenuManagement.vue')
      },
      {
        path: '/system/dict',
        name: 'DictManagement',
        component: () => import('@/views/system/DictManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path !== '/' && to.path !== '/register') {
    const token = localStorage.getItem('token')
    if (!token) {
      return '/'
    }
  }
})

export default router
