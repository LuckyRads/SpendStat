import apiClient from './client';
import { TokenPair } from '../types';

export interface RegisterRequest {
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export const authApi = {
  register: async (body: RegisterRequest): Promise<TokenPair> => {
    const { data } = await apiClient.post<TokenPair>('/api/auth/register', body);
    return data;
  },

  login: async (body: LoginRequest): Promise<TokenPair> => {
    const { data } = await apiClient.post<TokenPair>('/api/auth/login', body);
    return data;
  },

  refresh: async (refreshToken: string): Promise<TokenPair> => {
    const { data } = await apiClient.post<TokenPair>('/api/auth/refresh', { refreshToken });
    return data;
  },
};
