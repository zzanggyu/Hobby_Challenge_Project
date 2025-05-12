<!-- src/App.vue -->
<template>
	<v-app>
		<Header />

		<!-- 라우트에 매핑된 뷰가 이 자리에 렌더링됩니다 -->
		<v-main class="pt-20">
			<router-view />
		</v-main>

		<Footer />
	</v-app>
</template>

<script setup>
import Header from '@/components/layout/Header.vue'
import Footer from '@/components/layout/Footer.vue'
</script>

<style>
/* 글로벌 스타일이나 스코프 필요시 추가 */
</style>
