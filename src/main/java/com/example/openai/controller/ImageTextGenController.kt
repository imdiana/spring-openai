package com.example.openai.controller

import com.example.openai.config.UploadProperties
import com.example.openai.dto.ImageAnalysisDto
import com.example.openai.service.ImageTextGenService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@RestController
class ImageTextGenController(
    private val imageTextGenService: ImageTextGenService,
    private val uploadProperties: UploadProperties
) {

    @PostMapping("/image-text/analyze")
    fun getMultimodalResponse(
        @RequestParam("image") imageFile: MultipartFile,
        @RequestParam(defaultValue = "이 이미지에 무엇이 있나요?") message: String
    ): ResponseEntity<ImageAnalysisDto> {
        File(uploadProperties.path).apply {
            if (!exists()) mkdirs() // 업로드 디렉토리 생성
        }

        val filename = imageFile.originalFilename ?: throw IllegalArgumentException("파일 이름이 존재하지 않습니다.")
        val filePath = Paths.get(uploadProperties.path, filename)

        Files.write(filePath, imageFile.bytes) // 파일 저장

        val analysisText = imageTextGenService.analyzeImage(imageFile, message)
        val imageUrl = "/uploads/$filename"

        // 세제곱근, 제곱근, 곱셈
        val searchKeyword = imageTextGenService.extractKeyYouTubeSearch(analysisText)
        val youtubeUrls = searchKeyword?.let { imageTextGenService.searchYouTubeVideos(it) } ?: emptyList()

        return ResponseEntity.ok(ImageAnalysisDto(imageUrl, analysisText, youtubeUrls))
    }
}
