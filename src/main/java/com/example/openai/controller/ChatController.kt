package com.example.openai.controller

import com.example.openai.entity.Answer
import com.example.openai.entity.Movie
import com.example.openai.service.ChatService
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.web.bind.annotation.*

@RestController
class ChatController(private val chatService: ChatService) {

    @GetMapping("/chat")
    fun chat(@RequestParam("message") message: String): String {
        return chatService.chat(message)
    }

    @GetMapping("/chat-message")
    fun chatMessage(@RequestParam("message") message: String): String {
        return chatService.chatMessage(message)
    }

    @GetMapping("/chat-place")
    fun chatPlace(subject: String, tone: String, message: String): String {
        return chatService.chatPlace(subject, tone, message)
    }

    @GetMapping("/chat-json")
    fun chatJson(message: String): ChatResponse {
        return chatService.chatJson(message)
    }

    /** {"answer": "blahblahblah"} */
    @GetMapping("/chat-object")
    fun chatObject(message: String): Answer {
        return chatService.chatObject(message)
    }

    @GetMapping("/recipe")
    fun recipe(foodName: String, question: String): Answer {
        return chatService.getRecipe(foodName, question)
    }

    @GetMapping("/chat-list")
    fun chatList(message: String) : List<String> {
        return chatService.chatList(message)
    }

    @GetMapping("/chat-map")
    fun chatMap(message : String) : Map<String, String> {
        return chatService.chatMap(message)
    }

    @GetMapping("/movies")
    fun movie(directorName : String) : List<Movie> {
        return chatService.movieList(directorName)
    }
}