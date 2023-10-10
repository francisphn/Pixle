package app.pixle.model.entity.attempt

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID

@Serializable
@Entity
data class AtomicAttempt(
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

    /**
     * The general location of the attempt when it was taken
     */
    var location: String,

    var winningPhoto: String?,

)