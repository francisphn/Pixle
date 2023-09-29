package app.pixle.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.model.entity.attempt.AttemptWithItems
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
@Dao
interface AttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: Attempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attemptItems: List<AttemptItem>)

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptUuid = attempt.uuid" +
            " WHERE attempt.solutionDate = :utcIsoDate")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems(utcIsoDate: String) : List<AttemptWithItems>

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptUuid = attempt.uuid")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems() : List<AttemptWithItems>
}