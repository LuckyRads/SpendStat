import React from 'react';
import { View, Text, StyleSheet, Dimensions } from 'react-native';
import { LineChart } from 'react-native-chart-kit';
import { DailyBalance } from '../types';
import { format, parseISO } from 'date-fns';

interface Props {
  data: DailyBalance[];
  currency?: string;
}

export default function BalanceChart({ data, currency = 'EUR' }: Props) {
  const screenWidth = Dimensions.get('window').width;

  if (!data.length) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No balance data</Text>
      </View>
    );
  }

  // Show at most 30 data points — sample evenly if more
  const sampled = data.length > 30
    ? data.filter((_, i) => i % Math.ceil(data.length / 30) === 0)
    : data;

  const labels = sampled
    .filter((_, i) => i % Math.ceil(sampled.length / 6) === 0)
    .map((d) => format(parseISO(d.date), 'dd/MM'));

  const values = sampled.map((d) => Number(d.balance));

  // Pad labels array to match data length (chart requires same length or empty)
  const paddedLabels = sampled.map((d, i) => {
    const step = Math.ceil(sampled.length / 6);
    return i % step === 0 ? format(parseISO(d.date), 'dd/MM') : '';
  });

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Balance history</Text>
      <LineChart
        data={{
          labels: paddedLabels,
          datasets: [{ data: values }],
        }}
        width={screenWidth - 32}
        height={160}
        yAxisSuffix=""
        chartConfig={{
          backgroundColor: '#fff',
          backgroundGradientFrom: '#fff',
          backgroundGradientTo: '#fff',
          decimalPlaces: 0,
          color: (opacity = 1) => `rgba(99, 102, 241, ${opacity})`,
          labelColor: () => '#9ca3af',
          propsForDots: { r: '3', strokeWidth: '1', stroke: '#6366f1' },
        }}
        bezier
        style={{ marginLeft: -16 }}
        withInnerLines={false}
        withOuterLines={false}
      />
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
});
