import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 뷰 컴포넌트 import
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import FindIdView from '@/views/FindIdView.vue'
import FindPasswordView from '@/views/FindPasswordView.vue'
import SignupView from '@/views/SignupView.vue'
import CreateChallengeView from '@/views/CreateChallengeView.vue'
import ChallengeListView from '@/views/ChallengeListView.vue'
// import ChallengesView from '@/views/ChallengesView.vue'
// import MyPageView    from '@/views/MyPageView.vue'

const routes = [
	{ path: '/', name: 'home', component: HomeView },

	{ path: '/login', name: 'login', component: LoginView },
	{ path: '/find-id', name: 'find-id', component: FindIdView },
	{
		path: '/find-password',
		name: 'find-password',
		component: FindPasswordView,
	},
	{ path: '/signup', name: 'signup', component: SignupView },
	{
		path: '/challenges',
		name: 'challenge-list',
		component: ChallengeListView,
		// meta: { requiresAuth: true }, // 로그인된 사용자만 접근 가능 로그인 안돼있으면 로그인 페이지로 이동
	},
	{
		path: '/challenges/new',
		name: 'challenge-create',
		component: CreateChallengeView,
		// meta: { requiresAuth: true },
	},
	// {
	// 	path: '/mypage',
	// 	name: 'mypage',
	// 	component: MyPageView,
	// 	meta: { requiresAuth: true },
	// },

	//   // 필요한 라우트를 여기 더 추가
]

const router = createRouter({
	history: createWebHistory(), // import.meta.env.BASE_URL 써도 OK
	routes,
})

// 전역 네비게이션 가이드드
router.beforeEach((to, from, next) => {
	const auth = useAuthStore()

	// 1) 보호된 페이지에 접근 시도
	if (to.meta.requiresAuth && !auth.isAuthenticated) {
		// 로그인 페이지로 리다이렉트
		return next({ name: 'login', query: { redirect: to.fullPath } })
	}

	// 2) 이미 로그인된 상태로 로그인 페이지 접근 시
	if (to.name === 'login' && auth.isAuthenticated) {
		return next({ name: 'home' })
	}

	next()
})

export default router
