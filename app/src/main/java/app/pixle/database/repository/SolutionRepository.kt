package app.pixle.database.repository

import app.pixle.database.dao.SolutionDao
import app.pixle.lib.Utils
import app.pixle.model.dto.SolutionDto
import app.pixle.model.entity.solution.SolutionWithItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SolutionRepository(private val solutionDao: SolutionDao) {
    suspend fun add(solutionWithItems: SolutionWithItems) = coroutineScope {
        launch (Dispatchers.IO) { solutionDao.insert(solutionWithItems.solution) }
        launch (Dispatchers.IO) { solutionDao.insert(solutionWithItems.solutionItems) }
    }

    suspend fun getToday() : SolutionWithItems {
        return solutionDao.getSolutionForDate(Utils.utcDate().toString())
            ?: SolutionDto.ofTheDay().asEntity().also { this.add(it) }
    }
}