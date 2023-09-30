package app.pixle.model.entity.item

import androidx.room.Entity

@Entity
data class Item(
    val name: String,

    val icon: String,

    val category: String,

    val difficulty: Long
)