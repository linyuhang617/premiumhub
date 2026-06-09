<template>
  <div style="display:flex;justify-content:center;align-items:center;height:100vh;background:#f5f5f5">
    <div style="background:white;padding:40px;border-radius:8px;width:360px;box-shadow:0 2px 12px rgba(0,0,0,0.1)">
      <h2 style="margin:0 0 24px;text-align:center">PremiumHub 登入</h2>
      <div style="margin-bottom:16px">
        <label>帳號</label>
        <input v-model="username" type="text" placeholder="請輸入帳號"
          style="width:100%;padding:8px;margin-top:4px;border:1px solid #ddd;border-radius:4px;box-sizing:border-box" />
      </div>
      <div style="margin-bottom:24px">
        <label>密碼</label>
        <input v-model="password" type="password" placeholder="請輸入密碼"
          style="width:100%;padding:8px;margin-top:4px;border:1px solid #ddd;border-radius:4px;box-sizing:border-box"
          @keyup.enter="handleLogin" />
      </div>
      <p v-if="errorMsg" style="color:red;margin-bottom:16px;text-align:center">{{ errorMsg }}</p>
      <button @click="handleLogin" :disabled="loading"
        style="width:100%;padding:10px;background:#1890ff;color:white;border:none;border-radius:4px;cursor:pointer;font-size:16px">
        {{ loading ? '登入中...' : '登入' }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const username = ref('')
const password = ref('')
const errorMsg = ref('')
const loading = ref(false)
const router = useRouter()
const authStore = useAuthStore()

async function handleLogin() {
  errorMsg.value = ''
  loading.value = true
  try {
    await authStore.login(username.value, password.value)
    router.push('/')
  } catch (e: any) {
    errorMsg.value = e.response?.data?.message || '登入失敗'
  } finally {
    loading.value = false
  }
}
</script>
