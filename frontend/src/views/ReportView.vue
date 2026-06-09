<template>
  <div style="padding: 24px">
    <h2 style="margin-bottom: 16px">收據下載 / 報表</h2>

    <el-card>
      <template #header>
        <div style="display:flex; justify-content:space-between; align-items:center">
          <span>請款列表</span>
          <el-button type="success" :loading="exporting" @click="exportAll">
            匯出本月明細
          </el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" style="width:100%">
        <el-table-column prop="paymentId" label="請款 ID" width="100"/>
        <el-table-column prop="policyId"  label="保單 ID" width="100"/>
        <el-table-column prop="amount"    label="金額"    width="120"/>
        <el-table-column prop="status"    label="狀態"    width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : row.status === 'PENDING' ? 'warning' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="traceId"   label="Trace ID"/>
        <el-table-column prop="paidAt"    label="時間" width="180"/>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="primary"
              :loading="downloadingId === row.paymentId"
              @click="downloadPdf(row.paymentId)">
              下載收據
            </el-button>
            <el-button size="small"
              @click="previewHtml(row.paymentId)">
              HTML 預覽
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const list        = ref<any[]>([])
const loading     = ref(false)
const exporting   = ref(false)
const downloadingId = ref<string | null>(null)

async function fetchList() {
  loading.value = true
  try {
    const res = await axios.get('/api/reports/payment-list')
    list.value = res.data
  } finally {
    loading.value = false
  }
}

async function downloadPdf(paymentId: string) {
  downloadingId.value = paymentId
  try {
    const res = await axios.get(`/api/reports/receipt/${paymentId}`, {
      responseType: 'blob'
    })
    const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
    const a = document.createElement('a')
    a.href = url
    a.download = `receipt-${paymentId}.pdf`
    a.click()
    URL.revokeObjectURL(url)
  } catch {
    ElMessage.error('下載失敗')
  } finally {
    downloadingId.value = null
  }
}

async function exportAll() {
  exporting.value = true
  try {
    const res = await axios.get('/api/reports/receipt/1', {
      responseType: 'blob'
    })
    const url = URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
    const a = document.createElement('a')
    a.href = url
    a.download = `payment-list-${new Date().toISOString().slice(0,10)}.pdf`
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('匯出完成')
  } catch {
    ElMessage.error('匯出失敗')
  } finally {
    exporting.value = false
  }
}

function previewHtml(paymentId: string) {
  window.open(`http://localhost:8080/report-preview/${paymentId}`, '_blank')
}

onMounted(fetchList)
</script>
