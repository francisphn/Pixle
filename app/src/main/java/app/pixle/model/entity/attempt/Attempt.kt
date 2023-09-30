package app.pixle.model.entity.attempt

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class Attempt(
    @Embedded
    val attempt: AtomicAttempt,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "attemptUuid"
    )
    val attemptItems: List<AtomicAttemptItem>,
) {
    @Ignore
    val uuid = attempt.uuid

    @Ignore
    val solutionDate = attempt.solutionDate

    override fun toString(): String {
        return attemptItems
            .sortedBy { it.positionInAttempt }
            .map { it.icon }
            .reduce { acc, icon -> acc.plus(icon) }
    }
}