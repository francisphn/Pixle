package app.pixle.model.api

import android.content.Context
import android.util.Log
import app.pixle.database.PixleDatabase
import app.pixle.lib.Utils
import app.pixle.model.api.contracts.Queryable
import app.pixle.model.dto.SolutionDto
import app.pixle.model.entity.solution.Solution

object SolutionOfToday: Queryable<List<String>, Solution> {
    override val key: List<String>
        get() = listOf("goal", Utils.utcDate().toString())

    override suspend fun queryFn(keys: List<String>, context: Context): Solution {
        Log.d("", "Querying solution of the day")

        val repository = PixleDatabase.getInstance(context).solutionRepository()

        return repository.getToday().also { Log.d("Today", it.toString()) }
            ?: SolutionDto.ofTheDay().asEntity().also { repository.add(it) }
    }
}