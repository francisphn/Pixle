package app.pixle.repository.api

import app.pixle.model.Answer
import java.time.LocalDate

class ApiService : ApiInterface {
    override suspend fun getAnswer() : Answer {
        return Answer(listOf(), "", LocalDate.now())
    }
}