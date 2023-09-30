package app.pixle.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt

@Dao
interface AttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: AtomicAttempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attemptItems: List<AtomicAttemptItem>)

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptUuid = attempt.uuid" +
            " WHERE attempt.solutionDate = :utcIsoDate")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems(utcIsoDate: String) : List<Attempt>

    @Transaction
    @Query("SELECT * FROM attempt" +
            " JOIN attemptItem on attemptItem.attemptUuid = attempt.uuid")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems() : List<Attempt>
}