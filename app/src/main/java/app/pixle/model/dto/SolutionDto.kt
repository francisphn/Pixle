package app.pixle.model.dto

import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.lib.Utils
import app.pixle.model.entity.solution.AtomicSolution
import app.pixle.model.entity.solution.AtomicSolutionItem
import app.pixle.model.entity.solution.Solution
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable

@Serializable
data class SolutionDto(
    val items: List<SolutionItemDto>,
    val difficulty: String,
    val day: String,
) {
    companion object {
        suspend fun ofTheDay(): SolutionDto {
            val response = Utils
                .getHttpClient()
                .get("$SERVER_ENDPOINT/oftheday")
            return response.body()
        }
    }

    fun asEntity(): Solution {
        val solutionItems = arrayListOf<AtomicSolutionItem>()

        for (item in this.items) {
            solutionItems.add(
                AtomicSolutionItem(
                    icon = item.icon,
                    solutionDate = this.day,
                    positionInSolution = solutionItems.size.plus(1L),
                    category = item.category,
                    name = item.name
                )
            )
        }

        val solution = AtomicSolution(day, difficulty)

        return Solution(solution, solutionItems)
    }

    fun countItems() = this.items.count()
}

