import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, registerOwner, registerCaregiver, type LoginResponse, type RegisterOwnerParams, type RegisterCaregiverParams } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const role = ref(localStorage.getItem('role') || '')
  const userId = ref<number | null>(null)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setRole = (newRole: string) => {
    role.value = newRole
    localStorage.setItem('role', newRole)
  }

  const setUserId = (id: number) => {
    userId.value = id
    localStorage.setItem('userId', String(id))
  }

  const clearUser = () => {
    token.value = ''
    role.value = ''
    userId.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  const handleLogin = async (username: string, password: string): Promise<boolean> => {
    try {
      const result: LoginResponse = await login(username, password)
      setToken(result.token)
      setRole(result.role)
      setUserId(result.userId)
      return true
    } catch {
      return false
    }
  }

  const handleRegisterOwner = async (params: RegisterOwnerParams): Promise<boolean> => {
    try {
      await registerOwner(params)
      return true
    } catch {
      return false
    }
  }

  const handleRegisterCaregiver = async (params: RegisterCaregiverParams): Promise<boolean> => {
    try {
      await registerCaregiver(params)
      return true
    } catch {
      return false
    }
  }

  return {
    token,
    role,
    userId,
    setToken,
    setRole,
    setUserId,
    clearUser,
    handleLogin,
    handleRegisterOwner,
    handleRegisterCaregiver
  }
})
