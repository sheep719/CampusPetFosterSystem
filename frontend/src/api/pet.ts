import axios from './axios'

export interface Pet {
  id: number
  ownerId: number
  petName: string
  species: string
  breed: string
  gender: string
  age: number
  photoUrl: string
  healthNote: string
  dietNote: string
  vaccineStatus: string
  dewormStatus: string
}

export interface PetListParams {
  page?: number
  size?: number
  petName?: string
  species?: string
  vaccineStatus?: string
}

export interface PetListResponse {
  total: number
  list: Pet[]
}

export const getPetList = (params: PetListParams = {}): Promise<PetListResponse> => {
  return axios.get('/pets', { params })
}

export const getMyPets = (): Promise<Pet[]> => {
  return axios.get('/pets/my')
}

export const getPetById = (id: number): Promise<Pet> => {
  return axios.get(`/pets/${id}`)
}

export const createPet = (data: Partial<Pet>): Promise<Pet> => {
  return axios.post('/pets', data)
}

export const updatePet = (id: number, data: Partial<Pet>): Promise<Pet> => {
  return axios.put(`/pets/${id}`, data)
}

export const deletePet = (id: number): Promise<string> => {
  return axios.delete(`/pets/${id}`)
}

export const getSpeciesStats = (): Promise<Record<string, number>> => {
  return axios.get('/pets/stats/species')
}

export const getVaccineStats = (): Promise<Record<string, number>> => {
  return axios.get('/pets/stats/vaccine')
}
