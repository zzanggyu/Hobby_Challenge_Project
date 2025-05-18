import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'
import colors from 'vuetify/util/colors'
import { VDateInput } from 'vuetify/labs/VDateInput'

// date-fns 어댑터 + 한국어 로케일
import DateFnsAdapter from '@date-io/date-fns'
import koLocale from 'date-fns/locale/ko'

// Vuetify가 제공하는 기본 ko 메시지 가져오기
import { ko as vuetifyKo } from 'vuetify/locale'

export default createVuetify({
	components: {
		...components, // 코어 컴포넌트 모두 등록
		VDateInput, // Labs 컴포넌트
	},
	directives, // Vuetify 디렉티브 등록
	icons: {
		defaultSet: 'mdi',
		aliases,
		sets: { mdi },
	},
	theme: {
		defaultTheme: 'light',
		themes: {
			light: {
				dark: false,
				colors: {
					background: '#f0f4f8',
					primary: colors.lightGreen.base,
					secondary: colors.lightGreen.darken4,
				},
			},
		},
	},
	// date-io 어댑터 설정
	date: {
		adapter: DateFnsAdapter,
		locale: { ko: koLocale }, // 한국어 등록
	},
	// Vuetify 내부 메시지도 한국어로
	locale: {
		locale: 'ko',
		fallback: 'en',
		messages: {
			ko: vuetifyKo,
		},
	},
})
