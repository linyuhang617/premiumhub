<template>
  <div style="padding: 24px; background: #f0f2f5; min-height: calc(100vh - 48px)">
    <h2 style="margin: 0 0 24px; color: #001529">Dashboard — 請款總覽</h2>

    <div v-if="loading" style="text-align:center;padding:80px 0">
      <el-icon class="is-loading" :size="40"><Loading /></el-icon>
      <p style="color:#888;margin-top:12px">載入中...</p>
    </div>

    <el-result
      v-else-if="error"
      icon="error"
      title="載入失敗"
      :sub-title="error"
    >
      <template #extra>
        <el-button type="primary" @click="fetchDashboard">重試</el-button>
      </template>
    </el-result>

    <template v-else-if="data">
      <el-row :gutter="16" style="margin-bottom:24px">
        <el-col :span="6">
          <el-card shadow="hover">
            <div style="text-align:center">
              <div style="font-size:13px;color:#888;margin-bottom:8px">總請款筆數</div>
              <div style="font-size:32px;font-weight:bold;color:#1890ff">{{ data.totalPayments }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div style="text-align:center">
              <div style="font-size:13px;color:#888;margin-bottom:8px">總請款金額</div>
              <div style="font-size:32px;font-weight:bold;color:#52c41a">
                NT$ {{ formatAmount(data.totalAmount) }}
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div style="text-align:center">
              <div style="font-size:13px;color:#888;margin-bottom:8px">成功筆數</div>
              <div style="font-size:32px;font-weight:bold;color:#52c41a">{{ data.successCount }}</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card shadow="hover">
            <div style="text-align:center">
              <div style="font-size:13px;color:#888;margin-bottom:8px">待審核筆數</div>
              <div style="font-size:32px;font-weight:bold;color:#faad14">{{ data.pendingCount }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-card shadow="hover">
        <template #header>
          <span style="font-weight:bold">每月請款趨勢</span>
        </template>
        <el-empty v-if="data.monthlyTrend.length === 0" description="暫無趨勢資料" />
        <div v-else ref="chartRef" style="width:100%;height:300px" />
      </el-card>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { Loading } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

interface MonthlyTrend { month: string; count: number; amount: number }
interface DashboardData {
  totalPayments: number
  totalAmount: number
  successCount: number
  pendingCount: number
  monthlyTrend: MonthlyTrend[]
}

const loading = ref(false)
const error = ref('')
const data = ref<DashboardData | null>(null)
const chartRef = ref<HTMLElement | null>(null)

async function fetchDashboard() {
  loading.value = true
  error.value = ''
  try {
    const res = await axios.get('http://localhost:8080/api/dashboard', {
      headers: { Authorization: `Bearer ${authStore.accessToken}` }
    })
    data.value = res.data
    await nextTick()
    initChart()
  } catch (e: any) {
    error.value = e.response?.data?.message || '連線失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}

function initChart() {
  if (!chartRef.value || !data.value) return
  const chart = echarts.init(chartRef.value)
  const months = data.value.monthlyTrend.map(t => t.month)
  const counts = data.value.monthlyTrend.map(t => t.count)
  const amounts = data.value.monthlyTrend.map(t => t.amount)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['請款筆數', '請款金額 (NT$)'] },
    xAxis: { type: 'category', data: months },
    yAxis: [
      { type: 'value', name: '筆數', minInterval: 1 },
      { type: 'value', name: '金額', axisLabel: { formatter: (v: number) => `${(v/1000).toFixed(0)}K` } }
    ],
    series: [
      { name: '請款筆數', type: 'line', data: counts, smooth: true, itemStyle: { color: '#1890ff' } },
      { name: '請款金額 (NT$)', type: 'line', yAxisIndex: 1, data: amounts, smooth: true, itemStyle: { color: '#52c41a' } }
    ]
  })
  window.addEventListener('resize', () => chart.resize())
}

function formatAmount(val: number) {
  return val?.toLocaleString('zh-TW') ?? '0'
}

onMounted(fetchDashboard)
</script>
