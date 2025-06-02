/**
 * 이미지 리사이징 유틸리티
 * @param {File} file - 원본 이미지 파일
 * @param {Object} options - 리사이징 옵션
 * @returns {Promise<File>} - 리사이징된 이미지 파일
 */
export function resizeImage(file, options = {}) {
	return new Promise((resolve, reject) => {
		// 기본 옵션 설정
		const {
			maxWidth = 800, // 최대 너비
			maxHeight = 600, // 최대 높이
			quality = 0.85, // 품질 (0.1 ~ 1.0)
			format = 'jpeg', // 출력 포맷
		} = options

		console.log('이미지 리사이징 시작:', {
			원본파일: file.name,
			원본크기: `${(file.size / 1024 / 1024).toFixed(2)}MB`,
			타겟사이즈: `${maxWidth}x${maxHeight}`,
			품질: `${quality * 100}%`,
		})

		// Canvas와 Image 엘리먼트 생성
		const canvas = document.createElement('canvas')
		const ctx = canvas.getContext('2d')
		const img = new Image()

		img.onload = () => {
			try {
				// 원본 이미지 크기
				const originalWidth = img.width
				const originalHeight = img.height

				console.log(
					'원본 이미지 크기:',
					`${originalWidth}x${originalHeight}`
				)

				// 비율 계산 (가로세로 비율 유지)
				let newWidth = originalWidth
				let newHeight = originalHeight

				// 최대 크기를 넘으면 비율에 맞춰 줄이기
				if (originalWidth > maxWidth || originalHeight > maxHeight) {
					const widthRatio = maxWidth / originalWidth
					const heightRatio = maxHeight / originalHeight
					const ratio = Math.min(widthRatio, heightRatio)

					newWidth = Math.round(originalWidth * ratio)
					newHeight = Math.round(originalHeight * ratio)
				}

				console.log(' 계산된 새 크기:', `${newWidth}x${newHeight}`)

				// Canvas 크기 설정
				canvas.width = newWidth
				canvas.height = newHeight

				// 이미지 품질 향상을 위한 설정
				ctx.imageSmoothingEnabled = true
				ctx.imageSmoothingQuality = 'high'

				// 배경을 흰색으로 채우기 (투명 PNG를 JPEG로 변환할 때)
				if (format === 'jpeg') {
					ctx.fillStyle = '#FFFFFF'
					ctx.fillRect(0, 0, newWidth, newHeight)
				}

				// 이미지를 Canvas에 그리기
				ctx.drawImage(img, 0, 0, newWidth, newHeight)

				// Canvas를 Blob으로 변환
				canvas.toBlob(
					(blob) => {
						if (!blob) {
							reject(new Error('이미지 압축에 실패했습니다.'))
							return
						}

						// Blob을 File 객체로 변환
						const fileName = file.name.replace(
							/\.[^/.]+$/,
							`.${format === 'jpeg' ? 'jpg' : format}`
						)
						const resizedFile = new File([blob], fileName, {
							type: `image/${format}`,
							lastModified: Date.now(),
						})

						// 압축 결과 로깅
						const compressionRatio = (
							((file.size - resizedFile.size) / file.size) *
							100
						).toFixed(1)
						console.log(' 이미지 리사이징 완료:', {
							최종크기: `${newWidth}x${newHeight}`,
							압축전: `${(file.size / 1024 / 1024).toFixed(2)}MB`,
							압축후: `${(resizedFile.size / 1024 / 1024).toFixed(2)}MB`,
							압축률: `${compressionRatio}%`,
						})

						resolve(resizedFile)
					},
					`image/${format}`,
					quality
				)
			} catch (error) {
				console.error('❌ 이미지 처리 중 오류:', error)
				reject(error)
			}
		}

		img.onerror = (error) => {
			console.error('❌ 이미지 로드 실패:', error)
			reject(new Error('이미지를 로드할 수 없습니다.'))
		}

		// 이미지 로드 시작
		img.src = URL.createObjectURL(file)
	})
}

// 이미지 크기 정보 가져오기 (리사이징 없이)

export function getImageInfo(file) {
	return new Promise((resolve, reject) => {
		const img = new Image()

		img.onload = () => {
			resolve({
				width: img.width,
				height: img.height,
				size: file.size,
				type: file.type,
				name: file.name,
			})
			URL.revokeObjectURL(img.src) // 메모리 정리
		}

		img.onerror = () => {
			reject(new Error('이미지 정보를 읽을 수 없습니다.'))
		}

		img.src = URL.createObjectURL(file)
	})
}

// 파일 크기를 읽기 쉬운 형태로 변환

export function formatFileSize(bytes) {
	if (bytes === 0) return '0 Bytes'

	const k = 1024
	const sizes = ['Bytes', 'KB', 'MB', 'GB']
	const i = Math.floor(Math.log(bytes) / Math.log(k))

	return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}
