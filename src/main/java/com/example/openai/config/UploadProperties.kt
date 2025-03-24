package com.example.openai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "upload")
data class UploadProperties(
    var path: String = ""
)
