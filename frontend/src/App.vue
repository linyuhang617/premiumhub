<template>
  <div>
    <h1>PremiumHub</h1>
    <p>後端狀態：{{ status }}</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

const status = ref('檢查中...')

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/health')
    status.value = res.data
  } catch {
    status.value = '連線失敗'
  }
})
</script>
