package app.pixle.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "key")
class Key (
    @PrimaryKey(autoGenerate = false)
    var date: String,

    @ColumnInfo
    var difficulty: String,
)