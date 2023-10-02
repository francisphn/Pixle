package app.pixle.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.util.UUID

class LocalDateConverter {
    @TypeConverter
    fun localDateToString(date: LocalDate ): String  {
        return date .toString()
    }

    @TypeConverter
    fun stringToLocalDate(string: String ): LocalDate  {
        return string .let { LocalDate.parse(string) }
    }
}