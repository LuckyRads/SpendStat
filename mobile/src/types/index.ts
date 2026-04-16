export type TransactionType = 'INCOME' | 'EXPENSE';

export interface User {
  id: string;
  email: string;
  createdAt: string;
}

export interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

export interface Account {
  id: string;
  name: string;
  currency: string;
  initialBalance: number;
  createdAt: string;
}

export interface Category {
  id: string;
  name: string;
  type: TransactionType;
  isSystem: boolean;
}

export interface Transaction {
  id: string;
  accountId: string;
  categoryId: string | null;
  categoryName: string | null;
  amount: number;
  currency: string;
  type: TransactionType;
  description: string | null;
  merchant: string | null;
  txDate: string;
  createdAt: string;
}

export interface TransactionFilter {
  accountId?: string;
  categoryId?: string;
  type?: TransactionType;
  from?: string;
  to?: string;
  page?: number;
  size?: number;
}

export interface PagedTransactions {
  transactions: Transaction[];
  totalCount: number;
}

export interface StatisticsSummary {
  totalIncome: number;
  totalExpenses: number;
  netBalance: number;
}

export interface CategoryTotal {
  categoryId: string | null;
  name: string;
  total: number;
}

export interface DailyBalance {
  date: string;
  balance: number;
}
