package app.pixle.model.entity.attempt

import android.net.Uri
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.time.LocalDate
import java.util.UUID

data class Attempt(
    @Embedded
    val attempt: AtomicAttempt,

    @Relation(
        parentColumn = "uuid",
        entityColumn = "attemptUuid"
    )
    val attemptItems: List<AtomicAttemptItem>,
) {
    val uuid: UUID
        get() = attempt.uuid

    val solutionDate: LocalDate
        get() = attempt.solutionDate

    var winningPhoto: Uri?
        get() = attempt.winningPhoto
        set(value) { attempt.winningPhoto = value }

    val isWinningAttempt: Boolean
        get() = attemptItems.all { it.kind == AtomicAttemptItem.KIND_EXACT }

    override fun toString(): String {
        return attemptItems
            .sortedBy { it.positionInAttempt }
            .map { it.icon }
            .reduce { acc, icon -> acc.plus(icon) }
    }
}