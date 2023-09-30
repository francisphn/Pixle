package app.pixle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.pixle.database.PixleDatabase
import app.pixle.database.repository.AttemptRepository
import app.pixle.database.repository.SolutionRepository
import app.pixle.lib.Utils
import app.pixle.model.dto.SolutionDto
import app.pixle.model.entity.attempt.AtomicAttempt
import app.pixle.model.entity.attempt.AtomicAttemptItem
import app.pixle.model.entity.attempt.Attempt
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var solutionRepository: SolutionRepository
    private lateinit var attemptRepository: AttemptRepository

    private lateinit var database: PixleDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(context, PixleDatabase::class.java).build();

        solutionRepository = database.solutionRepository()
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
        val originalSolution = SolutionDto.ofTheDay().asEntity()

        solutionRepository.add(originalSolution)

        val fetchedSolution = solutionRepository.getToday()!!

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

        val yesterdayAttempt = AtomicAttempt(
            uuid = yesterdayAttemptUuid,
            solutionDate = Utils.utcDate().minusDays(1).toString()
        )

        val yesterdayAttemptItems = listOf("😱", "😭").mapIndexed { index, emoji ->
            AtomicAttemptItem(emoji, yesterdayAttemptUuid, index + 1L, AtomicAttemptItem.KIND_NONE)
        }

        val yesterday = Attempt(yesterdayAttempt, yesterdayAttemptItems)

        // Today's attempt
        val todayAttemptUuid = UUID.randomUUID().toString()

        val todayAttempt = AtomicAttempt(
            uuid = todayAttemptUuid,
            solutionDate = Utils.utcDate().toString()
        )

        val todayAttemptItems = listOf("😁", "🇬🇧").mapIndexed { index, emoji ->
            AtomicAttemptItem(emoji, todayAttemptUuid, index + 1L, AtomicAttemptItem.KIND_NONE)
        }

        val today = Attempt(todayAttempt, todayAttemptItems)

        attemptRepository.add(yesterday)
        attemptRepository.add(today)

        val fetchedToday = attemptRepository.getAttemptsOfToday()

        assertEquals(1, fetchedToday.size)
        assertEquals(today, fetchedToday.first())
    }

}