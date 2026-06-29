import axios from './axios'

export interface FosterLocation {
  id: number
  caregiverId: number
  locationName: string
  address: string
  distanceKm: number
  capacity: number
  acceptedSpecies: string
  carePeriod: string
  costDesc: string
  available: number
  environmentPhotos: string
}

export interface LocationListParams {
  page?: number
  size?: number
  locationName?: string
  available?: number
  acceptedSpecies?: string
}

export interface LocationListResponse {
  total: number
  list: FosterLocation[]
}

export const getLocationList = (params: LocationListParams = {}): Promise<LocationListResponse> => {
  return axios.get('/foster-locations', { params })
}

export const getMyLocations = (): Promise<FosterLocation[]> => {
  return axios.get('/foster-locations/my')
}

export const getAvailableLocations = (species?: string): Promise<FosterLocation[]> => {
  return axios.get('/foster-locations/available', { params: { species } })
}

export const getLocationById = (id: number): Promise<FosterLocation> => {
  return axios.get(`/foster-locations/${id}`)
}

export const createLocation = (data: Partial<FosterLocation>): Promise<FosterLocation> => {
  return axios.post('/foster-locations', data)
}

export const updateLocation = (id: number, data: Partial<FosterLocation>): Promise<FosterLocation> => {
  return axios.put(`/foster-locations/${id}`, data)
}

export const deleteLocation = (id: number): Promise<string> => {
  return axios.delete(`/foster-locations/${id}`)
}

export const toggleLocationStatus = (id: number): Promise<FosterLocation> => {
  return axios.put(`/foster-locations/${id}/toggle-status`)
}

export const getLocationStats = (): Promise<Record<string, number>> => {
  return axios.get('/foster-locations/stats/summary')
}
