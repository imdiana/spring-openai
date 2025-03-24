package com.example.openai.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RouteController {
    @GetMapping("/view/ask")
    fun askView() : String {
        return "ask"
    }

    @GetMapping("/view/image")
    fun imageView() : String {
        return "image"
    }

    @GetMapping("/view/image-voice")
    fun imageVoiceView() : String {
        return "imagev"
    }

    @GetMapping("/view/image-text")
    fun imageTextView() : String {
        return "imagetext"
    }

}
