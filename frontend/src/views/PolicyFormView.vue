<template>
  <div style="padding: 24px; max-width: 640px">
    <el-page-header @back="router.back()" :content="isEdit ? '編輯保單' : '新增保單'" style="margin-bottom: 24px" />

    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="保單號" prop="policyNo">
        <el-input v-model="form.policyNo" placeholder="例：POL-2024-009" />
      </el-form-item>
      <el-form-item label="被保人姓名" prop="insuredName">
        <el-input v-model="form.insuredName" placeholder="真實姓名（將加密儲存）" />
      </el-form-item>
      <el-form-item label="身分證號" prop="idNo">
        <el-input v-model="form.idNo" placeholder="例：A123456789" />
      </el-form-item>
      <el-form-item label="保費金額" prop="premiumAmount">
        <el-input-number v-model="form.premiumAmount" :min="1" :precision="0" style="width: 100%" />
      </el-form-item>
      <el-form-item label="狀態" prop="status">
        <el-select v-model="form.status" style="width: 100%">
          <el-option label="有效" value="ACTIVE" />
          <el-option label="已過期" value="EXPIRED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
      </el-form-item>
      <el-form-item label="到期日" prop="dueDate">
        <el-date-picker v-model="form.dueDate" type="date" value-format="YYYY-MM-DD"
          placeholder="選擇到期日" style="width: 100%" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit" :loading="loading">
          {{ isEdit ? '儲存變更' : '新增保單' }}
        </el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import api from '@/utils/axios'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const form = ref({
  policyNo: '',
  insuredName: '',
  idNo: '',
  premiumAmount: 1000,
  status: 'ACTIVE',
  dueDate: '',
  version: null as number | null,
})

const rules = {
  policyNo: [{ required: true, message: '請輸入保單號', trigger: 'blur' }],
  insuredName: [{ required: true, message: '請輸入被保人姓名', trigger: 'blur' }],
  idNo: [{ required: true, message: '請輸入身分證號', trigger: 'blur' }],
  premiumAmount: [{ required: true, message: '請輸入保費金額', trigger: 'blur' }],
  status: [{ required: true, message: '請選擇狀態', trigger: 'change' }],
}

onMounted(async () => {
  if (isEdit.value) {
    // 編輯：載入現有資料（但遮罩後的姓名/身分證不能填回表單，留空讓用戶重填）
    const res = await api.get(`/api/policies/${route.params.id}`)
    const p = res.data
    form.value.policyNo = p.policyNo
    form.value.premiumAmount = p.premiumAmount
    form.value.status = p.status
    form.value.dueDate = p.dueDate
    form.value.version = p.version
    // 姓名和身分證是加密欄位，API 只回傳遮罩值，需要用戶重新輸入
    form.value.insuredName = ''
    form.value.idNo = ''
  }
})

async function submit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const payload: any = { ...form.value }
    if (!isEdit.value) delete payload.version

    if (isEdit.value) {
      await api.put(`/api/policies/${route.params.id}`, payload)
      ElMessage.success('保單已更新')
    } else {
      await api.post('/api/policies', payload)
      ElMessage.success('保單已新增')
    }
    router.push('/policies')
  } catch (err: any) {
    if (err.response?.status === 409) {
      ElMessageBox.alert(
        '此保單已被他人修改，請返回列表重新進入後再編輯。',
        '資料衝突',
        { type: 'warning', confirmButtonText: '返回列表' }
      ).then(() => router.push('/policies'))
    } else {
      ElMessage.error(err.response?.data?.message || '操作失敗')
    }
  } finally {
    loading.value = false
  }
}
</script>
