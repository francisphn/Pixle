package app.pixle.dao

import androidx.room.Dao
import androidx.room.Insert
import app.pixle.model.entity.solution.Solution
import java.time.LocalDate

@Dao
interface KeyDao {
    @Insert
    suspend fun insert(answer: Solution): LocalDate

    suspend fun getLatest()
}