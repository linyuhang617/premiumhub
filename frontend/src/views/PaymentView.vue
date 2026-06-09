<template>
  <div style="padding: 24px">
    <h2>保費請款</h2>

    <!-- 請款表單 -->
    <el-card style="max-width: 480px; margin-bottom: 24px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="保單" prop="policyId">
          <el-select v-model="form.policyId" placeholder="請選擇保單" style="width: 100%">
            <el-option
              v-for="p in activePolicies"
              :key="p.policyId"
              :label="`${p.policyNo}（${p.premiumAmount} 元）`"
              :value="p.policyId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="金額" prop="amount">
          <el-input v-model="form.amount" placeholder="請輸入請款金額" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">送出請款</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 二次確認 Dialog -->
    <el-dialog v-model="dialogVisible" title="確認請款" width="400px">
      <p>保單：<strong>{{ selectedPolicyNo }}</strong></p>
      <p>金額：<strong>{{ form.amount }} 元</strong></p>
      <p>確認送出後將無法取消，是否繼續？</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="confirmPayment">確認送出</el-button>
      </template>
    </el-dialog>

    <!-- 結果顯示 -->
    <el-card v-if="result" style="max-width: 480px; margin-bottom: 24px">
      <el-result
        :icon="result.status === 'SUCCESS' ? 'success' : 'error'"
        :title="result.status === 'SUCCESS' ? '請款成功' : '請款失敗'"
      >
        <template #extra>
          <p style="font-size: 12px; color: #999">Trace ID：{{ result.traceId }}</p>
          <el-button @click="result = null">關閉</el-button>
        </template>
      </el-result>
    </el-card>

    <!-- 請款紀錄列表 -->
    <el-card>
      <template #header>請款紀錄</template>
      <el-table :data="payments" v-loading="loadingList" style="width: 100%">
        <el-table-column prop="traceId" label="Trace ID" width="300" />
        <el-table-column prop="policyId" label="保單 ID" width="90" />
        <el-table-column prop="amount" label="金額" width="100" />
        <el-table-column prop="status" label="狀態" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paidAt" label="時間" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from '@/utils/axios'
import type { FormInstance } from 'element-plus'

const formRef = ref<FormInstance>()
const form = ref({ policyId: null as number | null, amount: '' })
const rules = {
  policyId: [{ required: true, message: '請選擇保單', trigger: 'change' }],
  amount: [{ required: true, message: '請輸入金額', trigger: 'blur' }],
}

const activePolicies = ref<any[]>([])
const payments = ref<any[]>([])
const loadingList = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const result = ref<any>(null)

const selectedPolicyNo = computed(() => {
  const p = activePolicies.value.find((p) => p.policyId === form.value.policyId)
  return p ? p.policyNo : ''
})

async function loadPolicies() {
  const res = await axios.get('/api/policies', { params: { status: 'ACTIVE', size: 100 } })
  activePolicies.value = res.data.content ?? []
}

async function loadPayments() {
  loadingList.value = true
  try {
    const res = await axios.get('/api/payments')
    payments.value = res.data
  } finally {
    loadingList.value = false
  }
}

function handleSubmit() {
  formRef.value?.validate((valid) => {
    if (valid) dialogVisible.value = true
  })
}

async function confirmPayment() {
  submitting.value = true
  dialogVisible.value = false
  try {
    const res = await axios.post('/api/payments', {
      policyId: form.value.policyId,
      amount: parseFloat(form.value.amount),
    })
    result.value = res.data
    form.value = { policyId: null, amount: '' }
    await loadPayments()
  } catch (e: any) {
    result.value = { status: 'FAILED', traceId: '-', message: e.response?.data?.message }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPolicies()
  loadPayments()
})
</script>
