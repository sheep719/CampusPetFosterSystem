import axios from './axios'

export interface FosterApplication {
  id: number
  petId: number
  locationId: number
  caregiverId: number
  ownerId: number
  startTime: string
  expectedEndTime: string
  actualStartTime: string
  actualEndTime: string
  careNote: string
  emergencyContact: string
  status: string
  rejectReason: string
  rating: number
  reviewText: string
  createTime: string
}

export interface ApplicationListParams {
  page?: number
  size?: number
  status?: string
  ownerId?: number
  caregiverId?: number
  petName?: string
}

export interface ApplicationListResponse {
  total: number
  list: FosterApplication[]
}

export const getApplicationList = (params: ApplicationListParams = {}): Promise<ApplicationListResponse> => {
  return axios.get('/foster-applications', { params })
}

export const getMyApplications = (): Promise<FosterApplication[]> => {
  return axios.get('/foster-applications/my')
}

export const getPendingForCaregiver = (): Promise<FosterApplication[]> => {
  return axios.get('/foster-applications/pending-caregiver')
}

export const getApplicationById = (id: number): Promise<FosterApplication> => {
  return axios.get(`/foster-applications/${id}`)
}

export const createApplication = (data: Partial<FosterApplication>): Promise<FosterApplication> => {
  return axios.post('/foster-applications', data)
}

export const reviewApplication = (id: number, approved: boolean, reason?: string): Promise<FosterApplication> => {
  return axios.put(`/foster-applications/${id}/review`, { approved, reason })
}

export const startFoster = (id: number): Promise<FosterApplication> => {
  return axios.put(`/foster-applications/${id}/start`)
}

export const endFoster = (id: number, data?: { rating?: number; reviewText?: string }): Promise<FosterApplication> => {
  return axios.put(`/foster-applications/${id}/end`, data)
}

export const cancelApplication = (id: number, reason?: string): Promise<FosterApplication> => {
  return axios.put(`/foster-applications/${id}/cancel`, { reason })
}

export const getExpiringSoon = (days: number = 3): Promise<FosterApplication[]> => {
  return axios.get('/foster-applications/expiring-soon', { params: { days } })
}

export const getOverdue = (): Promise<FosterApplication[]> => {
  return axios.get('/foster-applications/overdue')
}

export const getApplicationStats = (): Promise<Record<string, number>> => {
  return axios.get('/foster-applications/stats/summary')
}
