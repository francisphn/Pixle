package app.pixle.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.pixle.database.dao.SolutionDao
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
    abstract fun solutionDao() : SolutionDao
}