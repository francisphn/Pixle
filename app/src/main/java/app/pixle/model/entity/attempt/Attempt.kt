package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.PrimaryKey

class Attempt(
    /**
     * The ID of this attempt
     */
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    /**
     * The date of the solution that this attempt should be checked against,
     * this is the foreign key to link this attempt to a solution
     */
    var solutionDate: Long,

    /**
     * The items associated with this attempt
     */
    var attemptItems: List<AttemptItem>
) {
}