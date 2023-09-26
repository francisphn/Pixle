package app.pixle.model.entity.solution

import androidx.room.Embedded
import androidx.room.Relation

data class SolutionWithItems(
    @Embedded val solution: Solution,

    @Relation(
        parentColumn = "date",
        entityColumn = "solutionDate"
    )
    val solutionItems: List<SolutionItem>
)