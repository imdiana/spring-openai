package com.example.openai.service

import kotlinx.serialization.json.*
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.model.Media
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.MimeType
import org.springframework.util.MimeTypeUtils
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile


@Service
class ImageTextGenService(
    @Value("classpath:/system.message") private val defaultSystemMessage : Resource,
    private val chatModel: ChatModel
) {
    fun analyzeImage(imageFile: MultipartFile, message: String) : String {
        // MIME 타입 결정
        val contentType = imageFile.contentType
        require(
            contentType == MimeTypeUtils.IMAGE_PNG_VALUE || contentType == MimeTypeUtils.IMAGE_JPEG_VALUE
        ) { "지원되지 않는 이미지 형식입니다." }

        try {
            // Media 객체 생성
            val media = Media(MimeType.valueOf(contentType), imageFile.resource)
            // 사용자 메시지 생성
            val userMessage = UserMessage(message, media)
            // 시스템 메시지 생성
            val systemMessage = SystemMessage(defaultSystemMessage)
            // AI 모델 호출
            return chatModel.call(userMessage, systemMessage)
        } catch (e: Exception) {
            throw RuntimeException("이미지 처리 중 오류가 발생했습니다.", e)
        }
    }

    fun searchYouTubeVideos(query: String): List<String> {
        val apiKey = System.getenv("youtube.data.api-key")
        val url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=EBS$query&order=relevance&key=$apiKey"
        val restTemplate = RestTemplate()
        val response: ResponseEntity<String> = restTemplate.getForEntity(url, String::class.java)
        println(response)

        return try {
            val jsonElement = Json.parseToJsonElement(response.body ?: "").jsonObject
            val items = jsonElement["items"]?.jsonArray ?: return emptyList()

            items.mapNotNull {
                val videoId = it.jsonObject["id"]?.jsonObject?.get("videoId")?.jsonPrimitive?.contentOrNull
                videoId
            }
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            emptyList()
        }
    }

    fun extractKeyYouTubeSearch(analysisText: String): String? {
        return analysisText.substringAfter("핵심 키워드:", "").trim().takeIf { it.isNotEmpty() }
    }
}
