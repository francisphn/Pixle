package app.pixle.model.api

import app.pixle.model.dto.Queryable
import app.pixle.model.dto.SolutionDto

object Goal: Queryable<List<String>, SolutionDto> {
    override val key: List<String>
        get() = listOf("goal", "today")

    override suspend fun queryFn(keys: List<String>): SolutionDto {
        return SolutionDto.getAnswerOfTheDay()
    }
}