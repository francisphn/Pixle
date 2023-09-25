package app.pixle.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem

@Dao
interface SolutionDao {
    @Insert
    suspend fun insert(solution: Solution): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solutionItems: List<SolutionItem>)

    @Query("SELECT * FROM solution" +
            " JOIN solutionItem ON solution.date = solutionItem.solutionDate" +
            " ORDER BY date LIMIT 1")
    suspend fun getLatest(): Map.Entry<Solution?, List<SolutionItem>>
}