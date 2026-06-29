import axios from './axios'

export interface SysUser {
  id: number
  username: string
  password?: string
  roleCode: string
  phone: string
  enabled: number
  createTime: string
  roleName?: string
}

export interface UserListParams {
  page?: number
  size?: number
  username?: string
  roleCode?: string
  phone?: string
}

export interface UserListResponse {
  total: number
  list: SysUser[]
}

export const getUserList = (params: UserListParams = {}): Promise<UserListResponse> => {
  return axios.get('/users', { params })
}

export const getUserById = (id: number): Promise<SysUser> => {
  return axios.get(`/users/${id}`)
}

export const getCurrentUser = (): Promise<SysUser> => {
  return axios.get('/users/current')
}

export const createUser = (data: Partial<SysUser>): Promise<SysUser> => {
  return axios.post('/users', data)
}

export const updateUser = (id: number, data: Partial<SysUser>): Promise<SysUser> => {
  return axios.put(`/users/${id}`, data)
}

export const deleteUser = (id: number): Promise<string> => {
  return axios.delete(`/users/${id}`)
}

export const resetPassword = (id: number, password: string): Promise<string> => {
  return axios.put(`/users/${id}/reset-password`, { password })
}

export const toggleUserStatus = (id: number): Promise<SysUser> => {
  return axios.put(`/users/${id}/toggle-status`)
}

export const getUserStats = (): Promise<Record<string, number>> => {
  return axios.get('/users/stats/summary')
}
