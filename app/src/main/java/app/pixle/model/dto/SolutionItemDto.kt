package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.model.entity.item.Item
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class SolutionItemDto(
    val name: String,
    val icon: String,
    val category: String,
    val difficulty: Long
) {
    companion object {
        suspend fun getLibraryOfItems(): List<SolutionItemDto> {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.get("$SERVER_ENDPOINT/lib")

            return response.body()
        }
    }

    fun asEntity() = Item(name, icon, category, difficulty)
}