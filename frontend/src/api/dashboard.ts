import axios from './axios'

export interface DashboardStats {
  totalPets: number
  fosteringPets: number
  monthlyApplications: number
  pendingApplications: number
  todayExpiring: number
  totalLocations: number
  totalUsers: number
}

export interface RecentActivities {
  recentApplications: any[]
  recentHandovers: any[]
}

export const getDashboardStats = (): Promise<DashboardStats> => {
  return axios.get('/dashboard/stats')
}

export const getApplicationStatusDistribution = (): Promise<Record<string, number>> => {
  return axios.get('/dashboard/application-status')
}

export const getPetSpeciesDistribution = (): Promise<Record<string, number>> => {
  return axios.get('/dashboard/pet-species')
}

export const getUserRoleDistribution = (): Promise<Record<string, number>> => {
  return axios.get('/dashboard/user-roles')
}

export const getLocationStatusDistribution = (): Promise<Record<string, number>> => {
  return axios.get('/dashboard/location-status')
}

export const getRecentActivities = (): Promise<RecentActivities> => {
  return axios.get('/dashboard/recent-activities')
}

export const getSystemOverview = (): Promise<Record<string, any>> => {
  return axios.get('/dashboard/overview')
}
