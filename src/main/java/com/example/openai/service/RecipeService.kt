package com.example.openai.service

import com.example.openai.entity.Recipe
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.prompt.PromptTemplate
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import kotlinx.serialization.json.*
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

@Service
class RecipeService(
    private val chatModel: ChatModel,
) {
    private val apiKey: String = System.getenv("youtube.data.api-key")
    private val googleCx: String = System.getenv("google.custom.search.api-key")

    fun createRecipeWithUrls(recipe: Recipe) : Map<String, Any> {
        println("1")
        val recipeContent = createRecipe(recipe)
        println("2")
        val urls = searchRecipeUrls(recipe.ingredients)
        println("3")
        return mapOf("recipe" to recipeContent, "urls" to urls)
    }

    private fun createRecipe(recipe: Recipe): String {
        val template = """
            제목: 요리 제목을 제공해 주세요.
            다음 재료를 사용하여 요리법을 만들고 싶습니다: {ingredients}.
            선호하는 요리 유형은 {cuisine}입니다.
            다음 식이 제한을 고려해 주세요: {dietaryRestrictions}.
            재료 목록과 조리법을 포함한 상세한 요리법을 제공해 주세요.
        """.trimIndent()

        val promptTemplate = PromptTemplate(template)
        val params = mapOf(
            "ingredients" to recipe.ingredients,
            "cuisine" to recipe.cuisine,
            "dietaryRestrictions" to recipe.dietaryRestrictions
        )

        val prompt = promptTemplate.create(params as Map<String, Any>?)
        println(prompt.contents)

        return chatModel.call(prompt).result.output.text
    }

    private fun searchRecipeUrls(query: String): List<String> {
        println(apiKey)
        println(googleCx)

        val apiUrl: URI = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/customsearch/v1")
            .queryParam("key", apiKey)
            .queryParam("cx", googleCx)
            .queryParam("q", query)
            .build()
            .toUri()

        println("API 요청 URL: $apiUrl")

        val restTemplate = RestTemplate()

        return try {
            val response: String = restTemplate.getForObject(apiUrl, String::class.java) ?: return emptyList()

            val jsonElement = Json.parseToJsonElement(response).jsonObject
            val itemsArray = jsonElement["items"]?.jsonArray

            itemsArray?.mapNotNull { it.jsonObject["link"]?.jsonPrimitive?.contentOrNull } ?: emptyList()
        } catch (e: Exception) {
            println("API 호출 또는 JSON 파싱 중 오류 발생: ${e.message}")
            emptyList()
        }
    }
}
