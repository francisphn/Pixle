package app.pixle

import app.pixle.model.Answer
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate
import java.time.ZoneId

class UnitTest {
    @Test
    fun getAnswerOfTheDay_isCorrect() = runBlocking {
        val answer = Answer.getAnswerOfTheDay()
        assertNotNull(answer)
        assertEquals(answer.date(), LocalDate.now(ZoneId.of("UTC")))
    }
}