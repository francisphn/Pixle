package app.pixle.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.pixle.asset.PIXLE_DATABASE_NAME
import app.pixle.database.room.dao.AttemptDao
import app.pixle.database.room.dao.SolutionDao
import app.pixle.database.room.repository.AttemptRepository
import app.pixle.database.room.repository.SolutionRepository
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem

@Database(entities = [
    Solution::class,
    SolutionItem::class,
    AtomicAttempt::class,
    AtomicAttemptItem::class,
                     ],
    version = 1)
abstract class PixleDatabase : RoomDatabase() {
    protected abstract fun solutionDao(): SolutionDao

    protected abstract fun attemptDao(): AttemptDao

    fun attemptRepository() = AttemptRepository(attemptDao())

    fun solutionRepository() = SolutionRepository(solutionDao())

    companion object {
        @Volatile
        private var instance: PixleDatabase? = null

        fun getInstance(context: Context): PixleDatabase {
            return instance ?: synchronized(this) {
                buildDatabase(context)
                    .also { instance = it }
                    .also { return it }
            }
        }

        private fun buildDatabase(context: Context): PixleDatabase {
            return Room
                .databaseBuilder(
                    context,
                    PixleDatabase::class.java,
                    PIXLE_DATABASE_NAME
                ).build()
        }
    }
}