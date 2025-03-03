package com.example.openai.service

import com.example.openai.entity.Answer
import com.example.openai.entity.Movie
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.converter.ListOutputConverter
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.convert.support.DefaultConversionService
import org.springframework.stereotype.Service
import java.util.Scanner

@Service
class ChatService(
    private val chatClient: ChatClient
) {

    fun chat(message: String): String {
        return chatClient.prompt()  //프롬프트 생성
            .user(message)          //사용자 메시지
            .call()                 //요청
            .content()              //응답 정보 (문자열)
    }

    fun chatMessage(message: String): String {
        return chatClient.prompt()
            .user(message)
            .call()
            .chatResponse() //ChatResponse
            .getResult()
            .getOutput()
            .getText()
    }

    fun chatPlace(subject: String, tone: String, message: String): String {
        return chatClient.prompt()
            .user(message)
            .system { it.param("subject", subject).param("tone", tone) }
            .call()
            .chatResponse() //ChatResponse
            .getResult()
            .getOutput()
            .getText()
    }

    fun chatJson(message: String): ChatResponse = chatClient.prompt().user(message).call().chatResponse()

    fun chatObject(message: String): Answer {
        return chatClient.prompt()
            .user(message)
            .call()
            .entity(Answer::class.java)
    }

    private val recipeTemplate = """
        Answer for {foodName} for {question} ?
    """.trimIndent()

    fun getRecipe(foodName: String, question: String): Answer =
        chatClient.prompt()
            .user {
                it.text(recipeTemplate)
                    .param("foodName", foodName)
                    .param("question", question)
            }
            .call()
            .entity(Answer::class.java)


    fun chatList(message: String): List<String> {
        return chatClient.prompt()
            .user(message)
            .call()
            .entity(ListOutputConverter(DefaultConversionService()))
    }

    fun chatMap(message: String): Map<String, String> {
        return chatClient.prompt()
            .user(message)
            .call()
            .entity(object : ParameterizedTypeReference<Map<String, String>>() {})
    }

    fun movieList(directorName: String): List<Movie> {
        val template = """
            Geneerate a list of movies directed by {directorName}. If the director is unknown, return null.
            한국 영화는 한글로 표기해줘.
            Each movie should include a title and release year. {format}
        """.trimIndent()

        return chatClient.prompt()
            .user {
                it.text(template)
                    .param("directorName", directorName)
                    .param("format", "json")
            }
            .call()
            .entity(object : ParameterizedTypeReference<List<Movie>>() {})
    }

    fun getResponse(message: String): String = chatClient.prompt().user(message).call().content()

    fun startChat() {
        val scanner = Scanner(System.`in`)
        println("Enter your message : ")
        while (true) {
            var message: String = scanner.nextLine()
            if (message.equals("exit")) {
                println("Exiting chat..")
                break
            }
            println("Bot: ${getResponse(message)}")
        }
        scanner.close()
    }
}