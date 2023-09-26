package app.pixle.database.repository

import app.pixle.database.dao.SolutionDao
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SolutionRepository(private val solutionDao: SolutionDao) {
    suspend fun add(solutionWithItems: SolutionWithItems) = coroutineScope {
        launch (Dispatchers.IO) { solutionDao.insert(solutionWithItems.solution) }
        launch (Dispatchers.IO) { solutionDao.insert(solutionWithItems.solutionItems) }
    }

    suspend fun getLatestLazily() : Solution? {
        return solutionDao.getLatest()
    }

    suspend fun getLatestEagerly() : SolutionWithItems? {
        return solutionDao.getLatestSolutionWithItems()
    }
}