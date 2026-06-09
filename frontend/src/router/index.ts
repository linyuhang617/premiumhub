import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import PolicyListView from '@/views/PolicyListView.vue'
import PolicyDetailView from '@/views/PolicyDetailView.vue'
import PolicyFormView from '@/views/PolicyFormView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', component: HomeView, meta: { requiresAuth: true } },
    { path: '/policies', component: PolicyListView, meta: { requiresAuth: true } },
    { path: '/policies/new', component: PolicyFormView, meta: { requiresAuth: true } },
    { path: '/policies/:id/edit', component: PolicyFormView, meta: { requiresAuth: true } },
    { path: '/policies/:id', component: PolicyDetailView, meta: { requiresAuth: true } },
  ]
})

router.beforeEach((to) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isLoggedIn()) {
    return '/login'
  }
})

export default router
