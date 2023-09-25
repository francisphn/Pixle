package app.pixle.model.entity.solution

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["solutionDate", "positionInSolution"])
data class SolutionItem(
    /**
     * The content of this solution item, which is a singular emoji
     */
    @ColumnInfo("emoji")
    var emoji: String,

    /**
     * The solution that this solution item is associated with,
     * identified by a foreign key as solution date.
     */
    @ColumnInfo("solutionDate")
    var solutionDate: String,

    /**
     * The position of this emoji in the solution
     */
    @ColumnInfo("positionInSolution")
    var positionInSolution: Long,
) {
}