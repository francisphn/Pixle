package app.pixle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import app.pixle.asset.PIXLE_DATABASE_NAME
import app.pixle.database.dao.AttemptDao
import app.pixle.database.dao.SolutionDao
import app.pixle.database.repository.AttemptRepository
import app.pixle.database.repository.SolutionRepository
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.model.entity.solution.Solution
import app.pixle.model.entity.solution.SolutionItem

@Database(entities = [
    Solution::class,
    SolutionItem::class,
    Attempt::class,
    AttemptItem::class
                     ],
    version = 1)
abstract class PixleDatabase : RoomDatabase() {
    abstract fun solutionDao(): SolutionDao

    abstract fun attemptDao(): AttemptDao

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