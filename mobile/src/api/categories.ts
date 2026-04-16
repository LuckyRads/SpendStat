import apiClient from './client';
import { Category, TransactionType } from '../types';

export interface CreateCategoryRequest {
  name: string;
  type: TransactionType;
}

export const categoriesApi = {
  list: async (): Promise<Category[]> => {
    const { data } = await apiClient.get<Category[]>('/api/categories');
    return data;
  },

  create: async (body: CreateCategoryRequest): Promise<Category> => {
    const { data } = await apiClient.post<Category>('/api/categories', body);
    return data;
  },
};
