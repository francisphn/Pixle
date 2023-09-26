package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Attempt(
    /**
     * The ID of this attempt
     */
    @PrimaryKey(autoGenerate = false)
    val uuid: String,

    /**
     * The date of the solution that this attempt should be checked against,
     * this is the foreign key to link this attempt to a solution
     */
    var solutionDate: String,

)