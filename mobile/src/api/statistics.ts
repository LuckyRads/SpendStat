import apiClient from './client';
import { StatisticsSummary, CategoryTotal, DailyBalance } from '../types';

export const statisticsApi = {
  getSummary: async (from: string, to: string): Promise<StatisticsSummary> => {
    const { data } = await apiClient.get<StatisticsSummary>('/api/statistics/summary', {
      params: { from, to },
    });
    return data;
  },

  getCategoryBreakdown: async (from: string, to: string): Promise<CategoryTotal[]> => {
    const { data } = await apiClient.get<CategoryTotal[]>('/api/statistics/category-breakdown', {
      params: { from, to },
    });
    return data;
  },

  getDailyBalance: async (from: string, to: string, accountId?: string): Promise<DailyBalance[]> => {
    const { data } = await apiClient.get<DailyBalance[]>('/api/statistics/daily-balance', {
      params: { from, to, ...(accountId ? { accountId } : {}) },
    });
    return data;
  },
};
