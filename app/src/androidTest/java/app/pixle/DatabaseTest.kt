package app.pixle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.pixle.database.PixleDatabase
import app.pixle.database.dao.AttemptDao
import app.pixle.database.dao.SolutionDao
import app.pixle.database.repository.AttemptRepository
import app.pixle.database.repository.SolutionRepository
import app.pixle.lib.Utils
import app.pixle.model.dto.SolutionDto
import app.pixle.model.entity.attempt.Attempt
import app.pixle.model.entity.attempt.AttemptItem
import app.pixle.model.entity.attempt.AttemptWithItems
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import java.util.stream.Stream

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var solutionDao: SolutionDao
    private lateinit var solutionRepository: SolutionRepository

    private lateinit var attemptDao: AttemptDao
    private lateinit var attemptRepository: AttemptRepository

    private lateinit var database: PixleDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(context, PixleDatabase::class.java).build();

        solutionDao = database.solutionDao()
        solutionRepository = database.solutionRepository()

        attemptDao = database.attemptDao()
        attemptRepository = database.attemptRepository()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun solutionRepositoryTest() = runBlocking {
        val originalSolution = SolutionDto.getAnswerOfTheDay().asEntity()

        solutionRepository.add(originalSolution)

        val fetchedSolution = solutionRepository.getLatestEagerly()!!

        assertNotNull(fetchedSolution)
        assertEquals(originalSolution, fetchedSolution)
        assertEquals(originalSolution.solution, originalSolution.solution)
        assertEquals(originalSolution.solutionItems, originalSolution.solutionItems)
    }

    @Test
    @Throws(Exception::class)
    fun attemptRepositoryTest(): Unit = runBlocking {
        // Yesterday's attempt
        val yesterdayAttemptUuid = UUID.randomUUID().toString()

        val yesterdayAttempt = Attempt(
            uuid = yesterdayAttemptUuid,
            solutionDate = Utils.utcDate().minusDays(1).toString()
        )

        val yesterdayAttemptItems = listOf("😱", "😭").mapIndexed { index, emoji ->
            AttemptItem(emoji, yesterdayAttemptUuid, index + 1L)
        }

        val yesterday = AttemptWithItems(yesterdayAttempt, yesterdayAttemptItems)

        // Today's attempt
        val todayAttemptUuid = UUID.randomUUID().toString()

        val todayAttempt = Attempt(
            uuid = yesterdayAttemptUuid,
            solutionDate = Utils.utcDate().toString()
        )

        val todayAttemptItems = listOf("😁", "🇬🇧").mapIndexed { index, emoji ->
            AttemptItem(emoji, yesterdayAttemptUuid, index + 1L)
        }

        val today = AttemptWithItems(todayAttempt, todayAttemptItems)

        attemptRepository.add(yesterday)
        attemptRepository.add(today)

        val fetchedToday = attemptRepository.getTodayAttemptsWithItems()

        assertEquals(fetchedToday.size, 1)
        assertEquals(today, fetchedToday[0])
    }

}