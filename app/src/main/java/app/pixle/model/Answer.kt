package app.pixle.model

import app.pixle.asset.SERVER_ENDPOINT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class Answer (
    val items: List<Item>,
    val difficulty: String,
    val day: String,
) {
    fun date() = LocalDate.parse(day, DateTimeFormatter.ISO_DATE)

    companion object {
        suspend fun getAnswerOfTheDay() : Answer {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.get(SERVER_ENDPOINT)
            return response.body()
        }
    }
}

