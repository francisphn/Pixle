package app.pixle.model.dto

import androidx.compose.runtime.Composable
import app.pixle.asset.SERVER_ENDPOINT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem
import app.pixle.model.entity.solution.SolutionWithItems
import app.pixle.ui.state.rememberQuery
import com.kazakago.swr.compose.config.SWRConfig
import com.kazakago.swr.compose.state.SWRState
import kotlinx.coroutines.CoroutineScope

@Serializable
data class SolutionDto (
    val items: List<SolutionItemDto>,
    val difficulty: String,
    val day: String,
) {
    companion object : Queryable<List<String>, SolutionDto>  {
        override val key: List<String>
            get() = listOf("goal", "today")

        override suspend fun queryFn(keys: List<String>): SolutionDto {
            return getAnswerOfTheDay()
        }

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

    fun asEntity() : SolutionWithItems {
        val solutionItems = arrayListOf<SolutionItem>()

        for (item in this.items) {
            solutionItems.add(SolutionItem(
                emoji = item.icon,
                solutionDate = this.day,
                positionInSolution = solutionItems.size.plus(1L)
            ))
        }

        val solution = Solution(day, difficulty)

        return SolutionWithItems(solution, solutionItems)
    }

    fun countItems() = this.items.count()
}

