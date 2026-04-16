import React from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';
import { PieChart } from 'react-native-chart-kit';
import { CategoryTotal } from '../types';

const COLORS = [
  '#6366f1', '#f59e0b', '#10b981', '#ef4444', '#3b82f6',
  '#8b5cf6', '#ec4899', '#14b8a6', '#f97316', '#84cc16',
];

interface Props {
  data: CategoryTotal[];
  currency?: string;
}

export default function CategoryChart({ data, currency = 'EUR' }: Props) {
  const screenWidth = Dimensions.get('window').width;

  if (!data.length) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No expense data</Text>
      </View>
    );
  }

  const chartData = data.slice(0, 10).map((item, i) => ({
    name: item.name,
    population: Number(item.total),
    color: COLORS[i % COLORS.length],
    legendFontColor: '#374151',
    legendFontSize: 12,
  }));

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Expenses by category</Text>
      <PieChart
        data={chartData}
        width={screenWidth - 32}
        height={180}
        chartConfig={{
          color: (opacity = 1) => `rgba(0, 0, 0, ${opacity})`,
        }}
        accessor="population"
        backgroundColor="transparent"
        paddingLeft="10"
        absolute={false}
      />
      <View style={styles.list}>
        {data.slice(0, 5).map((item, i) => (
          <View key={item.categoryId ?? i} style={styles.row}>
            <View style={[styles.dot, { backgroundColor: COLORS[i % COLORS.length] }]} />
            <Text style={styles.name} numberOfLines={1}>{item.name}</Text>
            <Text style={styles.amount}>
              {Number(item.total).toFixed(2)} {currency}
            </Text>
          </View>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#fff',
    borderRadius: 16,
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 8,
    shadowColor: '#000',
    shadowOpacity: 0.06,
    shadowOffset: { width: 0, height: 2 },
    shadowRadius: 8,
    elevation: 2,
  },
  title: {
    fontSize: 15,
    fontWeight: '600',
    color: '#111827',
    marginBottom: 8,
  },
  empty: {
    padding: 24,
    alignItems: 'center',
  },
  emptyText: {
    color: '#9ca3af',
    fontSize: 14,
  },
  list: {
    marginTop: 8,
    gap: 8,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  dot: {
    width: 8,
    height: 8,
    borderRadius: 4,
  },
  name: {
    flex: 1,
    fontSize: 13,
    color: '#374151',
  },
  amount: {
    fontSize: 13,
    fontWeight: '600',
    color: '#374151',
  },
});
