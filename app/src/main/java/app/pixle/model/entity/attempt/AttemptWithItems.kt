package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.Relation

data class AttemptWithItems(
    @Embedded
    val attempt: Attempt,

    @Relation(
        parentColumn = "id",
        entityColumn = "attemptId"
    )
    val attemptItems: List<AttemptItem>,
) {
}