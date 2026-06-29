import axios from './axios'

export interface DictType {
  dictCode: string
  dictName: string
  description: string
}

export interface DictDataItem {
  value: string
  label: string
  sortOrder: string
  remark?: string
}

export interface DictTypeDetail extends DictType {
  data: DictDataItem[]
}

export const getDictTypeList = (): Promise<DictType[]> => {
  return axios.get('/dicts/types')
}

export const getDictDataByCode = (dictCode: string): Promise<DictDataItem[]> => {
  return axios.get(`/dicts/${dictCode}`)
}

export const getDictTypeByCode = (dictCode: string): Promise<DictTypeDetail> => {
  return axios.get(`/dicts/types/${dictCode}`)
}
