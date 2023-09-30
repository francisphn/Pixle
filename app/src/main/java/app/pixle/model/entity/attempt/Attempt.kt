package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.Relation

data class Attempt(
    @Embedded
    private val attempt: AtomicAttempt,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "attemptUuid"
    )
    val attemptItems: List<AtomicAttemptItem>,
) {
    val uuid = attempt.uuid

    val solutionDate = attempt.solutionDate
}