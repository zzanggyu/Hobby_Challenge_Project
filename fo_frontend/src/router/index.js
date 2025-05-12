import { createRouter, createWebHistory } from 'vue-router'

// 뷰 컴포넌트 import
import HomeView from '@/views/HomeView.vue'
import LoginView from '@/views/LoginView.vue'
import FindIdView from '@/views/FindIdView.vue'
import FindPasswordView from '@/views/FindPasswordView.vue'
import SignupView from '@/views/SignupView.vue'
// import ChallengesView from '@/views/ChallengesView.vue'
// import MyPageView    from '@/views/MyPageView.vue'

const routes = [
	{ path: '/', name: 'home', component: HomeView },
	//   { path: '/challenges', name: 'challenges', component: ChallengesView },
	//   { path: '/mypage',     name: 'mypage',     component: MyPageView },
	{ path: '/login', name: 'login', component: LoginView },
	{ path: '/find-id', name: 'find-id', component: FindIdView },
	{
		path: '/find-password',
		name: 'find-password',
		component: FindPasswordView,
	},
	{ path: '/signup', name: 'signup', component: SignupView },

	//   // 필요한 라우트를 여기 더 추가
]

const router = createRouter({
	history: createWebHistory(), // import.meta.env.BASE_URL 써도 OK
	routes,
})

export default router
