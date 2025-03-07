package com.example.openai.controller

import com.example.openai.service.AskService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AskController(private val askService: AskService) {

    @GetMapping("/ask")
    fun getResponse(@RequestParam("message") message : String) : String {
        return askService.getResponse(message)
    }

    @GetMapping("/ask-ai")
    fun getResponseOptions(@RequestParam("message") message : String) : String {
        return askService.getResponseOptions(message)
    }
}