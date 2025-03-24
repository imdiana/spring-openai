package com.example.openai.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableConfigurationProperties(UploadProperties::class)
open class WebConfig(
    private val uploadProperties: UploadProperties
) : WebMvcConfigurer {
    @Override
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        //super.addResourceHandlers(registry)
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:${uploadProperties.path}")
    }
}
