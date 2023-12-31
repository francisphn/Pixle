package app.pixle.model.entity.attempt

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
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

): Parcelable