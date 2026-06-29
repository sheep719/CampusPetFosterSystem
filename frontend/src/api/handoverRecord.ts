import axios from './axios'

export interface HandoverRecord {
  id: number
  applicationId: number
  handoverType: string
  handoverTime: string
  fromPerson: string
  toPerson: string
  remark: string
  photoUrl: string
}

export interface RecordListParams {
  page?: number
  size?: number
  applicationId?: number
  handoverType?: string
}

export interface RecordListResponse {
  total: number
  list: HandoverRecord[]
}

export const getHandoverRecordList = (params: RecordListParams = {}): Promise<RecordListResponse> => {
  return axios.get('/handover-records', { params })
}

export const getRecordsByApplication = (applicationId: number): Promise<HandoverRecord[]> => {
  return axios.get(`/handover-records/application/${applicationId}`)
}

export const getHandoverRecordById = (id: number): Promise<HandoverRecord> => {
  return axios.get(`/handover-records/${id}`)
}

export const createHandoverRecord = (data: Partial<HandoverRecord>): Promise<HandoverRecord> => {
  return axios.post('/handover-records', data)
}

export const updateHandoverRecord = (id: number, data: Partial<HandoverRecord>): Promise<HandoverRecord> => {
  return axios.put(`/handover-records/${id}`, data)
}

export const deleteHandoverRecord = (id: number): Promise<string> => {
  return axios.delete(`/handover-records/${id}`)
}

export const getHandoverStats = (): Promise<Record<string, number>> => {
  return axios.get('/handover-records/stats/summary')
}
