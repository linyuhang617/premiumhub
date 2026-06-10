<template>
  <div>
    <nav v-if="authStore.isLoggedIn()" style="background:#001529;padding:0 24px;display:flex;align-items:center;gap:24px;height:48px">
      <span style="color:#fff;font-weight:bold;margin-right:16px">PremiumHub</span>
      <router-link to="/" style="color:#ccc;text-decoration:none">首頁</router-link>
      <router-link to="/policies" style="color:#ccc;text-decoration:none">保單列表</router-link>
      <router-link to="/payments" style="color:#ccc;text-decoration:none">保費請款</router-link>
      <router-link to="/reports" style="color:#ccc;text-decoration:none">收據下載</router-link>
      <router-link v-if="isAdmin" to="/seal-auth" style="color:#ccc;text-decoration:none">核印授權</router-link>
      <router-link v-if="isAdmin" to="/dashboard" style="color:#1890ff;text-decoration:none;font-weight:bold">Dashboard</router-link>
      <span style="margin-left:auto;color:#ccc;cursor:pointer" @click="logout">登出</span>
    </nav>
    <RouterView />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const router = useRouter()
const isAdmin = computed(() => authStore.role === 'ROLE_ADMIN')

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>
