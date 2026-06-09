<template>
  <div style="padding:40px">
    <h1>PremiumHub 保費管理系統</h1>
    <p>歡迎，目前角色：{{ authStore.role }}</p>
    <p>Token 狀態：{{ tokenStatus }}</p>
    <button @click="testAuth"
      style="padding:8px 16px;background:#1890ff;color:white;border:none;border-radius:4px;cursor:pointer;margin-right:12px">
      測試認證 API
    </button>
    <button @click="handleLogout"
      style="padding:8px 16px;background:#ff4d4f;color:white;border:none;border-radius:4px;cursor:pointer">
      登出
    </button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import api from '@/utils/axios'

const router = useRouter()
const authStore = useAuthStore()
const tokenStatus = ref('尚未測試')

async function testAuth() {
  try {
    const res = await api.get('/api/health')
    tokenStatus.value = '✅ 認證成功：' + res.data
  } catch (e) {
    tokenStatus.value = '❌ 認證失敗'
  }
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
