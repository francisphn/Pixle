package app.pixle.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.attempt.AttemptItem
import java.time.LocalDate

@Dao
interface AttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: Attempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attemptItems: AttemptItem)

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptId = attempt.id" +
            " WHERE attempt.solutionDate = :utcIsoDate")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems(utcIsoDate: String)

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptId = attempt.id")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems()
}