import React, { useCallback, useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
  SafeAreaView,
  Alert,
} from 'react-native';
import { useRouter } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import TransactionListItem from '../../src/components/TransactionListItem';
import { transactionsApi } from '../../src/api/transactions';
import { Transaction } from '../../src/types';

const PAGE_SIZE = 20;

export default function TransactionsScreen() {
  const router = useRouter();
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [page, setPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);

  const loadPage = useCallback(async (p: number, replace = false) => {
    setLoading(true);
    try {
      const result = await transactionsApi.list({ page: p, size: PAGE_SIZE });
      setTotalCount(result.totalCount);
      setTransactions((prev) => (replace ? result.transactions : [...prev, ...result.transactions]));
      setPage(p);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadPage(0, true);
  }, [loadPage]);

  const onRefresh = async () => {
    setRefreshing(true);
    await loadPage(0, true);
    setRefreshing(false);
  };

  const onEndReached = () => {
    const loaded = transactions.length;
    if (!loading && loaded < totalCount) {
      loadPage(page + 1);
    }
  };

  const onDelete = (id: string) => {
    Alert.alert('Delete transaction', 'Are you sure?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try {
            await transactionsApi.delete(id);
            setTransactions((prev) => prev.filter((t) => t.id !== id));
            setTotalCount((c) => c - 1);
          } catch {
            Alert.alert('Error', 'Failed to delete transaction.');
          }
        },
      },
    ]);
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Transactions</Text>
        <TouchableOpacity
          style={styles.fab}
          onPress={() => router.push('/add-transaction')}
        >
          <Ionicons name="add" size={22} color="#fff" />
        </TouchableOpacity>
      </View>

      <FlatList
        data={transactions}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TransactionListItem
            transaction={item}
            onPress={() => onDelete(item.id)}
          />
        )}
        onEndReached={onEndReached}
        onEndReachedThreshold={0.3}
        onRefresh={onRefresh}
        refreshing={refreshing}
        ListFooterComponent={loading ? <ActivityIndicator style={styles.loader} /> : null}
        ListEmptyComponent={
          !loading ? (
            <View style={styles.empty}>
              <Ionicons name="receipt-outline" size={48} color="#d1d5db" />
              <Text style={styles.emptyText}>No transactions yet</Text>
              <TouchableOpacity onPress={() => router.push('/add-transaction')}>
                <Text style={styles.emptyAction}>Add your first one</Text>
              </TouchableOpacity>
            </View>
          ) : null
        }
      />
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
  fab: {
    backgroundColor: '#6366f1',
    borderRadius: 20,
    width: 36,
    height: 36,
    alignItems: 'center',
    justifyContent: 'center',
  },
  loader: {
    paddingVertical: 16,
  },
  empty: {
    flex: 1,
    alignItems: 'center',
    paddingTop: 80,
    gap: 8,
  },
  emptyText: {
    fontSize: 16,
    color: '#6b7280',
  },
  emptyAction: {
    fontSize: 14,
    color: '#6366f1',
    marginTop: 4,
  },
});
