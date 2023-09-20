package app.pixle.repository.api

import app.pixle.model.Answer

interface ApiInterface {
    suspend fun getAnswer(): Answer
}