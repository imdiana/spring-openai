package com.example.openai.config

import com.example.openai.service.ChatService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

//@Component
class CommandLineAppStartRunner(private val chatService: ChatService) : CommandLineRunner {
    override fun run(vararg args: String?) {
        //chatService.startChat()
    }
}