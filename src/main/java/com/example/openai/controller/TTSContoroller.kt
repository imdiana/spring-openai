package com.example.openai.controller

import org.springframework.ai.openai.OpenAiAudioSpeechModel
import org.springframework.ai.openai.OpenAiAudioSpeechOptions
import org.springframework.ai.openai.api.OpenAiAudioApi
import org.springframework.ai.openai.audio.speech.SpeechPrompt
import org.springframework.ai.openai.audio.speech.SpeechResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import reactor.core.publisher.Flux
import java.io.IOException
import java.io.OutputStream

@RestController
class TTSController(
    private val openAiAudioSpeechModel: OpenAiAudioSpeechModel
) {
    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<StreamingResponseBody> {

        val content = file.bytes.toString(Charsets.UTF_8)

        val options = OpenAiAudioSpeechOptions.builder()
            .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
            .speed(1.1f)
            .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
            .model(OpenAiAudioApi.TtsModel.TTS_1.value)
            .build();

        val responseStream : Flux<SpeechResponse> = openAiAudioSpeechModel.stream(SpeechPrompt(content, options))

        val stream = StreamingResponseBody { outputStream ->
            responseStream.toStream().forEach { speechResponse -> writeToOutput(outputStream, speechResponse) }
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "audio/mpeg").body(stream)
    }

    private fun writeToOutput(outputStream: OutputStream, speechResponse: SpeechResponse) {
        try {
            // 데이터를 출력 스트림에 작성
            outputStream.write(speechResponse.result.output) // byte[]
            outputStream.flush() // 즉시 전송
        } catch (e: IOException) {
            throw RuntimeException("Error writing audio data to output stream", e)
        }
    }
}
