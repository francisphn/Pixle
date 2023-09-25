package app.pixle

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.pixle.database.PixleDatabase
import app.pixle.database.dao.SolutionDao
import app.pixle.database.repository.SolutionRepository
import app.pixle.model.dto.SolutionDto
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var solutionDao: SolutionDao
    private lateinit var solutionRepository: SolutionRepository
    private lateinit var database: PixleDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(context, PixleDatabase::class.java).build();

        solutionDao = database.solutionDao()
        solutionRepository = database.solutionRepository()
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
}