<template>
  <div style="padding: 24px">
    <h2>保單列表</h2>

    <!-- 篩選區 -->
    <el-form :inline="true" :model="query" style="margin-bottom: 16px">
      <el-form-item label="保單號">
        <el-input v-model="query.policyNo" placeholder="輸入保單號" clearable @change="fetchPolicies" />
      </el-form-item>
      <el-form-item label="狀態">
        <el-select v-model="query.status" placeholder="全部" clearable @change="fetchPolicies" style="width: 120px">
          <el-option label="有效" value="ACTIVE" />
          <el-option label="已過期" value="EXPIRED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="到期日起">
        <el-date-picker v-model="query.dueDateFrom" type="date" value-format="YYYY-MM-DD"
          placeholder="起始日期" @change="fetchPolicies" style="width: 150px" />
      </el-form-item>
      <el-form-item label="到期日迄">
        <el-date-picker v-model="query.dueDateTo" type="date" value-format="YYYY-MM-DD"
          placeholder="結束日期" @change="fetchPolicies" style="width: 150px" />
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table :data="policies" v-loading="loading" border stripe>
      <el-table-column prop="policyNo" label="保單號" width="160" />
      <el-table-column prop="insuredName" label="被保人" width="120" />
      <el-table-column prop="idNoMasked" label="身分證號" width="150" />
      <el-table-column prop="premiumAmount" label="保費金額" width="130">
        <template #default="{ row }">
          {{ row.premiumAmount.toLocaleString() }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="狀態" width="110">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="dueDate" label="到期日" width="120" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button size="small" @click="goDetail(row.policyId)">詳情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分頁 -->
    <el-pagination
      style="margin-top: 16px"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      :page-size="query.size"
      :current-page="query.page + 1"
      :page-sizes="[5, 10, 20]"
      @size-change="onSizeChange"
      @current-change="onPageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '@/utils/axios'

const router = useRouter()
const policies = ref([])
const total = ref(0)
const loading = ref(false)

const query = ref({
  policyNo: '',
  status: '',
  dueDateFrom: '',
  dueDateTo: '',
  page: 0,
  size: 10,
})

async function fetchPolicies() {
  loading.value = true
  try {
    const params = { ...query.value }
    const res = await api.get('/api/policies', { params })
    policies.value = res.data.content
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onPageChange(page: number) {
  query.value.page = page - 1
  fetchPolicies()
}

function onSizeChange(size: number) {
  query.value.size = size
  query.value.page = 0
  fetchPolicies()
}

function goDetail(id: number) {
  router.push(`/policies/${id}`)
}

function statusLabel(status: string) {
  return { ACTIVE: '有效', EXPIRED: '已過期', CANCELLED: '已取消' }[status] ?? status
}

function statusTagType(status: string) {
  return { ACTIVE: 'success', EXPIRED: 'info', CANCELLED: 'danger' }[status] ?? ''
}

onMounted(fetchPolicies)
</script>
