import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { Transaction } from '../types';
import { format, parseISO } from 'date-fns';

interface Props {
  transaction: Transaction;
  onPress?: () => void;
}

export default function TransactionListItem({ transaction, onPress }: Props) {
  const isIncome = transaction.type === 'INCOME';
  const amount = Math.abs(transaction.amount);
  const sign = isIncome ? '+' : '-';
  const color = isIncome ? '#22c55e' : '#ef4444';

  return (
    <TouchableOpacity style={styles.container} onPress={onPress} activeOpacity={0.7}>
      <View style={styles.left}>
        <View style={[styles.dot, { backgroundColor: color }]} />
        <View>
          <Text style={styles.description} numberOfLines={1}>
            {transaction.merchant ?? transaction.description ?? 'No description'}
          </Text>
          <Text style={styles.meta}>
            {transaction.categoryName ?? 'Uncategorised'} ·{' '}
            {format(parseISO(transaction.txDate), 'dd MMM')}
          </Text>
        </View>
      </View>
      <Text style={[styles.amount, { color }]}>
        {sign}
        {amount.toFixed(2)} {transaction.currency}
      </Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 12,
    backgroundColor: '#fff',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#e5e7eb',
  },
  left: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    flex: 1,
  },
  dot: {
    width: 10,
    height: 10,
    borderRadius: 5,
  },
  description: {
    fontSize: 15,
    fontWeight: '500',
    color: '#111827',
    maxWidth: 200,
  },
  meta: {
    fontSize: 13,
    color: '#6b7280',
    marginTop: 2,
  },
  amount: {
    fontSize: 15,
    fontWeight: '600',
  },
});
