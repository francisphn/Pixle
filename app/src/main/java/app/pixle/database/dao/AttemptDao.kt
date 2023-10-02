package app.pixle.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import java.time.LocalDate

@Dao
interface AttemptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attempt: AtomicAttempt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attemptItems: List<AtomicAttemptItem>)

    @Transaction
    @Query("SELECT * FROM atomicAttempt" +
            " JOIN atomicAttemptItem on atomicAttemptItem.attemptUuid = atomicAttempt.uuid" +
            " WHERE atomicAttempt.solutionDate = :utcIsoDate")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems(utcIsoDate: String) : List<Attempt>

    @Transaction
    @Query("SELECT * FROM atomicAttempt" +
            " JOIN atomicAttemptItem on AtomicAttemptItem.attemptUuid = atomicAttempt.uuid")
    @RewriteQueriesToDropUnusedColumns
    suspend fun getAttemptsWithItems() : List<Attempt>
}