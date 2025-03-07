package com.example.openai.service

import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service

@Service
class AskService(private val chatModel: ChatModel) {
    fun getResponse(message: String): String {
        return chatModel.call(message)
    }

    fun getResponseOptions(message: String): String {
        val response : ChatResponse = chatModel.call(
            object : Prompt(
                message,
                OpenAiChatOptions.builder().model("gpt-4o").temperature(0.4).build()
            ) {}
        )
        return response.getResult().getOutput().getText()
    }
}