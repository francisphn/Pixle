package app.pixle.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val name: String,
    val icon: String,
    val category: String,
    val difficulty: Long
)