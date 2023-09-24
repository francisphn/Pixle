package app.pixle.model.entity.solution

import androidx.room.PrimaryKey

class Solution (
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

    /**
     * The items associated with the solution
     */
    var solutionItems: List<SolutionItem>
)