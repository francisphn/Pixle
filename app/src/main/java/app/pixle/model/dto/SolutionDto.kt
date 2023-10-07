package app.pixle.model.dto

import android.util.Log
import app.pixle.asset.SERVER_ENDPOINT
import app.pixle.lib.Utils
import app.pixle.model.entity.solution.AtomicSolution
import app.pixle.model.entity.solution.AtomicSolutionItem
import app.pixle.model.entity.solution.Solution
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable

@Serializable
data class SolutionDto(
    val items: List<SolutionItemDto>,
    val difficulty: String,
    val day: String,
) {
    companion object {
        suspend fun ofTheDay() = Utils
            .getHttpClient()
            .get("$SERVER_ENDPOINT/oftheday")
            .body<SolutionDto>()
            .also { Log.d("solution", "Getting solution for today, ${Utils.utcDate()}, from remote") }

    }

    fun asEntity(): Solution {
        val solutionItems = this.items.mapIndexed { index, item ->
            AtomicSolutionItem(
                icon = item.icon,
                solutionDate = this.day,
                positionInSolution = index.plus(1L),
                category = item.category,
                name = item.name,
            )
        }

        return Solution(AtomicSolution(this.day, difficulty), solutionItems)
            .also { Log.d("solution", it.toString()) }
    }
}

