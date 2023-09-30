package app.pixle.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.solution.AtomicSolution
import app.pixle.model.entity.solution.AtomicSolutionItem
import app.pixle.model.entity.solution.Solution

@Dao
interface SolutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solution: AtomicSolution)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solutionItems: List<AtomicSolutionItem>)

    @Transaction
    @Query("SELECT * FROM solution" +
            " JOIN solutionItem ON solutionItem.solutionDate = solution.date" +
            " WHERE solution.date = :utcDate" +
            " LIMIT 1")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getSolutionForDate(utcDate: String): Solution?
}