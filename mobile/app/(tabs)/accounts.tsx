import React, { useCallback, useEffect, useState } from 'react';
import {
  View,
  Text,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
  Alert,
  Modal,
  TextInput,
  ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { accountsApi } from '../../src/api/accounts';
import { Account } from '../../src/types';

interface CreateForm {
  name: string;
  currency: string;
  initialBalance: string;
}

export default function AccountsScreen() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form, setForm] = useState<CreateForm>({ name: '', currency: 'EUR', initialBalance: '0' });
  const [saving, setSaving] = useState(false);

  const loadAccounts = useCallback(async () => {
    setLoading(true);
    try {
      const data = await accountsApi.list();
      setAccounts(data);
    } catch {
      // ignore
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    loadAccounts();
  }, [loadAccounts]);

  const onDelete = (id: string, name: string) => {
    Alert.alert('Delete account', `Delete "${name}"? All transactions will remain.`, [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Delete',
        style: 'destructive',
        onPress: async () => {
          try {
            await accountsApi.delete(id);
            setAccounts((prev) => prev.filter((a) => a.id !== id));
          } catch {
            Alert.alert('Error', 'Failed to delete account.');
          }
        },
      },
    ]);
  };

  const onCreate = async () => {
    const balance = parseFloat(form.initialBalance);
    if (!form.name.trim()) {
      Alert.alert('Error', 'Account name is required.');
      return;
    }
    if (isNaN(balance)) {
      Alert.alert('Error', 'Invalid initial balance.');
      return;
    }
    setSaving(true);
    try {
      const account = await accountsApi.create({
        name: form.name.trim(),
        currency: form.currency.trim().toUpperCase() || 'EUR',
        initialBalance: balance,
      });
      setAccounts((prev) => [account, ...prev]);
      setModalVisible(false);
      setForm({ name: '', currency: 'EUR', initialBalance: '0' });
    } catch {
      Alert.alert('Error', 'Failed to create account.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <SafeAreaView style={styles.safe}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Accounts</Text>
        <TouchableOpacity style={styles.fab} onPress={() => setModalVisible(true)}>
          <Ionicons name="add" size={22} color="#fff" />
        </TouchableOpacity>
      </View>

      {loading ? (
        <ActivityIndicator style={styles.loader} />
      ) : (
        <FlatList
          data={accounts}
          keyExtractor={(item) => item.id}
          contentContainerStyle={styles.list}
          renderItem={({ item }) => (
            <View style={styles.card}>
              <View>
                <Text style={styles.accountName}>{item.name}</Text>
                <Text style={styles.accountMeta}>
                  Initial: {Number(item.initialBalance).toFixed(2)} {item.currency}
                </Text>
              </View>
              <TouchableOpacity onPress={() => onDelete(item.id, item.name)}>
                <Ionicons name="trash-outline" size={20} color="#ef4444" />
              </TouchableOpacity>
            </View>
          )}
          ListEmptyComponent={
            <View style={styles.empty}>
              <Ionicons name="wallet-outline" size={48} color="#d1d5db" />
              <Text style={styles.emptyText}>No accounts yet</Text>
              <TouchableOpacity onPress={() => setModalVisible(true)}>
                <Text style={styles.emptyAction}>Create your first account</Text>
              </TouchableOpacity>
            </View>
          }
        />
      )}

      <Modal visible={modalVisible} animationType="slide" presentationStyle="pageSheet">
        <SafeAreaView style={styles.modal}>
          <View style={styles.modalHeader}>
            <TouchableOpacity onPress={() => setModalVisible(false)}>
              <Text style={styles.modalCancel}>Cancel</Text>
            </TouchableOpacity>
            <Text style={styles.modalTitle}>New Account</Text>
            <TouchableOpacity onPress={onCreate} disabled={saving}>
              {saving ? (
                <ActivityIndicator size="small" color="#6366f1" />
              ) : (
                <Text style={styles.modalSave}>Save</Text>
              )}
            </TouchableOpacity>
          </View>

          <View style={styles.modalForm}>
            <View style={styles.fieldGroup}>
              <Text style={styles.label}>Name</Text>
              <TextInput
                style={styles.input}
                placeholder="e.g. Main account"
                placeholderTextColor="#9ca3af"
                value={form.name}
                onChangeText={(v) => setForm((f) => ({ ...f, name: v }))}
              />
            </View>
            <View style={styles.fieldGroup}>
              <Text style={styles.label}>Currency</Text>
              <TextInput
                style={styles.input}
                placeholder="EUR"
                placeholderTextColor="#9ca3af"
                autoCapitalize="characters"
                maxLength={3}
                value={form.currency}
                onChangeText={(v) => setForm((f) => ({ ...f, currency: v }))}
              />
            </View>
            <View style={styles.fieldGroup}>
              <Text style={styles.label}>Initial balance</Text>
              <TextInput
                style={styles.input}
                placeholder="0.00"
                placeholderTextColor="#9ca3af"
                keyboardType="decimal-pad"
                value={form.initialBalance}
                onChangeText={(v) => setForm((f) => ({ ...f, initialBalance: v }))}
              />
            </View>
          </View>
        </SafeAreaView>
      </Modal>
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
    marginTop: 40,
  },
  list: {
    padding: 16,
    gap: 12,
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 16,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    shadowColor: '#000',
    shadowOpacity: 0.04,
    shadowOffset: { width: 0, height: 1 },
    shadowRadius: 4,
    elevation: 1,
  },
  accountName: {
    fontSize: 16,
    fontWeight: '600',
    color: '#111827',
  },
  accountMeta: {
    fontSize: 13,
    color: '#6b7280',
    marginTop: 2,
  },
  empty: {
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
  modal: {
    flex: 1,
    backgroundColor: '#f9fafb',
  },
  modalHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 14,
    backgroundColor: '#fff',
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: '#e5e7eb',
  },
  modalTitle: {
    fontSize: 17,
    fontWeight: '600',
    color: '#111827',
  },
  modalCancel: {
    fontSize: 16,
    color: '#6b7280',
  },
  modalSave: {
    fontSize: 16,
    color: '#6366f1',
    fontWeight: '600',
  },
  modalForm: {
    padding: 16,
    gap: 16,
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
});
