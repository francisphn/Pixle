package app.pixle.model.api

import android.content.Context
import app.pixle.model.dto.Queryable
import app.pixle.model.dto.SolutionItemDto

object Library : Queryable<List<String>, List<SolutionItemDto>> {
    override val key: List<String>
        get() = listOf("lib")

    override suspend fun queryFn(keys: List<String>, context: Context): List<SolutionItemDto> {
        return SolutionItemDto.getLibraryOfItems()
    }
}