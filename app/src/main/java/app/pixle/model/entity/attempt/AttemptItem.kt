package app.pixle.model.entity.attempt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["attemptUuid", "positionInAttempt"])
data class AttemptItem(
    /**
     * The content of this attempt item, which is a singular emoji
     */
    var emoji : String,

    /**
     * The attempt that this attempt item is associated with.
     */
    var attemptUuid: String,

    /**
     * The position of this emoji in the attempt
     */
    var positionInAttempt: Long,
) {
}