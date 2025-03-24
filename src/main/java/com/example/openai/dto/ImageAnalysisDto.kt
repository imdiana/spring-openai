package com.example.openai.dto

data class ImageAnalysisDto(
    val imageUrl: String,
    val analysisText : String,
    val youtubeUrls : List<String>? = null
)
