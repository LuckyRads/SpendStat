import React, { useCallback, useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  ScrollView,
  Alert,
  ActivityIndicator,
} from 'react-native';
import { useRouter } from 'expo-router';
import { Ionicons } from '@expo/vector-icons';
import { useForm, Controller } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { format } from 'date-fns';
import { accountsApi } from '../src/api/accounts';
import { categoriesApi } from '../src/api/categories';
import { transactionsApi } from '../src/api/transactions';
import { Account, Category, TransactionType } from '../src/types';

const schema = z.object({
  amount: z.string().min(1, 'Amount is required').refine((v) => !isNaN(parseFloat(v)) && parseFloat(v) > 0, {
    message: 'Must be a positive number',
  }),
  description: z.string().optional(),
  merchant: z.string().optional(),
  txDate: z.string().min(1, 'Date is required'),
});
type FormData = z.infer<typeof schema>;

export default function AddTransactionScreen() {
  const router = useRouter();
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedAccount, setSelectedAccount] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('');
  const [txType, setTxType] = useState<TransactionType>('EXPENSE');
  const [saving, setSaving] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { txDate: format(new Date(), 'yyyy-MM-dd') },
  });

  const load = useCallback(async () => {
    try {
      const [accs, cats] = await Promise.all([accountsApi.list(), categoriesApi.list()]);
      setAccounts(accs);
      setCategories(cats);
      if (accs.length) setSelectedAccount(accs[0].id);
    } catch {
      Alert.alert('Error', 'Failed to load accounts/categories.');
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const filteredCategories = categories.filter((c) => c.type === txType);

  const onSubmit = async (data: FormData) => {
    if (!selectedAccount) {
      Alert.alert('Error', 'Please create an account first.');
      return;
    }
    setSaving(true);
    try {
      const currency = accounts.find((a) => a.id === selectedAccount)?.currency ?? 'EUR';
      await transactionsApi.create({
        accountId: selectedAccount,
        categoryId: selectedCategory || undefined,
        amount: parseFloat(data.amount),
        currency,
        type: txType,
        description: data.description || undefined,
        merchant: data.merchant || undefined,
        txDate: data.txDate,
      });
      router.back();
    } catch {
      Alert.alert('Error', 'Failed to save transaction.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()}>
          <Ionicons name="close" size={24} color="#374151" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>Add Transaction</Text>
        <TouchableOpacity onPress={handleSubmit(onSubmit)} disabled={saving}>
          {saving ? (
            <ActivityIndicator size="small" color="#6366f1" />
          ) : (
            <Text style={styles.saveBtn}>Save</Text>
          )}
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={styles.content}>
        {/* Type toggle */}
        <View style={styles.typeRow}>
          <TouchableOpacity
            style={[styles.typeBtn, txType === 'EXPENSE' && styles.typeBtnActive]}
            onPress={() => { setTxType('EXPENSE'); setSelectedCategory(''); }}
          >
            <Text style={[styles.typeBtnText, txType === 'EXPENSE' && styles.typeBtnTextActive]}>
              Expense
            </Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={[styles.typeBtn, txType === 'INCOME' && styles.typeBtnActiveGreen]}
            onPress={() => { setTxType('INCOME'); setSelectedCategory(''); }}
          >
            <Text style={[styles.typeBtnText, txType === 'INCOME' && styles.typeBtnTextActive]}>
              Income
            </Text>
          </TouchableOpacity>
        </View>

        {/* Amount */}
        <View style={styles.fieldGroup}>
          <Text style={styles.label}>Amount</Text>
          <Controller
            control={control}
            name="amount"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                style={[styles.input, errors.amount && styles.inputError]}
                placeholder="0.00"
                placeholderTextColor="#9ca3af"
                keyboardType="decimal-pad"
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.amount && <Text style={styles.error}>{errors.amount.message}</Text>}
        </View>

        {/* Date */}
        <View style={styles.fieldGroup}>
          <Text style={styles.label}>Date (YYYY-MM-DD)</Text>
          <Controller
            control={control}
            name="txDate"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                style={[styles.input, errors.txDate && styles.inputError]}
                placeholder="2025-01-15"
                placeholderTextColor="#9ca3af"
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
          {errors.txDate && <Text style={styles.error}>{errors.txDate.message}</Text>}
        </View>

        {/* Account */}
        <View style={styles.fieldGroup}>
          <Text style={styles.label}>Account</Text>
          {accounts.length === 0 ? (
            <Text style={styles.hint}>No accounts — create one first in the Accounts tab.</Text>
          ) : (
            <View style={styles.pillRow}>
              {accounts.map((a) => (
                <TouchableOpacity
                  key={a.id}
                  style={[styles.pill, selectedAccount === a.id && styles.pillActive]}
                  onPress={() => setSelectedAccount(a.id)}
                >
                  <Text style={[styles.pillText, selectedAccount === a.id && styles.pillTextActive]}>
                    {a.name}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          )}
        </View>

        {/* Category */}
        {filteredCategories.length > 0 && (
          <View style={styles.fieldGroup}>
            <Text style={styles.label}>Category</Text>
            <View style={styles.pillRow}>
              {filteredCategories.map((c) => (
                <TouchableOpacity
                  key={c.id}
                  style={[styles.pill, selectedCategory === c.id && styles.pillActive]}
                  onPress={() => setSelectedCategory(selectedCategory === c.id ? '' : c.id)}
                >
                  <Text style={[styles.pillText, selectedCategory === c.id && styles.pillTextActive]}>
                    {c.name}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        )}

        {/* Merchant */}
        <View style={styles.fieldGroup}>
          <Text style={styles.label}>Merchant (optional)</Text>
          <Controller
            control={control}
            name="merchant"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                style={styles.input}
                placeholder="e.g. Lidl"
                placeholderTextColor="#9ca3af"
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
        </View>

        {/* Description */}
        <View style={styles.fieldGroup}>
          <Text style={styles.label}>Description (optional)</Text>
          <Controller
            control={control}
            name="description"
            render={({ field: { onChange, onBlur, value } }) => (
              <TextInput
                style={[styles.input, styles.textArea]}
                placeholder="Add a note..."
                placeholderTextColor="#9ca3af"
                multiline
                numberOfLines={3}
                onBlur={onBlur}
                onChangeText={onChange}
                value={value}
              />
            )}
          />
        </View>
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
    paddingVertical: 14,
    backgroundColor: '#fff',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#e5e7eb',
  },
  headerTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: '#111827',
  },
  saveBtn: {
    fontSize: 16,
    color: '#6366f1',
    fontWeight: '600',
  },
  content: {
    padding: 16,
    gap: 20,
  },
  typeRow: {
    flexDirection: 'row',
    gap: 8,
  },
  typeBtn: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 10,
    alignItems: 'center',
    backgroundColor: '#f3f4f6',
    borderWidth: 1,
    borderColor: 'transparent',
  },
  typeBtnActive: {
    backgroundColor: '#fef2f2',
    borderColor: '#ef4444',
  },
  typeBtnActiveGreen: {
    backgroundColor: '#f0fdf4',
    borderColor: '#22c55e',
  },
  typeBtnText: {
    fontSize: 15,
    fontWeight: '600',
    color: '#6b7280',
  },
  typeBtnTextActive: {
    color: '#111827',
  },
  fieldGroup: {
    gap: 6,
  },
  label: {
    fontSize: 13,
    color: '#6b7280',
    fontWeight: '500',
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#d1d5db',
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 16,
    color: '#111827',
  },
  textArea: {
    height: 80,
    textAlignVertical: 'top',
  },
  inputError: {
    borderColor: '#ef4444',
  },
  error: {
    fontSize: 12,
    color: '#ef4444',
  },
  hint: {
    fontSize: 13,
    color: '#9ca3af',
    fontStyle: 'italic',
  },
  pillRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  pill: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    backgroundColor: '#f3f4f6',
    borderWidth: 1,
    borderColor: 'transparent',
  },
  pillActive: {
    backgroundColor: '#eef2ff',
    borderColor: '#6366f1',
  },
  pillText: {
    fontSize: 13,
    color: '#6b7280',
  },
  pillTextActive: {
    color: '#6366f1',
    fontWeight: '600',
  },
});
