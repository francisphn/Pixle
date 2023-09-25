package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.Relation

data class AttemptWithItems(
    @Embedded
    val attempt: Attempt,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "attemptUuid"
    )
    val attemptItems: List<AttemptItem>,
) {
}