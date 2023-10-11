package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.lib.Utils
import app.pixle.model.entity.item.Item
import io.ktor.client.call.body
import io.ktor.client.request.get
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
            val response = Utils
                .getHttpClient()
                .get("$SERVER_ENDPOINT/lib")

            return response.body()
        }
    }

    fun asEntity() = Item(name, icon, category, difficulty)
}