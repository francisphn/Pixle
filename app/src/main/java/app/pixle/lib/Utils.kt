package app.pixle.lib

import java.time.Clock
import java.time.LocalDate
import java.time.ZoneId

class Utils {
    companion object {
        fun utcDate(): LocalDate = LocalDate.now(Clock.systemUTC())
    }
}
