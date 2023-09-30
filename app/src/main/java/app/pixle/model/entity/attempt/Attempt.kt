package app.pixle.model.entity.attempt

import android.net.Uri
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

    var winningPhoto: Uri?
        get() = attempt.winningPhoto
        set(value) { attempt.winningPhoto = value }

    override fun toString(): String {
        return attemptItems
            .sortedBy { it.positionInAttempt }
            .map { it.icon }
            .reduce { acc, icon -> acc.plus(icon) }
    }
}