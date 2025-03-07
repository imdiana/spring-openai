package com.example.openai.controller

import com.example.openai.dto.ImageRequestDto
import com.example.openai.service.ImageGenerateService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ImageGenerateController(
    private val imageGenerateService: ImageGenerateService,
) {
    @PostMapping(value = ["/image"], consumes = ["application/json; charset=UTF-8"])
    fun generateImages(@RequestBody request : ImageRequestDto) : List<String> {
        val imageResponse = imageGenerateService.getImageGeneration(request)
        println(imageResponse)
        return imageResponse.results.map({it.output.url})
    }
}