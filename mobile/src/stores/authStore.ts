import { create } from 'zustand';
import { tokenStorage } from '../api/client';
import { authApi } from '../api/auth';

interface AuthState {
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  hydrate: () => Promise<void>;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  clearError: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  isAuthenticated: false,
  isLoading: true,
  error: null,

  hydrate: async () => {
    try {
      const token = await tokenStorage.getAccessToken();
      set({ isAuthenticated: !!token, isLoading: false });
    } catch {
      set({ isAuthenticated: false, isLoading: false });
    }
  },

  login: async (email, password) => {
    set({ isLoading: true, error: null });
    try {
      const tokens = await authApi.login({ email, password });
      await tokenStorage.setTokens(tokens.accessToken, tokens.refreshToken);
      set({ isAuthenticated: true, isLoading: false });
    } catch (e: unknown) {
      const msg = extractErrorMessage(e) ?? 'Invalid email or password';
      set({ error: msg, isLoading: false });
      throw e;
    }
  },

  register: async (email, password) => {
    set({ isLoading: true, error: null });
    try {
      const tokens = await authApi.register({ email, password });
      await tokenStorage.setTokens(tokens.accessToken, tokens.refreshToken);
      set({ isAuthenticated: true, isLoading: false });
    } catch (e: unknown) {
      const msg = extractErrorMessage(e) ?? 'Registration failed';
      set({ error: msg, isLoading: false });
      throw e;
    }
  },

  logout: async () => {
    await tokenStorage.clearTokens();
    set({ isAuthenticated: false });
  },

  clearError: () => set({ error: null }),
}));

function extractErrorMessage(e: unknown): string | null {
  if (e && typeof e === 'object' && 'response' in e) {
    const res = (e as { response?: { data?: { detail?: string; title?: string } } }).response;
    return res?.data?.detail ?? res?.data?.title ?? null;
  }
  return null;
}
