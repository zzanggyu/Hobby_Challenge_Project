import { ref } from 'vue'

/**
 * 취미 챌린지 목업 데이터
 * id, title, desc, thumbnail(옵션), bg(옵션)
 */
export const Cards = ref([
	{
		id: 1,
		title: '30일 드로잉 챌린지',
		desc: '하루 한 장, 10분 드로잉으로 그림 실력 키우기',
		bg: '#8BC34A',
	},
	{
		id: 2,
		title: '아침 독서 20쪽 챌린지',
		desc: '출근 전 15분, 소설·에세이 함께 읽어요',
		bg: '#8BC34A',
	},
	{
		id: 3,
		title: '주 3회 5km 러닝',
		desc: '함께 달리며 기록 공유하고 완주 메달 받기',
		bg: '#8BC34A',
	},
	{
		id: 4,
		title: '홈베이킹 마스터',
		desc: '쿠키 → 파운드케이크 → 소프트 브레드까지 4주 코스',
		bg: '#8BC34A',
	},
	{
		id: 5,
		title: '코딩 알고리즘 1일 1문제',
		desc: 'Python·JS로 알고리즘 감각 기르기',
		bg: '#8BC34A',
	},
	{
		id: 6,
		title: '플라워 사진 100장',
		desc: '봄꽃부터 가을 국화까지, 일상에서 꽃 찾기',
		bg: '#8BC34A',
	},
	{
		id: 7,
		title: '명상 10분 루틴',
		desc: 'Headspace 가이드를 따라 마음챙김 습관 만들기',
		bg: '#8BC34A', // 썸네일 대신 컬러 카드
	},
	{
		id: 8,
		title: ' ukulele 4코드 마스터',
		desc: '주 2곡 커버 영상 인증, 초보 우쿨렐레 챌린지',
		bg: '#8BC34A',
	},
	{
		id: 9,
		title: 'DIY 미니어처룸 조립',
		desc: '키트 구매 → 매주 파트별 조립 인증',
		bg: '#8BC34A',
	},
	{
		id: 10,
		title: '세계 커피 원두 10종 테이스팅',
		desc: '에티오피아부터 과테말라까지, 노트 기록 공유',
		thumbnail: '/imgs/coffee.jpg',
	},
])
