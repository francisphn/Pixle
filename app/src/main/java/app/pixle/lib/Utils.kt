package app.pixle.lib

import android.net.http.HttpResponseCache.install
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class Utils {
    companion object {
        fun utcDate(): LocalDate = LocalDate.now(Clock.systemUTC())

        @Volatile
        private var client: HttpClient? = null

        fun getHttpClient(): HttpClient {
            if (client == null) {
                synchronized(this) {
                    client = HttpClient {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                }
            }

            return client as HttpClient
        }
    }
}
