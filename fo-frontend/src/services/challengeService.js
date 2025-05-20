import api from '@/api' // src/api.js

export const createChallenge = (payload) =>
	api.post('/challenges', payload).then((res) => res.data)

export const getChallenges = () =>
	api.get('/challenges').then((res) => res.data)

export const joinChallenge = (challengeId) =>
	api.post(`/challenges/${challengeId}/join`).then((res) => res.data)
