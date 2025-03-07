package com.example.openai.controller

import org.springframework.http.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.net.URI

@RestController
class FileDownloadController(private val restTemplate: RestTemplate) {
    @GetMapping("/download-file")
    fun downloadFile(@RequestParam url : String): ResponseEntity<ByteArray> {
        try {
            val uri = URI(url)
            val response = restTemplate.getForEntity(uri, ByteArray::class.java)

            val fileName = extractFileName(url)
            val downloadHeaders = HttpHeaders().apply {
                contentType = MediaType.APPLICATION_OCTET_STREAM
                contentDisposition = ContentDisposition.attachment().filename(fileName).build()
            }

            return ResponseEntity(response.body, downloadHeaders, HttpStatus.OK)

        } catch (e : Exception) {
            return ResponseEntity.badRequest().body(("Failed to download file: ${e.message}").toByteArray())
        }
    }

    private fun extractFileName(url: String): String {
        return URI.create(url).path.substringAfterLast("/")
    }


}