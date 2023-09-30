package app.pixle.model.api

import android.content.Context
import app.pixle.database.PixleDatabase
import app.pixle.model.dto.SolutionItemDto
import app.pixle.model.entity.item.Item

object Library : Queryable<List<String>, List<Item>> {
    override val key: List<String>
        get() = listOf("lib")

    override suspend fun queryFn(keys: List<String>, context: Context): List<Item> {
        return try {
            SolutionItemDto.getLibraryOfItems().map { it.asEntity() }
        } catch (e: Exception) {
            PixleDatabase.getInstance(context).itemDao().getAll()
        }
    }
}