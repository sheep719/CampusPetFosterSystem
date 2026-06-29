import axios from './axios'

export interface Role {
  roleCode: string
  roleName: string
  description: string
  enabled: boolean
}

export interface RoleDetail extends Role {
  permissions: string[]
}

export const getRoleList = (): Promise<Role[]> => {
  return axios.get('/roles')
}

export const getRoleByCode = (roleCode: string): Promise<RoleDetail> => {
  return axios.get(`/roles/${roleCode}`)
}

export const getRoleUsersCount = (roleCode: string): Promise<{ roleCode: string; userCount: number }> => {
  return axios.get(`/roles/${roleCode}/users-count`)
}
