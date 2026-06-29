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

export interface ActivityItem {
  id: number
  type: string
  title: string
  content: string
  status: string
  time: string
}

export interface WeeklyTrend {
  dates: string[]
  counts: number[]
}

export interface TopCaregiver {
  id: number
  name: string
  avgRating: number
  totalCareCount: number
  distanceKm: number
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

export const getWeeklyTrend = (): Promise<WeeklyTrend> => {
  return axios.get('/dashboard/trend')
}

export const getRecentActivitiesList = (): Promise<ActivityItem[]> => {
  return axios.get('/dashboard/recent-activities')
}

export const getTopCaregivers = (): Promise<TopCaregiver[]> => {
  return axios.get('/dashboard/top-caregivers')
}
