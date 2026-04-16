import apiClient from './client';
import { Account } from '../types';

export interface CreateAccountRequest {
  name: string;
  currency: string;
  initialBalance: number;
}

export const accountsApi = {
  list: async (): Promise<Account[]> => {
    const { data } = await apiClient.get<Account[]>('/api/accounts');
    return data;
  },

  create: async (body: CreateAccountRequest): Promise<Account> => {
    const { data } = await apiClient.post<Account>('/api/accounts', body);
    return data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/accounts/${id}`);
  },
};
