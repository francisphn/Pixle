package app.pixle

import app.pixle.model.dto.SolutionDto
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UnitTest {
    @Test
    fun getSolutionOfTheDay_isCorrect() = runBlocking {
        val solution = SolutionDto.getAnswerOfTheDay().asSolution()

        assertNotNull(solution)

        assertEquals(
            LocalDate.parse(solution.date, DateTimeFormatter.ISO_DATE),
            LocalDate.now(ZoneId.of("UTC"))
        )
    }
}