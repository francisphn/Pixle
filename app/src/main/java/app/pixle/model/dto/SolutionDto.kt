package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem

@Serializable
data class SolutionDto (
    val items: List<SolutionItemDto>,
    val difficulty: String,
    val day: String,
) {
    companion object {
        suspend fun getAnswerOfTheDay() : SolutionDto {
            val client = HttpClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response = client.get(SERVER_ENDPOINT)
            return response.body()
        }
    }

    fun asSolution() : Solution {
        val solutionItems = arrayListOf<SolutionItem>()

        for (item in this.items) {
            solutionItems.add(SolutionItem(
                emoji = item.icon,
                solutionDate = this.day,
                positionInSolution = solutionItems.size.plus(1L)
            ))
        }

        return Solution(day, difficulty, solutionItems)
    }

    fun countItems() = this.items.count()
}

