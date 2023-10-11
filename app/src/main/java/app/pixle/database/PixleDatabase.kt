package app.pixle.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.pixle.asset.PIXLE_DATABASE_NAME
import app.pixle.database.dao.AttemptDao
import app.pixle.database.dao.ItemDao
import app.pixle.database.dao.SolutionDao
import app.pixle.database.repository.AttemptRepository
import app.pixle.database.repository.SolutionRepository
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.item.Item
import app.pixle.model.entity.solution.AtomicSolution
import app.pixle.model.entity.solution.AtomicSolutionItem

@Database(entities = [
    AtomicSolution::class,
    AtomicSolutionItem::class,
    AtomicAttempt::class,
    AtomicAttemptItem::class,
    Item::class,
                     ],
    version = 1,
    exportSchema = true)
abstract class PixleDatabase : RoomDatabase() {
    protected abstract fun solutionDao(): SolutionDao

    protected abstract fun attemptDao(): AttemptDao

    abstract fun itemDao(): ItemDao

    @Volatile
    private var attemptRepository: AttemptRepository? = null

    fun attemptRepository(): AttemptRepository {
        return attemptRepository ?: synchronized(this) {
            AttemptRepository(attemptDao()).also { attemptRepository = it }
        }
    }

    @Volatile
    private var solutionRepository: SolutionRepository? = null

    fun solutionRepository(): SolutionRepository {
        return solutionRepository ?: synchronized(this) {
            SolutionRepository(solutionDao()).also { solutionRepository = it }
        }
    }

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
                )
                .build()
        }
    }
}