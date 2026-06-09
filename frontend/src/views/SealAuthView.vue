<template>
  <div style="padding: 24px">

    <!-- 權限不足：USER 看到此提示 -->
    <el-result
      v-if="authStore.role !== 'ROLE_ADMIN'"
      icon="warning"
      title="權限不足"
      sub-title="此功能僅限管理員使用"
    />

    <!-- ADMIN 看到核印操作 -->
    <div v-else>
      <h2 style="margin-bottom: 16px">核印授權</h2>

      <!-- PENDING 請款列表 -->
      <el-card style="margin-bottom: 24px">
        <template #header>待核印請款（PENDING）</template>
        <el-table :data="pendingList" v-loading="loadingPending" style="width: 100%">
          <el-table-column prop="paymentId" label="請款 ID" width="100" />
          <el-table-column prop="policyId"  label="保單 ID" width="100" />
          <el-table-column prop="amount"    label="金額"    width="120" />
          <el-table-column prop="traceId"   label="Trace ID" />
          <el-table-column label="狀態" width="100">
            <template #default="{ row }">
              <el-tag type="warning">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openDialog(row)">核印</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 已核印紀錄 -->
      <el-card>
        <template #header>已核印紀錄</template>
        <el-table :data="authList" v-loading="loadingAuth" style="width: 100%">
          <el-table-column prop="authId"        label="Auth ID"  width="100" />
          <el-table-column prop="payment.paymentId" label="請款 ID"  width="100" />
          <el-table-column prop="authorizedBy"  label="核印人"   width="120" />
          <el-table-column prop="remark"        label="備註" />
          <el-table-column prop="authorizedAt"  label="核印時間" width="180" />
        </el-table>
      </el-card>

      <!-- 核印 Dialog -->
      <el-dialog v-model="dialogVisible" title="確認核印" width="400px">
        <p>請款 ID：{{ selectedPayment?.paymentId }}</p>
        <p>金額：{{ selectedPayment?.amount }}</p>
        <el-input
          v-model="remark"
          placeholder="備註（選填）"
          style="margin-top: 12px"
        />
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitting" @click="submitAuth">確認核印</el-button>
        </template>
      </el-dialog>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const pendingList  = ref<any[]>([])
const authList     = ref<any[]>([])
const loadingPending = ref(false)
const loadingAuth    = ref(false)

const dialogVisible    = ref(false)
const selectedPayment  = ref<any>(null)
const remark           = ref('')
const submitting       = ref(false)

async function fetchPending() {
  loadingPending.value = true
  try {
    const res = await axios.get('/api/seal-auth/pending')
    pendingList.value = res.data
  } finally {
    loadingPending.value = false
  }
}

async function fetchAuthList() {
  loadingAuth.value = true
  try {
    const res = await axios.get('/api/seal-auth')
    authList.value = res.data
  } finally {
    loadingAuth.value = false
  }
}

function openDialog(row: any) {
  selectedPayment.value = row
  remark.value = ''
  dialogVisible.value = true
}

async function submitAuth() {
  submitting.value = true
  try {
    await axios.post(`/api/seal-auth/${selectedPayment.value.paymentId}?remark=${encodeURIComponent(remark.value)}`)
    ElMessage.success('核印成功')
    dialogVisible.value = false
    await fetchPending()
    await fetchAuthList()
  } catch (e: any) {
    ElMessage.error(e.response?.data?.message || '核印失敗')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  if (authStore.role === 'ROLE_ADMIN') {
    fetchPending()
    fetchAuthList()
  }
})
</script>
