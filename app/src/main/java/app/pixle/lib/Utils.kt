package app.pixle.lib

import com.google.android.gms.nearby.connection.Payload
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.time.Clock
import java.time.LocalDate

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

fun String.bA(): ByteArray {
    return this.toByteArray(Charsets.UTF_32)
}

fun String.asPayload(): Payload {
    return Payload.fromBytes(this.bA())
}

fun ByteArray.stringify(): String {
    return String(this, Charsets.UTF_32)
}