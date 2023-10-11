package app.pixle

import app.pixle.lib.Utils
import app.pixle.model.dto.SolutionDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UnitTest {
    @Test
    fun getSolutionOfTheDay_isCorrect() = runBlocking {
        val solution = SolutionDto.ofTheDay().asEntity()

        assertNotNull(solution)

        assertEquals(
            LocalDate.parse(solution.solution.date, DateTimeFormatter.ISO_DATE),
            Utils.utcDate()
        )
    }
}