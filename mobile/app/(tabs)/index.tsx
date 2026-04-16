import React, { useCallback, useEffect, useState } from 'react';
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
  RefreshControl,
  TouchableOpacity,
  SafeAreaView,
} from 'react-native';
import { useRouter } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { format, startOfMonth, endOfMonth } from 'date-fns';
import SummaryCard from '../../src/components/SummaryCard';
import CategoryChart from '../../src/components/CategoryChart';
import BalanceChart from '../../src/components/BalanceChart';
import TransactionListItem from '../../src/components/TransactionListItem';
import { statisticsApi } from '../../src/api/statistics';
import { transactionsApi } from '../../src/api/transactions';
import { useAuthStore } from '../../src/stores/authStore';
import { StatisticsSummary, CategoryTotal, DailyBalance, Transaction } from '../../src/types';

export default function DashboardScreen() {
  const router = useRouter();
  const { logout } = useAuthStore();

  const now = new Date();
  const from = format(startOfMonth(now), 'yyyy-MM-dd');
  const to = format(endOfMonth(now), 'yyyy-MM-dd');

  const [summary, setSummary] = useState<StatisticsSummary | null>(null);
  const [categories, setCategories] = useState<CategoryTotal[]>([]);
  const [dailyBalance, setDailyBalance] = useState<DailyBalance[]>([]);
  const [recent, setRecent] = useState<Transaction[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadData = useCallback(async () => {
    try {
      setError(null);
      const [s, c, d, t] = await Promise.all([
        statisticsApi.getSummary(from, to),
        statisticsApi.getCategoryBreakdown(from, to),
        statisticsApi.getDailyBalance(from, to),
        transactionsApi.list({ size: 5 }),
      ]);
      setSummary(s);
      setCategories(c);
      setDailyBalance(d);
      setRecent(t.transactions);
    } catch {
      setError('Failed to load data. Is the backend running?');
    }
  }, [from, to]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const onRefresh = async () => {
    setRefreshing(true);
    await loadData();
    setRefreshing(false);
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Dashboard</Text>
        <View style={styles.headerActions}>
          <TouchableOpacity
            style={styles.fab}
            onPress={() => router.push('/add-transaction')}
          >
            <Ionicons name="add" size={22} color="#fff" />
          </TouchableOpacity>
          <TouchableOpacity onPress={logout} style={styles.logoutBtn}>
            <Ionicons name="log-out-outline" size={22} color="#6b7280" />
          </TouchableOpacity>
        </View>
      </View>

      <ScrollView
        contentContainerStyle={styles.scroll}
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
      >
        {error ? (
          <View style={styles.errorBox}>
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : null}

        {summary && (
          <SummaryCard
            income={Number(summary.totalIncome)}
            expenses={Number(summary.totalExpenses)}
          />
        )}

        <BalanceChart data={dailyBalance} />

        <CategoryChart data={categories} />

        {recent.length > 0 && (
          <View style={styles.section}>
            <View style={styles.sectionHeader}>
              <Text style={styles.sectionTitle}>Recent transactions</Text>
              <TouchableOpacity onPress={() => router.push('/(tabs)/transactions')}>
                <Text style={styles.seeAll}>See all</Text>
              </TouchableOpacity>
            </View>
            {recent.map((t) => (
              <TransactionListItem key={t.id} transaction={t} />
            ))}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: '#f9fafb',
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: '#fff',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#e5e7eb',
  },
  headerTitle: {
    fontSize: 20,
    fontWeight: '700',
    color: '#111827',
  },
  headerActions: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  fab: {
    backgroundColor: '#6366f1',
    borderRadius: 20,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoutBtn: {
    padding: 4,
  },
  scroll: {
    paddingBottom: 32,
  },
  errorBox: {
    margin: 16,
    padding: 12,
    backgroundColor: '#fef2f2',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#fca5a5',
  },
  errorText: {
    color: '#dc2626',
    fontSize: 13,
  },
  section: {
    marginTop: 8,
    backgroundColor: '#fff',
    borderRadius: 16,
    marginHorizontal: 16,
    overflow: 'hidden',
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 8,
    elevation: 2,
  },
  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 16,
    paddingBottom: 8,
  },
  sectionTitle: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
  },
  seeAll: {
    fontSize: 13,
    color: '#6366f1',
  },
});
