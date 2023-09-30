package app.pixle.model.entity.solution

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation

data class Solution(
    @Embedded val solution: AtomicSolution,

    @Relation(
        parentColumn = "date",
        entityColumn = "solutionDate"
    )
    val solutionItems: List<AtomicSolutionItem>
) {
    @Ignore
    val date = solution.date

    @Ignore
    val difficulty = solution.difficulty

    fun stringRepresentation(): String {
        return solutionItems
            .sortedBy { it.positionInSolution }
            .map { it.icon }
            .reduce { acc, icon -> acc.plus(icon) }
    }
}