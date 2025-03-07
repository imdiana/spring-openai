package com.example.openai.service

import com.example.openai.dto.ImageRequestDto
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.image.ImageResponse
import org.springframework.ai.openai.OpenAiImageModel
import org.springframework.ai.openai.OpenAiImageOptions
import org.springframework.stereotype.Service

@Service
class ImageGenerateService(private val openAiImageModel: OpenAiImageModel) { //dall-e-3 default model
    fun getImageGeneration(request : ImageRequestDto) : ImageResponse {
        return openAiImageModel.call(
            ImagePrompt(
                request.message,
                OpenAiImageOptions.builder()
                    .model(request.model)
                    .quality("hd")
                    .N(request.n) // 생성할 이미지 개수
                    .height(1024)
                    .width(1024)
                    .build()
            )
        )
    }
}