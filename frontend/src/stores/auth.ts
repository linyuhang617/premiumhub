import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const role = ref(localStorage.getItem('role') || '')

  async function login(username: string, password: string) {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username,
      password
    })
    accessToken.value = res.data.accessToken
    refreshToken.value = res.data.refreshToken
    role.value = username === 'admin' ? 'ADMIN' : 'USER'
    localStorage.setItem('accessToken', accessToken.value)
    localStorage.setItem('refreshToken', refreshToken.value)
    localStorage.setItem('role', role.value)
  }

  function logout() {
    accessToken.value = ''
    refreshToken.value = ''
    role.value = ''
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('role')
  }

  function isLoggedIn() {
    return !!accessToken.value
  }

  return { accessToken, refreshToken, role, login, logout, isLoggedIn }
})
