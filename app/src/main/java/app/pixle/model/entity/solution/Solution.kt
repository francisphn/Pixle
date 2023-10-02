package app.pixle.model.entity.solution

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.time.LocalDate

data class Solution(
    @Embedded val solution: AtomicSolution,

    @Relation(
        parentColumn = "date",
        entityColumn = "solutionDate"
    )
    val solutionItems: List<AtomicSolutionItem>
) {

    val date: LocalDate
        get() = LocalDate.parse(solution.date)


    @Ignore
    val difficulty = solution.difficulty

    override fun toString(): String {
        return solutionItems
            .sortedBy { it.positionInSolution }
            .map { it.icon }
            .reduce { acc, icon -> acc.plus(icon) }
    }
}