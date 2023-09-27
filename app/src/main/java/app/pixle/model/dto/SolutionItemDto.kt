package app.pixle.model.dto

import app.pixle.model.entity.solution.SolutionItem
import kotlinx.serialization.Serializable

@Serializable
data class SolutionItemDto(
    val name: String,
    val icon: String,
    val category: String,
    val difficulty: Long
)