<template>
  <div style="padding:40px">
    <h1>PremiumHub 保費管理系統</h1>
    <p>歡迎，目前角色：{{ authStore.role }}</p>
    <button @click="handleLogout"
      style="padding:8px 16px;background:#ff4d4f;color:white;border:none;border-radius:4px;cursor:pointer">
      登出
    </button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>
