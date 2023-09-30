package app.pixle.model.entity.solution

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AtomicSolution (
    /**
     * The ID of this solution, and because there is only one
     * solution for a given date, it can be used as a primary
     * key.
     */
    @PrimaryKey(autoGenerate = false)
    var date: String,

    /**
     * Difficulty level
     */
    var difficulty: String,
)