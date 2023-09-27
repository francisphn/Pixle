package app.pixle.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem
import app.pixle.model.entity.solution.SolutionWithItems

@Dao
interface SolutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solution: Solution)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solutionItems: List<SolutionItem>)

    @Transaction
    @Query("SELECT * FROM solution" +
            " JOIN solutionItem ON solutionItem.solutionDate = solution.date" +
            " ORDER BY date desc LIMIT 1")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getLatestSolutionWithItems(): SolutionWithItems?

    @Query("SELECT * FROM solution ORDER by date desc LIMIT 1")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getLatest(): Solution?
}