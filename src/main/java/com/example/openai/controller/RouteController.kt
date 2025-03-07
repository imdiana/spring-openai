package com.example.openai.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RouteController {

    @GetMapping("/view")
    fun askView() : String {
        return "ask"
    }
}