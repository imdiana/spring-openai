package com.example.openai.config

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource

@Configuration
open class AppConfig(
    @Value("classpath:/prompt.txt") private val resource: Resource
) {

    /**
     * https://docs.spring.io/spring-ai/reference/api/chatclient.html
     * ChatClient <---OpenAPI key ---> LLM(openai)
     */
    @Bean
    open fun chatClient(chatClientBuilder: ChatClient.Builder) : ChatClient {
        return chatClientBuilder
            //.defaultSystem("당신은 교육 투터입닏. 개념을 명확하고 간단하게 설명하세요.") //System Message(LLM에 역할 부여)
            //.defaultSystem(resource)
            .defaultAdvisors(MessageChatMemoryAdvisor(InMemoryChatMemory()))
            .build();
    }
}