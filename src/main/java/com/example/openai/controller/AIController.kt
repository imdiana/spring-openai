package com.example.openai.controller

import org.springframework.http.*
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions
import org.springframework.ai.openai.api.OpenAiAudioApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@RestController
class AIController(
    private val openAiAudioTranscriptionModel: OpenAiAudioTranscriptionModel,
    @Value("classpath:response.mp4") private val resource: Resource
) {

    @GetMapping("/transcribe")
    fun transcribe(): ResponseEntity<String> {
        val options = OpenAiAudioTranscriptionOptions.builder()
            .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
            .temperature(0f)
            .build()

        val audioTranscriptionPrompt = AudioTranscriptionPrompt(resource, options)
        val audioTranscriptionResponse = openAiAudioTranscriptionModel.call(audioTranscriptionPrompt)

        return ResponseEntity.ok(audioTranscriptionResponse.result.output)
    }

    @PostMapping("/transcribe")
    fun transcribe(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        val resource: Resource = file.resource

        val options = OpenAiAudioTranscriptionOptions.builder()
            .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
            .language("ko")
            .temperature(0f)
            .build()

        val audioTranscriptionPrompt = AudioTranscriptionPrompt(resource, options)
        val audioTranscriptionResponse = openAiAudioTranscriptionModel.call(audioTranscriptionPrompt)

        return ResponseEntity.ok(audioTranscriptionResponse.result.output)
    }
}
