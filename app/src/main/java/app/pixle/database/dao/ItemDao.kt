package app.pixle.database.dao

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import app.pixle.model.entity.item.Item

interface ItemDao {
    @Transaction
    @Insert
    suspend fun insert(items: List<Item>)

    @Query("SELECT * FROM item")
    suspend fun getAll(): List<Item>
}