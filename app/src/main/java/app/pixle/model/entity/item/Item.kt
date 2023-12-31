package app.pixle.model.entity.item

import androidx.room.Entity

@Entity(primaryKeys = ["name", "icon", "category"])
data class Item(
    val name: String,

    val icon: String,

    val category: String,

    val difficulty: Long
)