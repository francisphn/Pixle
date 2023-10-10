package app.pixle.model.entity.attempt

import androidx.room.Entity
import kotlinx.serialization.Serializable
import java.util.UUID


@Serializable
@Entity(primaryKeys = ["attemptUuid", "positionInAttempt"])
data class AtomicAttemptItem(
    /**
     * The content of this attempt item, which is a singular emoji
     */
    var icon : String,

    /**
     * The attempt that this attempt item is associated with.
     */
    var attemptUuid: String,

    /**
     * The position of this emoji in the attempt
     */
    var positionInAttempt: Long,

    /**
     * The kind of this attempt item, which is either "exact", "similar", or "none"
     */
    var kind: String
) {
    companion object {
        const val KIND_EXACT = "exact"
        const val KIND_SIMILAR = "similar"
        const val KIND_NONE = "none"
    }
}