<template>
  <div style="padding: 24px; max-width: 600px">
    <el-page-header @back="router.back()" title="返回列表" content="保單詳情" style="margin-bottom: 24px" />

    <el-descriptions v-if="policy" :column="2" border>
      <el-descriptions-item label="保單號">{{ policy.policyNo }}</el-descriptions-item>
      <el-descriptions-item label="狀態">
        <el-tag :type="statusTagType(policy.status)">{{ statusLabel(policy.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="被保人">{{ policy.insuredName }}</el-descriptions-item>
      <el-descriptions-item label="身分證號">{{ policy.idNoMasked }}</el-descriptions-item>
      <el-descriptions-item label="保費金額">{{ policy.premiumAmount?.toLocaleString() }}</el-descriptions-item>
      <el-descriptions-item label="到期日">{{ policy.dueDate }}</el-descriptions-item>
    </el-descriptions>

    <el-skeleton v-else :rows="4" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/utils/axios'

const route = useRoute()
const router = useRouter()
const policy = ref(null)

onMounted(async () => {
  const res = await api.get(`/api/policies/${route.params.id}`)
  policy.value = res.data
})

function statusLabel(status: string) {
  return { ACTIVE: '有效', EXPIRED: '已過期', CANCELLED: '已取消' }[status] ?? status
}

function statusTagType(status: string) {
  return { ACTIVE: 'success', EXPIRED: 'info', CANCELLED: 'danger' }[status] ?? ''
}
</script>
