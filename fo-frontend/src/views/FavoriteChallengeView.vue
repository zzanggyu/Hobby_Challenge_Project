<!-- src/views/FavoriteListView.vue -->
<template>
	<div>
		<!-- <h2 class="text-h5 mb-4">관심 챌린지</h2> -->
		<!-- 컴포넌트는 components/challenge 아래에 만드신 것을 import -->
		<FavoriteChallenge />
	</div>
</template>

<script setup>
import FavoriteChallenge from '@/components/challenge/FavoriteChallenge.vue'
</script>

<style scoped></style>
