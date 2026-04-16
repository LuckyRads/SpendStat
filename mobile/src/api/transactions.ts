import apiClient from './client';
import { Transaction, TransactionFilter, PagedTransactions, TransactionType } from '../types';

export interface CreateTransactionRequest {
  accountId: string;
  categoryId?: string;
  amount: number;
  currency: string;
  type: TransactionType;
  description?: string;
  merchant?: string;
  txDate: string;
}

export const transactionsApi = {
  list: async (filter: TransactionFilter = {}): Promise<PagedTransactions> => {
    const params = new URLSearchParams();
    if (filter.accountId) params.set('accountId', filter.accountId);
    if (filter.categoryId) params.set('categoryId', filter.categoryId);
    if (filter.type) params.set('type', filter.type);
    if (filter.from) params.set('from', filter.from);
    if (filter.to) params.set('to', filter.to);
    if (filter.page != null) params.set('page', String(filter.page));
    if (filter.size != null) params.set('size', String(filter.size));

    const { data } = await apiClient.get<PagedTransactions>(`/api/transactions?${params}`);
    return data;
  },

  create: async (body: CreateTransactionRequest): Promise<Transaction> => {
    const { data } = await apiClient.post<Transaction>('/api/transactions', body);
    return data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/api/transactions/${id}`);
  },
};
