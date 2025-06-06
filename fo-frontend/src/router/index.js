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
import FavoriteChallengeView from '@/views/FavoriteChallengeView.vue'
// import MyChallengesView from '@/views/MyChallengesView.vue' // 삭제제
import ChallengeOverviewView from '@/views/ChallengeOverviewView.vue'
import ChallengeParticipantsView from '@/views/ChallengeParticipantsView.vue'
import ChallengeRequestsView from '@/views/ChallengeRequestsView.vue'
// import ChallengeCertificationView from '@/views/ChallengeCertificationView.vue'
import ChallengeEditView from '../views/ChallengeEditView.vue'
import ChallengeCertificationForm from '../components/challenge/ChallengeCertificationForm.vue'
import ChallengeCertificationList from '../components/challenge/ChallengeCertificationList.vue'
import CertificationDetailDialog from '@/components/challenge/CertificationDetailDialog.vue'
import MyNotificationsView from '../views/MyNotificationsView.vue'
import MyPageView from '../views/MyPageView.vue'

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
		meta: { requiresAuth: true }, // 로그인된 사용자만 접근 가능 로그인 안돼있으면 로그인 페이지로 이동
	},
	// 챌린지 생성
	{
		path: '/challenges/new',
		name: 'challenge-create',
		component: CreateChallengeView,
		meta: { requiresAuth: true },
	},
	// 관심 챌린지
	{
		path: '/challenges/favorite',
		name: 'favorite-challenge',
		component: FavoriteChallengeView,
		meta: { requiresAuth: true },
	},

	// // 내가 참여·생성한 챌린지 보기
	// {
	// 	path: '/challenges/my',
	// 	name: 'my-challenges',
	// 	component: MyChallengesView,
	// 	meta: { requiresAuth: true },
	// },

	// 챌린지 상세
	{
		path: '/challenges/:id',
		name: 'challenge-overview',
		component: ChallengeOverviewView,
		meta: { requiresAuth: true },
		children: [
			{
				path: '/challenges/:id/certifications/:certificationId',
				name: 'cert-detail',
				component: CertificationDetailDialog,
				props: true,
			},
			// 챌린지 참여자 화면
			// {
			// 	path: 'participants',
			// 	name: 'challenge-participants',
			// 	component: ChallengeParticipantsView,
			// 	meta: { requiresAuth: true },
			// },
			// // 챌린지 참여 요청 목록
			// {
			// 	path: 'requests',
			// 	name: 'challenge-requests',
			// 	component: ChallengeRequestsView,
			// 	meta: { requiresAuth: true },
			// },
			// // 인증 등록
			// {
			// 	path: 'certform',
			// 	name: 'challenge-certform',
			// 	component: ChallengeCertificationForm,
			// 	meta: { requiresAuth: true },
			// },
			// // 인증 등록
			// {
			// 	path: 'certlist',
			// 	name: 'challenge-certlist',
			// 	component: ChallengeCertificationList,
			// 	meta: { requiresAuth: true },
			// },
		],
	},
	// 챌린지 수정
	{
		path: '/challenges/:id/edit',
		name: 'challenge-edit',
		component: ChallengeEditView,
		meta: { requiresAuth: true },
		props: true,
	},
	// 내 알림
	{
		path: '/notifications',
		name: 'my-notifications',
		component: MyNotificationsView,
		meta: { requiresAuth: true },
	},
	// router/index.js에 추가
	{
		path: '/mypage',
		name: 'mypage',
		component: MyPageView,
		meta: { requiresAuth: true },
	},

	// 서브뷰들 (탭 내에서 라우팅)

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

	scrollBehavior(to, from, savedPosition) {
		if (savedPosition) {
			return new Promise((resolve) => {
				setTimeout(() => {
					// 즉시 스크롤 (애니메이션 없음)
					window.scrollTo(savedPosition.left, savedPosition.top)
					resolve(savedPosition)
				}, 100)
			})
		}
		return { top: 0 }
	},
})

// 전역 네비게이션 가이드드
router.beforeEach((to, from, next) => {
	const auth = useAuthStore()

	// 1) 이미 로그인된 상태로 로그인 페이지 접근 시 → 알림 없이 홈으로
	if (to.name === 'login' && auth.isAuthenticated) {
		return next({ name: 'home' })
	}

	// 2) 보호된 페이지에 접근 시도 (로그인 안 돼 있으면)
	if (to.meta.requiresAuth && !auth.isAuthenticated) {
		alert('로그인해야 이용할 수 있습니다.')
		return next({
			name: 'login',
			query: { redirect: to.fullPath },
		})
	}

	next()
})

export default router
