package app.pixle.model.api

import android.content.Context
import app.pixle.database.room.PixleDatabase
import app.pixle.model.dto.Queryable
import app.pixle.model.entity.solution.SolutionWithItems

object Goal: Queryable<List<String>, SolutionWithItems> {
    override val key: List<String>
        get() = listOf("goal", "today")

    override suspend fun queryFn(keys: List<String>, context: Context): SolutionWithItems {
        return PixleDatabase
            .getInstance(context)
            .solutionRepository()
            .getToday()
    }
}