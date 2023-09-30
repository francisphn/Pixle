package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.model.dto.Queryable
import app.pixle.model.dto.SolutionDto
import app.pixle.model.entity.solution.Solution

object SolutionOfToday: Queryable<List<String>, Solution> {
    override val key: List<String>
        get() = listOf("goal", "today")

    override suspend fun queryFn(keys: List<String>, context: Context): Solution {
        val repository = PixleDatabase.getInstance(context).solutionRepository()

        return repository.getToday()
            ?: SolutionDto
                .ofTheDay()
                .asEntity()
                .also { repository.add(it) }
    }
}