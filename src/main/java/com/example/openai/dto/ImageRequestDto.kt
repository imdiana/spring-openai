package com.example.openai.dto

data class ImageRequestDto(
    val message : String,
    val model : String,
    val n : Int
)