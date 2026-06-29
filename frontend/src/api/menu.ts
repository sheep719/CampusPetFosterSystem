import axios from './axios'

export interface MenuItem {
  id: string
  name: string
  component: string
  path: string
  icon: string
  roles: string[]
  children?: MenuItem[]
}

export const getMenuList = (): Promise<MenuItem[]> => {
  return axios.get('/menus')
}

export const getMenusByRole = (roleCode: string): Promise<MenuItem[]> => {
  return axios.get(`/menus/role/${roleCode}`)
}

export const getMenuById = (id: string): Promise<MenuItem> => {
  return axios.get(`/menus/${id}`)
}
