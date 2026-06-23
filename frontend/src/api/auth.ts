import axios from './axios'

export interface LoginResponse {
  token: string
  role: string
  userId: number
}

export interface RegisterOwnerParams {
  username: string
  password: string
  phone: string
  studentNo: string
  name: string
  school: string
  major: string
  dorm: string
}

export interface RegisterCaregiverParams {
  username: string
  password: string
  phone: string
  name: string
  address: string
  distance: number
  careTypes: string
}

export const login = (username: string, password: string): Promise<LoginResponse> => {
  return axios.post('/auth/login', { username, password })
}

export const registerOwner = (params: RegisterOwnerParams): Promise<string> => {
  return axios.post('/auth/register/owner', params)
}

export const registerCaregiver = (params: RegisterCaregiverParams): Promise<string> => {
  return axios.post('/auth/register/caregiver', params)
}
