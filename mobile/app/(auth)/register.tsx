import React from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ActivityIndicator,
  Alert,
} from 'react-native';
import { Link } from 'expo-router';
import { useForm, Controller } from 'react-hook-form';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { useAuthStore } from '../../src/stores/authStore';

const schema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  confirmPassword: z.string(),
}).refine((d) => d.password === d.confirmPassword, {
  message: 'Passwords do not match',
  path: ['confirmPassword'],
});
type FormData = z.infer<typeof schema>;

export default function RegisterScreen() {
  const { register, isLoading } = useAuthStore();

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({ resolver: zodResolver(schema) });

  const onSubmit = async (data: FormData) => {
    try {
      await register(data.email, data.password);
    } catch {
      Alert.alert('Registration failed', 'This email may already be in use.');
    }
  };

  return (
    <KeyboardAvoidingView
      style={styles.container}
      behavior={Platform.OS === 'ios' ? 'padding' : undefined}
    >
      <View style={styles.inner}>
        <Text style={styles.appName}>SpendStat</Text>
        <Text style={styles.tagline}>Create your account</Text>

        <View style={styles.form}>
          <Controller
            control={control}
            name="email"
            render={({ field: { onChange, onBlur, value } }) => (
              <View style={styles.fieldGroup}>
                <TextInput
                  style={[styles.input, errors.email && styles.inputError]}
                  placeholder="Email"
                  placeholderTextColor="#9ca3af"
                  keyboardType="email-address"
                  autoCapitalize="none"
                  autoCorrect={false}
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                />
                {errors.email && <Text style={styles.error}>{errors.email.message}</Text>}
              </View>
            )}
          />

          <Controller
            control={control}
            name="password"
            render={({ field: { onChange, onBlur, value } }) => (
              <View style={styles.fieldGroup}>
                <TextInput
                  style={[styles.input, errors.password && styles.inputError]}
                  placeholder="Password"
                  placeholderTextColor="#9ca3af"
                  secureTextEntry
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                />
                {errors.password && <Text style={styles.error}>{errors.password.message}</Text>}
              </View>
            )}
          />

          <Controller
            control={control}
            name="confirmPassword"
            render={({ field: { onChange, onBlur, value } }) => (
              <View style={styles.fieldGroup}>
                <TextInput
                  style={[styles.input, errors.confirmPassword && styles.inputError]}
                  placeholder="Confirm password"
                  placeholderTextColor="#9ca3af"
                  secureTextEntry
                  onBlur={onBlur}
                  onChangeText={onChange}
                  value={value}
                />
                {errors.confirmPassword && (
                  <Text style={styles.error}>{errors.confirmPassword.message}</Text>
                )}
              </View>
            )}
          />

          <TouchableOpacity
            style={styles.button}
            onPress={handleSubmit(onSubmit)}
            disabled={isLoading}
          >
            {isLoading ? (
              <ActivityIndicator color="#fff" />
            ) : (
              <Text style={styles.buttonText}>Create account</Text>
            )}
          </TouchableOpacity>
        </View>

        <Link href="/(auth)/login" asChild>
          <TouchableOpacity style={styles.link}>
            <Text style={styles.linkText}>
              Already have an account? <Text style={styles.linkBold}>Log in</Text>
            </Text>
          </TouchableOpacity>
        </Link>
      </View>
    </KeyboardAvoidingView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fafb',
  },
  inner: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 24,
    paddingBottom: 40,
  },
  appName: {
    fontSize: 36,
    fontWeight: '800',
    color: '#6366f1',
    textAlign: 'center',
    marginBottom: 8,
  },
  tagline: {
    fontSize: 15,
    color: '#6b7280',
    textAlign: 'center',
    marginBottom: 40,
  },
  form: {
    gap: 12,
  },
  fieldGroup: {
    gap: 4,
  },
  input: {
    backgroundColor: '#fff',
    borderWidth: 1,
    borderColor: '#d1d5db',
    borderRadius: 12,
    paddingHorizontal: 16,
    paddingVertical: 14,
    fontSize: 16,
    color: '#111827',
  },
  inputError: {
    borderColor: '#ef4444',
  },
  error: {
    fontSize: 12,
    color: '#ef4444',
    marginLeft: 4,
  },
  button: {
    backgroundColor: '#6366f1',
    borderRadius: 12,
    paddingVertical: 16,
    alignItems: 'center',
    marginTop: 8,
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: '600',
  },
  link: {
    marginTop: 24,
    alignItems: 'center',
  },
  linkText: {
    fontSize: 14,
    color: '#6b7280',
  },
  linkBold: {
    color: '#6366f1',
    fontWeight: '600',
  },
});
