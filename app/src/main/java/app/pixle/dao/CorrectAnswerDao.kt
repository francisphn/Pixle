package app.pixle.dao

import androidx.room.Dao
import androidx.room.Insert
import app.pixle.model.entity.Key
import java.time.LocalDate

@Dao
interface CorrectAnswerDao {
    @Insert
    suspend fun insert(answer: Key): LocalDate

    suspend fun getLatest()
}