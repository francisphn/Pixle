package app.pixle.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val name: String,
    val icon: String,
    val category: String,
    val difficulty: String
)