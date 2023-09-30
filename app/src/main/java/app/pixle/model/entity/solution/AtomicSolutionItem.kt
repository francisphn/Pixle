package app.pixle.model.entity.solution

import androidx.room.Entity

@Entity(primaryKeys = ["solutionDate", "positionInSolution", "category"])
data class AtomicSolutionItem(
    /**
     * The content of this solution item, which is a singular emoji
     */
    var icon: String,

    /**
     * The solution that this solution item is associated with,
     * identified by a foreign key as solution date.
     */
    var solutionDate: String,

    /**
     * The position of this emoji in the solution
     */
    var positionInSolution: Long,

    var category: String
) {
}