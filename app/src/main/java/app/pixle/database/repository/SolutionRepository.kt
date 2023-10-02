package app.pixle.database.repository

import android.util.Log
import app.pixle.database.dao.SolutionDao
import app.pixle.lib.Utils
import app.pixle.model.entity.solution.Solution
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SolutionRepository(private val solutionDao: SolutionDao) {
    suspend fun add(solutionWithItems: Solution) = coroutineScope {
        launch(Dispatchers.IO) { solutionDao.insert(solutionWithItems.solution) }
        launch(Dispatchers.IO) { solutionDao.insert(solutionWithItems.solutionItems) }
    }

    suspend fun getToday(): Solution? {
        val today = Utils.utcDate()
        Log.d("solution", "Getting solution for today, $today, from Room")
        return solutionDao.getSolutionForDate(today).also { Log.d("Solution", "$it") }
    }
}