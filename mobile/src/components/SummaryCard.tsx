import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

interface Props {
  income: number;
  expenses: number;
  currency?: string;
}

export default function SummaryCard({ income, expenses, currency = 'EUR' }: Props) {
  const net = income - expenses;

  return (
    <View style={styles.card}>
      <Text style={styles.title}>This month</Text>
      <Text style={[styles.net, { color: net >= 0 ? '#22c55e' : '#ef4444' }]}>
        {net >= 0 ? '+' : ''}
        {net.toFixed(2)} {currency}
      </Text>
      <View style={styles.row}>
        <View style={styles.block}>
          <Text style={styles.label}>Income</Text>
          <Text style={[styles.value, { color: '#22c55e' }]}>
            +{income.toFixed(2)} {currency}
          </Text>
        </View>
        <View style={styles.divider} />
        <View style={styles.block}>
          <Text style={styles.label}>Expenses</Text>
          <Text style={[styles.value, { color: '#ef4444' }]}>
            -{expenses.toFixed(2)} {currency}
          </Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 20,
    marginHorizontal: 16,
    marginVertical: 8,
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 8,
    elevation: 2,
  },
  title: {
    fontSize: 13,
    color: '#6b7280',
    marginBottom: 4,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
  },
  net: {
    fontSize: 28,
    fontWeight: '700',
    marginBottom: 16,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  block: {
    flex: 1,
  },
  divider: {
    width: 1,
    height: 36,
    backgroundColor: '#e5e7eb',
    marginHorizontal: 16,
  },
  label: {
    fontSize: 12,
    color: '#6b7280',
    marginBottom: 2,
  },
  value: {
    fontSize: 16,
    fontWeight: '600',
  },
});
