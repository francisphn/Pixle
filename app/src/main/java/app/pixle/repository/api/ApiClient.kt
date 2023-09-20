package app.pixle.repository.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val client = HttpClient {
    install(ContentNegotiation) {
        json()
    }
}