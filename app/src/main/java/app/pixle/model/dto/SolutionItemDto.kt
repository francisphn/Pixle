package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.model.entity.solution.SolutionItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

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
}