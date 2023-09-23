package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import app.pixle.model.entity.Key

@Serializable
data class KeyDto (
    val items: List<ItemDto>,
    val difficulty: String,
    val day: String,
) {
    companion object {
        suspend fun getAnswerOfTheDay() : KeyDto {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.get(SERVER_ENDPOINT)
            return response.body()
        }
    }

    fun asKey() = Key(day, difficulty)
}

