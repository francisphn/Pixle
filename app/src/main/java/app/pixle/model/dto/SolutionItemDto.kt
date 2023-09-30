package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.lib.Utils
import app.pixle.model.entity.solution.SolutionItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject

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

        fun fromString(string: String): SolutionItemDto {
            return Json.decodeFromString(string)
        }

        fun fromString(string: String?): SolutionItemDto? {
            return if (string == null) {
                null
            } else {
                fromString(string)
            }
        }
    }

    override fun toString(): String {
        return Json.encodeToString(this)
    }
}